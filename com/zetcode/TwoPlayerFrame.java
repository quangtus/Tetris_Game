package com.zetcode;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Arrays;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

public class TwoPlayerFrame extends JFrame implements KeyListener {

    // Constants for colors
    private static final Color LIGHT_BLUE = new Color(173, 216, 230);
    private static final Color DARK_BLUE = new Color(0, 102, 153);
    private static final Color DARK_GRAY = new Color(64, 64, 64);
    private static final Color MEDIUM_GRAY = new Color(100, 100, 100);

    // UI Components
    private JLabel player1Status;
    private JLabel player2Status;
    private TwoPlayerBoard player1Board;
    private TwoPlayerBoard player2Board;
    private JPanel topPanel;
    private JPanel boardsPanel;
    private JPanel outerPanel;
    private JPanel statusPanel;
    private JButton backButton;
    
    // Game state
    private AudioPlayer audioPlayer;
    private String username;
    private boolean gameRunning = true;

    public TwoPlayerFrame(String username) {
        this.username = username;
        initUI();
    }

    private void initUI() {
        // Basic frame setup
        setTitle("Tetris - Two Player Mode");
        setSize(800, 800);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Create top panel with back button
        createTopPanel();
        
        // Create game boards
        createGameBoards();
        
        // Create status panel
        createStatusPanel();
        
        // Start background music
        startBackgroundMusic();
        
        // Setup keyboard input
        setupKeyboardControl();
    }
    
