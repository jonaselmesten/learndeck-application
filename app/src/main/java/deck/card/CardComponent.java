package deck.card;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import deck.card.component.CardComponents;
import deck.card.component.ImageViewComponent;
import deck.card.component.SeparatorComponent;
import deck.card.component.TextAreaComponent;
import javafx.scene.Node;

/**
 * This interface must be implemented if you want to create a new type of card-component for the Card class.
 * @author Jonas Elmesten
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.WRAPPER_OBJECT, property = "cardComponent")
@JsonSubTypes( {
                @JsonSubTypes.Type(value = TextAreaComponent.class, name = "textArea"),
                @JsonSubTypes.Type(value = SeparatorComponent.class, name = "separator"),
                @JsonSubTypes.Type(value = ImageViewComponent.class, name = "imageView")})

public interface CardComponent {

    /**Returns the component as it's according JavaFX node so it can be shown in the GUI etc.
     * @return javafx.scene.Node
     */
    Node convertToNode();

    /**Returns the component as it's "raw" form.
     * @return Decided in the implementation.
     */
    Object getRawObject();

    /**Returns the components' enum instance.
     * @return CardComponents
     */
    CardComponents getComponentEnum();
}
