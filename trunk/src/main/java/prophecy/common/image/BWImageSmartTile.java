package prophecy.common.image;

public class BWImageSmartTile implements BWImageStorage {
  private int width, height;
  private byte color;
  private byte[] pixels;

  public BWImageSmartTile(int width, int height, byte[] pixels) {
    this.width = width;
    this.height = height;
    if (allEqual(pixels)) {
      //System.out.println("Creating collapsed tile");
      color = pixels[0];
    } else
      this.pixels = pixels;
  }

  private boolean allEqual(byte[] tilePixels) {
    for (int i = 1; i < tilePixels.length; i++)
      if (tilePixels[i] != tilePixels[0])
        return false;
    return true;
  }

  public void setByte(int x, int y, byte b) {
    if (pixels == null) expand();
    pixels[y*width+x] = b;
  }

  private void expand() {
    //System.out.println("Expanding tile");
    pixels = new byte[width*height];
    for (int i = 0; i < pixels.length; i++) pixels[i] = color;
  }

  public byte getByte(int x, int y) {
    if (pixels != null)
      return pixels[y*width+x];
    else
      return color;
  }
}
