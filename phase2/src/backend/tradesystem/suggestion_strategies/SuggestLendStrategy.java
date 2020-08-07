package backend.tradesystem.suggestion_strategies;

import backend.exceptions.AuthorizationException;
import backend.exceptions.UserNotFoundException;

public interface SuggestLendStrategy {

    Suggestion suggestLend(String thisTraderId, boolean inCity) throws UserNotFoundException, AuthorizationException;

}
