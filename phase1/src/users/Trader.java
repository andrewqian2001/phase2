package users;

import tradableitems.TradableItem;

import java.io.Serializable;
import java.util.ArrayList;

public class Trader extends User implements Serializable {

    ArrayList<String> Wishlist = new ArrayList<String>(); // I assume TradableItems ID are strings?
    ArrayList<String> Inventory = new ArrayList<String>();
    ArrayList<String> UnconfirmedInv = new ArrayList<String>();
    ArrayList<String> AcceptedTrades = new ArrayList<String>();
    ArrayList<String> RequestedTrades = new ArrayList<String>();
    int TradeLimit;
    int IncompleteTradeLim;
    int TotalItemsBorrowed;
    int TotalItemsLent;

    public Trader(String name, String password){
        super(name,password);
    }

    public ArrayList<String> getWishlist(){
        return Wishlist;
    }
    public void SetWishlist(ArrayList<String> Wishlist){
        this.Wishlist = Wishlist;
    }
    public ArrayList<String> getInventory(){
        return Inventory;
    }
    public void SetInventory(ArrayList<String> Inventory){
        this.Inventory = Inventory;
    }
    public ArrayList<String> getUnconfirmedInv(){
        return UnconfirmedInv;
    }
    public void SetUnconfirmedInv(ArrayList<String> UnconfirmedInv){
        this.UnconfirmedInv = UnconfirmedInv;
    }
    public ArrayList<String> getAcceptedTrades(){
        return AcceptedTrades;
    }
    public void SetAcceptedTrades(ArrayList<String> AcceptedTrades){
        this.AcceptedTrades = AcceptedTrades;
    }
    public ArrayList<String> GetRequestedTrades(){
        return RequestedTrades;
    }
    public void SetRequestedTrades(ArrayList<String> RequestedTrades){
        this.RequestedTrades = RequestedTrades;
    }
    public int GetTradeLimit(){
        return TradeLimit;
    }
    public void SetTradeLimit(int TradeLimit){
        this.TradeLimit = TradeLimit;
    }
    public int GetIncompleteTradeLim(){
        return IncompleteTradeLim;
    }
    public void SetIncompleteTradeLim(int IncompleteTradeLim){
        this.IncompleteTradeLim= IncompleteTradeLim;
    }
    public int GetTotalItemsBorrowed(){
        return TotalItemsBorrowed;
    }
    public void SetTotalItemsBorrowed(int TotalItemsBorrowed){
        this.TotalItemsBorrowed= TotalItemsBorrowed;
    }
    public int GetTotalItemsLent(){
        return TotalItemsLent;
    }
    public void SetTotalItemsLent(int TotalItemsLent){
        this.TotalItemsLent = TotalItemsLent;
    }







}
