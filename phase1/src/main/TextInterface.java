package main;

import java.io.IOException;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import exceptions.UserAlreadyExistsException;
import exceptions.UserNotFoundException;
import users.Permission;
import users.User;

public class TextInterface {

    private static final String filePath = "src/users/users.ser";
    private static final String adminFilePath = "src/users/admin.ser";

    private static final Logger LOGGER = Logger.getLogger(TextInterface.class.getName());
    private static final Handler CONSOLE_HANDLER = new ConsoleHandler();

    public TextInterface() {
        LOGGER.setLevel(Level.ALL);
        CONSOLE_HANDLER.setLevel(Level.WARNING);
        LOGGER.addHandler(CONSOLE_HANDLER);
    }

    public void run() throws IOException {
        Scanner sc = new Scanner(System.in);
        TradeSystem tSystem = new TradeSystem(filePath, adminFilePath);

        String lineBreak = "--------------------------------------------------";

        int userChoice = 0;
        User user = null;
        String userString = "";
        String userPaString = "";

        do {
            System.out.println("Enter a number:\n1. Login\n2. Register");
            try {
                System.out.print("=> ");
                userChoice = sc.nextInt();
                if(userChoice != 1 && userChoice != 2) System.out.println("Invalid Entry, please try again");
            } catch (InputMismatchException e) {
                System.out.println("Invalid Input, please try again");
                sc.nextLine();
            }
        } while (userChoice != 1 && userChoice != 2);

        System.out.println(lineBreak);

        do {
            try {
                System.out.print("Enter" + (userChoice == 2 ? " a new " : " ")  + "Username: ");
                userString = sc.nextLine();
                System.out.print("Enter Password for " + userString + ":");
                userPaString = sc.nextLine();
                user = userChoice == 1 ? tSystem.login(userString, userPaString) : tSystem.register(userString, userPaString);
            } catch(UserNotFoundException | ClassNotFoundException | UserAlreadyExistsException e) {
                System.out.println(e.getMessage());
            }
        } while(user == null);

        System.out.println(lineBreak);
        System.out.printf("Welcome, %s!\n", user.getUsername());
        if(user.isFrozen()) System.out.println("Your account is currently FROZEN");
        System.out.println(lineBreak);
        System.out.println((user.hasPermission(Permission.REGISTER_ADMIN) ? "ADMIN " : "TRADER ") + "MAIN MENU");

        if (user.hasPermission(Permission.REGISTER_ADMIN)) {
            String frozenUser = "";

            do {
                System.out.println(lineBreak);
                System.out.println("1. Freeze Trader");
                System.out.println("2. Un-Freeze Trader");
                System.out.println("3. Add new Administrator");
                System.out.println("4. Add new items to Trader's inventory");
                System.out.println("0. LOG OUT");
                System.out.println();
                try {
                    System.out.print("=> ");
                    userChoice = sc.nextInt();
                } catch (InputMismatchException e) {
                    System.out.println("Invalid Input, please try again");
                    sc.nextLine();
                }
                switch(userChoice) {
                    case 1:
                        System.out.println("Enter the username of the Trader you would like to freeze");
                        System.out.print("=> ");
                        frozenUser = sc.nextLine();
                         // tSystem.freezeUser(frozenUser);
                        System.out.println("Done! User "+ frozenUser + "is now frozen");
                        break;
                    case 2:
                        System.out.println("Enter the username of the frozen Trader");
                        System.out.print("=> ");
                        frozenUser = sc.nextLine();
                        // tSystem.unfreezeUser(frozenUser);
                        System.out.println("Done! User " + frozenUser + "is now unfrozen");
                        break;
                    case 3:
                        System.out.println("What username would you like this admin to have?");
                        System.out.print("=> ");
                        String newAdminUserString = sc.nextLine();
                        System.out.println("What password would you like "+ newAdminUserString +" to have?");
                        System.out.print("=> ");
                        String newAdminPaString = sc.nextLine();
                        // tSystem.makeadmin(newAdminUserString, newAdminPaString);
                        System.out.println("Done! The following user is registered as Admin:\nUsername:\t"+newAdminPaString+"\nPassword:\t" + newAdminPaString);
                        break;
                    case 4:
                        break;
                    case 5:
                        break;
                    default:
                        System.out.println("Invalid Entry , please try again");
                }
            } while (userChoice != 0);
        } else {
            do {
                System.out.println(lineBreak);
                System.out.println("1. View Ongoing trade(s)");
                System.out.println("2. View Inventory");
                System.out.println("3. View Wishlist");
                if (user.isFrozen() && !user.isUnfrozenRequested()) {
                    System.out.println("10. Request Un-Freeze");
                }
                System.out.println("0. LOG OUT");
                System.out.println();
                try {
                    System.out.print("=> ");
                    userChoice = sc.nextInt();
                } catch (InputMismatchException e) {
                    System.out.println("Invalid Input, please try again");
                    sc.nextLine();
                }
                switch (userChoice) {
                    case 1:
                        break;
                    case 2:
                        break;
                    case 3:
                        break;
                    case 10:
                        break;
                    default:
                        System.out.println("Invalid Entry, please try again");
                }
            } while (userChoice != 0);
        }
        sc.close();
    }
}
