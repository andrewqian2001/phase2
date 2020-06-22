package main;

import com.sun.deploy.security.SelectableSecurityManager;
import exceptions.UserAlreadyExistsException;
import exceptions.UserNotFoundException;
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
import java.util.logging.Logger;

public class TradeSystem implements Serializable {

    private UserManager userManager;
    private UserManager adminUserManager;
    private TradeManager tradeManager;
    private TradableItemManager tradableItemManager;
    private static final Logger LOGGER = Logger.getLogger(Manager.class.getName());
    private static final Handler CONSOLE_HANDLER = new ConsoleHandler();

    private boolean isAdmin;

    public TradeSystem(String filepath, String adminFilepath) throws IOException {
        this.adminUserManager = new UserManager(adminFilepath);
        this.userManager = new UserManager(filepath);
    }

    public User register(String username, String password) throws UserAlreadyExistsException, UserNotFoundException {
        if(checkOriginalAdmin(username, password)) {
            userManager.registerUser(username, password, true);
        } else {
            userManager.registerUser(username, password, false);
        }
        return login(username, password);
    }

    public User login(String username, String password) throws UserNotFoundException {
        return userManager.login(username, password);
    }

    private boolean checkOriginalAdmin(String username, String password) {
        return(adminUserManager.login(username, password).hasPermission());
    }
}
