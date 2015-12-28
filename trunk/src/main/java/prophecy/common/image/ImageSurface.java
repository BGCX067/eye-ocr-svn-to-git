/*
(C) 2007 Stefan Reich (jazz@drjava.de)
This source file is part of Project Prophecy.
For up-to-date information, see http://www.drjava.de/prophecy

This source file is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation, version 2.1.
*/

package prophecy.common.image;

import drjava.util.Errors;
import drjava.util.PopupMenuHelper;
import prophecy.common.Prophecy;
import prophecy.common.Surface;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class ImageSurface extends Surface {
  private BufferedImage image;
  private double zoomX = 1, zoomY = 1;

  public ImageSurface() {
    this(new RGBImage(1, 1, new int[] { 0xFFFFFF }));
  }

  public ImageSurface(RGBImage image) {
    this(image.getBufferedImage());
  }

  public ImageSurface(BufferedImage image) {
    clearSurface = false;
    this.image = image;
    new PopupMenuHelper() {
      protected void fillMenu(JPopupMenu menu, Point point) {
        Point p = new Point((int) (point.x/getZoomX()), (int) (point.y/getZoomY()));
        fillPopupMenu(menu, p);
      }
    }.install(this);
  }

  public ImageSurface(BWImage image) {
    this(image.toRGB());
  }

  public ImageSurface(RGBImage image, double zoom) {
    this(image);
    setZoom(zoom);
  }

  public ImageSurface(BWImage image, double zoom) {
    this(image);
    setZoom(zoom);
  }

  protected void fillPopupMenu(JPopupMenu menu, Point point) {
    JMenuItem miZoomReset = new JMenuItem("Zoom 100%");
    miZoomReset.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        setZoom(1.0);
      }
    });
    menu.add(miZoomReset);

    JMenuItem miZoomIn = new JMenuItem("Zoom in");
    miZoomIn.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        setZoom(getZoomX()*2.0, getZoomY()*2.0);
      }
    });
    menu.add(miZoomIn);

    JMenuItem miZoomOut = new JMenuItem("Zoom out");
    miZoomOut.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        setZoom(getZoomX()*0.5, getZoomY()*0.5);
      }
    });
    menu.add(miZoomOut);

    menu.addSeparator();

    JMenuItem miSave = new JMenuItem("Save image...");
    miSave.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        saveImage();
      }
    });
    menu.add(miSave);
  }

  public void saveImage() {
    RGBImage image = new RGBImage(getImage(), null);
    JFileChooser fileChooser = new JFileChooser(Prophecy.dataDir());
    if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
      try {
        image.save(fileChooser.getSelectedFile());
      } catch (IOException e) {
        Errors.add(e);
      }
    }
  }

  public void loadImage() {
    JFileChooser fileChooser = new JFileChooser(Prophecy.dataDir());
    if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
      try {
        RGBImage image = RGBImage.load(fileChooser.getSelectedFile());
        setImage(image);
      } catch (Throwable e) {
        Errors.add(e);
      }
    }
  }

  public void render(int w, int h, Graphics2D g) {
    if (image != null)
      g.drawImage(image, 0, 0, getZoomedWidth(), getZoomedHeight(), null);
  }

  public void setZoom(double zoom) {
    setZoom(zoom, zoom);
  }

  public void setZoom(double zoomX, double zoomY) {
    this.zoomX = zoomX;
    this.zoomY = zoomY;
    revalidate();
    repaint();
  }

  public Dimension getMinimumSize() {
    int w = getZoomedWidth();
    int h = getZoomedHeight();
    Dimension min = super.getMinimumSize();
    return new Dimension(Math.max(w, min.width), Math.max(h, min.height));
  }

  private int getZoomedHeight() {
    return (int) (image.getHeight() * zoomY);
  }

  private int getZoomedWidth() {
    return (int) (image.getWidth() * zoomX);
  }

  public void setImage(RGBImage image) {
    setImage(image.getBufferedImage());
  }

  public void setImage(BufferedImage image) {
    this.image = image;
    repaint();
  }

  public BufferedImage getImage() {
    return image;
  }

  public void setImage(BWImage image) {
    setImage(image.toRGB());
  }

  public double getZoomX() {
    return zoomX;
  }

  public double getZoomY() {
    return zoomY;
  }

  public Dimension getPreferredSize() {
    return new Dimension(getZoomedWidth(), getZoomedHeight());
  }

  /** returns a scrollpane with the scroll-mode prevent-garbage-drawing fix applied */
  public JScrollPane makeScrollPane() {
    JScrollPane scrollPane = new JScrollPane(this);
    scrollPane.getViewport().setScrollMode(JViewport.BACKINGSTORE_SCROLL_MODE);
    return scrollPane;
  }
}