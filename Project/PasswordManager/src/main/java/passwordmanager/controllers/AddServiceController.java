package passwordmanager.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import passwordmanager.DAO.ServicesDAO;
import passwordmanager.models.Service;
import passwordmanager.utils.*;

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
    private TextField URLField;
    @FXML
    private Button addServiceButton;

    private ServicesDAO servicesDAO;

    private final Image openedEyeIcon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/passwordmanager/icons/open-purple-eye.png")));
    private final Image closedEyeIcon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/passwordmanager/icons/close-purple-eye.png")));

    private boolean isPasswordVisible = false;

    public void initialize() {
        try {
            Connection connection = Database.getConnection();
            servicesDAO = new ServicesDAO(connection);

            visibleServicePasswordField.textProperty().bindBidirectional(servicePasswordField.textProperty());

            Platform.runLater(() -> usernameField.requestFocus());

            UIBehaviorUtils.setupFieldNavigation(serviceNameField, usernameField);
            UIBehaviorUtils.setupFieldNavigation(usernameField, servicePasswordField);
            UIBehaviorUtils.setupFieldNavigation(servicePasswordField, URLField);
            UIBehaviorUtils.setupFinalField(URLField, this::handleAddServiceButtonAction);
        } catch (SQLException e){
            e.printStackTrace();
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
    public void handleAddServiceButtonAction() {
        String serviceName = serviceNameField.getText();
        String username = usernameField.getText();
        String password = servicePasswordField.getText();
        String url = URLField.getText();

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
                WindowManager.closeWindow(addServiceButton);
            }
       } catch (Exception e){
            AlertUtils.showErrorAlert("Ошибка при входе в систему");
            e.printStackTrace();
        }
    }

}
