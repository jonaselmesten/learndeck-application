package database;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.sql.DataSource;
import java.beans.PropertyVetoException;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.Properties;

/**<h1>Connection pool</h1>
 * Connection pool for the database connections.
 * <p> Current settings:
 * Initial pool size: 5
 * Max pool size: 25 </p>
 * @author Jonas Elmesten
 */
public class ConnectionPool {

    private final static Logger logger = LogManager.getLogger(ConnectionPool.class);
    private final static ComboPooledDataSource pool = new ComboPooledDataSource();
    private final static Path configFile = Path.of(System.getProperty("user.dir") + "\\src\\main\\resources\\conifg\\db.properties");

    private ConnectionPool() {};

    public static void initialize() {

        try(FileInputStream fis = new FileInputStream(String.valueOf(configFile))) {

            Properties properties = new Properties();
            properties.load(fis);

            pool.setDriverClass("com.mysql.cj.jdbc.Driver");
            pool.setJdbcUrl(properties.getProperty("url"));
            pool.setUser(properties.getProperty("user"));
            pool.setPassword(properties.getProperty("password"));

            pool.setInitialPoolSize(5);
            pool.setAcquireIncrement(5);
            pool.setMaxPoolSize(25);
            pool.setAcquireRetryAttempts(0);

        }catch(IOException | PropertyVetoException e) {
            e.printStackTrace();
        }
    };

     /** Returns the data source which you can get a database connection from.
     * @return data source */
    public static DataSource getDataSource() {

        try {
            logger.debug("Method call:getDataSource - Busy connections:" + pool.getNumBusyConnections());
        }catch(SQLException e) {
            e.printStackTrace();
        }

        return pool;
    }
}
