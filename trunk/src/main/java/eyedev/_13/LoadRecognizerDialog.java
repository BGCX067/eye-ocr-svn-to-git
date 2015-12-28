package eyedev._13;

import drjava.util.Errors;
import drjava.util.GUIUtil;
import drjava.util.LetterLayout;
import eye.eye03.RecognizerInfo;
import eye.eye03.RecognizerListFilter;
import eye.eye03.RecognizerSelector;
import eye.eye03.Recognizers;
import prophecy.common.gui.CancelButton;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoadRecognizerDialog extends JDialog {
  public RecognizerInfo recognizerInfo;
  private RecognizerSelector recognizerSelector;

  public LoadRecognizerDialog(Window owner, Recognizers recognizers, RecognizerListFilter filter) {
    super(owner, "Load recognizer");

    recognizerSelector = new RecognizerSelector(recognizers, filter);

    JPanel buttons = new JPanel(LetterLayout.stalactite());

    JButton button = new JButton("Load");
    button.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent actionEvent) {
        try {
          recognizerInfo = recognizerSelector.getSelectedRecognizer();
          dispose();
        } catch (Throwable e) {
          Errors.report(e);
        }
      }
    });
    buttons.add(button);

    buttons.add(new CancelButton());

    JPanel selectorPanel = new JPanel(LetterLayout.stalactite());
    selectorPanel.add(recognizerSelector);

    JPanel panel = new JPanel(new LetterLayout("RRB").setBorder(10));
    panel.add("R", selectorPanel);
    panel.add("B", buttons);
    getContentPane().add(panel);
    pack();
    GUIUtil.centerOnScreen(this);
  }
}
