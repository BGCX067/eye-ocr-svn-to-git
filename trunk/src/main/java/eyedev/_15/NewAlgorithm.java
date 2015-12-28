package eyedev._15;

import drjava.util.Pair;
import eyedev._01.ImageReader;
import eyedev._01.OCRImageUtil;
import eyedev._10.PM2;
import prophecy.common.image.BWImage;
import prophecy.common.image.ImageProcessing;
import prophecy.common.image.RGBImage;

import java.awt.*;

public class NewAlgorithm extends ImageReader {
  PM2 pm;
  BWImage inputImage;
  private RGBImage markedImage;

  public String readImage(BWImage image) {
    image = OCRImageUtil.trim(image);
    image = ImageProcessing.addBorder(image, 1);
    inputImage = image;
    inputImage.setBorderColor(1f);
    markedImage = new RGBImage(inputImage);

    //printMatchesForEveryX();
    return strategy2();
  }

  private String strategy1() {
    StringBuffer buf = new StringBuffer();
    int x = 0;
    int wiggleLeft = 2, wiggleRight = 4;
    int lastX = -wiggleLeft;
    while (x < inputImage.getWidth()-1) {
      Pair<PM2.Item, Float> match = null;
      int bestX = 0;
      int xmin = Math.max(lastX + 1, x - wiggleLeft);
      int xmax = Math.min(x+wiggleRight, inputImage.getWidth()-1);
      System.out.println("xmin=" + xmin + ", xmax=" + xmax);
      if (xmax <= xmin) break;
      for (int x2 = xmin; x2 < xmax; x2++) {
        Pair<PM2.Item, Float> match2 = charMatch(x2);
        if (betterMatch(match, match2) == match2) {
          match = match2;
          bestX = x2;
        }
      }
      if (match == null)
        return null;
      x = bestX;
      lastX = x;
      System.out.println("match x=" + x + ": " + match.a.text + " (" + match.b + ")");
      buf.append(match.a.text);
      markImage(x, match.a);
      BWImage charImage = match.a.getImage();
      int charWidth = (int) (charImage.getWidth() * (float) inputImage.getHeight() / charImage.getHeight());
      x += Math.max(1, charWidth);
    }
    return buf.toString();
  }

  // like strategy1, just recursive
  private String strategy2() {
    return strategy2(0, -1000);
  }

  private String strategy2(int x, int lastX) {
    if (x >= inputImage.getWidth()-1) return "";

    int wiggleLeft = 2, wiggleRight = 4;
    Pair<PM2.Item, Float> match = null;
    int bestX = 0;
    int xmin = Math.max(lastX + 1, x - wiggleLeft);
    int xmax = Math.min(x+wiggleRight, inputImage.getWidth()-1);
    System.out.println("xmin=" + xmin + ", xmax=" + xmax);
    if (xmax <= xmin) return "";
    for (int x2 = xmin; x2 < xmax; x2++) {
      Pair<PM2.Item, Float> match2 = charMatch(x2);
      if (betterMatch(match, match2) == match2) {
        match = match2;
        bestX = x2;
      }
    }
    if (match == null)
      return null;
    x = bestX;
    lastX = x;
    System.out.println("match x=" + x + ": " + match.a.text + " (" + match.b + ")");
    markImage(x, match.a);
    BWImage charImage = match.a.getImage();
    int charWidth = (int) (charImage.getWidth() * (float) inputImage.getHeight() / charImage.getHeight());
    x += Math.max(1, charWidth);
    return match.a.text + strategy2(x, lastX);
  }

  private void markImage(int x1, PM2.Item item) {
    if (markedImage == null) return;
    BWImage charImage = resizeChar(item.getImage());
    BWImage mask = makeMask(charImage);
    for (int y = 0; y < charImage.getHeight(); y++)
      for (int x = 0; x < charImage.getWidth(); x++) {
        if (x1+x >= 0 && x1+x < markedImage.getWidth() && markedImage.getPixel(x1+x, y).isWhite()) {
          Color col = Color.white;
          if (charImage.getPixel(x, y) == 0f)
            col = new Color(1f, 0f, 0f);
          else if (mask.getPixel(x, y) == 1f)
            col = new Color(.75f, .75f, .75f);
          markedImage.setPixel(x1+x, y, col);
        }
      }
  }

