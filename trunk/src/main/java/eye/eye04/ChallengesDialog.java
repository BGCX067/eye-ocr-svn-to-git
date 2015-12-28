package eye.eye04;

import drjava.util.*;
import eye.eye01.TestProtocolWindow;
import eye.eye02.EyeDialogs;
import eye.eye02.FontEntry;
import eye.eye02.FontFinder;
import eye.eye03.RecognizerInfo;
import eye.eye03.RecognizerInputType;
import eye.eye03.Recognizers;
import eyedev._07.RecognitionTest;
import eyedev._07.TestProtocol;
import eyedev._07.TestRunner;
import prophecy.common.Var;
import prophecy.common.gui.SexyColumn;
import prophecy.common.gui.SexyTable;
import prophecy.common.gui.SexyTableModel;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ChallengesDialog extends JFrame {
  private SexyTableModel<Challenge> tableModel;
  private JButton btnSolve;
  private JTextField tfStatus;
  private JProgressBar progressBar;
  private Var<String> status = new Var<String>("");
  private JButton btnProtocol;
  private SexyTable<Challenge> sexyTable;
  private Recognizers recognizers;
  private JButton btnSaveRecognizer;

  public ChallengesDialog(List<Challenge> challenges, Recognizers recognizers) {
    this.recognizers = recognizers;
    setTitle("Challenges");
    setSize(800, 500);
    GUIUtil.centerOnScreen(this);

    JPanel buttons = new JPanel(LetterLayout.stalactite());

    SexyColumn<Challenge> colName = new SexyColumn<Challenge>("Challenge") {
      public Object getCell(int row, Challenge entry) {
        return entry.name;
      }
    };

    SexyColumn<Challenge> colCharSet = new SexyColumn<Challenge>("Characters") {
      public Object getCell(int row, Challenge entry) {
        return entry.charset;
      }
    };

    SexyColumn<Challenge> colFont = new SexyColumn<Challenge>("Font") {
      public Object getCell(int row, Challenge entry) {
        return entry.fontName;
      }
    };

    SexyColumn<Challenge> colSolved = new SexyColumn<Challenge>("Solved") {
      public Object getCell(int row, Challenge entry) {
        return StringUtil.formatDouble(entry.getPercentSolved(), 1) + "%";
      }
    };

    sexyTable = new SexyTable<Challenge>(colName, colCharSet, colFont, colSolved);
    tableModel = sexyTable.getModel();

    for (Challenge challenge : challenges)
      tableModel.addItem(challenge);

    sexyTable.getTableColumn(colCharSet).setMaxWidth(150);
    sexyTable.getTableColumn(colSolved).setMaxWidth(100);

    sexyTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
      public void valueChanged(ListSelectionEvent listSelectionEvent) {
        btnProtocol.setEnabled(showProtocol(false));
        btnSaveRecognizer.setEnabled(saveRecognizer(false));
      }
    });

    btnSolve = new JButton("Solve");
    btnSolve.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent actionEvent) {
        solveInBackground();
      }
    });
    buttons.add(btnSolve);

    buttons.add(new JPanel());

    btnProtocol = new JButton("Protocol");
    btnProtocol.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent actionEvent) {
        showProtocol(true);
      }
    });
    btnProtocol.setEnabled(false);
    buttons.add(btnProtocol);

    btnSaveRecognizer = new JButton("Save recognizer...");
    btnSaveRecognizer.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent actionEvent) {
        saveRecognizer(true);
      }
    });
    btnSaveRecognizer.setEnabled(false);
    buttons.add(btnSaveRecognizer);

    progressBar = new JProgressBar(0, 1000);
    tfStatus = new JTextField();

    JPanel statusPanel = new JPanel(new LetterLayout("P", "S"));
    statusPanel.add("P", progressBar);
    statusPanel.add("S", tfStatus);

    JPanel panel = new JPanel(new LetterLayout("II ", "SSB", "TTB", "TTB").setBorder(10));
    panel.add("I", new JLabel("Recognition challenges for: All Eye fonts, 26 chars (A-Z), size 30"));
    panel.add("B", buttons);
    panel.add("T", new JScrollPane(sexyTable));
    panel.add("S", statusPanel);

    new SwingTimerHelper(new Runnable() {
      public void run() {
        String s = status.get();
        if (!s.equals(tfStatus.getText()))
          tfStatus.setText(s);
      }
    }, 100).installOn(statusPanel);

    getContentPane().add(panel);
  }

  private boolean showProtocol(boolean doIt) {
    int row = sexyTable.getSelectedRow();
    if (row < 0) return false;
    Challenge challenge = tableModel.getItem(row);
    RecognitionTest test = challenge.getTest();
    if (test == null) return false;
    String solution = challenge.getSolution();
    if (solution == null) return false;

    if (doIt) {
      final TestRunner testRunner = new TestRunner(test, solution);
      testRunner.run();
      TestProtocol protocol = testRunner.getProtocol();
      TestProtocolWindow.show(protocol, solution);
    }

    return true;
  }

  private boolean saveRecognizer(boolean doIt) {
    int row = sexyTable.getSelectedRow();
    if (row < 0) return false;
    Challenge challenge = tableModel.getItem(row);
    String solution = challenge.getSolution();
    if (solution == null) return false;

    if (doIt) {
      EyeDialogs.saveRecognizer(this, recognizers, solution, challenge.fontName,
        RecognizerInputType.unknown, null, true);
    }

    return true;
  }

  static class ProgressItem {
    Challenge updatedChallenge;

    ProgressItem(Challenge updatedChallenge) {
      this.updatedChallenge = updatedChallenge;
    }
  }

  private void solveInBackground() {
    btnSolve.setEnabled(false);
    btnSolve.setText("Solving...");

    SwingWorker<String, ProgressItem> worker = new SwingWorker<String, ProgressItem>() {
      protected String doInBackground() throws Exception {
        long startTime = System.currentTimeMillis();
        try {
          Solver.solveIt(new SolvingEnv() {
            public void challengeUpdated(Challenge challenge) {
              publish(new ProgressItem(challenge));
            }

            public void setStatus(String s) {
              status.set(s);
            }

            public List<Challenge> getChallenges() {
              return tableModel.getList();
            }

            public void trySolution(Challenge challenge, String solution) {
              if (challenge.trySolution(solution))
                challengeUpdated(challenge);
            }
          });
          long endTime = System.currentTimeMillis();
          status.set("Done after " + StringUtil.formatDouble((endTime-startTime)/1000.0, 1) + " seconds");
        } catch (Throwable e) {
          Errors.report(e);
        }
        return "";
      }

      protected void process(List<ProgressItem> chunks) {
        //System.out.println("process " + chunks.size() + " progress items");
        tableModel.fireTableDataChanged();
        updateProgressBar(false);
      }

      protected void done() {
        updateProgressBar(true);
        btnSolve.setEnabled(true);
        btnSolve.setText("Solve");
      }
    };
    worker.execute();
  }

  private void updateProgressBar(boolean done) {
    int n = 0;
    double sum = 0;
    for (Challenge challenge : tableModel.getList()) {
      sum += challenge.getPercentSolved()/100.0;
      ++n;
    }
    if (n == 0) return;
    double overall = sum/n;
    progressBar.setValue((int) (overall*progressBar.getMaximum()));
    String text = "Challenges " + StringUtil.formatDouble(overall * 100, 1) + " % solved";
    if (done) text = "Done! " + text;
    progressBar.setString(text);
    progressBar.setStringPainted(true);
  }

  public static void main(String[] args) throws IOException {
    Errors.setPopup(true);

    //List<Challenge> challenges = standardChallenges();
    List<Challenge> challenges = arialAllSizes();

    GUIUtil.showMainFrame(new ChallengesDialog(challenges, new Recognizers()));
  }

  private static List<Challenge> arialAllSizes() throws IOException {
    FontEntry fontEntry = FontFinder.getEyeFont("Arial");
    List<Float> sizes = Lizt.of(15f, 20f, 25f, 30f, 35f, 40f, 45f, 50f);

    List<Challenge> challenges = new ArrayList<Challenge>();
    challenges.add(new IndividualLettersChallenge(fontEntry, sizes));
    challenges.add(new RandomWordsChallenge(fontEntry, 20, 4, sizes));
    return challenges;
  }

  public static List<Challenge> standardChallenges() {
    List<Challenge> challenges = new ArrayList<Challenge>();

    List<FontEntry> allFonts = FontFinder.allEyeFonts();
    List<FontEntry> fonts = new ArrayList<FontEntry>();

    /*for (FontEntry font : allFonts) {
      if (font.getName().equalsIgnoreCase("Falcon"))
        fonts.add(font);
    }*/
    fonts.addAll(allFonts);

    //if (fonts.size() > 10) fonts = fonts.subList(0, 10);
    for (FontEntry fontEntry : fonts) {
      challenges.add(new IndividualLettersChallenge(fontEntry, Lizt.of(30f)));
      challenges.add(new RandomWordsChallenge(fontEntry, 20, 4, Lizt.of(30f)));
    }
    return challenges;
  }
}
