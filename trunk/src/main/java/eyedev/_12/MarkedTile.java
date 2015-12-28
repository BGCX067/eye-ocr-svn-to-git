package eyedev._12;

class MarkedTile {
  Tile tile;
  TileType type;

  MarkedTile(Tile tile, TileType type) {
    this.tile = tile;
    this.type = type;
  }

  public MarkedTile(MarkedTile markedTile) {
    tile = markedTile.tile;
    type = markedTile.type;
  }

  public boolean isText() {
    return type == TileType.blue || type == TileType.lightblue;
  }
}
