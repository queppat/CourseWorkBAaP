package passwordmanager.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import passwordmanager.utils.AlertUtils;
import passwordmanager.utils.WindowManager;

import java.io.IOException;

public class AuthController {
    @FXML
    private Button loginButton;
    @FXML
    private Button signupButton;

    //TODO Доработать login.fxml и signup.fxml (разделить TextFields на те в которых есть иконка и в которых нету)
    @FXML
    private void handleLogin() {
        try {
            Stage stage = (Stage) loginButton.getScene().getWindow();
            WindowManager.defaultSwitchScene(stage,"/passwordmanager/fxml/login.fxml", "Вход в систему");
        } catch (IOException e) {
            AlertUtils.showErrorAlert("Ошибка загрузки окна входа");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleSignup() {
        try {
            Stage stage = (Stage) signupButton.getScene().getWindow();
            WindowManager.defaultSwitchScene(stage,"/passwordmanager/fxml/signup.fxml", "Регистрация");
        } catch (IOException e) {
            AlertUtils.showErrorAlert("Ошибка загрузки окна регистрации");
            e.printStackTrace();
        }
    }
}