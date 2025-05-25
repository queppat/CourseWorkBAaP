package passwordmanager.models;

import passwordmanager.utils.PasswordHasher;

public class SuperUser {
    private int id;
    private String username;
    private String masterPasswordHash;
    private String salt;

    public SuperUser(String username, String masterPassword) {
        this.username = username;
        if(masterPassword != null && !masterPassword.isEmpty()) {
            this.salt = PasswordHasher.generateSalt();
            this.masterPasswordHash = PasswordHasher.hashPassword(masterPassword, salt);
        }
    }

    public int getId() { return id; }

    public void setId(int id) {this.id = id; }

    public String getSalt(){
        return salt;
    }

    public void setSalt(String salt){
        this.salt = salt;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getMasterPasswordHash() {
        return masterPasswordHash;
    }

    public void setMasterPasswordHash(String password) {
        this.masterPasswordHash = password;
    }

    public boolean verifyMasterPassword(String inputPassword) {
        return PasswordHasher.verifyPassword(inputPassword, salt, masterPasswordHash);
    }
}
