package eyedev._13;

import drjava.util.Errors;
import drjava.util.GUIUtil;
import drjava.util.LetterLayout;
import drjava.util.ScalingSplitPane;
import eye.eye01.ScrollableImage;
import eye.eye02.EyeDialogs;
import eye.eye03.*;
import eye.eye05.DebugInfoWindow;
import eye.eye05.RecognizableImage;
import eyedev._01.DebugItem;
import eyedev._01.Example;
import eyedev._01.ImageReader;
import eyedev._01.OCRUtil;
import eyedev._09.Subrecognition;
import eyedev._18.WithFratboySegmenter;
import prophecy.common.image.BWImage;
import prophecy.common.image.ImageProcessing;
import prophecy.common.image.RGBImage;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class InteractiveRecognition extends JFrame {
  private Class<? extends IRSegmenter> segmenterClass = WithFratboySegmenter.class;
  private RGBImage baseImage;
  private RecognizableImage recognizableImage;
  private JTextArea taRecognizedText;
  private JTextField tfSelectedCharacter;
  private CompareImages compareImages;
  private boolean correctionEnabled = true;
  private boolean quickSelect = true;
  private IRSegmenter segmenter;
  private Recognizers recognizers;
  private SelectableImage selectableImage;
  private JTabbedPane tabs;
  private RGBImage region;
  private JLabel lblCharactersTrained;
  private boolean recognizerChanged;
  private String standardRecognizerName = "Default interactive recognizer";
  private JSlider thresholdSlider;
  private ScrollableImage bwScrollableImage;
  private int threshold = -1;
  private List<DebugItem> debugInfo;
  private String recognizerName = standardRecognizerName;

  public static void main(String[] args) {
    Errors.setPopup(true);
    
    String file = "other-examples/frat-boy.jpg";
    RGBImage image = RGBImage.load(file);
    GUIUtil.showMainFrame(new InteractiveRecognition(new Recognizers(), image, null));
  }

  public InteractiveRecognition(Recognizers recognizers, final RGBImage image, RecognizerInfo recognizer) {
    setTitle("Eye - Interactive recognition");
    setSize(800, 600);
    GUIUtil.centerOnScreen(this);

    this.recognizers = recognizers;
    this.baseImage = image;

    compareImages = new CompareImages();

    recognizableImage = new RecognizableImage() {
      protected void renderOverlays(Graphics2D g) {
        for (DebugItem debugItem : debugInfo) {
          if (debugItem.data instanceof Subrecognition) {
            Subrecognition s = (Subrecognition) debugItem.data;

            //Color boxColor = new Color(1f, 0f, 0f, 1f-s.confidence);
            //Color boxColor = PixelUtil.blend(Color.red, Color.white, s.confidence).getColor();

            Color textColor = Color.black; //new Color(0f, 0f, 0f, s.confidence);

            //System.out.println("Confidence: " + s.confidence + ", color: " + boxColor);
            //drawBox(g, s, boxColor, 2);

            String text = s.text == null ? "?" : s.text;
            int x = s.clip.x + s.clip.width / 2;
            int y = s.clip.y + s.clip.height + 4;
            FontMetrics metrics = g.getFontMetrics();
            x -= metrics.stringWidth(text) / 2;
            g.setColor(Color.white);
            g.fillRect(x, y, metrics.stringWidth(text), metrics.getHeight());
            g.setColor(textColor);
            y += metrics.getAscent();
            g.drawString(text, x, y);
          }
        }
        super.renderOverlays(g);
      }
    };

    recognizableImage.setDoubleBuffering(true);
    recognizableImage.setSelectableBoxes(!quickSelect);
    recognizableImage.setDrawConfidenceBoxes(true);

    tfSelectedCharacter = new JTextField();
    taRecognizedText = new JTextArea();
    taRecognizedText.setEditable(false);

    String helpText;
    if (quickSelect)
      helpText = "Move mouse over a character, then put correction here:";
    else
      helpText = "To correct, click in image, then type correct character:";
    
    JPanel buttons = new JPanel(LetterLayout.stalactite());

    buttons.add(new JPanel());

    buttons.add(new JLabel("Characters trained:", JLabel.CENTER));
    lblCharactersTrained = new JLabel("0", JLabel.CENTER);
    buttons.add(lblCharactersTrained);

    buttons.add(new JPanel());
    
    JButton button = new JButton("Load recognizer...");
    button.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent actionEvent) {
        loadRecognizer();
      }
    });
    buttons.add(button);

    button = new JButton("Save recognizer...");
    button.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent actionEvent) {
        saveRecognizer(true);
      }
    });
    buttons.add(button);

    JButton btnDebug = new JButton("Debug");
    btnDebug.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent actionEvent) {
        debug();
      }
    });
    buttons.add(btnDebug);

    /*JPanel panel = new JPanel(new LetterLayout("IIB", "IIB", "CCB", "TTB").setBorder(10));
    panel.add("I", recognizableImage);
    panel.add("C", GUIUtil.withTitle(helpText, tfSelectedCharacter));
    panel.add("T", GUIUtil.withTitle("Recognized text", new JScrollPane(tfRecognizedText)));
    panel.add("B", buttons);
    getContentPane().add(panel);*/

    JPanel panel = new JPanel(new LetterLayout("C", "T", "T").setBorder(10));
    panel.add("C", GUIUtil.withTitle(helpText, tfSelectedCharacter));
    panel.add("T", GUIUtil.withTitle("Recognized text", new JScrollPane(taRecognizedText)));

    ScalingSplitPane splitPane = new ScalingSplitPane(ScalingSplitPane.VERTICAL_SPLIT, 0.7f);
    splitPane.add(recognizableImage);
    splitPane.add(panel);

    selectableImage = new SelectableImage(this.baseImage);
    selectableImage.selectionChangeTrigger.addListener(new Runnable() {
      public void run() {
        newRegion();
      }
    });

    JPanel imageButtons = new JPanel(new LetterLayout("  B").setBorder(10));
    JButton btnWholeImage = new JButton("Whole image");
    btnWholeImage.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent actionEvent) {
        setRegion(new Rectangle(0, 0, image.getWidth(), image.getHeight()), true);
      }
    });
    imageButtons.add("B", btnWholeImage);

    JPanel imageTab = new JPanel(new LetterLayout("I", "I", "B").setSpacing(0));
    imageTab.add("I", GUIUtil.withTitle("Please select a region to recognize or click 'Whole image':", selectableImage));
    imageTab.add("B", imageButtons);

    /*
    thresholdSlider = new JSlider(0, 256, 128);
    bwScrollableImage = new ScrollableImage();
    thresholdUpdated();
    thresholdSlider.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent changeEvent) {
        thresholdUpdated();
      }
    });

    JPanel bwTab = new JPanel(new LetterLayout("S", "I", "I"));
    bwTab.add("S", thresholdSlider);
    bwTab.add("I", bwScrollableImage);
    */

    tabs = new JTabbedPane();
    tabs.add("Base image", imageTab);
    //tabs.add("B/W", bwTab);
    tabs.add("Selected region", splitPane);

    JPanel mainPanel = new JPanel(new LetterLayout("SSB").setBorder(10));
    mainPanel.add("S", tabs);
    mainPanel.add("B", buttons);

    getContentPane().add(mainPanel);

    Runnable selectionChanged = new Runnable() {
      public void run() {
        selectionChanged();
      }
    };

    if (quickSelect)
      recognizableImage.markChangeTrigger.addListener(selectionChanged);
    else
      recognizableImage.selectionChangeTrigger.addListener(selectionChanged);

    /*tfSelectedCharacter.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent actionEvent) {
        correctCharacter();
      }
    });*/
    tfSelectedCharacter.getDocument().addDocumentListener(new DocumentListener() {
      public void insertUpdate(DocumentEvent documentEvent) {
        correctCharacter();
      }

      public void removeUpdate(DocumentEvent documentEvent) {
        correctCharacter();
      }

      public void changedUpdate(DocumentEvent documentEvent) {
        correctCharacter();
      }
    });

    if (recognizer != null)
      loadRecognizer(recognizer);

    setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
    addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent windowEvent) {
        if (recognizerChanged) {
          int option = JOptionPane.showConfirmDialog(InteractiveRecognition.this,
            "Recognizer has unsaved changes. Save them now?", "Recognizer changes", JOptionPane.YES_NO_CANCEL_OPTION);

          if (option == JOptionPane.YES_OPTION) {
            saveRecognizer(false);
            if (recognizerChanged)
              return; // save was cancelled, don't close window
          } else if (option == JOptionPane.CANCEL_OPTION)
            return;
        }
        dispose();
      }
    });
  }

  private void thresholdUpdated() {
    int newThreshold = thresholdSlider.getValue();
    if (newThreshold != threshold) {
      threshold = newThreshold;
      BWImage bwImage = ImageProcessing.threshold(baseImage.toBW(), thresholdSlider.getValue()/256f);
      bwScrollableImage.setImage(bwImage.toRGB());
    }
  }

  private void newRegion() {
    Rectangle r = selectableImage.getSelection();
    if (r == null) return;
    if (r.width < 5 || r.height < 5) return;
    setRegion(r, true);
  }

  private void setRegion(Rectangle r, boolean switchTab) {
    region = ImageProcessing.addBorder(baseImage.clip(r), 1);
    recognizableImage.setImage(region);
    recognizableImage.setZoom(2.0);
    segmenter = null;
    recognize();
    if (switchTab)
      tabs.setSelectedIndex(tabs.getTabCount()-1);
  }

  private void selectionChanged() {
    Subrecognition s = quickSelect ? recognizableImage.getMarkedSubrecognition()
      : recognizableImage.getSelectedSubrecognition();
    correctionEnabled = false;
    if (s == null) {
      tfSelectedCharacter.setText("");
      tfSelectedCharacter.setEnabled(false);
    } else {
      tfSelectedCharacter.setText(s.text == null ? "?" : s.text);
      tfSelectedCharacter.setEnabled(true);
      tfSelectedCharacter.requestFocus();
    }
    tfSelectedCharacter.selectAll();
    correctionEnabled = true;
  }

  private void correctCharacter() {
    if (!correctionEnabled) return; // when text field is set programmatically
    Subrecognition s = quickSelect ? recognizableImage.getMarkedSubrecognition()
      : recognizableImage.getSelectedSubrecognition();
    if (s == null) return;
    String text = tfSelectedCharacter.getText();
    if (text.equals("") || text.equals("?")) return;
    compareImages.addExample(new Example(s.image, text));
    recognizerChanged = true;
    recognize();
  }

  private void recognize() {
    updateCharactersTrained();

    if (region == null) return;

    boolean firstTime = segmenter == null;
    if (firstTime)
      segmenter = makeSegmenter();
    segmenter.setCharRecognizer(compareImages.getDescription());

    segmenter.setCollectDebugInfo(true);
    String text;
    if (firstTime)
      text = segmenter.readImage(region.toBW());
    else
      text = segmenter.rerecognize();
    debugInfo = segmenter.getDebugInfo();
    taRecognizedText.setText(text == null ? "" : text);
    recognizableImage.setDebugInfo(debugInfo);
    recognizableImage.repaint();
    tfSelectedCharacter.setEnabled(false);
  }

  public IRSegmenter makeSegmenter() {
    try {
      return segmenterClass.newInstance();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private void updateCharactersTrained() {
    lblCharactersTrained.setText(String.valueOf(compareImages.numExamples()));
  }

  private void loadRecognizer() {
    try {
      RecognizerListFilter filter = getRecognizerListFilter();
      LoadRecognizerDialog dialog = new LoadRecognizerDialog(this, recognizers, filter);
      dialog.setModal(true);
      dialog.setVisible(true);
      if (dialog.recognizerInfo != null)
        loadRecognizer(dialog.recognizerInfo);
    } catch (Throwable e) {
      Errors.report(e);
    }
  }

  public static RecognizerListFilter getRecognizerListFilter() {
    return new RecognizerListFilter() {
      public boolean accept(RecognizerOnDisk recognizerOnDisk) {
        try {
          return recognizerOnDisk.getRecognizerInfo().getCode().getName().equals(
            OCRUtil.treeHeadForClass(CompareImages.class));
        } catch (Throwable t) {
          Errors.report(t);
          return false;
        }
      }
    };
  }

  private void loadRecognizer(RecognizerInfo recognizerInfo) {
    ImageReader imageReader = recognizerInfo.getImageReader();
    if (!(imageReader instanceof CompareImages)) {
      JOptionPane.showMessageDialog(this, "Sorry: This recognizer is not of the right type");
    } else {
      compareImages = (CompareImages) imageReader;
      recognizerName = recognizerInfo.getName();
      recognize();
    }
  }

  private void saveRecognizer(boolean showMessageBox) {
    try {
      String recognizer = compareImages.getDescription();
      String savedAs = EyeDialogs.saveRecognizer(this, recognizers, recognizer, null,
        RecognizerInputType.character,
        recognizerName, showMessageBox);
      if (savedAs != null) {
        recognizerName = savedAs;
        recognizerChanged = false;
      }
    } catch (Throwable e) {
      Errors.report(e);
    }
  }

  private void debug() {
    try {
      if (debugInfo != null)
        new DebugInfoWindow(debugInfo).setVisible(true);
    } catch (Throwable e) {
      Errors.report(e);
    }
  }
}
