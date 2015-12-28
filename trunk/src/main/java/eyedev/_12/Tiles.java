package eyedev._12;

import prophecy.common.image.BWImage;

import java.awt.*;

public class Tiles {
  BWImage image;
  int tileSize, cols, rows;

  public Tiles(BWImage image, int tileSize) {
    this.image = image;
    this.tileSize = tileSize;
    cols = image.getWidth()/tileSize;
    rows = image.getHeight()/tileSize;
  }

  public BWImage getImage() {
    return image;
  }

  public int getTileSize() {
    return tileSize;
  }

  public int getCols() {
    return cols;
  }

  public int getRows() {
    return rows;
  }

  public Tile getTile(int x, int y) {
    return new Tile(this, x, y);
  }

  /** this should be mt-safe */
  public BWImage getImage(Tile tile) {
    Rectangle rect = tile.getRect();
    try {
      return image.clip(rect);
    } catch (RuntimeException e) {
      System.out.println(rect);
      throw e;
    }
  }
}
