package passwordmanager.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import passwordmanager.DAO.ServicesDAO;
import passwordmanager.dto.ServiceDTO;
import passwordmanager.utils.*;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;
//TODO посмотреть названия окошек, сверить чтобы они были одинаковые
public class EditServiceInformationController {
    @FXML
    private TextField serviceNameField;
    @FXML
    private TextField serviceUsernameField;
    @FXML
    private PasswordField servicePasswordField;
    @FXML
    private TextField visibleServicePasswordField;
    @FXML
    private TextField urlField;

    @FXML
    private Button saveButton;
    @FXML
    private Button cancelButton;
    @FXML
    private ImageView deleteIcon;
    @FXML
    private ImageView backIcon;

    @FXML
    private ImageView eyeIcon;

    boolean isPasswordVisible = false;

    private final Image openedEyeIcon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/passwordmanager/icons/open-purple-eye.png")));
    private final Image closedEyeIcon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/passwordmanager/icons/close-purple-eye.png")));

    private ServicesDAO servicesDAO;
    private int serviceId;

    UserSession userSession = UserSession.getInstance();

    public void setServiceID(int serviceId) {
        this.serviceId = serviceId;
        loadInformation();
    }

    @FXML
    public void initialize() {
        try{
            Connection connection = Database.getConnection();
            servicesDAO = new ServicesDAO(connection);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        Platform.runLater(() -> serviceNameField.requestFocus());
        visibleServicePasswordField.textProperty().bindBidirectional(servicePasswordField.textProperty());

    }

    private void loadInformation() {
        ServiceDTO selectedService = servicesDAO.getServiceDTOById(serviceId);

        if (selectedService != null) {
            serviceNameField.setText(selectedService.getServiceName());
            serviceUsernameField.setText(selectedService.getUsername());
            urlField.setText(selectedService.getUrl());

            String decryptedPassword = servicesDAO.getDecryptedPassword(serviceId,userSession.getMasterPassword());
            servicePasswordField.setText(decryptedPassword);
            visibleServicePasswordField.setText(decryptedPassword);
        } else {
            AlertUtils.showErrorAlert("Ошибка: сервис с ID " + serviceId + " не найден.");
        }
    }

    @FXML
    public void togglePasswordVisibility() {
        isPasswordVisible = UIBehaviorUtils.togglePasswordVisibility(
                isPasswordVisible,
                servicePasswordField,
                visibleServicePasswordField,
                eyeIcon,
                openedEyeIcon,
                closedEyeIcon
        );
    }

    @FXML
    public void handleBackAction() {
        try {
            Stage stage = (Stage) backIcon.getScene().getWindow();
            WindowManager.slideReplaceWindowFromLeft(stage,"/passwordmanager/fxml/service_information.fxml","Информация о сервисе",serviceId);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleDeleteServiceAction() {
        boolean confirmed = AlertUtils.showConfirmationDialog("Вы действительно хотите удалить этот элемент?");
        if(confirmed) {
            boolean successfulRemove = servicesDAO.deleteServiceById(serviceId);
            if(successfulRemove) {
                try {
                    Stage stage = (Stage) deleteIcon.getScene().getWindow();
                    WindowManager.slideReplaceWindowFromLeft(stage, "/passwordmanager/fxml/main.fxml", "KeyForge",null);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else {
                AlertUtils.showErrorAlert("Что-то пошло не так при удалении пользователя!");
            }
        }
    }

    public void handleSaveServiceAction() {
        try {
            String newServiceName = serviceNameField.getText();
            String newServiceUsername = serviceUsernameField.getText();
            String newServicePassword = servicePasswordField.getText();
            String newUrl = urlField.getText();

            String newSalt = PasswordHasher.generateSalt();
            String newEncryptedPassword = AESEncryptor.encrypt(newServicePassword, userSession.getMasterPassword(), newSalt);

            servicesDAO.updateServiceById(serviceId, newServiceName, newServiceUsername, newEncryptedPassword, newUrl, newSalt);

            try {
                Stage stage = (Stage) saveButton.getScene().getWindow();
                WindowManager.slideReplaceWindowFromLeft(stage, "/passwordmanager/fxml/service_information.fxml", "Информация о сервисе",serviceId);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
