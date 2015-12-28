/*
(C) 2007 Stefan Reich (jazz@drjava.de)
This source file is part of Project Prophecy.
For up-to-date information, see http://www.drjava.de/prophecy

This source file is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation, version 2.1.
*/

package prophecy.common.gui;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * A table model that is typically easy to use/customize with less complicated methods to write
 * than when you directly subclass AbstractTableModel.
 *
 * First, you choose an item type (A) - the type of object your table is about.
 *
 * Then, for every column, you define a subclass of SexyColumn<A> that implements getName()
 * (the column's title) and getCell() which determines what to display in this column.
 *
 * Now create a SexyTableModel with your columns in the constructor and add your data with
 * setList().
 *
 * That's it!
 *
 * @param <A>
 */
public class SexyTableModel<A> extends AbstractTableModel {
  List<A> list = new ArrayList<A>();
  private List<SexyColumn<A>> columns = new ArrayList<SexyColumn<A>>();

  public SexyTableModel(SexyColumn<A>... columns) {
    this.columns.addAll(Arrays.asList(columns));
  }

  public void setList(Collection<A> collection) {
    list = collection == null ? new ArrayList<A>() : new ArrayList<A>(collection);
    fireTableDataChanged();
  }

  public int getRowCount() {
    return list.size();
  }

  public Object getValueAt(final int row, final int col) {
    Object data = getCell(list.get(row), row, col);
    return data;
  }

  /** may return: String, Icon */
  protected Object getCell(A a, int row, int col) {
    return getColumn(col).getCell(row, a);
  }

  private SexyColumn<A> getColumn(int col) {
    return columns.get(col);
  }

  public List<A> getList() {
    return list;
  }

  public int getColumnCount() {
    return columns.size();
  }

  public void setColumms(List<SexyColumn<A>> columns) {
    this.columns = columns;
  }

  public String getColumnName(int i) {
    return getColumn(i).getName();
  }

  public A getItem(int row) {
    return row >= 0 && row < list.size() ? list.get(row) : null;
  }

  public void addItem(A a) {
    list.add(a);
    fireTableRowsInserted(list.size()-1, list.size()-1);
  }

  public void removeItem(int row) {
    list.remove(row);
    fireTableRowsDeleted(row, row);
  }

  public void addColumn(SexyColumn<A> column) {
    columns.add(column);
    fireTableStructureChanged();
  }

  public void insertItem(int row, A a) {
    list.add(row, a);
    fireTableRowsInserted(row, row);
  }

  public boolean isCellEditable(int rowIndex, int columnIndex) {
    return getColumn(columnIndex).isCellEditable(rowIndex, getItem(rowIndex));
  }

  public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
    getColumn(columnIndex).setValueAt(rowIndex, getItem(rowIndex), aValue);
  }

  public void setItem(int row, A a) {
    list.set(row, a);
    fireTableRowsUpdated(row, row);
  }

  public int findColumn(SexyColumn<A> column) {
    return columns.indexOf(column);
  }
}