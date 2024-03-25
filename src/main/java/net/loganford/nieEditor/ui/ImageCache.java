package net.loganford.nieEditor.ui;

import lombok.extern.log4j.Log4j2;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

@Log4j2
public class ImageCache {
    private static ImageCache INSTANCE;

    public static ImageCache getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new ImageCache();
        }
        return INSTANCE;
    }


    private HashMap<String, ImageIcon> cache;
    public ImageCache() {
        cache = new HashMap<>();
    }

    public ImageIcon getImage(String filename, int width, int height) {
        return getImage(new File(filename), width, height);
    }

    public ImageIcon getImage(File file, int width, int height) {
        String key = file.getAbsolutePath() + "|" + width + "|" + height;
        ImageIcon result = cache.get(key);
        if(result == null) {
            try {
                BufferedImage image = ImageIO.read(file);

                float w_ratio = (float) width / image.getWidth();
                float h_ratio = (float) height / image.getHeight();
                float ratio = Math.min(w_ratio, h_ratio);

                result = new ImageIcon(image.getScaledInstance((int) (image.getWidth() * ratio), (int) (image.getHeight() * ratio), Image.SCALE_SMOOTH));
            }
            catch(IOException e) {
                log.error(e);
            }
            cache.put(key, result);
        }

        return result;
    }
}
