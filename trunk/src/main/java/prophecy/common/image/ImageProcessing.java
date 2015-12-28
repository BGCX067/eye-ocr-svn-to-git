/*
(C) 2007 Stefan Reich (jazz@drjava.de)
This source file is part of Project Prophecy.
For up-to-date information, see http://www.drjava.de/prophecy

This source file is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation, version 2.1.
*/

package prophecy.common.image;

import drjava.util.Function;
import prophecy.common.Clippable;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ImageProcessing {
  public static BWImage hdiff(RGBImage image) {
    int w = image.getWidth(), h = image.getHeight();
    float[] pixels = new float[h*w];
    for (int y = 0; y < h; y++)
      for (int x = 0; x < w-1; x++) {
        pixels[y*w+x] = PixelUtil.getDiff(image.getRGB(x, y), image.getRGB(x+1, y));
      }
    return new BWImage(w, h, pixels);
  }

  public static BWImage vdiff(RGBImage image) {
    int w = image.getWidth(), h = image.getHeight();
    float[] pixels = new float[h*w];
    for (int y = 0; y < h-1; y++)
      for (int x = 0; x < w; x++) {
        pixels[y*w+x] = PixelUtil.getDiff(image.getRGB(x, y), image.getRGB(x, y+1));
      }
    return new BWImage(w, h, pixels);
  }

  public static RGBImage brighten(RGBImage image, float factor) {
    int w = image.getWidth(), h = image.getHeight();
    RGB[] pixels = new RGB[w*h];
    for (int y = 0; y < h; y++)
      for (int x = 0; x < w-1; x++) {
        pixels[y*w+x] = PixelUtil.brighten(image.getRGB(x, y), factor);
      }
    return new RGBImage(w, h, pixels);
  }

  public static BWImage brighten(BWImage image, float factor) {
    int w = image.getWidth(), h = image.getHeight();
    float[] pixels = new float[h*w];
    for (int y = 0; y < h; y++)
      for (int x = 0; x < w-1; x++) {
        pixels[y*w+x] = PixelUtil.brighten(image.getPixel(x, y), factor);
      }
    return new BWImage(w, h, pixels);
  }

  public static RGBImage add(RGBImage img1, RGBImage img2) {
    int w = img1.getWidth(), h = img2.getHeight();
    RGB[] pixels = new RGB[w*h];
    for (int y = 0; y < h; y++)
      for (int x = 0; x < w-1; x++) {
        pixels[y*w+x] = PixelUtil.add(img1.getRGB(x, y), img2.getRGB(x, y));
      }
    return new RGBImage(w, h, pixels);
  }

  public static BWImage add(BWImage img1, BWImage img2) {
    int w = img1.getWidth(), h = img2.getHeight();
    float[] pixels = new float[w*h];
    for (int y = 0; y < h; y++)
      for (int x = 0; x < w-1; x++) {
        pixels[y*w+x] = fixPixel(img1.getPixel(x, y) + img2.getPixel(x, y));
      }
    return new BWImage(w, h, pixels);
  }

  private static float fixPixel(float f) {
    return Math.min(1, Math.max(0, f));
  }

  public static RGBImage mul(RGBImage img1, RGBImage img2) {
    int w = img1.getWidth(), h = img2.getHeight();
    RGB[] pixels = new RGB[w*h];
    for (int y = 0; y < h; y++)
      for (int x = 0; x < w-1; x++) {
        pixels[y*w+x] = PixelUtil.mul(img1.getRGB(x, y), img2.getRGB(x, y));
      }
    return new RGBImage(w, h, pixels);
  }

  public static RGBImage rgbAdjust(RGBImage image, float r, float g, float b) {
    int w = image.getWidth(), h = image.getHeight();
    RGB[] pixels = new RGB[w*h];
    for (int y = 0; y < h; y++)
      for (int x = 0; x < w-1; x++) {
        pixels[y*w+x] = PixelUtil.rgbAdjust(image.getRGB(x, y), r, g, b);
      }
    return new RGBImage(w, h, pixels);
  }

  public static BWImage diff(RGBImage image, RGBImage image2) {
    int w = image.getWidth(), h = image.getHeight();
    float[] pixels = new float[h*w];
    for (int y = 0; y < h; y++)
      for (int x = 0; x < w; x++) {
        pixels[y*w+x] = PixelUtil.getDiff(image.getRGB(x, y), image2.getRGB(x, y));
      }
    return new BWImage(w, h, pixels);
  }

  public static RGBImage scale(RGBImage image, int scale) {
    int w = image.getWidth()*scale, h = image.getHeight()*scale;
    RGB[] pixels = new RGB[w*h];
    for (int y = 0; y < h; y++)
      for (int x = 0; x < w-1; x++) {
        pixels[y*w+x] = image.getRGB(x/scale, y/scale);
      }
    return new RGBImage(w, h, pixels);
  }

  public static List<Rectangle> getMiniclipRects(int imageWidth, int imageHeight, int miniClipWidth, int miniClipHeight,
                                                 int stepX, int stepY) {
    ArrayList<Rectangle> miniClips = new ArrayList<Rectangle>();
    for (int y = 0; y <= imageHeight-miniClipHeight; y += stepY)
      for (int x = 0; x <= imageWidth-miniClipWidth; x += stepX)
        miniClips.add(new Rectangle(x, y, miniClipWidth, miniClipHeight));
    return miniClips;
  }

  public static <A> List<A> getMiniclips(Clippable<A> image, int miniClipWidth, int miniClipHeight, int stepX, int stepY) {
    ArrayList<A> miniClips = new ArrayList<A>();
    for (Rectangle r : getMiniclipRects(image.getWidth(), image.getHeight(), miniClipWidth, miniClipHeight, stepX, stepY))
      miniClips.add(image.clip(r));
    return miniClips;
  }

  /*public static RGBImage loadBMP(File file) throws IOException {
    BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(file));
    BMPDecoder decoder = new BMPDecoder(inputStream);
    BufferedImage image = decoder.getBufferedImage();
    inputStream.close();
    int width = image.getWidth();
    int height = image.getHeight();
    //System.out.println("image size: " + width + "*" + height);
    int[] pixels = new int[width*height];
    PixelGrabber pixelGrabber = new PixelGrabber(image, 0, 0, width, height, pixels, 0, width);
    try {
      if (!pixelGrabber.grabPixels())
        throw new RuntimeException("Could not grab pixels");
    } catch (InterruptedException e) {
      throw new RuntimeException("oh please");
    }
    return new RGBImage(width, height, pixels);
  }*/

  public static BWImage threshold(BWImage image, double threshold) {
    int w = image.getWidth(), h = image.getHeight();
    float[] pixels = new float[h*w];
    for (int y = 0; y < h; y++)
      for (int x = 0; x < w; x++) {
        pixels[y*w+x] = image.getPixel(x, y) >= threshold ? 1 : 0;
      }
    return new BWImage(w, h, pixels);
  }

  public static BWImage apply(BWImage image, Function<Float, Float> function) {
    int w = image.getWidth(), h = image.getHeight();
    float[] pixels = new float[h*w];
    for (int y = 0; y < h-1; y++)
      for (int x = 0; x < w; x++) {
        pixels[y*w+x] = fixPixel(function.get(image.getPixel(x, y)));
      }
    return new BWImage(w, h, pixels);
  }

  public static RGBImage resize(RGBImage image, int w, int h) {
    if (w == image.getWidth() && h == image.getHeight()) return image;

    int[] pixels = new int[w*h];
    for (int y = 0; y < h; y++)
      for (int x = 0; x < w; x++)
        pixels[y*w+x] = image.getInt(x*image.getWidth()/w, y*image.getHeight()/h);
    return new RGBImage(w, h, pixels);
  }

  public static BWImage resize(BWImage image, int w, int h) {
    if (w == image.getWidth() && h == image.getHeight()) return image;

    float[] pixels = new float[w*h];
    for (int y = 0; y < h; y++)
      for (int x = 0; x < w; x++)
        pixels[y*w+x] = image.getPixel(x*image.getWidth()/w, y*image.getHeight()/h);
    return new BWImage(w, h, pixels);
  }

  public static BWImage resize(BWImage image, int w, int h, PixelMixer pixelMixer) {
    if (w == image.getWidth() && h == image.getHeight()) return image;

    int iw = image.getWidth();
    int ih = image.getHeight();
    float[] pixels = new float[w*h];
    for (int y = 0; y < h; y++)
      for (int x = 0; x < w; x++) {
        int ix = x * iw / w;
        int iy = y * ih / h;
        BWImage clip = image.clip(new Rectangle(ix, iy, (x + 1) * iw / w - ix, (y + 1) * ih / h - iy));
        pixels[y*w+x] = pixelMixer.mix(clip);
      }
    return new BWImage(w, h, pixels);
  }

  public static RGBImage resize(RGBImage image, int w, int h, RGBPixelMixer pixelMixer) {
    if (w == image.getWidth() && h == image.getHeight()) return image;

    int iw = image.getWidth();
    int ih = image.getHeight();
    int[] pixels = new int[w*h];
    for (int y = 0; y < h; y++)
      for (int x = 0; x < w; x++) {
        int ix = x * iw / w;
        int iy = y * ih / h;
        RGBImage clip = image.clip(new Rectangle(ix, iy, (x + 1) * iw / w - ix, (y + 1) * ih / h - iy));
        pixels[y*w+x] = pixelMixer.mix(clip).asInt();
      }
    return new RGBImage(w, h, pixels);
  }

  public static void copy(BWImage src, int srcX, int srcY, BWImage dst, int dstX, int dstY, int w, int h) {
    for (int y = 0; y < h; y++)
      for (int x = 0; x < w; x++)
        dst.setPixel(dstX+x, dstY+y, src.getPixel(srcX+x, srcY+y));
  }

  public static void copy(RGBImage src, int srcX, int srcY, RGBImage dst, int dstX, int dstY, int w, int h) {
    for (int y = 0; y < h; y++)
      for (int x = 0; x < w; x++)
        dst.setPixel(dstX+x, dstY+y, src.getPixel(srcX+x, srcY+y));
  }

  public static BWImage addBorder(BWImage originalImage, int borderSize) {
    return addBorder(originalImage, borderSize, borderSize, borderSize, borderSize);
  }

  public static BWImage addBorder(BWImage originalImage, int left, int top, int right, int bottom) {
    BWImage image = new BWImage(originalImage.getWidth()+left+right,
      originalImage.getHeight()+top+bottom, 1);
    copy(originalImage, 0, 0, image, left, top, originalImage.getWidth(), originalImage.getHeight());
    return image;
  }

  public static RGBImage addBorder(RGBImage originalImage, int borderSize) {
    return addBorder(originalImage, borderSize, borderSize, borderSize, borderSize);
  }

  public static RGBImage addBorder(RGBImage originalImage, int left, int top, int right, int bottom) {
    RGBImage image = new RGBImage(
      originalImage.getWidth()+left+right,
      originalImage.getHeight()+top+bottom, new RGB(Color.white));
    copy(originalImage, 0, 0, image, left, top, originalImage.getWidth(), originalImage.getHeight());
    return image;
  }

  public static long hash(RGBImage image) {
    return Arrays.hashCode(image.getPixels())
      + image.getWidth() + image.getHeight() * 10000;
  }

  public static RGBImage resizeToWidth(RGBImage image, int newWidth, RGBPixelMixer pixelMixer) {
    int newHeight = (int) ((double) newWidth/image.getWidth()*image.getHeight());
    return resize(image, newWidth, newHeight, pixelMixer);
  }

  public static void fillRect(RGBImage image, int x1, int y1, int w, int h, RGB color) {
    int x2 = Math.min(x1+w, image.getWidth());
    int y2 = Math.min(y1+h, image.getHeight());
    for (int y = y1; y < y2; y++)
      for (int x = x1; x < x2; x++)
        image.setPixel(x, y, color);
  }

  public static void fillRect(BWImage image, int x1, int y1, int w, int h, float color) {
    int x2 = Math.min(x1+w, image.getWidth());
    int y2 = Math.min(y1+h, image.getHeight());
    for (int y = y1; y < y2; y++)
      for (int x = x1; x < x2; x++)
        image.setPixel(x, y, color);
  }

  public static void drawRect(RGBImage image, int x1, int y1, int w, int h, RGB color) {
    for (int y = y1; y <= y1+h; y++) {
      image.setPixel(x1, y, color);
      image.setPixel(x1+w, y, color);
    }

    for (int x = x1+1; x < x1+w; x++) {
      image.setPixel(x, y1, color);
      image.setPixel(x, y1+h, color);
    }
  }
}