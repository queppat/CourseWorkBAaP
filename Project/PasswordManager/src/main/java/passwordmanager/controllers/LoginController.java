package passwordmanager.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import passwordmanager.DAO.SuperUserDAO;
import passwordmanager.models.SuperUser;
import passwordmanager.utils.*;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;

public class LoginController {
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private TextField visiblePasswordField;
    @FXML
    private ImageView eyeIcon;
    @FXML
    private Button loginButton;
    @FXML
    private ImageView backIcon;

    private final Image openedEyeIcon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/passwordmanager/icons/open-red-eye.png")));
    private final Image closedEyeIcon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/passwordmanager/icons/close-red-eye.png")));

    private boolean isPasswordVisible = false;

    private SuperUserDAO superUserDAO;

    @FXML
    public void initialize() {
        try {
            Connection connection = Database.getConnection();
            superUserDAO = new SuperUserDAO(connection);

            visiblePasswordField.textProperty().bindBidirectional(passwordField.textProperty());

            UIBehaviorUtils.setupFieldNavigation(usernameField, passwordField);
            UIBehaviorUtils.setupFinalField(passwordField, this::handleLogin);
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    @FXML
    public void togglePasswordVisibility() {
        isPasswordVisible = UIBehaviorUtils.togglePasswordVisibility(
                isPasswordVisible,
                passwordField,
                visiblePasswordField,
                eyeIcon,
                openedEyeIcon,
                closedEyeIcon
        );
    }

    @FXML
    private void handleLogin(){
        String username = usernameField.getText();
        String masterPassword = passwordField.getText();

        if(username.isEmpty() || masterPassword.isEmpty()){
            AlertUtils.showErrorAlert("Заполните все поля");
            return;
        }

        try{
            SuperUser superUser = superUserDAO.getSuperUserByUsername(username);

            if(superUser == null){
                AlertUtils.showErrorAlert("Пользователь не найден");
                return;
            }

            if(superUser.verifyMasterPassword(masterPassword)){
                UserSession.getInstance().login(superUser.getId(), masterPassword);

                AlertUtils.showSuccessAlert("Вход выполнен успешно");

                Stage stage = (Stage) loginButton.getScene().getWindow();
                WindowManager.mainSwitchScene(stage, "/passwordmanager/fxml/main.fxml","KeyForge");
            } else{
                AlertUtils.showErrorAlert("Неверный пароль");
            }
        } catch (Exception e){
            AlertUtils.showErrorAlert("Ошибка при входе в систему");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleBack(){
        try{
            Stage stage = (Stage) backIcon.getScene().getWindow();
            WindowManager.defaultSwitchScene(stage,"/passwordmanager/fxml/auth.fxml","KeyForge");
        } catch(IOException e){
            AlertUtils.showErrorAlert("Ошибка возврата на главный экран");
            e.printStackTrace();
        }
    }
}
