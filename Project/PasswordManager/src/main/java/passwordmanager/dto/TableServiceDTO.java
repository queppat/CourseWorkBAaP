package passwordmanager.dto;

public class TableServiceDTO {
    private final int id;
    private final String serviceName;
    private final String username;

    public TableServiceDTO(int id,String serviceName, String username) {
        this.id = id;
        this.serviceName = serviceName;
        this.username = username;
    }

    public int getId() {return id;}

    public String getServiceName() {return serviceName;}

    public String getUsername() {return username;}
}
