package com.zetcode;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.FlowLayout;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import java.util.Arrays;

/**
 * Main class for single-player Tetris.
 * It creates and manages the game UI.
 */
public class Tetris extends JFrame {

    // === Color Constants (in sync with TwoPlayerFrame) ===
    private static final Color LIGHT_BLUE = new Color(173, 216, 230);
    private static final Color DARK_BLUE = new Color(0, 102, 153);
    private static final Color DARK_GRAY = new Color(64, 64, 64);
    private static final Color MEDIUM_GRAY = new Color(100, 100, 100);

    // === UI Components ===
    private JLabel statusbar;       // displays score
    private JPanel topPanel;        // top panel holding the back button
    private JButton backButton;     // back button for returning to mode selection
    protected Board board;            // the game board

    // === Game State ===
    protected String username;        // player's name
    protected AudioPlayer audioPlayer;// for background music

    public Tetris(String username) {
        this.username = username;
        initUI();
    }

    private void initUI() {
        createStatusBar();
        createTopPanel();
        createGameBoard();
        startBackgroundMusic();

        setupFrame();
        focusOnBoard();
    }

    private void createStatusBar() {
        statusbar = new JLabel(" 0");
        add(statusbar, BorderLayout.SOUTH);
    }

    private void createTopPanel() {
        // Create a top panel with a left-aligned FlowLayout
        topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        topPanel.setBackground(LIGHT_BLUE);
        topPanel.setOpaque(true);

        // Create the back button
        backButton = new JButton("Back");
        backButton.setFocusable(false);
        backButton.setFont(new Font("Arial", Font.BOLD, 14));
        backButton.setBackground(LIGHT_BLUE);
        backButton.setForeground(DARK_BLUE);
        backButton.setFocusPainted(false);
        backButton.setOpaque(true);
        backButton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(DARK_BLUE, 2, true),
            BorderFactory.createEmptyBorder(5, 15, 5, 15)
        ));
        // Back button action: return to mode selection
        backButton.addActionListener(e -> returnToMainMenu());

        topPanel.add(backButton);
        add(topPanel, BorderLayout.NORTH);
    }

    protected void createGameBoard() {
        board = new Board(this, username);
        add(board);
        board.start();
    }

    protected void startBackgroundMusic() {
        audioPlayer = new AudioPlayer();
        audioPlayer.playRandomLoop(Arrays.asList(
            "resources/single_player_music1.wav",
            "resources/single_player_music2.wav",
            "resources/single_player_music3.wav"
        ));
    }

    private void setupFrame() {
        setTitle("Tetris");
        setSize(400, 800);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    private void focusOnBoard() {
        EventQueue.invokeLater(() -> {
            if (board != null) {
                board.requestFocusInWindow();
            }
        });
    }

    // Update the returnToMainMenu() method to properly shut down the game

private void returnToMainMenu() {
    // First, stop the game completely to prevent any pending game over dialogs
    if (board != null) {
        board.stopGame();
    }
    
    // Then stop audio
    if (audioPlayer != null) {
        audioPlayer.stop();
        audioPlayer = null;
    }
    
    // Save a local reference to username since we'll need it after dispose
    String currentUsername = username;
    
    // Dispose of the current window
    dispose();
    
    // Only after everything is stopped and disposed, launch the menu
    EventQueue.invokeLater(() -> {
        var modeSelectionFrame = new ModeSelectionFrame(currentUsername);
        modeSelectionFrame.setVisible(true);
    });
}

    /**
     * Toggle dark/light mode.
     * This method is called from Board when the user presses L.
     */
    public void toggleDarkMode() {
        // Get dark mode flag from board
        boolean isDarkMode = board != null && board.isDarkMode();
        System.out.println("Tetris.toggleDarkMode() called. isDarkMode = " + isDarkMode);

        if (isDarkMode) {
            applyDarkModeStyles();
        } else {
            applyLightModeStyles();
        }

        // Ensure UI components are updated
        SwingUtilities.invokeLater(() -> {
            if (topPanel != null) {
                topPanel.invalidate();
                topPanel.repaint();
            }
            if (backButton != null) {
                backButton.invalidate();
                backButton.repaint();
            }
            if (statusbar != null) {
                statusbar.invalidate();
                statusbar.repaint();
            }
            repaint();
            revalidate();
        });
    }

    private void applyDarkModeStyles() {
        System.out.println("Tetris.applyDarkModeStyles() called");
        // Update topPanel background
        if (topPanel != null) {
            topPanel.setBackground(DARK_GRAY);
            topPanel.setOpaque(true);
        }
        // Update backButton with dark background and white border/text
        if (backButton != null) {
            backButton.setBackground(DARK_GRAY);
            backButton.setForeground(Color.WHITE);
            backButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.WHITE, 2, true),
                BorderFactory.createEmptyBorder(5, 15, 5, 15)
            ));
        }
        // Update statusbar styling
        if (statusbar != null) {
            statusbar.setBackground(MEDIUM_GRAY);
            statusbar.setForeground(Color.WHITE);
            statusbar.setOpaque(true);
        }
    }

    private void applyLightModeStyles() {
        // Revert topPanel to light blue
        if (topPanel != null) {
            topPanel.setBackground(LIGHT_BLUE);
            topPanel.setOpaque(true);
        }
        // Revert backButton to light blue with dark blue border and text
        if (backButton != null) {
            backButton.setBackground(LIGHT_BLUE);
            backButton.setForeground(DARK_BLUE);
            backButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(DARK_BLUE, 2, true),
                BorderFactory.createEmptyBorder(5, 15, 5, 15)
            ));
        }
        // Revert statusbar
        if (statusbar != null) {
            statusbar.setBackground(null);
            statusbar.setForeground(Color.BLACK);
            statusbar.setOpaque(false);
        }
    }

    /**
     * Returns the status bar for use by the Board.
     */
    JLabel getStatusBar() {
        return statusbar;
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            var loginFrame = new LoginFrame();
            loginFrame.setVisible(true);
        });
    }
}