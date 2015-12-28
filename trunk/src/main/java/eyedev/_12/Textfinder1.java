package eyedev._12;

import drjava.util.Function;
import drjava.util.MultiCoreUtil;
import eyedev._01.OCRImageUtil;
import eyedev._09.Segment;
import eyedev._09.SegmentLevel;
import eyedev._09.Segmenter;
import prophecy.common.image.BWImage;
import prophecy.common.image.ImageProcessing;
import prophecy.common.image.RGB;
import prophecy.common.image.RGBImage;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

public class Textfinder1 extends Segmenter {
  private BWImage baseImage;
  int rows, cols;
  MarkedTile[][] markedTiles;
  protected Dimension minimumClusterSize = null;
  protected int tileSize = 4;
  protected int maxBlueGapToFill = 12;

  /** Warning: baseImage is changed in the process */
  public Textfinder1(RGBImage baseImage, boolean removeLines) {
    if (removeLines)
      LineRemover.removeLines(baseImage);
    process(baseImage.toBW());
  }

  public void process(BWImage baseImage) {
    this.baseImage = baseImage;
    //long startTime = System.currentTimeMillis();
    makeTiles();
    //long endTime = System.currentTimeMillis();
    //System.out.println((endTime - startTime) + " ms for makeTiles");
    fillBlueGaps();
  }

  public Textfinder1() {
  }

  private void makeTiles() {
    Tiles tiles = new Tiles(baseImage, tileSize);
    rows = tiles.getRows();
    cols = tiles.getCols();
    makeTiles_mt2(tiles);
  }

  /** single threaded */
  private void makeTiles_st(Tiles tiles) {
    markedTiles = new MarkedTile[rows][cols];
    for (int row = 0; row < rows; row++)
      for (int col = 0; col < cols; col++) {
        Tile tile = tiles.getTile(col, row);
        BWImage img = tiles.getImage(tile);
        TileType type = getTileType_mt(img);

        MarkedTile markedTile = new MarkedTile(tile, type);
        markedTiles[row][col] = markedTile;
      }
  }

  /** multithreaded, but curiously slower than st. Too much overhead? */
  private void makeTiles_mt(final Tiles tiles) {
    markedTiles = new MarkedTile[rows][cols];
    System.out.println("num tiles: " + (rows*cols));
    List<Tile> points = new ArrayList<Tile>();
    for (int row = 0; row < rows; row++)
      for (int col = 0; col < cols; col++)
        points.add(tiles.getTile(col, row));

    Function<Tile, MarkedTile> f = new Function<Tile, MarkedTile>() {
      public MarkedTile get(Tile tile) {
        BWImage img = tiles.getImage(tile);
        TileType type = getTileType_mt(img);
        return new MarkedTile(tile, type);
      }
    };

    List<MarkedTile> markedTiles = MultiCoreUtil.parallelMap(points, f);

    int i = 0;
    for (int row = 0; row < rows; row++)
      for (int col = 0; col < cols; col++)
        this.markedTiles[row][col] = markedTiles.get(i++);
  }

  private void makeTiles_mt2(final Tiles tiles) {
    //System.out.println("rows: " + rows);
    List<Integer> rowList = new ArrayList<Integer>();
    for (int row = 0; row < rows; row++)
      rowList.add(row);

    Function<Integer, MarkedTile[]> f = new Function<Integer, MarkedTile[]>() {
      public MarkedTile[] get(Integer _row) {
        int row = _row;
        MarkedTile[] markedTiles = new MarkedTile[cols];
        for (int col = 0; col < cols; col++) {
          Tile tile = tiles.getTile(col, row);
          BWImage img = tiles.getImage(tile);
          TileType type = getTileType_mt(img);
          MarkedTile markedTile = new MarkedTile(tile, type);
          markedTiles[col] = markedTile;
        }
        return markedTiles;
      }
    };

    List<MarkedTile[]> markedTiles = MultiCoreUtil.parallelMap(rowList, f);

    this.markedTiles = new MarkedTile[rows][];
    for (int row = 0; row < rows; row++)
      this.markedTiles[row] = markedTiles.get(row);
  }

  /** override this if you feel like it. Warning: This is called in multiple threads */
  public TileType getTileType_mt(BWImage img) {
    TileType type = TileType.white;

    double avg = img.averageBrightness();
    //System.out.println(avg);

    if (avg >= 0.99)
      type = TileType.yellow;
    else {
      float min = OCRImageUtil.minBrightness(img);
      float max = OCRImageUtil.maxBrightness(img);
      if (min <= 0.1 && max >= 0.9)
        type = TileType.blue;
    }
    return type;
  }

