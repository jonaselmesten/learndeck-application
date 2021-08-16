package model;

import com.google.gson.annotations.SerializedName;

public class User {

    @SerializedName("userId")
    Long userId;

    @SerializedName("userType")
    String userType;

    public User(Long userId, String userType) {
        this.userId = userId;
        this.userType = userType;
    }

    public Long getUserId() {
        return userId;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }
}
