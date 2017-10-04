package snake;

import javax.swing.*;

/**
 * Created by 122 on 02.12.2015.
 */
public class SnakeGame {
  static void createAndShowGUI() {
            JFrame frame = new JFrame("MySnake");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            SnakePanel spanel = new SnakePanel();
            spanel.setFocusable(true);
            frame.add(spanel);
            frame.pack();
            frame.setVisible(true);
            frame.setResizable(false);
  }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() { createAndShowGUI(); }
        });
    }
}
