package eye.eye01;

import eyedev._01.ImageWithMarkLines;
import eyedev._01.OCRImageUtil;
import eyedev._17.BaseLineFinder;
import eyedev._17.TopLineFinder;
import prophecy.common.SurfaceUtil;
import prophecy.common.image.BWImage;
import prophecy.common.image.ImageSurface;

import java.awt.*;
import java.awt.image.BufferedImage;

// TODO: Use maxAscent instead of ascent? Can umlauts/accents possibly extend the box we reserve?
public class TextPainter2 {
  private FontMetrics metrics;
  private Font font;
  public static final int defaultInset = 5;
  private int topLine, baseLine;

  public TextPainter2(Font font) {
    this.font = font;

    // Make 1x1 image to get the font metrics...
    BufferedImage dummyImage = new BufferedImage(1, 1, BufferedImage.TYPE_BYTE_BINARY);
    Graphics g = dummyImage.getGraphics();
    metrics = g.getFontMetrics(font);
  }

  public BWImage makeImage(String text) {
    return makeImage(text, defaultInset);
  }


  public BWImage makeImage(String text, int inset) {
    return makeImageWithMarkLines(text, inset, false).image;
  }

  public ImageWithMarkLines makeImageWithMarkLines(String text, int inset, boolean needMarkLines) {
    String[] lines = text.split("\r?\n");
    Dimension size = sizeNeeded(lines);
    BufferedImage image = new BufferedImage(size.width+inset*2, size.height+inset*2, BufferedImage.TYPE_BYTE_BINARY);
    Graphics g = image.getGraphics();
    g.setColor(Color.white);
    g.fillRect(0, 0, image.getWidth(), image.getHeight());
    g.setColor(Color.black);
    g.setFont(font);

    int y = metrics.getAscent()+inset;
    for (String line : lines) {
      g.drawString(line, inset, y);
      y += metrics.getHeight();
    }

    int topLine = 0, baseLine = 0;
    // this only makes sense with one line
    if (needMarkLines && lines.length == 1) {
      if (this.topLine == 0 && this.baseLine == 0)
        findMarkLines();
      topLine = this.topLine+inset;
      baseLine = this.baseLine+inset;
      /*topLine = inset;
      baseLine = metrics.getAscent()+inset;*/
    }

    return new ImageWithMarkLines(new BWImage(image), topLine, baseLine);
  }

  private void findMarkLines() {
    BWImage image = makeImage("A");
    Rectangle boundingBox = OCRImageUtil.getBoundingBox(image);
    BWImage clipped = image.clip(boundingBox);
    topLine = new TopLineFinder().findTopLine(clipped).y+boundingBox.y-defaultInset;
    baseLine = new BaseLineFinder().findBaseLine(clipped).y+boundingBox.y-defaultInset;
  }

  private Dimension sizeNeeded(String[] lines) {
    int w = 0;
    for (String line : lines) {
      w = Math.max(w, metrics.stringWidth(line));
    }
    return new Dimension(w, metrics.getHeight()*lines.length);
  }
}
