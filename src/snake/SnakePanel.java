package snake;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.Rectangle2D;


public class SnakePanel extends JPanel implements KeyListener, Runnable {
    public static int WIDTH = 400;
    public static int HEIGHT = 400;
    static int score = 0;
    enum Trends  {NORTH, WEST, SOUTH, EAST }

    Snake snake1;
    Food food;
    Thread t;

    public  SnakePanel() {
        snake1 = new Snake();
        food = new Food();
        setBackground(Color.WHITE);
        setOpaque(true);
        this.addKeyListener(this);
        t = new Thread(this);
        t.start();
    }

    class Snake {
        Rectangle2D head;
        Rectangle2D[] body;
        int basicLength = 5;
        final static int snakeWidth = 10;
        final double xStart = WIDTH*0.5;
        final double yStart = HEIGHT*0.5;

        public Trends trendHead;

        Snake() {
            head = new Rectangle2D.Double(xStart, yStart, snakeWidth, snakeWidth);

            body = new Rectangle2D[basicLength];
            for(int i = 0; i < basicLength; i++)
                body[i] = new Rectangle2D.Double(head.getX()-(i+1)*snakeWidth, head.getY(), snakeWidth, snakeWidth);

            trendHead = Trends.EAST;
        }

        Snake(Snake orig) { //новая змея создается из старой путем добавления к конечной хвостового элемента
            head = orig.head;

            body = new Rectangle2D[orig.body.length + 1];
            for(int i = 0; i < body.length-1; i++)
                body[i] = orig.body[i];

            body[body.length-1] = body[body.length-2];
            trendHead = orig.trendHead;
        }

        public void snakeMove() {
             for(int i = body.length-1; i > 0; i--)
                body[i] = new Rectangle2D.Double(body[i-1].getX(), body[i-1].getY(), snakeWidth, snakeWidth);

            body[0] = new Rectangle2D.Double(head.getX(), head.getY(), snakeWidth, snakeWidth);

            switch (trendHead) {
                case NORTH:
                    head = new Rectangle2D.Double(head.getX(), head.getY()-snakeWidth, snakeWidth, snakeWidth);
                    break;
                case WEST:
                    head = new Rectangle2D.Double(head.getX()-snakeWidth, head.getY(), snakeWidth, snakeWidth);
                    break;
                case SOUTH:
                    head = new Rectangle2D.Double(head.getX(), head.getY()+snakeWidth, snakeWidth, snakeWidth);
                    break;
                case EAST:
                    head = new Rectangle2D.Double(head.getX()+snakeWidth, head.getY(), snakeWidth, snakeWidth);
                    break;
            }
        }
    }

    class Food {
        private double x, y;
        private Rectangle2D.Double fd;
        final  Color foodColor = Color.blue;

        Food() {
            x = Math.random()*WIDTH;
            y = Math.random()*HEIGHT;

            x = Math.floor(x / 10) * 10;
            y = Math.floor(y / 10) * 10;

            if(x < Snake.snakeWidth)
                x = Snake.snakeWidth;

            if(y < Snake.snakeWidth)
                y = Snake.snakeWidth;

            if(x > WIDTH - Snake.snakeWidth)
                x = WIDTH - Snake.snakeWidth;

            if(y > HEIGHT - Snake.snakeWidth)
                y = HEIGHT - Snake.snakeWidth;

            fd = new Rectangle2D.Double(x, y, 10, 10);
        }

        double getX() {
            return x;
        }

        double getY() {
            return y;
        }

        Rectangle2D.Double getFoodRect() {
            return  fd;
        }
    }

    @Override
    public void run() {
        for(;;) {
            try {
                repaint();
                Thread.sleep(150);
            } catch (InterruptedException e) {
            }
        }
    }


    @Override
    public Dimension getPreferredSize() {
        return new Dimension(WIDTH, HEIGHT);
    }

