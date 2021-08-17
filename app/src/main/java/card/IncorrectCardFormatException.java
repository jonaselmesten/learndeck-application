package card;

public class IncorrectCardFormatException extends RuntimeException {
    public IncorrectCardFormatException(String message) {
        super(message);
    }
}
