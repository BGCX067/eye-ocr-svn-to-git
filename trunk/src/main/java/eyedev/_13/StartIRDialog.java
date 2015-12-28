package eyedev._13;

import drjava.util.Errors;
import drjava.util.GUIUtil;
import drjava.util.LetterLayout;
import eye.eye01.ScrollableImage;
import eye.eye03.EyeEnv;
import eye.eye03.RecognizerInfo;
import eye.eye03.RecognizerSelector;
import eye.eye03.Recognizers;
import prophecy.common.gui.AutoVMExit;
import prophecy.common.image.RGBImage;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;

/** Dialog to startup interactive recognition */
public class StartIRDialog extends StandardDialog {
  private Recognizers recognizers;
  private ScrollableImage scrollableImage;
  private RGBImage image;
  private JButton btnStart;
  private RecognizerSelector recognizerSelector;
  //private JCheckBox cbNewRecognizer;

  public StartIRDialog(Recognizers recognizers) {
    this.recognizers = recognizers;
    setTitle("Eye - Interactive recognition");
    setSize(700, 400);
    GUIUtil.centerOnScreen(this);

    btnStart = addButton("Start interactive recognition", new ActionListener() {
      public void actionPerformed(ActionEvent actionEvent) {
        start();
      }
    });
    btnStart.setEnabled(false);

    addButton("Load image...", new ActionListener() {
      public void actionPerformed(ActionEvent actionEvent) {
        openImage();
      }
    });

    recognizerSelector = new RecognizerSelector(recognizers, InteractiveRecognition.getRecognizerListFilter());
    recognizerSelector.addNullEntry("New recognizer");
    recognizerSelector.addItemListener(new ItemListener() {
      public void itemStateChanged(ItemEvent itemEvent) {
        updateButtons();
      }
    });

    /*cbNewRecognizer = new JCheckBox("Start with empty recognizer");
    cbNewRecognizer.addItemListener(new ItemListener() {
      public void itemStateChanged(ItemEvent itemEvent) {
        recognizerSelector.setEnabled(!cbNewRecognizer.isSelected());
        updateButtons();
      }
    });*/

    mainPanel.setLayout(new LetterLayout("I", "I", "C", "r", "R"));
    scrollableImage = new ScrollableImage();
    mainPanel.add("I", GUIUtil.withTitle("Input image", scrollableImage));
    //mainPanel.add("C", cbNewRecognizer);
    mainPanel.add("r", new JLabel("Start with this recognizer:"));
    mainPanel.add("R", recognizerSelector);
  }

  private void start() {
    try {
      if (image == null) return;
      RecognizerInfo recognizer = /*cbNewRecognizer.isSelected() ? null :*/ recognizerSelector.getSelectedRecognizer();
      InteractiveRecognition ir = new InteractiveRecognition(recognizers, image, recognizer);
      ir.setVisible(true);
      dispose();
    } catch (Throwable e) {
      Errors.report(e);
    }
  }

  public void openImage() {
    try {
      JFileChooser fileChooser = new JFileChooser();
      fileChooser.setCurrentDirectory(new File("examples"));
      if (fileChooser.showDialog(this, "Open image") == JFileChooser.APPROVE_OPTION) {
        File file = fileChooser.getSelectedFile();
        if (file != null)
          setImage(new RGBImage(ImageIO.read(file)));
      }
    } catch (Throwable e) {
      Errors.report(e);
    }
  }

  private void setImage(RGBImage image) {
    this.image = image;
    scrollableImage.setImage(image);
    updateButtons();
  }

  private void updateButtons() {
    //boolean recognizerSelected = cbNewRecognizer.isSelected() || recognizerSelector.getSelectedRecognizer() != null;
    btnStart.setEnabled(image != null /*&& recognizerSelected*/);
  }

  public static void main(String[] args) {
    EyeEnv.init();
    AutoVMExit.install();
    new StartIRDialog(new Recognizers()).setVisible(true);
  }
}
