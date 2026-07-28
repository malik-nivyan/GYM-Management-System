import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class dump {

    private static final String DEFAULT_URL = "jdbc:mysql://127.0.0.1:3306/dump?serverTimezone=UTC";
    private static final String DEFAULT_USER = "root";
    private static final String DEFAULT_PASSWORD = "root";  // change if your MySQL password is different

    private static final String URL = readConfig("GYM_DB_URL", "gym.db.url", DEFAULT_URL);
    private static final String USER = readConfig("GYM_DB_USER", "gym.db.user", DEFAULT_USER);
    private static final String PASSWORD = readConfig("GYM_DB_PASSWORD", "gym.db.password", DEFAULT_PASSWORD);

    private static String readConfig(String envName, String propertyName, String defaultValue) {
        String propertyValue = System.getProperty(propertyName);
        if (propertyValue != null && !propertyValue.isBlank()) {
            return propertyValue;
        }

        String envValue = System.getenv(envName);
        if (envValue != null && !envValue.isBlank()) {
            return envValue;
        }

        return defaultValue;
    }

    public static Connection getConnection() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Connected Successfully to Database 'Dump'!");
        } catch (SQLException e) {
            System.out.println("Connection Failed!");
            e.printStackTrace();
        }
        return conn;
    }
}






