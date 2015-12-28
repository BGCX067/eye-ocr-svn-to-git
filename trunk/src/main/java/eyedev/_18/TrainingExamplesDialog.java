package eyedev._18;

import drjava.util.*;
import eye.eye01.EyeGuiUtil;
import eye.eye01.ScrollableImage;
import eye.eye03.Main;
import eyedev._13.StandardDialog;
import prophecy.common.gui.SexyColumn;
import prophecy.common.gui.SexyTable;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.List;

public class TrainingExamplesDialog extends StandardDialog {
  public SexyTable<TrainingExampleOnDisk> table;
  private TrainingExamples trainingExamples;
  private Main main;
  public ScrollableImage scrollableImage;
  public JButton btnDelete, btnEditText, btnLoadInMain;

  public TrainingExamplesDialog(final TrainingExamples trainingExamples, Main main) {
    this.trainingExamples = trainingExamples;
    this.main = main;
    setTitle("Training examples");
    setSize(700, 500);
    GUIUtil.centerOnScreen(this);

    /*SexyColumn<TrainingExampleOnDisk> colImageName = new SexyColumn<TrainingExampleOnDisk>("Image name") {
      public Object getCell(int row, TrainingExampleOnDisk entry) {
        return entry.getTrainingExample().getImageName();
      }
    };*/

    /*SexyColumn<TrainingExampleOnDisk> colID = new SexyColumn<TrainingExampleOnDisk>("Nr.") {
      public Object getCell(int row, TrainingExampleOnDisk entry) {
        return entry.getID();
      }
    };*/

    SexyColumn<TrainingExampleOnDisk> colOriginalImage = new SexyColumn<TrainingExampleOnDisk>("Taken from image") {
      public Object getCell(int row, TrainingExampleOnDisk entry) {
        return entry.getTrainingExample().getOriginalImage();
      }
    };

    SexyColumn<TrainingExampleOnDisk> colImageSize = new SexyColumn<TrainingExampleOnDisk>("Image size") {
      public Object getCell(int row, TrainingExampleOnDisk entry) {
        Dimension size = entry.getTrainingExample().getImageSize();
        return size.width + " x " + size.height;
      }
    };

    SexyColumn<TrainingExampleOnDisk> colText = new SexyColumn<TrainingExampleOnDisk>("Text") {
      public Object getCell(int row, TrainingExampleOnDisk entry) {
        return entry.getTrainingExample().getText();
      }
    };

    table = new SexyTable<TrainingExampleOnDisk>(colOriginalImage, colText, colImageSize);
    //table.getTableColumn(colID).setMaxWidth(50);
    SwingUtil.setPopupMenu(table, new PopupMenuFactory() {
      public JPopupMenu createPopupMenu() {
        return createTablePopupMenu();
      }
    });

    ScalingSplitPane splitPane = new ScalingSplitPane(JSplitPane.VERTICAL_SPLIT, 0.75f);
    mainPanel.add(splitPane);
    splitPane.setTopComponent(new JScrollPane(table));
    scrollableImage = new ScrollableImage();
    splitPane.setBottomComponent(GUIUtil.withTitle("Image view", scrollableImage));

    btnDelete = new JButton("Delete...");
    btnDelete.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent actionEvent) {
        delete();
      }
    });
    buttons.add(btnDelete);

    btnEditText = new JButton("Edit text...");
    btnEditText.addActionListener(EyeGuiUtil.actionListener(this, "editText"));
    buttons.add(btnEditText);

    btnLoadInMain= new JButton("Load in main window");
    btnLoadInMain.addActionListener(EyeGuiUtil.actionListener(this, "loadInMain"));
    buttons.add(btnLoadInMain);

    table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
      public void valueChanged(ListSelectionEvent e) {
        TrainingExampleOnDisk item = table.getSelectedItem();
        if (item == null)
          scrollableImage.setImage(null);
        else
          scrollableImage.setImage(trainingExamples.loadImage(item.getTrainingExample()));
        updateButtons();
      }
    });

    trainingExamples.trigger.addListener(new Runnable() {
      public void run() {
        scan();
      }
    });

    scan();
  }

  private JPopupMenu createTablePopupMenu() {
    JPopupMenu menu = new JPopupMenu();
    menu.add(EyeGuiUtil.makeMenuItem("Edit text...", this, "editText"));
    menu.add(EyeGuiUtil.makeMenuItem("Load in main window", this, "loadInMain"));
    return menu;
  }

  private void delete() {
    try {
      TrainingExampleOnDisk item = table.getSelectedItem();
      if (item == null) return;
      int confirm = JOptionPane.showConfirmDialog(this,
        "Delete training example?", "Confirm deletion", JOptionPane.OK_CANCEL_OPTION);
      if (confirm == JOptionPane.OK_OPTION) {
        trainingExamples.delete(item);
      }
    } catch (Throwable e) {
      Errors.report(e);
    }
  }

  private void updateButtons() {
    boolean hasSelection = table.getSelectedItem() != null;
    btnDelete.setEnabled(hasSelection);
    btnEditText.setEnabled(hasSelection);
    btnLoadInMain.setEnabled(hasSelection);
  }

  public void scan() {
    try {
      List<TrainingExampleOnDisk> list = this.trainingExamples.scan();
      /*Collections.sort(list, new Comparator<TrainingExampleOnDisk>() {
        public int compare(TrainingExampleOnDisk o1, TrainingExampleOnDisk o2) {
          try {
            return Integer.parseInt(o1.getID()) - Integer.parseInt(o2.getID());
          } catch (NumberFormatException e) {
            return 0;
          }
        }
      });*/
      table.getModel().setList(list);
      updateButtons();
    } catch (Throwable e) {
      Errors.report(e);
    }
  }

  @SuppressWarnings({"UnusedDeclaration"})
  public void editText() {
    try {
      TrainingExampleOnDisk item = table.getSelectedItem();
      if (item == null) return;
      String text = JOptionPane.showInputDialog(this,
        "Enter example text:", item.getTrainingExample().getText());
      if (text != null) {
        item.getTrainingExample().setText(text);
        trainingExamples.save(item);
      }
    } catch (Throwable e) {
      Errors.report(e);
    }
  }

  @SuppressWarnings({"UnusedDeclaration"})
  public void loadInMain() {
    try {
      TrainingExampleOnDisk item = table.getSelectedItem();
      if (item == null) return;
      main.loadImage(trainingExamples.getImageFile(item.getTrainingExample()));
    } catch (Throwable e) {
      Errors.report(e);
    }
  }
}
