package eyedev._13;

import eye.eye01.TextPainter2;
import eye.eye02.FontFinder;
import eyedev._01.ImageWithMarkLines;
import eyedev._01.OCRImageUtil;
import prophecy.common.gui.AutoVMExit;
import prophecy.common.image.BWImage;
import prophecy.common.image.ImageProcessing;

import java.awt.*;
import java.io.IOException;

public class CharacterCanvas {
  int topLine, baseLine, width;
  private BWImage canvasImage;

  public CharacterCanvas(int topLine, int baseLine, int width, int height) {
    this.topLine = topLine;
    this.baseLine = baseLine;
    this.width = width;
    canvasImage = new BWImage(width, height, 1f);
  }

  public BWImage getCanvasImage() {
    return canvasImage;
  }

  public Rectangle drawCharacter(ImageWithMarkLines image, int x) {
    int imageFontHeight = image.baseLine-image.topLine;
    int canvasFontHeight = baseLine-topLine;
    int imageDistanceFromTop = -image.topLine;
    float scalingFactor = (float) canvasFontHeight/imageFontHeight;
    System.out.println("scaling factor: " + scalingFactor + ", distanceFromTop=" + imageDistanceFromTop);
    int scaledDistanceFromTop = (int) (imageDistanceFromTop*scalingFactor);
    int y = topLine+scaledDistanceFromTop;
    BWImage resized = ImageProcessing.resize(image.image,
      (int) (image.image.getWidth()*scalingFactor), (int) (image.image.getHeight()*scalingFactor));
    ImageProcessing.copy(resized, 0, 0, canvasImage, x, y, resized.getWidth(), resized.getHeight());
    return new Rectangle(x, y, resized.getWidth(), resized.getHeight());
  }

  public static void main(String[] args) throws Exception {
    String text = "abcdÄ";
    Font font = FontFinder.getEyeFont("Arial").loadFont(30);
    TextPainter2 textPainter = new TextPainter2(font);
    CharacterCanvas canvas = new CharacterCanvas(10, 50, 150, 70);

    int x = 0;
    for (char c : text.toCharArray()) {
      ImageWithMarkLines imageWithMarkLines = textPainter.makeImageWithMarkLines("" + c, 5, true).trim();
      Rectangle r = canvas.drawCharacter(imageWithMarkLines, x);
      x = r.x+r.width;
    }

    BWImage image = canvas.getCanvasImage();
    ImageProcessing.fillRect(image, 0, canvas.topLine, image.getWidth(), 1, 0.5f);
    ImageProcessing.fillRect(image, 0, canvas.baseLine, image.getWidth(), 1, 0.5f);
    OCRImageUtil.show(image.toRGB());
    AutoVMExit.install();
  }
}
