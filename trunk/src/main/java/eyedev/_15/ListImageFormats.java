package eyedev._15;

import javax.imageio.ImageIO;

public class ListImageFormats {
  public static void main(String[] args) {
    String[] names = ImageIO.getReaderFormatNames();
    for (String name : names) {
      System.out.println(name);
    }
  }
}
