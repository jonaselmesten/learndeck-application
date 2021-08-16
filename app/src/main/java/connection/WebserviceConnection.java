package connection;

import model.Deck;
import model.DeckResponse;
import model.User;
import rest.DeckService;
import rest.UserService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

class WebserviceConnection {

    private final String BASE_URL = "http://10.0.2.2:8080";

    public Optional<User> getUser(long id) {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        UserService service = retrofit.create(UserService.class);

        service.getUser(1).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                User user = response.body();
                System.out.println(user.getUserId());
                System.out.println(user.getUserType());
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {

            }
        });

        return null;
    }

    public List<Deck> getUserDecks(long userId) throws ConnectionException {

        List<Deck> deckList = new ArrayList<>();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        DeckService service = retrofit.create(DeckService.class);

        try {
            Response<DeckResponse> response = service.getUserDecks(userId).execute();

            if(response.isSuccessful())
                deckList = response.body().getResponse().getDeckList();
            else throw new ConnectionException("Could not get resource");

        } catch (IOException | ConnectionException e) {
            throw new ConnectionException("Could not get resource");
        }

        return deckList;
    }

}
