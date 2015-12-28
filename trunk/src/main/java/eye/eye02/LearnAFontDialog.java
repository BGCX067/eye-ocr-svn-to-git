package eye.eye02;

import drjava.util.Errors;
import drjava.util.GUIUtil;
import drjava.util.LetterLayout;
import drjava.util.StringUtil;
import eye.eye01.CodeWindow;
import eye.eye01.EyeGuiUtil;
import eye.eye01.ScrollableImage;
import eye.eye01.TextPainter2;
import eye.eye03.*;
import eyedev._05.Alphabet;
import eyedev._08.FontLearner;
import eyedev._08.SegSigLearner;
import eyedev._08.TopList;
import eyedev._19.SideProfileLearner;
import eyedev._20.CompareImagesLearner;
import prophecy.common.ClassData;
import prophecy.common.gui.CancelButton;
import prophecy.common.gui.Form;
import prophecy.common.gui.Sheet;
import prophecy.common.image.RGBImage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.List;

public class LearnAFontDialog extends JFrame {
  private ScrollableImage scrollableImage;
  private JTextArea taResults;
  private JButton btnTest;
  private String recognizer, recognizerFontName;
  private FontSelector fontSelector;
  private static final String previewText = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
  private ClassData classData;
  private JButton btnSave;
  private Recognizers recognizers;
  //private JRadioButton rbAllSizes;
  //private JRadioButton rbAllChars;
  private JComboBox cbCharacterSet, cbSizes;
  private String alg_compareImages = "Compare images (standard)";
  private String alg_segsig = "Segment signature (traditional)";
  private String alg_sideprofile = "Side profile (experimental)";
  private JComboBox cbAlgorithm;
  private JLabel lblFontPreview;

  public LearnAFontDialog(Recognizers recognizers) {
    this.recognizers = recognizers;
    setTitle("Learn a font");
    setSize(700, 600);
    GUIUtil.centerOnScreen(this);

    classData = ClassData.get(this);

    JPanel buttons = new JPanel(LetterLayout.stalactite());

    JButton btnLearn = new JButton("Learn font");
    btnLearn.addActionListener(EyeGuiUtil.actionListener(this, "start"));
    buttons.add(btnLearn);

    btnTest = new JButton("Test recognizer");
    btnTest.addActionListener(EyeGuiUtil.actionListener(this, "test"));
    btnTest.setEnabled(false);
    buttons.add(btnTest);

    btnSave = new JButton("Save recognizer");
    btnSave.addActionListener(EyeGuiUtil.actionListener(this, "save"));
    btnSave.setEnabled(false);
    buttons.add(btnSave);

    JButton btnShowAlphabet = new JButton("Show alphabet");
    btnShowAlphabet.addActionListener(EyeGuiUtil.actionListener(this, "showAlphabet"));
    buttons.add(btnShowAlphabet);

    buttons.add(new CancelButton("Done"));

    Sheet sheet = new Sheet();
    sheet.getSheetLayout().setBorder(0);

    Form form = new Form();

    fontSelector = new FontSelector();
    fontSelector.selectFont(classData.getString("fontName"));
    fontSelector.addItemListener(new ItemListener() {
      public void itemStateChanged(ItemEvent itemEvent) {
        classData.setString("fontName", fontSelector.getSelectedFontName());
        classData.save();
        updatePreviewImage();
      }
    });
    fontSelector.setMultiSelect(true);

    //sheet.addLeftAlignedLabel("Font to be learned:");
    form.addRow("Font(s) to be learned:", fontSelector);

    // character set selection

    /*ButtonGroup group = new ButtonGroup();
    JRadioButton rbUppercase = new JRadioButton("Uppercase only");
    rbAllChars = new JRadioButton("Upper + lower + punctuation", true);
    group.add(rbUppercase);
    group.add(rbAllChars);*
    sheet.addComponent(new Line(rbUppercase, rbAllChars));*/
    cbCharacterSet = new JComboBox(new Object[] {"All characters", "Uppercase only"});
    form.addRow("Characters:", cbCharacterSet);

    // sizes selection

    /*ButtonGroup group = new ButtonGroup();
    JRadioButton rbOneSize = new JRadioButton("Learn only size 30", true);
    rbAllSizes = new JRadioButton("Learn 8 sizes (30-100)");
    group.add(rbOneSize);
    group.add(rbAllSizes);
    sheet.addComponent(new Line(rbOneSize, rbAllSizes));*/
    cbSizes = new JComboBox(new Object[] {"Multiple sizes (30 through 100)", "Single size (30)"});
    cbSizes.setSelectedIndex(classData.getInt("cbSizes", 0));
    form.addRow("Sizes:", cbSizes);

    // algorithm selection

    cbAlgorithm = new JComboBox(new String[] {alg_compareImages, alg_segsig, alg_sideprofile});
    form.addRow("Algorithm:", cbAlgorithm);

    sheet.addComponent(form);

    lblFontPreview = sheet.addBlueHeading("Font preview (size 30)");
    scrollableImage = new ScrollableImage();
    scrollableImage.setMinimumSize(new Dimension(100, 80));
    sheet.addComponent(scrollableImage);

    sheet.addBlueHeading("Results");

    taResults = new JTextArea();
    taResults.setLineWrap(true);
    taResults.setWrapStyleWord(true);

    getContentPane().setLayout(new LetterLayout("SSB", "RRB", "RRB").setBorder(10));
    getContentPane().add("B", buttons);
    getContentPane().add("S", sheet.getPanel());
    getContentPane().add("R", new JScrollPane(taResults));

    updatePreviewImage();
  }

