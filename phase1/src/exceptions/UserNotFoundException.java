package exceptions;

public class UserNotFoundException extends EntryNotFoundException {
    public UserNotFoundException(String errMsg){
        super(errMsg);
    }
}