package passwordmanager.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import passwordmanager.DAO.ServicesDAO;
import passwordmanager.dto.ServiceDTO;
import passwordmanager.dto.TableServiceDTO;
import passwordmanager.utils.AlertUtils;
import passwordmanager.utils.Database;
import passwordmanager.utils.UserSession;
import passwordmanager.utils.WindowManager;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;


public class ServiceStorageController {

    @FXML private TableView<TableServiceDTO> serviceTable;
    @FXML private TableColumn<TableServiceDTO, String> serviceColumn;
    @FXML private TableColumn<TableServiceDTO, String> usernameColumn;

    private ServicesDAO servicesDAO;
    private final UserSession session = UserSession.getInstance();

    private boolean canChange = true;

    @FXML
    public void initialize() {
        try{
            Connection connection = Database.getConnection();
            servicesDAO = new ServicesDAO(connection);
        } catch (SQLException e){
            e.printStackTrace();
        }
        updateListOfServices();
        setupTableRowClickHandler();
    }


    private void setupTableRowClickHandler() {
        serviceTable.setRowFactory(tv -> {
            TableRow<TableServiceDTO> row = new TableRow<>();
            row.setOnMouseClicked(this::handleTableRowClick);
            return row;
        });
    }

    private void handleTableRowClick(MouseEvent event) {
        try {
            if (canChange) {
                canChange = false;
                TableRow<TableServiceDTO> row = (TableRow<TableServiceDTO>) event.getSource();
                if (!row.isEmpty()) {
                    TableServiceDTO selected = row.getItem();
                    Stage stage = (Stage) row.getScene().getWindow();
                    WindowManager.slideReplaceWindowFromRight(stage,"/passwordmanager/fxml/service_information.fxml", "Информация", selected.getId());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updateListOfServices() {
        List<TableServiceDTO> servicesList = servicesDAO.getAllServicesForTable(session.getUserId());
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
}
