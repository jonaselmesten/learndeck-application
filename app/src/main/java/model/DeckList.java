package model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DeckList {

    @SerializedName("userCourseList")
    private List<Deck> deckList;

    public List<Deck> getDeckList() {
        if(this.deckList == null || this.deckList.isEmpty())
            return Collections.emptyList();
        else
            return deckList;
    }
}
