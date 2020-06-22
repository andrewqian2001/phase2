package users;

import java.io.Serializable;

public class Admin extends User implements Serializable {

    /**
     * @return if this user has permission
     */
    public boolean hasPermission() {
        return true;
    }
}
