package backend.models;

import java.io.Serializable;

/**
 * Represents giving something a review
 */
public class Review extends DatabaseItem implements Serializable {
    private final String FROM_USER_ID;
    private final String TO_USER_ID;
    private final double RATING;
    private final String MESSAGE;

    /**
     * Makes a new review object
     * @param fromUserId the user that sent the review
     * @param toUserId  the user the review is directed to
     * @param rating the rating of the review
     * @param message the message of the review
     */
    public Review(String fromUserId, String toUserId, double rating, String message) {
        this.FROM_USER_ID = fromUserId;
        this.TO_USER_ID = toUserId;
        this.RATING = rating;
        this.MESSAGE = message;
    }

    /**
     * The rating of the review
     * @return The rating of the review
     */
    public double getRating() {
        return RATING;
    }

    /**
     * The user who sent the review
     * @return user who sent the review
     */
    public String getFromUser() {
        return FROM_USER_ID;
    }

    /**
     * User who received the review
     * @return user who received the review
     */
    public String getToUser() {
        return TO_USER_ID;
    }

    /**
     * Message of the review
     * @return message of the review
     */
    public String getMessage() {
        return MESSAGE;
    }
}
