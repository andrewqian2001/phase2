package backend.tradesystem.managers;

import backend.exceptions.*;
import backend.models.TradableItem;
import backend.models.Trade;
import backend.models.users.Trader;
import backend.models.users.User;

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
     * @param userFilePath the user database file path
     * @param tradableItemFilePath the tradable item database file path
     * @param tradeFilePath the trade database file path
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

    /** return traders that contain name or is similar to name
     *
     * @param name is the name of the trader
     * @return an arraylist of traders with similar names
     */
    public ArrayList<Trader> searchTrader(String name){

        ArrayList<Trader> similarTraders = new ArrayList();
        ArrayList<Trader> allTraders = getAllTraders();
        LinkedList<Trader> one = new LinkedList<>(); //Traders with names that have one char differences from input name
        LinkedList<Trader> two = new LinkedList<>();
        LinkedList<Trader> three = new LinkedList<>();

        for(Trader trader: allTraders){
            String traderName = trader.getUsername();
            if(traderName.contains(name)){
                similarTraders.add(trader);
            }else{ //checks for up to three differences
                for(int i = 0; i <= traderName.length() - name.length(); i++){
                    int differences = 0;
                    int i2 = i;
                    int j = 0;
                    while(j < name.length() && i2 < traderName.length()){
                        if(traderName.charAt(i2) != name.charAt(j)){
                            differences ++;
                            i2++;
                        }
                    }
                    switch(differences){
                        case 1:
                            one.add(trader);
                            break;
                        case 2:
                            two.add(trader);
                            break;
                        case 3:
                            three.add(trader);
                            break;
                    }
                }
            }

        }
        for(Trader trader: one){
            similarTraders.add(trader);
            if(similarTraders.size() > 9)return similarTraders;
        }
        for(Trader trader: two) {
            similarTraders.add(trader);
            if(similarTraders.size() > 9)return similarTraders;
        }
        for(Trader trader: three) {
            similarTraders.add(trader);
            if(similarTraders.size() > 9)return similarTraders;
        }
        return similarTraders;
    }

    /**
     * Gets all the traders within the same city
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
    public TradableItem[] getTradableItemsWithName(String name) {
        ArrayList<TradableItem> items = new ArrayList<>();
        for (TradableItem item : getTradableItemDatabase().getItems())
            if (item.getName().toLowerCase().contains(name.toLowerCase()))
                items.add(item);
        return (TradableItem[]) items.toArray();
    }

    /**
     * Gets the trader that has the tradable item id
     * @param id the tradable item id
     * @return the trader
     * @throws TradableItemNotFoundException if the item id is invalid
     */
    public Trader getTraderThatHasTradableItemId(String id) throws TradableItemNotFoundException{
        for (Trader trader: getAllTraders()){
            if (trader.getAvailableItems().contains(id)){
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
            if (distinct.size() == 0){
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
}
