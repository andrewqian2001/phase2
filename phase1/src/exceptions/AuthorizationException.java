package exceptions;

/**
 * If a user doesn't have authority
 */
public class AuthorizationException extends Exception {
    public AuthorizationException(String errMsg){
        super(errMsg);
    }
}
