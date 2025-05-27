package passwordmanager.dto;

public class ServiceDTO {
    private final int id;
    private final String serviceName;
    private final String username;
    private final String password;
    private final String url;

    public ServiceDTO(int id, String serviceName, String username, String password, String url) {
        this.id = id;
        this.serviceName = serviceName;
        this.username = username;
        this.password = password;
        this.url = url;
    }

    public int getId() {
        return id;
    }

    public String getServiceName() {
        return serviceName;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getUrl() {return url;}
}