package service;

import model.DeckList;
import model.ListResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface DeckService {

    @GET("courses/{id}")
    public Call<ListResponse<DeckList>> getUserDecks(@Path("id") long userId);

}
