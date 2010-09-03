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
package com.google.gwt.requestfactory.shared;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

/**
 * <p>
 * <span style="color:red">Experimental API: This class is still under rapid
 * development, and is very likely to be deleted. Use it at your own risk.
 * </span>
 * </p>
 * Abstract base class for an event announcing changes to a {@link EntityProxy}.
 * <p>
 * Note that this event includes an unpopulated copy of the changed proxy
 * &mdash; all properties are undefined except it's id. That is, the event
 * includes only enough information for receivers to issue requests to get
 * themselves fresh copies of the proxy.
 * <p>
 * TODO: rather than an empty proxy, consider using a string token 
 * 
 * @param <P> the type of the proxy
 * @param <H> the type of event handler
 */
// TODO Should this provide a collection of changed values rather than fire for
// each one?
public abstract class EntityProxyChangedEvent<P extends EntityProxy, H extends EventHandler>
    extends GwtEvent<H> {
  P proxy;
  WriteOperation writeOperation;

  public EntityProxyChangedEvent(P proxy, WriteOperation writeOperation) {
    this.proxy = proxy;
    this.writeOperation = writeOperation;
  }

  /**
   * @return an unpopulated copy of the changed proxy &mdash; all properties
   *         are undefined except its id
   */
  public P getProxy() {
    return proxy;
  }

  public WriteOperation getWriteOperation() {
    return writeOperation;
  }
}