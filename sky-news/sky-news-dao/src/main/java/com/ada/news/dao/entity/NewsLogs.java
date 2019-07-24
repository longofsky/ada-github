package com.ada.news.dao.entity;

import java.util.Date;
import javax.persistence.*;

@Table(name = "news_logs")
public class NewsLogs {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "news_id")
    private Long newsId;

    @Column(name = "view_time")
    private Date viewTime;

    @Column(name = "prefer_degree")
    private Integer preferDegree;

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
     * @return user_id
     */
    public Long getUserId() {
        return userId;
    }

    /**
     * @param userId
     */
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    /**
     * @return news_id
     */
    public Long getNewsId() {
        return newsId;
    }

    /**
     * @param newsId
     */
    public void setNewsId(Long newsId) {
        this.newsId = newsId;
    }

    /**
     * @return view_time
     */
    public Date getViewTime() {
        return viewTime;
    }

    /**
     * @param viewTime
     */
    public void setViewTime(Date viewTime) {
        this.viewTime = viewTime;
    }

    /**
     * @return prefer_degree
     */
    public Integer getPreferDegree() {
        return preferDegree;
    }

    /**
     * @param preferDegree
     */
    public void setPreferDegree(Integer preferDegree) {
        this.preferDegree = preferDegree;
    }
}