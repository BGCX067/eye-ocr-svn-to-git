package eyedev._06;

import prophecy.common.image.BWImage;

import java.util.HashSet;
import java.util.Set;

public class RecogUtil {
  static float getAverageWidth(BWImage image, int y1, int y2) {
    float sum = 0;
    for (int y = y1; y < y2; y++) {
      sum += getWidth(image, y);
    }
    return sum/(y2-y1);
  }

  static int getWidth(BWImage image, int y) {
    int x2 = getRightmostPoint(image, y);
    int x1 = getLeftmostPoint(image, y);
    return Math.max(0, x2-x1);
  }

  public static int getLeftmostPoint(BWImage image, int y) {
    int x1 = 0;
    while (x1 < image.getWidth() && image.getPixel(x1, y) == 1f)
      ++x1;
    return x1;
  }

  public static int getRightmostPoint(BWImage image, int y) {
    int x2 = image.getWidth();
    while (x2 > 0 && image.getPixel(x2-1, y) == 1f)
      --x2;
    return x2;
  }

  public static float upperWidthToLowerWidth(BWImage image) {
    float upper = RecogUtil.getAverageWidth(image, 0, (int) (image.getHeight()/3f));
    float lower = RecogUtil.getAverageWidth(image, (int) (image.getHeight()*2/3f), image.getHeight());
    float ratio = safeRatio(upper, lower);
    //System.out.println("upper: " + upper + ", lower: " + lower + ", ratio: " + ratio);
    return ratio;
  }

  private static float safeRatio(float x, float y) {
    float r = x/y;
    //System.out.println("ratio=" + r + " (x=" + x + ", y=" + y + ")");
    if (Double.isNaN(r))
      return 0f;
    return r;
  }

  public static float leftishness(BWImage image) {
    float middle = image.getWidth()/2f;
    float sum = 0;
    for (int y = 0; y < image.getHeight(); y++) {
      int x1 = getLeftmostPoint(image, y);
      int x2 = getRightmostPoint(image, y);
      if (x2 > x1) {
        float center = (x1+x2)/2f;
        sum += (middle-center)/middle;
      }
    }
    float result = sum/image.getHeight();
    //System.out.println("leftishness: " + result);
    return result;
  }

  public static int uniqueWidths(BWImage image) {
    Set<Integer> widths = new HashSet<Integer>();
    for (int y = 0; y < image.getHeight(); y++) {
      int w = getWidth(image, y);
      if (w > 0)
        widths.add(w);
    }
    return widths.size();
  }

  public static int uniqueLines(BWImage image) {
    Set<String> set = new HashSet<String>();
    for (int y = 0; y < image.getHeight(); y++) {
      String line = lineAsString(image, y);
      if (line.contains("X"))
        set.add(line);
    }
    return set.size();
  }

  private static String lineAsString(BWImage image, int y) {
    char[] chars = new char[image.getWidth()];
    for (int x = 0; x < image.getWidth(); x++)
      chars[x] = image.getPixel(x, y) == 1f ? ' ' : 'X';
    return new String(chars);
  }
}
