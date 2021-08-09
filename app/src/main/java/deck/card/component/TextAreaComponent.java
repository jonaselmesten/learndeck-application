package deck.card.component;

import deck.card.CardComponent;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

/**<h1>Text area component</h1>
 * This class is used to represent the text parts int a card.
 * @author Jonas Elmesten
 */
public class TextAreaComponent implements CardComponent {

    private final String STRING;
    private final CardComponents componentEnum = CardComponents.TEXT_AREA;

    public TextAreaComponent(String string)
    {
        this.STRING = string;
    }

    /**@return javafx.scene.text.Text*/
    @Override
    public Text convertToNode() {

        Text text = new Text(STRING);
        text.setStyle("-fx-font: 24 arial;");
        text.setTextAlignment(TextAlignment.CENTER);
        text.setWrappingWidth(500);

        return text;
    }

    /**@return String*/
    @Override
    public String getRawObject() {
        return STRING;
    }

    @Override
    public CardComponents getComponentEnum()
    {
        return  this.componentEnum;
    }

}
