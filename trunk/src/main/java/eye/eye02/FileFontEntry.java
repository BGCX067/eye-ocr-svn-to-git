package eye.eye02;

import java.awt.*;
import java.io.File;

public class FileFontEntry extends FontEntry {
  File fontFile;
  String name;

  FileFontEntry(File fontFile) {
    this.fontFile = fontFile;
    name = fontFile.getName();
    name = name.substring(0, name.lastIndexOf('.'));
  }

  @Override
  public String toString() {
    return name + "  (Eye font)";
  }

  public Font loadFont() throws Exception {
    return Font.createFont(Font.TRUETYPE_FONT, fontFile);
  }

  public String getName() {
    return name;
  }
}
