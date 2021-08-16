package model;

import com.google.gson.annotations.SerializedName;

public class DeckResponse {

    @SerializedName("_embedded")
    DeckList deckList;

    public DeckList getResponse() {
        return deckList;
    }
}
