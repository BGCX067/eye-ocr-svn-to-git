package eye.eye02;

import drjava.util.GUIUtil;
import drjava.util.LetterLayout;
import drjava.util.ScalingSplitPane;
import eye.eye01.EyeGuiUtil;
import eye.eye01.MakeImageDialog;
import eye.eye01.ScrollableImage;
import eyedev._01.ImageReader;
import eyedev._01.OCRUtil;
import prophecy.common.image.RGBImage;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

// TODO: fix this whole thing (make it more like Main)
public class RecognitionDialog extends JFrame {
  private JTextArea textArea;
  private RGBImage image;
  private ScrollableImage scrollableImage;
  private String recognizerDesc;
  private String preferredFontName;

  public RecognitionDialog(String recognizerDesc, String recognizerName, String preferredFontName) {
    this.recognizerDesc = recognizerDesc;
    this.preferredFontName = preferredFontName;
    setTitle("[Warning: This dialog is outdated] Recognize text - " + recognizerName);
    setSize(700, 400);

    ScalingSplitPane splitPane = new ScalingSplitPane(ScalingSplitPane.VERTICAL_SPLIT, 0.7f);
    scrollableImage = new ScrollableImage();

    /*JPanel topPanel = new JPanel(new LetterLayout("I", "I", "R").setSpacing(0));
    topPanel.add("I", GUIUtil.withTitle("Input image", scrollableImage));
    topPanel.add("R", GUIUtil.withLabel("Recognizer:", cbRecognizer));*/
    splitPane.setTopComponent(GUIUtil.withTitle("Input image", scrollableImage));

    textArea = new JTextArea();
    /*JPanel bottomPanel = new JPanel(new LetterLayout("R", "T", "T"));
    bottomPanel.add("R", GUIUtil.withLabel("Recognizer:", cbRecognizer));
    bottomPanel.add("T", GUIUtil.withTitle("Recognized text", new JScrollPane(textArea)));*/
    splitPane.setBottomComponent(GUIUtil.withTitle("Recognized text", new JScrollPane(textArea)));

    ActionListener openImage = EyeGuiUtil.actionListener(this, "openImage");
    ActionListener makeImage = EyeGuiUtil.actionListener(this, "makeImage");

    JPanel buttons = new JPanel(LetterLayout.stalactite());
    JButton btnOpenImage = new JButton("Load image...");
    btnOpenImage.addActionListener(openImage);
    buttons.add(btnOpenImage);
    
    JButton btnMakeImage = new JButton("Make image...");
    btnMakeImage.addActionListener(makeImage);
    buttons.add(btnMakeImage);

    JPanel mainPanel = new JPanel(new LetterLayout("CCB").setBorder(10));
    mainPanel.add("C", splitPane);
    mainPanel.add("B", buttons);
    getContentPane().add(mainPanel);

    JMenuItem mi;

    JMenu imageMenu = new JMenu("Image");
    mi = new JMenuItem("Load image...");
    mi.addActionListener(openImage);
    imageMenu.add(mi);
    mi = new JMenuItem("Make image...");
    mi.addActionListener(makeImage);
    imageMenu.add(mi);

    /*JMenu extrasMenu = new JMenu("Extras");
    mi = new JMenuItem("Learn a font...");
    mi.addActionListener(EyeGuiUtil.actionListener(this, "makeRecognizer"));
    extrasMenu.add(mi);*/

    JMenuBar menuBar = new JMenuBar();
    menuBar.add(imageMenu);
    //menuBar.add(extrasMenu);
    setJMenuBar(menuBar);

    GUIUtil.centerOnScreen(this);
  }

  @SuppressWarnings({"UnusedDeclaration"})
  public void openImage() throws IOException {
    JFileChooser fileChooser = new JFileChooser();
    fileChooser.setCurrentDirectory(new File("examples"));
    if (fileChooser.showDialog(this, "Open image") == JFileChooser.APPROVE_OPTION) {
      File file = fileChooser.getSelectedFile();
      if (file != null)
        openFile(file);
    }
  }

  @SuppressWarnings({"UnusedDeclaration"})
  public void makeImage() {
    MakeImageDialog dialog = new MakeImageDialog(this, preferredFontName);
    dialog.setModal(true);
    dialog.setVisible(true);
    if (dialog.image != null)
      setImage(dialog.image);
  }

  private void openFile(File file) throws IOException {
    BufferedImage image = ImageIO.read(file);
    setImage(new RGBImage(image));
  }

  private void setImage(RGBImage image) {
    this.image = image;
    scrollableImage.setImage(image);

    //btnRecognize.setEnabled(true);
    recognize();
  }

  private void recognize() {
    if (image == null) return;
    ImageReader recognizer = OCRUtil.makeImageReader(recognizerDesc);
    String text = recognizer.readImage(image.toBW());
    if (text == null) text = "";
    textArea.setText(text);
  }

  /*
  @SuppressWarnings({"UnusedDeclaration"})
  public void makeRecognizer() {
    new MakeRecognizerDialog().setVisible(true);
  }
  */
}
