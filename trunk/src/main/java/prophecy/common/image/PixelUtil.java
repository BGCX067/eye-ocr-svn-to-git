/*
(C) 2007 Stefan Reich (jazz@drjava.de)
This source file is part of Project Prophecy.
For up-to-date information, see http://www.drjava.de/prophecy

This source file is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation, version 2.1.
*/

package prophecy.common.image;

import java.awt.*;
import java.util.Collection;

public class PixelUtil {
  public static RGB getAverage(Collection<RGB> pixels) {
    double[] sums = new double[3];
    for (int c = 0; c < 3; c++) {
      for (RGB pixel : pixels) {
        sums[c] += pixel.getComponent(c);
      }
    }
    int count = pixels.size();
    return new RGB(sums[0]/count, sums[1]/count, sums[2]/count);
  }

  public static double getSquaredDiff(RGB a, RGB b) {
    return square(a.r-b.r) + square(a.g-b.g) + square(a.b-b.b);
  }

  private static double square(double r) {
    return r*r;
  }

  public static RGB blend(Color x, Color y, double yish) {
    return blend(new RGB(x), new RGB(y), yish);
  }

  public static RGB blend(RGB x, RGB y, double yish) {
    yish = Math.max(0, Math.min(1, yish));
    double xish = 1-yish;
    return new RGB(x.r*xish+y.r*yish, x.g*xish+y.g*yish, x.b*xish+y.b*yish);
  }

  public static int[] asInts(RGB[] pixels) {
    int[] ints = new int[pixels.length];
    for (int i = 0; i < pixels.length; i++)
      ints[i] = pixels[i] == null ? 0 : pixels[i].getColor().getRGB();
    return ints;
  }

  public static float getDiff(RGB a, RGB b) {
    return (Math.abs(a.r-b.r) + Math.abs(a.g-b.g) + Math.abs(a.b-b.b))/3;
  }

  public static RGB brighten(RGB rgb, float f) {
    return RGB.newSafe(rgb.r*f, rgb.g*f, rgb.b*f);
  }

  public static RGB add(RGB a, RGB b) {
    return RGB.newSafe(a.r+b.r, a.g+b.g, a.b+b.g);
  }

  public static RGB mul(RGB a, RGB b) {
    return RGB.newSafe(a.r*b.r, a.g*b.g, a.b*b.g);
  }

  public static RGB rgbAdjust(RGB rgb, float r, float g, float b) {
    return RGB.newSafe(rgb.r*r, rgb.g*g, rgb.b*b);
  }

  public static RGB[] asRGB(int[] pixels) {
    RGB[] rgbs = new RGB[pixels.length];
    for (int i = 0; i < pixels.length; i++) {
      int packed = pixels[i];
      RGB rgb = asRGB(packed);
      rgbs[i] = rgb;
    }
    return rgbs;
  }

  public static RGB asRGB(int packed) {
    int r = (packed >> 16) & 0xFF;
    int g = (packed >> 8) & 0xFF;
    int b = packed & 0xFF;
    return new RGB(r / 255f, g / 255f, b / 255f);
  }

  public static float getBrightness(RGB rgb) {
    return rgb.getBrightness();
  }

  public static float brighten(float pixel, float factor) {
    return fix(pixel*factor);
  }

  private static float fix(float pixel) {
    return Math.min(1, Math.max(0, pixel));
  }

  public static int asInt(RGB rgb) {
    return rgb.getColor().getRGB();
  }
}