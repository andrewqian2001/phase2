package backend.tradesystem.managers;

import backend.exceptions.*;
import backend.models.TradableItem;
import backend.models.Trade;
import backend.models.users.Trader;
import backend.models.users.User;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.*;

/**
 * Handles anything about getting info on trading and on traders, but this does not handle trading itself
 */
public class TradingInfoManager extends Manager {
    /**
     * Initialize the objects to get items from databases
     *
     * @throws IOException if something goes wrong with getting database
     */
    public TradingInfoManager() throws IOException {
        super();
    }

    /**
     * Making the database objects with set file paths
     *
     * @param userFilePath         the user database file path
     * @param tradableItemFilePath the tradable item database file path
     * @param tradeFilePath        the trade database file path
     * @throws IOException issues with getting the file path
     */
    public TradingInfoManager(String userFilePath, String tradableItemFilePath, String tradeFilePath) throws IOException {
        super(userFilePath, tradableItemFilePath, tradeFilePath);
    }


    /**
     * Gets all the traders in the database
     *
     * @return all the traders in the database
     */
    public ArrayList<Trader> getAllTraders() {
        ArrayList<Trader> allTraders = new ArrayList<>();
        for (User user : getUserDatabase().getItems())
            if (user instanceof Trader)
                allTraders.add((Trader) user);
        return allTraders;
    }

    /**
     * Return traders that contain name
     *
     * @param name the string to search for
     * @return traders with similar names
     */
    public ArrayList<Trader> searchTrader(String name) {
        ArrayList<Trader> similarTraders = new ArrayList<>();
        for (Trader trader : getAllTraders())
            if (trader.getUsername().toLowerCase().contains(name.toLowerCase()))
                similarTraders.add(trader);
        return similarTraders;
    }

    /**
     * Gets all the traders within the same city
     *
     * @param city the city name
     * @return list of all traders within the same city
     */
    public ArrayList<Trader> getAllTradersInCity(String city) {
        ArrayList<Trader> allTraders = getAllTraders();
        ArrayList<Trader> updatedTraders = new ArrayList<>();
        for (Trader trader : allTraders) {
            if (trader.getCity().equalsIgnoreCase(city))
                updatedTraders.add(trader);
        }
        return updatedTraders;
    }


    /**
     * Gets tradable items that has the name substring within the input name string
     * For example, if the item name is "Apple Pie", and the name to check for is "apple",
     * then that TradableItem is included as a list of items to return
     *
     * @param name the name to check for
     * @return list of tradable items that match the name
     */
    public ArrayList<TradableItem> getTradableItemsWithName(String name) {
        ArrayList<TradableItem> items = new ArrayList<>();
        for (TradableItem item : getTradableItemDatabase().getItems())
            if (item.getName().toLowerCase().contains(name.toLowerCase()))
                items.add(item);
        return items;
    }

    /**
     * Gets the trader that has the tradable item id
     *
     * @param id the tradable item id
     * @return the trader
     * @throws TradableItemNotFoundException if the item id is invalid
     */
    public Trader getTraderThatHasTradableItemId(String id) throws TradableItemNotFoundException {
        for (Trader trader : getAllTraders()) {
            if (trader.getAvailableItems().contains(id)) {
                return trader;
            }
        }
        throw new TradableItemNotFoundException("No trader contains this item");
    }

    /**
     * return the 3 most recent traders that this trader has traded with.
     *
     * @param traderId the trader being checked for
     * @return a Trader array list of the 3 most recently traded with Traders
     * @throws AuthorizationException trader is not allowed to know the frequent traders
     * @throws UserNotFoundException  user not found
     * @throws TradeNotFoundException trade not found
     */
    public ArrayList<Trader> getRecentTraders(String traderId) throws AuthorizationException, UserNotFoundException, TradeNotFoundException {
        ArrayList<Trader> traders = new ArrayList<>();
        Trader trader = getTrader(traderId);
        if (trader.isFrozen()) throw new AuthorizationException("Frozen account");
        ArrayList<String> completedTradesIds = trader.getCompletedTrades();
        for (int i = completedTradesIds.size() - 1; i >= Math.max(completedTradesIds.size() - 3, 0); i--) {
            Trade trade = getTrade(completedTradesIds.get(i));
            if (trade.getFirstUserId().equals(traderId)) traders.add((getTrader(trade.getSecondUserId())));
            else traders.add(getTrader(trade.getFirstUserId()));
        }
        return traders;
    }

