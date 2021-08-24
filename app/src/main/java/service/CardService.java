package service;

import model.CardList;
import model.ListResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface CardService {

    @GET("courses/{id1}/reviews/users/{id2}")
    public Call<ListResponse<CardList>> getUserReviewCards(@Path("id1") long courseId,
                                                           @Path("id2") long userId);

}
