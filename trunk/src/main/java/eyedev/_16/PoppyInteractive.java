package eyedev._16;

import drjava.util.GUIUtil;
import drjava.util.LetterLayout;
import drjava.util.ScalingSplitPane;
import eye.eye03.EyeEnv;
import eye.eye05.RecognizableImage;
import eyedev._01.OCRImageUtil;
import eyedev._12.TileCluster;
import eyedev._13.StandardDialog;
import prophecy.common.gui.SexyColumn;
import prophecy.common.gui.SexyTable;
import prophecy.common.gui.SingleComponentPanel;
import prophecy.common.image.ImageProcessing;
import prophecy.common.image.ImageSurface;
import prophecy.common.image.RGB;
import prophecy.common.image.RGBImage;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.util.List;

public class PoppyInteractive extends StandardDialog {
  private PoppyLineFinderPrototype poppyLineFinder;
  private SexyTable<Line> lineTable;
  SingleComponentPanel bottomPanel;
  /*private JSlider characterWidthSlider;
  int sliderDivider = 5;*/
  private JTextField tfCharacterWidth, tfDefaultCharacterWidth;
  private RGBImage currentClip;
  private RecognizableImage recognizableImage;
  private Line currentLine;
  private float defaultCharacterWidth = 25.0f;
  private int inUpdate;

  public static void main(String[] args) {
    EyeEnv.init();
    new PoppyInteractive().setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
  }

  public float parseFloat(String text) {
    try {
      return Float.parseFloat(text.trim());
    } catch (NumberFormatException e) {
      return 0.0f;
    }
  }

  public float getCharacterWidth() {
    return parseFloat(tfCharacterWidth.getText());
  }

  class Line {
    int lineNr;
    TileCluster cluster;
    float characterWidth;

    Line(int lineNr, TileCluster cluster) {
      this.lineNr = lineNr;
      this.cluster = cluster;
    }

    public RGBImage getClip() {
      return poppyLineFinder.getImage().clip(cluster.getBoundingRect());
    }
  }

