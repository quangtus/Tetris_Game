package com.zetcode;

import com.zetcode.Shape.Tetrominoe;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * Board class for the Tetris game.
 * This class manages the game board, game logic, and user interaction.
 */
public class Board extends JPanel {

    // === CONSTANTS ===
    private final int BOARD_WIDTH = 10;
    private final int BOARD_HEIGHT = 22;
    private final int PERIOD_INTERVAL = 100;

    // === GAME STATE ===
    private Timer timer;
    private boolean isFallingFinished = false;
    private boolean isPaused = false;
    private boolean isStopping = false;      // Flag to indicate the game is intentionally being stopped
    private boolean showGhostPiece = false;  // Whether to show ghost piece (shadow)
    private boolean isDarkMode = false;      // Dark mode flag

    // === GAME DATA ===
    private int numLinesRemoved = 0;
    private int curX = 0;
    private int curY = 0;
    private String username;
    private Shape curPiece;
    private Tetrominoe[] board;

    // === UI COMPONENTS ===
    private JLabel statusbar;
    private Tetris parent;  // Reference to parent frame

    /**
     * Constructor for the Board.
     * 
     * @param parent   Parent Tetris frame
     * @param username Player's username
     */
    public Board(Tetris parent, String username) {
        this.parent = parent;
        this.username = username;
        initBoard();
    }

    /**
     * Initialize the board.
     */
    private void initBoard() {
        setFocusable(true);
        statusbar = parent.getStatusBar();
        addKeyListener(new TAdapter());
        
        // Set background color
        setBackground(Color.WHITE);
    }

    /**
     * Calculate the width of each square.
     */
    private int squareWidth() {
        return getWidth() / BOARD_WIDTH;
    }

    /**
     * Calculate the height of each square.
     */
    private int squareHeight() {
        return getHeight() / BOARD_HEIGHT;
    }

    /**
     * Start the game.
     */
    public void start() {
        curPiece = new Shape();
        board = new Tetrominoe[BOARD_WIDTH * BOARD_HEIGHT];
        clearBoard();
        newPiece();
        timer = new Timer(PERIOD_INTERVAL, new GameCycle());
        timer.start();
    }

    /**
     * Stop the game.
     */
    public void stopGame() {
        isStopping = true;
        
        if (timer != null) {
            timer.stop();
        }
        
        curPiece.setShape(Tetrominoe.NoShape);
        isFallingFinished = true;
    }

    /**
     * Clear the game board.
     */
    private void clearBoard() {
        for (int i = 0; i < BOARD_HEIGHT * BOARD_WIDTH; i++) {
            board[i] = Tetrominoe.NoShape;
        }
    }

    /**
     * Draw the game board and pieces.
     */
    private void doDrawing(Graphics g) {
        // Set background based on dark mode
        setBackground(isDarkMode ? Color.BLACK : Color.WHITE);

        // Draw all placed pieces on the board
        for (int i = 0; i < BOARD_HEIGHT; i++) {
            for (int j = 0; j < BOARD_WIDTH; j++) {
                Tetrominoe shape = shapeAt(j, BOARD_HEIGHT - i - 1);
                if (shape != Tetrominoe.NoShape) {
                    drawSquare(g, j * squareWidth(), i * squareHeight(), shape);
                }
            }
        }

        // Draw the current falling piece
        if (curPiece.getShape() != Tetrominoe.NoShape) {
            for (int i = 0; i < 4; i++) {
                int x = curX + curPiece.x(i);
                int y = curY - curPiece.y(i);
                drawSquare(g, x * squareWidth(), 
                          (BOARD_HEIGHT - y - 1) * squareHeight(), 
                          curPiece.getShape());
            }
        }
    }

