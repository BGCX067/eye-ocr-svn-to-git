package eye.eye01;

import drjava.util.GUIUtil;
import drjava.util.LetterLayout;
import drjava.util.ScalingSplitPane;
import drjava.util.Tree;
import eye.eye03.RecognizerInfo;
import eyedev._07.ProtocolEntry;
import eyedev._07.TestProtocol;
import prophecy.common.image.RGBImage;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TestProtocolWindow extends JFrame {
  private ScrollableImage scrollableImage;

  public static TestProtocolWindow show(final TestProtocol protocol, final String recognizer) {
    return new TestProtocolWindow(protocol, recognizer);
  }

  TestProtocolWindow(final TestProtocol protocol, final String recognizer) {
    setTitle("Test protocol");
    setSize(600, 500);
    GUIUtil.centerOnScreen(this);

    String[] strings = new String[protocol.entries.size()];
    for (int row = 0; row < protocol.entries.size(); row++) {
      ProtocolEntry entry = protocol.entries.get(row);
      if (entry.correctText.equals(entry.recognizedText))
        strings[row] = "OK: \"" + entry.correctText + "\"";
      else
        strings[row] = "Expected: \"" + entry.correctText + "\", got: \"" + entry.recognizedText + "\"";
    }
    final JList list = new JList(strings);

    scrollableImage = new ScrollableImage();
    final JTextArea taAnalysis = new JTextArea();

    list.addListSelectionListener(new ListSelectionListener() {
      public void valueChanged(ListSelectionEvent listSelectionEvent) {
        int idx = list.getSelectedIndex();
        if (idx >= 0) {
          RGBImage image = protocol.entries.get(idx).getImage();
          scrollableImage.setImage(image);
          taAnalysis.setText(ImageAnalysis.getAnalysis(image));
        } else {
          scrollableImage.setImage(null);
          taAnalysis.setText("");
        }
      }
    });

    JPanel rightPanel = new JPanel(new LetterLayout("I", "A"));
    rightPanel.add("I", scrollableImage);
    rightPanel.add("A", new JScrollPane(taAnalysis));

    ScalingSplitPane splitPane = new ScalingSplitPane(ScalingSplitPane.HORIZONTAL_SPLIT, 0.5f);
    splitPane.add(GUIUtil.withTitle("Test items", new JScrollPane(list)));
    splitPane.add(GUIUtil.withTitle("Input image", rightPanel));

    JPanel buttons = new JPanel(LetterLayout.leftAlignedRow());
    JButton btnCode = new JButton("Show recognizer code");
    btnCode.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent actionEvent) {
        CodeWindow.show(recognizer, "Recognizer code", "Recognizer code");
      }
    });
    buttons.add(btnCode);

    getContentPane().setLayout(new LetterLayout("B", "S", "S").setBorder(10));
    getContentPane().add("B", buttons);
    getContentPane().add("S", splitPane);

    setZoom(2.0);
    setVisible(true);
  }

  public void setZoom(double zoom) {
    scrollableImage.setZoom(zoom);
  }
}
