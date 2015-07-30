
package org.heinz.eda.schem.ui;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.heinz.framework.utils.clipboard.SystemClipBoard;

public class SchemClipBoard extends SystemClipBoard {

	private static SchemClipBoard instance;

	@SuppressWarnings("LeakingThisInConstructor")
	public SchemClipBoard() {
		super(new DataFlavor(ArrayList.class, "JSchemComponents"));

		if(instance != null) {
			throw new IllegalArgumentException("Instance exists");
		}
		instance = this;
	}

	public static SchemClipBoard instance() {
		return (SchemClipBoard) instance;
	}

	@Override
	public void post(Object data) {
		Transferable transferData = new JSchemTransferable((List) data);
		clipboard.setContents(transferData, this);
	}

	public Iterator iterator() {
		return getClipboardContents().iterator();
	}

	public List getClipboardContents() {
		List contents = (List) getContents();
		if(contents == null) {
			return new ArrayList();
		}

		return contents;
	}

	//-------------------------------------------

	class JSchemTransferable implements Transferable {

		private final List components;

		public JSchemTransferable(List components) {
			this.components = components;
		}

		@Override
		public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
			if(flavor.equals(dataFlavor)) {
				return components;
			}
			throw new UnsupportedFlavorException(flavor);
		}

		@Override
		public DataFlavor[] getTransferDataFlavors() {
			return new DataFlavor[]{dataFlavor};
		}

		@Override
		public boolean isDataFlavorSupported(DataFlavor flavor) {
			return flavor.equals(dataFlavor);
		}

	}

}
