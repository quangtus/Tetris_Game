package com.zetcode;

import javax.swing.JLabel;
import javax.swing.JOptionPane;

import com.zetcode.Shape.Tetrominoe;

import java.awt.Color;

public class SpeedBoard extends Board {
    
    // Tốc độ ban đầu và tối đa
    private static final int INITIAL_SPEED = 300;  // 800ms - khá chậm để bắt đầu
    private static final int MAX_SPEED = 100;      // 100ms - rất nhanh
    private static final int SPEED_DECREMENT = 50; // Giảm 50ms mỗi lần
    private static final int LINES_PER_LEVEL = 5;  // Tăng cấp độ sau 5 dòng
    
    private int currentSpeed; // Tốc độ hiện tại
    private int currentLevel; // Cấp độ hiện tại
    private int linesForNextLevel; // Số dòng để lên cấp tiếp theo
    
    public SpeedBoard(Tetris parent, String username) {
        super(parent, username);
        this.currentSpeed = INITIAL_SPEED;
        this.currentLevel = 1;
        this.linesForNextLevel = LINES_PER_LEVEL;
    }
    
    @Override
    public void start() {
        super.start();
        // Ghi đè phương thức start để đặt tốc độ ban đầu cho timer
        if (timer != null) {
            timer.setDelay(currentSpeed);
        }
        updateStatusText();
    }
    
    @Override
    protected void removeFullLines() {
        int numFullLines = 0;
        
        // Đếm số dòng được xóa - code tương tự như trong Board.removeFullLines()
        for (int i = BOARD_HEIGHT - 1; i >= 0; i--) {
            boolean lineIsFull = true;
            
            for (int j = 0; j < BOARD_WIDTH; j++) {
                if (shapeAt(j, i) == Shape.Tetrominoe.NoShape) {
                    lineIsFull = false;
                    break;
                }
            }
            
            if (lineIsFull) {
                numFullLines++;
                
                // Di chuyển các dòng ở trên xuống
                for (int k = i; k < BOARD_HEIGHT - 1; k++) {
                    for (int j = 0; j < BOARD_WIDTH; j++) {
                        board[(k * BOARD_WIDTH) + j] = shapeAt(j, k + 1);
                    }
                }
                
                // Kiểm tra dòng hiện tại lại
                i++;
            }
        }
        
        if (numFullLines > 0) {
            // Phát âm thanh xóa dòng
            AudioPlayer lineClearSound = new AudioPlayer();
            lineClearSound.play("resources/line_clear.wav");
            
            // Cập nhật điểm và tăng tốc độ
            numLinesRemoved += numFullLines;
            linesForNextLevel -= numFullLines;
            
            // Kiểm tra xem có đủ điều kiện để tăng cấp độ
            if (linesForNextLevel <= 0) {
                levelUp();
            }
            
            // Cập nhật hiển thị
            updateStatusText();
            
            isFallingFinished = true;
            curPiece.setShape(Tetrominoe.NoShape);
        }
    }
    
    private void levelUp() {
        currentLevel++;
        linesForNextLevel = LINES_PER_LEVEL;
        
        // Tăng tốc độ (giảm delay)
        if (currentSpeed > MAX_SPEED) {
            currentSpeed = Math.max(MAX_SPEED, currentSpeed - SPEED_DECREMENT);
            if (timer != null) {
                timer.setDelay(currentSpeed);
            }
            
            // Hiển thị thông báo lên cấp
            String levelMessage = "Cấp độ " + currentLevel + "! Tốc độ tăng!";
            statusbar.setText(levelMessage);
            
            // Hiệu ứng nhấp nháy khi lên cấp
            flashLevelUp();
        }
    }
    
    private void flashLevelUp() {
        // Tạo hiệu ứng nhấp nháy khi lên cấp
        new Thread(() -> {
            Color originalColor = statusbar.getForeground();
            
            try {
                for (int i = 0; i < 5; i++) {
                    statusbar.setForeground(Color.RED);
                    Thread.sleep(200);
                    statusbar.setForeground(originalColor);
                    Thread.sleep(200);
                }
            } catch (InterruptedException e) {
                statusbar.setForeground(originalColor);
            } finally {
                // Cập nhật lại trạng thái sau khi nhấp nháy
                updateStatusText();
            }
        }).start();
    }
    
    private void updateStatusText() {
        statusbar.setText(String.format("Điểm: %d | Cấp độ: %d | Tốc độ: %d", 
                                       numLinesRemoved, currentLevel, 
                                       1000 - currentSpeed));
    }
    
    @Override
    protected void restartGame() {
        super.restartGame();
        
        // Reset các biến điều khiển tốc độ
        currentSpeed = INITIAL_SPEED;
        currentLevel = 1;
        linesForNextLevel = LINES_PER_LEVEL;
        
        if (timer != null) {
            timer.setDelay(currentSpeed);
        }
        
        updateStatusText();
    }
    // Thêm hàm mới để ghi đè phương thức kiểm tra điểm cao

@Override
protected void pieceDropped() {
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

    @Override
    protected void newPiece() {
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

            // Check high score - use SPEED_MODE instead of SINGLE_MODE
            int highScore = Database.getHighScore(username, Database.SPEED_MODE);
            String msg = String.format("Game over. Score: %d. High score: %d", 
                                    numLinesRemoved, highScore);

            if (numLinesRemoved > highScore) {
                Database.updateHighScore(username, numLinesRemoved, Database.SPEED_MODE);
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
}