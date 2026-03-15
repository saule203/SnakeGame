package com.snake;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedList;

import static com.snake.GamePanel.UNIT_SIZE;

public class Snake {
    private LinkedList<Point> body;
    private Direction direction = Direction.RIGHT;

    private Image headUp;
    private Image headDown;
    private Image headLeft;
    private Image headRight;

    private Image bodyTurnDownRight;
    private Image bodyTurnDownLeft;
    private Image bodyTurnUpRight;
    private Image bodyTurnUpLeft;
    private Image bodyHorizontal;
    private Image bodyVertical;

    private Image tailUp;
    private Image tailDown;
    private Image tailLeft;
    private Image tailRight;

    public Snake() {

        headUp = new ImageIcon(getClass().getResource("/images/snack_up.png")).getImage();
        headDown = new ImageIcon(getClass().getResource("/images/snack_down.png")).getImage();
        headLeft = new ImageIcon(getClass().getResource("/images/snack_left.png")).getImage();
        headRight = new ImageIcon(getClass().getResource("/images/snack_right.png")).getImage();

        tailUp = new ImageIcon(getClass().getResource("/images/tail_up.png")).getImage();
        tailDown = new ImageIcon(getClass().getResource("/images/tail_down.png")).getImage();
        tailLeft = new ImageIcon(getClass().getResource("/images/tail_left.png")).getImage();
        tailRight = new ImageIcon(getClass().getResource("/images/tail_right.png")).getImage();

        bodyTurnDownRight = new ImageIcon(getClass().getResource("/images/snack_turn_down_right.png")).getImage();
        bodyTurnDownLeft = new ImageIcon(getClass().getResource("/images/snack_turn_down_left.png")).getImage();
        bodyTurnUpLeft = new ImageIcon(getClass().getResource("/images/snack_turn_up_left.png")).getImage();
        bodyTurnUpRight = new ImageIcon(getClass().getResource("/images/snack_turn_up_right.png")).getImage();
        bodyVertical = new ImageIcon(getClass().getResource("/images/snack_vertical.png")).getImage();
        bodyHorizontal = new ImageIcon(getClass().getResource("/images/snack_horizontal.png")).getImage();

        body = new LinkedList<>();
        body.add(new Point(100, 100));
        body.add(new Point(80, 100));
        body.add(new Point(60, 100));
    }

    public void move () {
        Point head = body.getFirst();
        Point newHead = new Point(head);

        switch (direction) {
            case UP -> newHead.y -= UNIT_SIZE;
            case DOWN -> newHead.y += UNIT_SIZE;
            case LEFT -> newHead.x -= UNIT_SIZE;
            case RIGHT -> newHead.x += UNIT_SIZE;
        }

        body.addFirst(newHead);
        body.removeLast();
    }

    public void grow () {
        Point head = body.getFirst();
        Point newHead = new Point(head);

        switch (direction) {
            case UP -> newHead.y -= UNIT_SIZE;
            case DOWN -> newHead.y += UNIT_SIZE;
            case LEFT -> newHead.x -= UNIT_SIZE;
            case RIGHT -> newHead.x += UNIT_SIZE;
        }

        body.addFirst(newHead);
    }

    private Image getHeadImage() {
        switch (direction) {
            case UP:
                return headUp;
            case DOWN:
                return headDown;
            case RIGHT:
                return headRight;
            case LEFT:
                return headLeft;
        }
        return null;
    }

    private Image getBodyImage(BodyType bodyType) {
        switch (bodyType) {
            case TURN_DOWN_RIGHT:
                return bodyTurnDownRight;
            case TURN_DOWN_LEFT:
                return bodyTurnDownLeft;
            case TURN_UP_LEFT:
                return bodyTurnUpLeft;
            case TURN_UP_RIGHT:
                return bodyTurnUpRight;
            case VERTICAL:
                return bodyVertical;
            case HORIZONTAL:
                return bodyHorizontal;
        }
        return null;
    }

    private Image getTailImage(Point beforeTail, Point tail) {
        if (tail.x > beforeTail.x) return tailRight;
        if (tail.x < beforeTail.x) return tailLeft;
        if (tail.y > beforeTail.y) return tailDown;
        return tailUp;
    }

    public void draw(Graphics g) {
        //画蛇头
        Point head = body.getFirst();
        Image headImg = getHeadImage();

        g.drawImage(
                headImg,
                head.x ,
                head.y ,
                UNIT_SIZE,
                UNIT_SIZE,
                null
        );

        //画蛇身
        for (int i = 1; i < body.size() - 1; i++) {
            Point p = body.get(i);
            BodyType type = getBodyType(i);

            Image bodyImg = getBodyImage(type);

            g.drawImage(
                    bodyImg,
                    p.x ,
                    p.y ,
                    UNIT_SIZE,
                    UNIT_SIZE,
                    null
            );
        }

        //画蛇尾
        Point tail = body.getLast();
        Point beforeTail = body.get(body.size() - 2);

        Image tailImg = getTailImage(beforeTail, tail);

        g.drawImage(
                tailImg,
                tail.x,
                tail.y,
                UNIT_SIZE,
                UNIT_SIZE,
                null
        );
    }

    public void changeDirection(int keyCode) {
        switch (keyCode) {
            case 38 -> { if (direction != Direction.DOWN) direction = Direction.UP;}
            case 40 -> { if (direction != Direction.UP) direction = Direction.DOWN;}
            case 37 -> { if (direction != Direction.RIGHT) direction = Direction.LEFT;}
            case 39 -> { if(direction != Direction.LEFT) direction = Direction.RIGHT;}
        }
    }

    public boolean checkCollision() {
        Point head = body.getFirst();

        if (head.x < 0 || head.x >= GamePanel.GAME_WIDTH || head.y < 0 || head.y >= GamePanel.GAME_HEIGHT) {
            return true;
        }

        for (int i = 1; i < body.size(); i++) {
            if (head.equals(body.get(i))) {
                return true;
            }
        }
        return false;
    }

    public boolean checkFood(Food food) {
        return body.getFirst().equals(food.getPosition());
    }

    public int getBodyLength() {
        return body.size();
    }

    public boolean contains(int x, int y) {
        for (Point p : body) {
            if (p.x == x && p.y == y) {
                return true;
            }
        }
        return false;
    }

    private BodyType getBodyType(int i) { //蛇身状态
        Point prev = body.get(i - 1);
        Point curr = body.get(i);
        Point next = body.get(i + 1);

        if (prev.y == curr.y && curr.y == next.y) return BodyType.HORIZONTAL;
        if (prev.x == curr.x && curr.x == next.x) return BodyType.VERTICAL;
        if ((prev.y < curr.y && next.x > curr.x) || (next.y < curr.y && prev.x > curr.x)) return BodyType.TURN_UP_RIGHT;
        if ((prev.y < curr.y && next.x < curr.x) || (next.y < curr.y && prev.x < curr.x)) return BodyType.TURN_UP_LEFT;
        if ((prev.y > curr.y && next.x > curr.x) || (next.y > curr.y && prev.x > curr.x)) return BodyType.TURN_DOWN_RIGHT;
        return BodyType.TURN_DOWN_LEFT;
    }
}