    /**
     * Return the 3 most frequent traders that this trader has traded with.
     *
     * @param traderId the trader being checked for
     * @return a Trader array list of the 3 most frequently traded with Traders
     * @throws AuthorizationException trader is not allowed to know the frequent traders
     * @throws UserNotFoundException  user not found
     * @throws TradeNotFoundException trade not found
     */
    public ArrayList<Trader> getFrequentTraders(String traderId) throws AuthorizationException, UserNotFoundException, TradeNotFoundException {
        ArrayList<Trader> frequentTraders = new ArrayList<>();
        ArrayList<String> traders = new ArrayList<>();


        for (String tradeId : getTrader(traderId).getCompletedTrades()) {
            Trade trade = getTrade(tradeId);
            if (trade.getFirstUserId().equals(traderId)) traders.add(trade.getSecondUserId());
            else traders.add(trade.getFirstUserId());
        }

        Set<String> distinct = new HashSet<>(traders);
        for (int i = 0; i < 3; i++) {
            int highest = 0;
            if (distinct.size() == 0) {
                break;
            }
            for (String traderID : distinct) {
                int possibleHigh = Collections.frequency(traders, traderID);
                if (possibleHigh > highest) {
                    if (frequentTraders.size() >= i)
                        frequentTraders.add(null);
                    frequentTraders.set(i, getTrader(traderID));
                    highest = possibleHigh;
                }
            }
            distinct.remove(frequentTraders.get(i).getId());
        }
        return frequentTraders;
    }


    /**
     * Gets a list of the items used in trades
     *
     * @param traderId the trader id
     * @return list of tradable items that were recently traded
     * @throws AuthorizationException        trader not allowed to get recently traded items
     * @throws TradeNotFoundException        trade not found
     * @throws UserNotFoundException         trader id is bad
     * @throws TradableItemNotFoundException tradable item is not found
     */
    public ArrayList<TradableItem> getRecentTradeItems(String traderId) throws AuthorizationException, TradeNotFoundException,
            UserNotFoundException, TradableItemNotFoundException {
        Trader trader = getTrader(traderId);
        if (trader.isFrozen()) throw new AuthorizationException("Frozen account");
        ArrayList<String> completedTrades = trader.getCompletedTrades();
        ArrayList<TradableItem> recentTradeItems = new ArrayList<>();
        for (String tradeID : completedTrades) {
            Trade trade = getTrade(tradeID);
            String firstItemId = trade.getFirstUserOffer();
            String secondItemId = trade.getSecondUserOffer();
            try {
                if (!firstItemId.equals(""))
                    recentTradeItems.add(getTradableItem(firstItemId));
                if (!secondItemId.equals(""))
                    recentTradeItems.add(getTradableItem(secondItemId));
            } catch (EntryNotFoundException e) {
                throw new TradableItemNotFoundException();
            }
        }
        return recentTradeItems;
    }

    /**
     * Used for suggesting what items that otherTrader will want from thisTrader based on the wishlist
     *
     * @param thisTraderId  the trader that wants to know what otherTrader will want
     * @param otherTraderId the other trader
     * @return list of items that otherTrader will want
     * @throws UserNotFoundException         if user isn't found
     * @throws AuthorizationException        thisTrader isn't allowed to get suggestions
     * @throws TradableItemNotFoundException if the tradable item isn't found
     */
    public ArrayList<TradableItem> suggestLend(String thisTraderId, String otherTraderId) throws
            UserNotFoundException, AuthorizationException, TradableItemNotFoundException {
        Trader thisTrader = getTrader(thisTraderId);
        if (thisTrader.isFrozen()) throw new AuthorizationException("Frozen account");
        Trader otherTrader = getTrader(otherTraderId);
        ArrayList<TradableItem> suggestions = new ArrayList<>();
        /*
        I think this function should be called suggestBorrow?
        b/c it gets the other users items that this user wishes to have
         */
        for (String wishlistId : thisTrader.getWishlist()) {
            for (String tradableItemId : otherTrader.getAvailableItems()) {
                if (wishlistId.equals(tradableItemId)) {
                    suggestions.add(getTradableItem(wishlistId));
                    break;
                }
            }
        }

        return suggestions;
    }

    /**
     * Suggests all item trades that can be done between two users
     *
     * @param trader1 is the id of this trader
     * @param trader2 is the trader this trader wants to trade with
     * @return a hashmap of the items that can be traded between two users (key is your item to lend, value the item to borrow)
     * @throws UserNotFoundException bad trader ids
     * @throws AuthorizationException can't suggest because user is not a trader or is frozen
     * @throws TradableItemNotFoundException a tradable item wasn't found
     */
    public HashMap<TradableItem, TradableItem> suggestTrade(String trader1, String trader2) throws
            UserNotFoundException, AuthorizationException, TradableItemNotFoundException {
        Trader thisTrader = getTrader(trader1);
        HashMap<TradableItem, TradableItem> suggestedTrades = new HashMap<>();
        if (thisTrader.isFrozen()) throw new AuthorizationException("Frozen account");

        ArrayList<TradableItem> lend = suggestLend(trader1, trader2);
        ArrayList<TradableItem> borrow = suggestLend(trader2, trader1);
        int i = 0;
        while (i < lend.size() && i < borrow.size()) {
            suggestedTrades.put(lend.get(i), borrow.get(i));
            i++;
        }
        return suggestedTrades;
    }

