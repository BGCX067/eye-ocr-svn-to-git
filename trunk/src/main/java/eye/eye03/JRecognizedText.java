package eye.eye03;

import eyedev._01.DebugItem;
import eyedev._09.Subrecognition;
import eyedev._16.TextLocation;
import eyedev._16.TextLocations;

import javax.swing.*;
import java.util.List;

public class JRecognizedText extends JTextArea {
  private TextLocations textLocations;

  public JRecognizedText() {
    // make cursor visible & movable, but disallow changing the text
    setEditable(false);
    getCaret().setVisible(true);
  }

  public void setText(String text, List<DebugItem> debugInfo) {
    if (text == null) text = "";
    setText(text);
    setCaretPosition(0);
    textLocations = findTextLocations(debugInfo);
  }

  private TextLocations findTextLocations(List<DebugItem> debugInfo) {
    for (DebugItem item : debugInfo) {
      if (item.data instanceof TextLocations)
        return (TextLocations) item.data;
    }
    return null;
  }

  public TextLocation findLocationAtCaret() {
    if (textLocations == null) return null;
    int caret = getSelectionStart();
    for (TextLocation location : textLocations.locations) {
      if (caret >= location.charIndex1 && caret < location.charIndex2)
        return location;
    }
    return null;
  }

  public void jumpToLocation(Subrecognition s) {
    TextLocation location = findLocation(s);
    if (location != null) {
      select(location.charIndex1, location.charIndex2);
      getCaret().setSelectionVisible(true);
    }
  }

  private TextLocation findLocation(Subrecognition s) {
    if (s == null || textLocations == null) return null;
    for (TextLocation location : textLocations.locations) {
      if (location.subrecognition == s)
        return location;
    }
    return null;
  }
}
