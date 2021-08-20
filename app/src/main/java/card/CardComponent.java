package card;


public interface CardComponent {

    /**Returns the component as it's "raw" form.
     * @return Decided in the implementation.
     */
    Object getRawObject();

    /**Returns the components' enum instance.
     * @return CardComponents
     */
    CardComponents getComponentEnum();
}