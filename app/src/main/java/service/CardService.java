package service;

import model.CardList;
import model.ListResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface CardService {

    @GET("reviews/users")
    public Call<ListResponse<CardList>> getUserReviewCards(@Query("user_id") long userId,
                                                           @Query("course_id") long courseId);

}
