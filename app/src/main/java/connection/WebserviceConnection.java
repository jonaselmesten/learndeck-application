package connection;

import card.Card;
import card.IncorrectCardFormatException;
import exceptions.ConnectionException;
import model.*;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import service.CardService;
import service.DeckService;
import service.UserService;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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

        } catch (IOException e) {
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
            Response<ListResponse<DeckList>> response = service.getUserDecks(userId).execute();

            if(response.isSuccessful())
                return response.body().getList().getDeckList();
            else throw new ConnectionException("Could not get resource");

        } catch (IOException e) {
            throw new ConnectionException("Could not get resource");
        }
    }

    public List<Card> getDeckCards(int courseId, int userId) throws IOException {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        CardService service = retrofit.create(CardService.class);
        Response<ListResponse<CardList>> response = service.getUserReviewCards(userId, courseId).execute();

        if(!response.isSuccessful())
            throw new ConnectionException("Response unsuccessful:" + response.errorBody());

        List<CardResponse> responseList = Objects.requireNonNull(response.body()).getList().getCardList();
        List<Card> cardList = new ArrayList<>();

        //Try to add all valid cards from the response.
        for (CardResponse card : responseList) {
            try {
                cardList.add(Card.fromResponse(card));
            } catch (IncorrectCardFormatException | ParseException e) {
                e.printStackTrace();
            }
        }

        return cardList;
    }

}
