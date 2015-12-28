package eye.eye01;

import drjava.util.StringUtil;
import eye.eye03.HtmlWindow;
import eye.eye03.RecognizerInfo;

import static drjava.util.StringUtil.escapeHtml;

public class RecognizerInfoWindow {
  public static void show(RecognizerInfo info) {
    String preferredFont = info.getPreferredFontName() == null ? "not specified" : info.getPreferredFontName();
    String code = info.getCode().toString();
    String text = "Recognizer name: <b>" + escapeHtml(info.getName()) + "</b>\n\n" +
      (info.getComment() == null ? "" : "Description: " + escapeHtml(info.getComment()) + "\n\n") +
      "Input type: " + info.getInputType().name() + "\n" +
      "Preferred font: " + escapeHtml(preferredFont) + "\n\n" +
      "Code (" + code.length() + " chars): " + escapeHtml(EyeGuiUtil.shortenCode(info.getCode().toString(), 500));

    String html = StringUtil.nlToBr(text);
    new HtmlWindow("Recognizer info", html).setVisible(true);
  }
}
