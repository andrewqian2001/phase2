package backend.tradesystem.trader_managers;

import backend.exceptions.*;
import backend.models.TradableItem;
import backend.models.Trade;
import backend.models.users.Trader;
import backend.tradesystem.Manager;

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
     * Gets all the trader ids in the database
     *
     * @return all the traders in the database
     */
    public ArrayList<String> getAllTraders() {
        ArrayList<String> allTraders = new ArrayList<>();
        for (String userId : getUserDatabase().getItems().keySet()) {
            try {
                if (getUser(userId)  instanceof Trader)
                    allTraders.add(userId);
            } catch (UserNotFoundException e) {
                e.printStackTrace();
            }
        }
        return allTraders;
    }

    /**
     * Return trader ids that contain name
     *
     * @param name the string to search for
     * @return traders with similar names
     */
    public ArrayList<String> searchTrader(String name) {
        ArrayList<String> similarTraders = new ArrayList<>();
        for (String userId : getUserDatabase().getItems().keySet()) {
            try {
                if (getUser(userId) instanceof Trader && getUser(userId).getUsername().toLowerCase().contains(name.toLowerCase()))
                    similarTraders.add(userId);
            } catch (UserNotFoundException e) {
                e.printStackTrace();
            }
        }
        return similarTraders;
    }

    /**
     * Gets all the trader ids within the same city
     *
     * @param city the city name
     * @return list of all traders within the same city
     */
    public ArrayList<String> getAllTradersInCity(String city) {
        ArrayList<String> allTraders = new ArrayList<>();
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
     * Gets tradable item ids that has the name substring within the input name string
     * For example, if the item name is "Apple Pie", and the name to check for is "apple",
     * then that TradableItem is included as a list of items to return
     *
     * @param name the name to check for
     * @return list of tradable item ids that match the name
     */
    public ArrayList<String> getTradableItemsWithName(String name) {
        ArrayList<String> items = new ArrayList<>();
        for (String userId : getUserDatabase().getItems().keySet()) {
            try {
                if (getUser(userId)  instanceof Trader) {
                    for (String id : ((Trader) getUser(userId) ).getAvailableItems()) {
                        try {
                            TradableItem item = getTradableItem(id);
                            if (item.getName().toLowerCase().contains(name.toLowerCase()))
                                items.add(item.getId());
                        } catch (TradableItemNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                }
            } catch (UserNotFoundException e) {
                e.printStackTrace();
            }
        }
        return items;
    }

    /**
     * Gets the trader that has the tradable item id
     *
     * @param id the tradable item id
     * @return the trader id
     * @throws TradableItemNotFoundException if the item id is invalid
     */
    public String getTraderThatHasTradableItemId(String id) throws TradableItemNotFoundException {
        for (String userId : getUserDatabase().getItems().keySet()) {
            try {
                if (getUser(userId)  instanceof Trader) {
                    if (((Trader) getUser(userId) ).getAvailableItems().contains(id)) {
                        return userId;
                    }
                }
            } catch (UserNotFoundException e) {
                e.printStackTrace();
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
    public ArrayList<String> getFrequentTraders(String traderId) throws AuthorizationException, UserNotFoundException, TradeNotFoundException {
        ArrayList<String> frequentTraders = new ArrayList<>();
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
                    frequentTraders.set(i, getTrader(traderID).getId());
                    highest = possibleHigh;
                }
            }
            distinct.remove(frequentTraders.get(i));
        }
        return frequentTraders;
    }


    /**
     * Gets a list of the items used in trades
     *
     * @param traderId the trader id
     * @return list of tradable items ids that were recently traded
     * @throws AuthorizationException        trader not allowed to get recently traded items
     * @throws TradeNotFoundException        trade not found
     * @throws UserNotFoundException         trader id is bad
     * @throws TradableItemNotFoundException tradable item is not found
     */
    public ArrayList<String> getRecentTradeItems(String traderId) throws AuthorizationException, TradeNotFoundException,
            UserNotFoundException, TradableItemNotFoundException {
        Trader trader = getTrader(traderId);
        if (trader.isFrozen()) throw new AuthorizationException("Frozen account");
        ArrayList<String> completedTrades = trader.getCompletedTrades();
        ArrayList<String> recentTradeItems = new ArrayList<>();
        for (String tradeID : completedTrades) {
            Trade trade = getTrade(tradeID);
            String firstItemId = trade.getFirstUserOffer();
            String secondItemId = trade.getSecondUserOffer();
            try {
                if (!firstItemId.equals(""))
                    recentTradeItems.add(getTradableItem(firstItemId).getId());
                if (!secondItemId.equals(""))
                    recentTradeItems.add(getTradableItem(secondItemId).getId());
            } catch (EntryNotFoundException e) {
                throw new TradableItemNotFoundException();
            }
        }
        return recentTradeItems;
    }

    /**
     * Returns a list of the best lends that trader thisTraderId can preform
     * The elements in the list is in the format of [thisTraderId, toTraderId, itemId]
     *
     * @param thisTraderId The id of the trader that will be lending the item
     * @return a list of the best lends that trader thisTraderId can preform
     * @throws UserNotFoundException  if the user can not be found
     * @throws AuthorizationException if the user is frozen
     */
    public ArrayList<String[]> suggestLend(String thisTraderId) throws
            UserNotFoundException, AuthorizationException {
        Trader thisTrader = getTrader(thisTraderId);
        if (thisTrader.isFrozen()) throw new AuthorizationException("Frozen account");
        ArrayList<String[]> result = new ArrayList<>();
        HashSet<String> thisTraderItems = new HashSet<>(thisTrader.getAvailableItems());
        // Get suggested items for all traders
        for (String traderId : getAllTradersInCity(thisTrader.getCity())) {
            if (traderId.equals(thisTraderId)) {
                continue;
            }
            Trader trader =  getTrader(traderId);
            for (String item : trader.getWishlist()) {
                if (thisTraderItems.contains(item)) {
                    String[] items = {thisTraderId, traderId, item};
                    result.add(items);
                }
            }
        }

        return result;
    }

    /**
     * Returns a list of the best lends that trader thisTraderId can preform
     * The elements in the list is in the format of [thisTraderId, toTraderId, itemIdToGive, itemIdToReceive]
     *
     * @param thisTraderId is the id of this trader
     * @return a list of all possible suggested trades (trades where each trader gives an item from the other trader's wishlist)
     * @throws UserNotFoundException  bad trader ids
     * @throws AuthorizationException can't suggest because user is not a trader or is frozen
     */
    public ArrayList<String[]> suggestTrade(String thisTraderId) throws
            UserNotFoundException, AuthorizationException {
        Trader thisTrader = getTrader(thisTraderId);
        ArrayList<String[]> suggestedTrades = new ArrayList<>();
        if (thisTrader.isFrozen()) throw new AuthorizationException("Frozen account");

        ArrayList<String[]> toLend = suggestLend(thisTraderId);

        HashSet<String> thisTraderWishlist = new HashSet<>(thisTrader.getWishlist());

        // Create trades where both traders give an item that is in each other's wish list
        for (String[] lendInfo : toLend) {
            for (String candidateItem : getTrader(lendInfo[1]).getAvailableItems()) {
                if (thisTraderWishlist.contains(candidateItem)) {
                    String[] items = {lendInfo[0], lendInfo[1], lendInfo[2], candidateItem};
                    suggestedTrades.add(items);
                }
            }
        }
        return suggestedTrades;
    }



    /**
     * The user types in a item that is wanted, the function finds the most similar item and then returns a trade
     *
     * @param thisTraderId      is the id of this trader
     * @param itemToBorrow      is the string that the user types in which represents the item the user wants
     * @param itemToLend        is the item from the users inventory that is going to be given
     * @param meetingTime       is the meeting time
     * @param secondMeetingTime is the second meeting time
     * @param location          ois the location of the meeting
     * @param allowedEdits      is the max number of edits the traders can make
     * @param message
     * @return the most reasonable trade between this user and another trader in the database
     * @throws UserNotFoundException
     * @throws AuthorizationException
     * @throws TradableItemNotFoundException
     */
     public Trade automatedTradeSuggestion(String thisTraderId, String itemToBorrow, String itemToLend, Date meetingTime, Date secondMeetingTime, String location, int allowedEdits, String message) throws UserNotFoundException, AuthorizationException, TradableItemNotFoundException {

        //Note that the item this trader will lend is for sure going to be itemToLend, however
        //the item you borrow is the one most similar to itemToBorrow

        ArrayList<String> allTraders = getAllTraders();
        allTraders.remove(thisTraderId);

        int max = 0;
        String mostSimItem = null;
        String mostSimTraderId = null;

        for (String otherTraderId : allTraders) {
            //returns the most similar item to the one that the trader wishes to have
            Trader otherTrader = getTrader(otherTraderId);
            Object[] similarGetItem = similarSearch(itemToBorrow, otherTrader.getAvailableItems());

            //returns the most similar item to the one that the trader wishes to give away
            Object[] similarGiveItem = similarSearch(itemToLend, otherTrader.getWishlist());

            if (((int) similarGetItem[1] + (int) similarGiveItem[1]) > max) {

                max = ((int) similarGetItem[1] + (int) similarGiveItem[1]);
                mostSimItem = (String) similarGetItem[0];
                mostSimTraderId = otherTrader.getId();

            }
        }
        Trade trade = new Trade(thisTraderId, mostSimTraderId, meetingTime, secondMeetingTime, location, itemToLend, mostSimItem, allowedEdits, message);
        return trade;

    }

    /**
     * checks how many similarities name has with strings in list
     *
     * @param name is the name of the item we wish to find a similar name of
     * @param list is the list of strings that we are traversing through
     * @return an array with two cells containing the items name and the score of how similar it is
     */
    public Object[] similarSearch(String name, ArrayList<String> list) throws TradableItemNotFoundException, UserNotFoundException, AuthorizationException {

        if (list.size() == 0) {
            return new Object[]{"", 0};
        }
        ArrayList<Object[]> similarNames = new ArrayList<>();

        //This is to check what type of list the parameter list is so that this function can work with traders
        // and tradable items
        boolean isListOfTraders = false;
        for (String traderIds : getAllTraders()) {
            if (traderIds.equals(list.get(0))) isListOfTraders = true;
        }

        /*
        Goes through all items in list and finds similarity score
        The score is calculated like this, for every char in otherNames, we traverse name.length() more chars
        and find how many match, then store the max number of char matches so that we have the max matches for every otherName
        in the list. We put that into an array and find the otherName with the highest number of matches which is the most similar string

        Current problems with this algorithm:
        1. (solved) when length of name > length of otherNames (e.g name = red hat, otherName = hat)
        2. when a char is added or a char is missing to the strings
         */
        for (String otherNamesId : list) {

            String otherNames;
            if (isListOfTraders) { //this is here to allow similarSearch to work with traders and tradableItems
                otherNames = getTrader(otherNamesId).getUsername();
            } else {
                otherNames = getTradableItem(otherNamesId).getName();
            }

            //the solution for problem 1.
            String longerName = otherNames;
            String shorterName = name;
            if (otherNames.length() < name.length()) {
                longerName = name;
                shorterName = otherNames;
            }

            int maxSim = 0;
            //Finds the maximum similarity score for each word in list then adds it to similarNames
            for (int i = 0; i < longerName.length(); i++) {
                int similarities = 0;
                int i2 = i;
                int j = 0;

                while (j < shorterName.length() && i2 < longerName.length()) {
                    if (Character.toLowerCase(shorterName.charAt(j)) == Character.toLowerCase(longerName.charAt(i2)))
                        similarities++;
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

        //finds the max similarity score in similarNames
        int max = 0;
        String mostSimilarName = "";
        for (Object[] simNameArr : similarNames) {
            int x = (int) simNameArr[1]; //x is similarity score
            String similarName = (String) simNameArr[0];
            //The reason for the || x== max && .... is b/c
            //say we have name = a, one of the strings in list is an, however another name is andrew
            // both a and andrew would have the same similarity score but an is obviously more similar
            if (x > max || x == max && (Math.abs(similarName.length() - name.length()) < (Math.abs(mostSimilarName.length() - name.length())))) {
                max = x;
                mostSimilarName = similarName;

            }
        }

        Object[] arr = {mostSimilarName, max};
        return arr;
    }


}
