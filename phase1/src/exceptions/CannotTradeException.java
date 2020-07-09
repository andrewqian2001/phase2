package exceptions;

/**
 * If the trade cannot happen
 */
public class CannotTradeException extends Exception {
    public CannotTradeException(String msg) {
        super(msg);
    }
}
