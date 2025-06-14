package passwordmanager.utils;

import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.util.concurrent.*;

public class UserSession {
    private static final UserSession instance = new UserSession();
    private ScheduledExecutorService scheduler;
    private ScheduledFuture<?> logoutTask;
    private static final int INACTIVITY_TIMEOUT_MINUTES = 3;

    private int userId = -1;
    private String masterPassword;

    private int serviceId;
    private String serviceName;
    private String username;
    private String url;
    private String generatedPassword;
    private String fxmlFilePath;
    private String title;

    private final EventHandler<Event> activityHandler;
    private boolean isTrackingActive = false;

    private UserSession() {
        this.scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "UserSession-Inactivity-Timer");
            t.setDaemon(true);
            return t;
        });

        this.activityHandler = event -> {
            if (event.getEventType() != WindowEvent.WINDOW_SHOWING &&
                    event.getEventType() != WindowEvent.WINDOW_HIDING) {
                resetInactivityTimer();
            }
        };
    }

    public static UserSession getInstance() {
        return instance;
    }

    public void login(int userId, String masterPassword) {
        this.userId = userId;
        this.masterPassword = masterPassword;

        this.serviceId = -1;
        this.serviceName = null;
        this.username =null;
        this.url = null;
        this.generatedPassword = null;
        this.fxmlFilePath = null;
        this.title = null;

        startActivityTracking();
        resetInactivityTimer();
    }

    private void startActivityTracking() {
        if (isTrackingActive) return;

        for (Window window : Window.getWindows()) {
            if (window instanceof Stage) {
                setupStageTracking((Stage) window);
            }
        }

        Window.getWindows().addListener((ListChangeListener<Window>) change -> {
            while (change.next()) {
                if (change.wasAdded()) {
                    for (Window window : change.getAddedSubList()) {
                        if (window instanceof Stage) {
                            setupStageTracking((Stage) window);
                        }
                    }
                }
            }
        });

        isTrackingActive = true;
    }

    public void deleteServiceInfo(){
        serviceId = -1;
        serviceName = null;
        username = null;
        url = null;
        generatedPassword = null;
        fxmlFilePath = null;
        title = null;
    }

    private void setupStageTracking(Stage stage) {
        stage.addEventHandler(Event.ANY, activityHandler);

        stage.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (oldScene != null) {
                removeSceneHandlers(oldScene);
            }
            if (newScene != null) {
                addSceneHandlers(newScene);
            }
        });

        stage.setOnCloseRequest(event -> {
            if (Window.getWindows().stream().noneMatch(Window::isShowing)) {
                shutdown();
            }
        });

        if (stage.getScene() != null) {
            addSceneHandlers(stage.getScene());
        }
    }

    private void addSceneHandlers(Scene scene) {
        scene.addEventHandler(Event.ANY, activityHandler);
        if (scene.getRoot() != null) {
            addNodeHandlers(scene.getRoot());
        }
    }

    private void removeSceneHandlers(Scene scene) {
        scene.removeEventHandler(Event.ANY, activityHandler);
        if (scene.getRoot() != null) {
            removeNodeHandlers(scene.getRoot());
        }
    }

    private void addNodeHandlers(Node node) {
        node.addEventHandler(Event.ANY, activityHandler);
        if (node instanceof Parent) {
            for (Node child : ((Parent) node).getChildrenUnmodifiable()) {
                addNodeHandlers(child);
            }
        }
    }

    private void removeNodeHandlers(Node node) {
        node.removeEventHandler(Event.ANY, activityHandler);
        if (node instanceof Parent) {
            for (Node child : ((Parent) node).getChildrenUnmodifiable()) {
                removeNodeHandlers(child);
            }
        }
    }

    private void resetInactivityTimer() {
        if (logoutTask != null) {
            logoutTask.cancel(false);
        }
        logoutTask = scheduler.schedule(this::logout, INACTIVITY_TIMEOUT_MINUTES, TimeUnit.MINUTES);
    }

    public void logout() {
        if (userId == -1) return;

        masterPassword = null;
        userId = -1;
        serviceId = 0;
        serviceName = null;
        username = null;
        generatedPassword = null;
        url = null;
        fxmlFilePath = null;
        title = null;
        System.out.println("Сессия завершена по таймауту бездействия");

        Platform.runLater(() -> {
            AlertUtils.showWarningAlert("Ваша сессия завершена из-за неактивности!");
            try {
                WindowManager.openNewWindow("/passwordmanager/fxml/auth.fxml", "KeyForge", 500, 500, false);
                closeAllWindows();
            } catch (IOException e) {
                System.err.println("Ошибка при открытии окна авторизации: " + e.getMessage());
            }
        });

        cancelLogoutTask();
    }

    public void quietLogout() {
        if (userId == -1) return;

        masterPassword = null;
        userId = -1;
        serviceId = 0;
        serviceName = null;
        username = null;
        generatedPassword = null;
        url = null;
        fxmlFilePath = null;
        title = null;
        System.out.println("Сессия завершена (тихий режим)");

        Platform.runLater(() -> {
            try {
                WindowManager.openNewWindow("/passwordmanager/fxml/auth.fxml", "KeyForge", 500, 500, false);
                closeAllWindows();
            } catch (IOException e) {
                System.err.println("Ошибка при открытии окна авторизации: " + e.getMessage());
            }
        });

        cancelLogoutTask();
    }

    private void closeAllWindows() {
        for (Window window : Window.getWindows()) {
            if (window.isShowing()) {
                window.hide();
            }
        }
    }

    private void cancelLogoutTask() {
        if (logoutTask != null) {
            logoutTask.cancel(false);
            logoutTask = null;
        }
    }

    public void shutdown() {
        cancelLogoutTask();
        scheduler.shutdownNow();
        try {
            if (!scheduler.awaitTermination(1, TimeUnit.SECONDS)) {
                System.out.println("Таймер бездействия не завершился вовремя");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        isTrackingActive = false;
    }

    public void setUserId(int userId) {this.userId = userId;}

    public int getServiceId() {return serviceId;}

    public void setServiceId(int serviceId) {this.serviceId = serviceId;}

    public String getServiceName() {return serviceName;}

    public void setServiceName(String serviceName) {this.serviceName = serviceName;}

    public String getUsername() {return username;}

    public void setUsername(String username) {this.username = username;}

    public String getUrl() {return url;}

    public void setUrl(String url) {this.url = url;}

    public String getFxmlFilePath() {return fxmlFilePath;}

    public void setFxmlFilePath(String fxmlFilePath) {this.fxmlFilePath = fxmlFilePath;}

    public String getTitle() {return title;}

    public void setTitle(String title) {this.title = title;}

    public void setGeneratedPassword(String password) {
        this.generatedPassword = password;
    }

    public String getGeneratedPassword() {return generatedPassword;}

    public void clearGeneratedPassword() {
        this.generatedPassword = null;
    }

    public int getUserId() {
        return userId;
    }

    public String getMasterPassword() {
        return masterPassword;
    }
}