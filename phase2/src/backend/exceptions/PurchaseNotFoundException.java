package backend.exceptions;

public class PurchaseNotFoundException extends EntryNotFoundException{
    /**
     * Making an exception when the trade id entry isn't found
     * @param id the trade id that wasn't found
     */
    public PurchaseNotFoundException(String id){
        super(id, "The purchase " + id + " was not found.");
    }
}