    /**
     * Draw the ghost piece (shadow of where the piece will land).
     */
    private void drawGhostPiece(Graphics g) {
        if (curPiece.getShape() == Tetrominoe.NoShape) {
            return;
        }

        // Find where the piece would land if dropped
        int ghostY = findDropPosition();

        for (int i = 0; i < 4; i++) {
            int x = curX + curPiece.x(i);
            int y = ghostY - curPiece.y(i);
            
            // Draw semi-transparent ghost squares
            drawGhostSquare(g, x * squareWidth(), 
                           (BOARD_HEIGHT - y - 1) * squareHeight());
        }
    }

    /**
     * Find where the current piece would land if dropped.
     */
    private int findDropPosition() {
        int newY = curY;
        
        while (newY > 0) {
            boolean canMove = true;
            
            for (int i = 0; i < 4; i++) {
                int x = curX + curPiece.x(i);
                int y = (newY - 1) - curPiece.y(i);
                
                if (y < 0 || x < 0 || x >= BOARD_WIDTH || 
                    shapeAt(x, y) != Tetrominoe.NoShape) {
                    canMove = false;
                    break;
                }
            }
            
            if (!canMove) {
                break;
            }
            
            newY--;
        }
        
        return newY;
    }

    /**
     * Draw a ghost square (semi-transparent).
     */
    private void drawGhostSquare(Graphics g, int x, int y) {
        // Semi-transparent ghost piece
        g.setColor(new Color(128, 128, 128, 70));
        g.fillRect(x + 1, y + 1, squareWidth() - 2, squareHeight() - 2);
        
        // Lighter edge
        g.setColor(new Color(192, 192, 192, 70));
        g.drawLine(x, y + squareHeight() - 1, x, y);
        g.drawLine(x, y, x + squareWidth() - 1, y);
        
        // Darker edge
        g.setColor(new Color(64, 64, 64, 70));
        g.drawLine(x + 1, y + squareHeight() - 1, 
                  x + squareWidth() - 1, y + squareHeight() - 1);
        g.drawLine(x + squareWidth() - 1, y + squareHeight() - 1, 
                  x + squareWidth() - 1, y + 1);
    }

    /**
     * Check which shape is at the given position.
     */
    private Tetrominoe shapeAt(int x, int y) {
        return board[(y * BOARD_WIDTH) + x];
    }

    /**
     * Create and position a new piece.
     */
    private void newPiece() {
        // Don't create new piece if we're stopping the game
        if (isStopping) {
            return;
        }
        
        curPiece.setRandomShape();
        curX = BOARD_WIDTH / 2 + 1;
        curY = BOARD_HEIGHT - 1 + curPiece.minY();

        // Check for game over
        if (!tryMove(curPiece, curX, curY)) {
            curPiece.setShape(Tetrominoe.NoShape);
            timer.stop();
            
            // Don't show game over dialog if we're stopping the game
            if (isStopping) {
                return;
            }

            // Play game over sound
            AudioPlayer gameOverSound = new AudioPlayer();
            gameOverSound.play("resources/gameover.wav");

            // Check high score
            int highScore = Database.getHighScore(username);
            String msg = String.format("Game over. Score: %d. High score: %d", 
                                     numLinesRemoved, highScore);

            if (numLinesRemoved > highScore) {
                Database.updateHighScore(username, numLinesRemoved);
                msg += "\nChúc mừng bạn đã phá kỷ lục!";
            }

            // Show game over dialog
            int option = JOptionPane.showConfirmDialog(this,
                    msg + "\nBạn có muốn chơi lại không?", "Game Over",
                    JOptionPane.YES_NO_OPTION);

            if (option == JOptionPane.YES_OPTION) {
                restartGame();
            } else {
                statusbar.setText(msg);
            }
        }
    }

    /**
     * Toggle dark mode and notify parent frame.
     */
    private void toggleDarkMode() {
        isDarkMode = !isDarkMode;
        System.out.println("Board.toggleDarkMode() called. isDarkMode = " + isDarkMode);
        
        // Update this component
        repaint();
        
        // Notify the parent frame to update its UI
        if (parent != null) {
            parent.toggleDarkMode();
        }
    }

