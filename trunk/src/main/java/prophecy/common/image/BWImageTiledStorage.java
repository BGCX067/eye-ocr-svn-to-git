package prophecy.common.image;

public class BWImageTiledStorage implements BWImageStorage {
  public static final int ts = 32;
  private int width, height, tw, th;
  private BWImageStorage[] tiles;

  public BWImageTiledStorage(int width, int height, byte[] pixels) {
    this.width = width;
    this.height = height;
    tw = (width+ts-1)/ts;
    th = (height+ts-1)/ts;
    tiles = new BWImageStorage[tw*th];
    for (int y = 0; y < th; y++)
      for (int x = 0; x < tw; x++) {
        byte[] tilePixels = new byte[ts*ts];
        for (int ty = 0; ty < ts; ty++)
          for (int tx = 0; tx < ts; tx++)
            if (x*ts+tx < width && y*ts+ty < height)
              tilePixels[ty*ts+tx] = pixels[(y*ts+ty)*width+(x*ts+tx)];
            else
              tilePixels[ty*ts+tx] = tilePixels[0];
        tiles[y*tw+x] = new BWImageSmartTile(ts, ts, tilePixels);
      }
  }

  public void setByte(int x, int y, byte b) {
    int tx = x/ts, ty = y/ts;
    tiles[ty*tw+tx].setByte(x % ts, y % ts, b);
  }

  public byte getByte(int x, int y) {
    int tx = x/ts, ty = y/ts;
    return tiles[ty*tw+tx].getByte(x % ts, y % ts);
  }
}
