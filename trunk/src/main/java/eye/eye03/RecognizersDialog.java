package eye.eye03;

import drjava.util.Errors;
import drjava.util.GUIUtil;
import drjava.util.LetterLayout;
import eye.eye01.RecognizerInfoWindow;
import eye.eye02.LearnAFontDialog;
import eye.eye02.RecognitionDialog;
import eye.eye05.CreateRecognizerDialog;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

public class RecognizersDialog extends JFrame {
  private Recognizers recognizers;
  private JList list;

  public RecognizersDialog(final Recognizers recognizers) {
    setTitle("Manage Recognizers");
    setSize(600, 500);
    GUIUtil.centerOnScreen(this);

    this.recognizers = recognizers;

    list = new JList();

    JPanel buttons = new JPanel(LetterLayout.stalactite());

    final JButton btnUse = new JButton("Recognize text...");
    btnUse.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent actionEvent) {
        use();
      }
    });
    btnUse.setEnabled(false);
    buttons.add(btnUse);

    buttons.add(new JPanel());

    final JButton btnInfo = new JButton("Info");
    btnInfo.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent actionEvent) {
        info();
      }
    });
    btnInfo.setEnabled(false);
    buttons.add(btnInfo);

    final JButton btnRename = new JButton("Rename...");
    btnRename.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent actionEvent) {
        rename();
      }
    });
    btnRename.setEnabled(false);
    buttons.add(btnRename);

    final JButton btnDelete = new JButton("Delete...");
    btnDelete.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent actionEvent) {
        delete();
      }
    });
    btnDelete.setEnabled(false);
    buttons.add(btnDelete);

    buttons.add(new JPanel());

    JButton btnCreate = new JButton("Create manually...");
    btnCreate.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent actionEvent) {
        create();
      }
    });
    buttons.add(btnCreate);

    JButton btnLearnAFont = new JButton("Learn a font...");
    btnLearnAFont.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent actionEvent) {
        learnAFont();
      }
    });
    buttons.add(btnLearnAFont);

    JPanel panel = new JPanel(new LetterLayout("TTT", "LLB", "LLB").setBorder(10));

    panel.add("T", new JLabel("Recognizers:"));
    panel.add("B", buttons);
    panel.add("L", new JScrollPane(list));

    getContentPane().add(panel);

    scan();

    list.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
      public void valueChanged(ListSelectionEvent listSelectionEvent) {
        boolean b = list.getSelectedValue() != null;
        btnUse.setEnabled(b);
        btnInfo.setEnabled(b);
        btnRename.setEnabled(b);
        btnDelete.setEnabled(b);
      }
    });

    addTriggerListener();
  }

  private void rename() {
    try {
      Entry info = (Entry) list.getSelectedValue();
      if (info == null) return;
      String newName = JOptionPane.showInputDialog(this,
        "Please enter new name for recognizer", info.recognizerOnDisk.name);
      if (newName != null) {
        recognizers.rename(info.recognizerOnDisk, newName);
      }
    } catch (Throwable e) {
      Errors.report(e);
    }
  }

  private void delete() {
    try {
      Entry info = (Entry) list.getSelectedValue();
      if (info == null) return;
      int confirm = JOptionPane.showConfirmDialog(this,
        "Delete \"" + info.recognizerOnDisk.name + "\"?", "Confirm deletion", JOptionPane.OK_CANCEL_OPTION);
      if (confirm == JOptionPane.OK_OPTION) {
        recognizers.delete(info.recognizerOnDisk);
      }
    } catch (Throwable e) {
      Errors.report(e);
    }
  }

  private void use() {
    try {
      Entry entry = (Entry) list.getSelectedValue();
      if (entry == null) return;
      RecognizerInfo info = entry.recognizerOnDisk.getRecognizerInfo();
      new RecognitionDialog(info.getCode().toString(), info.getName(), info.getPreferredFontName()).setVisible(true);
    } catch (Throwable e) {
      Errors.report(e);
    }
  }

  private void info() {
    try {
      Entry entry = (Entry) list.getSelectedValue();
      if (entry == null) return;
      RecognizerInfo info = entry.recognizerOnDisk.getRecognizerInfo();

      RecognizerInfoWindow.show(info);
    } catch (Throwable e) {
      Errors.report(e);
    }
  }

  private void addTriggerListener() {
    final Runnable innerListener = new Runnable() {
      public void run() {
        scan();
      }
    };
    final Runnable triggerListener = new Runnable() {
      public void run() {
        SwingUtilities.invokeLater(innerListener);
      }
    };
    recognizers.trigger.addListener(triggerListener);
    addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent e) {
        recognizers.trigger.removeListener(triggerListener);
      }
    });
  }

  private void scan() {
    List<RecognizerOnDisk> recognizers;
    try {
      recognizers = this.recognizers.scan();
    } catch (Throwable e) {
      Errors.report(e);
      return;
    }

    DefaultListModel listModel = new DefaultListModel();
    for (RecognizerOnDisk recognizer : recognizers)
      listModel.addElement(new Entry(recognizer));
    list.setModel(listModel);
  }

  private void learnAFont() {
    new LearnAFontDialog(recognizers).setVisible(true);
  }

  private void create() {
    new CreateRecognizerDialog(recognizers).setVisible(true);
  }

  private class Entry {
    private RecognizerOnDisk recognizerOnDisk;

    public Entry(RecognizerOnDisk recognizerOnDisk) {
      this.recognizerOnDisk = recognizerOnDisk;
    }

    @Override
    public String toString() {
      return recognizerOnDisk.name;
    }
  }
}