  public void updatePreviewImage() {
    try {
      int size = 30;
      Font font = getFont(size);
      RGBImage image = font == null ? null : new TextPainter2(font).makeImage(previewText).toRGB();
      scrollableImage.setImage(image);
      lblFontPreview.setText(font == null
        ? "Font preview (size 30)"
        : "Font preview (" + font.getName() + ", size " + size + ")");
    } catch (Throwable e) {
      Errors.report(e);
    }
  }

  private Font getFont(int size) throws Exception {
    Font font = fontSelector.getSelectedFont();
    if (font == null) return null;
    font = font.deriveFont((float) size);
    return font;
  }

  @SuppressWarnings({"UnusedDeclaration"})
  public void start() {
    String result;
    try {
      classData.setInt("cbSizes", cbSizes.getSelectedIndex());
      classData.save();

      final FontLearner fontLearner;
      if (cbAlgorithm.getSelectedItem() == alg_compareImages)
        fontLearner = new CompareImagesLearner();
      else if (cbAlgorithm.getSelectedItem() == alg_sideprofile)
        fontLearner = new SideProfileLearner();
      else if (cbAlgorithm.getSelectedItem() == alg_segsig)
        fontLearner = new SegSigLearner();
      else
        throw new RuntimeException("Unknown algorithm");
      List<Font> fonts = getFonts();
      for (Font font : fonts) {
        if (cbSizes.getSelectedIndex() == 0) {
          for (int size = 30; size <= 100; size += 10)
            fontLearner.addFont(font.deriveFont((float) size));
        } else
          fontLearner.addFont(font.deriveFont((float) 30));
      }
      fontLearner.setAllChars(cbCharacterSet.getSelectedIndex() == 0);

      new ProgressDialog<Object>(this, "Learning font") {
        protected void done(Object o) {
          TopList toplist = fontLearner.getToplist();
          String result;
          if (toplist.isEmpty())
            result = "Font could not be learned";
          else {
            String fullCode = toplist.get(0);
            String shortenedCode = EyeGuiUtil.shortenCode(fullCode);
            result = "Font has been learned. Achieved accuracy on character level: " +
              StringUtil.formatDouble(toplist.getTopScore()*100, 1) + "%\n\n" +
              "Recognizer code (" + fullCode.length() + " chars):\n\n" + shortenedCode;
            recognizer = fullCode;
            /*recognizer = OCRUtil.getImageReaderDescription(
              new WithFlexibleSegmenter(recognizer));*/
            recognizerFontName = fontSelector.getSelectedFontName();
            btnTest.setEnabled(true);
            btnSave.setEnabled(true);
          }

          taResults.setText(result);
          taResults.setCaretPosition(0);
        }

        protected Object doInBackground() throws Exception {
          fontLearner.setStatusListener(getStatusListener());
          fontLearner.go();
          return null;
        }
      }.start();
    } catch (Throwable e) {
      e.printStackTrace();
      taResults.setText(e.toString());
      taResults.setCaretPosition(0);
    }
  }

  private List<Font> getFonts() throws Exception {
    return fontSelector.getSelectedFonts();
  }

  @SuppressWarnings({"UnusedDeclaration"})
  public void test() {
    if (recognizer != null)
      new RecognitionDialog(recognizer, recognizerFontName + " recognizer (user-made)",
        recognizerFontName).setVisible(true);
  }

  @SuppressWarnings({"UnusedDeclaration"})
  public void save() {
    try {
      if (recognizer != null) {
        EyeDialogs.saveRecognizer(this, recognizers, recognizer, recognizerFontName,
          RecognizerInputType.character, null, true);
      }
    } catch (Throwable e) {
      Errors.report(e);
    }
  }

  @SuppressWarnings({"UnusedDeclaration"})
  public void showAlphabet() {
    try {
      CodeWindow.show(Alphabet.getAllChars(), "Alphabet used for learning", "");
    } catch (Throwable e) {
      Errors.report(e);
    }
  }

  public static void main(String[] args) {
    EyeEnv.init();
    EyeData eyeData = new EyeData();
    LearnAFontDialog dialog = new LearnAFontDialog(eyeData.recognizers);
    dialog.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    dialog.setVisible(true);
  }
}
