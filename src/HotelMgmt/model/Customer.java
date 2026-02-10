package HotelMgmt.model;

public class Customer extends User {

        public Customer(String id, String name, String email,
                        String password, String status){
            super(id, name, email, password, status);
        }

        @Override
        public String getRole(){
            return "CUSTOMER";
        }

}