    /**
     * Get the current dark mode state.
     */
    public boolean isDarkMode() {
        return isDarkMode;
    }

    /**
     * Toggle the visibility of the ghost piece.
     */
    private void toggleGhostPiece() {
        showGhostPiece = !showGhostPiece;
        repaint();
    }

    /**
     * Pause or unpause the game.
     */
    private void pause() {
        isPaused = !isPaused;
        
        if (isPaused) {
            timer.stop();
            statusbar.setText("paused");
        } else {
            timer.start();
            statusbar.setText(String.valueOf(numLinesRemoved));
        }
        
        repaint();
    }

    /**
     * Move the current piece one line down.
     */
    private void oneLineDown() {
        if (!tryMove(curPiece, curX, curY - 1)) {
            pieceDropped();
        }
    }

    /**
     * Drop the current piece all the way down.
     */
    private void dropDown() {
        int newY = curY;
        
        while (newY > 0 && tryMove(curPiece, curX, newY - 1)) {
            newY--;
        }
        
        pieceDropped();
    }

    /**
     * Handle when a piece is dropped to the bottom.
     */
    private void pieceDropped() {
        // Add the piece to the board
        for (int i = 0; i < 4; i++) {
            int x = curX + curPiece.x(i);
            int y = curY - curPiece.y(i);
            board[(y * BOARD_WIDTH) + x] = curPiece.getShape();
        }

        // Check for and remove completed lines
        removeFullLines();

        // Create a new piece if needed
        if (!isFallingFinished) {
            newPiece();
        }
    }

    /**
     * Remove completed lines and update score.
     */
    private void removeFullLines() {
        int numFullLines = 0;

        // Check each line from bottom to top
        for (int i = BOARD_HEIGHT - 1; i >= 0; i--) {
            boolean lineIsFull = true;

            // Check if line is completely filled
            for (int j = 0; j < BOARD_WIDTH; j++) {
                if (shapeAt(j, i) == Tetrominoe.NoShape) {
                    lineIsFull = false;
                    break;
                }
            }

            // If line is full, remove it
            if (lineIsFull) {
                numFullLines++;

                // Move all lines above down
                for (int k = i; k < BOARD_HEIGHT - 1; k++) {
                    for (int j = 0; j < BOARD_WIDTH; j++) {
                        board[(k * BOARD_WIDTH) + j] = shapeAt(j, k + 1);
                    }
                }

                // Check the same line again (now with new content)
                i++;
            }
        }

        // Update score if lines were removed
        if (numFullLines > 0) {
            // Play line clear sound
            AudioPlayer lineClearSound = new AudioPlayer();
            lineClearSound.play("resources/line_clear.wav");

            // Update score
            numLinesRemoved += numFullLines;
            statusbar.setText(String.valueOf(numLinesRemoved));
            isFallingFinished = true;
            curPiece.setShape(Tetrominoe.NoShape);
        }
    }

    /**
     * Try to move the piece to a new position.
     * 
     * @return true if the move is valid
     */
    private boolean tryMove(Shape newPiece, int newX, int newY) {
        for (int i = 0; i < 4; i++) {
            int x = newX + newPiece.x(i);
            int y = newY - newPiece.y(i);

            // Check boundaries
            if (x < 0 || x >= BOARD_WIDTH || y < 0 || y >= BOARD_HEIGHT) {
                return false;
            }

            // Check collision with other pieces
            if (shapeAt(x, y) != Tetrominoe.NoShape) {
                return false;
            }
        }

        // Move is valid - update position
        curPiece = newPiece;
        curX = newX;
        curY = newY;
        
        repaint();
        return true;
    }

