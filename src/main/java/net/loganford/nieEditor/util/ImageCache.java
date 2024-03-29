package net.loganford.nieEditor.util;

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


    private HashMap<String, CachedImage> imageCache = new HashMap<>();

    public ImageIcon getImage(File file) {
        return getCachedImage(file).getImage();
    }

    public ImageIcon getImage(File file, int width, int height) {
        return getCachedImage(file).getImage(width, height);
    }

    public void clearCache(File file) {
        if(file != null) {
            String key = file.getAbsolutePath();
            imageCache.remove(key);
        }
    }

    private CachedImage getCachedImage(File file) {
        String key = file.getAbsolutePath();
        CachedImage foundCachedImage = imageCache.get(key);

        if(foundCachedImage == null) {
            foundCachedImage = new CachedImage(file);
            imageCache.put(key, foundCachedImage);
        }

        return foundCachedImage;
    }
}
