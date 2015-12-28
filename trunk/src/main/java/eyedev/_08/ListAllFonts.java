package eyedev._08;

import java.awt.*;

public class ListAllFonts {
  public static void main(String[] args) {
    String[] fontNames = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
    System.out.println(fontNames.length + " font family names");
    for (String fontName : fontNames) {
      System.out.println(fontName);
    }

    Font[] fonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFonts();
    System.out.println();
    System.out.println(fonts.length + " fonts");

    for (Font font : fonts) {
      System.out.println(font);
    }

  }
}
