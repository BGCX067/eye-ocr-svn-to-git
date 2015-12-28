package prophecy.common;

public interface RunnableListenerList {
  void addListener(Runnable listener);
  void removeListener(Runnable listener);
}
