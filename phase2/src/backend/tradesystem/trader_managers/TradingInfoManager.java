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
     * @throws AuthorizationException        trader not allowed to get recently traded items
     * @throws TradeNotFoundException        trade not found
     * @throws UserNotFoundException         trader id is bad
     */
    public ArrayList<String> getRecentTradeItems(String traderId) throws AuthorizationException, TradeNotFoundException,
            UserNotFoundException {
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
     * @param inCity Whether to only search for possible trades within the trader's city
     * @return a list of the best lends that trader thisTraderId can preform
     * @throws UserNotFoundException  if the user can not be found
     * @throws AuthorizationException if the user is frozen
     */
    public String[] suggestLend(String thisTraderId, boolean inCity) throws
            UserNotFoundException, AuthorizationException {
        ArrayList<String[]> lends = suggestLendList(thisTraderId, inCity);
        if (lends.size() == 0){
            return new String[0];
        }
        return lends.get(0);
    }

    /**
     * Returns a list of the best lends that trader thisTraderId can preform
     * The elements in the list is in the format of [thisTraderId, toTraderId, itemId]
     *
     * @param thisTraderId The id of the trader that will be lending the item
     * @param inCity Whether to only search for possible trades within the trader's city
     * @return a list of the best lends that trader thisTraderId can preform
     * @throws UserNotFoundException  if the user can not be found
     * @throws AuthorizationException if the user is frozen
     */
    private ArrayList<String[]> suggestLendList(String thisTraderId, boolean inCity) throws
            UserNotFoundException, AuthorizationException {
        Trader thisTrader = getTrader(thisTraderId);
        if (thisTrader.isFrozen()) throw new AuthorizationException("Frozen account");
        ArrayList<String[]> result = new ArrayList<>();
        HashSet<String> thisTraderItems = new HashSet<>(thisTrader.getAvailableItems());

        ArrayList<String> allTraders = inCity ? getAllTradersInCity(thisTrader.getCity()) : getAllTraders();

        // Get suggested items for all traders
        for (String traderId : allTraders) {
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
     * Returns one of the best trades this trader can perform
     * The elements in the list is in the format of [thisTraderId, toTraderId, itemIdToGive, itemIdToReceive]
     *
     * @param thisTraderId is the id of this trader
     * @param inCity Whether or not to suggest trades only within the trader's city.
     * @return a list of all possible suggested trades (trades where each trader gives an item from the other trader's wishlist)
     * @throws UserNotFoundException  bad trader ids
     * @throws AuthorizationException can't suggest because user is not a trader or is frozen
     */
    public String[] suggestTrade(String thisTraderId, boolean inCity) throws
            UserNotFoundException, AuthorizationException {
        Trader thisTrader = getTrader(thisTraderId);
        if (thisTrader.isFrozen()) throw new AuthorizationException("Frozen account");



        ArrayList<String[]> toLend = suggestLendList(thisTraderId, inCity);

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
     * traverses through the users wishlist, finds the most similar item by name
     *
     * @param thisTraderId      id of this trader
     * @return a arraylist of 2 cell arrays containing the other traders id and the other traders item id [otherTraderId, otherTraderItemId]
     * @throws UserNotFoundException if thisTraderId is a bad id
     * @throws AuthorizationException if thisTraderId isn't a trader
     * @throws TradableItemNotFoundException if the tradable item wasn't found
     */
     public ArrayList<String[]> automatedTradeSuggestion(String thisTraderId, String city, Boolean filterCity) throws UserNotFoundException, AuthorizationException, TradableItemNotFoundException {

        ArrayList<String> allTraders = getAllTraders();
        allTraders.remove(thisTraderId);
        ArrayList<String[]> wishlistTrades = new ArrayList<>();

        // Every item on the users wishlist is excluded from being considered similar

        for(String wishlistItemId: getTrader(thisTraderId).getWishlist()){
            int max = 0;
            String mostSimItemId = null;
            String mostSimTraderId = null;
            for (String otherTraderId : allTraders) {
                Trader otherTrader = getTrader(otherTraderId);
                if(otherTrader.getCity().equals(city) || !filterCity){
                    Object[] similarGetItem = similarSearch(wishlistItemId, otherTrader.getAvailableItems());
                    if(!(similarGetItem == null)){
                        if (((int) similarGetItem[1]) > max) {
                            max = ((int) similarGetItem[1]);
                            mostSimItemId = (String) similarGetItem[0];
                            mostSimTraderId = otherTrader.getId();
                        }
                    }

                }

            }
            if(mostSimItemId != null && mostSimTraderId != null)
            wishlistTrades.add(new String[]{mostSimTraderId, mostSimItemId});
        }


        return wishlistTrades;

    }

    /**
     * checks how many similarities name has with strings in list
     *
     * @param nameId is the id of the item we wish to find a similar name of
     * @param list is the list of strings that we are traversing through
     * @return an array with two cells containing the items name and the score of how similar it is
     */
    public Object[] similarSearch(String nameId, ArrayList<String> list) throws TradableItemNotFoundException, UserNotFoundException, AuthorizationException {

        if (list.size() == 0) {
            return new Object[]{null, 0};
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
         */
        String name;
        if (isListOfTraders) { //this is here to allow similarSearch to work with traders and tradableItems
            name = getTrader(nameId).getUsername();
        } else {
            name = getTradableItem(nameId).getName();
        }

        String partOfSimWord = ""; //this is here to work with the threshold


        for (String otherNamesId : list) {
            String otherNames;
            if (isListOfTraders) {
                otherNames = getTrader(otherNamesId).getUsername();
            } else {
                otherNames = getTradableItem(otherNamesId).getName();
            }
            //we don't want the exact item in the wishlist, b/c that would always be the most similar so if its the same item it skips over it
            if(!otherNamesId.equals(nameId)) {
                int maxSim = 0;
                String[] otherNameWords = otherNames.split("\\s+");
                String[] thisNameWords = name.split("\\s+");


                for(int i = 0; i < otherNameWords.length; i++){//compares every single word in otherWord to every single word in the string we are searching for
                    for(int j = 0; j < thisNameWords.length; j++){

                        String longerName;
                        String shorterName;

                        if(otherNameWords[i].length() < thisNameWords[j].length()){
                            longerName = thisNameWords[j];
                            shorterName = otherNameWords[i];
                        }else{
                            shorterName = thisNameWords[j];
                            longerName = otherNameWords[i];
                        }

                        for(int k = 0; k < longerName.length(); k++) {//Finds the maximum similarity score for each word in list then adds it to similarNames
                            int similarities = 0;
                            int k2 = k;
                            int l = 0;
                            while (l < shorterName.length() && k2 < longerName.length()) {
                                if (Character.toLowerCase(shorterName.charAt(l)) == Character.toLowerCase(longerName.charAt(k2)))
                                    similarities++;
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

                        //EVERYTHING IN THE LOOP BELOW IS JUST FOR TWO TEST CASES, IF YOU HAVE A BETTER WAY OF DOING THIS THAT WOULD BE GREAT
                        int similarities2 = 0;
                        int endOfShortWord = shorterName.length() - 1;
                        int endOfLongWord = longerName.length() - 1;
                        int k = 0;
                        while((k < shorterName.length()) && Character.toLowerCase(shorterName.charAt(k)) == Character.toLowerCase((longerName.charAt(k)))){
                            similarities2++;
                            k++;

                        }
                        while(Character.toLowerCase(shorterName.charAt(endOfShortWord)) == Character.toLowerCase((longerName.charAt(endOfLongWord)))){
                            similarities2++;
                            endOfShortWord--;
                            endOfLongWord--;
                            if(endOfShortWord == 0){
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
                similarNames.add(new Object[]{otherNames,  maxSim, otherNamesId});
            }
        }

        //finds the max similarity score in similarNames
        int max = 0;
        String mostSimilarName = "";
        String mostSimilarNameId = "";
        for(Object[] simNameArr : similarNames) {
            int x = (int) simNameArr[1]; //x is similarity score
            String similarName = (String) simNameArr[0];
            //The reason for the || x== max && .... is b/c
            //say we have name = a, one of the strings in list is an, however another name is andrew
            // both a and andrew would have the same similarity score but an is obviously more similar
            if (x > max || x == max && (Math.abs(similarName.length() - name.length()) < (Math.abs(mostSimilarName.length() - name.length())))) {
                max = x;
                mostSimilarName = similarName;
                mostSimilarNameId = (String) simNameArr[2];
            }
        }

        if(max >= ((int)(name.length()*0.8))){
            return new Object[] {mostSimilarNameId, max};
        }

        return null;
    }



}
