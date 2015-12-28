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
import java.util.ArrayList;
import java.util.List;

public class PoppyInteractive2 extends StandardDialog {
  private PoppyLineFinderPrototype poppyLineFinder;
  private SexyTable<Line> lineTable;
  SingleComponentPanel bottomPanel;
  private JTextField tfDefaultCharacterWidth;
  private RGBImage currentClip;
  private RecognizableImage recognizableImage;
  private Line currentLine;
  private float defaultCharacterWidth = 25.0f;
  private int inUpdate;
  private double maxWidthVariation = 0.2;

  public static void main(String[] args) {
    EyeEnv.init();
    new PoppyInteractive2().setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
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

  public PoppyInteractive2() {
    super("Poppy Interactive 2");

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

    //float characterWidth = characterWidthSlider.getValue()/(float) sliderDivider;
    float characterWidth = defaultCharacterWidth;

    RGBImage markedImage = currentClip.copy();

    if (characterWidth >= 1.0) {
      List<Integer> segments = segmentCharacters(currentClip, characterWidth);

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
    recognizableImage.updateImage(markedImage);
  }

  private List<Integer> segmentCharacters(RGBImage image, float defaultCharacterWidth) {
    List<Integer> result = new ArrayList<Integer>();
    int w = image.getWidth(), h = image.getHeight();
    double[] whitenessMap = new double[w];
    for (int x = 0; x < w; x++)
      whitenessMap[x] = columnWhiteness(image, h, x);
    int x = 0;
    result.add(x);
    while (x < w) {
      int x1 = x + (int) (defaultCharacterWidth*(1- maxWidthVariation));
      int x2 = x + (int) (defaultCharacterWidth*(1+ maxWidthVariation))+1;
      int preferredX = x + (int) defaultCharacterWidth;
      if (x1 > w) break;
      if (x2 > w) x2 = w;
      if (x2 <= x1) {
        result.add(x2);
        break;
      }
      x = findBestSplitPoint(whitenessMap, x1, x2, preferredX);
      result.add(x);
    }
    return result;
  }

  private int findBestSplitPoint(double[] whitenessMap, int x1, int x2, int preferredX) {
    double whitenessTolerance = 0.0;

    double maxWhiteness = 0;
    for (int x = x1; x < x2; x++)
      maxWhiteness = Math.max(maxWhiteness, whitenessMap[x]);
    double threshold = maxWhiteness-whitenessTolerance;
    boolean[] candidate = new boolean[x2-x1];
    for (int x = x1; x < x2; x++) candidate[x-x1] = whitenessMap[x] >= threshold;

    int xStart = -1;
    findStart: for (int i = 0; i < Math.max(preferredX-x1, x2-preferredX); i++)
      for (int lr = 0; lr < 2; lr++) {
        int x = lr == 0 ? preferredX+i : preferredX-i;
        if (x < x1 || x >= x2) continue;
        if (candidate[x-x1]) {
          xStart = x;
          break findStart;
        }
      }
    if (xStart == -1) return preferredX;

    int xleft = xStart, xright = xStart;
    while (xleft > x1 && candidate[xleft-1-x1]) --xleft;
    while (xright < x2-1 && candidate[xright+1-x1]) ++xright;
    //System.out.println("xleft=" + xleft + ", xright=" + xright);
    return (xright+xleft)/2;

    /*for (int i = 0; i < Math.max(preferredX-x1, x2-preferredX); i++) {
      int x = preferredX+i;
      if (x >= x1 && x < x2 && whitenessMap[x] >= threshold)
        return x;
      x = preferredX-i;
      if (x >= x1 && x < x2 && whitenessMap[x] >= threshold)
        return x;
    }

    return preferredX;*/
  }

  private RGBImage addWhitenessBar(RGBImage markedImage) {
    int barHeight = 4;
    int w = markedImage.getWidth(), h = markedImage.getHeight();
    RGBImage newImage = new RGBImage(w, h+barHeight, new RGB(Color.white));
    ImageProcessing.copy(markedImage, 0, 0, newImage, 0, 0, w, h);
    for (int x = 0; x < w; x++) {
      double whiteness = columnWhiteness(markedImage, h, x);
      ImageProcessing.drawRect(newImage, x, h, 1, barHeight, new RGB(whiteness));
    }
    return newImage;
  }

  private double columnWhiteness(RGBImage markedImage, int h, int x) {
    int count = OCRImageUtil.numPixelsBrighterThan(markedImage.clip(new Rectangle(x, 0, 1, h)).toBW(), 0.1f);
    return count/(double) h;
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
}
