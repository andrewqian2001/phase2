package backend.models.users;

import java.io.Serializable;

/**
 * Represents an admin
 */
public class Admin extends User implements Serializable{

    /**
     * Constructs an admin with a given username and password.
     *
     * @param username the user's username.
     * @param password the user's password.
     */
    public Admin(String username, String password){
        super(username,password);
    }


    /**
     * if the user is frozen (always false since admins do not participate in trades)
     * @return if the user is frozen (always false since admins do not participate in trades)
     */
    public boolean isFrozen() {
        return false;
    }

}
