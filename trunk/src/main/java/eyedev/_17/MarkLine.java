package eyedev._17;

import eyedev._09.Translatable;

public class MarkLine implements Translatable {
  public enum Type { base, top }
  public Type type;
  public int x, y, width;

  public MarkLine(Type type, int x, int y, int width) {
    this.type = type;
    this.x = x;
    this.y = y;
    this.width = width;
  }

  public Translatable translate(int x, int y) {
    return new MarkLine(type, this.x+x, this.y+y, width);
  }
}
