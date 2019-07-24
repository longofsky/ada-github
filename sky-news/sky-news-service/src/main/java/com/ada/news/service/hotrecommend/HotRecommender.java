/**
 * 
 */
package com.ada.news.service.hotrecommend;

import com.ada.news.dao.entity.NewsLogs;
import com.ada.news.dao.entity.RecommenDations;
import com.ada.news.dao.mapper.NewsLogsMapper;
import com.ada.news.dao.mapper.RecommenDationsMapper;
import com.ada.news.service.algorithms.RecommendAlgorithm;
import com.ada.news.service.algorithms.RecommendKit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.*;

/**
 * @author qianxinyao
 * @email tomqianmaple@gmail.com
 * @github https://github.com/bluemapleman
 * @date 2016年11月30日 基于“热点新闻”生成的推荐，一般用于在CF和CB算法推荐结果数较少时进行数目的补充
 */
//@Component
public class HotRecommender implements RecommendAlgorithm
{

	@Autowired
	private RecommendKit recommendKit;
	@Autowired
	private RecommenDationsMapper recommenDationsMapper;

	@Autowired
	private NewsLogsMapper newsLogsMapper;
	
	// 热点新闻的有效时间
	public static int beforeDays = -10;
	// 推荐系统每日为每位用户生成的推荐结果的总数，当CF与CB算法生成的推荐结果数不足此数时，由该算法补充
	public static int TOTAL_REC_NUM = 20;
	// 将每天生成的“热点新闻”ID，按照新闻的热点程度从高到低放入此List
	private static ArrayList<Long> topHotNewsList = new ArrayList<Long>();

	@Override
	public void recommend(List<Long> users)
	{
		System.out.println("HR start at "+new Date());
		int count=0;
		for (Long userId : users)
		{
			try
			{
				RecommenDations recommendation = recommenDationsMapper.findFirst(getCertainTimestamp(0,0,0),userId);
				//获得已经预备为当前用户推荐的新闻，若数目不足达不到单次的最低推荐数目要求，则用热点新闻补充

				boolean flag=(recommendation!=null);
				Integer tmpRecNums=0;
//				if(recommendation!=null) {
//					tmpRecNums = recommendation.getRecnums();
//				}
				int delta=flag?TOTAL_REC_NUM - Integer.valueOf(tmpRecNums.toString()):TOTAL_REC_NUM;
				Set<Long> toBeRecommended = new HashSet<Long>();
				if (delta > 0)
				{
					int i = topHotNewsList.size() > delta ? delta : topHotNewsList.size();
					while (i-- > 0)
						toBeRecommended.add(topHotNewsList.get(i));
				}
				recommendKit.filterBrowsedNews(toBeRecommended, userId);
				recommendKit.filterReccedNews(toBeRecommended, userId);
				recommendKit.insertRecommend(userId, toBeRecommended.iterator(), RecommendAlgorithm.HR);
				count+=toBeRecommended.size();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		System.out.println("HR has contributed " + (users.size()==0?0:count/users.size()) + " recommending news on average");
		System.out.println("HR end at "+new Date());

	}

	public  void formTodayTopHotNewsList()
	{
		topHotNewsList.clear();
		ArrayList<Long> hotNewsTobeReccommended = new ArrayList<Long>();
		try
		{
			List<NewsLogs> newslogsList = newsLogsMapper.find(recommendKit.getDay(beforeDays));

			for (NewsLogs newslog:newslogsList)
			{
				hotNewsTobeReccommended.add(newslog.getNewsId());
			}
			for (Long news : hotNewsTobeReccommended)
			{
				topHotNewsList.add(news);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public List<Long> getTopHotNewsList()
	{
		return topHotNewsList;
	}

	public static int getTopHopNewsListSize()
	{
		return topHotNewsList.size();
	}

	private Date getCertainTimestamp(int hour, int minute, int second)
	{
		Calendar calendar = Calendar.getInstance(); // 得到日历
		calendar.set(Calendar.HOUR_OF_DAY, hour); // 设置为前beforeNum天
		calendar.set(Calendar.MINUTE, minute);
		calendar.set(Calendar.SECOND, second);
		return calendar.getTime();
	}
}
