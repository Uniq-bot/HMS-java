package HotelMgmt.cli.customer;

import HotelMgmt.cli.LoginMenu;

import java.util.Scanner;

public class CustomerMenu {

    private static Scanner sc = new Scanner(System.in);

    public static void showMenu() {

        while (true) {
            System.out.println("\n=== CUSTOMER MENU ===");
            System.out.println("1. Booking Menu");
            System.out.println("2. Issue Date");
            System.out.println("3. Logout");
            System.out.print("Enter your choice: ");

            String choice = sc.nextLine();

            switch (choice) {
                case "1":
                    BookingCli.BookingMenu();
                    break;
                case "2":
                    bookRoom();
                    break;
                case "3":
                    System.out.println("Logging out...");
                    LoginMenu.loginStart(); // exit menu
                default:
                    System.out.println("Invalid choice. Try again.");
            }
        }
    }

    private static void viewProfile() {
        System.out.println("Profile details will be shown here.");
    }

    private static void bookRoom() {
        System.out.println("Booking feature coming soon...");
    }
}
