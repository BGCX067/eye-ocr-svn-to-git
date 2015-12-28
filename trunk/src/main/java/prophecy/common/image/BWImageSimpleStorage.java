package prophecy.common.image;

public class BWImageSimpleStorage implements BWImageStorage {
  private int width, height;
  private byte[] pixels;

  public BWImageSimpleStorage(int width, int height, byte[] pixels) {
    this.width = width;
    this.height = height;
    this.pixels = pixels;
  }

  public void setByte(int x, int y, byte b) {
    pixels[y*width+x] = b;
  }

  public byte getByte(int x, int y) {
    return pixels[y*width+x];
  }
}
