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

    public String getTradableItemName(String tradableItemId) throws EntryNotFoundException {
        return tradableItemManager.getName(tradableItemId);
    }

    public String getTradableItemDesc(String tradableItemId) throws EntryNotFoundException {
        return tradableItemManager.getDesc(tradableItemId);
    }

    public void requestItem(String ID, TradableItem item) {
        Trader user = null;
        try {
            user = (Trader) userManager.populate(ID);
        } catch (EntryNotFoundException e) {
            // Not sure what im supposed to do when I get an exception
        } finally {
            ArrayList<String> List = user.getRequestedItems();
            List.add(item.getId());
            user.setRequestedItems(List);
            userManager.update(user);
        }

    }

    public void addItem(String ID, TradableItem item) {
        Trader user = null;
        try {
            user = (Trader) userManager.populate(ID);
        } catch (EntryNotFoundException e) {
            // Not sure what im supposed to do when I get an exception
        } finally {
            if (user != null && user.hasPermission(Permission.CONFIRM_ADDED_ITEM)) {
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

    }

    public void printTrades(String ID) {
       printList(ID, "Accepted", "Trade");
       System.out.println();
       printList(ID, "Requested", "Trade");
    }

    public void printInventory(String ID) {
        printList(ID, "Inventory", "Item");
    }

    public void printWishlist(String ID) {
        printList(ID, "Wishlist", "Item");
    }

    private void printList(String ID, String listType, String itemType) {
        try {
            Trader user = (Trader) userManager.populate(ID);
            ArrayList<String> list = null;
            String itemID = "";
            if(listType.equals("Wishlist")) list = user.getWishlist();
            else if(listType.equals("Inventory")) list = user.getAvailableItems();
            else if(listType.equals("Accepted")) list = user.getAcceptedTrades();
            else if(listType.equals("Requested")) list = user.getRequestedTrades();
            System.out.printf("%s's %s %ss\n***************\n", user.getUsername(), listType, itemType);
            for(int i = 0; i < list.size(); i++) {
                itemID = list.get(i);
                System.out.printf("%s %s #%d: %s\n\t%s\n", listType, itemType, i, getTradableItemName(itemID), getTradableItemDesc(itemID));
            }
        } catch (EntryNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }

    public void requestUnfreeze(String ID) {
        Trader user = (Trader) getLoggedInUser(ID);
    }
}
