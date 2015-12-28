package eye.eye04;

import drjava.util.GUIUtil;
import drjava.util.LetterLayout;
import drjava.util.ScalingSplitPane;
import eye.eye01.ScrollableImage;
import eyedev._01.CharacterLearner;
import eyedev._01.Example;
import eyedev._01.ExampleSet;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class KnownCharactersWindow extends JFrame {
  private ScrollableImage scrollableImage;
  private ExampleSet exampleSet;

  public KnownCharactersWindow(CharacterLearner characterLearner) {
    setTitle("Known characters");
    setSize(600, 500);
    GUIUtil.centerOnScreen(this);

    exampleSet = characterLearner.getExampleSet();

    String[] strings = new String[exampleSet.examples.size()];
    for (int row = 0; row < exampleSet.examples.size(); row++) {
      Example example = exampleSet.get(row);
      strings[row] = example.text;
    }
    final JList list = new JList(strings);

    scrollableImage = new ScrollableImage();
    final JTextArea taAnalysis = new JTextArea();

    list.addListSelectionListener(new ListSelectionListener() {
      public void valueChanged(ListSelectionEvent listSelectionEvent) {
        int idx = list.getSelectedIndex();
        if (idx >= 0) {
          Example example = exampleSet.get(idx);
          scrollableImage.setImage(example.image.toRGB());
          taAnalysis.setText(makeAnalysis(example));
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
    splitPane.add(GUIUtil.withTitle("Characters", new JScrollPane(list)));
    splitPane.add(GUIUtil.withTitle("Image", rightPanel));

    JPanel buttons = new JPanel(LetterLayout.leftAlignedRow());

    getContentPane().setLayout(new LetterLayout("B", "S", "S").setBorder(10));
    getContentPane().add("B", buttons);
    getContentPane().add("S", splitPane);

    setZoom(2.0);
    setVisible(true);
  }

  private String makeAnalysis(Example example) {
    return "Width, height: " + example.image.getWidth() + "*" + example.image.getHeight() + "\n" +
      "Top line, base line: " + example.topLine + ", " + example.baseLine;
  }

  public void setZoom(double zoom) {
    scrollableImage.setZoom(zoom);
  }
}
