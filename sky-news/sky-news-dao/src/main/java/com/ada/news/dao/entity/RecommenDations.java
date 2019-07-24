package com.ada.news.dao.entity;

import java.util.Date;
import javax.persistence.*;

@Table(name = "recommend_ations")
public class RecommenDations {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "news_id")
    private Long newsId;

    @Column(name = "derive_time")
    private Date deriveTime;

    private Boolean feedback;

    @Column(name = "derive_algorithm")
    private Integer deriveAlgorithm;

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
     * @return derive_time
     */
    public Date getDeriveTime() {
        return deriveTime;
    }

    /**
     * @param deriveTime
     */
    public void setDeriveTime(Date deriveTime) {
        this.deriveTime = deriveTime;
    }

    /**
     * @return feedback
     */
    public Boolean getFeedback() {
        return feedback;
    }

    /**
     * @param feedback
     */
    public void setFeedback(Boolean feedback) {
        this.feedback = feedback;
    }

    /**
     * @return derive_algorithm
     */
    public Integer getDeriveAlgorithm() {
        return deriveAlgorithm;
    }

    /**
     * @param deriveAlgorithm
     */
    public void setDeriveAlgorithm(Integer deriveAlgorithm) {
        this.deriveAlgorithm = deriveAlgorithm;
    }
}