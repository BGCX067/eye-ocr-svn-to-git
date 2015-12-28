package eyedev._08;

import java.awt.*;

public class LearnAFont {
  public static void main(String[] args) {
    Font[] fonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFonts();
    if (fonts.length == 0) {
      System.out.println("There are no system fonts (huh?)");
      return;
    }
    Font font = fonts[0]; // just choose the first one arbitrarily
    int size = 40;

    font = font.deriveFont((float) size);
    System.out.println("Learning font: " + font);
    FontLearner fontLearner = new SegSigLearner();
    fontLearner.addFont(font);
    fontLearner.go();

    //SurfaceUtil.showAsMain(font.getName(), new ImageSurface(new TextPainter2(font).makeImage("ALPHABET")));
  }

}
