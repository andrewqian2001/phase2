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
     * if the user is frozen, admins cannot be frozen so this is always false
     * @return admins cannot be frozen
     */
    @Override
    public boolean isFrozen() {
        return false;
    }

    /**
     * An admin's frozen status is always false
     * @param frozen regardless of input, frozen status is always false
     */
    @Override
    public void setFrozen(boolean frozen){
        this.setFrozen(false);
    }
}
