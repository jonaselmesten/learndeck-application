package deck.card.component;

import deck.card.CardComponent;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.control.Separator;

/**<h1>Separator component</h1>
 * This class is used when you want separate the question from the answer in a card.
 * <p>Example: Question - Separator - Answer</p>
 * @author Jonas Elmesten
 */
public class SeparatorComponent implements CardComponent {

    private int prefWidth = 1920;
    private final CardComponents componentEnum = CardComponents.SEPARATOR;

    public SeparatorComponent() {};

    public SeparatorComponent(int prefWidth) {
        this.prefWidth = prefWidth;
    }

    /**@return javafx.scene.control.Separator*/
    @Override
    public Separator convertToNode() {

        Separator separator = new Separator(Orientation.HORIZONTAL);
        separator.setPrefWidth(prefWidth);
        separator.setPadding(new Insets(15,0,15,0));

        return separator;
    }

    /**@return Integer*/
    @Override
    public Integer getRawObject() {
        return prefWidth;
    }

    @Override
    public CardComponents getComponentEnum() {
        return componentEnum;
    }
}
