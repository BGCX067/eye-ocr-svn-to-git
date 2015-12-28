/*
 (C) 2007 Stefan Reich (jazz@drjava.de)
 This source file is part of Project Prophecy.
 For up-to-date information, see http://www.drjava.de/prophecy

 This source file is free software; you can redistribute it and/or
 modify it under the terms of the GNU Lesser General Public
 License as published by the Free Software Foundation, version 2.1.
 */
package prophecy.common.image;

import prophecy.common.Clippable;
import prophecy.common.Visualizable;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.PixelGrabber;

import static java.lang.System.arraycopy;
import org.apache.log4j.Logger;

public class BWImage implements Visualizable, Clippable<BWImage> {

    private Logger l = Logger.getLogger(BWImage.class);
    private int width, height;
    private BWImageStorage storage;
    // color returned when getPixel is called with a position outside the actual image
    private float borderColor = 0.0f;

    public BWImage(int width, int height, float[] pixels) {
        this.width = width;
        this.height = height;
        byte[] bytePixels = new byte[pixels.length];
        for (int i = 0; i < pixels.length; i++) {
            bytePixels[i] = toByte(pixels[i]);
        }
        storage = makeStorage(width, height, bytePixels);
    }

    private BWImageStorage makeStorage(int width, int height, byte[] bytePixels) {
        if (width < BWImageTiledStorage.ts || height < BWImageTiledStorage.ts) {
            return new BWImageSimpleStorage(width, height, bytePixels);
        } else {
            return new BWImageTiledStorage(width, height, bytePixels);
        }
    }

    public BWImage(int width, int height, byte[] pixels) {
        this.height = height;
        this.width = width;
        storage = makeStorage(width, height, pixels);
    }

    public BWImage(BWImage image) {
        width = image.getWidth();
        height = image.getHeight();
        byte[] bytePixels = new byte[width * height];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                bytePixels[y * width + x] = image.getByte(x, y);
            }
        }
        storage = makeStorage(width, height, bytePixels);
    }

    public BWImage(BufferedImage image) {
        BWImage bwImage = new RGBImage(image).toBW();
        if (bwImage == null) {
            l.debug("bwImage is null");
        } else {

            width = bwImage.width;
            height = bwImage.height;
            storage = bwImage.storage;
        }
    }

    /*
     * too slow! public BWImage(BufferedImage image) { int width =
     * image.getWidth(), height = image.getHeight(); byte[] pixels = new
     * byte[width*height]; int[] linePixels = new int[width]; for (int y = 0; y
     * < height; y++) { PixelGrabber pixelGrabber = new PixelGrabber(image, 0,
     * y, width, 1, linePixels, 0, width); try { if (!pixelGrabber.grabPixels())
     * throw new RuntimeException("Could not grab pixels"); } catch
     * (InterruptedException e) { throw new RuntimeException(e); } for (int x =
     * 0; x < width; x++) pixels[y*width+x] = toByte(new
     * RGB(linePixels[x]).getBrightness()); } }
     */
    public byte getByte(int x, int y) {
        return toByte(getPixel(x, y));
    }

    public BWImage(int width, int height, float brightness) {
        this.width = width;
        this.height = height;
        byte[] pixels = new byte[width * height];
        for (int i = 0; i < pixels.length; i++) {
            pixels[i] = toByte(brightness);
        }
        storage = makeStorage(width, height, pixels);
    }

    public double averageBrightness() {
        double sum = 0;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                sum += getPixel(x, y);
            }
        }
        return (sum / (double) (height * width));
    }

    public float getPixel(int x, int y) {
        return inRange(x, y) ? toFloat(storage.getByte(x, y)) : borderColor;
    }

    public static byte toByte(float pixel) {
        return (byte) (pixel * 255f);
    }

    public static float toFloat(byte pixel) {
        return (((int) pixel) & 255) / 255f;
    }

    private boolean inRange(int x, int y) {
        return x >= 0 && x < width && y >= 0 && y < height;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public RGBImage toRGB() {
        RGB[] rgbs = new RGB[width * height];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                float p = getPixel(x, y);
                rgbs[y * width + x] = new RGB(p, p, p);
            }
        }
        return new RGBImage(width, height, rgbs);
    }

    public BWImage clip(int x, int y, int w, int h) {
        return clip(new Rectangle(x, y, w, h));
    }

    private Rectangle fixClipRect(Rectangle r) {
        return r.intersection(new Rectangle(0, 0, width, height));
    }

    /**
     * this should be multithread-safe
     */
    public BWImage clip(Rectangle r) {
        r = fixClipRect(r);
        byte[] newPixels = new byte[r.height * r.width];
        for (int y = 0; y < r.height; y++) {
            for (int x = 0; x < r.width; x++) {
                newPixels[y * r.width + x] = getByte(r.x + x, r.y + y);
            }
        }
        return new BWImage(r.width, r.height, newPixels);
    }

    public JComponent visualize() {
        RGBImage rgb = toRGB();
        int scale = Math.min(8, 300 / Math.max(width, height));
        if (scale > 1) {
            rgb = ImageProcessing.scale(rgb, scale);
        }
        return new JLabel(new ImageIcon(rgb.getBufferedImage()));
    }

    public void setPixel(int x, int y, float brightness) {
        storage.setByte(x, y, toByte(fixPixel(brightness)));
    }

    private float fixPixel(float pixel) {
        return Math.max(0, Math.min(1, pixel));
    }

    public float getBorderColor() {
        return borderColor;
    }

    public void setBorderColor(float borderColor) {
        this.borderColor = borderColor;
    }
}