package eyedev._05;

import eyedev._06.RecogUtil;
import prophecy.common.image.BWImage;

public class SegmentCounter {
  public static String getSegmentCount(BWImage image, boolean flip) {
    StringBuffer buf = new StringBuffer();
    int lastN = 0;
    for (int y = 0; y < (flip ? image.getWidth() : image.getHeight()); y++) {
      int n = countSegments(image, y, flip);
      if (n > 9) n = 9;
      if (n != lastN)
        buf.append(String.valueOf(n));
      lastN = n;
    }
    return buf.substring(0, lastN == 0 && buf.length() > 0 ? buf.length()-1 : buf.length());
  }

  private static int countSegments(BWImage image, int y, boolean flip) {
    float lastPix = 1f;
    int segments = 0;
    for (int x = 0; x < (flip ? image.getHeight() : image.getWidth()); x++) {
      float pix = flip ? image.getPixel(y, x) : image.getPixel(x, y);
      if (pix == 1f && lastPix == 0f)
        ++segments;
      lastPix = pix;
    }
    if (lastPix == 0f) ++segments;
    return segments;
  }

  public static String getXYSegmentSignature(BWImage image) {
    String s1 = SegmentCounter.getSegmentCount(image, false);
    String s2 = SegmentCounter.getSegmentCount(image, true);
    String s = s1 + "-" + s2;
    return s;
  }

  public static String getExtendedTTBSignature(BWImage image) {
    StringBuffer buf = new StringBuffer();
    String last = "0";
    for (int y = 0; y < image.getHeight(); y++) {
      int n = countSegments(image, y, false);
      if (n > 9) n = 9;
      String s = n == 0 ? "0" : n + position(image, y);
      if (!s.equals(last)) {
        if (buf.length() != 0) buf.append(".");
        buf.append(s);
      }
      last = s;
    }
    return buf.substring(0, last.equals("0") && buf.length() > 0 ? buf.length()-2 : buf.length());
  }

  private static String position(BWImage image, int y) {
    int l = RecogUtil.getLeftmostPoint(image, y);
    int r = RecogUtil.getRightmostPoint(image, y);
    if (r < l) return "";
    float middle = image.getWidth()/2f;
    float center = (l+r)/2f;
    float ratio = center/middle;
    float width = (r-l)/(middle*2);
    if (ratio <= 0.5f)
      return "L";
    else if (ratio >= 1.5f)
      return "R";
    else if (width <= 0.6f)
      return "C";
    return "";
  }
}
