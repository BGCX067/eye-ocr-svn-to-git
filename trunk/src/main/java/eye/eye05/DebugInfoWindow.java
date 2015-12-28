package eye.eye05;

import drjava.util.GUIUtil;
import drjava.util.LetterLayout;
import drjava.util.ScalingSplitPane;
import eye.eye01.ScrollableImage;
import eyedev._01.DebugItem;
import eyedev._09.Subrecognition;
import prophecy.common.image.RGBImage;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.util.ArrayList;
import java.util.List;

public class DebugInfoWindow extends JFrame {
  private ScrollableImage scrollableImage;
  private JTextArea textArea;
  private List<DebugItem> debugItems;

  public DebugInfoWindow(List<DebugItem> debugInfo) {
    setTitle("Recognition debug info");
    setSize(600, 500);
    GUIUtil.centerOnScreen(this);

    debugItems = debugInfo;
    if (debugItems == null) debugItems = new ArrayList<DebugItem>();

    String[] strings = new String[debugItems.size()];
    for (int row = 0; row < debugItems.size(); row++) {
      DebugItem item = debugItems.get(row);
      strings[row] = item.name;
      //strings[row] = item.name + " (" + nice(item.data.getClass()) + ")";
    }
    final JList list = new JList(strings);

    scrollableImage = new ScrollableImage();
    textArea = new JTextArea();
    textArea.setWrapStyleWord(true);
    textArea.setLineWrap(true);

    list.addListSelectionListener(new ListSelectionListener() {
      public void valueChanged(ListSelectionEvent listSelectionEvent) {
        int idx = list.getSelectedIndex();
        if (idx >= 0) {
          showItem(debugItems.get(idx));
        } else {
          scrollableImage.setImage(null);
          textArea.setText("");
        }
      }
    });

    JPanel rightPanel = new JPanel(new LetterLayout("I", "A"));
    rightPanel.add("I", scrollableImage);
    rightPanel.add("A", new JScrollPane(textArea));

    ScalingSplitPane splitPane = new ScalingSplitPane(ScalingSplitPane.HORIZONTAL_SPLIT, 0.5f);
    splitPane.add(GUIUtil.withTitle("Debug items", new JScrollPane(list)));
    splitPane.add(GUIUtil.withTitle("Details", rightPanel));

    getContentPane().add(splitPane);

    setZoom(2.0);
  }

  private void showItem(DebugItem debugItem) {
    scrollableImage.setImage(extractImage(debugItem));
    textArea.setText(extractText(debugItem));
    textArea.setCaretPosition(0);
  }

  private RGBImage extractImage(DebugItem item) {
    if (item.data instanceof Subrecognition)
      return ((Subrecognition) item.data).image.toRGB();
    return null;
  }

  private String extractText(DebugItem item) {
    if (item.data instanceof Subrecognition) {
      Subrecognition s = (Subrecognition) item.data;
      return "Recognized text: " + s.text + "\n\n" +
        "Recognizer: " + s.recognizer;
    }
    return "";
  }

  public void setZoom(double zoom) {
    scrollableImage.setZoom(zoom);
  }
}
