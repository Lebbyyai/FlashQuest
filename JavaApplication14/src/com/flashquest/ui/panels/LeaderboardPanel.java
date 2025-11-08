package com.flashquest.ui.panels;

import com.flashquest.AppController;
import com.flashquest.services.NetworkClient;
import com.flashquest.ui.Theme;
import com.flashquest.ui.components.AnimatedRoundedButton;
import com.flashquest.ui.components.RoundedPanel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class LeaderboardPanel extends RoundedPanel {

    private NetworkClient networkClient;
    private AppController appController;
    private DefaultTableModel tableModel;
    private JTable leaderboardTable;

    public LeaderboardPanel(NetworkClient client, AppController controller) {
        super(20, Theme.COLOR_CARD_BG); 
        this.networkClient = client;
        this.appController = controller;

        setLayout(new BorderLayout(0, 15));
        setMaximumSize(new Dimension(800, 600)); 
        setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        add(createHeader(), BorderLayout.NORTH);
        add(createTable(), BorderLayout.CENTER);
    }

    private JPanel createHeader() {
        JPanel headerPanel = new JPanel(new BorderLayout(20, 0));
        headerPanel.setOpaque(false);

        AnimatedRoundedButton backButton = new AnimatedRoundedButton(
            "Back", Theme.COLOR_PRIMARY_DARK, Color.WHITE, 15
        );
        backButton.setFont(Theme.FONT_BODY_BOLD);
        backButton.addActionListener(e -> appController.showDashboard());
        headerPanel.add(backButton, BorderLayout.WEST);

        JLabel titleLabel = new JLabel("Leaderboard");
        titleLabel.setFont(Theme.FONT_HEADER_LARGE); 
        titleLabel.setForeground(Theme.COLOR_TEXT_DARK);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        
        JPanel spacer = new JPanel();
        spacer.setOpaque(false);
        spacer.setPreferredSize(backButton.getPreferredSize());
        headerPanel.add(spacer, BorderLayout.EAST);

        return headerPanel;
    }

    private JScrollPane createTable() {
        // Define column names
        String[] columnNames = {"Rank", "Username", "Badges"};
        
        // Create a non-editable table model
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; 
            }
        };

        leaderboardTable = new JTable(tableModel);
        
        // --- Setup Table Appearance ---
        leaderboardTable.setFont(Theme.FONT_BODY);
        leaderboardTable.setRowHeight(30);
        leaderboardTable.getTableHeader().setFont(Theme.FONT_BODY_BOLD);
        leaderboardTable.getTableHeader().setBackground(Theme.COLOR_PRIMARY);
        leaderboardTable.getTableHeader().setForeground(Color.WHITE);
        leaderboardTable.setBackground(Color.WHITE);
        leaderboardTable.setFillsViewportHeight(true); 

        // Center-align text in "Rank" and "Badges" columns
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        leaderboardTable.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        leaderboardTable.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
        
        // Set column widths
        leaderboardTable.getColumnModel().getColumn(0).setMaxWidth(100); // Rank
        leaderboardTable.getColumnModel().getColumn(2).setMaxWidth(150); // Badges

        JScrollPane scrollPane = new JScrollPane(leaderboardTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(Theme.COLOR_BORDER, 2));
        return scrollPane;
    }

    public void loadLeaderboardData() {
        networkClient.requestLeaderboard();
    }

    // --- THIS IS THE FIXED METHOD ---
    public void updateTable(String data) {
        // Clear the table of any old data (like "Could not load data")
        tableModel.setRowCount(0); 
        
        if (data == null || data.isEmpty()) {
            // As requested, if no data, just show a blank table.
            return;
        }

        // Data format: "Rank1,User1,Badges1|Rank2,User2,Badges2"
        String[] users = data.split("\\|");
        
        for (String user : users) {
            String[] parts = user.split(",");
            if (parts.length == 3) {
                tableModel.addRow(new Object[]{parts[0], parts[1], parts[2]});
            }
        }
    }
}