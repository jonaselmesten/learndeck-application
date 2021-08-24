package connection;

import activity.DeckActivity;
import android.util.Log;
import card.Card;
import card.IncorrectCardFormatException;
import deck.Deck;
import exceptions.ConnectionException;
import exceptions.ResourceException;
import model.*;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import service.CardService;
import service.DeckService;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * <h1>WebserviceConnection</h1>
 * This class hides all the backend HTTP calls for the application.
 */
class WebserviceConnection {

    private final String BASE_URL = "http://10.0.2.2:8080";
    private final Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    /**
     * Fetches all the decks that the user is currently enrolled in.
     * @return List of decks, ready for cards to be added.
     * @throws ConnectionException If the connection fails.
     */
    List<Deck> getUserDecks() throws ConnectionException {

        DeckService service = retrofit.create(DeckService.class);
        List<Deck> deckList = new ArrayList<>();

        try {
            Response<ListResponse<DeckList>> response = service.getUserDecks(DeckActivity.USER_ID).execute();

            if(response.isSuccessful()) {
                List<DeckResponse> deckResponses =  Objects.requireNonNull(response.body()).getList().getDeckList();

                for(DeckResponse deck : deckResponses)
                    deckList.add(Deck.fromResponse(deck));

            } else throw new ConnectionException("Could not get resource");

        } catch (IOException e) {
            throw new ConnectionException("Could not get resource");
        }

        return deckList;
    }

    /**
     * Fetches all the cards for a specific deck.
     * Some cards might not be included due to format errors.
     * @param courseId Id of the deck.
     * @return All the cards, ready to be used as they are.
     * @throws IOException If the connection fails.
     */
    List<Card> getDeckCards(int courseId) throws IOException {

        CardService service = retrofit.create(CardService.class);
        Response<ListResponse<CardList>> response = service.getUserReviewCards(courseId, DeckActivity.USER_ID).execute();

        if(!response.isSuccessful()) {
            Log.e("WebserviceConnection", "Response unsuccessful:" + response.errorBody());
            throw new ConnectionException("Response unsuccessful:" + response.errorBody());
        }

        List<CardResponse> responseList = Objects.requireNonNull(response.body()).getList().getCardList();
        List<Card> cardList = new ArrayList<>();

        //Try to add all valid cards from the response.
        for (CardResponse card : responseList) {
            try {
                cardList.add(Card.fromResponse(card));
            } catch (IncorrectCardFormatException | ParseException e) {
                Log.e("WebserviceConnection", "Malformed card couldn't be created: " + card);
                e.printStackTrace();
            } catch (ResourceException e) {
                Log.e("Resource", "Failed to retrieve the resource for this card:" + card);
                e.printStackTrace();
            }
        }

        return cardList;
    }

}
