package eyedev._06;

import prophecy.common.image.BWImage;

public class SimpleSegmenter extends AbstractLineSegmenter {
  public SimpleSegmenter(BWImage image) {
    super(image);
  }

  @Override
  public void go() {
    boolean lastWhite = true;
    int letterStart = 0;
    rectangles.clear();

    for (int x = 0; x < image.getWidth(); x++) {
      boolean white = isWhiteColumn(x);
      if (!white && lastWhite) {
        //System.out.println("Letter start " + x);
        letterStart = x;
      } else if (white && !lastWhite) {
        //System.out.println("Letter end " + (x-1));
        addLetter(letterStart, x);
      }
      lastWhite = white;
    }
  }
}
