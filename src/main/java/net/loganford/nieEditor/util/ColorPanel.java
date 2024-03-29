package net.loganford.nieEditor.util;

import lombok.Getter;
import lombok.Setter;

import javax.swing.*;
import java.awt.*;

public class ColorPanel extends JPanel {
    public static int WIDTH = 128;
    public static int HEIGHT = 24;

    @Getter Color color;

    public ColorPanel(Color color) {
        this.color = color;

        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setSize(new Dimension(WIDTH, HEIGHT));
        setMaximumSize(new Dimension(WIDTH, HEIGHT));
        setMinimumSize(new Dimension(WIDTH, HEIGHT));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.setColor(color);
        g.fillRect(0, 0, WIDTH, HEIGHT);
    }

    public void setColor(Color color) {
        this.color = color;
        repaint();
    }
}
