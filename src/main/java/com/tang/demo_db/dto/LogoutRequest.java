package com.tang.demo_db.dto;

public class LogoutRequest {
    private String userId;
    private int searchCount;

    public LogoutRequest() {} // 默认构造函数

    public LogoutRequest(String userId, int searchCount) {
        this.userId = userId;
        this.searchCount = searchCount;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getSearchCount() {
        return searchCount;
    }

    public void setSearchCount(int searchCount) {
        this.searchCount = searchCount;
    }
}
