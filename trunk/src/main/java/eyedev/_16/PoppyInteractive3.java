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

public class PoppyInteractive3 extends StandardDialog {
  private PoppyLineFinderPrototype poppyLineFinder;
  private SexyTable<Line> lineTable;
  SingleComponentPanel bottomPanel;
  private JTextField tfDefaultCharacterWidth;
  private RGBImage currentClip;
  private RecognizableImage recognizableImage;
  private Line currentLine;
  private float defaultCharacterWidth = 25.0f;
  private int inUpdate;

  public static void main(String[] args) {
    EyeEnv.init();
    new PoppyInteractive3().setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
  }

  public float parseFloat(String text) {
    try {
      return Float.parseFloat(text.trim());
    } catch (NumberFormatException e) {
      return 0.0f;
    }
  }

  class Line {
    int lineNr;
    TileCluster cluster;

    Line(int lineNr, TileCluster cluster) {
      this.lineNr = lineNr;
      this.cluster = cluster;
    }

    public RGBImage getClip() {
      return poppyLineFinder.getImage().clip(cluster.getBoundingRect());
    }
  }

  public PoppyInteractive3() {
    super("Poppy Interactive 3");

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

    /*SexyColumn<Line> colCharacterWidth = new SexyColumn<Line>("Character width") {
      public Object getCell(int row, Line line) {
        return line.characterWidth == 0f ? defaultCharacterWidth + " (default)"
          : String.valueOf(line.characterWidth);
      }
    };*/

    SexyColumn<Line> colText = new SexyColumn<Line>("Text") {
      public Object getCell(int row, Line entry) {
        return "";
      }
    };

    lineTable = new SexyTable<Line>(colLineNr, colImage, colText);

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
    tfDefaultCharacterWidth.getDocument().addDocumentListener(documentListener);

    mainPanel.setLayout(new LetterLayout("S", "S", "D"));
    mainPanel.add("S", splitPane);
    mainPanel.add("D", GUIUtil.withLabel("Default character width (pixels):", tfDefaultCharacterWidth));

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
    } else {
      currentClip = line.getClip();
      RGBImage clip = currentClip;
      clip = ImageProcessing.addBorder(clip, 10);
      recognizableImage = new RecognizableImage(clip);
      paintCharacterBoxes();
      bottomPanel.setComponent(recognizableImage);
    }
    --inUpdate;
  }

  private void paintCharacterBoxes() {
    if (recognizableImage == null || currentLine == null) return;

    RGBImage markedImage = currentClip.copy();

    if (defaultCharacterWidth >= 1.0) {
      PoppySegmenter segmenter = new PoppySegmenter();
      segmenter.defaultCharacterWidth = defaultCharacterWidth;
      List<Integer> segments = segmenter.segmentCharacters(currentClip.toBW());

      boolean col = false;
      for (int i = 1; i < segments.size(); i++) {
        int x1 = segments.get(i-1);
        int x2 = segments.get(i);
        OCRImageUtil.fillBackground(markedImage, x1, 0, x2-x1, markedImage.getHeight(),
          new RGB(col ? 0.9 : 0.6));
        col = !col;
      }
    }

    //markedImage = addWhitenessBar(markedImage);
    markedImage = addWhitenessColumn(markedImage);
    recognizableImage.updateImage(markedImage);
  }

  private void updateWidths() {
    if (inUpdate != 0) return;
    ++inUpdate;
    defaultCharacterWidth = parseFloat(tfDefaultCharacterWidth.getText());
    if (currentLine != null) {
      paintCharacterBoxes();
    }
    lineTable.getModel().fireTableDataChanged();
    --inUpdate;
  }

  private RGBImage addWhitenessColumn(RGBImage markedImage) {
    int barWidth = 20;
    int w = markedImage.getWidth(), h = markedImage.getHeight();
    RGBImage newImage = new RGBImage(w+barWidth, h, new RGB(Color.white));
    ImageProcessing.copy(markedImage, 0, 0, newImage, barWidth, 0, w, h);
    for (int y = 0; y < h; y++) {
      double whiteness = lineWhiteness(markedImage, w, y);
      ImageProcessing.drawRect(newImage, 0, y, barWidth, 1, new RGB(whiteness));
    }
    return newImage;
  }

  private double lineWhiteness(RGBImage image, int w, int y) {
    int count = OCRImageUtil.numPixelsBrighterThan(image.clip(new Rectangle(0, y, w, 1)).toBW(), 0.1f);
    return count/(double) w;
  }

}
