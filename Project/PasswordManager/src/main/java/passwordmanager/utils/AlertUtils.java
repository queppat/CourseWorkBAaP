package passwordmanager.utils;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.stage.StageStyle;

import java.util.Optional;

public class AlertUtils {

    public static void showErrorAlert(String message) {
        showAlert(Alert.AlertType.ERROR,"Ошибка",null,message);
    }

    public static void showSuccessAlert(String message) {
        showAlert(Alert.AlertType.INFORMATION, "Успех", null, message);
    }

    public static void showInfoAlert(String message) {
        showAlert(Alert.AlertType.INFORMATION, "<UNK>", null, message);
    }

    public static void showWarningAlert(String message) {
        showAlert(Alert.AlertType.WARNING, "<UNK>", null, message);
    }

    public static void showConfirmationAlert(String message) {
        showAlert(Alert.AlertType.CONFIRMATION, "<UNK>", null, message);
    }

    private static void showAlert(Alert.AlertType type, String title, String header, String message) {
        Alert alert = new Alert(type);
        alert.initStyle(StageStyle.UTILITY);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static boolean showConfirmationDialog(String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.initStyle(StageStyle.UTILITY);
        alert.setTitle("Подтверждение");
        alert.setHeaderText(null);
        alert.setContentText(message);

        alert.getButtonTypes().clear();

        ButtonType btntNo = new ButtonType("Нет", ButtonBar.ButtonData.NO);
        ButtonType btntYes = new ButtonType("Да", ButtonBar.ButtonData.YES);

        alert.getButtonTypes().addAll(btntNo, btntYes);

        Optional<ButtonType> result = alert.showAndWait();

        return result.isPresent() && result.get() == btntYes;
    }
}
