/**
 * 
 */
package com.ada.news.service.contentbasedrecommend;

import com.ada.common.utils.JsonUtil;
import com.ada.news.dao.entity.*;
import com.ada.news.dao.mapper.NewsLogsMapper;
import com.ada.news.dao.mapper.NewsMapper;
import com.ada.news.dao.mapper.UsersMapper;
import com.ada.news.service.algorithms.JsonKit;
import com.ada.news.service.algorithms.RecommendKit;
import org.ansj.app.keyword.Keyword;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author qianxinyao
 * @email tomqianmaple@gmail.com
 * @github https://github.com/bluemapleman
 * @date 2016年11月3日 
 * 每次用户浏览新的新闻时，用以更新用户的喜好关键词列表
 */
@Service
public class UserPrefRefresher
{
	@Autowired
	private RecommendKit recommendKit;
	@Autowired
	private UsersMapper usersMapper;
	@Autowired
	private NewsLogsMapper newsLogsMapper;
	@Autowired
	private NewsMapper newsMapper;
	//设置TFIDF提取的关键词数目
	private static final int KEY_WORDS_NUM = 10;
	
	//每日衰减系数
	private static final double DEC_COEE=0.7;

	public void refresh(){
			refresh(recommendKit.getUserList());
	}
	
	/**
	 * 按照推荐频率调用的方法，一般为一天执行一次。
	 * 定期根据前一天所有用户的浏览记录，在对用户进行喜好关键词列表TFIDF值衰减的后，将用户前一天看的新闻的关键词及相应TFIDF值更新到列表中去。
	 * @param userIdsCol
	 */
	@SuppressWarnings("unchecked")
	public void refresh(Collection<Long> userIdsCol){
			//首先对用户的喜好关键词列表进行衰减更新
			autoDecRefresh(userIdsCol);
			
			//用户浏览新闻纪录：userBrowsexMap:<Long(userid),ArrayList<String>(newsid List)>
			HashMap<Long,ArrayList<Long>> userBrowsedMap=getBrowsedHistoryMap();
			//如果前一天没有浏览记录（比如新闻门户出状况暂时关停的情况下，或者初期用户较少的时候均可能出现这种情况），则不需要执行后续更新步骤
			if(userBrowsedMap.size()==0) {
				return;
			}
			
			//用户喜好关键词列表：userPrefListMap:<String(userid),String(json))>
			HashMap<Long,CustomizedHashMap<Integer,CustomizedHashMap<String,Double>>> userPrefListMap=recommendKit.getUserPrefListMap(userBrowsedMap.keySet());
			//新闻对应关键词列表与模块ID：newsTFIDFMap:<String(newsid),List<Keyword>>,<String(newsModuleId),Integer(moduleid)>
			HashMap<String,Object> newsTFIDFMap=getNewsTFIDFMap();
			
			//开始遍历用户浏览记录，更新用户喜好关键词列表
			//对每个用户（外层循环），循环他所看过的每条新闻（内层循环），对每个新闻，更新它的关键词列表到用户的对应模块中
			Iterator<Long> ite=userBrowsedMap.keySet().iterator();
			
			while(ite.hasNext()){
				Long userId=ite.next();
				ArrayList<Long> newsList=userBrowsedMap.get(userId);

				CustomizedHashMap<Integer,CustomizedHashMap<String,Double>> customizedHashMapCustomizedHashMap = userPrefListMap.get(userId);

				for(Long news:newsList){
					Integer moduleId=(Integer) newsTFIDFMap.get(news+"moduleid");
					//获得对应模块的（关键词：喜好）map
					CustomizedHashMap<String,Double> rateMap;

					if (customizedHashMapCustomizedHashMap != null && customizedHashMapCustomizedHashMap.containsKey(moduleId)) {
						rateMap=customizedHashMapCustomizedHashMap.get(moduleId);
					} else {
						customizedHashMapCustomizedHashMap = new CustomizedHashMap();
						rateMap = new CustomizedHashMap();
					}
					//获得新闻的（关键词：TFIDF值）map
					List<Keyword> keywordList=(List<Keyword>) newsTFIDFMap.get(news.toString());
					Iterator<Keyword> keywordIte=keywordList.iterator();
					while(keywordIte.hasNext()){
						Keyword keyword=keywordIte.next();
						String name=keyword.getName();
						if(rateMap.containsKey(name)){
							rateMap.put(name, rateMap.get(name)+keyword.getScore());
						}
						else{
							rateMap.put(name,keyword.getScore());
						}
					}

					customizedHashMapCustomizedHashMap.put(moduleId,rateMap);
				}

				userPrefListMap.put(userId,customizedHashMapCustomizedHashMap);
			}
			Iterator<Long> iterator=userBrowsedMap.keySet().iterator();
			while(iterator.hasNext()){
				Long userId=iterator.next();
				try
				{
					usersMapper.updatePrelistById(JsonUtil.beanToJson(userPrefListMap.get(userId)),userId);
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
			
	}
	
	/**
	 * 所有用户的喜好关键词列表TFIDF值随时间进行自动衰减更新
	 */
	public void autoDecRefresh(){
		autoDecRefresh(recommendKit.getUserList());
	}
	
	/**
	 * 所有用户的喜好关键词列表TFIDF值随时间进行自动衰减更新
	 */
	public void autoDecRefresh(Collection<Long> userIdsCol){
		try
		{
			List<Long> ids=recommendKit.getInQueryStringWithSingleQuote(userIdsCol.iterator());
			if(ids.size() <= 0){
				return;
			}
			UsersExample usersExample = new UsersExample();
			UsersExample.Criteria criteria = usersExample.createCriteria();
			criteria.andIdIn(ids);

			List<Users> userList = usersMapper.selectByExample(usersExample);
			//用以更新的用户喜好关键词map的json串
			//用于删除喜好值过低的关键词
			ArrayList<String> keywordToDelete=new ArrayList<String>();
			for(Users user:userList){
				String newPrefList="{";
				HashMap<Integer,CustomizedHashMap<String,Double>> map= JsonKit.jsonPrefListtoMap(user.getPrefList());
				Iterator<Integer> ite=map.keySet().iterator();
				while(ite.hasNext()){
					//用户对应模块的喜好不为空
					Integer moduleId=ite.next();
					CustomizedHashMap<String,Double> moduleMap=map.get(moduleId);
					newPrefList+="\""+moduleId+"\":";
					//N:{"X1":n1,"X2":n2,.....}
					if(!(moduleMap.toString().equals("{}"))){
						Iterator<String> inIte=moduleMap.keySet().iterator();
						while(inIte.hasNext()){
							String key=inIte.next();
							//累计TFIDF值乘以衰减系数
							double result=moduleMap.get(key)*DEC_COEE;
							if(result<10){
								keywordToDelete.add(key);
							}
							moduleMap.put(key,result);
						}
					}
					for(String deleteKey:keywordToDelete){
						moduleMap.remove(deleteKey);
					}
					keywordToDelete.clear();
					newPrefList+=moduleMap.toString()+",";
				}
				newPrefList="'"+newPrefList.substring(0,newPrefList.length()-1)+"}'";
				usersMapper.updatePrelistById(newPrefList,user.getId());
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * 提取出当天有浏览行为的用户及其各自所浏览过的新闻id列表
	 * @return
	 */
	private HashMap<Long,ArrayList<Long>> getBrowsedHistoryMap(){
		HashMap<Long, ArrayList<Long>> userBrowsedMap=new HashMap<Long,ArrayList<Long>>();
		try
		{
			NewsLogsExample newsLogsExample = new NewsLogsExample();
			NewsLogsExample.Criteria criteria = newsLogsExample.createCriteria();

			criteria.andViewTimeGreaterThan(recommendKit.getDay(0));

			List<NewsLogs> newslogsList=newsLogsMapper.selectByExample(newsLogsExample);
			for(NewsLogs newslogs:newslogsList){
				if(userBrowsedMap.containsKey(newslogs.getUserId())){
					userBrowsedMap.get(newslogs.getUserId()).add(newslogs.getNewsId());
				}
				else{
					userBrowsedMap.put(newslogs.getUserId(), new ArrayList<Long>());
					userBrowsedMap.get(newslogs.getUserId()).add(newslogs.getNewsId());
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return userBrowsedMap;
	}
	
	
	/**
	 * 获得浏览过的新闻的集合
	 * @return
	 */
	private HashSet<Long> getBrowsedNewsSet(){
		HashMap<Long,ArrayList<Long>> browsedMap=getBrowsedHistoryMap();
		HashSet<Long> newsIdSet=new HashSet<Long>();
		Iterator<Long> ite=getBrowsedHistoryMap().keySet().iterator();
		while(ite.hasNext()){
			Iterator<Long> inIte=browsedMap.get(ite.next()).iterator();
			while(inIte.hasNext()){
				newsIdSet.add(inIte.next());
			}
		}
		return newsIdSet;
	}
	
	/**
	 * 将所有当天被浏览过的新闻提取出来，以便进行TFIDF求值操作，以及对用户喜好关键词列表的更新。
	 * @return
	 */
	private HashMap<String,Object> getNewsTFIDFMap(){
		HashMap<String,Object> newsTFIDFMap=new HashMap<String,Object>();;
		try
		{
			Iterator<Long> ite=getBrowsedNewsSet().iterator();

			List<Long> ids = new ArrayList<>();
			String newsIdListQuery="(";
			while(ite.hasNext()){
				ids.add(ite.next());
			}
			
//			//当天存在用户浏览记录
//			if(newsIdListQuery.length()>1){
//				newsIdListQuery=newsIdListQuery.substring(0, newsIdListQuery.length()-1)+")";
//				//提取出所有新闻的关键词列表及对应TF-IDf值，并放入一个map中
//				List<News> newsList=News.dao.find("select id,title,content,module_id from news where id in "+newsIdListQuery);
//				System.out.println("newsIdListQuery:"+newsIdListQuery);
//				for(News news:newsList){
//					newsTFIDFMap.put(String.valueOf(news.getId()), TFIDF.getTFIDE(news.getTitle(), news.getContent(),KEY_WORDS_NUM));
//					newsTFIDFMap.put(news.getId()+"moduleid", news.getModuleId());
//				}
//				for()
//			}

			//提取出所有新闻的关键词列表及对应TF-IDf值，并放入一个map中

			NewsExample newsExample = new NewsExample();
			NewsExample.Criteria criteria = newsExample.createCriteria();
			criteria.andIdIn(ids);
			List<News> newsList = newsMapper.selectByExample(newsExample);
			for(News news:newsList){
				newsTFIDFMap.put(String.valueOf(news.getId()), TFIDF.getTFIDE(news.getTitle(), news.getContent(),KEY_WORDS_NUM));
				newsTFIDFMap.put(news.getId()+"moduleid", news.getModuleId());
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return newsTFIDFMap;
	}
}
