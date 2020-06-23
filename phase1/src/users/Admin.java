package users;

import java.io.Serializable;

public class Admin extends User implements Serializable {

    /**
     * @return if this user has permission
     */
    public Admin(String name, String password){
        super(name,password);
    }
    public boolean hasPermission() {
        return true;
    }
    public boolean isFrozen(){ return false;}

}
