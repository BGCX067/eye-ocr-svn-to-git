package eye.eye02;

import java.awt.*;

public abstract class FontEntry {
  public abstract String getName();
  public abstract Font loadFont() throws Exception;
  
  public Font loadFont(float size) throws Exception {
    return loadFont().deriveFont(size);
  }
}