    /**
     * Draw a single square for a tetromino.
     */
    private void drawSquare(Graphics g, int x, int y, Tetrominoe shape) {
        Color colors[] = {
            new Color(0, 0, 0),         // NoShape
            new Color(204, 102, 102),   // ZShape 
            new Color(102, 204, 102),   // SShape
            new Color(102, 102, 204),   // LineShape
            new Color(204, 204, 102),   // TShape
            new Color(204, 102, 204),   // SquareShape
            new Color(102, 204, 204),   // LShape
            new Color(218, 170, 0)      // MirroredLShape
        };

        Color color = colors[shape.ordinal()];

        // Adjust color for dark mode
        if (isDarkMode) {
            color = color.darker();
        }

        // Draw square fill
        g.setColor(color);
        g.fillRect(x + 1, y + 1, squareWidth() - 2, squareHeight() - 2);

        // Draw highlights (top and left edges)
        g.setColor(color.brighter());
        g.drawLine(x, y + squareHeight() - 1, x, y);
        g.drawLine(x, y, x + squareWidth() - 1, y);

        // Draw shadows (bottom and right edges)
        g.setColor(color.darker());
        g.drawLine(x + 1, y + squareHeight() - 1, 
                  x + squareWidth() - 1, y + squareHeight() - 1);
        g.drawLine(x + squareWidth() - 1, y + squareHeight() - 1, 
                  x + squareWidth() - 1, y + 1);
    }

    /**
     * Restart the game.
     */
    private void restartGame() {
        if (timer != null && timer.isRunning()) {
            timer.stop();
        }
        
        // Reset all game state
        isStopping = false;
        numLinesRemoved = 0;
        isFallingFinished = false;
        isPaused = false;
        
        clearBoard();
        newPiece();
        
        // Start timer and update UI
        timer.start();
        statusbar.setText("0");
        repaint();
    }

    /**
     * Show confirmation dialog for restart.
     */
    private void confirmRestart() {
        // Remember if game was running
        boolean wasRunning = !isPaused;
        
        // Pause game while showing dialog
        if (wasRunning) {
            pause();
        }

        // Ask for confirmation
        int option = JOptionPane.showConfirmDialog(this,
                "Bạn có muốn chơi lại không?", "Restart Game",
                JOptionPane.YES_NO_OPTION);

        if (option == JOptionPane.YES_OPTION) {
            restartGame();
        } else if (wasRunning) {
            // If user cancels, resume game if it was running
            pause();
        }
    }

    /**
     * Game loop cycle.
     */
    private void doGameCycle() {
        if (isPaused || isStopping) {
            return;
        }

        if (isFallingFinished) {
            isFallingFinished = false;
            newPiece();
        } else {
            oneLineDown();
        }
    }

    /**
     * Paint the component.
     */
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        doDrawing(g);
        
        if (showGhostPiece) {
            drawGhostPiece(g);
        }
    }

    /**
     * Game cycle handler for Timer events.
     */
    private class GameCycle implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (isStopping) return;
            doGameCycle();
        }
    }

    /**
     * Keyboard input handler.
     */
    class TAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            // Ignore key events if no piece is active or game is stopping
            if (curPiece.getShape() == Tetrominoe.NoShape || isStopping) {
                return;
            }

            int keycode = e.getKeyCode();
            
            // Process key input
            switch (keycode) {
                case KeyEvent.VK_P -> pause();
                case KeyEvent.VK_LEFT -> tryMove(curPiece, curX - 1, curY);
                case KeyEvent.VK_RIGHT -> tryMove(curPiece, curX + 1, curY);
                case KeyEvent.VK_DOWN -> tryMove(curPiece.rotateRight(), curX, curY);
                case KeyEvent.VK_UP -> tryMove(curPiece.rotateLeft(), curX, curY);
                case KeyEvent.VK_SPACE -> dropDown();
                case KeyEvent.VK_D -> oneLineDown();
                case KeyEvent.VK_L -> toggleDarkMode();
                case KeyEvent.VK_R -> confirmRestart();
                case KeyEvent.VK_G -> toggleGhostPiece();
            }
        }
    }
}