package net.loganford.nieEditor.util;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

@Log4j2
public class CachedImage {
    @Getter private String filename;

    private HashMap<String, ImageIcon> sizeMap = new HashMap<>();
    private BufferedImage image;
    private ImageIcon icon;

    public CachedImage(File file) {
        this.filename = filename;

        try {
            image = ImageIO.read(file);
            icon = new ImageIcon(image);
        }
        catch(IOException e) {
            log.error("File path caused error: " + file.getAbsolutePath());
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof CachedImage) {
            return filename.equals(((CachedImage)obj).getFilename());
        }
        return false;
    }

    public ImageIcon getImage() {
        return icon;
    }

    public ImageIcon getImage(int width, int height) {
        String key = width + "|" + height;
        ImageIcon foundIcon = sizeMap.get(key);

        if(foundIcon == null) {
            float w_ratio = (float) width / image.getWidth();
            float h_ratio = (float) height / image.getHeight();
            float ratio = Math.min(w_ratio, h_ratio);

            foundIcon = new ImageIcon(image.getScaledInstance((int) (image.getWidth() * ratio), (int) (image.getHeight() * ratio), Image.SCALE_SMOOTH));

            sizeMap.put(key, foundIcon);
        }

        return foundIcon;
    }
}
