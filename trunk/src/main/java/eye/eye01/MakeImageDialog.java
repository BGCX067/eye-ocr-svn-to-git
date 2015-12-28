package eye.eye01;

import drjava.util.Errors;
import drjava.util.GUIUtil;
import drjava.util.LetterLayout;
import drjava.util.Line;
import eye.eye02.FontSelector;
import prophecy.common.ClassData;
import prophecy.common.gui.CancelButton;
import prophecy.common.gui.Sheet;
import prophecy.common.image.BWImage;
import prophecy.common.image.RGBImage;

import javax.swing.*;
import java.awt.*;

public class MakeImageDialog extends JDialog {
  private JTextArea textArea;
  private ScrollableImage scrollableImage;
  public RGBImage image;
  private FontSelector fontSelector;
  private JSpinner fontSizeSpinner;
  private ClassData classData;

  public MakeImageDialog(JFrame owner) {
    this(owner, null);
  }
  
  public MakeImageDialog(JFrame owner, String preferredFontName) {
    super(owner);
    setTitle("Make image");
    setSize(400, 400);
    GUIUtil.centerOnScreen(this);

    classData = ClassData.get(this);

    JPanel buttons = new JPanel(LetterLayout.stalactite());

    JButton btnOK = new JButton("OK");
    btnOK.addActionListener(EyeGuiUtil.actionListener(this, "ok"));
    buttons.add(btnOK);

    JButton btnPreview = new JButton("Preview");
    btnPreview.addActionListener(EyeGuiUtil.actionListener(this, "preview"));
    buttons.add(btnPreview);
    
    buttons.add(new CancelButton());

    Sheet sheet = new Sheet();
    sheet.getSheetLayout().setBorder(0);
    textArea = new JTextArea(classData.getString("text", "WHAT IS BUDDHA"));
    sheet.addLeftAlignedLabel("Text to render:");
    JScrollPane scrollPane = new JScrollPane(textArea);
    scrollPane.setMinimumSize(new Dimension(200, 75));
    sheet.addComponent(scrollPane);

    fontSelector = new FontSelector();
    fontSelector.selectFont(preferredFontName != null ? preferredFontName : classData.getString("fontName", null));
    sheet.addComponent(GUIUtil.withLabel("Font:", fontSelector));

    int fontSize = classData.getInt("fontSize", 20);
    fontSizeSpinner = new JSpinner(new SpinnerNumberModel(fontSize, 5, 100, 5));
    sheet.addComponent(new Line(new JLabel("Font size:"), fontSizeSpinner));

    sheet.addSpacer();
    sheet.addLeftAlignedLabel("Image preview:");

    scrollableImage = new ScrollableImage();

    getContentPane().setLayout(new LetterLayout("SSB", "IIB", "IIB").setBorder(10));
    getContentPane().add("B", buttons);
    getContentPane().add("S", sheet.getPanel());
    getContentPane().add("I", scrollableImage);
  }

  @SuppressWarnings({"UnusedDeclaration"})
  public void preview() {
    int fontSize = getFontSize();
    Font font;
    try {
      font = fontSelector.getSelectedFont();
    } catch (Exception e) {
      Errors.report(e);
      return;
    }
    font = font.deriveFont((float) fontSize);
    int inset = 5;
    BWImage image = new TextPainter2(font).makeImage(textArea.getText(), inset);
    scrollableImage.setImage(image.toRGB());
  }

  private int getFontSize() {
    return ((Number) fontSizeSpinner.getValue()).intValue();
  }

  @SuppressWarnings({"UnusedDeclaration"})
  public void ok() {
    preview();
    image = scrollableImage.getImage();
    dispose();
  }

  @Override
  public void dispose() {
    classData.setString("text", textArea.getText());
    classData.setString("fontName", fontSelector.getSelectedFontName());
    classData.setInt("fontSize", getFontSize());
    classData.save();
    super.dispose();
  }
}
