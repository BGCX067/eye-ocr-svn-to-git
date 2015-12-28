package eyedev._17;

import eyedev._09.Translatable;

public class TopLine implements Translatable {
  public int x, y, width;

  public TopLine(int x, int y, int width) {
    this.x = x;
    this.y = y;
    this.width = width;
  }

  public Translatable translate(int x, int y) {
    return new TopLine(this.x+x, this.y+y, width);
  }
}
