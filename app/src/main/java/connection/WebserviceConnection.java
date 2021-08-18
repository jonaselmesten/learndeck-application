package connection;

import exceptions.ConnectionException;
import model.Deck;
import model.DeckResponse;
import model.User;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import service.DeckService;
import service.UserService;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static activity.DeckActivity.USER_ID;

/**
 * <h1>WebserviceConnection</h1>
 * This class hides all the backend HTTP calls for the application.
 */
class WebserviceConnection {

    private final String BASE_URL = "http://10.0.2.2:8080";

    public Optional<User> getUser(long id) throws ConnectionException {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        UserService service = retrofit.create(UserService.class);

        try {
            Response<User> response = service.getUser(USER_ID).execute();

            if(response.isSuccessful())
                return Optional.of(response.body());
            else throw new ConnectionException("Could not get resource");

        } catch (IOException | ConnectionException e) {
            throw new ConnectionException("Could not get resource");
        }
    }

    public List<Deck> getUserDecks(long userId) throws ConnectionException {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        DeckService service = retrofit.create(DeckService.class);

        try {
            Response<DeckResponse> response = service.getUserDecks(userId).execute();

            if(response.isSuccessful())
                return response.body().getResponse().getDeckList();
            else throw new ConnectionException("Could not get resource");

        } catch (IOException | ConnectionException e) {
            throw new ConnectionException("Could not get resource");
        }
    }

}
