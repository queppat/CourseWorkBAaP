package passwordmanager.utils;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.stage.StageStyle;

import java.util.Optional;

public class AlertUtils {
    private static Alert createStyledAlert(Alert.AlertType type, String title, String header, String message) {
        Alert alert = new Alert(type);
        alert.initStyle(StageStyle.UTILITY);
        alert.getDialogPane().getStylesheets().add(AlertUtils.class.getResource("/passwordmanager/styles/alerts.css").toExternalForm());
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(message);
        return alert;
    }

    private static void showAlert(Alert.AlertType type, String title, String header, String message) {
        Alert alert = createStyledAlert(type, title, header, message);
        alert.showAndWait();
    }

    public static void showErrorAlert(String message) {
        showAlert(Alert.AlertType.ERROR, "Ошибка", null, message);
    }

    public static void showSuccessAlert(String message) {
        showAlert(Alert.AlertType.INFORMATION, "Успех", null, message);
    }

    public static void showInfoAlert(String message) {
        showAlert(Alert.AlertType.INFORMATION, "Информация", null, message);
    }

    public static void showWarningAlert(String message) {
        showAlert(Alert.AlertType.WARNING, "Предупреждение", null, message);
    }

    public static boolean showConfirmationDialog(String message) {
        Alert alert = createStyledAlert(Alert.AlertType.CONFIRMATION, "Подтверждение", null, message);
        alert.getButtonTypes().clear();

        ButtonType btnYes = new ButtonType("Да", ButtonBar.ButtonData.OTHER);
        ButtonType btnNo = new ButtonType("Нет", ButtonBar.ButtonData.OTHER);

        alert.getButtonTypes().addAll(btnYes, btnNo);

        alert.getDialogPane().lookupButton(btnYes).setId("yes-button");
        alert.getDialogPane().lookupButton(btnNo).setId("no-button");

        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == btnYes;
    }

}
