package frontend.components;

import java.io.IOException;

import backend.exceptions.UserNotFoundException;
import backend.tradesystem.queries.UserQuery;

public class TraderComboBoxItem {
    private UserQuery userQuery = new UserQuery();
    
        final String id;

        public TraderComboBoxItem(String id) throws IOException {
            this.id = id;
        }

        public String toString() {
            try {
                return userQuery.getUsername(id);
            } catch (UserNotFoundException e) {
                e.printStackTrace();
            }
            return "";
        }

        public String getId() {
            return id;
        }
    
}