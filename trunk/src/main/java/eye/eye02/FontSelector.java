package eye.eye02;

import drjava.util.Lizt;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class FontSelector extends JComboBox {
  private FontEntry[] fontEntries;

  public FontSelector() {
    List<FontEntry> fontEntries = new ArrayList<FontEntry>();

    fontEntries.addAll(FontFinder.allEyeFonts());
    fontEntries.addAll(FontFinder.allSystemFonts());

    this.fontEntries = fontEntries.toArray(new FontEntry[fontEntries.size()]);
    setModel(new DefaultComboBoxModel(this.fontEntries));
  }

  public Font getSelectedFont() throws Exception {
    Object item = getSelectedItem();
    if (item instanceof FontEntry)
      return ((FontEntry) item).loadFont();
    else
      return null;
  }

  public String getSelectedFontName() {
    Object item = getSelectedItem();
    if (item instanceof FontEntry)
      return ((FontEntry) item).getName();
    else
      return null;
  }

  public void selectFont(String name) {
    if (name == null) return;
    for (int i = 0; i < fontEntries.length; i++) {
      FontEntry fontEntry = fontEntries[i];
      if (name.equals(fontEntry.getName())) {
        setSelectedIndex(i);
        return;
      }
    }
  }

  public FontEntry findEntry(String name) {
    if (name == null) return null;
    for (int i = 0; i < fontEntries.length; i++) {
      FontEntry fontEntry = fontEntries[i];
      if (name.equals(fontEntry.getName()))
        return fontEntry;
    }
    return null;
  }

  public void setMultiSelect(boolean multiSelect) {
    setEditable(multiSelect);
  }

  public List<Font> getSelectedFonts() throws Exception {
    Object item = getSelectedItem();
    if (item instanceof FontEntry)
      return Lizt.of(((FontEntry) item).loadFont());
    else if (item instanceof String) {
      String[] s = ((String) item).split(",");
      List<Font> list = new ArrayList<Font>();
      for (String name : s) {
        name = name.trim();
        FontEntry fontEntry = findEntry(name);
        if (fontEntry != null)
          list.add(fontEntry.loadFont());
      }
      return list;
    }
    return Lizt.of();
  }
}
