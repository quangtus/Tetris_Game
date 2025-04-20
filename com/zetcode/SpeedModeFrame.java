package com.zetcode;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import javax.swing.JLabel;
import java.util.Arrays;

public class SpeedModeFrame extends Tetris {
    
    public SpeedModeFrame(String username) {
        super(username);
        setTitle("Tetris - Speed Mode");
    }
    
    @Override
    protected void createGameBoard() {
        // Sử dụng SpeedBoard thay vì Board thông thường
        board = new SpeedBoard(this, username);
        add(board);
        board.start();
    }
    
    @Override
    protected void startBackgroundMusic() {
        audioPlayer = new AudioPlayer();
        // Sử dụng nhạc nhanh hơn hoặc nhạc khác để phù hợp với chế độ tốc độ
        audioPlayer.playRandomLoop(Arrays.asList(
            "resources/single_player_music3.wav" // Chọn bản nhạc nhanh nhất
        ));
    }
}