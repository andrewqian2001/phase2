package backend.tradesystem.suggestion_strategies;

import backend.exceptions.AuthorizationException;
import backend.exceptions.UserNotFoundException;
import backend.models.Suggestion;

public interface SuggestTradeStrategy {

    Suggestion suggestTrade(String thisTraderId, boolean inCity) throws UserNotFoundException, AuthorizationException;
}
