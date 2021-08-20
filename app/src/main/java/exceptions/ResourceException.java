package exceptions;

public class ResourceException extends Exception{
    public ResourceException(String string) {
        super("Error when trying to find resource in disk: "+ string);
    }
}
