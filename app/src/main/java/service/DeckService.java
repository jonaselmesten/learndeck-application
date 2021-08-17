package service;

import model.DeckResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface DeckService {

    @GET("courses/{id}")
    public Call<DeckResponse> getUserDecks(@Path("id") long userId);

}