  private Pair<PM2.Item, Float> bestMatch(int x1, int x2) {
    Pair<PM2.Item, Float> best = null;
    for (int x = x1; x < x2; x++)
      best = betterMatch(best, charMatch(x));
    return best;
  }

  private Pair<PM2.Item, Float> betterMatch(Pair<PM2.Item, Float> match1, Pair<PM2.Item, Float> match2) {
    if (match1 == null) return match2;
    if (match2 == null) return match1;
    return match1.b >= match2.b ? match1 : match2;
  }

  private void printMatchesForEveryX() {
    for (int x = 0; x < inputImage.getWidth(); x++) {
      Pair<PM2.Item, Float> match = charMatch(x);
      System.out.println("x=" + x + ": " + (match == null ? "no match" : match.a.text + " " + match.b));
    }
  }

  private Pair<PM2.Item, Float> charMatch(int x) {
    Pair<PM2.Item, Float> best = null;
    for (PM2.Item item : pm.getItems()) {
      float similarity = matchCharacter(item.getImage(), x);
      if (best == null || best.b < similarity)
        best = new Pair<PM2.Item, Float>(item, similarity);
    }
    return best;
  }

  private float matchCharacter(BWImage charImage, int x) {
    charImage = resizeChar(charImage);
    BWImage clippedInput = clipInput(inputImage, x, charImage.getWidth());
    BWImage mask = makeMask(charImage);
    BWImage img1 = OCRImageUtil.multiply(charImage, mask);
    BWImage img2 = OCRImageUtil.multiply(clippedInput, mask);
    float diffs = diffs(img1, img2);
    float maskPixelCount = maskPixelCount(mask);
    //float adjust = (mask.getWidth()*mask.getHeight()) / maskPixelCount;
    return 1f - diffs/maskPixelCount;
  }

  private BWImage resizeChar(BWImage charImage) {
    int h = inputImage.getHeight()-2;
    if (charImage.getHeight() != h) {
      int newWidth = (int) (charImage.getWidth() * (float) h / charImage.getHeight());
      charImage = ImageProcessing.resize(charImage, newWidth, h);
    }
    return ImageProcessing.addBorder(charImage, 1);
  }

  private float diffs(BWImage img1, BWImage img2) {
    double sum = 0.0;
    for (int y = 0; y < img1.getHeight(); y++)
      for (int x = 0; x < img1.getWidth(); x++)
        sum += Math.abs(img1.getPixel(x, y)-img2.getPixel(x, y));
    return (float) sum;
  }

  private float maskPixelCount(BWImage mask) {
    float sum = 0f;
    for (int y = 0; y < mask.getHeight(); y++)
      for (int x = 0; x < mask.getWidth(); x++)
        sum += mask.getPixel(x, y);
    return sum;
  }

  // makes a multiplication mask for the character
  // 1 = pixel is part of the character itself or a 1-pixel border around it
  // 0 = pixel is not part of the character
  private BWImage makeMask(BWImage charImage) {
    charImage.setBorderColor(1f);
    BWImage mask = new BWImage(charImage.getWidth(), charImage.getHeight(), 0f);
    for (int y = 0; y < charImage.getHeight(); y++)
      pixelLoop: for (int x = 0; x < charImage.getWidth(); x++) {
        for (int y2 = y-1; y2 <= y+1; y2++)
          for (int x2 = x-1; x2 <= x+1; x2++)
            if (charImage.getPixel(x2, y2) != 1f) {
              mask.setPixel(x, y, 1f);
              continue pixelLoop;
            }
      }
    return mask;
  }

  // this is flexible in terms of parts of the clip being outside the input image
  // (they will just show up as white pixels)
  private BWImage clipInput(BWImage inputImage, int x1, int width) {
    BWImage clip = new BWImage(width, inputImage.getHeight(), 1f);
    for (int y = 0; y < inputImage.getHeight(); y++)
      for (int x = 0; x < width; x++)
        clip.setPixel(x, y, inputImage.getPixel(x1+x, y));
    return clip;
  }

  public RGBImage getMarkedImage() {
    return markedImage;
  }
}
