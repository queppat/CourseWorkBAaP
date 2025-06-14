package passwordmanager.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import passwordmanager.utils.AlertUtils;
import passwordmanager.utils.UserSession;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class MainController {
    @FXML
    private StackPane contentPane;
    @FXML
    private Button storageButton;
    @FXML
    private Button passwordGenerateButton;

    @FXML
    private ImageView backIcon;

    private final Map<String, Parent> screens = new HashMap<>();

    @FXML
    private void initialize() {
        loadScreen("storage", "/passwordmanager/fxml/service_storage.fxml");
        loadScreen("generator", "/passwordmanager/fxml/main_password_generator.fxml");

        setScreen("storage");
    }

    @FXML
    public void handleBackToAuthAction() {
        boolean confirmed = AlertUtils.showConfirmationDialog("Вы уверены, что хотите выйти из аккаунта?");
        if(confirmed) {
            UserSession.getInstance().quietLogout();
        }
    }

    private void loadScreen(String name, String fxmlFile) {
        try {
            Parent screen = FXMLLoader.load(Objects.requireNonNull(getClass().getResource(fxmlFile)));
            screens.put(name, screen);
        } catch (IOException e) {
            AlertUtils.showErrorAlert("Ошибка загрузки " + fxmlFile + ": " + e.getMessage());
        }
    }

    private void setScreen(String name) {
        if (screens.containsKey(name)) {
            contentPane.getChildren().setAll(screens.get(name));
        }

        storageButton.getStyleClass().remove("active-tab");
        passwordGenerateButton.getStyleClass().remove("active-tab");

        if ("storage".equals(name)) {
            storageButton.getStyleClass().add("active-tab");
        } else if ("generator".equals(name)) {
            passwordGenerateButton.getStyleClass().add("active-tab");
        }
    }

    @FXML
    private void showStorage() {
        setScreen("storage");
    }

    @FXML
    private void showGenerator() {
        setScreen("generator");
    }

}