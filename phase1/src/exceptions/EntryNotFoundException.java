package exceptions;

/**
 * If the entry requested doesn't exist
 */
public class EntryNotFoundException extends Exception{
    public EntryNotFoundException(String errMsg){
        super(errMsg);
    }
}
