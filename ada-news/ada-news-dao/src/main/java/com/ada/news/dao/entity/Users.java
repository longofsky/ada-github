package com.ada.news.dao.entity;

import java.util.Date;
import javax.persistence.*;

public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "latest_log_time")
    private Date latestLogTime;

    private String name;

    @Column(name = "pref_list")
    private String prefList;

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
     * @return latest_log_time
     */
    public Date getLatestLogTime() {
        return latestLogTime;
    }

    /**
     * @param latestLogTime
     */
    public void setLatestLogTime(Date latestLogTime) {
        this.latestLogTime = latestLogTime;
    }

    /**
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return pref_list
     */
    public String getPrefList() {
        return prefList;
    }

    /**
     * @param prefList
     */
    public void setPrefList(String prefList) {
        this.prefList = prefList;
    }
}