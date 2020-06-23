package users;

import java.io.Serializable;

public class Admin extends User implements Serializable {

    public Admin (String username, String password) {
        super(username, password);
    }

    /**
     * @return if this user has permission
     */
    public boolean hasPermission() {
        return true;
    }

    /**
     * @return if the user is frozen (always false since admins do not participate in trades)
     */
    public boolean isFrozen() {
        return false;
    }
}
