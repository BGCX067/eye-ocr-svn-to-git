package eye.eye02;

import java.awt.*;

public class SystemFontEntry extends FontEntry {
  Font font;

  SystemFontEntry(Font font) {
    this.font = font;
  }

  public String toString() {
    if (font == null) return "";
    if (font.getStyle() == 0)
      return font.getName();
    return font.getName() + " (style " + font.getStyle() + ")";
  }

  public Font loadFont() {
    return font;
  }

  public String getName() {
    return font.getName();
  }
}
