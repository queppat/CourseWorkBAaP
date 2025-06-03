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
import passwordmanager.models.Service;
import passwordmanager.utils.*;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;

public class AddServiceController {

    @FXML
    private TextField serviceNameField;
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField servicePasswordField;
    @FXML
    private TextField visibleServicePasswordField;
    @FXML
    private ImageView eyeIcon;
    @FXML
    private ImageView generateIcon;
    @FXML
    private TextField urlField;
    @FXML
    private Button addServiceButton;
    @FXML
    private ImageView backIcon;

    private ServicesDAO servicesDAO;
    private UserSession userSession = UserSession.getInstance();

    private final Image openedEyeIcon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/passwordmanager/icons/open-purple-eye.png")));
    private final Image closedEyeIcon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/passwordmanager/icons/close-purple-eye.png")));

    private boolean isPasswordVisible = false;

    public void initialize() {
        try {
            Connection connection = Database.getConnection();
            servicesDAO = new ServicesDAO(connection);

            UserSession.getInstance().setFxmlFilePath("/passwordmanager/fxml/add_service.fxml");
            UserSession.getInstance().setTitle("Добавление сервиса");

            if(userSession.getServiceName() != null) {
                serviceNameField.setText(userSession.getServiceName());
            }

            if(userSession.getUsername() != null) {
                usernameField.setText(userSession.getUsername());
            }

            if(userSession.getGeneratedPassword() != null) {
                servicePasswordField.setText(userSession.getGeneratedPassword());
            }

            if(userSession.getUrl() !=null){
                urlField.setText(userSession.getUrl());
            }

            visibleServicePasswordField.textProperty().bindBidirectional(servicePasswordField.textProperty());

            UIBehaviorUtils.setupFieldNavigation(serviceNameField, usernameField);
            UIBehaviorUtils.setupFieldNavigation(usernameField, servicePasswordField);
            UIBehaviorUtils.setupFieldNavigation(servicePasswordField, urlField);
            UIBehaviorUtils.setupFinalField(urlField, this::handleAddServiceButtonAction);
        } catch (SQLException e){
            e.printStackTrace();
        }

        Platform.runLater(() -> {
            serviceNameField.requestFocus();
            serviceNameField.deselect();
            serviceNameField.positionCaret(serviceNameField.getText().length());
        });
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
            WindowManager.slideReplaceWindowFromLeft(stage, "/passwordmanager/fxml/main.fxml", "KeyForge",null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleAddServiceButtonAction() {
        String serviceName = serviceNameField.getText();
        String username = usernameField.getText();
        String password = servicePasswordField.getText();
        String url = urlField.getText();

        if(serviceName.isEmpty() || username.isEmpty() || password.isEmpty() || url.isEmpty()){
            AlertUtils.showErrorAlert("Заполните все поля");
            return;
        }

        UserSession session = UserSession.getInstance();
        int userId = session.getUserId();
        String masterPassword = session.getMasterPassword();

        if (masterPassword == null) {
            AlertUtils.showErrorAlert("Сессия истекла");
            return;
        }

        try {
            String salt = PasswordHasher.generateSalt();

            String encryptedPassword = AESEncryptor.encrypt(password, masterPassword ,salt);

            Service service = new Service(
                    userId,
                    serviceName,
                    username,
                    encryptedPassword,
                    url,
                    salt
            );
            if(servicesDAO.addService(service)){
                AlertUtils.showSuccessAlert("Сервис успешно добавлен");
                try {
                    Stage stage = (Stage) addServiceButton.getScene().getWindow();
                    WindowManager.slideReplaceWindowFromLeft(stage, "/passwordmanager/fxml/main.fxml", "KeyForge",null);
                } catch (IOException e){
                    e.printStackTrace();
                }
            }
       } catch (Exception e){
            AlertUtils.showErrorAlert("Ошибка при входе в систему");
            e.printStackTrace();
        }

        UserSession.getInstance().deleteServiceInfo();
    }

    @FXML
    public void handleGeneratePasswordAction() {
        try{
            userSession.setServiceName(serviceNameField.getText());
            userSession.setUsername(usernameField.getText());
            userSession.setGeneratedPassword(servicePasswordField.getText());
            userSession.setUrl(urlField.getText());


            Stage stage = (Stage) generateIcon.getScene().getWindow();
            WindowManager.mainSwitchScene(stage, "/passwordmanager/fxml/service_password_generator.fxml","Генератор");
        } catch (IOException e){
            AlertUtils.showErrorAlert("Ошибка перехода в Генератор");
            e.printStackTrace();
        }
    }

}
