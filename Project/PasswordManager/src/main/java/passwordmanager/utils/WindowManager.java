package passwordmanager.utils;

import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.Screen;
import javafx.util.Duration;
import passwordmanager.controllers.EditServiceInformationController;
import passwordmanager.controllers.ServiceInformationController;

import java.io.IOException;
import java.util.List;

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
        double newY = centerY - newHeight / 2 - 12;

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

        double centerX = (screenWidth - 500) / 2;
        double centerY = (screenHeight - 500) / 2;

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

    public static void slideReplaceWindowFromRight(Stage currentStage, String fxmlPath, String title, Object data) throws IOException {
        Parent previousContent = currentStage.getScene().getRoot();

        FXMLLoader loader = new FXMLLoader(WindowManager.class.getResource(fxmlPath));
        Parent newContent = loader.load();

        Object controller = loader.getController();
        if (controller instanceof ServiceInformationController) {
            ((ServiceInformationController) controller).setServiceId((Integer) data);
        }
        if(controller instanceof EditServiceInformationController) {
            ((EditServiceInformationController) controller).setServiceID((Integer) data);
        }

        List<String> newStyles = newContent.getStylesheets();

        StackPane container = new StackPane();
        double stageWidth = currentStage.getWidth();
        double stageHeight = currentStage.getHeight() - 25;

        Scene tempScene = new Scene(container, stageWidth, stageHeight);

        tempScene.getStylesheets().addAll(newStyles);

        newContent.setTranslateX(currentStage.getWidth());
        container.getChildren().addAll(previousContent, newContent);

        currentStage.setScene(tempScene);

        TranslateTransition slideOut = new TranslateTransition(Duration.millis(400), previousContent);
        slideOut.setFromX(0);
        slideOut.setToX(-currentStage.getWidth());

        TranslateTransition slideIn = new TranslateTransition(Duration.millis(400), newContent);
        slideIn.setFromX(currentStage.getWidth());
        slideIn.setToX(0);

        ParallelTransition parallelTransition = new ParallelTransition(slideOut, slideIn);

        parallelTransition.setOnFinished(e -> {
            container.getChildren().remove(previousContent);
            currentStage.setTitle(title);
        });

        parallelTransition.play();
    }

    public static void slideReplaceWindowFromLeft(Stage currentStage, String fxmlPath, String title, Object data) throws IOException {
        Parent previousContent = currentStage.getScene().getRoot();

        FXMLLoader loader = new FXMLLoader(WindowManager.class.getResource(fxmlPath));
        Parent newContent = loader.load();

        Object controller = loader.getController();
        if (controller instanceof ServiceInformationController) {
            ((ServiceInformationController) controller).setServiceId((Integer) data);
        }
        if(controller instanceof EditServiceInformationController) {
            ((EditServiceInformationController) controller).setServiceID((Integer) data);
        }

        List<String> newStyles = newContent.getStylesheets();

        StackPane container = new StackPane();
        double stageWidth = currentStage.getWidth();
        double stageHeight = currentStage.getHeight() - 25;

        Scene tempScene = new Scene(container, stageWidth, stageHeight);
        tempScene.getStylesheets().addAll(newStyles);

        newContent.setTranslateX(-stageWidth);
        container.getChildren().addAll(previousContent, newContent);

        currentStage.setScene(tempScene);

        TranslateTransition slideOut = new TranslateTransition(Duration.millis(400), previousContent);
        slideOut.setFromX(0);
        slideOut.setToX(stageWidth);

        TranslateTransition slideIn = new TranslateTransition(Duration.millis(400), newContent);
        slideIn.setFromX(-stageWidth);
        slideIn.setToX(0);

        ParallelTransition parallelTransition = new ParallelTransition(slideOut, slideIn);

        parallelTransition.setOnFinished(e -> {
            container.getChildren().remove(previousContent);
            currentStage.setTitle(title);
        });

        parallelTransition.play();
    }

    public static void closeWindow(Node node) {
        Stage stage = (Stage) node.getScene().getWindow();
        stage.close();
    }
}