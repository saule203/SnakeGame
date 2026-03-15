package com.snake;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;


public class GamePanel extends JPanel implements ActionListener, KeyListener {
    public static final int UNIT_SIZE = 20;
    public static final int GAME_WIDTH = 600;
    public static final int GAME_HEIGHT = 600;

    private static final int BASE_DELAY = 150; //初始速度 越小越快
    private static final int MIN_DELAY = 40; //速度上限

    private static final String HIGH_SCORE_FILE = "highscore.txt";
    private Snake snake;
    private Food food;
    private Timer timer;
    private GameState state = GameState.START;

    private int score = 0;
    private int highScore = 0;

    public GamePanel() {
        setPreferredSize(new Dimension(GAME_WIDTH, GAME_HEIGHT));
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(this);

        timer = new Timer(BASE_DELAY, this);

        loadHighScore();
    }

    private void restartGame() {
        snake = new Snake();
        food = spawnFood();

        score = 0;
        state = GameState.RUNNING;

        timer.setDelay(BASE_DELAY);
        timer.start();
        repaint();
    }

    private void updateSpeed() { //速度依据分数变化
        int newDelay = BASE_DELAY - score / 5;
        if (newDelay < MIN_DELAY) {
            newDelay = MIN_DELAY;
        }

        timer.setDelay(newDelay);
    }

    private void drawCenteredString(Graphics g, String text, int y) { //文字中间显示
        FontMetrics metrics = g.getFontMetrics(g.getFont());
        int x =(getWidth() - metrics.stringWidth(text)) / 2;
        g.drawString(text, x, y);
    }

    private Food spawnFood() {
        Food f = new Food();
        while (snake.contains(f.getPosition().x, f.getPosition().y)) {
            f.relocate();
        }
        return f;
    }

    private void loadHighScore() { //加载历史最高分
        try {
            File file = new File(HIGH_SCORE_FILE);
            if (file.exists()) {
                Scanner sc = new Scanner(file);
                highScore = sc.nextInt();
                sc.close();
            }
        } catch (Exception e) {
            highScore = 0;
        }
    }

    private void saveHighScore() {
        try{
            FileWriter writer = new FileWriter(HIGH_SCORE_FILE);
            writer.write(String.valueOf(highScore));
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (state == GameState.RUNNING) { //游戏进行界面
            food.draw(g);
            snake.draw(g);

            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD,20));
            g.drawString("Score: " + score, 10, 20);
            g.drawString("High Score: " + highScore, 10, 40);
        } else if (state == GameState.GAME_OVER) { //游戏结束界面
            g.setColor(Color.RED);
            g.setFont(new Font("Arial", Font.BOLD, 40));
            drawCenteredString(g,"Game Over",getHeight() / 2);

            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.PLAIN, 20));
            drawCenteredString(g,"Press SPACE to Restart", getHeight()/2 + 40);
            drawCenteredString(g, "Score: " + score, getHeight()/2 + 20);
            g.setColor(Color.YELLOW);
            drawCenteredString(g, "HighScore: " + highScore, getHeight()/2 +60);
        } else if (state == GameState.START){ //游戏开始界面
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD,32));
            drawCenteredString(g,"Press SPACE to start", getHeight() / 2);
        } else { //暂停界面
            food.draw(g);
            snake.draw(g);

            g.setColor(new Color(0, 0, 0, 150));
            g.fillRect(0, 0, getWidth(),getHeight());

            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD,20));
            drawCenteredString(g, "PAUSED", getHeight() / 2);
            drawCenteredString(g,"Press SPACE to Continue", getHeight() / 2 + 20);
            drawCenteredString(g,"Score: " + score, getHeight() / 2 + 60);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (state == GameState.RUNNING) {
            if (snake.checkCollision()) {
                state = GameState.GAME_OVER;
                timer.stop();

                if (score > highScore) {
                    highScore = score;
                    saveHighScore();
                }
            }

            if (snake.checkFood(food)) {
                snake.grow();
                score += snake.getBodyLength();
                food = spawnFood();

                updateSpeed();
            } else {
                snake.move();
            }
        }
        repaint();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();

        if (state == GameState.RUNNING) {
            if (key == KeyEvent.VK_SPACE) {
                state = GameState.PAUSED;
                timer.stop();
                repaint();
            } else {
                snake.changeDirection(key);
            }
        } else if (state == GameState.GAME_OVER){
            if (key == KeyEvent.VK_SPACE) {
                restartGame();
            }
        } else if (state == GameState.PAUSED) {
            if (key == KeyEvent.VK_SPACE) {
                state =GameState.RUNNING;
                timer.start();
                repaint();
            }
        }else {
            if (key == KeyEvent.VK_SPACE) {
                restartGame();
            }
        }
    }

    @Override public void keyTyped(KeyEvent e) {}
    @Override public void keyReleased(KeyEvent e) {}

}
