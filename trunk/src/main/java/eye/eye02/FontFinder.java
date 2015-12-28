package eye.eye02;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class FontFinder {
  public static List<FontEntry> allEyeFonts() {
    List<FontEntry> fontEntries = new ArrayList<FontEntry>();

    // Eye fonts
    addEntries(fontEntries, "fonts");

    // Extra fonts
    addEntries(fontEntries, "extrafonts");

    // Sort Eye fonts alphabetically
    Collections.sort(fontEntries, new Comparator<FontEntry>() {
      public int compare(FontEntry fontEntry1, FontEntry fontEntry2) {
        return fontEntry1.getName().compareToIgnoreCase(fontEntry2.getName());
      }
    });
    return fontEntries;
  }

  private static void addEntries(List<FontEntry> fontEntries, String dir) {
    File dirFile = new File(dir);
    if (!dirFile.isDirectory()) return;
    File[] files = new File(dir).listFiles();
    for (File file : files) {
      if (file.getName().toLowerCase().endsWith(".ttf"))
        fontEntries.add(new FileFontEntry(file));
    }
  }

  public static List<FontEntry> allSystemFonts() {
    List<FontEntry> fontEntries = new ArrayList<FontEntry>();

    Font[] fonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFonts();
    for (Font font : fonts)
      fontEntries.add(new SystemFontEntry(font));

    return fontEntries;
  }

  public static FontEntry getEyeFont(String name) throws IOException {
    FontEntry fontEntry = findEyeFont("fonts", name);
    if (fontEntry == null)
      fontEntry = findEyeFont("extrafonts", name);
    if (fontEntry == null)
      throw new IOException("Font not found: " + name + ".ttf");
    return fontEntry;
  }

  private static FontEntry findEyeFont(String dir, String name) {
    File dirFile = new File(dir);
    if (!dirFile.isDirectory()) return null;
    File[] files = dirFile.listFiles();
    for (File file : files)
      if (file.getName().equalsIgnoreCase(name + ".ttf"))
        return new FileFontEntry(file);
    return null;
  }
}
