package prophecy.common.image;

public interface BWImageStorage {
  byte getByte(int x, int y);
  void setByte(int x, int y, byte b);
}