    private void createTopPanel() {
        topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        topPanel.setBackground(LIGHT_BLUE);
        
        backButton = new JButton("Back");
        backButton.setFocusable(false);
        backButton.setFont(new Font("Arial", Font.BOLD, 14));
        backButton.setBackground(LIGHT_BLUE);
        backButton.setForeground(DARK_BLUE);
        backButton.setFocusPainted(false);
        backButton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(DARK_BLUE, 2, true),
            BorderFactory.createEmptyBorder(5, 15, 5, 15)
        ));
        
        backButton.addActionListener(e -> returnToMainMenu());
        
        topPanel.add(backButton);
        add(topPanel, BorderLayout.NORTH);
    }
    
    private void createGameBoards() {
        // Create status labels
        player1Status = new JLabel("Player 1: 0", SwingConstants.CENTER);
        player1Status.setFocusable(false);
        
        player2Status = new JLabel("Player 2: 0", SwingConstants.CENTER);
        player2Status.setFocusable(false);
        
        // Create game boards
        player1Board = new TwoPlayerBoard(player1Status);
        player1Board.setFocusable(false);
        player1Board.setPreferredSize(new Dimension(350, 700));
        
        player2Board = new TwoPlayerBoard(player2Status);
        player2Board.setFocusable(false);
        player2Board.setPreferredSize(new Dimension(350, 700));
        
        // Create board container with proper spacing
        JPanel player1Panel = new JPanel(new BorderLayout());
        player1Panel.setBackground(LIGHT_BLUE);
        player1Panel.add(player1Board, BorderLayout.CENTER);
        
        JPanel player2Panel = new JPanel(new BorderLayout());
        player2Panel.setBackground(LIGHT_BLUE);
        player2Panel.add(player2Board, BorderLayout.CENTER);
        
        // Center panel holding both boards with spacing between them
        boardsPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        boardsPanel.setBackground(LIGHT_BLUE);
        boardsPanel.add(player1Panel);
        boardsPanel.add(player2Panel);
        
        // Outer panel with padding around the boards
        outerPanel = new JPanel(new BorderLayout());
        outerPanel.setBackground(LIGHT_BLUE);
        outerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        outerPanel.add(boardsPanel, BorderLayout.CENTER);
        
        add(outerPanel, BorderLayout.CENTER);
    }
    
    private void createStatusPanel() {
        statusPanel = new JPanel(new GridLayout(1, 2));
        statusPanel.add(player1Status);
        statusPanel.add(player2Status);
        
        add(statusPanel, BorderLayout.SOUTH);
    }
    
    private void startBackgroundMusic() {
        audioPlayer = new AudioPlayer();
        audioPlayer.playRandomLoop(Arrays.asList(
            "resources/two_player_music1.wav",
            "resources/two_player_music2.wav",
            "resources/two_player_music3.wav"
        ));
    }
    
    private void setupKeyboardControl() {
        addKeyListener(this);
        setFocusable(true);
        requestFocusInWindow();
    }
    
    private void returnToMainMenu() {
        // Stop music and game
        stopGame();
        
        // Close current window
        dispose();
        
        // Open mode selection
        EventQueue.invokeLater(() -> {
            var modeSelectionFrame = new ModeSelectionFrame(username);
            modeSelectionFrame.setVisible(true);
        });
    }
    
    private void stopGame() {
        gameRunning = false;
        
        // Stop music
        if (audioPlayer != null) {
            audioPlayer.stop();
            audioPlayer = null;
        }
        
        // Stop game boards
        try {
            if (player1Board != null) {
                player1Board.stopGame();
            }
            if (player2Board != null) {
                player2Board.stopGame();
            }
        } catch (Exception e) {
            System.err.println("Error stopping game: " + e.getMessage());
        }
    }

    public void toggleDarkMode() {
        // Toggle dark mode on both boards
        if (player1Board != null) player1Board.toggleDarkMode();
        if (player2Board != null) player2Board.toggleDarkMode();
        
        // Get dark mode state
        boolean isDarkMode = player1Board != null && player1Board.isDarkMode();
        
        // Update UI colors based on dark mode
        if (isDarkMode) {
            // Apply dark colors
            if (topPanel != null) topPanel.setBackground(DARK_GRAY);
            if (outerPanel != null) outerPanel.setBackground(DARK_GRAY);
            if (boardsPanel != null) boardsPanel.setBackground(DARK_GRAY);
            if (statusPanel != null) statusPanel.setBackground(MEDIUM_GRAY);
            
            // Update text colors
            if (player1Status != null) player1Status.setForeground(Color.WHITE);
            if (player2Status != null) player2Status.setForeground(Color.WHITE);
            
            // Update back button
            if (backButton != null) {
                backButton.setBackground(DARK_GRAY);
                backButton.setForeground(Color.WHITE);
                backButton.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Color.WHITE, 2, true),
                    BorderFactory.createEmptyBorder(5, 15, 5, 15)
                ));
            }
        } else {
            // Apply light colors
            if (topPanel != null) topPanel.setBackground(LIGHT_BLUE);
            if (outerPanel != null) outerPanel.setBackground(LIGHT_BLUE);
            if (boardsPanel != null) boardsPanel.setBackground(LIGHT_BLUE);
            if (statusPanel != null) statusPanel.setBackground(null);
            
            // Update text colors
            if (player1Status != null) player1Status.setForeground(Color.BLACK);
            if (player2Status != null) player2Status.setForeground(Color.BLACK);
            
            // Update back button
            if (backButton != null) {
                backButton.setBackground(LIGHT_BLUE);
                backButton.setForeground(DARK_BLUE);
                backButton.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(DARK_BLUE, 2, true),
                    BorderFactory.createEmptyBorder(5, 15, 5, 15)
                ));
            }
        }
        
        // Update UI
        repaint();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (!gameRunning) return;
        
        // Handle dark mode toggle
        if (e.getKeyCode() == KeyEvent.VK_L) {
            toggleDarkMode();
            return;
        }
        
        // Process other key events on the EDT to avoid blocking UI
        SwingUtilities.invokeLater(() -> {
            // Handle Player 1 input
            if (player1Board != null && !player1Board.isGameOver()) {
                handlePlayer1Input(e);
            }

            // Handle Player 2 input
            if (player2Board != null && !player2Board.isGameOver()) {
                handlePlayer2Input(e);
            }

            // Check for game over condition
            if (player1Board != null && player2Board != null && 
                player1Board.isGameOver() && player2Board.isGameOver()) {
                showGameOverDialog();
            }
        });
    }

    @Override
    public void keyReleased(KeyEvent e) {
        // Not needed
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // Not needed
    }

    private void handlePlayer1Input(KeyEvent e) {
        if (player1Board == null) return;
        
        switch (e.getKeyCode()) {
            case KeyEvent.VK_W:
                player1Board.tryMove(player1Board.getCurrentPiece().rotateLeft(), 
                                   player1Board.getCurrentX(), 
                                   player1Board.getCurrentY());
                break;
            case KeyEvent.VK_S:
                player1Board.tryMove(player1Board.getCurrentPiece().rotateRight(), 
                                   player1Board.getCurrentX(), 
                                   player1Board.getCurrentY());
                break;
            case KeyEvent.VK_A:
                player1Board.tryMove(player1Board.getCurrentPiece(), 
                                   player1Board.getCurrentX() - 1, 
                                   player1Board.getCurrentY());
                break;
            case KeyEvent.VK_D:
                player1Board.tryMove(player1Board.getCurrentPiece(), 
                                   player1Board.getCurrentX() + 1, 
                                   player1Board.getCurrentY());
                break;
            case KeyEvent.VK_Z:
                player1Board.oneLineDown();
                break;
            case KeyEvent.VK_SPACE:
                player1Board.dropDown();
                break;
        }
    }
    
    private void handlePlayer2Input(KeyEvent e) {
        if (player2Board == null) return;
        
        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP:
                player2Board.tryMove(player2Board.getCurrentPiece().rotateLeft(), 
                                   player2Board.getCurrentX(), 
                                   player2Board.getCurrentY());
                break;
            case KeyEvent.VK_DOWN:
                player2Board.tryMove(player2Board.getCurrentPiece().rotateRight(), 
                                   player2Board.getCurrentX(), 
                                   player2Board.getCurrentY());
                break;
            case KeyEvent.VK_LEFT:
                player2Board.tryMove(player2Board.getCurrentPiece(), 
                                   player2Board.getCurrentX() - 1, 
                                   player2Board.getCurrentY());
                break;
            case KeyEvent.VK_RIGHT:
                player2Board.tryMove(player2Board.getCurrentPiece(), 
                                   player2Board.getCurrentX() + 1, 
                                   player2Board.getCurrentY());
                break;
            case KeyEvent.VK_CONTROL:
                player2Board.oneLineDown();
                break;
            case KeyEvent.VK_ENTER:
                player2Board.dropDown();
                break;
        }
    }

    public void checkGameOver() {
        if (!gameRunning) return;
        
        if (player1Board != null && player2Board != null && 
            player1Board.isGameOver() && player2Board.isGameOver()) {
            SwingUtilities.invokeLater(this::showGameOverDialog);
        }
    }

    private void showGameOverDialog() {
        if (!gameRunning) return;
        
        // Get scores
        int player1Score = player1Board != null ? player1Board.getScore() : 0;
        int player2Score = player2Board != null ? player2Board.getScore() : 0;
    
        // Play game over sound in separate thread
        new Thread(() -> {
            try {
                AudioPlayer gameOverSound = new AudioPlayer();
                gameOverSound.play("resources/gameover.wav");
            } catch (Exception e) {
                System.err.println("Error playing game over sound: " + e.getMessage());
            }
        }).start();
    
        // Determine winner
        String winner = player1Score > player2Score ? "Player 1 Wins!" :
                       player1Score < player2Score ? "Player 2 Wins!" : "It's a Tie!";
    
        // Create message
        String message = String.format("Game Over!\nPlayer 1 Score: %d\nPlayer 2 Score: %d\n%s\nDo you want to restart?", 
                                      player1Score, player2Score, winner);
    
        // Show dialog
        int option = JOptionPane.showConfirmDialog(this, message, "Game Over", JOptionPane.YES_NO_OPTION);
    
        if (option == JOptionPane.YES_OPTION) {
            restartGame();
        } else {
            returnToMainMenu();
        }
    }

    private void restartGame() {
        if (!gameRunning) return;
        
        // Reset Player 1
        if (player1Board != null) {
            player1Board.resetBoard();
        }
        if (player1Status != null) {
            player1Status.setText("Player 1: 0");
        }

        // Reset Player 2
        if (player2Board != null) {
            player2Board.resetBoard();
        }
        if (player2Status != null) {
            player2Status.setText("Player 2: 0");
        }

        // Ensure focus for keyboard input
        setFocusable(true);
        requestFocusInWindow();
    }

    @Override
    public void dispose() {
        // Stop game before disposing
        stopGame();
        
        // Call parent dispose
        super.dispose();
    }
}