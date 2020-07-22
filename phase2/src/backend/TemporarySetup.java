package backend;

import backend.exceptions.*;
import backend.models.TradableItem;
import backend.models.users.Admin;
import backend.models.users.Trader;
import backend.models.users.User;
import backend.tradesystem.UserTypes;
import backend.tradesystem.managers.HandleItemRequestsManager;
import backend.tradesystem.managers.LoginManager;
import backend.tradesystem.managers.TraderManager;

import java.io.IOException;

public class TemporarySetup {

    private Trader trader1;
    private Trader trader2;
    private Admin admin;
    private LoginManager loginManager;
    private TraderManager traderManager;
    private HandleItemRequestsManager handleRequestsManager;
    public TemporarySetup(){
        try {
            loginManager = new LoginManager();
            traderManager = new TraderManager();
            handleRequestsManager = new HandleItemRequestsManager();
            trader1 = (Trader)loginManager.registerUser("user", "passssssssS11", UserTypes.TRADER);
            trader2 = (Trader)loginManager.registerUser("user1", "passssssssS11", UserTypes.TRADER);
            admin = (Admin) loginManager.registerUser("admin", "passssssssS11", UserTypes.ADMIN);
            traderManager.addRequestItem(trader1.getId(), "apple", "sweet");
            traderManager.addRequestItem(trader1.getId(), "apple1", "sweet1");
            traderManager.addRequestItem(trader1.getId(), "apple2", "sweet2");
            traderManager.addRequestItem(trader2.getId(), "pear1", "disgusting");
            traderManager.addRequestItem(trader2.getId(), "pear2", "disgusting2");
            traderManager.addRequestItem(trader2.getId(), "pear3", "disgusting3");


            update();
            handleRequestsManager.processItemRequest(trader1.getId(), trader1.getRequestedItems().get(0), true);
            handleRequestsManager.processItemRequest(trader1.getId(), trader1.getRequestedItems().get(0), true);
            handleRequestsManager.processItemRequest(trader1.getId(), trader1.getRequestedItems().get(0), true);
            handleRequestsManager.processItemRequest(trader2.getId(), trader2.getRequestedItems().get(0), true);
            handleRequestsManager.processItemRequest(trader2.getId(), trader2.getRequestedItems().get(0), true);
            handleRequestsManager.processItemRequest(trader2.getId(), trader2.getRequestedItems().get(0), true);

            update();
            traderManager.addToWishList(trader1.getId(), trader2.getAvailableItems().get(0));
            traderManager.addToWishList(trader1.getId(), trader2.getAvailableItems().get(1));
            traderManager.addToWishList(trader2.getId(), trader1.getAvailableItems().get(0));
            traderManager.addToWishList(trader2.getId(), trader1.getAvailableItems().get(1));



        }catch (IOException | UserAlreadyExistsException | BadPasswordException | UserNotFoundException | AuthorizationException | TradableItemNotFoundException e){

        }
    }

    private void update(){
        try {
            trader1 = traderManager.getTrader(trader1.getId());
            trader2 = traderManager.getTrader(trader2.getId());
            admin = (Admin) traderManager.getUser(admin.getId());

        } catch (UserNotFoundException | AuthorizationException e) {
            e.printStackTrace();
        }

    }

}
