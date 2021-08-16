package model;

import com.google.gson.annotations.SerializedName;

public class Deck {

    @SerializedName("userId")
    Long userId;

    @SerializedName("courseId")
    Long courseId;

    @SerializedName("courseName")
    String courseName;

    public Deck(long userId, long courseId, String courseName) {
        this.userId = userId;
        this.courseId = courseId;
        this.courseName = courseName;
    }

    public Long getUserId() {
        return userId;
    }

    public String getCourseName() {
        return courseName;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }
}
