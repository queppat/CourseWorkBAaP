package passwordmanager.utils;

import javafx.application.Platform;
import javafx.scene.control.Control;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;

public class UIBehaviorUtils {
    public static void setupFieldNavigation(Control currentField, Control nextField) {
        currentField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                nextField.requestFocus();
            }
        });
    }

    public static void setupFinalField(Control field, Runnable onEnterAction) {
        field.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                onEnterAction.run();
            }
        });
    }

    public static boolean togglePasswordVisibility(boolean isPasswordVisible,
                                                   PasswordField passwordField,
                                                   TextField visiblePasswordField,
                                                   ImageView imageView,
                                                   Image visibleIcon,
                                                   Image hiddenIcon) {
        int caretPosition = isPasswordVisible ?
                visiblePasswordField.getCaretPosition() :
                passwordField.getCaretPosition();

        isPasswordVisible = !isPasswordVisible;

        if (isPasswordVisible) {
            passwordField.setVisible(false);
            visiblePasswordField.setVisible(true);
            imageView.setImage(visibleIcon);

            Platform.runLater(() -> {
                visiblePasswordField.requestFocus();
                visiblePasswordField.positionCaret(caretPosition);
            });
        } else {
            passwordField.setVisible(true);
            visiblePasswordField.setVisible(false);
            imageView.setImage(hiddenIcon);

            Platform.runLater(() -> {
                passwordField.requestFocus();
                passwordField.positionCaret(caretPosition);
            });        }

        return isPasswordVisible;
    }
}