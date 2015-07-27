package org.heinz.framework.crossplatform.utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public abstract class AbstractListenerSupport {
	private List listeners = new ArrayList();
	protected Object sender;
	
	public AbstractListenerSupport(Object sender) {
		this.sender = sender;
	}
	
	protected void addListener(Object l) {
		if(!listeners.contains(l))
			listeners.add(l);
	}
	
	protected void removeListener(Object l) {
		listeners.remove(l);
	}
	
	protected Iterator listeners() {
		return new ArrayList(listeners).iterator();
	}
	
	public int getListenerCount() {
		return listeners.size();
	}
}
