package exceptions;

import exceptions.IncorrectCardFormatException;

public class CardComponentException extends IncorrectCardFormatException {
    public CardComponentException(String message) {
        super(message);
    }
}
