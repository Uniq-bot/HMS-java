package HotelMgmt.model;

public abstract class Staff extends User {

    protected String staffId;
    protected String department;

    public Staff(String id, String name, String email,
                 String password, String status,
                 String staffId, String department) {

        super(id, name, email, password, status);
        this.staffId = staffId;
        this.department = department;
    }
}
