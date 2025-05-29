package passwordmanager.controllers;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import passwordmanager.utils.PasswordGenerator;

public class PasswordGeneratorController {
    @FXML
    private TextArea passwordField;
    @FXML
    private TextField lengthOfPasswordField;

    @FXML
    private CheckBox lowerCaseCheckBox;
    @FXML
    private CheckBox upperCaseCheckBox;
    @FXML
    private CheckBox digitCheckBox;
    @FXML
    private CheckBox specialCharacterCheckBox;

    @FXML
    private ImageView generateIcon;
    @FXML
    private ImageView copyIcon;

    private String lastValidLength = "14";

    @FXML
    public void initialize() {
        lengthOfPasswordField.setText(lastValidLength);

        lowerCaseCheckBox.setSelected(true);
        upperCaseCheckBox.setSelected(true);
        digitCheckBox.setSelected(true);
        specialCharacterCheckBox.setSelected(true);

        setupAutoResize();

        lengthOfPasswordField.setOnAction(e -> {
            String newLength = lengthOfPasswordField.getText();
            if (newLength.chars().allMatch(Character::isDigit) && !newLength.equals(lastValidLength)) {
                lastValidLength = newLength;
                handleGeneratePasswordAction();
            }
        });

        lengthOfPasswordField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                String newLength = lengthOfPasswordField.getText();
                if (newLength.chars().allMatch(Character::isDigit) && !newLength.equals(lastValidLength)) {
                    lastValidLength = newLength;
                    handleGeneratePasswordAction();
                }
            }
        });

        lowerCaseCheckBox.setOnAction(e -> {
            ensureAtLeastOneOptionSelected();
            handleGeneratePasswordAction();
        });

        upperCaseCheckBox.setOnAction(e -> {
            ensureAtLeastOneOptionSelected();
            handleGeneratePasswordAction();
        });

        digitCheckBox.setOnAction(e -> {
            ensureAtLeastOneOptionSelected();
            handleGeneratePasswordAction();
        });

        specialCharacterCheckBox.setOnAction(e -> {
            ensureAtLeastOneOptionSelected();
            handleGeneratePasswordAction();
        });

        handleGeneratePasswordAction();
    }

    private void setupAutoResize() {
        passwordField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.isEmpty()) {
                passwordField.setPrefHeight(46);
                return;
            }
            Node textNode = passwordField.lookup(".text");
            if (textNode == null) {
                return;
            }

            double newHeight = textNode.getBoundsInLocal().getHeight() + 30;
            passwordField.setPrefHeight(newHeight);
        });
    }

    private void ensureAtLeastOneOptionSelected() {
        if (!lowerCaseCheckBox.isSelected() &&
                !upperCaseCheckBox.isSelected() &&
                !digitCheckBox.isSelected() &&
                !specialCharacterCheckBox.isSelected()) {

            lowerCaseCheckBox.setSelected(true);
            digitCheckBox.setSelected(true);
        }
    }

    @FXML
    public void handleGeneratePasswordAction() {
        String stringLength = lengthOfPasswordField.getText();

        int length = 5;
        if (stringLength.chars().allMatch(Character::isDigit)) {
            length = Math.min(128, Math.max(5, Integer.parseInt(stringLength)));
        }

        String password = PasswordGenerator.generate(
                length,
                lowerCaseCheckBox.isSelected(),
                upperCaseCheckBox.isSelected(),
                digitCheckBox.isSelected(),
                specialCharacterCheckBox.isSelected()
        );
        passwordField.setText(password);
    }

    public void handleCopyAction(){
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent content = new ClipboardContent();

        content.putString(passwordField.getText());
        clipboard.setContent(content);

        System.out.println(" Текст скопирован: " + passwordField.getText());
    }
}
