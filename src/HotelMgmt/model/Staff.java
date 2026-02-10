package HotelMgmt.model;

public class Staff extends User {

    private String staffId;
    private String department;

    public Staff(String id, String name, String email, String password,
                 String role, String status, String staffId, String department) {

        super(id, name, email, password, role, status);
        this.staffId = staffId;
        this.department = department;
    }
}
