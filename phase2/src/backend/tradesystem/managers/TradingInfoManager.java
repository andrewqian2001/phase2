package backend.tradesystem.managers;

import backend.exceptions.*;
import backend.models.TradableItem;
import backend.models.Trade;
import backend.models.users.Trader;
import backend.models.users.User;
import backend.tradesystem.TradeBuilder;

import java.io.IOException;
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
        for (Trader trader : getAllTraders()) {
            for (String id : trader.getAvailableItems()) {
                try {
                    TradableItem item = getTradableItem(id);
                    if (item.getName().toLowerCase().contains(name.toLowerCase()))
                        items.add(item);
                } catch (TradableItemNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
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
        throw new TradableItemNotFoundException();
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
     * Returns a list of the best lends that trader thisTraderId can preform
     *
     * @param thisTraderId The id of the trader that will be lending the item
     * @return a list of the best lends that trader thisTraderId can preform
     * @throws UserNotFoundException  if the user can not be found
     * @throws AuthorizationException if the user is frozen
     */
    public ArrayList<Trade> suggestLend(String thisTraderId) throws
            UserNotFoundException, AuthorizationException {
        Trader thisTrader = getTrader(thisTraderId);
        if (thisTrader.isFrozen()) throw new AuthorizationException("Frozen account");

        ArrayList<Trade> result = new ArrayList<>();
        HashMap<String, Boolean> thisTraderItems = new HashMap<>();
        HashMap<String, ArrayList<String>> suggestions = new HashMap<>(); // trader id to list of suggestions

        // Store possible suggestions in a hashmap for better time complexity
        for (String item : thisTrader.getAvailableItems()) {
            thisTraderItems.put(item, true);
        }

        // Get suggested items for all traders
        for (Trader trader : getAllTradersInCity(thisTrader.getCity())) {
            if (trader.getId() == thisTraderId) {
                continue;
            }
            for (String item : trader.getWishlist()) {
                if (thisTraderItems.getOrDefault(item, false)) {

                    TradeBuilder builder = new TradeBuilder();
                    builder.fromUser(thisTraderId);
                    builder.toUser(trader.getId());
                    builder.setFirstUserOffer(item);
                    builder.setMeeting1(new Date(System.currentTimeMillis() + 100000));
                    builder.setMeeting2(new Date(System.currentTimeMillis() + 1000000));
                    builder.setAllowedEditsPerUser(3);
                    builder.setLocation(thisTrader.getCity());
                    result.add(builder.createTrade());

                }
            }
        }

        return result;
    }

    /**
     * Suggests all item trades that can be done between two users
     *
     * @param thisTraderId is the id of this trader
     * @return a list of all possible suggested trades (trades where each trader gives an item from the other trader's wishlist)
     * @throws UserNotFoundException  bad trader ids
     * @throws AuthorizationException can't suggest because user is not a trader or is frozen
     */
    public ArrayList<Trade> suggestTrade(String thisTraderId) throws
            UserNotFoundException, AuthorizationException {
        Trader thisTrader = getTrader(thisTraderId);
        ArrayList<Trade> suggestedTrades = new ArrayList<>();
        if (thisTrader.isFrozen()) throw new AuthorizationException("Frozen account");

        ArrayList<Trade> toLend = suggestLend(thisTraderId);

        HashMap<String, Boolean> thisTraderWishlist = new HashMap<>();
        // Store wishlist items in a hashmap for better time complexity
        for (String item : thisTrader.getAvailableItems()) {
            thisTraderWishlist.put(item, true);
        }

        // Create trades where both traders give an item that is in each other's wish list
        for (Trade lendTrade : toLend) {
            for (String candidateItem : getTrader(lendTrade.getSecondUserId()).getAvailableItems()) {
                if (thisTraderWishlist.getOrDefault(candidateItem, false)) {
                    TradeBuilder builder = new TradeBuilder();
                    builder.fromUser(thisTraderId);
                    builder.toUser(lendTrade.getSecondUserId());
                    builder.setFirstUserOffer(lendTrade.getFirstUserOffer());
                    builder.setSecondUserOffer(candidateItem);
                    builder.setMeeting1(new Date(System.currentTimeMillis() + 100000));
                    builder.setMeeting2(new Date(System.currentTimeMillis() + 1000000));
                    builder.setAllowedEditsPerUser(3);
                    builder.setLocation(thisTrader.getCity());
                    suggestedTrades.add(builder.createTrade());
                }
            }
        }

        return suggestedTrades;
    }

    /**
     * trader inputs an item they want to trade then it gets an array of most reasonable trades, with every
     * item in the this traders wishlist
     *
     * @param thisTraderId      is the id of the trader
     * @param itemToLend        is the name of the item that the trader wants to lend from the traders inventory
     * @param meetingTime       is the time of the first meeting
     * @param secondMeetingTime is the time of the second meeting
     * @param location          is the location of the meetings
     * @param allowedEdits      is the max num of edits the traders can max
     * @param message
     * @return An array of trades(the most reasonable trades with every item in the users wishlist)
     * @throws UserNotFoundException
     * @throws AuthorizationException
     * @throws TradableItemNotFoundException
     */
    public ArrayList<Trade> automatedTradeSuggestion(String thisTraderId, String itemToLend, Date meetingTime, Date secondMeetingTime, String location, int allowedEdits, String message) throws UserNotFoundException, AuthorizationException, TradableItemNotFoundException {

        ArrayList<Trader> allTraders = getAllTraders();
        ArrayList<Trade> suggestedTrades = new ArrayList<>();
        Trader thisTrader = getTrader(thisTraderId);
        allTraders.remove(thisTrader); //so it doesn't trade with itself

        //goes through this traders wishlist and creates the most reasonable trades with other traders
        for (String wishlistItemIds : thisTrader.getWishlist()) {
            String wishlistItemName = getTradableItem(wishlistItemIds).getName();
            int max = 0;
            String mostSimItem = null;
            String mostSimTraderId = null;

            for (Trader otherTrader : allTraders) {

                //returns the most similar item to the one that the trader wishes to have
                Object[] similarGetItem = similarSearch(wishlistItemName, otherTrader.getAvailableItems());

                //returns the most similar item to the one that the trader wishes to give away
                Object[] similarGiveItem = similarSearch(itemToLend, otherTrader.getWishlist());

                if (((int) similarGetItem[1] + (int) similarGiveItem[1]) > max) {
                    max = ((int) similarGetItem[1] + (int) similarGetItem[1]);
                    mostSimItem = (String) similarGetItem[0];
                    mostSimTraderId = otherTrader.getId();
                }
            }
            Trade trade = new Trade(thisTraderId, mostSimTraderId, meetingTime, secondMeetingTime, location, itemToLend, mostSimItem, allowedEdits, message);
            suggestedTrades.add(trade);
        }
        return suggestedTrades;

    }


    /**
     * checks how many similarities name has with strings in list
     *
     * @param name is the name of the item we wish to find a similar name of
     * @param list is the list of strings that we are traversing through
     * @return an array with two cells containing the items name and the score of how similar it is
     */
    private Object[] similarSearch(String name, ArrayList<String> list) {

        ArrayList<Object[]> similarNames = new ArrayList<>();

        //Can accurately work with different letters and different number of words however missing letters
        // will prob cause a problem

        //Goes through all items in list and counts how many of the same chars are in the word
        for (String otherNames : list) {
            int maxSim = 0;
            //Finds the maximum similarity score for each word in list then adds it to similarNames
            for (int i = 0; i < otherNames.length(); i++) {
                int similarities = 0;
                int i2 = i;
                int j = 0;
                while (j < name.length() && i2 < otherNames.length()) {
                    if (otherNames.charAt(i2) == name.charAt(j)) similarities++;
                    j++;
                    i2++;
                }

                if (similarities > maxSim) {
                    maxSim = similarities;
                }
            }
            //Note similarNames contains the max similarity score for each String in list, not
            //then we need to find the max of those similarity scores and return it
            similarNames.add(new Object[]{otherNames, maxSim});

        }

        /*
        We may also have to consider size of the string when determining which string is most similar b/c for e.g say we are searching for a name called An,
        then we should return And instead of Andrew however the algorithm currently does not implement this, can
        just add another condition on (x>max)

        We also have to consider when there is a missing char in the string, rn it can work with
        misspelled strings but if a char is missing the search wont be as accurate
        for eg if we are looking for apple and we have aple the similarity score should be
        4 but it will prob be 2
         */

        //finds the max similarity score in similarNames
        int max = 0;
        String mostSimilarName = null;
        for (Object[] simNameArr : similarNames) {
            int x = (int)simNameArr[1]; //x is similarity score
            String similarName = (String)simNameArr[0];

            //The reason for the || x== max && .... is b/c
            //say we have name = a, one of the strings in list is an, however another name is andrew
            // both a and andrew would have the same similarity score but an is obviously better
            if (x > max || x == max && (Math.abs(similarName.length()-name.length()) < (Math.abs(mostSimilarName.length()-name.length())))){
                max = x;
                mostSimilarName = similarName;
            }
        }
        Object[] arr = {mostSimilarName, max};
        return arr;
    }


}
