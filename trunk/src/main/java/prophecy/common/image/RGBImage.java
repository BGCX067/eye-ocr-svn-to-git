/*
 (C) 2007 Stefan Reich (jazz@drjava.de)
 This source file is part of Project Prophecy.
 For up-to-date information, see http://www.drjava.de/prophecy

 This source file is free software; you can redistribute it and/or
 modify it under the terms of the GNU Lesser General Public
 License as published by the Free Software Foundation, version 2.1.
 */
package prophecy.common.image;

import drjava.util.ToTree;
import drjava.util.Tree;
import prophecy.common.Clippable;
import prophecy.common.Visualizable;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.PixelGrabber;
import java.io.File;
import java.io.IOException;

import static java.lang.System.arraycopy;

public class RGBImage implements Clippable<RGBImage>, Visualizable, ToTree {

    private BufferedImage bufferedImage;
    private File file;
    private int width, height;
    private int[] pixels;
    // color returned when getPixel is called with out-of-bounds position
    private int background = 0xFFFFFF;

    public RGBImage(BufferedImage image) {
        this(image, null);
    }

    public RGBImage(BufferedImage image, File file) {
        this.file = file;
        bufferedImage = image;
        width = image.getWidth();
        height = image.getHeight();
        pixels = new int[width * height];
        PixelGrabber pixelGrabber = new PixelGrabber(image, 0, 0, width, height, pixels, 0, width);
        try {
            if (!pixelGrabber.grabPixels()) {
                throw new RuntimeException("Could not grab pixels");
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public RGBImage(int width, int height, int[] pixels) {
        this.width = width;
        this.height = height;
        this.pixels = pixels;
    }

    public RGBImage(int w, int h, RGB[] pixels) {
        this.width = w;
        this.height = h;
        this.pixels = PixelUtil.asInts(pixels);
    }

    public RGBImage(int w, int h, RGB rgb) {
        this.width = w;
        this.height = h;
        this.pixels = new int[w * h];
        for (int i = 0; i < pixels.length; i++) {
            pixels[i] = rgb.asInt();
        }
    }

    public RGBImage(RGBImage image) {
        this(image.width, image.height, copyPixels(image.pixels));
    }

    public RGBImage(BWImage image) {
        this(image.getWidth(), image.getHeight(), image.toRGB().pixels);
    }

    private static int[] copyPixels(int[] pixels) {
        int[] copy = new int[pixels.length];
        arraycopy(pixels, 0, copy, 0, pixels.length);
        return copy;
    }

    public BWImage toBW() {
        byte[] result = new byte[height * width];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                RGB rgb = getRGB(x, y);
                result[y * width + x] = BWImage.toByte(PixelUtil.getBrightness(rgb));
            }
        }
        return new BWImage(width, height, result);
    }

    public BWImage redChannel() {
        float[] result = new float[height * width];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                RGB rgb = getRGB(x, y);
                result[y * width + x] = rgb.r;
            }
        }
        return new BWImage(width, height, result);
    }

    public BWImage greenChannel() {
        float[] result = new float[height * width];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                RGB rgb = getRGB(x, y);
                result[y * width + x] = rgb.g;
            }
        }
        return new BWImage(width, height, result);
    }

    public BWImage blueChannel() {
        float[] result = new float[height * width];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                RGB rgb = getRGB(x, y);
                result[y * width + x] = rgb.b;
            }
        }
        return new BWImage(width, height, result);
    }

    public int getIntPixel(int x, int y) {
        return pixels[y * width + x];
    }

    public RGB getRGB(int x, int y) {
        if (inRange(x, y)) {
            return PixelUtil.asRGB(pixels[y * width + x]);
        } else {
            return new RGB(background);
        }
    }

    /**
     * alias of getRGB - I kept typing getPixel instead of getRGB all the time,
     * so I finally created it
     */
    public RGB getPixel(int x, int y) {
        return getRGB(x, y);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    /**
     * @NotNull
     */
    public BufferedImage getBufferedImage() {
        if (bufferedImage == null) {
            bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            //bufferedImage.setData(Raster.createRaster(new SampleModel()));
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    bufferedImage.setRGB(x, y, pixels[y * width + x]);
                }
            }
        }
        return bufferedImage;
    }

    public RGBImage clip(Rectangle r) {
        r = fixClipRect(r);
        int[] newPixels;
        try {
            newPixels = new int[r.width * r.height];
        } catch (RuntimeException e) {
            System.out.println(r);
            throw e;
        }
        for (int y = 0; y < r.height; y++) {
            System.arraycopy(pixels, (y + r.y) * width + r.x, newPixels, y * r.width, r.width);
        }
        return new RGBImage(r.width, r.height, newPixels);
    }

    private Rectangle fixClipRect(Rectangle r) {
        r = r.intersection(new Rectangle(0, 0, width, height));
        if (r.isEmpty()) {
            r = new Rectangle(r.x, r.y, 0, 0);
        }
        return r;
    }

    public JComponent visualize() {
        return new ImageSurface(this);
    }

    public Tree toTree() {
        return new Tree(getClass()).add("file", file != null ? file.getPath() : null);
    }

    public File getFile() {
        return file;
    }

    /**
     * can now also do GIF (not just JPEG)
     */
    public static RGBImage load(String fileName) {
        return load(new File(fileName));
    }

    /**
     * can now also do GIF (not just JPEG)
     */
    public static RGBImage load(File file) {
        try {
            BufferedImage bufferedImage = ImageIO.read(file);
            return new RGBImage(bufferedImage);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public int getInt(int x, int y) {
        return pixels[y * width + x];
    }

    public void save(File file) throws IOException {
        ImageIO.write(getBufferedImage(), "jpeg", file);
    }

    public static RGBImage dummyImage() {
        return new RGBImage(1, 1, new int[]{0xFFFFFF});
    }

    public int[] getPixels() {
        return pixels;
    }

    public void setPixel(int x, int y, RGB rgb) {
        if (x >= 0 && y >= 0 && x < width && y < height) {
            pixels[y * width + x] = rgb.asInt();
        }
    }

    public void setPixel(int x, int y, Color color) {
        setPixel(x, y, new RGB(color));
    }

    public RGBImage copy() {
        return new RGBImage(this);
    }

    public boolean inRange(int x, int y) {
        return x >= 0 && y >= 0 && x < width && y < height;
    }

    public int getBackground() {
        return background;
    }

    public void setBackground(int background) {
        this.background = background;
    }
}
