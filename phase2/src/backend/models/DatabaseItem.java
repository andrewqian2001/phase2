package backend.models;

import java.io.Serializable;
import java.util.UUID;

/**
 * Provides for a way to keep track of all items with each having a unique id
 */
public abstract class DatabaseItem implements Serializable {
    private final String thisId;

    /**
     * Generates random id
     */
    public DatabaseItem() {
        this.thisId = UUID.randomUUID().toString();
    }

    /**
     * Gets the id of the item
     * @return the id of this item
     */
    public String getId() {
        return thisId;

    }

}
