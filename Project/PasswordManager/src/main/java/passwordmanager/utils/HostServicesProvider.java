package passwordmanager.utils;
import javafx.application.HostServices;

public class HostServicesProvider {
    private static HostServices hostServices;

    public static void setHostServices(HostServices services) {
        hostServices = services;
    }

    public static HostServices getHostServices() {
        return hostServices;
    }
}