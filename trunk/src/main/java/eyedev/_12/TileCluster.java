package eyedev._12;

import java.awt.*;
import java.util.List;
import java.util.ArrayList;

public class TileCluster {
  private java.util.List<Tile> tiles = new ArrayList<Tile>();

  public void addTile(Tile tile) {
    tiles.add(tile);
  }

  public List<Tile> getTiles() {
    return tiles;
  }

  public Rectangle getBoundingRect() {
    Rectangle r = null;
    for (Tile tile : tiles) {
      if (r == null)
        r = tile.getRect();
      else
        r = r.union(tile.getRect());
    }
    return r;
  }
}
