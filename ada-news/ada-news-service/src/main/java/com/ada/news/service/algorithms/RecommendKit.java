/**
 * 
 */
package com.ada.news.service.algorithms;

import com.ada.news.dao.entity.*;
import com.ada.news.dao.mapper.NewsLogsMapper;
import com.ada.news.dao.mapper.NewsMapper;
import com.ada.news.dao.mapper.RecommenDationsMapper;
import com.ada.news.dao.mapper.UsersMapper;
import com.ada.news.service.contentbasedrecommend.CustomizedHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author qianxinyao
 * @email tomqianmaple@gmail.com
 * @github https://github.com/bluemapleman
 * @date 2016年11月21日 提供推荐算法通用的一些方法
 */
@Component
public class RecommendKit
{
	@Autowired
	private NewsMapper newsMapper;

	@Autowired
	private UsersMapper usersMapper;

	@Autowired
	private RecommenDationsMapper recommenDationsMapper;

	@Autowired
	private NewsLogsMapper newsLogsMapper;
	
	public static final Logger logger= LoggerFactory.getLogger(RecommendKit.class);
	
	/**
	 * 推荐新闻的时效性天数，即从推荐当天开始到之前beforeDays天的新闻属于仍具有时效性的新闻，予以推荐。
	 */
	private static int beforeDays = PropGetKit.getInt("beforeDays");

	/**
	 * @return the inRecDate 返回时效时间的"year-month-day"的格式表示，方便数据库的查询
	 */
	public String getInRecDate()
	{
		return getSpecificDayFormat(beforeDays);
	}

	/**
	 * @return the inRecDate 返回时效时间的"year-month-day"的格式表示，方便数据库的查询
	 */
	public String getInRecDate(int beforeDays)
	{
		return getSpecificDayFormat(beforeDays);
	}

	/**
	 * @return the inRecDate 返回时效时间timestamp形式表示，方便其他推荐方法在比较时间先后时调用
	 */
	public static Timestamp getInRecTimestamp(int before_Days)
	{
		Calendar calendar = Calendar.getInstance(); // 得到日历
		calendar.add(Calendar.DAY_OF_MONTH, before_Days); // 设置为前beforeNum天
		return new Timestamp(calendar.getTime().getTime());
	}

