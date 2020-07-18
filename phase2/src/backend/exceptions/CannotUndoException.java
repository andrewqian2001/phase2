package backend.exceptions;

/**
 * If an undo cannot be performed
 */
public class CannotUndoException extends Exception {
    /**
     * New exception without err message
     */
    public CannotUndoException(){super();}

    /**
     * New exception with err message
     * @param msg the err message
     */
    public CannotUndoException(String msg){super(msg);}
}
