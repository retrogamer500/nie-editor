package net.loganford.nieEditor.util;

import net.loganford.nieEditor.ui.Window;

import javax.swing.*;
import java.awt.*;

public class IconRadioButton extends JRadioButton {
    public IconRadioButton(Icon icon) {
        super(icon);
    }

    public IconRadioButton(Icon icon, boolean selected) {
        super(icon, selected);
    }

    @Override
    public void paint(Graphics g) {
        Color color = Color.BLACK;
        if(Window.darkMode) {
            color = Color.white;
        }

        if(this.getMousePosition() != null) {
            g.setColor(Color.LIGHT_GRAY);
            g.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
        }

        if(isSelected()) {
            g.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 32));
            g.fillRect(0, 0, getWidth() - 1, getHeight() - 1);

            g.setColor(Color.WHITE);
            g.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
        }

        if(this.getMousePosition() != null) {
            g.translate(0, 1);
        }

        super.paint(g);
    }
}
