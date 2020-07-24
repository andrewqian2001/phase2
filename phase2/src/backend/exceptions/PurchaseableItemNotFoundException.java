package backend.exceptions;

public class PurchaseableItemNotFoundException extends EntryNotFoundException{

    /**
     * Making an exception when the purchasable item id entry isn't found
     *
     * @param id the tradable item id that wasn't found
     */
    public PurchaseableItemNotFoundException(String id) {
        super(id, "The purchasable item " + id + " was not found.");
    }

    /**
     * Exception with no msg
     */
    public PurchaseableItemNotFoundException(){
        super();
    }
}
