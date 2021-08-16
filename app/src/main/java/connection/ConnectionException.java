package connection;

public class ConnectionException extends Exception {
    public ConnectionException(String string) {
        super("Error when trying to connect to webservice." + string);
    }
}