    @Override
    public void keyPressed(KeyEvent e) {
         Trends temp = snake1.trendHead;

         switch(e.getKeyCode()){
             case KeyEvent.VK_W :
                  temp = Trends.NORTH;
                  break;
             case KeyEvent.VK_A :
                 temp = Trends.WEST;
                 break;
             case KeyEvent.VK_S :
                 temp = Trends.SOUTH;
                 break;
             case KeyEvent.VK_D :
                 temp = Trends.EAST;
                 break;
         }

         if((snake1.trendHead.equals(Trends.NORTH) || snake1.trendHead.equals(Trends.SOUTH)) && (temp.equals(Trends.EAST) || temp.equals(Trends.WEST)))
             snake1.trendHead = temp;
         if((temp.equals(Trends.NORTH) || temp.equals(Trends.SOUTH)) && (snake1.trendHead.equals(Trends.EAST) || snake1.trendHead.equals(Trends.WEST)))
             snake1.trendHead = temp;

         repaint();
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    void drawSnake(Graphics2D g2) {
        g2.setColor(Color.green);
        g2.fill(snake1.head);
        for(int i = 0; i < snake1.body.length; i++)
            g2.fill(snake1.body[i]);

        g2.setColor(Color.red);
        g2.draw(snake1.head);
        for(int i = 0; i < snake1.body.length; i++)
            g2.draw(snake1.body[i]);

        snake1.snakeMove();
    }

    void drawFood(Graphics2D g2) {
        g2.setColor(food.foodColor);
        g2.fill(food.fd);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        g.drawString("Координаты головы змеи: " + snake1.head.getX() + " , " + snake1.head.getY(), 10, 10);
        g.drawString("Счёт: " + score, 10, HEIGHT - 15);
        g.drawString("Координаты текущей еды: " + food.getX() + " , " + food.getY(), 10, HEIGHT);
        drawSnake(g2);
        drawFood(g2);

        if(snake1.head.intersects(food.getFoodRect())) {
            food = new Food();
            score++;

            switch(score) {
                case 1 :
                    snake1 = new Snake(snake1);
                    break;
                case 3 :
                    snake1 = new Snake(snake1);
                    break;
                case 5 :
                    snake1 = new Snake(snake1);
                    break;
                case  8:
                    snake1 = new Snake(snake1);
                    break;
                case 11 :
                    snake1 = new Snake(snake1);
                    break;
                case 14 :
                    snake1 = new Snake(snake1);
                    break;
                case 17 :
                    snake1 = new Snake(snake1);
                    break;
                case 20 :
                    snake1 = new Snake(snake1);
                    break;
                case 24 :
                    snake1 = new Snake(snake1);
                    break;
                case 28 :
                    snake1 = new Snake(snake1);
                    break;
                case 33 :
                    snake1 = new Snake(snake1);
                    break;
                case 38 :
                    snake1 = new Snake(snake1);
                    break;
                case 43 :
                    snake1 = new Snake(snake1);
                    break;
                case 50 :
                    snake1 = new Snake(snake1);
                    break;
            }
        }

        for(int i = 0; i < snake1.body.length; i++) {
            if(snake1.head.intersects(snake1.body[i])) {
                snake1 = new Snake();
                score = 0;
                break;
            }

        }


        if(snake1.head.intersectsLine(0-snake1.basicLength, 0-snake1.basicLength, 0-snake1.basicLength, HEIGHT+snake1.basicLength) ||
           snake1.head.intersectsLine(0-snake1.basicLength, 0-snake1.basicLength, WIDTH+snake1.basicLength, 0-snake1.basicLength) ||
           snake1.head.intersectsLine(0-snake1.basicLength, HEIGHT+snake1.basicLength, WIDTH+snake1.basicLength, HEIGHT+snake1.basicLength) ||
           snake1.head.intersectsLine(WIDTH+snake1.basicLength, 0-snake1.basicLength, WIDTH+snake1.basicLength, HEIGHT+snake1.basicLength)) {
            snake1 = new Snake();
            score = 0;
        }
    }
}
