package exceptions;

/**
 * The user entry already exists
 */
public class UserAlreadyExistsException extends Exception {
    public UserAlreadyExistsException(String errMsg){
        super(errMsg);
    }
}