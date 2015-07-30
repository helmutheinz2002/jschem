
package org.heinz.eda.schem.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ListenableArrayList extends ArrayList {

	private final List listeners = new ArrayList();

	public void addListListener(ListListener listener) {
		if(!listeners.contains(listener)) {
			listeners.add(listener);
		}
	}

	public void removeListListener(ListListener listener) {
		listeners.remove(listener);
	}

	@Override
	public void clear() {
		super.clear();
		fireListChange();
	}

	@Override
	public boolean add(Object c) {
		boolean b = super.add(c);
		fireListChange();
		return b;
	}

	@Override
	public boolean remove(Object c) {
		boolean b = super.remove(c);
		fireListChange();
		return b;
	}

	@SuppressWarnings("CallToPrintStackTrace")
	private void fireListChange() {
		for(Iterator it = listeners.iterator(); it.hasNext();) {
			ListListener l = (ListListener) it.next();
			try {
				l.listChanged();
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}

}
