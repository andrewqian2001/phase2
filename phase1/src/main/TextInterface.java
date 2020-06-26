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

    private static final Logger LOGGER = Logger.getLogger(TextInterface.class.getName());
    private static final Handler CONSOLE_HANDLER = new ConsoleHandler();

    /**
     * Constructor for TextInterface
     *
     * @throws IOException
     */
    public TextInterface() {
        LOGGER.setLevel(Level.ALL);
        CONSOLE_HANDLER.setLevel(Level.WARNING);
        LOGGER.addHandler(CONSOLE_HANDLER);
    }

    /**
     * Visual Presenter for the application
     *
     * @throws IOException
     */
    public void run() throws IOException, ClassNotFoundException {
        Scanner sc = new Scanner(System.in);
        TradeSystem tSystem = new TradeSystem();

        String lineBreak = "--------------------------------------------------";

        int userChoice = 0;
        
        String userID = "";
        String userString = "";
        String userPaString = "";

        User loggedInUser = null;

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
                userID = userChoice == 1 ? tSystem.login(userString, userPaString) : tSystem.register(userString, userPaString);
            } catch(UserNotFoundException | ClassNotFoundException | UserAlreadyExistsException e) {
                System.out.println(e.getMessage());
            }
        } while(userID.equals(""));

        // Just for getting properties of the user (isFrozen, hasPermission, etc.)
        loggedInUser = tSystem.getLoggedInUser(userID);

        System.out.println(lineBreak);
        System.out.printf("Welcome, %s!\n", userString);
        if(loggedInUser.isFrozen()) System.out.println("Your account is currently FROZEN");
        System.out.println(lineBreak);
        System.out.println((loggedInUser.hasPermission(Permission.REGISTER_ADMIN) ? "ADMIN " : "TRADER ") + "MAIN MENU");

        if (loggedInUser.hasPermission(Permission.REGISTER_ADMIN)) {
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
                         tSystem.freezeUser(frozenUser);
                        System.out.println("Done! User "+ frozenUser + "is now frozen");
                        break;
                    case 2:
                        System.out.println("Enter the username of the frozen Trader");
                        System.out.print("=> ");
                        frozenUser = sc.nextLine();
                        tSystem.unfreezeUser(frozenUser);
                        System.out.println("Done! User " + frozenUser + "is now unfrozen");
                        break;
                    case 3:
                        System.out.println("What username would you like this admin to have?");
                        System.out.print("=> ");
                        String newAdminUserString = sc.nextLine();
                        System.out.println("What password would you like "+ newAdminUserString +" to have?");
                        System.out.print("=> ");
                        String newAdminPaString = sc.nextLine();
                        try {
                            tSystem.registerAdmin(newAdminUserString, newAdminPaString);
                        } catch (UserAlreadyExistsException | ClassNotFoundException e) {
                            System.out.println(e.getMessage());
                        }
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
                if (loggedInUser.isFrozen() && !loggedInUser.isUnfrozenRequested()) {
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
                        tSystem.printInventory(userID);
                        break;
                    case 3:
                        tSystem.printWishlist(userID);
                        break;
                    case 10:
                        tSystem.requestUnfreeze(userID);
                        break;
                    default:
                        System.out.println("Invalid Entry, please try again");
                }
            } while (userChoice != 0);
        }
        sc.close();
    }
}
