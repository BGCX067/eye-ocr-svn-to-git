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
import drjava.util.MultiCoreUtil;
import drjava.util.Pair;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.stream.ImageInputStream;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.applet.Applet;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class ImageSet implements Iterable<ImageSet.Image> {
  private ArrayList<Image> images = new ArrayList<Image>();
  public static Applet applet;

  public ImageSet() {
  }

  public ImageSet(final String dir, String... names) {
    init(dir, names);
  }

  protected void init(final String dir, String... names) {
    long t1 = System.currentTimeMillis();
    images.addAll(MultiCoreUtil.parallelMap(Arrays.asList(names), new Function<String, Image>() {
      public Image get(String imgName) {
        String fileName = (dir.length() == 0 ? "" : dir + "/") + imgName + ".jpg";
        File file = new File(fileName);
        return loadImage(imgName, file);
      }
    }));

    long t2 = System.currentTimeMillis();
    System.out.println(images.size() + " images loaded in " + (t2-t1) + " ms");
  }

  static Image loadImage(String imgName, File file) {
    Pair<BufferedImage, IIOMetadata> pair = null;
    try {
      pair = getImageAndMetadata(file);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    String comment = getJPEGComment(pair.b);
    return new Image(new RGBImage(pair.a, file), imgName, comment);
  }

  public static AllImages allImages(String dir) {
    return new AllImages(dir);
  }

  private BufferedImage loadImage(String fileName) throws IOException {
    File file = new File(fileName);
    BufferedImage image = ImageIO.read(file);
    //System.out.println(fileName + ": " + image.getWidth() + "*" + image.getHeight());
    return image;
  }

  private static Pair<BufferedImage, IIOMetadata> getImageAndMetadata(File file) throws IOException {
    ImageInputStream iis;
    if (applet != null) {
      // BROKEN
      //iis = ImageIO.createImageInputStream(applet.getAppletContext().getStream(file.getPath()));
      /*URL url = new URL(applet.getDocumentBase(), file.getPath());
      System.out.println(url);
      java.awt.Image image = applet.getAppletContext().getImage(url);*/
      return null;
    } else {
      if (!file.exists())
        throw new FileNotFoundException(file + " not found");
      iis = ImageIO.createImageInputStream(file);
    }
    ImageReader imageReader = ImageIO.getImageReadersByFormatName("jpeg").next();
    imageReader.setInput(iis);
    BufferedImage image = imageReader.read(0);
    Pair<BufferedImage, IIOMetadata> result = new Pair<BufferedImage, IIOMetadata>(image, imageReader.getImageMetadata(0));
    iis.close();
    return result;
  }

  private static String getJPEGComment(IIOMetadata metadata) {
    try {
      if (metadata != null) {
        Node root = metadata.getAsTree(metadata.getNativeMetadataFormatName());
        NodeList result = (NodeList) XPathFactory.newInstance().newXPath().compile("//com")
          .evaluate(root, XPathConstants.NODESET);
        //displayMetadata(root);
        if (result.getLength() != 0)
          return result.item(0).getAttributes().getNamedItem("comment").getNodeValue();
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return "";
  }

  public List<Image> getImages() {
    return images;
  }

  public RGBImage getImage(int i) {
    return images.get(i).image;
  }

  public void add(RGBImage image) {
    images.add(new Image(image, "", ""));
  }

  public void add(RGBImage image, String comment) {
    images.add(new Image(image, "", comment));
  }

  public int size() {
    return images.size();
  }

  public Iterator<Image> iterator() {
    return images.iterator();
  }

  public static class Image {
    public final RGBImage image;
    public final String name;
    public String comment;

    public Image(RGBImage image, String name, String comment) {
      this.image = image;
      this.name = name;
      this.comment = comment;
    }
  }
}