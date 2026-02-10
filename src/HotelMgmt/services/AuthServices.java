package HotelMgmt.services;

import HotelMgmt.model.Customer;
import HotelMgmt.model.Staff;
import HotelMgmt.model.User;
import HotelMgmt.util.FileUtil;

import java.util.List;

public class AuthServices {

    public static User login(String email, String password) {

        List<String> users = FileUtil.read("data/users");

        for (String line : users) {
            String[] d = line.split(",");

            if (d[2].equals(email) && d[3].equals(password)) {

                String role = d[5];

                if (role.equalsIgnoreCase("CUSTOMER")) {
                    return new Customer(d[0], d[1], d[2], d[3], d[4]);
                }

                if (role.equalsIgnoreCase("STAFF")) {
                    return new Staff(
                            d[0], // id
                            d[1], // name
                            d[2], // email
                            d[3], // password
                            d[4], // status
                            d[6], // staffId
                            d[7]  // department
                    ) {
                        @Override
                        public String getRole() {
                            return "";
                        }
                    };
                }

            }
        }

        return null; // login failed
    }
}
