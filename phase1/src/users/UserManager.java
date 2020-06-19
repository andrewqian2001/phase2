package users;

import main.Manager;

import java.io.IOException;
import java.io.Serializable;

public class UserManager extends Manager<User> implements Serializable {

    public UserManager(String filePath) throws IOException {
        super(filePath);
    }
}
