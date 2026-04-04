package model;

public class Admin {
    private int id;
    private String name;
    private String email;
    private String permissions;

    public Admin() {}

    public Admin(int id, String name, String email, String permissions) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.permissions = permissions;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPermissions() {
        return permissions;
    }

    public void setPermissions(String permissions) {
        this.permissions = permissions;
    }
}
