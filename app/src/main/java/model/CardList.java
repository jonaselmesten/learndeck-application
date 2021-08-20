package model;

import com.google.gson.annotations.SerializedName;

import java.util.Collections;
import java.util.List;

public class CardList {

    @SerializedName("cardReviewList")
    private List<CardResponse> cardList;

    public List<CardResponse> getCardList() {
        if(this.cardList == null || this.cardList.isEmpty())
            return Collections.emptyList();
        else
            return cardList;
    }
}