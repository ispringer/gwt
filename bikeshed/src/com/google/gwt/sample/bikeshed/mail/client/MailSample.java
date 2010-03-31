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
package com.google.gwt.sample.bikeshed.mail.client;

import com.google.gwt.bikeshed.cells.client.CheckboxCell;
import com.google.gwt.bikeshed.cells.client.FieldUpdater;
import com.google.gwt.bikeshed.cells.client.TextCell;
import com.google.gwt.bikeshed.list.client.Column;
import com.google.gwt.bikeshed.list.client.Header;
import com.google.gwt.bikeshed.list.client.PagingTableListView;
import com.google.gwt.bikeshed.list.shared.ListListModel;
import com.google.gwt.bikeshed.list.shared.SelectionModel.DefaultSelectionModel;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;

import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

/**
 * A demo of selection features.
 */
public class MailSample implements EntryPoint {

  class MailSelectionModel extends DefaultSelectionModel<Message> {
    private static final int ALL = 0;
    private static final int NONE = 1;
    private static final int READ = 2;
    private static final int SENDER = 3;
    private static final int SUBJECT = 4;
    private static final int UNREAD = 5;

    private Set<Integer> minusIdExceptions = new TreeSet<Integer>();
    private Set<Integer> minusRowExceptions = new TreeSet<Integer>();
    private Set<Integer> plusIdExceptions = new TreeSet<Integer>();
    private Set<Integer> plusRowExceptions = new TreeSet<Integer>();

    private String search;
    private int type = NONE;

    public boolean isSelected(Message object, int index) {
      // Check row exceptions first
      if (plusRowExceptions.contains(index)) {
        return true;
      }
      if (minusRowExceptions.contains(index)) {
        return false;
      }
      // Check id exceptions next
      int id = object.id;
      if (plusIdExceptions.contains(id)) {
        return true;
      }
      if (minusIdExceptions.contains(id)) {
        return false;
      }
      return isDefaultSelected(object);
    }

    public void setSearch(String search) {
      this.search = search.toLowerCase();
      updateListeners();
    }

    public void setSelected(int index, boolean selected) {
      if (!selected) {
        plusRowExceptions.remove(index);
        minusRowExceptions.add(index);
      } else {
        plusRowExceptions.add(index);
        minusRowExceptions.remove(index);
      }
      updateListeners();
    }

    public void setSelected(int start, int len, boolean selected) {
      for (int index = start; index < start + len; index++) {
        if (!selected) {
          plusRowExceptions.remove(index);
          minusRowExceptions.add(index);
        } else {
          plusRowExceptions.add(index);
          minusRowExceptions.remove(index);
        }
      }
      updateListeners();
    }

    public void setSelected(Message object, boolean selected) {
      int id = object.id;
      if (!selected) {
        plusIdExceptions.remove(id);
        minusIdExceptions.add(id);
      } else {
        plusIdExceptions.add(id);
        minusIdExceptions.remove(id);
      }
      updateListeners();
    }
    
    public void setType(int type) {
      this.type = type;
      plusIdExceptions.clear();
      plusRowExceptions.clear();
      minusIdExceptions.clear();
      minusRowExceptions.clear();
      updateListeners();
    }

    public String toString() {
      StringBuilder sb = new StringBuilder();
      switch (type) {
        case NONE:
          sb.append("NONE ");
          break;
        case ALL:
          sb.append("ALL ");
          break;
        case READ:
          sb.append("READ ");
          break;
        case UNREAD:
          sb.append("UNREAD ");
          break;
        case SENDER:
          sb.append("SENDER ");
          sb.append(search);
          sb.append(' ');
          break;
        case SUBJECT:
          sb.append("SUBJECT ");
          sb.append(search);
          sb.append(' ');
          break;
      }

      boolean first = true;
      for (int i : plusRowExceptions) {
        if (first) {
          first = false;
          sb.append("+row(s) ");
        }
        sb.append(i);
        sb.append(' ');
      }

      first = true;
      for (int i : plusIdExceptions) {
        if (first) {
          first = false;
          sb.append("+msg(s) ");
        }
        sb.append(i);
        sb.append(' ');
      }

      first = true;
      for (int i : minusRowExceptions) {
        if (first) {
          first = false;
          sb.append("-row(s) ");
        }
        sb.append(i);
        sb.append(' ');
      }

      first = true;
      for (int i : minusIdExceptions) {
        if (first) {
          first = false;
          sb.append("-msg(s) ");
        }
        sb.append(i);
        sb.append(' ');
      }

      return sb.toString();
    }

    public void updateListeners() {
      super.updateListeners();
      selectionLabel.setText("Selected " + this.toString());
    }

    private boolean isDefaultSelected(Message object) {
      switch (type) {
        case NONE:
          return false;
        case ALL:
          return true;
        case READ:
          return object.isRead();
        case UNREAD:
          return !object.isRead();
        case SENDER:
          return object.getSender().toLowerCase().contains(search);
        case SUBJECT:
          return object.getSubject().toLowerCase().contains(search);
        default:
          throw new IllegalStateException("type = " + type);
      }
    }
  }

  class Message {
    int id;
    boolean isRead;
    String sender;
    String subject;

    public Message(int id, String sender, String subject) {
      super();
      this.id = id;
      this.sender = sender;
      this.subject = subject;
    }

    @Override
    public boolean equals(Object obj) {
      if (!(obj instanceof Message)) {
        return false;
      }
      return id == ((Message) obj).id;
    }

    public int getId() {
      return id;
    }

    public String getSender() {
      return sender;
    }

    public String getSubject() {
      return subject;
    }

