package users;

import java.io.IOException;

public class TraderManager extends UserManager {

    /**
     * For storing the file path of the .ser file
     *
     * @param filePath must take in .ser file
     * @throws IOException if creating a file causes issues
     */
    public TraderManager(String filePath) throws IOException {
        super(filePath);
    }


    public void acceptTrade(String userId, String tradeId){

    }

    public void denyTrade(String userId, String tradeId){

    }

    public void addRequestItem(String userId, String itemId){

    }

    public void acceptRequestItem(String userId, String itemId){

    }

    public void borrowItem(String user1, String user2, String itemId){

    }

    public void lendItem(String user1, String user2, String itemId){

    }

    public void trade(String user1, String item1, String user2, String item2){

    }



    //
}