  public RGBImage getMarkedImage() {
    RGBImage image = new RGBImage(baseImage);
    markTiles(image);
    markClusters(image, getClusters());
    return image;
  }

  private void markTiles(RGBImage image) {
    for (int row = 0; row < rows; row++)
      for (int col = 0; col < cols; col++) {
        MarkedTile markedTile = markedTiles[row][col];
        Tile tile = markedTile.tile;
        Rectangle r = tile.getRect();
        Color color;
        switch (markedTile.type) {
          case white:
            color = null;
            break;
          case yellow:
            color = Color.yellow;
            break;
          case blue:
            color = Color.blue;
            break;
          case lightblue:
            color = Color.blue; // just render this as blue
            break;
          default:
            color = Color.black;
        }

        if (color != null)
          if (r.width <= 3 || r.height <= 3)
            image.setPixel(r.x, r.y, new RGB(color));
          else
            ImageProcessing.drawRect(image, r.x, r.y, r.width - 1, r.height - 1, new RGB(color));
      }
  }

  private void fillBlueGaps() {
    for (int row = 0; row < rows; row++)
      loopCol:for (int col = 0; col < cols - 2;) {
        MarkedTile markedTile = markedTiles[row][col];
        if (markedTile.type == TileType.blue) {
          ++col;
          for (int col2 = col; col2 < cols - 1; col2++) {
            MarkedTile markedTile2 = markedTiles[row][col2];
            if (markedTile2.type == TileType.blue) {
              int numYellow = col2 - col;
              if (numYellow > 0 && numYellow <= maxBlueGapToFill/tileSize) {
                //System.out.println("Filling blue gap at " + row + "/" + col + ": " + numYellow);
                for (int col3 = col; col3 < col2; col3++)
                  markedTiles[row][col3].type = TileType.lightblue;
              }
            } else // if (markedTile2.type == TileType.yellow)
              continue;

            col = col2;
            continue loopCol;
          }
        }

        ++col;
      }
  }

  public List<TileCluster> getClusters() {
    List<TileCluster> clusters = new TileClusterer(rows, cols, markedTiles).getClusters();
    if (minimumClusterSize != null)
      for (ListIterator<TileCluster> it = clusters.listIterator(); it.hasNext(); ) {
        TileCluster cluster = it.next();
        Dimension size = cluster.getBoundingRect().getSize();
        if (size.width < minimumClusterSize.width || size.height < minimumClusterSize.height)
          it.remove();
      }
    return filterClusters(clusters);
  }

  /** return filtered list (only clusters that look like they're actually text).
   *  By default, returns all clusters */
  public List<TileCluster> filterClusters(List<TileCluster> clusters) {
    return clusters;
  }

  public BWImage getClusterImage(TileCluster cluster) {
    return baseImage.clip(cluster.getBoundingRect());
  }

  public void markClusters(RGBImage markedImage, List<TileCluster> clusters) {
    for (TileCluster cluster : clusters) {
      Rectangle r = cluster.getBoundingRect();
      ImageProcessing.drawRect(markedImage, r.x-1, r.y-1, r.width+1, r.height+1, new RGB(Color.green));
      ImageProcessing.drawRect(markedImage, r.x-2, r.y-2, r.width+3, r.height+3, new RGB(Color.black));
    }
  }

  public void markClustersWithGrayBackground(RGBImage markedImage, List<TileCluster> clusters) {
    for (TileCluster cluster : clusters) {
      Rectangle r = cluster.getBoundingRect();
      OCRImageUtil.fillBackground(markedImage, r.x-1, r.y-1, r.width+2, r.height+2, new RGB(0.8));
    }
  }

  public void setMinimumClusterSize(Dimension dimension) {
    minimumClusterSize = dimension;
  }

  /** sets tile width and height */
  public void setTileSize(int tileSize) {
    this.tileSize = tileSize;
  }

  /** in pixels */
  public void setMaxBlueGapToFill(int maxBlueGapToFill) {
    this.maxBlueGapToFill = maxBlueGapToFill;
  }

  public List<Segment> segment(BWImage baseImage) {
    process(baseImage);
    List<Segment> segments = new ArrayList<Segment>();
    for (TileCluster cluster : getClusters()) {
      segments.add(new Segment(SegmentLevel.line, cluster.getBoundingRect(),
        baseImage.clip(cluster.getBoundingRect())));
    }
    return segments;
  }
}
