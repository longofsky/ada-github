package com.ada.news.dao.entity;

import java.util.Date;
import javax.persistence.*;

public class News {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "news_time")
    private Date newsTime;

    @Column(name = "module_id")
    private Integer moduleId;

    private String url;

    private String content;

    private String title;

    /**
     * @return id
     */
    public Long getId() {
        return id;
    }

    /**
     * @param id
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @return news_time
     */
    public Date getNewsTime() {
        return newsTime;
    }

    /**
     * @param newsTime
     */
    public void setNewsTime(Date newsTime) {
        this.newsTime = newsTime;
    }

    /**
     * @return module_id
     */
    public Integer getModuleId() {
        return moduleId;
    }

    /**
     * @param moduleId
     */
    public void setModuleId(Integer moduleId) {
        this.moduleId = moduleId;
    }

    /**
     * @return url
     */
    public String getUrl() {
        return url;
    }

    /**
     * @param url
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * @return content
     */
    public String getContent() {
        return content;
    }

    /**
     * @param content
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * @return title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title
     */
    public void setTitle(String title) {
        this.title = title;
    }
}