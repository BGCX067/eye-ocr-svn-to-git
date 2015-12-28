package eye.eye05;

import drjava.util.*;
import eye.eye02.EyeDialogs;
import eye.eye03.RecognizerInputType;
import eye.eye03.Recognizers;
import eyedev._13.StandardDialog;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CreateRecognizerDialog extends StandardDialog {
  private JTextArea taCode;
  private JButton btnOK;
  private boolean codeChanged;
  private Recognizers recognizers;
  private JComboBox cbInputType;

  public CreateRecognizerDialog(Recognizers recognizers) {
    this.recognizers = recognizers;

    setTitle("Create recognizer manually");
    setSize(500, 300);
    GUIUtil.centerOnScreen(this);

    taCode = new JTextArea();

    cbInputType = new JComboBox(new Object[] {"unknown", "character", "line", "lines" });

    mainPanel.setLayout(new LetterLayout("C", "C", "T"));
    mainPanel.add("C", GUIUtil.withTitle("Recognizer code", new JScrollPane(taCode)));
    mainPanel.add("T", GUIUtil.withLabel("Input type:", cbInputType));

    btnOK = addButton("Create recognizer", new ActionListener() {
      public void actionPerformed(ActionEvent actionEvent) {
        save();
      }
    });
    btnOK.setEnabled(false);

    taCode.getDocument().addDocumentListener(new DocumentListener() {
      public void insertUpdate(DocumentEvent documentEvent) {
        codeChanged = true;
      }

      public void removeUpdate(DocumentEvent documentEvent) {
        codeChanged = true;
      }

      public void changedUpdate(DocumentEvent documentEvent) {
        codeChanged = true;
      }
    });

    new SwingTimerHelper(new Runnable() {
      public void run() {
        checkSyntax();
      }
    }, 1000).installOn(taCode);
  }

  private void save() {
    try {
      if (syntaxOK()) {
        RecognizerInputType inputType = RecognizerInputType.values()[cbInputType.getSelectedIndex()];
        if (EyeDialogs.saveRecognizer(this, recognizers, taCode.getText(), null,
          inputType, null, true) != null)
          dispose();
      }
    } catch (Throwable e) {
      Errors.report(e);
    }
  }

  private boolean syntaxOK() {
    try {
      Tree.parse(taCode.getText());
      return true;
    } catch (Throwable e) {
      return false;
    }
  }

  private void checkSyntax() {
    if (codeChanged) {
      codeChanged = false;
      String text = taCode.getText();
      try {
        Tree.parse(text);
        btnOK.setEnabled(true);
      } catch (Throwable e) {
        btnOK.setEnabled(false);
      }
    }
  }
}
