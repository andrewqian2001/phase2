package backend.models;


import java.io.Serializable;
import java.util.Date;

public class Purchase extends DatabaseItem implements Serializable {
    private String BUYER_ID;
    private String SELLER_ID;

    public Purchase (String buyerID, String sellerID) {
        super();
        BUYER_ID = buyerID;
        SELLER_ID = sellerID;
    }

    public String getBUYER_ID() {
        return BUYER_ID;
    }

    public String getSELLER_ID() {
        return SELLER_ID;
    }
}