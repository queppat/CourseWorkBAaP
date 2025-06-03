package passwordmanager.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import passwordmanager.utils.UserSession;
import passwordmanager.utils.WindowManager;

import java.io.IOException;

public class ServicePasswordGeneratorController {
    @FXML
    private StackPane contentPane;
    @FXML
    private ImageView backIcon;
    @FXML
    private Button applyPasswordButton;

    private String backFxmlFilePath;
    private String backTitle;

    private String generatedPassword;

    private FXMLLoader currentLoader;

    @FXML
    public void initialize() {
        backFxmlFilePath = UserSession.getInstance().getFxmlFilePath();
        backTitle = UserSession.getInstance().getTitle();

        try {
            currentLoader = new FXMLLoader(
                    getClass().getResource("/passwordmanager/fxml/main_password_generator.fxml")
            );
            Parent screen = currentLoader.load();
            contentPane.getChildren().setAll(screen);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleApplyPasswordAction() {
        try {
            MainPasswordGeneratorController controller = currentLoader.getController();

            if (controller != null) {
                generatedPassword = controller.getGeneratedPassword();
                UserSession.getInstance().setGeneratedPassword(generatedPassword);
            } else {
                System.err.println("Контроллер не загружен!");
            }

            Stage stage = (Stage) applyPasswordButton.getScene().getWindow();
            WindowManager.mainSwitchScene(stage, backFxmlFilePath, backTitle);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleBackAction() {
        try {
            Stage stage = (Stage) backIcon.getScene().getWindow();
            WindowManager.mainSwitchScene(stage, backFxmlFilePath, backTitle);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
