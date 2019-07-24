/**
 * 
 */
package com.ada.news.service.UserBasedCollaborativeRecommender;

import com.ada.news.dao.entity.NewsLogs;
import com.ada.news.dao.mapper.NewsLogsMapper;
import com.ada.news.service.algorithms.PropGetKit;
import com.ada.news.service.algorithms.RecommendAlgorithm;
import com.ada.news.service.algorithms.RecommendKit;
import org.apache.log4j.Logger;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.neighborhood.NearestNUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.LogLikelihoodSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author qianxinyao
 * @email tomqianmaple@gmail.com
 * @github https://github.com/bluemapleman
 * @date 2016年10月18日 协同过滤
 */

/**
 * Collaborative-Based Filter 基于用户的协同过滤
 * 
 */
@Component
public class MahoutUserBasedCollaborativeRecommender implements RecommendAlgorithm
{
	public static final Logger logger = Logger.getLogger(MahoutUserBasedCollaborativeRecommender.class);

	@Autowired
	private RecommendKit recommendKit;

	@Autowired
	private NewsLogsMapper newsLogsMapper;

	@Autowired
	private DataModel dataModel;

	/**
	 * 对应计算相似度时的时效天数
	 */
	private static int inRecDays = PropGetKit.getInt("CFValidDay");

	/**
	 * 给每个用户推荐的新闻的条数
	 */
	public static int N =PropGetKit.getInt("CFRecNum");

	/**
	 * 给特定的一批用户进行新闻推荐
	 * 
	 * @param users 目标用户的id列表
	 */
	@SuppressWarnings("unused")
	@Override
	public void recommend(List<Long> users)
	{
		int count=0;
		try
		{
			System.out.println("CF start at "+new Date());

			List<NewsLogs> newslogList=newsLogsMapper.selectAll();

			// 移除过期的用户浏览新闻行为，这些行为对计算用户相似度不再具有较大价值
			for (NewsLogs newslog:newslogList)
			{
				if (newslog.getViewTime().before(recommendKit.getDay(inRecDays)))
				{
					dataModel.removePreference(newslog.getUserId(), newslog.getNewsId());
				}
			}

			UserSimilarity similarity = new LogLikelihoodSimilarity(dataModel);

			// NearestNeighborhood的数量有待考察
			UserNeighborhood neighborhood = new NearestNUserNeighborhood(5, similarity, dataModel);

			Recommender recommender = new GenericUserBasedRecommender(dataModel, neighborhood, similarity);

			for (Long  user : users)
			{
				long start = System.currentTimeMillis();

				Long userid = user;

				List<RecommendedItem> recItems = recommender.recommend(userid, N);

				Set<Long> hs = new HashSet<Long>();

				for (RecommendedItem recItem : recItems)
				{
					hs.add(recItem.getItemID());
				}

				// 过滤掉已推荐新闻和已过期新闻
				recommendKit.filterOutDateNews(hs,userid);
				recommendKit.filterReccedNews(hs,userid);

				// 无可推荐新闻
				if (hs == null)
				{
					continue;
				}

				if(hs.size()>N){
					recommendKit.removeOverNews(hs, N);
				}

				recommendKit.insertRecommend(userid, hs.iterator(),RecommendAlgorithm.CF);
				
				count+=hs.size();
			}
		}
		catch (TasteException e)
		{
			logger.error("CB算法构造偏好对象失败！");
			e.printStackTrace();
		}
		catch (Exception e)
		{
			logger.error("CB算法数据库操作失败！");
			e.printStackTrace();
		}
		System.out.println("CF has contributed " + (count/users.size()) + " recommending news on average");
		System.out.println("CF finish at "+new Date());
		return;
	}

	public int getRecNums()
	{
		return N;
	}

	public void setRecNums(int recNums)
	{
		N = recNums;
	}
}
