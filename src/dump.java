import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class dump {

    private static final String URL = "jdbc:mysql://127.0.0.1:3306/dump";
    private static final String USER = "root";
    private static final String PASSWORD = "nivyan";  //

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