    /**
     * Checks through wishlist of all other traders and returns the most reasonable trade (returns the info necessary to make a trade not an actual trade object)
     * @param itemGet is the item the trader wishes to get
     * @param itemGive is the item the trader wishes to give
     * @param thisTraderId is the id of the trader
     * @return an array with two cells, first cell containing the traders name, second cell containing the item name
     */
    public String[] automatedTradeSuggestion(String thisTraderId, String itemGet, String itemGive) throws UserNotFoundException, AuthorizationException, TradableItemNotFoundException {
        //! note that suggestLend may need to be changed, reason commented in suggestLend
        //and if we do change it then we will also need to change 2 lines on suggestTrade

        ArrayList<Trader> allTraders = getAllTraders();
        Trader thisTrader = getTrader(thisTraderId);
        allTraders.remove(thisTrader); //so it doesn't trade with itself

        //This will return traders with the exact items to trade
        //IDK if this is 100 percent necessary but If i don't include this then it will take longer to find an exact match
        //since it will have to go through a more complex searching algorithm
        for(Trader trader: allTraders){
            HashMap<TradableItem, TradableItem> items = suggestTrade(thisTraderId,trader.getId());
            if(suggestTrade(thisTraderId,trader.getId()) != null){
                TradableItem key = ((TradableItem)items.keySet().toArray()[0]); //gets first key in hashmap
                String[] tradeSuggestion = {trader.getUsername(),items.get(key).getName()};
                return tradeSuggestion;
            }
        }

        //if there are no exact matches then it will search for items with similar names
        int max = 0;
        String mostSimItem = null;
        String mostSimTrader = null;


        for(Trader otherTrader: allTraders){
            //returns the most similar item to the one that the trader wishes to have
            HashMap similarGetItem = similarSearch(itemGet, otherTrader.getAvailableItems());

            //returns the most similar item to the one that the trader wishes to give away
            HashMap similarGiveItem = similarSearch(itemGive, otherTrader.getWishlist());

            //the keys of the hashmap are the item names, the values of the hashmap
            //are how similar it is the the inputted item.
            String keyGet = (String)similarGetItem.keySet().toArray()[0];
            String keyGive = (String)similarGiveItem.keySet().toArray()[0];

            if(((int)similarGiveItem.get(keyGive) + (int)similarGetItem.get(keyGet))> max){
                max = (int)similarGiveItem.get(keyGive) + (int)similarGetItem.get(keyGet);
                mostSimItem = keyGet;
                mostSimTrader = otherTrader.getUsername();
            }
        }
        String[] tradeSuggestion = {mostSimTrader, mostSimItem};
        return tradeSuggestion;

    }

    /**checks how many similarities name has with strings in list
     *
     * @param name is the name of the item we wish to find a similar name of
     * @param list is the list of strings that we are traversing through
     * @return the most similar name in list
     */
    public HashMap<String, Integer> similarSearch(String name, ArrayList<String> list){
            HashMap<String, Integer>  similarNames = new HashMap<>();

            //If this ever gets fully functional it can be used instead of our current queries

        //Goes through all items in list and counts how many of the same chars are in the word
        for(String otherNames: list){
            int maxSim = 0;

            //traverses otherName, on each char of otherName, it will traverse up to
            //name.length() more chars and will see how many chars are the same. It will then store the max amount of same
            //chars that otherName has with name and store that in a hashmap
            for(int i = 0; i < otherNames.length(); i++){
                int similarities = 0;
                int i2 = i;
                int j = 0;
                while(j < name.length() && i2 < otherNames.length()){
                    if(otherNames.charAt(i2) == name.charAt(j))similarities ++;
                    j++;
                    i2++;
                }

                if(similarities > maxSim){
                    maxSim = similarities;
                }
            }
            //hashmap keys represent item names, hashmap values represent similarity score
            similarNames.put(otherNames, maxSim);
        }

        /*
        We may also have to consider size of the string when determining which string is most similar b/c for e.g say we are searching for a name called An,
        then we should return And instead of Andrew however the algorithm currently does not implement this
         */

        //finds highest value in hashmap (which indicates most similar OtherName)
        int max = 0;
        String mostSimilarName = null;
        for (String simName : similarNames.keySet()) {
            int x = similarNames.get(simName); //x is similarity score
            if(x > max){
                max = x;
                mostSimilarName = simName;
            }
        }
        HashMap<String, Integer> simMap = new HashMap<>();
        simMap.put(mostSimilarName, max);
        return simMap;
    }




}
