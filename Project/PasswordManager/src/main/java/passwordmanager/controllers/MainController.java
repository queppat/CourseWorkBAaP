package passwordmanager.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import passwordmanager.DAO.ServicesDAO;
import passwordmanager.utils.Database;

import java.sql.Connection;
import java.sql.SQLException;

public class MainController {

    @FXML
    private TabPane tabPane;
    
    private ServiceStorageTabController serviceStorageTabController;
    private PasswordGeneratorTabController passwordGeneratorTabController;

    @FXML private Tab serviceStorageTab;
    @FXML private Tab passwordGeneratorTab;

    private ServicesDAO servicesDAO;

    @FXML
    public void initialize() {
        try {
            Connection connection = Database.getConnection();
            servicesDAO = new ServicesDAO(connection);
        } catch (SQLException e){
            e.printStackTrace();
        }
        if(passwordGeneratorTabController != null && serviceStorageTabController != null) {
            serviceStorageTabController.setMainController(this);
            passwordGeneratorTabController.setMainController(this);
            serviceStorageTabController.setServicesDAO(servicesDAO);
            passwordGeneratorTabController.setServicesDAO(servicesDAO);
        }
    }
}
