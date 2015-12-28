/*
(C) 2007 Stefan Reich (jazz@drjava.de)
This source file is part of Project Prophecy.
For up-to-date information, see http://www.drjava.de/prophecy

This source file is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation, version 2.1.
*/

package drjava.util;

import static drjava.util.ObjectUtil.nice;

public class NoResult<A> extends Result<A> {
  private Throwable exception;

  public NoResult() {
    super(null, false);
  }

  public NoResult(Throwable exception) {
    super(null, false);
    this.exception = exception;
  }

  public NoResult(String msg) {
    this(new RuntimeException(msg));
  }

  public Throwable getException() {
    return exception;
  }

  public String toString() {
    return nice(exception);
  }

  @Override
  public A get() {
    throw exception instanceof RuntimeException ? (RuntimeException) exception
      : new RuntimeException(exception);
  }

  @Override
  public A getValue() {
    return get();
  }
}