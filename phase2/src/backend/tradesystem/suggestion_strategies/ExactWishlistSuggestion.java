package backend.tradesystem.suggestion_strategies;

import backend.exceptions.AuthorizationException;
import backend.exceptions.UserNotFoundException;
import backend.models.Suggestion;
import backend.models.users.Trader;
import backend.tradesystem.Manager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class ExactWishlistSuggestion extends Manager implements SuggestLendStrategy, SuggestTradeStrategy{

    public ExactWishlistSuggestion(String userFilePath, String tradableItemFilePath, String tradeFilePath) throws IOException {
        super(userFilePath, tradableItemFilePath, tradeFilePath);
    }

    public ExactWishlistSuggestion() throws IOException {
        super();
    }

    /**
     * Gets all the trader ids in the database
     *
     * @return all the traders in the database
     */
    private List<String> getAllTraders() {
        List<String> allTraders = new ArrayList<>();
        for (String userId : getUserDatabase().getItems().keySet()) {
            try {
                if (getUser(userId) instanceof Trader)
                    allTraders.add(userId);
            } catch (UserNotFoundException e) {
                e.printStackTrace();
            }
        }
        return allTraders;
    }

    /**
     * Gets all the trader ids within the same city
     *
     * @param city the city name
     * @return list of all traders within the same city
     */
    private List<String> getAllTradersInCity(String city) {
        List<String> allTraders = new ArrayList<>();
        for (String userId : getUserDatabase().getItems().keySet()) {
            try {
                if (getUser(userId) instanceof Trader && ((Trader) getUser(userId)).getCity().equalsIgnoreCase(city))
                    allTraders.add(userId);
            } catch (UserNotFoundException e) {
                e.printStackTrace();
            }
        }
        return allTraders;
    }

    /**
     * Returns a list of the best lends that trader thisTraderId can preform
     * The elements in the list is in the format of [thisTraderId, toTraderId, itemId]
     *
     * @param thisTraderId The id of the trader that will be lending the item
     * @param inCity       Whether to only search for possible trades within the trader's city
     * @return a list of the best lends that trader thisTraderId can preform
     * @throws UserNotFoundException  if the user can not be found
     * @throws AuthorizationException if the user is frozen
     */
    private List<String[]> suggestLendList(String thisTraderId, boolean inCity) throws
            UserNotFoundException, AuthorizationException {
        Trader thisTrader = getTrader(thisTraderId);
        if (thisTrader.isFrozen()) throw new AuthorizationException("Frozen account");
        List<String[]> result = new ArrayList<>();
        HashSet<String> thisTraderItems = new HashSet<>(thisTrader.getAvailableItems());

        List<String> allTraders = inCity ? getAllTradersInCity(thisTrader.getCity()) : getAllTraders();

        // Get suggested items for all traders
        for (String traderId : allTraders) {
            if (traderId.equals(thisTraderId)) {
                continue;
            }
            Trader trader = getTrader(traderId);
            for (String item : trader.getWishlist()) {
                if (thisTraderItems.contains(item)) {
                    String[] items = {thisTraderId, traderId, item};
                    result.add(items);
                }
            }
        }

        return result;
    }


    @Override
    public Suggestion suggestLend(String thisTraderId, boolean inCity) throws
            UserNotFoundException, AuthorizationException {
        List<String[]> lends = suggestLendList(thisTraderId, inCity);
        if (lends.size() == 0) {
            return null;
        }
        return new Suggestion(lends.get(0)[0], lends.get(0)[1], lends.get(0)[2]);
    }

    @Override
    public Suggestion suggestTrade(String thisTraderId, boolean inCity) throws
            UserNotFoundException, AuthorizationException {

        Trader thisTrader = getTrader(thisTraderId);
        if (thisTrader.isFrozen()) throw new AuthorizationException("Frozen account");

        List<String[]> toLend = suggestLendList(thisTraderId, inCity);

        HashSet<String> thisTraderWishlist = new HashSet<>(thisTrader.getWishlist());

        // Create trades where both traders give an item that is in each other's wish list
        for (String[] lendInfo : toLend) {
            for (String candidateItem : getTrader(lendInfo[1]).getAvailableItems()) {
                if (thisTraderWishlist.contains(candidateItem)) {
                    return new Suggestion(lendInfo[0], lendInfo[1], lendInfo[2], candidateItem);
                }
            }
        }
        return null;
    }
}
