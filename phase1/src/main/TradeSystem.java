package main;

import tradableitems.TradableItem;
import tradableitems.TradableItemManager;
import trades.TradeManager;
import users.User;
import users.UserManager;

import javax.jws.soap.SOAPBinding;
import java.io.*;
import java.util.Map;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TradeSystem implements Serializable {

    private UserManager userManager;
    private TradeManager tradeManager;
    private TradableItemManager tradableItemManager;
    private static final Logger LOGGER = Logger.getLogger(Manager.class.getName());
    private static final Handler CONSOLE_HANDLER = new ConsoleHandler();

    private String username;
    private String password;
    private boolean isAdmin;

    public TradeSystem(String filepath, String username, String password) throws IOException {
        this.userManager = new UserManager(filepath);
        this.username = username;
        this.password = password;

        if(checkAdmin());
    }

    public boolean register() {
        return userManager.registerUser(username, password, false);
    }



    private boolean checkAdmin() {
        return false;
    }
}
