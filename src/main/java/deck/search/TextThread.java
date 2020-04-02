package deck.search;

import deck.card.Card;
import deck.card.CardComponent;
import deck.card.component.CardComponents;
import deck.card.component.TextAreaComponent;

import java.util.List;

class TextThread implements Runnable {

    private final String SEARCH_WORD;
    private final List<Card> searchResultList;
    private final List<Card> subList;

    TextThread(List<Card> subList, List<Card> searchResultList, String searchWord) {
        SEARCH_WORD = searchWord;
        this.searchResultList = searchResultList;
        this.subList = subList;
    }

    @Override
    public void run() {

        //Searches in all cards' text-area components after the search word.
        for(Card card : subList) {

            for(CardComponent component : card.getComponentList()) {

                if(CardComponents.TEXT_AREA == component.getComponentEnum()) {

                    TextAreaComponent text = (TextAreaComponent) component;
                    int index = text.getRawObject().toLowerCase().indexOf(SEARCH_WORD.toString().toLowerCase());

                    if(index != -1) {
                        searchResultList.add(card);
                        break;
                    }
                }
            }
        }
    }
}
