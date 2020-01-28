package menu;

import java.net.URL;

class DBFiles {

    private DBFiles() {};

    private static URL db;

    static void setDBconfig(URL url) {
        DBFiles.db = url;
    }

    static URL getDBconfig() {
        return DBFiles.db;
    }
}
