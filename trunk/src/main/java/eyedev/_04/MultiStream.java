package eyedev._04;

import eyedev._01.ImageReaderStream;
import eyedev._02.Streamable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MultiStream implements Streamable {
  List<Streamable> substreams = new ArrayList<Streamable>();

  public ImageReaderStream stream() {
    return new ImageReaderStream() {
      Iterator<Streamable> it = substreams.iterator();
      ImageReaderStream currentStream;

      {
        nextStream();
      }

      private void nextStream() {
        if (!it.hasNext())
          currentStream = null;
        else
          currentStream = it.next().stream();
      }

      public String getNextImageReaderDescription() {
        while (true) {
          if (currentStream == null)
            return null;
          String desc = currentStream.getNextImageReaderDescription();
          if (desc != null)
            return desc;
          nextStream();
        }
      }
    };
  }

  public void add(Streamable streamable) {
    substreams.add(streamable);
  }
}
