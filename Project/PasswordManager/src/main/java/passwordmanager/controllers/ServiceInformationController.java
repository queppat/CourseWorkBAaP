package passwordmanager.controllers;

import javafx.application.HostServices;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import passwordmanager.DAO.ServicesDAO;
import passwordmanager.dto.ServiceDTO;
import passwordmanager.utils.*;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;

public class ServiceInformationController {
    @FXML
    private TextField serviceNameField;
    @FXML
    private TextField serviceUsernameField;
    @FXML
    private PasswordField servicePasswordField;
    @FXML
    private TextField visibleServicePasswordField;
    @FXML
    private TextField urlField;
    @FXML
    private Button editButton;

    @FXML
    private ImageView usernameCopyIcon;
    @FXML
    private ImageView passwordCopyIcon;
    @FXML
    private ImageView linkIcon;
    @FXML
    private ImageView backIcon;
    @FXML
    private ImageView deleteIcon;
    @FXML
    private ImageView eyeIcon;

    private ServicesDAO servicesDAO;
    private boolean canBack = true;
    private boolean isPasswordVisible = false;
    private int serviceId;

    UserSession userSession = UserSession.getInstance();

    private final Image openedEyeIcon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/passwordmanager/icons/open-purple-eye.png")));
    private final Image closedEyeIcon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/passwordmanager/icons/close-purple-eye.png")));

    public void setServiceId(int serviceId) {
        this.serviceId = serviceId;
        loadInformation();
    }

    @FXML
    public void initialize() {
        try{
            Connection connection = Database.getConnection();
            servicesDAO = new ServicesDAO(connection);
        } catch (SQLException e) {
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

    private void loadInformation() {
            ServiceDTO selectedService = servicesDAO.getServiceDTOById(serviceId);

        if (selectedService != null) {
            serviceNameField.setText(selectedService.getServiceName());
            serviceUsernameField.setText(selectedService.getUsername());
            urlField.setText(selectedService.getUrl());

            String decryptedPassword = servicesDAO.getDecryptedPassword(serviceId,userSession.getMasterPassword());
            servicePasswordField.setText(decryptedPassword);
            visibleServicePasswordField.setText(decryptedPassword);
        } else {
            AlertUtils.showErrorAlert("Ошибка: сервис с ID " + serviceId + " не найден.");
        }
    }

    @FXML
    public void handleEditServiceAction() {
        try {
            Stage stage = (Stage) editButton.getScene().getWindow();
            WindowManager.mainSwitchScene(stage, "/passwordmanager/fxml/edit_service_information.fxml", "Изменение информации");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleBackAction() {
        try {
            if(canBack) {
                canBack = false;
                Stage stage = (Stage) backIcon.getScene().getWindow();
                WindowManager.slideReplaceWindowFromLeft(stage, "/passwordmanager/fxml/main.fxml", "KeyForge");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleDeleteServiceAction() {
        boolean confirmed = AlertUtils.showConfirmationDialog("Вы действительно хотите удалить этот элемент?");
        if(confirmed) {
            boolean successfulRemove = servicesDAO.deleteServiceById(serviceId);
            if(successfulRemove) {
                try {
                    Stage stage = (Stage) deleteIcon.getScene().getWindow();
                    WindowManager.slideReplaceWindowFromLeft(stage, "/passwordmanager/fxml/main.fxml", "KeyForge");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else {
                AlertUtils.showErrorAlert("Что-то пошло не так при удалении пользователя!");
            }
        }
    }

    @FXML
    public void handleCopyUsernameAction() {
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent content = new ClipboardContent();

        content.putString(serviceUsernameField.getText());
        clipboard.setContent(content);

        System.out.println(" Текст скопирован: " + serviceUsernameField.getText());
    }

    @FXML
    public void handleCopyPasswordAction() {
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent content = new ClipboardContent();

        content.putString(servicePasswordField.getText());
        clipboard.setContent(content);

        System.out.println(" Текст скопирован: " + servicePasswordField.getText());
    }

    @FXML
    public void handleLinkAction() {
        String url = urlField.getText();

        if (!url.startsWith("http")) {
            url = "https://" + url;
        }

        HostServices hostServices = HostServicesProvider.getHostServices();
        hostServices.showDocument(url);
    }

}
