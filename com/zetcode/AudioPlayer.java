package com.zetcode;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Random;

public class AudioPlayer {

    private Clip clip;
    // Biến static để theo dõi tất cả các player đang chạy
    private static AudioPlayer currentLoopPlayer = null;
    private boolean isLooping = false;

    public void play(String filePath) {
        stop(); // Dừng nhạc nếu đang phát
        try {
            File audioFile = new File(filePath);
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);
            clip = AudioSystem.getClip();
            clip.open(audioStream);
            clip.start(); // Phát âm thanh chỉ một lần
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            System.out.println("Error playing audio: " + e.getMessage());
        }
    }

    public void playRandom(List<String> filePaths) {
        Random random = new Random();
        String randomFile = filePaths.get(random.nextInt(filePaths.size()));
        play(randomFile);
    }

    public void stop() {
        // Đánh dấu rằng player này đã dừng
        isLooping = false;
        
        // Nếu instance này là player vòng lặp hiện tại, xóa tham chiếu
        if (currentLoopPlayer == this) {
            currentLoopPlayer = null;
        }
        
        // Dừng clip hiện tại nếu đang phát
        if (clip != null) {
            clip.stop();
            clip.close();
            clip = null;
        }
    }

    public void playRandomLoop(List<String> filePaths) {
        // Dừng player loop hiện tại nếu có
        if (currentLoopPlayer != null && currentLoopPlayer != this) {
            currentLoopPlayer.stop();
        }
        
        // Đặt instance hiện tại làm currentLoopPlayer
        currentLoopPlayer = this;
        isLooping = true;
        
        // Dừng clip hiện tại nếu đang phát
        if (clip != null) {
            clip.stop();
            clip.close();
            clip = null;
        }
        
        new Thread(() -> {
            Random random = new Random();
            while (isLooping) { // Sử dụng biến isLooping thay vì currentLoopPlayer
                try {
                    String randomFile = filePaths.get(random.nextInt(filePaths.size()));
                    File audioFile = new File(randomFile);
                    
                    // Kiểm tra xem file có tồn tại không
                    if (!audioFile.exists()) {
                        System.out.println("File not found: " + randomFile);
                        Thread.sleep(1000); // Đợi 1 giây trước khi thử lại
                        continue;
                    }
                    
                    AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);
                    
                    synchronized (this) {
                        if (!isLooping) break; // Kiểm tra lại trước khi tạo clip mới
                        
                        // Tạo và phát clip mới
                        Clip newClip = AudioSystem.getClip();
                        newClip.open(audioStream);
                        
                        // Đặt clip mới vào biến clip
                        if (clip != null) {
                            clip.stop();
                            clip.close();
                        }
                        clip = newClip;
                        clip.start();
                        
                        System.out.println("Playing: " + randomFile);
                        
                        // Đợi cho đến khi clip kết thúc
                        long clipDuration = clip.getMicrosecondLength() / 1000;
                        Thread.sleep(clipDuration);
                    }
                } catch (Exception e) {
                    System.out.println("Error playing audio: " + e.getMessage());
                    try {
                        Thread.sleep(1000); // Đợi 1 giây trước khi thử lại
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            }
        }).start();
    }
}