package HotelMgmt.model;

public abstract class User   {
    protected String id;
    protected String name;
    protected String email;
    protected String password;
    protected String status;

    public User(String id, String name, String email,
                String password, String status) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.status = status;
    }

    public abstract String getRole();

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getStatus() {
        return status;
    }

    public boolean isActive() {
        return status.equalsIgnoreCase("ACTIVE");
    }
}
