package backend.tradesystem.suggestion_strategies;

import backend.exceptions.AuthorizationException;
import backend.exceptions.UserNotFoundException;

/**
 * Interface which has a suggestTrade method that represents a suggestTrade algorithm
 */
public interface SuggestTradeStrategy {

    /**
     * Suggests a trade where thisTraderId is offering some item to another trader.
     *
     * @param thisTraderId the id of the trader asking for the suggestion
     * @param inCity       true if you desire to search for other traders within the same city as the original trader
     * @return A trade suggestion in the form of [fromUserId, toUserId, lendItemId, receiveItemId]
     * @throws UserNotFoundException  if a user was not found
     * @throws AuthorizationException if the given trader id represents a non-trader object.
     */
    String[] suggestTrade(String thisTraderId, boolean inCity) throws UserNotFoundException, AuthorizationException;
}
