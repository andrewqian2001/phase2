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
    public List<String> getAllTraders() {
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
     * Return trader ids that contain name
     *
     * @param name the string to search for
     * @return traders with similar names
     */
    public List<String> searchTrader(String name) {
        List<String> similarTraders = new ArrayList<>();
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
    public List<String> getAllTradersInCity(String city) {
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
     * Gets tradable item ids that has the name substring within the input name string
     * For example, if the item name is "Apple Pie", and the name to check for is "apple",
     * then that TradableItem is included as a list of items to return
     *
     * @param name the name to check for
     * @return list of tradable item ids that match the name
     */
    public List<String> getTradableItemsWithName(String name) {
        List<String> items = new ArrayList<>();
        for (String userId : getUserDatabase().getItems().keySet()) {
            try {
                if (getUser(userId) instanceof Trader) {
                    for (String id : ((Trader) getUser(userId)).getAvailableItems()) {
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
                if (getUser(userId) instanceof Trader) {
                    if (((Trader) getUser(userId)).getAvailableItems().contains(id)) {
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
    public List<String> getFrequentTraders(String traderId) throws AuthorizationException, UserNotFoundException, TradeNotFoundException {
        List<String> frequentTraders = new ArrayList<>();
        List<String> traders = new ArrayList<>();


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
                    frequentTraders.set(i, traderID);
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
     * @throws AuthorizationException trader not allowed to get recently traded items
     * @throws TradeNotFoundException trade not found
     * @throws UserNotFoundException  trader id is bad
     */
    public List<String> getRecentTradeItems(String traderId) throws AuthorizationException, TradeNotFoundException,
            UserNotFoundException {
        Trader trader = getTrader(traderId);
        if (trader.isFrozen()) throw new AuthorizationException("Frozen account");
        List<String> completedTrades = trader.getCompletedTrades();
        List<String> recentTradeItems = new ArrayList<>();
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
                e.printStackTrace();
            }
        }
        return recentTradeItems;
    }

    /**
     * Returns one of the best lends that trader thisTraderId can preform
     * The elements in the list is in the format of [thisTraderId, toTraderId, itemId]
     *
     * @param thisTraderId The id of the trader that will be lending the item
     * @param inCity       Whether to only search for possible trades within the trader's city
     * @return one of the best lends that trader thisTraderId can preform
     * @throws UserNotFoundException  if the user can not be found
     * @throws AuthorizationException if the user is frozen
     */
    public String[] suggestLend(String thisTraderId, boolean inCity) throws
            UserNotFoundException, AuthorizationException {
        List<String[]> lends = suggestLendList(thisTraderId, inCity);
        if (lends.size() == 0) {
            return new String[0];
        }
        return lends.get(0);
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

    /**
     * Returns one of the best trades this trader can perform
     * The elements in the list is in the format of [thisTraderId, toTraderId, itemIdToGive, itemIdToReceive]
     *
     * @param thisTraderId is the id of this trader
     * @param inCity       Whether or not to suggest trades only within the trader's city.
     * @return a single possible suggested trade (a trade where each trader gives an item from the other trader's wishlist)
     * @throws UserNotFoundException  bad trader ids
     * @throws AuthorizationException can't suggest because user is not a trader or is frozen
     */
    public String[] suggestTrade(String thisTraderId, boolean inCity) throws
            UserNotFoundException, AuthorizationException {
        Trader thisTrader = getTrader(thisTraderId);
        if (thisTrader.isFrozen()) throw new AuthorizationException("Frozen account");

        List<String[]> toLend = suggestLendList(thisTraderId, inCity);

        HashSet<String> thisTraderWishlist = new HashSet<>(thisTrader.getWishlist());

        // Create trades where both traders give an item that is in each other's wish list
        for (String[] lendInfo : toLend) {
            for (String candidateItem : getTrader(lendInfo[1]).getAvailableItems()) {
                if (thisTraderWishlist.contains(candidateItem)) {
                    return new String[]{lendInfo[0], lendInfo[1], lendInfo[2], candidateItem};
                }
            }
        }
        return new String[]{};
    }


    /**
     * Gives the best trade by taking the trader's wishlist items and their names.
     * Returns string array in this format [thisTraderId, mostSimTraderId, mostSimGiveItemId, mostSimGetItemId]
     *
     * @param thisTraderId id of this trader
     * @param filterCity   is if the trader wants to filter for city
     * @return an array with this traders id, the other traders id, the item this trader will give, the item this trader will get
     * @throws UserNotFoundException  if thisTraderId is a bad id
     * @throws AuthorizationException if thisTraderId isn't a trader
     */
    public String[] automatedTradeSuggestion(String thisTraderId, boolean filterCity) throws UserNotFoundException, AuthorizationException {

        //Finds the most similar trade, most similar is calculated through similarSearch

        List<String> allTraders = getAllTraders();
        allTraders.remove(thisTraderId);
        Trader thisTrader = getTrader(thisTraderId);
        String city = thisTrader.getCity();
        int maxTotalSim = 0;
        String mostSimGetItemId = null;
        String mostSimGiveItemId = null;
        String mostSimTraderId = null;

        for (String otherTraderId : allTraders) {
            Trader otherTrader = getTrader(otherTraderId);
            if (filterCity && !(otherTrader.getCity().equalsIgnoreCase(city))) {
                continue;
            }
            String simGetItemId = null;
            int maxGetSim = 0;
            String simGiveItemId = null;
            int maxGiveSim = 0;

            //finds the item that thisTrader wants the most from otherTrader
            for (String wishlistItemId : thisTrader.getWishlist()) {
                Object[] getItem = null;
                try {
                    getItem = similarSearch(wishlistItemId, otherTrader.getAvailableItems());
                } catch (TradableItemNotFoundException e) {
                    getItem = null;
                } finally {
                    if (!(getItem == null)) {
                        if (((int) getItem[1]) > maxGetSim) {
                            simGetItemId = (String) getItem[0];
                            maxGetSim = ((int) getItem[1]);
                        }
                    }
                }
            }

            //finds the item that otherTrader wants the most from thisTrader
            for (String otherTraderWishlistItemId : otherTrader.getWishlist()) {

                Object[] giveItem = null;
                try {
                    giveItem = similarSearch(otherTraderWishlistItemId, thisTrader.getAvailableItems());
                } catch (TradableItemNotFoundException e) {
                    giveItem = null;
                } finally {
                    if (!(giveItem == null)) {
                        if (((int) giveItem[1]) > maxGiveSim) {
                            simGiveItemId = (String) giveItem[0];

                            maxGiveSim = ((int) giveItem[1]);
                        }
                    }
                }
            }
            if (maxGetSim + maxGiveSim > maxTotalSim && (maxGetSim != 0 && maxGiveSim != 0)) {
                maxTotalSim = maxGetSim + maxGiveSim;
                mostSimGetItemId = simGetItemId;
                mostSimGiveItemId = simGiveItemId;
                mostSimTraderId = otherTraderId;

            }
        }

        if (mostSimTraderId == null || mostSimGetItemId == null || mostSimGiveItemId == null)
            return new String[]{};

        return new String[]{thisTraderId, mostSimTraderId, mostSimGiveItemId, mostSimGetItemId};


    }

    /**
     * checks how many similarities name has with strings in list
     *
     * @param nameId is the id of the item we wish to find a similar name of
     * @param list   is the list of strings that we are traversing through
     * @return an array with two cells containing the items name and the score of how similar it is
     */
    private Object[] similarSearch(String nameId, List<String> list) throws TradableItemNotFoundException, UserNotFoundException, AuthorizationException {

        if (list.size() == 0) {
            return new Object[]{null, 0};
        }
        List<Object[]> similarNames = new ArrayList<>();

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
         */
        String name;
        if (isListOfTraders) { //this is here to allow similarSearch to work with traders and tradableItems
            name = getTrader(nameId).getUsername();
        } else {
            name = getTradableItem(nameId).getName();
        }
        String longestWord = "";
        for (String otherNamesId : list) {
            String otherNames;
            if (isListOfTraders) {
                otherNames = getTrader(otherNamesId).getUsername();
            } else {
                otherNames = getTradableItem(otherNamesId).getName();
            }

            //we don't want the exact item in the wishlist, b/c that would always be the most similar so if its the same item it skips over it
            if (!otherNamesId.equals(nameId)) {
                int maxSim = 0;
                String[] otherNameWords = otherNames.split("\\s+");
                String[] thisNameWords = name.split("\\s+");

                for (int i = 0; i < otherNameWords.length; i++) {//compares every single word in otherWord to every single word in the string we are searching for
                    for (int j = 0; j < thisNameWords.length; j++) {

                        String longerName; //these are needed to fix bug when comparing strings with different sizes
                        String shorterName;

                        if (otherNameWords[i].length() < thisNameWords[j].length()) {
                            longerName = thisNameWords[j];
                            shorterName = otherNameWords[i];
                        } else {
                            shorterName = thisNameWords[j];
                            longerName = otherNameWords[i];
                        }

                        if (longerName.length() > longestWord.length()) { //needed for threshold
                            longestWord = longerName;
                        }

                        for (int k = 0; k < longerName.length(); k++) {//Finds the maximum similarity score for each word in list
                            int similarities = 0;
                            int k2 = k;
                            int l = 0;
                            while (l < shorterName.length() && k2 < longerName.length()) {
                                if (Character.toLowerCase(shorterName.charAt(l)) == Character.toLowerCase(longerName.charAt(k2))) {
                                    similarities++;
                                }
                                l++;
                                k2++;
                            }
                            if (similarities > maxSim) {
                                maxSim = similarities;
                            }
                        }
                        //when you add an extra char(name = apple, otherName = appxle) or subtract an extra char, the above algorithm
                        // does not work properly so we need another algorithm below
                        //ideally if name = apple and otherName = appxle then the similarity score should be 4
                        //if name = apple and other otherName = appe then the similarity score should be 4

                        //THE ENTIRE SECTION BELOW IS FOR THE ABOVE TWO TEST CASES...
                        int similarities2 = 0;
                        int endOfShortWord = shorterName.length() - 1;
                        int endOfLongWord = longerName.length() - 1;
                        int k = 0;
                        while ((k < shorterName.length()) && Character.toLowerCase(shorterName.charAt(k)) == Character.toLowerCase((longerName.charAt(k)))) {
                            similarities2++;
                            k++;
                        }
                        while (Character.toLowerCase(shorterName.charAt(endOfShortWord)) == Character.toLowerCase((longerName.charAt(endOfLongWord)))) {
                            similarities2++;
                            endOfShortWord--;
                            endOfLongWord--;
                            if (endOfShortWord == 0) {
                                break;
                            }
                        }
                        similarities2 = Math.min(shorterName.length(), similarities2); //deals with when both words are the same
                        similarities2 = similarities2 - (longerName.length() - shorterName.length());

                        if (similarities2 > maxSim) {
                            maxSim = similarities2;
                        }
                    }
                }
                similarNames.add(new Object[]{otherNames, maxSim, otherNamesId});
            }
        }

        //finds the max similarity score in similarNames
        int max = 0;
        String mostSimilarName = "";
        String mostSimilarNameId = "";
        for (Object[] simNameArr : similarNames) {
            int x = (int) simNameArr[1];
            String similarName = (String) simNameArr[0];
            if (x > max || x == max && (Math.abs(similarName.length() - name.length()) < (Math.abs(mostSimilarName.length() - name.length())))) {
                max = x;
                mostSimilarName = similarName;
                mostSimilarNameId = (String) simNameArr[2];
            }
        }

        //adds a threshold, so that items we consider not simialr dont get added, even if there is nothing else
        if (max >= ((int) (longestWord.length() * 0.8))) {
            return new Object[]{mostSimilarNameId, max};
        }

        return null;
    }


}
