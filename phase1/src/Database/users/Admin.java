package Database.users;

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
     * @return if the user is frozen (always false since admins do not participate in Database.trades)
     */
    public boolean isFrozen() {
        return false;
    }

}
