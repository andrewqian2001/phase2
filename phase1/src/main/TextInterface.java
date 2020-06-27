package main;

import java.io.IOException;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import exceptions.AuthorizationException;
import exceptions.EntryNotFoundException;
import exceptions.UserAlreadyExistsException;

public class TextInterface {

    private static final Logger LOGGER = Logger.getLogger(TextInterface.class.getName());
    private static final Handler CONSOLE_HANDLER = new ConsoleHandler();

    private static String lineBreak = "--------------------------------------------------";

    private Scanner sc;
    private TradeSystem tSystem;
    private int userChoice;
    private String userID;

    /**
     * Constructor for TextInterface
     *
     * @throws IOException
     */
    public TextInterface() throws IOException {
        LOGGER.setLevel(Level.ALL);
        CONSOLE_HANDLER.setLevel(Level.WARNING);
        LOGGER.addHandler(CONSOLE_HANDLER);

        tSystem = new TradeSystem();
        sc = new Scanner(System.in);
        this.userID = "";
    }

    /**
     * Visual Presenter for the application
     *
     * @throws IOException
     */
    public void run() throws IOException {

        System.out.println("tRaDeMaStEr 9000");
        System.out.println(lineBreak);
        do {
            System.out.println("Enter a number:\n1.\tLogin using an existing account\n2.\tRegister for a new account");
            promptChoice();
        } while (!(userChoice == 1 || userChoice == 2));

        login();

        if (tSystem.checkAdmin(this.userID)) // TO-DO: ADD
            adminMenu();
        else
            traderMenu();

        sc.close();
    }

    private void promptChoice() {
        try {
            System.out.print("=> ");
            this.userChoice = sc.nextInt();
        } catch (InputMismatchException e) {
            System.out.println("Invalid Input, please try again");
            sc.nextLine();
        }
    }

    private void login() {
        String userString = "";
        String userPaString = "";
        do {
            try {
                System.out.print("Enter" + (this.userChoice == 2 ? " a new " : " ") + "Username: ");
                userString = sc.nextLine();
                System.out.print("Enter Password for " + userString + ":");
                userPaString = sc.nextLine();
                this.userID = this.userChoice == 1 ? tSystem.login(userString, userPaString)
                        : tSystem.registerTrader(userString, userPaString);
            } catch (UserAlreadyExistsException | EntryNotFoundException | IOException e) {
                System.out.println(e.getMessage());
            }
        } while (this.userID.equals(""));
    }

    private void adminMenu() {
        System.out.println("ADMIN MAIN MENU");
        do {
            System.out.println(lineBreak);
            System.out.println("1. Freeze Trader");
            System.out.println("2. Un-Freeze Trader");
            System.out.println("3. Add new Administrator");
            System.out.println("0. LOG OUT");
            promptChoice();
            System.out.println(lineBreak);
            switch (this.userChoice) {
                case 1:
                    freeze(true);
                    break;
                case 2:
                    freeze(false);
                    break;
                case 3:
                    addNewAdmin();
                    break;
                default:
                    System.out.println("Invalid Selection, please try again");
            }
        } while (userChoice != 0);
    }

    private void traderMenu() {
        boolean isFrozen;
        System.out.println("TRADER MAIN MENU");
        System.out.println(lineBreak);
        do {
            isFrozen = checkFrozen(this.userID); // TO-DO: ADD
            System.out.println("1. View Trades");
            System.out.println("2. View Inventory");
            System.out.println("3. View Wishlist");
            if (isFrozen)
                System.out.println("10. Request Un-Freeze Account");
            System.out.println("0. LOG OUT");
            promptChoice();
            System.out.println(lineBreak);
            switch (this.userChoice) {
                case 1:
                    tSystem.printTrades(this.userID);
                    break;
                case 2:
                    tSystem.printInventory(this.userID);
                    break;
                case 3:
                    tSystem.printWishlist(this.userID);
                    break;
                case 10:
                    if (isFrozen) {
                        tSystem.requestUnfreeze(this.userID);
                        System.out.println("Done! Now please be patient while an admin un-freezes your account");
                    } else
                        System.out.println("Your account is not frozen!");
                    break;
                default:
                    System.out.println("Invalid Selection, please try again");
            }
        } while (userChoice != 0);
    }

    private void freeze(boolean freezeStatus) {
        String wantToFreeze = "";
        System.out.printf("Enter the username of the Trader you would like to %s\n",
                freezeStatus ? "freeze" : "un-freeze");
        System.out.print("=> ");
        do {
            try {
                wantToFreeze = sc.nextLine();
                tSystem.freezeUser(wantToFreeze, freezeStatus); // TO-DO: change input parameters in tradesystem.java
            } catch (EntryNotFoundException | AuthorizationException e) {
                System.out.println(e.getMessage());
            }
        } while (wantToFreeze.equals(""));
    }

    private void addNewAdmin() {
        String newAdminString = "";
        String newAdminPaString = "";
        boolean success = false;
        do {
            System.out.println("Enter a username for this new Admin");
            System.out.print("=> ");
            newAdminString = sc.nextLine();
            System.out.printf("Enter a password for %s", newAdminString);
            newAdminPaString = sc.nextLine();
            try {
                tSystem.registerAdmin(newAdminString, newAdminPaString);
                success = true;
            } catch (Exception e) {
                success = false;
                System.out.println(e.getMessage());
            }
        } while (!success);
        System.out.println("Done! A new Admin has been created with the following details:");
        System.out.println(lineBreak);
        System.out.printf("Username:\t%s\nPassword:\t%s\n", newAdminString, newAdminPaString);
    }
}