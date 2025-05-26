package passwordmanager.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import passwordmanager.DAO.ServicesDAO;
import passwordmanager.dto.ServiceDTO;
import passwordmanager.utils.AlertUtils;
import passwordmanager.utils.Database;
import passwordmanager.utils.UserSession;
import passwordmanager.utils.WindowManager;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;


public class ServiceStorageController {

    @FXML private TableView<ServiceDTO> serviceTable;
    @FXML private TableColumn<ServiceDTO, String> serviceColumn;
    @FXML private TableColumn<ServiceDTO, String> usernameColumn;

    private ServicesDAO servicesDAO;
    private final UserSession session = UserSession.getInstance();

    @FXML
    public void initialize() {
        try{
            Connection connection = Database.getConnection();
            servicesDAO = new ServicesDAO(connection);
        } catch (SQLException e){
            e.printStackTrace();
        }
        updateListOfServices();
    }

    private void updateListOfServices() {
        List<ServiceDTO> servicesList = servicesDAO.getAllServices(session.getUserId());
        serviceTable.getItems().setAll(servicesList);
    }

    @FXML
    private void handleAddPService() {
       try {
            Stage addServiceStage = WindowManager.openNewWindow("/passwordmanager/fxml/add_service.fxml",
                    "Добавление нового сервиса", 500, 500, false);
            addServiceStage.setOnHidden(event -> updateListOfServices());
       } catch (IOException e) {
            e.printStackTrace();
       }
    }

    @FXML
    private void showSelectedService() {
        //TODO открыть безопасное окно для копирования
        ServiceDTO selected = serviceTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Пароль");
            alert.setHeaderText("Пароль для " + selected.getServiceName());
            alert.setContentText(selected.getPassword());
            alert.showAndWait();
        } else {
            AlertUtils.showErrorAlert("Выберите запись для просмотра пароля");
        }
    }
}
