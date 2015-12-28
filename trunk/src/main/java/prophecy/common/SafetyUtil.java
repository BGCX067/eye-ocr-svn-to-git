package prophecy.common;

public class SafetyUtil {
  public static String cleanFileName(String name) {
    name = name.replaceAll("[^a-zA-Z0-9\\-.]", "_");
    if (name.length() > 64) name = name.substring(0, 64);
    return name;
  }
}
