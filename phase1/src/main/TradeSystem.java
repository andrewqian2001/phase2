package main;

import exceptions.AuthorizationException;
import exceptions.EntryNotFoundException;
import exceptions.UserAlreadyExistsException;
import tradableitems.TradableItem;
import tradableitems.TradableItemManager;
import trades.TradeManager;
import users.*;

import java.io.*;
import java.util.ArrayList;
import java.util.logging.Level;

public class TradeSystem implements Serializable {

    private static final String USERS_FILE_PATH = "src/users/users.ser";
    private static final String TRADE_FILE_PATH = "src/users/trades.ser";
    private static final String TRADABLE_ITEM_FILE_PATH = "src/users/tradableitems.ser";

    private UserManager userManager;
    private TradeManager tradeManager;
    private TradableItemManager tradableItemManager;
    private String loggedInUserId;


    public TradeSystem() throws IOException {
        userManager = new UserManager(USERS_FILE_PATH);
        tradeManager = new TradeManager(TRADE_FILE_PATH);
        tradableItemManager = new TradableItemManager((TRADABLE_ITEM_FILE_PATH));
        loggedInUserId = "";
    }

    public String getLoggedInUserId() {
        return this.loggedInUserId;
    }

    public String registerTrader(String username, String password) throws IOException, UserAlreadyExistsException {
        userManager = new TraderManager(USERS_FILE_PATH);
        this.loggedInUserId = userManager.registerUser(username, password);
        return this.loggedInUserId;
    }

    public String registerAdmin(String username, String password) throws IOException, UserAlreadyExistsException {
        userManager = new AdminManager(USERS_FILE_PATH);
        this.loggedInUserId = userManager.registerUser(username, password);
        return this.loggedInUserId;
    }

    public String login(String username, String password) throws EntryNotFoundException, IOException {
        this.loggedInUserId = userManager.login(username, password);

        User loggedInUser = userManager.populate(loggedInUserId);
        if (loggedInUser instanceof Admin)
            userManager = new AdminManager(USERS_FILE_PATH);
        else
            userManager = new TraderManager(USERS_FILE_PATH);

        return this.loggedInUserId;
    }

    public void freezeUser(String userId, boolean freezeStatus) throws EntryNotFoundException, AuthorizationException {
        userManager.freezeUser(loggedInUserId, userId, freezeStatus);
    }


    public void requestItem(String ID, TradableItem item) {
        Trader user = (Trader) getLoggedInUser(ID);

        ArrayList<String> List = user.getRequestedItems();
        List.add(item.getId());
        user.setRequestedItems(List);
        userManager.update(user);

    }

    public void addItem(String ID, TradableItem item) {
        Trader user = (Trader) getLoggedInUser(ID);
        if (user.hasPermission(Permission.CONFIRM_ADDED_ITEM)) {
            ArrayList<String> inventory = user.getAvailableItems();
            ArrayList<String> reqList = user.getRequestedItems();

            if (reqList.contains(item.getId())) {
                inventory.add(item.getId());
                user.setAvailableItems(inventory);
                reqList.remove(item.getId());
                user.setRequestedItems(reqList);
                userManager.update(user);
            }
        }
    }

    public void printTrades(String ID) {
        Trader user = (Trader) getLoggedInUser(ID);
        ArrayList<String> AccTrades = user.getAcceptedTrades();
        ArrayList<String> ReqTrades = user.getRequestedTrades();
        System.out.println("User " + user.getUsername() + "'s accepted trades");
        for (int i = 0; i < AccTrades.size(); i++) {
            String item = AccTrades.get(i); //this is just the ID, how do you get the actual item name?
            System.out.println(item);
        }
        System.out.println("User " + user.getUsername() + "'s requested trades");
        for (int i = 0; i < ReqTrades.size(); i++) {
            String item = ReqTrades.get(i); //this is just the ID, how do you get the actual item name?
            System.out.println(item);
        }

    }

    public void printInventory(String ID) {
        Trader user = (Trader) getLoggedInUser(ID);
        ArrayList<String> Inventory = user.getAvailableItems();
        System.out.println("User " + user.getUsername() + "'s available items");
        for (int i = 0; i < Inventory.size(); i++) {
            String item = Inventory.get(i);  //this is just the ID, how do you get the actual item name?
            System.out.println(item);
        }
    }

    public void printWishlist(String ID) {
        Trader user = (Trader) getLoggedInUser(ID);
        ArrayList<String> list = user.getWishlist();
        System.out.println("User " + user.getUsername() + "'s wishlist");
        for (int i = 0; i < list.size(); i++) {
            String item = list.get(i);  //this is just the ID, how do you get the actual item name?
            System.out.println(item);
        }
    }

    public void requestUnfreeze(String ID) {
        Trader user = (Trader) getLoggedInUser(ID);
    }
}
