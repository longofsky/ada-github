package com.ada.news.dao.vo;

/**
 * @ProjectName: ada-github
 * @Package: com.ada.news.dao.vo
 * @ClassName: Recommendations
 * @Author: litianlong
 * @Description: ${description}
 * @Date: 2019-07-23 13:41
 * @Version: 1.0
 */
public class Recommendations {

    private Integer recnums;

    private Long userId;

    public Integer getRecnums() {
        return recnums;
    }

    public void setRecnums(Integer recnums) {
        this.recnums = recnums;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
