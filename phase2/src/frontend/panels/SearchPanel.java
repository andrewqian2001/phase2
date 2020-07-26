package frontend.panels;

import javax.swing.*;

import java.awt.*;

import backend.models.users.Trader;
import backend.models.users.User;

public class SearchPanel extends JPanel {

    private JLabel searchTitle;
    private JPanel userListContainer, searchBarContainer;
    private JTextField searchTextField;
    private JButton searchButton;
    private JScrollPane userListScrollPane;
    private Font regular, bold, italic, boldItalic;

    private Color bg = new Color(51, 51, 51);
    private Color current = new Color(159, 159, 159);
    private Color gray = new Color(75, 75, 75);
    private Color gray2 = new Color(196, 196, 196);
    private Color red = new Color(219, 58, 52);
    private Color detailsButton = new Color(142, 142, 142);

    public SearchPanel(User user, Font regular, Font bold, Font italic, Font boldItalic) {

        this.regular = regular;
        this.bold = bold;
        this.italic = italic;
        this.boldItalic = boldItalic;
        
        this.setSize(1000, 900);
        this.setBorder(BorderFactory.createEmptyBorder(30,0,0,0));
        this.setBackground(bg);

        searchTitle = new JLabel("Trader Search");
        searchTitle.setPreferredSize(new Dimension(1200, 75));
        searchTitle.setBackground(bg);
        searchTitle.setForeground(Color.WHITE);
        searchTitle.setFont(regular.deriveFont(35f));

        searchBarContainer = new JPanel();
        searchBarContainer.setLayout(new BoxLayout(this.searchBarContainer, BoxLayout.X_AXIS));
        searchBarContainer.setPreferredSize(new Dimension(1200, 75));
        searchBarContainer.setBackground(bg);

        searchTextField = new JTextField();
        searchTextField.setBackground(gray);
        searchTextField.setBorder(BorderFactory.createEmptyBorder(0, 25, 0, 25));
        searchTextField.setFont(regular.deriveFont(25f));
        searchTextField.setCaretColor(Color.WHITE);
        searchTextField.setForeground(Color.WHITE);
        
        searchButton = new JButton("Search");
        searchButton.setBackground(current);
        searchButton.setForeground(Color.WHITE);
        searchButton.setFont(boldItalic.deriveFont(30f));
        searchButton.setOpaque(true);
        searchButton.setBorder(BorderFactory.createLineBorder(current, 20));
        searchButton.setPreferredSize(new Dimension(200,75));
        searchButton.addActionListener(e -> {
            if(searchTextField.getText().trim().length() > 0) {
                findUsers(searchTextField.getText().trim());
            }
        });
        
        searchBarContainer.add(searchTextField);
        searchBarContainer.add(searchButton);
        
        userListContainer = new JPanel();
        userListContainer.setBackground(gray2);
        userListContainer.setBorder(null);

        userListScrollPane = new JScrollPane();
        userListScrollPane.setPreferredSize(new Dimension(1200, 600));
        userListScrollPane.setViewportView(userListContainer);
        
        this.add(searchTitle);
        this.add(searchBarContainer);
        this.add(userListScrollPane);
    }

    private void findUsers(String username) {
        userListContainer = new JPanel(new GridLayout(username.length(), 1));
        userListContainer.setBackground(gray2);
        userListContainer.setBorder(null);
        for(int i = 0; i < username.length(); i++) {
            JPanel trader = new JPanel(new GridLayout(1,3)); 
            trader.setPreferredSize(new Dimension(1000, 75));
            trader.setBackground(gray2);

            JLabel traderName = new JLabel(username.toLowerCase().substring(0, i + 1));
            traderName.setFont(regular.deriveFont(20f));
            traderName.setForeground(Color.BLACK);
            traderName.setHorizontalAlignment(JLabel.LEFT);
            traderName.setBorder(BorderFactory.createEmptyBorder(0,25,0,0));

            JLabel traderId = new JLabel("<html><pre>#aib-94nmd-823</pre></html>");
            traderId.setFont(regular.deriveFont(20f));
            traderId.setForeground(Color.BLACK);
            traderId.setHorizontalAlignment(JLabel.CENTER);

            JButton traderDetailsButton = new JButton("Details");
            traderDetailsButton.setFont(bold.deriveFont(20f));
            traderDetailsButton.setForeground(Color.WHITE);
            traderDetailsButton.setBackground(detailsButton);
            traderDetailsButton.setOpaque(true);
            traderDetailsButton.setBorder(BorderFactory.createLineBorder(gray2, 15));

            trader.add(traderName);
            trader.add(traderId);
            trader.add(traderDetailsButton);
            userListContainer.add(trader);
        }
        userListScrollPane.setViewportView(userListContainer);
    }
}