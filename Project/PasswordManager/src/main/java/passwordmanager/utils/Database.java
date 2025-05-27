package passwordmanager.utils;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;


public class Database {
    private static final Properties properties = new Properties();

    static {
        try (InputStream input = Database.class.getClassLoader().getResourceAsStream("application.properties")) {
            if (input == null) {
                throw new RuntimeException("Файл application.properties не найден!");
            }
            properties.load(input);
        } catch (IOException e) {
            throw new RuntimeException("Ошибка загрузки application.properties", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        String url = properties.getProperty("db.url");
        String user = properties.getProperty("db.user");
        String password = properties.getProperty("db.password");
        return DriverManager.getConnection(url, user, password);
    }
}