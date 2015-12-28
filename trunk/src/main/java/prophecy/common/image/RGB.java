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

public class RGB {
  public final float r, g, b;

  public RGB(float r, float g, float b) {
    this.r = r;
    this.g = g;
    this.b = b;
  }

  public RGB(double r, double g, double b) {
    this.r = (float) r;
    this.g = (float) g;
    this.b = (float) b;
  }

  public RGB(double brightness) {
    this.r = this.g = this.b = (float) brightness;
  }

  public RGB(Color color) {
    this.r = color.getRed()/255f;
    this.g = color.getGreen()/255f;
    this.b = color.getBlue()/255f;
  }

  public float getComponent(int i) {
    return i == 0 ? r : i == 1 ? g : b;
  }

  public Color getColor() {
    return new Color(r, g, b);
  }

  public static RGB newSafe(float r, float g, float b) {
    return new RGB(Math.max(0, Math.min(1, r)), Math.max(0, Math.min(1, g)), Math.max(0, Math.min(1, b)));
  }

  public int asInt() {
    return PixelUtil.asInt(this);
  }

  public float getBrightness() {
    return (r+g+b)/3.0f;
  }

  public String getHexString() {
    return Integer.toHexString(asInt()).substring(2).toUpperCase();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof RGB)) return false;

    RGB rgb = (RGB) o;

    if (Float.compare(rgb.b, b) != 0) return false;
    if (Float.compare(rgb.g, g) != 0) return false;
    if (Float.compare(rgb.r, r) != 0) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = (r != +0.0f ? Float.floatToIntBits(r) : 0);
    result = 31 * result + (g != +0.0f ? Float.floatToIntBits(g) : 0);
    result = 31 * result + (b != +0.0f ? Float.floatToIntBits(b) : 0);
    return result;
  }

  public boolean isBlack() {
    return r == 0f && g == 0f && b == 0f;
  }

  public boolean isWhite() {
    return r == 1f && g == 1f && b == 1f;
  }
}