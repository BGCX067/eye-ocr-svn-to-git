package eyedev._16;

import eyedev._09.Subrecognition;

import java.util.ArrayList;
import java.util.List;

/** collects lines (strings) and concatenates them to a multi-line string
 * */
public class TextCollector {
  StringBuffer buf = new StringBuffer();
  List<TextLocation> locations = new ArrayList<TextLocation>();
  int lineStart = 0;

  public String getText() {
    return buf.toString();
  }

  public void addText(String text, Subrecognition subrecognition) {
    //System.out.println("addText " + text);
    if (subrecognition != null)
      locations.add(new TextLocation(buf.length(), buf.length()+text.length(), subrecognition));
    buf.append(text);
  }

  public void addLineBreak() {
    if (buf.length() != 0) {
      buf.append("\n");
      lineStart = buf.length();
    }
  }

  public List<TextLocation> getLocations() {
    return locations;
  }

  public String getLine() {
    return buf.substring(lineStart, buf.length());
  }
}
