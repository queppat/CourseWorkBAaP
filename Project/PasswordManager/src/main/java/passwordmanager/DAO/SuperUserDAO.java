package passwordmanager.DAO;

import passwordmanager.models.SuperUser;

import java.sql.*;

public class SuperUserDAO {
    private final Connection connection;

    public SuperUserDAO(Connection connection) {
        this.connection = connection;
        createTableIfNotExists();
    }

    public void createTableIfNotExists() {
        String sql = "CREATE TABLE IF NOT EXISTS super_users (" +
                "id SERIAL PRIMARY KEY," +
                "username VARCHAR(50) UNIQUE NOT NULL," +
                "master_password_hash TEXT NOT NULL," +
                "salt TEXT NOT NULL)";
        try(Statement statement = connection.createStatement()){
            statement.execute(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean createSuperUser(SuperUser superUser) {
        String sql = "INSERT INTO super_users (username, master_password_hash, salt) VALUES (?, ?, ?)";

        try(PreparedStatement preparedStatement= connection.prepareStatement(sql)){
            preparedStatement.setString(1,superUser.getUsername());
            preparedStatement.setString(2,superUser.getMasterPasswordHash());
            preparedStatement.setString(3,superUser.getSalt()); ;
            preparedStatement.executeUpdate();
            return true;
        } catch (SQLException e) {
            if("23505".equals(e.getSQLState())) { //UNIQUE constraint failed
                System.out.println("Пользователь с таким именем существует");
            } else {
                e.printStackTrace();
            }
            return false;
        }
    }
    public SuperUser getSuperUserByUsername(String username) {
        String sql = "SELECT id, username, master_password_hash, salt FROM super_users WHERE username = ?";

        try(PreparedStatement preparedStatement = connection.prepareStatement(sql)){
            preparedStatement.setString(1,username);
            ResultSet resultSet = preparedStatement.executeQuery();

            if(resultSet.next()){
                SuperUser user = new SuperUser(
                        resultSet.getString("username"),
                        ""
                );
                user.setId(resultSet.getInt("id"));
                user.setMasterPasswordHash(resultSet.getString("master_password_hash"));
                user.setSalt(resultSet.getString("salt"));
                return user;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
