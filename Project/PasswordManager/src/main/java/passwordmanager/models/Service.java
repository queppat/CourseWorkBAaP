package passwordmanager.models;

public class Service {
    private int userId;
    private String serviceName;
    private String username;
    private String encryptedPassword;
    private String URL;
    private String salt;

    public Service(int userId, String serviceName, String username, String encryptedPassword, String URL, String salt) {
        this.userId = userId;
        this.serviceName = serviceName;
        this.username = username;
        this.encryptedPassword = encryptedPassword;
        this.URL = URL;
        this.salt = salt;
    }

    public int getUserId() { return userId; }

    public void setUserId(int userId) { this.userId = userId; }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEncryptedPassword() {
        return encryptedPassword;
    }

    public void setEncryptedPassword(String encryptedPassword) {
        this.encryptedPassword = encryptedPassword;
    }

    public String getURL() {return URL;}

    public void setURL(String URL) {this.URL = URL;}

    public String getSalt() {return salt;}

    public void setSalt(String salt) {this.salt = salt;}
}
