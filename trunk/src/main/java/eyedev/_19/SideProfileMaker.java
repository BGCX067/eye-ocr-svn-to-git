package eyedev._19;

import eyedev._01.OCRImageUtil;
import prophecy.common.image.BWImage;

public class SideProfileMaker {
  private int minLen = 3;
  private float threshold = 0.5f;
  private byte[] profile;
  private String signature;

  public SideProfileMaker(BWImage image, int rotation) {
    for (int r = 0; r < rotation; r++)
      image = OCRImageUtil.rotateCounterClockwise(image);
    profile = rawProfile(image, 3);
    //System.out.println("raw: " + profileToString(profile));
    simplifyProfile(profile, minLen);
    //System.out.println("sim: " + profileToString(profile));
    signature = makeSignature(profile);
  }

  private String makeSignature(byte[] profile) {
    StringBuffer buf = new StringBuffer();
    for (byte x : profile) {
      char digit = (char) ('0' + x);
      if (buf.length() == 0 || buf.charAt(buf.length()-1) != digit)
        buf.append(digit);
    }
    return buf.toString();
  }

  public String getSignature() {
    return signature;
  }

  private String profileToString(byte[] profile) {
    char[] chars = new char[profile.length];
    for (int i = 0; i < profile.length; i++) chars[i] = (char) ('0' + profile[i]);
    return new String(chars);
  }

  private void simplifyProfile(byte[] profile, int minLen) {
    int i = 0;
    while (i < profile.length) {
      int j = i+1;
      while (j < profile.length && profile[j] == profile[i]) ++j;
      int len = j-i;
      if (len < minLen)
        fillGap(profile, i, len);
      i = j;
    }
  }

  private void fillGap(byte[] profile, int i, int len) {
    if (len == profile.length) return;
    byte valueLeft = i == 0 ? profile[i+len] : profile[i-1];
    byte valueRight = i+len == profile.length ? profile[i-1] : profile[i+len];
    for (int j = 0; j < len/2; j++)
      profile[i+j] = valueLeft;
    for (int j = len/2; j < len; j++)
      profile[i+j] = valueRight;
  }

  private byte[] rawProfile(BWImage image, int n) {
    int w = image.getWidth(), h = image.getHeight();
    byte[] profile = new byte[w];
    for (int x = 0; x < w; x++) {
      int y = 0;
      while (y < h && image.getPixel(x, y) >= threshold) ++y;
      profile[x] = (byte) ((h-y)*n/(h+1));
    }
    return profile;
  }

}
