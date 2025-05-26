package passwordmanager.utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.Screen;

import java.io.IOException;

public class WindowManager {
    public static void defaultSwitchScene(Stage stage, String fxmlPath, String title) throws IOException {
        FXMLLoader loader = new FXMLLoader(WindowManager.class.getResource(fxmlPath));
        Parent root = loader.load();

        Scene scene = new Scene(root, 500, 500);
        stage.setScene(scene);
        stage.setTitle(title);
        stage.setResizable(false);

        if (!stage.isShowing()) {
            centerStageOnScreen(stage);
        } else {
            stage.sizeToScene();
        }
    }

    public static void mainSwitchScene(Stage stage, String fxmlPath, String title) throws IOException {
        FXMLLoader loader = new FXMLLoader(WindowManager.class.getResource(fxmlPath));
        Parent root = loader.load();

        double centerX = stage.getX() + stage.getWidth() / 2;
        double centerY = stage.getY() + stage.getHeight() / 2;

        double newWidth = 550;
        double newHeight = 600;

        double newX = centerX - newWidth / 2;
        double newY = centerY - newHeight / 2;

        Scene scene = new Scene(root, newWidth, newHeight);
        stage.setScene(scene);
        stage.setTitle(title);
        stage.setResizable(true);
        stage.setMinWidth(300);
        stage.setMinHeight(400);
        stage.setResizable(false);

        stage.setX(newX);
        stage.setY(newY);
    }


    private static void centerStageOnScreen(Stage stage) {
        Screen screen = Screen.getPrimary();
        double screenWidth = screen.getBounds().getWidth();
        double screenHeight = screen.getBounds().getHeight();

        double centerX = (screenWidth - 500) / 2;  // 500 - ширина окна
        double centerY = (screenHeight - 500) / 2; // 500 - высота окна

        stage.setX(centerX);
        stage.setY(centerY);
    }

    public static Stage openNewWindow(String fxmlPath, String title,
                                      double width, double height,
                                      boolean resizable) throws IOException {
        Stage newStage = new Stage();
        FXMLLoader loader = new FXMLLoader(WindowManager.class.getResource(fxmlPath));
        Parent root = loader.load();

        Scene scene = new Scene(root, width, height);
        newStage.setScene(scene);
        newStage.setTitle(title);
        newStage.setResizable(resizable);

        try {
            Image appIcon = new Image(WindowManager.class.getResourceAsStream("/passwordmanager/icons/AppIcon.png"));
            newStage.getIcons().add(appIcon);
        } catch (Exception e) {
            System.err.println("Не удалось загрузить иконку: " + e.getMessage());
        }

        Screen screen = Screen.getPrimary();
        double screenWidth = screen.getBounds().getWidth();
        double screenHeight = screen.getBounds().getHeight();

        double centerX = (screenWidth - width) / 2;
        double centerY = (screenHeight - height) / 2;

        newStage.setX(centerX);
        newStage.setY(centerY);

        newStage.show();
        return newStage;
    }

    public static void closeWindow(Node node) {
        Stage stage = (Stage) node.getScene().getWindow();
        stage.close();
    }
}