    @Override
    public int hashCode() {
      return id;
    }

    public boolean isRead() {
      return isRead;
    }

    @Override
    public String toString() {
      return "Message [id=" + id + ", sender=" + sender + ", subject="
          + subject + ", read=" + isRead + "]";
    }
  }

  private static final String[] senders = {
      "test@example.com", "spam1@spam.com", "gwt@google.com"};

  private static final String[] subjects = {
      "GWT rocks", "What's a widget?", "Money in Nigeria"};

  private Label selectionLabel = new Label();

  public void onModuleLoad() {
    TextCell textCell = new TextCell();

    ListListModel<Message> listModel = new ListListModel<Message>();
    List<Message> messages = listModel.getList();
    Random rand = new Random();
    for (int i = 0; i < 1000; i++) {
      Message message = new Message(10000 + i,
          senders[rand.nextInt(senders.length)],
          subjects[rand.nextInt(subjects.length)]);
      message.isRead = rand.nextBoolean();
      messages.add(message);
    }

    final MailSelectionModel selectionModel = new MailSelectionModel();

    final PagingTableListView<Message> table = new PagingTableListView<Message>(
        listModel, 10);

    Column<Message, Boolean, Void> selectedColumn = new Column<Message, Boolean, Void>(
        new CheckboxCell()) {
      @Override
      public Boolean getValue(Message object, int index) {
        return selectionModel.isSelected(object, index);
      }
    };
    selectedColumn.setFieldUpdater(new FieldUpdater<Message, Boolean, Void>() {
      public void update(int index, Message object, Boolean value, Void viewData) {
        selectionModel.setSelected(object, value);
      }
    });
    Header<String> selectedHeader = new Header<String>(textCell);
    selectedHeader.setValue("Selected");
    table.addColumn(selectedColumn, selectedHeader);

    Column<Message, String, Void> isReadColumn = new Column<Message, String, Void>(
        textCell) {
      @Override
      public String getValue(Message object, int index) {
        return object.isRead ? "read" : "unread";
      }
    };
    Header<String> isReadHeader = new Header<String>(textCell);
    isReadHeader.setValue("Read");
    table.addColumn(isReadColumn, isReadHeader);

    Column<Message, String, Void> senderColumn = new Column<Message, String, Void>(
        new TextCell()) {
      @Override
      public String getValue(Message object, int index) {
        return object.getSender();
      }
    };
    Header<String> senderHeader = new Header<String>(textCell);
    senderHeader.setValue("Sender");
    table.addColumn(senderColumn, senderHeader);

    Column<Message, String, Void> subjectColumn = new Column<Message, String, Void>(
        textCell) {
      @Override
      public String getValue(Message object, int index) {
        return object.getSubject();
      }
    };
    Header<String> subjectHeader = new Header<String>(textCell);
    subjectHeader.setValue("Subject");
    table.addColumn(subjectColumn, subjectHeader);

    table.setSelectionModel(selectionModel);
    
    Label searchLabel = new Label("Search Sender or Subject:");
    final TextBox searchBox = new TextBox();
    searchBox.addKeyUpHandler(new KeyUpHandler() {
      public void onKeyUp(KeyUpEvent event) {
        selectionModel.setSearch(searchBox.getText());
      }
    });

    Button noneButton = new Button("Select None");
    Button allOnPageButton = new Button("Select All On This Page");
    Button allButton = new Button("Select All");
    Button readButton = new Button("Select Read");
    Button unreadButton = new Button("Select Unread");
    Button senderButton = new Button("Search Senders");
    Button subjectButton = new Button("Search Subject");

    noneButton.addClickHandler(new ClickHandler() {
      public void onClick(ClickEvent event) {
        selectionModel.setType(MailSelectionModel.NONE);
      }
    });

    allOnPageButton.addClickHandler(new ClickHandler() {
      public void onClick(ClickEvent event) {
        int pageSize = table.getPageSize();
        selectionModel.setSelected(table.getPage() * pageSize, pageSize, true);
      }
    });

    allButton.addClickHandler(new ClickHandler() {
      public void onClick(ClickEvent event) {
        selectionModel.setType(MailSelectionModel.ALL);
      }
    });

    readButton.addClickHandler(new ClickHandler() {
      public void onClick(ClickEvent event) {
        selectionModel.setType(MailSelectionModel.READ);
      }
    });

    unreadButton.addClickHandler(new ClickHandler() {
      public void onClick(ClickEvent event) {
        selectionModel.setType(MailSelectionModel.UNREAD);
      }
    });
    
    senderButton.addClickHandler(new ClickHandler() {
      public void onClick(ClickEvent event) {
        selectionModel.setType(MailSelectionModel.SENDER);
      }
    });
    
    subjectButton.addClickHandler(new ClickHandler() {
      public void onClick(ClickEvent event) {
        selectionModel.setType(MailSelectionModel.SUBJECT);
      }
    });
    
    HorizontalPanel panel = new HorizontalPanel();
    panel.add(searchLabel);
    panel.add(searchBox);
    
    RootPanel.get().add(panel);
    RootPanel.get().add(new HTML("<br>"));
    RootPanel.get().add(table);
    RootPanel.get().add(new HTML("<br>"));
    RootPanel.get().add(noneButton);
    RootPanel.get().add(allOnPageButton);
    RootPanel.get().add(allButton);
    RootPanel.get().add(readButton);
    RootPanel.get().add(unreadButton);
    RootPanel.get().add(subjectButton);
    RootPanel.get().add(senderButton);
    RootPanel.get().add(selectionLabel);
  }
}