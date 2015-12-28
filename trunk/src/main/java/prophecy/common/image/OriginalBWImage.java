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

import static java.lang.System.arraycopy;

public class OriginalBWImage implements Visualizable, Clippable<OriginalBWImage> {
  private int width, height;
  private float[] pixels;

  // color returned when getPixel is called with a position outside the actual image
  private float borderColor = 0.0f;

  public OriginalBWImage(int width, int height, float[] pixels) {
    this.pixels = pixels;
    this.height = height;
    this.width = width;
  }

  public OriginalBWImage(OriginalBWImage image) {
    this(image.width, image.height, copyPixels(image.pixels));
  }

  public OriginalBWImage(int width, int height, float brightness) {
    this.width = width;
    this.height = height;
    pixels = new float[width*height];
    for (int i = 0; i < pixels.length; i++)
      pixels[i] = brightness;
  }

  private static float[] copyPixels(float[] pixels) {
    float[] copy = new float[pixels.length];
    arraycopy(pixels, 0, copy, 0, pixels.length);
    return copy;
  }

  public double averageBrightness() {
    double sum = 0;
    for (int y = 0; y < height; y++)
      for (int x = 0; x < width; x++)
        sum += pixels[y*width+x];
    return (sum/(double) (height*width));
  }

  public float getPixel(int x, int y) {
    return inRange(x, y) ? pixels[y*width+x] : borderColor;
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
    RGB[] rgbs = new RGB[width*height];
    for (int y = 0; y < height; y++)
      for (int x = 0; x < width; x++) {
        float p = pixels[y*width+x];
        rgbs[y*width+x] = new RGB(p, p, p);
      }
    return new RGBImage(width, height, rgbs);
  }

  public OriginalBWImage clip(int x, int y, int w, int h) {
    return clip(new Rectangle(x, y, w, h));
  }

  private Rectangle fixClipRect(Rectangle r) {
    return r.intersection(new Rectangle(0, 0, width, height));
  }

  public OriginalBWImage clip(Rectangle r) {
    r = fixClipRect(r);
    float[] newPixels = new float[r.height*r.width];
    for (int y = 0; y < r.height; y++)
      System.arraycopy(pixels, (r.y+y)*width+r.x, newPixels, y*r.width, r.width);
    return new OriginalBWImage(r.width, r.height, newPixels);
  }

  public JComponent visualize() {
    RGBImage rgb = toRGB();
    int scale = Math.min(8, 300/Math.max(width, height));
    if (scale > 1) rgb = ImageProcessing.scale(rgb, scale);
    return new JLabel(new ImageIcon(rgb.getBufferedImage()));
  }

  public void setPixel(int x, int y, float brightness) {
    pixels[y*width+x] = fixPixel(brightness);
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