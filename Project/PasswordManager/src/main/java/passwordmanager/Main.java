package passwordmanager;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import passwordmanager.utils.HostServicesProvider;
import passwordmanager.utils.UserSession;
import passwordmanager.utils.WindowManager;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        HostServicesProvider.setHostServices(getHostServices());

        primaryStage.getIcons().add(new Image("passwordmanager/icons/AppIcon.png"));
        WindowManager.defaultSwitchScene(primaryStage, "/passwordmanager/fxml/auth.fxml", "KeyForge");
        primaryStage.setResizable(false);

        primaryStage.setOnCloseRequest(e -> {
            UserSession.getInstance().shutdown();
            Platform.exit();
        });

        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}