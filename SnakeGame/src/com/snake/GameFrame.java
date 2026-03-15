package com.snake;

import javax.swing.*;

public class GameFrame extends JFrame {

    public GameFrame() {
        setTitle("贪吃蛇");

        GamePanel panel = new GamePanel();
        add(panel);

        pack();
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        setVisible(true);
    }
}