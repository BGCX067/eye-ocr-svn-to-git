package drjava.util;

import java.util.List;

public interface Sender<Data> {
  void addReceiver(Port<Data> receiver);
  void removeReceiver(Port<Data> receiver);
  void removeAllReceivers();
  List<Port<Data>> getReceivers();
  boolean hasReceivers();
}
