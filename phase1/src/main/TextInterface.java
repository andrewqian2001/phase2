package main;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Set;
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
     * Constructor for TextInterface - Initializes TradeSystem, Scanner and UserID
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
        boolean isAdmin = false;
        System.out.println("tRaDeMaStEr 9000");
        System.out.println(lineBreak);
        do {
            System.out.println(
                    "Enter a number:\n1.\tLogin using an existing account\n2.\tRegister for a new account\n0.\tEXIT\n");
            promptChoice();
        } while (!(userChoice == 1 || userChoice == 2 || userChoice == 0));

        if (userChoice != 0) {
            login();

            // since this valid userID would be returned from a logged in user...
            // the exception will never be thrown
            try {
                isAdmin = tSystem.checkAdmin(this.userID);
            } catch (EntryNotFoundException e) {
                System.out.println(e.getMessage());
            }

            if (isAdmin)
                adminMenu();
            else
                traderMenu();
        }
        logOut();
    }

    /**
     * Prompts user for an integer, and sets userChoice to it. Handles any incorrect
     * input
     */
    private void promptChoice() {
        try {
            System.out.print("=> ");
            this.userChoice = Integer.parseInt(sc.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid Input, please try again");
        }
    }

    /**
     * Login Helper Method - Displays prompts to login, and sets the userID to the
     * loggedInUser's ID Also handles registering a user
     */
    private void login() {
        String userString = "";
        String userPaString = "";
        do {
            try {
                System.out.println("Enter" + (this.userChoice == 2 ? " a new " : " ") + "Username:");
                System.out.print("=> ");
                userString = sc.nextLine();
                System.out.println("Enter Password for " + userString + ":");
                System.out.print("=> ");
                userPaString = sc.nextLine();
                this.userID = this.userChoice == 1 ? tSystem.login(userString, userPaString)
                        : tSystem.registerTrader(userString, userPaString);
            } catch (UserAlreadyExistsException | EntryNotFoundException | IOException e) {
                System.out.println(e.getMessage());
            }
        } while (this.userID.equals(""));
    }

    /**
     * Log-Out Helper Method - Clears the userID and Closes the scanner
     */
    private void logOut() {
        System.out.println("Thank you for using tRaDeMaStEr 9000!");
        System.out.printf("ID='%s' Log-Out Success\n", this.userID);
        this.userID = "";
        sc.close();
    }

    /**
     * Admin Main Menu Helper Method - Displays choices for Admin to select from,
     * and executes said selection
     */
    private void adminMenu() {
        System.out.println("ADMIN MAIN MENU");
        do {
            System.out.println(lineBreak);
            System.out.println("1.\tFreeze Trader");
            System.out.println("2.\tUn-Freeze Trader");
            System.out.println("3.\tAdd new Administrator");
            System.out.println("4.\tView all Un-freeze Requests");
            System.out.println("5.\tView all Item Requests");
            System.out.println("6.\tApprove Item Request");
            System.out.println("7.\tReject Item Request");
            System.out.println("8.\tChange Weekly Trade Limit Value");
            System.out.println("0.\tLOG OUT");
            promptChoice();
            System.out.println(lineBreak);
            switch (this.userChoice) {
                case 0:
                    System.out.println("Logging Out...");
                    break;
                case 1:
                    freeze(true);
                    break;
                case 2:
                    freeze(false);
                    break;
                case 3:
                    addNewAdmin();
                    break;
                case 4:
                    printAllUnfreezeRequests();
                    break;
                case 5:
                    printAllItemRequests();
                    break;
                case 6:
                    processItemRequest(true);
                    break;
                case 7:
                    processItemRequest(false);
                    break;
                case 8:
                    changeWeeklyTradeLimit();
                    break;
                default:
                    System.out.println("Invalid Selection, please try again");
            }
        } while (userChoice != 0);
    }

    /**
     * Trader Main Menu Helper Method - Displays choices for Trader to select from,
     * and executes said selection
     */
    private void traderMenu() {
        boolean isFrozen = false;
        System.out.println("TRADER MAIN MENU");
        System.out.println(lineBreak);
        do {
            try {
                // since this valid userID would be returned from a logged in user...
                // the exception will never be thrown
                isFrozen = tSystem.checkFrozen(this.userID);
            } catch (EntryNotFoundException e) {
                System.out.println(e.getMessage());
            }
            System.out.println("1.\tView Trades");
            System.out.println("2.\tView Inventory");
            System.out.println("3.\tView Wishlist");
            System.out.println("4.\tView Frequent Traders");
            System.out.println("5.\tView Recent Trade Items");
            System.out.println("6.\tView All Items in Database");
            System.out.println("7.\tRequest to add item to Inventory");
            System.out.println("8.\tAdd item to Wishlist");
            if (!isFrozen) {
                System.out.println("9.\tBorrow an Item from a Trader");
                System.out.println("10.\tLend an Item to a Trader");
                System.out.println("11.\tTrade with a trader");
            }
            if (isFrozen)
                System.out.println("12.\tRequest Un-Freeze Account");
            System.out.println("0.\tLOG OUT");
            promptChoice();
            System.out.println(lineBreak);
            switch (this.userChoice) {
                case 0:
                    System.out.println("Logging Out...");
                    break;
                case 1:
                    printTrades();
                    break;
                case 2:
                    printInventory();
                    break;
                case 3:
                    printWishlist();
                    break;
                case 4:
                    viewFreqTraders();
                    break;
                case 5:
                    viewRecentTradeItems();
                    break;
                case 6:
                    printDatabase();
                    break;
                case 7:
                    requestItem();
                    break;
                case 8:
                    addItemToWishList();
                    break;
                case 9:
                    if (!isFrozen)
                        borrowItem();
                    else
                        System.out.println("Invalid Selection, please try again");
                    break;
                case 10:
                    if (!isFrozen)
                        lendItem();
                    else
                        System.out.println("Invalid Selection, please try again");
                    break;
                case 11:
                    if (!isFrozen)
                        tradeItems();
                    else
                        System.out.println("Invalid Selection, please try again");
                    break;
                case 12:
                    if (isFrozen) {
                        requestUnFreeze();
                    } else
                        System.out.println("Invalid Selection, please try again");
                    break;
                default:
                    System.out.println("Invalid Selection, please try again");
            }
        } while (userChoice != 0);
    }

    /**
     * Freeze Helper Method Prompts the Admin to enter the username of the Trader to
     * (un-)freeze And (un-)freezes the inputted Trader
     * 
     * @param freezeStatus if true, the method will freeze the given Trader, else it
     *                     will un-freeze
     */
    private void freeze(boolean freezeStatus) {
        String wantToFreeze = "";
        boolean success = false;
        System.out.printf("Enter the username of the Trader you would like to %s\n",
                freezeStatus ? "freeze" : "un-freeze");
        System.out.print("=> ");
        do {
            try {
                wantToFreeze = sc.nextLine();
                tSystem.freezeUser(wantToFreeze, freezeStatus);
                success = true;
            } catch (EntryNotFoundException | AuthorizationException e) {
                success = false;
                System.out.println(e.getMessage());
            }
        } while (!success);
        System.out.printf("Done! Trader \"%s\" has now been %s\n", wantToFreeze, freezeStatus ? "un-frozen" : "frozen");
    }

    /**
     * Helper Method to create a new Admin account Prompts the Admin to enter a
     * username and password for a new Admin account And will then register a new
     * Admin Account
     */
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
            } catch (UserAlreadyExistsException | IOException e) {
                success = false;
                System.out.println(e.getMessage());
            }
        } while (!success);
        System.out.println("Done! A new Admin has been created with the following details:");
        System.out.println(lineBreak);
        System.out.printf("Username:\t%s\nPassword:\t%s\n", newAdminString, newAdminPaString);
    }

    /**
     * Prints the Trader's Trades given their ID NOTE: This method will not be
     * called by an Admin ever
     * 
     */
    private void printTrades() {
        printList(this.userID, "Accepted", "Trade");
        System.out.println();
        printList(this.userID, "Requested", "Trade");
    }

    private void printDatabase() {
        ArrayList<String> allTraders = tSystem.getAllTraders();
        for (String userID : allTraders) {
            printList(userID, "Inventory", "Item");
        }
    }

    /**
     * Prints the Trader's Inventory given their ID NOTE: This method will not be
     * called by an Admin ever
     * 
     */
    private void printInventory() {
        printList(this.userID, "Inventory", "Item");
    }

    /**
     * Prints the Trader's WishList given their ID NOTE: This method will not be
     * called by an Admin ever
     * 
     */
    private void printWishlist() {
        printList(this.userID, "Wishlist", "Item");
    }

    /**
     * Prints a list given the Trader's ID, Type of List, and Item type NOTE: This
     * is method is not called by an Admin ever NOTE: This method is just a helper
     * for the other print__ methods
     * 
     * @param listType
     * @param itemType
     */
    private void printList(String ID, String listType, String itemType) {
        try {
            ArrayList<String> list = null;
            String itemID = "";
            if (listType.equals("Wishlist"))
                list = tSystem.getWishlist(ID);
            else if (listType.equals("Inventory"))
                list = tSystem.getAvailableItems(ID);
            else if (listType.equals("Accepted"))
                list = tSystem.getAcceptedTrades(ID);
            else if (listType.equals("Requested"))
                list = tSystem.getRequestedTrades(ID);
            System.out.printf("%s's %s %ss\n***************\n", tSystem.getUsername(ID), listType, itemType);
            for (int i = 0; i < list.size(); i++) {
                itemID = list.get(i);
                System.out.printf("%s %s #%d: %s\n\t%s\n", listType, itemType, i, tSystem.getTradableItemName(itemID),
                        tSystem.getTradableItemDesc(itemID));
            }
        } catch (EntryNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Prints all Traders that have requested an Unfreeze on their account
     */
    private void printAllUnfreezeRequests() {
        System.out.println("*** Un-Freeze Requesters ***");
        try {
            ArrayList<String> unFreezeRequests = tSystem.getAllUnfreezeRequests();
            for (String userID : unFreezeRequests) {
                System.out.println(tSystem.getUsername(userID));
            }
        } catch (EntryNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Prints all Item Requests for all Traders
     */
    private void printAllItemRequests() {
        System.out.println("*** Item Requests ***");
        try {
            HashMap<String, ArrayList<String>> itemRequests = tSystem.getAllItemRequests();
            for (String userID : itemRequests.keySet()) {
                if (itemRequests.get(userID).size() > 0) {
                    System.out.println(tSystem.getUsername(userID) + " :");
                    for (String itemID : itemRequests.get(userID)) {
                        System.out.printf("\t%s\n", tSystem.getTradableItemName(itemID));
                    }
                }
            }
        } catch (EntryNotFoundException e) {

        }
    }

    /**
     * Prompts user to enter item to be requested
     */
    private void requestItem() {
        String itemName = "";
        String itemDesc = "";
        boolean success = false;
        do {
            System.out.println("Enter the name of an item you request to store");
            System.out.println("This item must be reasonable (i.e. not the North Pole)");
            System.out.print("=> ");
            itemName = sc.nextLine();
            System.out.println("Enter a short description for " + itemName);
            System.out.print("=> ");
            itemDesc = sc.nextLine();
            if (!itemName.trim().equals("")) {
                try {
                    tSystem.requestItem(this.userID, itemName, itemDesc);
                    success = true;
                } catch (EntryNotFoundException e) {
                    System.out.println(e.getMessage());
                    success = false;
                }
            } else {
                System.out.println("I'm sorry but each item must have a name, please try again");
            }
        } while (!success);
        System.out.printf("Done! Your request to add %s has now been processed.", itemName);
        System.out.println("Please be patient while an Admin approves or rejects your request.");
    }

    /**
     * Prompts user to enter the item name and adds the item to the users Wishlist
     */
    private void addItemToWishList() {
        String itemName = "";
        boolean success = false;
        do {
            System.out.println("Enter the name of an item you want to add to your Wishlist");
            System.out.print("=> ");
            itemName = sc.nextLine();
            try {
                tSystem.addToWishList(this.userID, itemName);
                success = true;
            } catch (Exception e) {
                System.out.println(e.getMessage());
                success = false;
            }
        } while (!success);
        System.out.printf("Done! %s is now in your Wishlist.", itemName);
    }

    /**
     * Prompts admin to accept or reject an item request given a Trader Username and
     * Item Name
     * 
     * @param isAccepted if true then item is accepted, else it is rejected
     */
    private void processItemRequest(boolean isAccepted) {
        String traderName = "";
        String itemName = "";
        boolean success = false;
        do {
            try {
                System.out.println("Enter the name of the Trader");
                System.out.print("=> ");
                traderName = sc.nextLine();
                System.out.println("Enter the name of the Item");
                itemName = sc.nextLine();
                tSystem.processItemRequest(traderName, itemName, isAccepted);
            } catch (EntryNotFoundException e) {
                System.out.println(e.getMessage());
                success = false;
            }
        } while (!success);
        System.out.printf("Done! %s's request for item '%s' has now been %s\n", traderName, itemName,
                isAccepted ? "accepted" : "rejected");
    }

    /**
     * Prints recent Traded items
     */
    private void viewRecentTradeItems() {
        try {
            Set<String> recentTradeItems = tSystem.getRecentTradeItems(this.userID);
            for (String itemName : recentTradeItems) {
                System.out.println("Item: " + itemName);
            }
        } catch (EntryNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Prints the 3 most traded with Traders
     */
    private void viewFreqTraders() {
        try {
            String[] freqTraders = tSystem.getFrequentTraders(this.userID);
            for (int i = 0; i < freqTraders.length; i++) {
                System.out.printf("Trader #%d: %s", i + 1, freqTraders[i]);
            }
        } catch (EntryNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Prompts admin to change the current trade limit
     */
    private void changeWeeklyTradeLimit() {
        int tradeLimit = 0;
        try {
            tradeLimit = tSystem.getCurrentTradeLimit();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            tradeLimit = 0;
        }
        boolean success = false;
        if(tradeLimit == 0) System.out.println("There are no Traders in the System!");
        else System.out.println("The current weekly trade limit is " + tradeLimit);
        do {
            try {
                System.out.println("Enter the new value for the trade limit");
                System.out.print("=> ");
                tradeLimit = Integer.parseInt(sc.nextLine());
                tSystem.setTradeLimit(tradeLimit);
                success = true;
            } catch (NumberFormatException | EntryNotFoundException e) {
                success = false;
                System.out.println(e.getMessage());
            }
        } while (!success);
        System.out.println("Done! The new weekly trade limit is now" + tradeLimit);
    }

    /**
     * Requests the logged in trader to be unfrozen by an admin REQUIREMENT:
     * isFrozen == true
     */
    private void requestUnFreeze() {
        // since this valid userID would be returned from a logged in user...
        // the exception will never be thrown
        try {
            tSystem.requestUnfreeze(this.userID, true);
            System.out.println("Done! Now please be patient while an admin un-freezes your account");
        } catch (EntryNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * TODO: FINISH 
     * Prompt to trade items with another trader
     */
    private void tradeItems() {
        System.out.println("Done!");
    }

    /**
     * TODO: FINISH 
     * Prompt to lend item to another trader
     */
    private void lendItem() {
        System.out.println("Done!");
    }

    /**
     * TODO: FINISH 
     * Prompt to borrow item with another trader
     */
    private void borrowItem() {
        System.out.println("Done!");
    }
}