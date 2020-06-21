package users;

import main.DatabaseItem;

import java.io.Serializable;

public abstract class User extends DatabaseItem implements Serializable {
    private String username;
    private String password;
    private boolean isFrozen;
    private boolean isUnfrozenRequested;

    /**
     *
     * @return this user's username.
     */
    public String getUsername() {
        return username;
    }

    /**
     *
     * @param username to be set to this user.
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     *
     * @return this user's password
     */
    public String getPassword() {
        return password;
    }

    /**
     *
     * @param password to be set to this user.
     */
    public void setPassword(String password) {
        this.password = password;
    }


    /**
     *
     * @return if this user is frozen.
     */
    public boolean isFrozen(){
        return isFrozen;
    }

    /**
     *
     * @param frozen if this user is now frozen.
     */
    public void setFrozen(boolean frozen) {
        isFrozen = frozen;
    }


    /**
     *
     * @return if this user requested to be unfrozen
     */
    public boolean isUnfrozenRequested() {
        return isUnfrozenRequested;
    }

    /**
     *
     * @param unfrozenRequested if this user requested to be unfrozen
     */
    public void setUnfrozenRequested(boolean unfrozenRequested) {
        isUnfrozenRequested = unfrozenRequested;
    }
}
