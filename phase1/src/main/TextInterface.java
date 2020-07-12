package main;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import exceptions.*;
import main.TradeSystem.Accounts.Account;
import main.TradeSystem.Accounts.AdminAccount;
import main.TradeSystem.Accounts.TraderAccount;
import main.TradeSystem.Accounts.UserTypes;
import main.TradeSystem.TradeSystem;

/**
 * Used as a way to prompt the user and receive input.
 * Code is partially taken from logging.zip, StudentManager.java from week 6 slides and codes.
 */
public class TextInterface {


    private static final String lineBreak = "--------------------------------------------------";

    private final Scanner sc;
    private final TradeSystem tSystem;
    private TraderAccount traderAccount = null;
    private AdminAccount adminAccount = null;
    private int userChoice;
    private String userID;

    /**
     * Constructor for TextInterface - Initializes TradeSystem, Scanner and UserID
     *
     * @throws IOException if file paths are bad
     */
    public TextInterface() throws IOException {
        tSystem = new TradeSystem();
        sc = new Scanner(System.in);
        this.userID = "";
    }

    /**
     * Starts the console prompting and inputs
     */
    public void run() {
        loginAdmin();
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
            if (adminAccount != null) {
                adminMenu();
            } else if (traderAccount != null) {
                traderMenu();
            }
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
                        : tSystem.register(userString, userPaString, UserTypes.TRADER);
                Account tmp = tSystem.getAccount();
                switch (tmp.getAccountType()) {
                    case ADMIN:
                        adminAccount = new AdminAccount(userID);
                        break;
                    case TRADER:
                    default:
                        traderAccount = new TraderAccount(userID);
                }
            } catch (UserAlreadyExistsException e) {
                System.out.println("This username already exists");
            } catch (UserNotFoundException e) {
                System.out.println("This user doesn't exist");
            } catch (IOException e) {
                System.out.println("Couldn't connect to database");
            } catch (AuthorizationException e) {
                System.out.println(e.getMessage());
            }
        } while (this.userID.equals(""));
    }

    /**
     * helper method to create an initial admin
     */
    private void loginAdmin() {
        try {
            tSystem.register("admin", "password", UserTypes.ADMIN);
        } catch (UserAlreadyExistsException e) {
            System.out.println(e.getMessage());
        }
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
        boolean ableToBorrow = false;
        System.out.println("TRADER MAIN MENU");
        System.out.println(lineBreak);
        do {
            try {
                isFrozen = traderAccount.isFrozen();
                ableToBorrow = traderAccount.canBorrow();
            } catch (AuthorizationException | UserNotFoundException e) {
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
                if (ableToBorrow)
                    System.out.println("9.\tBorrow an Item from a Trader");
                System.out.println("10.\tLend an Item to a Trader");
                System.out.println("11.\tTrade with a trader");
                System.out.println("12.\tAccept Trade Request Offer");
                System.out.println("13.\tEdit Trade Request Offer");
                System.out.println("14.\tReject Trade Request Offer");
                System.out.println("15.\tConfirm Successful Trade");
                System.out.println("16.\tConfirm UnSuccessful Trade");
            }

            if (isFrozen)
                System.out.println("17.\tRequest Un-Freeze Account");
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
                    if (!isFrozen && ableToBorrow)
                        trade("BORROW");
                    else
                        System.out.println("Invalid Selection, please try again");
                    break;
                case 10:
                    if (!isFrozen)
                        trade("LEND");
                    else
                        System.out.println("Invalid Selection, please try again");
                    break;
                case 11:
                    if (!isFrozen)
                        trade("TRADE");
                    else
                        System.out.println("Invalid Selection, please try again");
                    break;
                case 12:
                    if (!isFrozen)
                        respondToTradeOffer(true);
                    else
                        System.out.println("Invalid Selection, please try again");
                    break;
                case 13:
                    if (!isFrozen)
                        editTradeOffer();
                    else
                        System.out.println("Invalid Selection, please try again");
                    break;
                case 14:
                    if (!isFrozen)
                        respondToTradeOffer(false);
                    else
                        System.out.println("Invalid Selection, please try again");
                    break;
                case 15:
                    if (!isFrozen)
                        confirmTrade();
                    else
                        System.out.println("Invalid Selection, please try again");
                    break;
                case 16:
                    if (!isFrozen)
                        confirmIncompleteTrade();
                    else
                        System.out.println("Invalid Selection, please try again");
                case 17:
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
                adminAccount.freezeUser(wantToFreeze, freezeStatus);
                success = true;
            } catch (UserNotFoundException e) {
                success = false;
                System.out.println(e.getMessage());
            }
        } while (!success);
        System.out.printf("Done! Trader \"%s\" has now been %s\n", wantToFreeze, freezeStatus ? "frozen" : "unfrozen");//change 2 statements positions
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
            System.out.printf("Enter a password for %s\n", newAdminString);
            newAdminPaString = sc.nextLine();
            try {
                adminAccount.registerAdmin(newAdminString, newAdminPaString);
                success = true;
            } catch (UserAlreadyExistsException e) {
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
     */
    private void printTrades() {
        printList(this.userID, "Accepted", "Trade");
        System.out.println();
        printList(this.userID, "Requested", "Trade");
    }

    /**
     * Prints all Inventory Items for all users in the database
     */
    private void printDatabase() {
        ArrayList<String> allTraders = traderAccount.getAllTraders();
        for (String userID : allTraders) {
            printList(userID, "Inventory", "Item");
        }
    }

    /**
     * Prints the Trader's Inventory given their ID NOTE: This method will not be
     * called by an Admin ever
     */
    private void printInventory() {
        printList(this.userID, "Inventory", "Item");
    }

    /**
     * Prints the Trader's WishList given their ID NOTE: This method will not be
     * called by an Admin ever
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
            TraderAccount currTraderAccount = new TraderAccount(ID);
            ArrayList<String> list = null;
            String itemID = "";
            if (listType.equals("Wishlist"))
                list = currTraderAccount.getWishlist();
            else if (listType.equals("Inventory"))
                list = currTraderAccount.getAvailableItems();
            else if (listType.equals("Accepted"))
                list = currTraderAccount.getAcceptedTrades();
            else if (listType.equals("Requested"))
                list = currTraderAccount.getRequestedTrades();
            System.out.printf("%s's %s %ss\n***************\n", currTraderAccount.getUsername(), listType, itemType);
            for (int i = 0; i < list.size(); i++) {
                itemID = list.get(i);
                if (itemType.equals("Item"))
                    System.out.printf("%s %s #%d: %s\n\t%s\n", listType, itemType, i, currTraderAccount.getTradableItemName(itemID),
                            currTraderAccount.getTradableItemDesc(itemID));
                else {
                    System.out.printf("%s %s #%d\n", listType, itemType, i);
                    if (listType.equals("Accepted"))
                        printTrade(currTraderAccount.getAcceptedTradeId(i), false);
                    else if (listType.equals("Requested"))
                        printTrade(currTraderAccount.getRequestedTradeId(i), true);
                }
            }
        } catch (AuthorizationException | IOException | UserNotFoundException | TradableItemNotFoundException | TradeNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Prints all the details of the trade
     *
     * @param tradeID id of the trade
     */
    private void printTrade(String tradeID, boolean isRequested) throws IOException, AuthorizationException, TradeNotFoundException, UserNotFoundException, TradableItemNotFoundException {
        TraderAccount otherTraderAccount = new TraderAccount(traderAccount.getTraderIdFromTrade(tradeID));
        String typeOfTrade = traderAccount.getUserOffer(tradeID).equals("") ? "borrowing from" : traderAccount.getUserOffer(tradeID).equals("") ? "lending to" : "2-way trading with";
        String isTemporaryString = traderAccount.isTradeTemporary(tradeID) ? "temporarily" : "permanently";
        String userItemID = typeOfTrade.equals("borrowing from") ? "N/A" : traderAccount.getTradableItemName(traderAccount.getUserOffer(tradeID));
        String traderItemID = typeOfTrade.equals("lending to") ? "N/A" : traderAccount.getTradableItemName(otherTraderAccount.getUserOffer(tradeID));
        System.out.printf("\t%s is %s %s TRADER=\"%s\"\n", traderAccount.getUsername(), isTemporaryString, typeOfTrade, otherTraderAccount.getUsername());
        if (!isRequested)
            System.out.println("TRADE IS " + (traderAccount.isTradeInProgress(tradeID) ? "IN PROGRESS" : "FINISHED"));
        System.out.printf("\tYour Item:\t%s\n\t%s's Item:\t%s\n", userItemID, otherTraderAccount.getUsername(), traderItemID);
        System.out.println("\tFirst Meeting: " + traderAccount.getFirstMeeting(tradeID) + " @ " + traderAccount.getMeetingLocation(tradeID));
        if (isTemporaryString.equals("temporarily")) {
            System.out.println("\tSecond Meeting: " + traderAccount.getSecondMeeting(tradeID) + " @ " + traderAccount.getMeetingLocation(tradeID));
        }
    }

    /**
     * Prints all Traders that have requested an Unfreeze on their account
     */
    private void printAllUnfreezeRequests() {
        System.out.println("*** Un-Freeze Requesters ***");
        ArrayList<String> unFreezeRequests = adminAccount.getAllUnfreezeRequests();
        for (String username : unFreezeRequests) {
            System.out.println(username);
        }
    }

    /**
     * Prints all Item Requests for all Traders
     */
    private void printAllItemRequests() {
        System.out.println("*** Item Requests ***");
        try {
            HashMap<String, ArrayList<String>> itemRequests = adminAccount.getAllItemRequests();
            for (String userID : itemRequests.keySet()) {
                TraderAccount currTradeAccount = new TraderAccount(userID);
                if (itemRequests.get(userID).size() > 0) {
                    System.out.println(currTradeAccount.getUsername() + " :");
                    ArrayList<String> currItemRequests = itemRequests.get(userID);
                    for (int i = 0; i < currItemRequests.size(); i++) {
                        String itemID = currItemRequests.get(i);
                        System.out.printf("\t#%d: %s\n", i, adminAccount.getTradableItemName(itemID));
                    }
                }
            }
        } catch (IOException | AuthorizationException | UserNotFoundException | TradableItemNotFoundException e) {
            System.out.println(e.getMessage());
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
                    traderAccount.requestItem(itemName, itemDesc);
                    success = true;
                } catch (AuthorizationException | UserNotFoundException e) {
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
                traderAccount.addToWishList(itemName);
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
        int itemIndex = 0;
        boolean success = false;
        do {
            try {
                System.out.println("Enter the name of the Trader");
                System.out.print("=> ");
                traderName = sc.nextLine();
                System.out.println("Enter the index of the Item");
                System.out.print("=> ");
                itemIndex = Integer.parseInt(sc.nextLine());
                adminAccount.processItemRequest(adminAccount.getUserId(traderName), itemIndex, isAccepted);
                success = true;
            } catch (AuthorizationException | EntryNotFoundException | NumberFormatException e) {
                System.out.println(e.getMessage());
                success = false;
            }
        } while (!success);
        System.out.printf("Done! %s's request for item at index '%s' has now been %s\n", traderName, itemIndex,
                isAccepted ? "accepted" : "rejected");
    }

    /**
     * Prints recent Traded items
     */
    private void viewRecentTradeItems() {
        try {
            Set<String> recentTradeItems = traderAccount.getRecentTradeItems();
            for (String itemName : recentTradeItems) {
                System.out.println("Item: " + itemName);
            }
        } catch (AuthorizationException | UserNotFoundException | TradableItemNotFoundException | TradeNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Prints the 3 most traded-with Traders
     */
    private void viewFreqTraders() {
        try {
            String[] freqTraders = traderAccount.getFrequentTraders();
            for (int i = 0; i < freqTraders.length && freqTraders[i] != null; i++) {
                System.out.printf("Trader #%d: %s\n", i + 1, freqTraders[i]);
            }
        } catch (AuthorizationException | UserNotFoundException | TradeNotFoundException e) {
            System.out.println(e.getMessage());
        }
        System.out.println(lineBreak);
    }


    /**
     * Prompts admin to change the current trade limit
     */
    private void changeWeeklyTradeLimit() {
        int tradeLimit = 0;
        try {
            tradeLimit = adminAccount.getCurrentTradeLimit();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            tradeLimit = 0;
        }
        boolean success = false;
        if (tradeLimit == 0) System.out.println("There are no Traders in the System!");
        else System.out.println("The current weekly trade limit is " + tradeLimit);
        do {
            try {
                System.out.println("Enter the new value for the trade limit");
                System.out.print("=> ");
                tradeLimit = Integer.parseInt(sc.nextLine());
                adminAccount.setTradeLimit(tradeLimit);
                success = true;
            } catch (NumberFormatException | UserNotFoundException e) {
                success = false;
                System.out.println(e.getMessage());
            }
        } while (!success);
        System.out.println("Done! The new weekly trade limit is now" + tradeLimit);
    }

    /**
     * Requests the logged in trader to be unfrozen by an admin
     * REQUIREMENT: isFrozen == true
     */
    private void requestUnFreeze() {
        // since this valid userID would be returned from a logged in user...
        // the exception will never be thrown
        try {
            traderAccount.requestUnfreeze(true);
            System.out.println("Done! Now please be patient while an admin un-freezes your account");
        } catch (AuthorizationException | UserNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Prompt to trade items with another trader
     * REQUIREMENT: isFrozen == true
     *
     * @param tradeType LEND: 1-way Trade - logged-in user gives to another user
     *                  BORROW: 1-way Trade - logged-in user gets from another user (REQUIREMENT: ableToBorrow == true)
     *                  TRADE: Standard 2-way trade between logged-in user and another user
     */
    private void trade(String tradeType) {
        String traderName = "";
        String meetingTime = "";
        String meetingLocation = "";
        int inventoryItemIndex = -1;
        int traderInventoryItemIndex = -1;
        boolean isTemporary = true;
        Date firstMeeting = null;
        Date secondMeeting = null;
        boolean success = false;
        do {
            try {
                if (!tradeType.equals("BORROW")) {
                    if (traderAccount.getAvailableItems().size() == 0) {
                        System.out.println("Ruh Roh! Looks like you have no available items to trade\nABORTING TRADE...");
                        return;
                    }
                    System.out.println("Enter the username of the Trader you would like to lend your item to");
                    System.out.print("=> ");
                    traderName = sc.nextLine();
                    if (traderAccount.getIdFromUsername(traderName).equals(this.userID)) {
                        System.out.println("A Trader cannot perform borrow/lend/trade action to him/herself");
                        return;
                    }
                    System.out.println("Here is your inventory:");
                    printInventory();
                    System.out.println("Please enter the index of the item you would like to give");
                    System.out.print("=> ");
                    inventoryItemIndex = Integer.parseInt(sc.nextLine());
                }
                if (!tradeType.equals("LEND")) {
                    System.out.println("Enter the username of the Trader you would like to borrow from");
                    System.out.print("=> ");
                    traderName = sc.nextLine();
                    TraderAccount otherTraderAccount = new TraderAccount(traderAccount.getIdFromUsername(traderName));
                    if (otherTraderAccount.getAvailableItems().size() == 0) {
                        System.out.printf("Ruh Roh! %s does not have any available items to trade\nABORTING TRADE...\n", traderName);
                        return;
                    }
                    System.out.printf("Here is %s's current inventory:", traderName);
                    printList(otherTraderAccount.getTraderId(), "Inventory", "Item");
                    System.out.println("Enter the index of the item that you would like to receive from the trader");
                    System.out.print("=> ");
                    traderInventoryItemIndex = Integer.parseInt(sc.nextLine());
                }

                System.out.println("Enter your preferred first meeting time in the format yyyy/MM/dd HH:mm");
                System.out.print("=> ");
                meetingTime = sc.nextLine();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm");
                Date d = new Date();
                firstMeeting = sdf.parse(meetingTime);
                if (d.after(firstMeeting)) {
                    System.out.println("please enter a valid date");
                    return;
                }

                System.out.println("Is this a temporary trade? Y/N");
                System.out.print("=> ");
                isTemporary = sc.nextLine().equals("Y");
                if (isTemporary) {
                    Calendar c = Calendar.getInstance();
                    c.setTime(firstMeeting);
                    c.add(Calendar.MONTH, 1);
                    secondMeeting = c.getTime();
                }
                System.out.println("Enter your preferred meeting location");
                System.out.print("=> ");
                meetingLocation = sc.nextLine();

                if (tradeType.equals("LEND"))
                    traderAccount.lendItem(traderName, firstMeeting, secondMeeting, meetingLocation,
                            inventoryItemIndex);
                else if (tradeType.equals("BORROW"))
                    traderAccount.borrowItem(traderName, firstMeeting, secondMeeting, meetingLocation,
                            traderInventoryItemIndex);
                else
                    traderAccount.trade(traderName, firstMeeting, secondMeeting, meetingLocation, inventoryItemIndex, traderInventoryItemIndex);
                success = true;
            } catch (ParseException | NumberFormatException | IndexOutOfBoundsException | AuthorizationException | IOException | UserNotFoundException | TradableItemNotFoundException | CannotTradeException e) {
                success = false;
                System.out.println(e.getMessage());
            }
        } while (!success);
        System.out.printf("Done! Your Trade Request to TRADER=\"%s\" has been sent\n", traderName);
    }

    /**
     * Prompts user to reject a trade offer
     * REQUIREMENT: isFrozen == true
     */
    private void respondToTradeOffer(boolean isAccepted) {
        int requestedTradeIndex = -1;
        boolean canTrade = true;
        boolean success = false;
        do {
            try {
                if (traderAccount.getRequestedTrades().size() == 0) {
                    System.out.println(
                            "Ruh Roh! Looks like you do not have any requested trades\nABORTING TRADE REQUEST RESPONSE...");
                    return;
                }
                System.out.println("Here is your requested trades");
                printList(this.userID, "Requested", "Trade");
                System.out.println("Enter the index of the requested trade that you would like to " + (isAccepted ? "accept" : "reject"));
                System.out.print("=> ");
                requestedTradeIndex = Integer.parseInt(sc.nextLine());
                String tradeID = traderAccount.getRequestedTradeId(requestedTradeIndex);
                String user2ID = traderAccount.getTraderIdFromTrade(tradeID);


//                if (!traderAccount.canTrade()) { //breaks infinite loop
//                    canTrade = false;
//                    break;
//                }
                // not sure if these 3 lines above are needed since we're now doing everything within the managers

                if (isAccepted) traderAccount.acceptTrade(tradeID);
                else traderAccount.rejectTrade(tradeID);
                success = true;


            } catch (NumberFormatException | IndexOutOfBoundsException | AuthorizationException | UserNotFoundException | TradeNotFoundException | CannotTradeException e) {
                success = false;
                System.out.println(e.getMessage());
            }
        } while (!success);
        if (canTrade) //only displays msg if actually accepted or rejected
            System.out.println("Done! You have successfully " + (isAccepted ? "accepted" : "rejected") + " the requested trade");
    }

    /**
     * Prompts user to edit a trade offer
     * REQUIREMENT: isFrozen == true
     */
    private void editTradeOffer() {
        int requestedTradeIndex = -1;

        String tempInputString = "";
        String tradeID = "";
        String traderID = "";
        String typeOfTrade = "";
        String meetingLocation = "";
        int inventoryItemIndex = -1;
        int traderInventoryItemIndex = -1;
        boolean isTemporary = true;
        Date firstMeeting = null;
        Date secondMeeting = null;

        boolean success = false;
        do {
            try {
                if (traderAccount.getRequestedTrades().size() == 0) {
                    System.out.println(
                            "Ruh Roh! Looks like you do not have any requested trades\nABORTING TRADE REQUEST RESPONSE...");
                    return;
                }
                System.out.println("Here is your requested trades");
                printList(this.userID, "Requested", "Trade");
                System.out.println("Enter the index of the requested trade that you would like to edit");
                System.out.print("=> ");
                requestedTradeIndex = Integer.parseInt(sc.nextLine());
                // YO MAMA SO FAT WHEN SHE DOES A 180, A WHOLE YEAR PASSES
                tradeID = traderAccount.getRequestedTradeId(requestedTradeIndex);
                traderID = traderAccount.getTraderIdFromTrade(tradeID);
                TraderAccount otherTraderAccount = new TraderAccount(traderID);

                inventoryItemIndex = traderAccount.getUserTradeItemIndex(tradeID);
                traderInventoryItemIndex = otherTraderAccount.getUserTradeItemIndex(tradeID);
                isTemporary = traderAccount.isTradeTemporary(tradeID);
                firstMeeting = traderAccount.getFirstMeeting(tradeID);
                secondMeeting = traderAccount.getSecondMeeting(tradeID);
                meetingLocation = traderAccount.getMeetingLocation(tradeID);
                typeOfTrade = traderAccount.getUserOffer(tradeID).equals("") ? "lending to" : otherTraderAccount
                        .getUserOffer(tradeID).equals("") ? "borrowing from" : "2-way trading with";
                System.out.printf("In this trade, TRADER=\"%s\" is %s you", typeOfTrade, otherTraderAccount.getUsername());
                // if this trader is lending to me
                if (typeOfTrade.equals("lending to")) {
                    System.out.println("Since the trader is lending to you originally, you do not have any items selected to lend");
                    if (traderAccount.getAvailableItems().size() > 0) {
                        System.out.println("Would you like to add an item to give to the trader? Y/N");
                        System.out.print("=> ");
                        tempInputString = sc.nextLine();
                    }
                }
                if (tempInputString.trim().equals("Y") || (tempInputString.trim().equals("") && traderAccount.getAvailableItems().size() != 0)) {
                    System.out.println("Here is your inventory:");
                    printInventory();
                    if (!typeOfTrade.equals("lending to")) {
                        System.out.println("Item #" + inventoryItemIndex + "is the currently selected item");
                        System.out.println("If you would not like to change this item, simply press ENTER/RETURN at the prompt");
                    }
                    System.out.println("Please enter the index of the item you would like to give");
                    System.out.print("=> ");
                    tempInputString = sc.nextLine();
                    if (!tempInputString.trim().equals("")) {
                        inventoryItemIndex = Integer.parseInt(tempInputString);
                    }
                }
                tempInputString = ""; //reset the input string
                // if this trader is borrowing from me
                if (typeOfTrade.equals("borrowing from")) {
                    System.out.println("Since the trader is borrowing from you originally, they do not have any items selected to lend");
                    if (otherTraderAccount.getAvailableItems().size() > 0) {
                        System.out.println("Would you like to add an item to borrow from this trader? Y/N");
                        System.out.print("=> ");
                        tempInputString = sc.nextLine();
                    }
                }
                if (tempInputString.trim().equals("Y") || (tempInputString.trim().equals("") && otherTraderAccount.getAvailableItems().size() != 0)) {
                    System.out.printf("Here is %s's current inventory:", otherTraderAccount.getUsername());
                    printList(traderID, "Inventory", "Item");
                    if (!typeOfTrade.equals("borrowing from")) {
                        System.out.println("Item #" + traderInventoryItemIndex + "is the currently selected item");
                        System.out.println("If you would not like to change this item, simply press ENTER/RETURN at the prompt");
                    }
                    System.out.println("Enter the index of the item that you would like to recieve from the trader");
                    System.out.print("=> ");
                    tempInputString = sc.nextLine();
                    if (!tempInputString.trim().equals("")) {
                        traderInventoryItemIndex = Integer.parseInt(tempInputString);
                    }
                }
                tempInputString = ""; //reset the input string
                System.out.println("The first meeting will take place on " + firstMeeting.toString());
                System.out.println("If you would not like to change this, please press ENTER/RETURN at the prompt");
                System.out.println("Enter your preferred first meeting time in the format yyyy/MM/dd HH:mm");
                System.out.print("=> ");
                tempInputString = sc.nextLine();
                if (!tempInputString.trim().equals("")) {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm");
                    firstMeeting = sdf.parse(tempInputString);
                }
                tempInputString = ""; //reset the input string
                System.out.println("Originally, this is a " + (isTemporary ? "TEMPORARY" : "PERMANENT") + "trade");
                System.out.println("If you would not like to change this, please press ENTER/RETURN at the prompt");
                System.out.println("Is this a temporary trade? Y/N");
                tempInputString = sc.nextLine();
                // temporary = N => permanent
                // temporary = "" => styll temp
                // permanent - Y => temporary
                // permanent - "" => styll permanent
                if ((isTemporary && tempInputString.trim().equals("")) || (!isTemporary && tempInputString.trim().equals("Y"))) {
                    if (tempInputString.trim().equals("")) {
                        System.out.println("The second meeting will take place on " + firstMeeting.toString());
                        System.out.println("If you would not like to change this, please press ENTER/RETURN at the prompt");
                    }
                    System.out.println("Enter your preferred second meeting time in the format yyyy/MM/dd HH:mm");
                    System.out.print("=> ");
                    tempInputString = sc.nextLine();
                    if (!tempInputString.trim().equals("")) {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm");
                        secondMeeting = sdf.parse(tempInputString);
                    }
                } else if (isTemporary && tempInputString.trim().equals("N")) {
                    secondMeeting = null;
                }
                tempInputString = ""; // reset the input string
                System.out.println("The meeting location was at " + meetingLocation);
                System.out.println("If you would not like to change this, please press ENTER/RETURN at the prompt");
                System.out.println("Enter your preferred meeting location");
                System.out.print("=> ");
                tempInputString = sc.nextLine();
                if (!tempInputString.trim().equals("")) {
                    meetingLocation = tempInputString;
                }
                tempInputString = ""; // reset the input string
                traderAccount.editTrade(tradeID, firstMeeting,
                        secondMeeting, meetingLocation, inventoryItemIndex, traderInventoryItemIndex);
                success = true;
            } catch (NumberFormatException | ParseException | CannotTradeException | AuthorizationException | IOException | TradeNotFoundException | UserNotFoundException | TradableItemNotFoundException e) {
                success = false;
                System.out.println(e.getMessage());
            }
        } while (!success);
        System.out.println("Done! This requested trade has been successfully editied");
    }

    /**
     * Prompts user to confirm that a trade has happened outside of this program
     */

    private void confirmTrade() {
        boolean success = false;
        int acceptedTradeIndex = -1;
        do {
            try {


                if (traderAccount.getAcceptedTrades().size() == 0) {
                    System.out.println(
                            "Ruh Roh! Looks like you do not have any ongoing accepted trades\nABORTING TRADE CONFIRMATION...");
                    return;
                }
                System.out.println("Here is your accepted trades");
                printList(this.userID, "Accepted", "Trade");
                System.out.println("Enter the index of the accepted trade that you would like to confirm took place");
                System.out.print("=> ");
                acceptedTradeIndex = Integer.parseInt(sc.nextLine());
                String tradeID = traderAccount.getAcceptedTradeId(acceptedTradeIndex);
                System.out.println(traderAccount.getAcceptedTrades());
                if (!traderAccount.isTradeInProgress(tradeID)) {
                    System.out.println(
                            "Ruh Roh! Looks like this trade has already finished taking place\nABORTING TRADE CONFIRMATION...");
                    return;
                }

                if (traderAccount.hasUserConfirmedAllMeetings(tradeID)) {
                    System.out.println(
                            "Ruh Roh! Looks like you have already confirmed to all the meetings for this trade\nABORTING TRADE CONFIRMATION...");
                    return;
                }
                success = traderAccount.confirmMeeting(tradeID, true);
            } catch (NumberFormatException | AuthorizationException | CannotTradeException | UserNotFoundException | TradeNotFoundException e) {
                System.out.println(e.getMessage());
            }
        } while (!success);
        System.out.println("Done! You have successfully confirmed the accepted trade");
    }

    /**
     * Prompts user to confirm that the other trader did not show up to IRL trade
     */
    //TODO: we should probably delete this and instead set a time limit until the trade is auto detected as incomplete
    private void confirmIncompleteTrade() {
        boolean success = false;
        int acceptedTradeIndex = -1;
        do {
            try {
                System.out.println(traderAccount.getAcceptedTrades());
                if (traderAccount.getAcceptedTrades().size() == 0) {
                    System.out.println(
                            "Ruh Roh! Looks like you do not have any ongoing accepted trades\nABORTING TRADE CONFIRMATION...");
                    return;
                }
                System.out.println("Here is your accepted trades");
                printList(this.userID, "Accepted", "Trade");
                System.out.println("Enter the index of the accepted trade that you would like to confirm was unsuccessful");
                System.out.print("=> ");
                acceptedTradeIndex = Integer.parseInt(sc.nextLine());
                if (!traderAccount.isTradeInProgress(traderAccount.getAcceptedTradeId(acceptedTradeIndex))) {
                    System.out.println(
                            "Ruh Roh! Looks like this trade has already finished taking place\nABORTING TRADE CONFIRMATION...");
                    return;
                }
                if (traderAccount.hasUserConfirmedAllMeetings(traderAccount.getAcceptedTradeId(acceptedTradeIndex))) {
                    System.out.println(
                            "Ruh Roh! Looks like you have already confirmed to all the meetings for this trade\nABORTING TRADE CONFIRMATION...");
                    return;
                }
                traderAccount.confirmIncompleteTrade(traderAccount.getAcceptedTradeId(acceptedTradeIndex));
                success = true;

            } catch (NumberFormatException | AuthorizationException | UserNotFoundException | TradeNotFoundException e) {
                System.out.println(e.getMessage());
            }
        } while (!success);
        System.out.println("Done! The trade has been marked as unsuccessful");
    }
}