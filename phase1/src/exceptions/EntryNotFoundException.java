package exceptions;

public class EntryNotFoundException extends Exception{
    public EntryNotFoundException(String errMsg){
        super(errMsg);
    }
}
