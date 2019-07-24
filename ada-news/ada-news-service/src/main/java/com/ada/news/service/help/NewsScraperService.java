package com.ada.news.service.help;


import com.ada.news.dao.entity.News;
import com.ada.news.dao.entity.NewsModules;
import com.ada.news.dao.entity.NewsModulesExample;
import com.ada.news.dao.mapper.NewsMapper;
import com.ada.news.dao.mapper.NewsModulesMapper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Description userCF/itemCF
 * @Author litianlong
 * @Date 2019-07-15 11:36
 */
@Service
public class NewsScraperService {

    Logger logger = LoggerFactory.getLogger(NewsScraperService.class);

    @Autowired
    private NewsMapper newsMapper;
    @Autowired
    private NewsModulesMapper newsModulesMapper;

    public void newsScraper () throws IOException {

        String url="http://www.163.com/";
        Document docu1= Jsoup.connect(url).get();
        Elements lis=docu1.getElementsByTag("li");
        for(Element li: lis) {
            if(li.getElementsByTag("a").size()==0) {
                continue;
            }
            else {
                Element a=li.getElementsByTag("a").get(0);
                String title=a.text();
                //去除标题小于5个字的、非新闻的<li>标签
                String regex=".{10,}";
                Pattern pattern=Pattern.compile(regex);
                Matcher match=pattern.matcher(title);
                if(!match.find())
                    continue;
                String newsUrl=a.attr("href");


                //图集类忽略，Redirect表示广告类忽略
                if(newsUrl.contains("photoview") || newsUrl.contains("Redirect") || newsUrl.contains("{"))
                    continue;

                try
                {
                    Document docu2=Jsoup.connect(newsUrl).get();
                    Elements eles=docu2.getElementsByClass("post_crumb");
                    //没有面包屑导航栏的忽略：不是正规新闻
                    if(eles.size()==0)
                        continue;
                    String moduleName=eles.get(0).getElementsByTag("a").get(1).text();

                    System.out.println(title+"("+moduleName+"):"+newsUrl);

                    News news=new News();

                    news.setTitle(title);
                    news.setModuleId(getModuleID(moduleName));
                    news.setNewsTime(new Date());
                    news.setUrl(newsUrl);

                    newsMapper.insertSelective(news);

                }
                catch (SocketTimeoutException e)
                {
                    continue;
                }
                catch(Exception e) {
                    e.printStackTrace();
                }
            }
        }
        logger.info("本次新闻抓取完毕！");
    }

    /**
     * 初次使用，填充新闻模块信息：将默认RSS源所有模块填入。
     */
    private  int getModuleID(String moduleName) {
        int mododuleID=-1;
        try {

            NewsModulesExample newsModulesExample = new NewsModulesExample();

            NewsModulesExample.Criteria criteria = newsModulesExample.createCriteria();

            criteria.andNameEqualTo(moduleName);

            List<NewsModules> newsModules =  newsModulesMapper.selectByExample(newsModulesExample);

            if(newsModules==null || newsModules.size() <= 0) {
                NewsModules module=new NewsModules();
                module.setName(moduleName);

                return newsModulesMapper.insertSelective(module);
            }
            else {
                return newsModules.get(0).getId();
            }

        }
        catch (Exception e) {
            logger.error(e.getMessage());
        }
        return mododuleID;
    }

}
