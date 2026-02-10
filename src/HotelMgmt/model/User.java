package HotelMgmt.model;

public abstract class User {
    protected String id;
    protected String name;
    protected String email;
    protected String password;
    protected String role;
    protected String status;

    public User(String id, String name, String email, String password, String role, String status) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
        this.status = status;
    }

    public String getRole() {
        return role;
    }
}
