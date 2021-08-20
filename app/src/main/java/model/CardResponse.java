package model;

import com.google.gson.annotations.SerializedName;

public class CardResponse {

    @SerializedName("_embedded")
    CardList deckList;

    public CardList getResponse() {
        return deckList;
    }
}