package main;

import java.util.InputMismatchException;
import java.util.Scanner;

import exceptions.UserAlreadyExistsException;
import exceptions.UserNotFoundException;
import users.User;

public class TextInterface {

    private static final String filePath = "src/users/users.ser";
    private static final String adminFilePath = "src/users/admin.ser";

    public static void run() {

        Scanner sc = new Scanner(System.in);
        TradeSystem tSystem = new TradeSystem(filePath, adminFilePath);
        int userChoice = 0;
        User user = null;
        String userString = "";
        String userPaString = "";

        do {
            System.out.println("Enter a number:\n1. Login\n2. Register");
            try {
                userChoice = sc.nextInt();
            } catch (InputMismatchException e) {
                System.out.println("Invalid Entry, please try again");
            }
        } while (userChoice != 1 || userChoice != 2);

        do {
            try {
                System.out.println("Enter" + (userChoice == 2 ? " a new " : " ")  + "Username");
                userString = sc.nextLine();
                System.out.println("Enter Password for " + userString);
                userPaString = sc.nextLine();
                user = userChoice == 1 ? tSystem.login(userString, userPaString) : tSystem.register(userString, userPaString);
            } catch(UserNotFoundException | UserAlreadyExistsException e) {
                System.out.println(e.getMessage());
            }

        } while(user != null);
        System.out.printf("Welcome, %s!\n", user.getUsername());
        sc.close();
    }
}
