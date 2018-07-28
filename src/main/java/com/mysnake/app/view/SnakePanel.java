package com.mysnake.app.view;

import com.mysnake.app.model.Food;
import com.mysnake.app.model.Snake;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.Rectangle2D;
import java.util.Locale;
import java.util.logging.*;
import java.io.*;

// due to issues with proxies for SnakePanel and creating every time the instances of the Snake and Food,
// it is almost impossible to use the logging in AOP style
public class SnakePanel extends JPanel implements KeyListener, Runnable {
    public static int WIDTH = 400;
    public static int HEIGHT = 400;
    private static int score = 0;

    private Snake snake1;
    private Food food;
    private Thread t;
    private Logger logger;
    private AbstractApplicationContext context;

    @Override
    public Locale getLocale() {
        return locale;
    }

    @Override
    public void setLocale(Locale locale) {
        this.locale = locale;
        logger.log(Level.INFO, "Set locale to " + locale.getLanguage());
    }

    private Locale locale;

    public SnakePanel() {
        setBackground(Color.WHITE);
        setOpaque(true);
        this.addKeyListener(this);

        context = new ClassPathXmlApplicationContext("file:src/main/resources/spring.xml");
        // setting for destruction all beans in the end of main()
        context.registerShutdownHook();

        try {
            final FileInputStream inputStream = new FileInputStream("logging.properties");
            LogManager.getLogManager().readConfiguration(inputStream);
        } catch (Exception e) {
            System.err.println("Couldn't load the logger configuration");
            e.printStackTrace();
        }

        logger = Logger.getLogger(SnakePanel.class.getName());
        logger.log(Level.INFO, "The game is started.");
        t = new Thread(this);
        t.start();
    }

    @Override
    public void run() {
        for (; ; ) {
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
        Snake.Trends temp = snake1.getTrendHead();

        switch (e.getKeyCode()) {
            // control of moving of the snake
            case KeyEvent.VK_W:
                temp = Snake.Trends.NORTH;
                break;
            case KeyEvent.VK_A:
                temp = Snake.Trends.WEST;
                break;
            case KeyEvent.VK_S:
                temp = Snake.Trends.SOUTH;
                break;
            case KeyEvent.VK_D:
                temp = Snake.Trends.EAST;
                break;

            // change the locale to the opposite
            case KeyEvent.VK_L:
                Locale lc = (Locale) ("en".equals(locale.getLanguage()) ?
                        context.getBean("localeRu") :
                        context.getBean("localeEn"));
                this.setLocale(lc);
        }

        if (!temp.isOpposite(snake1.getTrendHead()))
            snake1.setTrendHead(temp);

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
        g2.fill(snake1.getHead());
        for (int i = 0; i < snake1.getBody().length; i++)
            g2.fill(snake1.getBody()[i]);

        g2.setColor(Color.red);
        g2.draw(snake1.getHead());
        for (int i = 0; i < snake1.getBody().length; i++)
            g2.draw(snake1.getBody()[i]);

        snake1.snakeMove();
    }

    void drawFood(Graphics2D g2) {
        g2.setColor(food.getFoodColor());
        g2.fill(food.getFd());
    }


    public boolean isIntersectBody(Snake snake) {
        Rectangle2D head = snake.getHead();
        Rectangle2D[] body = snake.getBody();

        for (int i = 0; i < body.length; i++) {
            if (head.intersects(body[i])) {
                return true;
            }
        }

        return false;
    }

    public boolean isIntersectEdges(Rectangle2D head) {
        return (head.getMinX() < 0 || head.getMaxX() > WIDTH || head.getMinY() < 0 || head.getMaxY() > HEIGHT);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        if (snake1 == null)
            snake1 = (Snake) context.getBean("snake1");
        if (food == null)
            food = (Food) context.getBean("food");
        // default locale - russian
        if (locale == null)
            locale = (Locale) context.getBean("localeEn");

        // get the necessary prefix of the locale
        String lng = locale.getLanguage() + ".";

        // as context has many features, we can get messages from msgs.properties, using ResourceBundleMessageSource bean.
        // Getting messages from property files is handy for internationalization of the programs with many locales, for example
        String msg1 = context.getMessage(lng + "curpossnake",
                new Object[]{snake1.getHead().getX(), snake1.getHead().getY()},
                "Default snake coords", locale);
        g.drawString(msg1, 10, 10);
        //g.drawString("Координаты головы змеи: " + snake1.getHead().getX() + " , " + snake1.getHead().getY(), 10, 10);

        msg1 = context.getMessage(lng + "score", new Object[]{score}, "Default score", locale);
        g.drawString(msg1, 10, HEIGHT - 15);
        //g.drawString("Счёт: " + score, 10, HEIGHT - 15);

        msg1 = context.getMessage(lng + "curposfood",
                new Object[]{food.getX(), food.getY()},
                "Default food coords", locale);
        g.drawString(msg1, 10, HEIGHT);
        //g.drawString("Координаты текущей еды: " + food.getX() + " , " + food.getY(), 10, HEIGHT);

        drawSnake(g2);
        drawFood(g2);

        if (snake1.getHead().intersects(food.getFoodRect())) {
            food = (Food) context.getBean("food");
            score++;
            logger.log(Level.INFO, "Eaten the food, score: " + score);

            int bodyLength = snake1.getBody().length;

            if (score >= 2 * bodyLength - 7)
                snake1 = new Snake(snake1); // NullPointerException!
        }

        if (isIntersectBody(snake1)) {
            logger.log(Level.INFO, "Snake intersected itself, score: " + score);
            snake1 = (Snake) context.getBean("snake1");
            score = 0;
        }

        if (isIntersectEdges(snake1.getHead())) {
            logger.log(Level.INFO, "Snake intersected the edges, score: " + score);
            snake1 = (Snake) context.getBean("snake1");
            score = 0;
        }
    }
}