	/**
	 * 过滤方法filterOutDateNews() 过滤掉失去时效性的新闻（由beforeDays属性控制）
	 */
	public void filterOutDateNews(Collection<Long> col, Long userId)
	{
		try
		{
			List<Long> newsids = getInQueryString(col.iterator());
			if (newsids.size() > 0)
			{
				NewsExample newsExample = new NewsExample();
				NewsExample.Criteria criteria = newsExample.createCriteria();

				criteria.andIdIn(newsids);

				List<News> newsList = newsMapper.selectByExample(newsExample);
				for(News news:newsList)
				{
					if (news.getNewsTime().before(getInRecTimestamp(beforeDays)))
					{
						col.remove(news.getId());
					}
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * 过滤方法filterBrowsedNews() 过滤掉已经用户已经看过的新闻
	 */
	public void filterBrowsedNews(Collection<Long> col, Long userId)
	{
		try
		{
			NewsLogsExample newsLogsExample = new NewsLogsExample();

			NewsLogsExample.Criteria criteria = newsLogsExample.createCriteria();
			criteria.andUserIdEqualTo(userId);

			List<NewsLogs> newslogsList =newsLogsMapper.selectByExample(newsLogsExample);

			for (NewsLogs newslog:newslogsList)
			{
				if (col.contains(newslog.getNewsId()))
				{
					col.remove(newslog.getNewsId());
				}
			}
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 过滤方法filterReccedNews() 过滤掉已经推荐过的新闻（在recommend表中查找）
	 */
	public void filterReccedNews(Collection<Long> col, Long userId)
	{
		try
		{
			RecommenDationsExample recommenDationsExample = new RecommenDationsExample();
			RecommenDationsExample.Criteria criteria = recommenDationsExample.createCriteria();
			criteria.andUserIdEqualTo(userId);
			criteria.andDeriveTimeGreaterThan(getDay(beforeDays));

			//但凡近期已经给用户推荐过的新闻，都过滤掉
			List<RecommenDations> recommendationList = recommenDationsMapper.selectByExample(recommenDationsExample);
			for (RecommenDations recommendation:recommendationList)
			{
				if (col.contains(recommendation.getNewsId()))
				{
					col.remove(recommendation.getNewsId());
				}
			}
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 获取所有用户的Id列表
	 * 
	 * @return
	 */
	public ArrayList<Long> getUserList()
	{
		ArrayList<Long> users = new ArrayList<Long>();
		try
		{

			List<Users> userList = usersMapper.selectAll();
			for (Users user:userList)
			{
				users.add(user.getId());
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return users;
	}

	public static int getbeforeDays()
	{
		return beforeDays;
	}

	public static void setbeforeDays(int beforeDays)
	{
		RecommendKit.beforeDays = beforeDays;
	}

	public String getSpecificDayFormat(int before_Days)
	{

		SimpleDateFormat date_format = new SimpleDateFormat("yyyy-MM-dd");
		Date d = getDay(before_Days);
		return "'" + date_format.format(d) + "'";
	}

	public Date getDay()
	{
		return getDay(beforeDays);
	}

	public Date getDay(int before_Days)
	{
		Calendar calendar = Calendar.getInstance(); // 得到日历
		calendar.add(Calendar.DAY_OF_MONTH, before_Days); // 设置为前beforeNum天
		Date d = calendar.getTime();
		return d;
	}

	/**
	 * 获取所有用户的喜好关键词列表
	 * 
	 * @return
	 */
	public HashMap<Long, CustomizedHashMap<Integer, CustomizedHashMap<String, Double>>> getUserPrefListMap(
			Collection<Long> userSet)
	{
		HashMap<Long, CustomizedHashMap<Integer, CustomizedHashMap<String, Double>>> userPrefListMap = null;
		try
		{
			List<Long> ids = getInQueryStringWithSingleQuote(userSet.iterator());
			if (ids.size() >0)
			{
				UsersExample usersExample = new UsersExample();
				UsersExample.Criteria criteria = usersExample.createCriteria();
				criteria.andIdIn(ids);

				List<Users> userList = usersMapper.selectByExample(usersExample);
				userPrefListMap = new HashMap<Long, CustomizedHashMap<Integer, CustomizedHashMap<String, Double>>>();
				for (Users user:userList)
				{
					userPrefListMap.put(user.getId(), JsonKit.jsonPrefListtoMap(user.getPrefList()));
				}
			}
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return userPrefListMap;
	}

	/**
	 * 用以select语句中使用in (n1，n2,n3...)范围查询的字符串拼接
	 * 
	 * @param ite
	 *            待查询对象集合的迭代器
	 * @return 若迭代集合不为空:"(n1,n2,n3)"，若为空："()"
	 */
	public static <T> List<Long> getInQueryString(Iterator<T> ite)
	{
		List<Long> ids = new ArrayList<>();
		while (ite.hasNext())
		{
			ids.add((Long)ite.next());
		}

		return ids;
	}

	public <T> List<Long> getInQueryStringWithSingleQuote(Iterator<T> ite)
	{

		List<Long> ids = new ArrayList<>();

		while (ite.hasNext())
		{
			ids.add((Long) ite.next());
		}

		return ids;
	}

	/**
	 * 将推荐结果插入recommend表
	 * 
	 * @param userId
	 *            推荐目标用户id
	 * @param newsIte
	 *            待推荐新闻集合的迭代器
	 * @param recAlgo
	 *            标明推荐结果来自哪个推荐算法(RecommendAlgorithm.XX)
	 */
	public void insertRecommend(Long userId, Iterator<Long> newsIte, int recAlgo)
	{
		try
		{
			while (newsIte.hasNext())
			{
				RecommenDations rec=new RecommenDations();
				rec.setUserId(userId);
				rec.setDeriveAlgorithm(recAlgo);
				rec.setNewsId(newsIte.next());

				recommenDationsMapper.insertSelective(rec);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	/**
	 *  Acquire a list of active users 
	 * "Active" means who read news recently ('recent' determined by method getInRecDate(), default in a month)
	 * 
	 * @return
	 */
	public  List<Long> getActiveUsers()
	{
		try
		{
			int activeDay=PropGetKit.getInt("activeDay");

			UsersExample usersExample = new UsersExample();
			UsersExample.Criteria criteria = usersExample.createCriteria();
			criteria.andLatestLogTimeGreaterThan(getDay(activeDay));

			List<Users> userList = usersMapper.selectByExample(usersExample);
			List<Long> userIDList=new ArrayList<Long>();
			for(Users user:userList) {
				userIDList.add(user.getId());
			}

			return userIDList;
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		logger.info("获取活跃用户异常！");
		return null;
	}
	
	public  List<Long> getAllUsers(){
		try
		{
			List<Users> userList = usersMapper.selectAll();
			List<Long> userIDList=new ArrayList<Long>();
			for(Users user:userList) {
				userIDList.add(user.getId());
			}
			return userIDList;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		logger.info("获取全体用户异常！");
		return null;
	}
	

	/**
	 * 去除数量上超过为算法设置的推荐结果上限值的推荐结果
	 * 
	 * @param set
	 * @param N
	 * @return
	 */
	public void removeOverNews(Set<Long> set, int N)
	{
		int i = 0;
		Iterator<Long> ite = set.iterator();
		while (ite.hasNext())
		{
			if (i >= N)
			{
				ite.remove();
				ite.next();
			}
			else
			{
				ite.next();
			}
			i++;
		}
	}
}
