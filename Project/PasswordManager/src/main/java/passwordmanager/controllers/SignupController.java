package passwordmanager.controllers;

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

public class SignupController {
    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;
    @FXML
    private TextField visiblePasswordField;
    @FXML
    private ImageView eyeIcon;

    @FXML
    private PasswordField confirmPasswordField;
    @FXML
    private TextField visibleConfirmPasswordField;
    @FXML
    private ImageView confirmEyeIcon;

    @FXML
    private Button signupButton;

    @FXML
    private ImageView backIcon;

    private final Image openedEyeIcon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/passwordmanager/icons/open-purple-eye.png")));
    private final Image closedEyeIcon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/passwordmanager/icons/close-purple-eye.png")));

    private boolean isPasswordVisible = false;
    private boolean isConfirmPasswordVisible = false;

    private SuperUserDAO superUserDAO;

    @FXML
    public void initialize() {
        eyeIcon.setImage(closedEyeIcon);
        confirmEyeIcon.setImage(closedEyeIcon);

        visiblePasswordField.textProperty().bindBidirectional(passwordField.textProperty());
        visibleConfirmPasswordField.textProperty().bindBidirectional(confirmPasswordField.textProperty());

        try {
            Connection connection = Database.getConnection();
            superUserDAO = new SuperUserDAO(connection);
        } catch (SQLException e){
            AlertUtils.showErrorAlert("Ошибка подключения к базе данных");
            e.printStackTrace();
        }
        UIBehaviorUtils.setupFieldNavigation(usernameField, passwordField);
        UIBehaviorUtils.setupFieldNavigation(passwordField, confirmPasswordField);
        UIBehaviorUtils.setupFinalField(confirmPasswordField, this::handleSignup);
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
    public void toggleConfirmPasswordVisibility() {
        isConfirmPasswordVisible = UIBehaviorUtils.togglePasswordVisibility(
                isConfirmPasswordVisible,
                confirmPasswordField,
                visibleConfirmPasswordField,
                confirmEyeIcon,
                openedEyeIcon,
                closedEyeIcon
        );
    }

    @FXML
    private void handleSignup() {
        String username = usernameField.getText();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        if(username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            AlertUtils.showErrorAlert("Заполните все поля!");
            return;
        }

        if(!password.equals(confirmPassword)) {
            AlertUtils.showErrorAlert("Пароли не совпадают");
            return;
        }

        SuperUser superUser = new SuperUser(username, password);
        boolean isCreated = superUserDAO.createSuperUser(superUser);

        if(isCreated) {
            AlertUtils.showSuccessAlert("Регистрация успешна!");
            try{
                Stage stage = (Stage) signupButton.getScene().getWindow();
                WindowManager.defaultSwitchScene(stage,"/passwordmanager/fxml/auth.fxml","KeyForge");
            } catch (IOException e){
                e.printStackTrace();
            }
        } else{
            AlertUtils.showErrorAlert("Пользователь с таким именем уже существует");
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
