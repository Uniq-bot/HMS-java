package HotelMgmt.cli;

import HotelMgmt.cli.customer.CustomerMenu;
import HotelMgmt.model.User;
import HotelMgmt.services.AuthServices;

import java.util.Scanner;

public class LoginMenu {

    public static void loginStart(){
        Scanner sc = new Scanner(System.in);

        System.out.print("Email: ");
        String email = sc.nextLine();

        System.out.print("Password: ");
        String pass = sc.nextLine();

        User user = AuthServices.login(email, pass);

        if (user == null) {
            System.out.println("no such user");
            return;
        }

        if (user.getRole().equals("CUSTOMER")) {
           CustomerMenu.showMenu();
        }
        if(user.getRole().equals("STAFF")){
            System.out.println(user);
        }

    }

}
