package passwordmanager.utils;

public class UserSession {
    private static final UserSession instance = new UserSession();

    private UserSession() {
    }

    private int userId;
    private String masterPassword;

    public static UserSession getInstance() {
        return instance;
    }

    public void login(int userId, String masterPassword) {
        this.userId = userId;
        this.masterPassword = masterPassword;
    }

    public void logout() {
        masterPassword = null;
        this.userId = -1;
    }

    public int getUserId() {
        return userId;
    }

    public String getMasterPassword() {
        return masterPassword;
    }

}
