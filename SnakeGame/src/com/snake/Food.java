package com.snake;

import java.awt.*;
import java.util.Random;

public class Food {
    private Point position;
    private Random random = new Random();

    public Food() {
        relocate();
    }

    public void relocate() {
        int x = random.nextInt(GamePanel.GAME_WIDTH / GamePanel.UNIT_SIZE) * GamePanel.UNIT_SIZE;
        int y = random.nextInt(GamePanel.GAME_HEIGHT / GamePanel.UNIT_SIZE) * GamePanel.UNIT_SIZE;
        this.position = new Point(x, y);
    }

    public void draw(Graphics g) {
        g.setColor(Color.RED);
        g.fillOval(position.x, position.y, 20, 20);
    }

    public Point getPosition() {
        return position;
    }

}
