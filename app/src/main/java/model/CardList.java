package model;

import card.Card;
import com.google.gson.annotations.SerializedName;

import java.util.Collections;
import java.util.List;

public class CardList {

    @SerializedName("cardReviewList")
    private List<Card> cardList;

    public List<Card> getCardList() {
        if(this.cardList == null || this.cardList.isEmpty())
            return Collections.emptyList();
        else
            return cardList;
    }
}