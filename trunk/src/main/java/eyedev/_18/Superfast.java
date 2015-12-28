package eyedev._18;

import eyedev._01.ExtendedImageReader;
import eyedev._01.InputImage;
import eyedev._01.RecognizedText;
import prophecy.common.image.BWImage;

public class Superfast extends ExtendedImageReader {
  private BWImage image;
  private int w, h;
  private float threshold = 0.5f;

  public RecognizedText extendedReadImage(InputImage image) {
    this.image = image.image;
    w = image.image.getWidth();
    h = image.image.getHeight();
    /*System.out.println("topLine: " + image.topLine + ", baseLine: " + image.baseLine
      + ", height: " + image.image.getHeight());*/

    String text = recognize();
    return text == null ? null : new RecognizedText(text);
  }

  private String recognize() {
    String southDiv3 = southDiv(3);
    String northDiv3 = northDiv(3);
    String eastDiv3 = eastDiv(3);
    System.out.println("southDiv3=" + southDiv3 + ", northDiv3=" + northDiv3 + ", eastDiv3=" + eastDiv3);

    if (eastDiv3.equals("12"))
      return "A";

    if (southDiv3.equals("212")) {
      if (eastDiv3.equals("2"))
        return "H";
      else
        return "X";
    }

    if (southDiv3.equals("2")) {
      if (eastDiv3.equals("02"))
        return "L";
      else
        return "E";
    }

    if (southDiv3.equals("121"))
      return "O";

    if (southDiv3.equals("020"))
      return "T";


    return null;
  }

  private String northDiv(int n) {
    StringBuffer buf = new StringBuffer();
    for (int x = 0; x < w; x++) {
      int y = 0;
      while (y < h && image.getPixel(x, y) >= threshold) ++y;
      char digit = (char) ('0' + ((h-y)*n/(h+1)));
      if (buf.length() == 0 || buf.charAt(buf.length()-1) != digit)
        buf.append(digit);
    }
    return buf.toString();
  }

  private String southDiv(int n) {
    StringBuffer buf = new StringBuffer();
    for (int x = 0; x < w; x++) {
      int y = h;
      while (y > 0 && image.getPixel(x, y-1) >= threshold) --y;
      char digit = (char) ('0' + (y*n/(h+1)));
      if (buf.length() == 0 || buf.charAt(buf.length()-1) != digit)
        buf.append(digit);
    }
    return buf.toString();
  }

  private String eastDiv(int n) {
    StringBuffer buf = new StringBuffer();
    for (int y = 0; y < h; y++) {
      int x = w;
      while (x > 0 && image.getPixel(x-1, y) >= threshold) --x;
      char digit = (char) ('0' + (x*n/(w+1)));
      if (buf.length() == 0 || buf.charAt(buf.length()-1) != digit)
        buf.append(digit);
    }
    return buf.toString();
  }
}
