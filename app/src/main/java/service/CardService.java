package service;

import model.CardResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface CardService {

    @GET("reviews/users")
    public Call<CardResponse> getUserReviewCards(@Query("user_id") long userId,@Query("course_id") long courseId);

}
