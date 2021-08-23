package model;

import com.google.gson.annotations.SerializedName;

import java.util.Collections;
import java.util.List;

public class DeckList {

    @SerializedName("userCourseList")
    private List<DeckResponse> deckList;

    public List<DeckResponse> getDeckList() {
        if(this.deckList == null || this.deckList.isEmpty())
            return Collections.emptyList();
        else
            return deckList;
    }
}
