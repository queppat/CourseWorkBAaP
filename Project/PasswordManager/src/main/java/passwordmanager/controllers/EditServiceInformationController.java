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
    private ImageView generateIcon;

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

            UserSession.getInstance().setFxmlFilePath("/passwordmanager/fxml/edit_service_information.fxml");
            UserSession.getInstance().setTitle("Изменение информации");

            if(userSession.getServiceId() !=-1){
                serviceId = userSession.getServiceId();
            }

            if(userSession.getServiceName() != null) {
                serviceNameField.setText(userSession.getServiceName());
            }

            if(userSession.getUsername() != null) {
                serviceUsernameField.setText(userSession.getUsername());
            }

            if(userSession.getGeneratedPassword() != null) {
                servicePasswordField.setText(userSession.getGeneratedPassword());
            }

            if(userSession.getUrl() !=null){
                urlField.setText(userSession.getUrl());
            }

            UIBehaviorUtils.setupFieldNavigation(serviceNameField, serviceUsernameField);
            UIBehaviorUtils.setupFieldNavigation(serviceUsernameField, servicePasswordField);
            UIBehaviorUtils.setupFieldNavigation(servicePasswordField, urlField);
            UIBehaviorUtils.setupFinalField(urlField, this::handleSaveServiceAction);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        Platform.runLater(() -> {
            serviceNameField.requestFocus();
            serviceNameField.deselect();
            serviceNameField.positionCaret(serviceNameField.getText().length());
        });

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
            UserSession.getInstance().deleteServiceInfo();
            Stage stage = (Stage) backIcon.getScene().getWindow();
            WindowManager.slideReplaceWindowFromLeft(stage,"/passwordmanager/fxml/service_information.fxml","Информация",serviceId);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleGeneratePasswordAction() {
        try {
            userSession.setServiceId(serviceId);
            userSession.setServiceName(serviceNameField.getText());
            userSession.setUsername(serviceUsernameField.getText());
            userSession.setGeneratedPassword(servicePasswordField.getText());
            userSession.setUrl(urlField.getText());

            Stage stage = (Stage) generateIcon.getScene().getWindow();
            WindowManager.mainSwitchScene(stage,"/passwordmanager/fxml/service_password_generator.fxml","Генератор");
        } catch (IOException e){
            AlertUtils.showErrorAlert("Ошибка перехода в Генератор");
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
        UserSession.getInstance().deleteServiceInfo();
    }

}
