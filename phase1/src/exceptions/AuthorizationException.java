package exceptions;

public class AuthorizationException extends Exception {
    public AuthorizationException(String errMsg){
        super(errMsg);
    }
}
