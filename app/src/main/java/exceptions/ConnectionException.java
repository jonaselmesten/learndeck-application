package exceptions;

import java.io.IOException;

public class ConnectionException extends IOException {
    public ConnectionException(String string) {
        super("Error when trying to connect to webservice." + string);
    }
}