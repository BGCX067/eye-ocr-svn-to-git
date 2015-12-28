package eye.eye03;

import drjava.util.Errors;
import eye.eye04.EyeStandardCharacterRecognizers;
import eye.eye04.EyeStandardTextRecognizers;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

public class RecognizerSelector extends JComboBox {
  Recognizers recognizers;
  RecognizerListFilter filter;
  private DefaultComboBoxModel model;

  public RecognizerSelector(Recognizers recognizers) {
    this.recognizers = recognizers;
    updateList();
  }

  public RecognizerSelector(Recognizers recognizers, RecognizerListFilter filter) {
    this.recognizers = recognizers;
    this.filter = filter;
    updateList();
  }

  public void addTriggerListener(Window window) {
    final Runnable innerListener = new Runnable() {
      public void run() {
        updateList();
      }
    };
    final Runnable triggerListener = new Runnable() {
      public void run() {
        SwingUtilities.invokeLater(innerListener);
      }
    };
    recognizers.trigger.addListener(triggerListener);
    window.addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent e) {
        recognizers.trigger.removeListener(triggerListener);
      }
    });
  }

  private void updateList() {
    java.util.List<RecognizerOnDisk> recognizers;
    try {
      recognizers = new ArrayList<RecognizerOnDisk>();
      for (RecognizerOnDisk recognizer : this.recognizers.scan())
        if (filter == null || filter.accept(recognizer))
          recognizers.add(recognizer);
    } catch (Throwable e) {
      Errors.report(e);
      return;
    }

    model = new DefaultComboBoxModel();

    if (filter == null) {
      addStandardRecognizers();
    }

    for (RecognizerOnDisk recognizer : recognizers) try {
      addRecognizer(recognizer.getRecognizerInfo());
    } catch (Throwable t) {
      Errors.report(t);
    }
    setModel(model);
  }

  private void addStandardRecognizers() {
    EyeStandardTextRecognizers.getStandardList();
    /*ImageReader recognizer = new RecognizerWithAdvancedSegmenter(Recognizer06.getRecognizerDesc());
    return recognizer.toTree();*/
    //return Tree.parse(EyeStandardLineRecognizer.classicRecognizer());
    addRecognizer(EyeStandardCharacterRecognizers.getAlpha7());
    addRecognizer(EyeStandardTextRecognizers.getTypewriterAlpha8());
    //addRecognizer(new RecognizerInfo(OCRUtil.treeFor(ScreenshotRecognizer.class), "Alpha 9 screenshot recognizer"));
    addRecognizer(EyeStandardTextRecognizers.getScreenshotAlpha9());
    //addRecognizer(EyeStandardCharacterRecognizers.getHungarian());
  }

  private void addRecognizer(RecognizerInfo recognizerInfo) {
    model.addElement(new Entry(recognizerInfo));
  }

  public RecognizerInfo getSelectedRecognizer() {
    if (!(getSelectedItem() instanceof Entry)) return null;
    Entry entry = (Entry) getSelectedItem();
    return entry.recognizerInfo;
  }

  public void addNullEntry(String text) {
    model.insertElementAt(text, 0);
    if (model.getSize() == 1)
      setSelectedIndex(0); // select null entry if no other recognizers in list
  }

  public void selectRecognizer(String recognizerName) {
    for (int i = 0; i < model.getSize(); i++)
      if (model.getElementAt(i) instanceof Entry) {
        Entry entry = (Entry) model.getElementAt(i);
        if (entry.recognizerInfo.getName().equals(recognizerName)) {
          setSelectedIndex(i);
          return;
        }
      }
  }

  private class Entry {
    private RecognizerInfo recognizerInfo;

    public Entry(RecognizerInfo recognizerInfo) {
      this.recognizerInfo = recognizerInfo;
    }

    @Override
    public String toString() {
      return recognizerInfo.getName();
    }
  }
}
