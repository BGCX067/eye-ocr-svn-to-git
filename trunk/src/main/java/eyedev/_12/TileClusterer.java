package eyedev._12;

import java.util.ArrayList;
import java.util.List;

public class TileClusterer {
  private MarkedTile[][] tiles;
  private List<TileCluster> clusters = new ArrayList<TileCluster>();
  private int rows;
  private int cols;

  public TileClusterer(int rows, int cols, MarkedTile[][] tiles) {
    this.rows = rows;
    this.cols = cols;
    this.tiles = new MarkedTile[rows][cols];
    for (int row = 0; row < rows; row++)
      for (int col = 0; col < cols; col++)
        this.tiles[row][col] = new MarkedTile(tiles[row][col]);
    findShapes();
  }

  void findShapes() {
    for (int y = 0; y < rows; y++)
      for (int x = 0; x < cols; x++)
        if (tiles[y][x].isText())
          createShape(x, y);
  }

  private void createShape(int x, int y) {
    TileCluster shape = new TileCluster();
    trace(shape, x, y);
    clusters.add(shape);
  }

  private void trace(TileCluster shape, int x, int y) {
    if (!tiles[y][x].isText())
      return;
    shape.addTile(tiles[y][x].tile);
    tiles[y][x].type = TileType.white;
    if (x > 0)
      trace(shape, x-1, y);
    if (x < cols-1)
      trace(shape, x+1, y);
    if (y > 0)
      trace(shape, x, y-1);
    if (y < rows-1)
      trace(shape, x, y+1);
  }

  public List<TileCluster> getClusters() {
    return clusters;
  }
}
