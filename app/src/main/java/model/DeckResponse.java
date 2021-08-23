package model;

import com.google.gson.annotations.SerializedName;

public class DeckResponse {

    @SerializedName("courseId")
    private final Long courseId;
    @SerializedName("courseName")
    private final String courseName;
    @SerializedName("dueCount")
    private final int dueCount;

    public DeckResponse(String courseName, long courseId, int dueCount) {
        this.courseName = courseName;
        this.courseId = courseId;
        this.dueCount = dueCount;
    }

    public String getCourseName() {
        return courseName;
    }

    public Long getCourseId() {
        return courseId;
    }

    public int getDueCount() {
        return dueCount;
    }
}

