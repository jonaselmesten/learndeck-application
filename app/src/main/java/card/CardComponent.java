package card;

import exceptions.IncorrectCardFormatException;

/**<h1>Card components</h1>
 * This enum contains all the components that a card can consist of.
 */
public enum CardComponent {

    TEXT, IMG_TEXT;

    public static CardComponent from(String questionType) throws IncorrectCardFormatException {
        switch (questionType) {
            case "TEXT":
                return TEXT;
            case "IMG_TEXT":
                return IMG_TEXT;
        }
        throw new IncorrectCardFormatException("Could not create CardComponent enum from string");
    }
}
