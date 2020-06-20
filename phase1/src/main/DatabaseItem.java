package main;

import java.io.Serializable;
import java.util.UUID;

/**
 * Provides for a way to keep track of all items with each having a unique id
 */
public abstract class DatabaseItem implements Serializable {
    private String thisId;

    public DatabaseItem()   {
        this.thisId = UUID.randomUUID().toString();
    }

    /**
     * @return the id of this item
     */
    public String getId() {
        return thisId;

    }

}
