package deck.card.component;

import deck.card.CardComponent;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

/**<h1>Image view component</h1>
 * This class is used to represent an image in cards.
 * @author Jonas Elmesten
 */
public class ImageViewComponent implements CardComponent {

    private final byte[] byteArray;
    private final CardComponents componentEnum = CardComponents.IMAGE_VIEW;
    private boolean isBig;

    public ImageViewComponent(String picture) {
        byteArray = Base64.getDecoder().decode(picture);
    }

    public ImageViewComponent(byte[] byteArray) {
        this.byteArray = byteArray;
    }

    public ImageViewComponent(Image image) throws IOException {
        byteArray = convertImageToByteArray(image);
    }

    private static byte[] convertImageToByteArray(Image image) throws IOException {

        ByteArrayOutputStream byteOutput = new ByteArrayOutputStream();
        ImageIO.write(SwingFXUtils.fromFXImage(image,null),"png",byteOutput);

        return byteOutput.toByteArray();
    }

    @Override
    public ImageView convertToNode() {

        ImageView imageView = new ImageView(new Image(new ByteArrayInputStream(byteArray)));
        imageView.setPreserveRatio(true);

        isBig = true;

        //Change the size of the picture when double-clicked
        imageView.setOnMouseClicked(event -> {

            if(event.getButton().equals(MouseButton.PRIMARY)) {

                if(event.getClickCount() == 2) {

                    if(isBig) {

                        System.out.println("MAKE SMALL");
                        imageView.setFitWidth(400);
                        imageView.setFitHeight(400);
                        isBig = false;

                    }else {

                        System.out.println("MAKE BIG");
                        imageView.setFitWidth(800);
                        imageView.setFitHeight(800);
                        isBig = true;
                    }
                }
            }
        });

        return imageView;
    }

    /**@return String*/
    @Override
    public String getRawObject() {
        return Base64.getEncoder().encodeToString(byteArray);
    }
    @Override
    public CardComponents getComponentEnum() {
        return this.componentEnum;
    }
}
