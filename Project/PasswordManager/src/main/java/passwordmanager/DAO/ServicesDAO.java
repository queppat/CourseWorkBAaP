package passwordmanager.DAO;

import passwordmanager.dto.ServiceDTO;
import passwordmanager.dto.TableServiceDTO;
import passwordmanager.models.Service;
import passwordmanager.utils.AESEncryptor;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServicesDAO {
    private final Connection connection;

    public ServicesDAO(Connection connection) {
        this.connection = connection;
        createTableIfNotExists();
    }

    public void createTableIfNotExists() {
        String sqlServices ="CREATE TABLE IF NOT EXISTS services (" +
                "id SERIAL PRIMARY KEY," +
                "user_id INTEGER REFERENCES super_users(id) ON DELETE CASCADE," +
                "service_name VARCHAR(100) NOT NULL," +
                "username VARCHAR(100) NOT NULL," +
                "encrypted_password TEXT NOT NULL," +
                "URL TEXT NOT NULL," +
                "salt TEXT NOT NULL)";


        try(Statement statement = connection.createStatement()){
            statement.execute(sqlServices);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean addService(Service service) {
        try {

            String sql = "INSERT INTO services (user_id, service_name ,username, encrypted_password, URL, salt) VALUES (?, ?, ?, ?, ?, ?)";

            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setInt(1, service.getUserId());
                stmt.setString(2, service.getServiceName());
                stmt.setString(3, service.getUsername());
                stmt.setString(4, service.getEncryptedPassword());
                stmt.setString(5, service.getURL());
                stmt.setString(6, service.getSalt());

                return stmt.executeUpdate() > 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public String getDecryptedPassword(int serviceId, String masterPassword){
        String sql = "SELECT encrypted_password, salt FROM services WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, serviceId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String encryptedPassword = rs.getString("encrypted_password");
                String salt = rs.getString("salt");

                return AESEncryptor.decrypt(encryptedPassword, masterPassword, salt);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean deleteServiceById(int id) {
        String sql = "DELETE FROM services WHERE id=?";

        try(PreparedStatement preparedStatement = connection.prepareStatement(sql)){
                preparedStatement.setInt(1, id);
                int affectedRows = preparedStatement.executeUpdate();
                return affectedRows > 0;
        } catch (SQLException e){
            e.printStackTrace();
            return false;
        }
    }

    public List<TableServiceDTO> getAllServicesForTable(int userId){
        String sql = "SELECT id, service_name, username FROM services WHERE user_id=?";

        List<TableServiceDTO> services = new ArrayList<>();
        try(PreparedStatement preparedStatement = connection.prepareStatement(sql)){
            preparedStatement.setInt(1,userId);

            try(ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    services.add(new TableServiceDTO(
                            resultSet.getInt("id"),
                            resultSet.getString("service_name"),
                            resultSet.getString("username")
                    ));
                }
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
        return services;
    }

    public List<ServiceDTO> getAllServices(int userId) {
        String sql = "SELECT id, service_name, username, encrypted_password FROM services WHERE user_id=?";

        List<ServiceDTO> services = new ArrayList<>();
        try(PreparedStatement preparedStatement = connection.prepareStatement(sql)){
            preparedStatement.setInt(1,userId);

            try(ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    services.add(new ServiceDTO(
                            resultSet.getInt("id"),
                            resultSet.getString("service_name"),
                            resultSet.getString("username"),
                            resultSet.getString("encrypted_password")
                    ));
                }
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
        return services;
    }
}

