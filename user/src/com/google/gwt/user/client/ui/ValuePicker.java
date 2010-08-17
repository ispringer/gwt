/*
 * Copyright 2010 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.google.gwt.user.client.ui;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.text.shared.Renderer;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;

import java.util.ArrayList;
import java.util.Collection;

/**
 * <p>
 * <span style="color:red">Experimental API: This class is still under rapid
 * development, and is very likely to be deleted. Use it at your own risk.
 * </span>
 * </p>
 * Allows the user to pick a single value from a list.
 * 
 * @param <T> the type of value
 */
public class ValuePicker<T> extends Composite
    implements HasConstrainedValue<T> {
  
  private static class DefaultCell<T> extends AbstractCell<T> {
    private final Renderer<T> renderer;
    
    DefaultCell(Renderer<T> renderer) {
      this.renderer = renderer; 
    }
    
    @Override
    public void render(T value, Object viewData, StringBuilder sb) {
      sb.append(renderer.render(value));
    }
  }

  private T value;

  private final CellList<T> cellList;
  private SingleSelectionModel<T> smodel = new SingleSelectionModel<T>();

  public ValuePicker(CellList<T> cellList) {
    this.cellList = cellList;
    initWidget(cellList);
    cellList.setSelectionModel(smodel);
    smodel.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
      public void onSelectionChange(SelectionChangeEvent event) {
        setValue(smodel.getSelectedObject());
      }
    });
  }
  
  public ValuePicker(Renderer<T> renderer) {
    this(new CellList<T>(new DefaultCell<T>(renderer)));
  }

  public HandlerRegistration addValueChangeHandler(ValueChangeHandler<T> handler) {
    return addHandler(handler, ValueChangeEvent.getType());
  }

  /**
   * @return this view
   */
  public ValuePicker<T> asWidget() {
    return this;
  }

  public int getPageSize() {
    return cellList.getPageSize();
  }

  public T getValue() {
    return value;
  }
  
  public void setPageSize(int size) {
    cellList.setPageSize(size);
  }

  public void setValue(T value) {
    setValue(value, true);
  }

  public void setValue(T value, boolean fireEvents) {
    T current = getValue();
    if ((current == value) || (current != null && current.equals(value))) {
      return;
    }
    this.value = value;
    smodel.setSelected(value, true);
    if (fireEvents) {
      ValueChangeEvent.fire(this, value);
    }
  }

  public void setValues(Collection<T> places) {
    cellList.setRowValues(0, new ArrayList<T>(places));
  }
}