  public PoppyInteractive() {
    super("Poppy Interactive");

    poppyLineFinder = new PoppyLineFinderPrototype();
    poppyLineFinder.loadImageAndProcess();

    SexyColumn<Line> colLineNr = new SexyColumn<Line>("Line") {
      public Object getCell(int row, Line entry) {
        return "Line " + entry.lineNr;
      }
    };

    SexyColumn<Line> colImage = new SexyColumn<Line>("Image preview") {
      public Object getCell(int row, Line line) {
        return line;
      }
    };

    SexyColumn<Line> colCharacterWidth = new SexyColumn<Line>("Character width") {
      public Object getCell(int row, Line line) {
        return line.characterWidth == 0f ? defaultCharacterWidth + " (default)"
          : String.valueOf(line.characterWidth);
      }
    };

    SexyColumn<Line> colText = new SexyColumn<Line>("Text") {
      public Object getCell(int row, Line entry) {
        return "";
      }
    };

    lineTable = new SexyTable<Line>(colLineNr, colImage, colCharacterWidth, colText);

    List<TileCluster> clusters = poppyLineFinder.getClusters();
    for (int i = 0; i < clusters.size(); i++)
      lineTable.getModel().addItem(new Line(i+1, clusters.get(i)));

    JSplitPane splitPane = new ScalingSplitPane(ScalingSplitPane.VERTICAL_SPLIT, 0.4f);
    splitPane.setTopComponent(new JScrollPane(lineTable));
    bottomPanel = new SingleComponentPanel();
    splitPane.setBottomComponent(/*GUIUtil.withInset(*/bottomPanel/*, 10)*/);

    lineTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
      public void valueChanged(ListSelectionEvent e) {
        selectionChanged();
      }
    });

    lineTable.getTableColumn(colImage).setCellRenderer(new TableCellRenderer() {
      public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        ImageSurface imageSurface = new ImageSurface(((Line) value).getClip(), 0.5);
        JPanel panel = new JPanel(new LetterLayout("X").setBorder(2));
        panel.add("X", imageSurface);
        panel.setBackground(Color.white);
        return panel;
        //return GUIUtil.withInset(imageSurface, 2);
      }
    });
    lineTable.setRowHeight(25);

    /*characterWidthSlider = new JSlider(JSlider.HORIZONTAL, 1, 500, sliderDivider*20);
    characterWidthSlider.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
        paintCharacterBoxes();
      }
    });*/
    tfCharacterWidth = new JTextField();
    tfDefaultCharacterWidth = new JTextField(String.valueOf(defaultCharacterWidth));
    DocumentListener documentListener = new DocumentListener() {
      public void insertUpdate(DocumentEvent e) {
        updateWidths();
      }

      public void removeUpdate(DocumentEvent e) {
        updateWidths();
      }

      public void changedUpdate(DocumentEvent e) {
        updateWidths();
      }
    };
    tfCharacterWidth.getDocument().addDocumentListener(documentListener);
    tfDefaultCharacterWidth.getDocument().addDocumentListener(documentListener);

    mainPanel.setLayout(new LetterLayout("SSSS", "SSSS", "DDCC"));
    mainPanel.add("S", splitPane);
    mainPanel.add("D", GUIUtil.withLabel("Default character width (pixels):", tfDefaultCharacterWidth));
    mainPanel.add("C", GUIUtil.withLabel("Character width for line (pixels):", tfCharacterWidth));

    setSize(800, 600);
    centerAndShow();
  }

  private void selectionChanged() {
    if (inUpdate != 0) return;
    ++inUpdate;
    Line line = lineTable.getSelectedItem();
    currentLine = line;
    if (line == null) {
      bottomPanel.setComponent(new JPanel());
      tfCharacterWidth.setText("");
    } else {
      currentClip = line.getClip();
      RGBImage clip = currentClip;
      clip = ImageProcessing.addBorder(clip, 10);
      recognizableImage = new RecognizableImage(clip);
      paintCharacterBoxes();
      bottomPanel.setComponent(recognizableImage);
      tfCharacterWidth.setText(String.valueOf(line.characterWidth));
    }
    --inUpdate;
  }

  private void paintCharacterBoxes() {
    if (recognizableImage == null || currentLine == null) return;

    //float characterWidth = characterWidthSlider.getValue()/(float) sliderDivider;
    float characterWidth = currentLine.characterWidth != 0f ? currentLine.characterWidth : defaultCharacterWidth;

    RGBImage markedImage = currentClip.copy();
    boolean col = false;
    if (characterWidth >= 1.0)
      for (float x = 0; x < markedImage.getWidth(); x += characterWidth) {
        OCRImageUtil.fillBackground(markedImage, (int) x, 0, (int) (characterWidth+1), markedImage.getHeight(),
          new RGB(col ? 0.9 : 0.6));
        col = !col;
      }

    markedImage = addWhitenessBar(markedImage);
    recognizableImage.updateImage(markedImage);
  }

  private RGBImage addWhitenessBar(RGBImage markedImage) {
    int barHeight = 4;
    int w = markedImage.getWidth(), h = markedImage.getHeight();
    RGBImage newImage = new RGBImage(w, h+barHeight, new RGB(Color.white));
    ImageProcessing.copy(markedImage, 0, 0, newImage, 0, 0, w, h);
    for (int x = 0; x < w; x++) {
      int count = OCRImageUtil.numPixelsBrighterThan(markedImage.clip(new Rectangle(x, 0, 1, h)).toBW(), 0.1f);
      double whiteness = count/(double) h;
      ImageProcessing.drawRect(newImage, x, h, 1, barHeight, new RGB(whiteness));
    }
    return newImage;
  }

  private void updateWidths() {
    if (inUpdate != 0) return;
    ++inUpdate;
    defaultCharacterWidth = parseFloat(tfDefaultCharacterWidth.getText());
    if (currentLine != null) {
      currentLine.characterWidth = getCharacterWidth();
      paintCharacterBoxes();
    }
    lineTable.getModel().fireTableDataChanged();
    --inUpdate;
  }
}
