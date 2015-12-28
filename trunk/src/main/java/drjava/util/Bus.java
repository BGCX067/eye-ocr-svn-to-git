/*
(C) 2007 Stefan Reich (jazz@drjava.de)
This source file is part of Project Prophecy.
For up-to-date information, see http://www.drjava.de/prophecy

This source file is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation, version 2.1.
*/

package drjava.util;

import java.util.ArrayList;
import java.util.List;

public class Bus<Data> implements Port<Data>, Sender<Data> {
  private List<Port<Data>> receivers = new ArrayList<Port<Data>>();

  public Bus() {
    Tracing.busCreated(this);
  }

  public void receive(Data data) {
    for (Port<Data> receiver : new ArrayList<Port<Data>>(receivers)) {
      try {
        receiver.receive(data);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  public void addReceiver(Port<Data> receiver) {
    receivers.add(receiver);
  }

  public void removeReceiver(Port<Data> receiver) {
    receivers.remove(receiver);
  }

  public void removeAllReceivers() {
    receivers.clear();
  }

  public List<Port<Data>> getReceivers() {
    return receivers;
  }

  public boolean hasReceivers() {
    return !receivers.isEmpty();
  }
}