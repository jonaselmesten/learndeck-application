package deck.search;

import com.mysql.cj.xdevapi.Collection;
import deck.Deck;
import deck.card.Card;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;

public class CardTextSearcher {

    private final CountDownLatch latch;
    private final List<Card> searchResultList = new ArrayList<>();
    private final int THREAD_COUNT;
    private final Deck deck;
    private final ExecutorService searchThreads;
    private String searchWord;

    public CardTextSearcher(Deck deck, int threadCount) {
        this.deck = deck;
        THREAD_COUNT = threadCount;
        latch = new CountDownLatch(threadCount);
        searchThreads = Executors.newFixedThreadPool(threadCount);
    }

    //TO DO: Make search threads wait for all.
    public List<Card> searchAfterWord(String word) {

        searchWord = searchWord.trim().toLowerCase();

        if(searchWord.isEmpty())
            return Collections.emptyList();

        startThreads();

        return searchResultList;
    }

    private void startThreads() {

        //Gather information to distribute the search over all of the search threads.
        List<Card> list = deck.getImmutableList();
        int listSize = list.size();
        int listSizeDivided = listSize / THREAD_COUNT;
        int excess = listSize % THREAD_COUNT;
        int fromIndex = 0;
        int toIndex = listSizeDivided - 1;
        boolean excessLeft = false;

        //Even out the load for each thread and then submit to the executor service.
        if(excess > 0){excessLeft = true;}
        for(int i = 1; i < THREAD_COUNT + 1; i++) {

            if(excessLeft) {

                toIndex++;
                searchThreads.submit(new TextThread(list.subList(fromIndex, toIndex + 1), searchResultList, searchWord));
                excess--;
                fromIndex = toIndex + 1;
                toIndex += listSizeDivided;

                if(excess == 0){excessLeft = false;}
                continue;
            }

            searchThreads.submit(new TextThread(list.subList(fromIndex, toIndex +1), searchResultList, searchWord));
            fromIndex = toIndex + 1;
            toIndex += listSizeDivided;
        }
    }
}
