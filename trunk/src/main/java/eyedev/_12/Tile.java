package eyedev._12;

import prophecy.common.image.BWImage;
import prophecy.common.image.RGBImage;

import java.awt.*;

public class Tile {
  private int col;
  private int row;
  private int tileSize;

  public Tile(Tiles tiles, int col, int row) {
    this.col = col;
    this.row = row;
    tileSize = tiles.tileSize;
  }

  public Rectangle getRect() {
    return new Rectangle(col*tileSize, row*tileSize, tileSize, tileSize);
  }
}
