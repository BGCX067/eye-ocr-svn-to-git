package eye.eye01;

import drjava.util.GUIUtil;
import drjava.util.LetterLayout;
import drjava.util.RepeatableRandomizer;
import eyedev._01.ImageReader;
import eyedev._01.OCRUtil;
import eyedev._06.Recognizer06;
import eyedev._07.*;
import eyedev._09.WithAdvancedSegmenter;
import prophecy.common.ClassData;
import prophecy.common.gui.CancelButton;
import prophecy.common.gui.Sheet;

import javax.swing.*;
import java.awt.*;

public class RunTestDialog extends JFrame {
  private JComboBox cbTest;
  private JComboBox cbRecognizer;
  private ClassData classData;
  private JTextArea taResults;
  private TestProtocol protocol;
  private JButton btnShowProtocol;

  public RunTestDialog() {
    setTitle("Run a test");
    setSize(600, 400);
    GUIUtil.centerOnScreen(this);

    classData = ClassData.get(this);

    JPanel buttons = new JPanel(LetterLayout.stalactite());

    JButton btnRun = new JButton("Run");
    btnRun.addActionListener(EyeGuiUtil.actionListener(this, "run"));
    buttons.add(btnRun);

    JButton btnShowDefinition = new JButton("Recognizer code...");
    btnShowDefinition.addActionListener(EyeGuiUtil.actionListener(this, "showDefinition"));
    buttons.add(btnShowDefinition);

    btnShowProtocol = new JButton("Show protocol");
    btnShowProtocol.addActionListener(EyeGuiUtil.actionListener(this, "showProtocol"));
    btnShowProtocol.setEnabled(false);
    buttons.add(btnShowProtocol);

    CancelButton btnDone = new CancelButton();
    btnDone.setText("Done");
    buttons.add(btnDone);

    Sheet sheet = new Sheet();
    sheet.getSheetLayout().setBorder(0);

    cbTest = new JComboBox();
    cbTest.addItem("Arial 40, uppercase, 100*5 chars");

    sheet.addLeftAlignedLabel("Please choose a test:");
    sheet.addComponent(cbTest);

    cbRecognizer = new JComboBox();
    cbRecognizer.addItem("Alpha 2 default");

    sheet.addLeftAlignedLabel("Please choose which recognizer to test:");
    sheet.addComponent(cbRecognizer);

    sheet.addSpacer();
    sheet.addLeftAlignedLabel("Results:");

    taResults = new JTextArea();

    getContentPane().setLayout(new LetterLayout("SSB", "IIB", "IIB").setBorder(10));
    getContentPane().add("B", buttons);
    getContentPane().add("S", sheet.getPanel());
    getContentPane().add("I", new JScrollPane(taResults));
  }

  @Override
  public void dispose() {
    //classData.save();
    super.dispose();
  }

  @SuppressWarnings({"UnusedDeclaration"})
  public void run() {
    Font font = new Font("Arial", Font.PLAIN, 40);
    String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    int rows = 100;
    int cols = 5;
    RecognitionTest test = new SingleFontRecognitionTest("bla", font,
      new RandomStrings(rows, cols, alphabet, new RepeatableRandomizer()));

    ImageReader recognizer = makeRecognizer();

    final TestRunner testRunner = new TestRunner(test, recognizer);
    testRunner.addProgressListener(new Runnable() {
      public void run() {
        final int progress = (int) (testRunner.getProgress()*100);
        SwingUtilities.invokeLater(new Runnable() {
          public void run() {
            taResults.setText("Testing... " + progress + " %");
          }
        });
      }
    });

    new Thread() {
      public void run() {
        testRunner.run();
        final String result = "Score: " + testRunner.getScore() + " of " + testRunner.getMax();
        SwingUtilities.invokeLater(new Runnable() {
          public void run() {
            taResults.setText(result);
            protocol = testRunner.getProtocol();
            btnShowProtocol.setEnabled(true);
          }
        });
      }
    }.start();
  }

  @SuppressWarnings({"UnusedDeclaration"})
  public void showDefinition() {
    String desc = OCRUtil.getImageReaderDescription(makeRecognizer());
    CodeWindow.show(desc, "Recognizer code", "Recognizer code");
  }

  @SuppressWarnings({"UnusedDeclaration"})
  public void showProtocol() {
    final TestProtocol protocol = this.protocol;
    TestProtocolWindow.show(protocol, OCRUtil.getImageReaderDescription(makeRecognizer()));
  }

  private ImageReader makeRecognizer() {
    return new WithAdvancedSegmenter(Recognizer06.getRecognizerDesc());
  }
}
