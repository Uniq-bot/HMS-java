package HotelMgmt.services;

import HotelMgmt.model.*;
import HotelMgmt.util.FileUtil;

import java.util.List;

public class AuthServices {

    public static User login(String email, String password) {

        List<String> users = FileUtil.read(
"src/HotelMgmt/data/users"        );

        for (String line : users) {

            String[] d = line.split(",");

            // clean spaces
            for (int i = 0; i < d.length; i++) {
                d[i] = d[i].trim();
            }

            if (d[2].equals(email) && d[3].equals(password)) {

                String role = d[4];

                if (role.equalsIgnoreCase("CUSTOMER")) {
                    return new Customer(d[0], d[1], d[2], d[3], d[4], d[5]);
                }

                if (role.equalsIgnoreCase("STAFF")) {
                    return new Staff(
                            d[0], d[1], d[2], d[3], d[4], d[5], d[6], d[7]
                    );
                }
            }
        }

        return null;
    }
}
