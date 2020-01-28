package deck;


/**Interface for classes that will be de-/serialized.
 * They will need to have an instance of IoStatus class for the methods in this interface.
 * @author Jonas Elmesten
 */
public interface IOObject {

    IOStatus getIoStatus();

    void setIoStatus(IOStatus ioStatus);
}
