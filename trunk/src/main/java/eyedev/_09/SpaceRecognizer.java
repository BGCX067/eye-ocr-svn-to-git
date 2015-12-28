package eyedev._09;

import eyedev._17.MarkLine;

import java.awt.*;

public class SpaceRecognizer {
  private float spaceThreshold;

  public SpaceRecognizer(float spaceThreshold) {
    this.spaceThreshold = spaceThreshold;
  }

  public boolean isSpace(Rectangle leftChar, Rectangle rightChar, MarkLine baseLine, MarkLine topLine) {
    int h;
    if (baseLine != null && topLine != null)
      h = baseLine.y-topLine.y;
    else
      return false;
      //h = Math.max(leftChar.height, rightChar.height);
    int distance = rightChar.x-(leftChar.x+leftChar.width);
    float threshold = h*spaceThreshold;
    //System.out.println("h=" + h + ", threshold: " + threshold + ", distance: " + distance + ", left: " + leftChar + ", right: " + rightChar);
    return distance >= threshold;
  }

  public float getSpaceThreshold() {
    return spaceThreshold;
  }

  public void setSpaceThreshold(float spaceThreshold) {
    this.spaceThreshold = spaceThreshold;
  }
}
