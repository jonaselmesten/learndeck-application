package deck.card;

/**Thrown when a card with incorrect format is trying to be created.
 * <p>Correct format: 3 or more card components - Has one separator-component</p>
 * @author Jonas Elmesten
 */
public class IncorrectCardFormatException extends RuntimeException {

    public IncorrectCardFormatException(String message) {
        super(message);
    }
}
