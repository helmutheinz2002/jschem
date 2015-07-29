
package org.heinz.framework.crossplatform.utils;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.util.Iterator;
import java.util.List;

import javax.swing.Action;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.SwingUtilities;

import org.heinz.framework.crossplatform.Application;
import org.heinz.framework.crossplatform.Document;
import org.heinz.framework.crossplatform.DocumentAdapter;
import org.heinz.framework.crossplatform.DocumentListener;
import org.heinz.framework.crossplatform.platforms.basic.ApplicationActions;

public class WindowMenuHelper {

	private final Application application;

	private final boolean isMDI;

	private final boolean activateDisabledMenu;

	public WindowMenuHelper(Application application, boolean isMDI, boolean activateDisabledMenu) {
		this.application = application;
		this.isMDI = isMDI;
		this.activateDisabledMenu = activateDisabledMenu;
	}

	public void update(final JMenu menu) {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			@SuppressWarnings("null")
			public void run() {
				final List docs = application.getDocuments();
				final Document doc = application.getActiveDocument();

				menu.removeAll();

				JMenuItem miniItem = new JMenuItem((String) ApplicationActions.instance().miniWindowItem.getValue(Action.NAME));
				menu.add(miniItem);
				miniItem.setEnabled((doc != null) && !doc.isIconified());
				miniItem.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						doc.setIconified(true);
					}

				});

				JMenuItem maxiItem = new JMenuItem((String) ApplicationActions.instance().maxiWindowItem.getValue(Action.NAME));
				menu.add(maxiItem);
				maxiItem.setEnabled(doc != null);
				maxiItem.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						if(doc.isMaximized()) {
							doc.setMaximized(false);
						} else {
							doc.setIconified(false);
							doc.setMaximized(true);
						}
					}

				});
				menu.addSeparator();

				JMenuItem nextItem = new JMenuItem((String) ApplicationActions.instance().nextWindowItem.getValue(Action.NAME));
				menu.add(nextItem);
				nextItem.setEnabled((docs.size() > 1) && (doc != null));
				nextItem.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						nextDocument(docs, doc, 1);
					}

				});

				JMenuItem prevItem = new JMenuItem((String) ApplicationActions.instance().prevWindowItem.getValue(Action.NAME));
				menu.add(prevItem);
				prevItem.setEnabled((docs.size() > 1) && (doc != null));
				prevItem.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						nextDocument(docs, doc, -1);
					}

				});

				if(!isMDI) {
					menu.addSeparator();

					JMenuItem allToFrontItem = new JMenuItem((String) ApplicationActions.instance().allToFrontItem.getValue(Action.NAME));
					menu.add(allToFrontItem);
					allToFrontItem.setEnabled(docs.size() > 0);
					allToFrontItem.addActionListener(new ActionListener() {

						@Override
						public void actionPerformed(ActionEvent e) {
							allToFront(docs, false);
						}

					});
				}

				if(docs.size() > 0) {
					menu.addSeparator();
				}

				for(Iterator it = docs.iterator(); it.hasNext();) {
					final Document d = (Document) it.next();
					String t = d.getTitle();
					if(d.isIconified()) {
						t = "(" + t + ")";
					}

					final JCheckBoxMenuItem item = new JCheckBoxMenuItem(t);

					item.addActionListener(new ActionListener() {

						@Override
						public void actionPerformed(ActionEvent e) {
							d.setIconified(false);
							d.setSelected();
						}

					});

					final DocumentListener dl = new DocumentAdapter() {

						@Override
						public void documentActivated(Document document) {
							item.setSelected(true);
						}

						@Override
						public void documentDeactivated(Document document) {
							item.setSelected(false);
						}

						@Override
						public void documentClosed(Document document) {
							document.removeDocumentListener(this);
						}

					};

					d.addDocumentListener(dl);

					item.addHierarchyListener(new HierarchyListener() {

						@Override
						public void hierarchyChanged(HierarchyEvent e) {
							if((e.getChangeFlags() & HierarchyEvent.PARENT_CHANGED) != 0) {
								if(e.getSource() == item) {
									d.removeDocumentListener(dl);
								}
							}
						}

					});
					if(d.isSelected()) {
						item.setSelected(true);
					}

					menu.add(item);
				}

				if(activateDisabledMenu) {
					menu.setEnabled(MenuHelper.hasActiveEntries(menu));
					MenuHelper.activateMenuDisabledState(menu);
				}
			}

		});
	}

	public static void allToFront(List docs, boolean withIconified) {
		for(Iterator it = docs.iterator(); it.hasNext();) {
			Document d = (Document) it.next();
			if(!d.isIconified()) {
				d.setSelected();
			} else {
				if(withIconified) {
					d.setIconified(false);
					d.setSelected();
				}
			}
		}
	}

	private void nextDocument(List docs, Document d, int offset) {
		if(d == null) {
			return;
		}

		int idx = docs.indexOf(d);
		int nidx = idx + offset;
		if(nidx >= docs.size()) {
			nidx = 0;
		}
		if(nidx < 0) {
			nidx = docs.size() - 1;
		}

		Document nd = (Document) docs.get(nidx);
		nd.setIconified(false);
		nd.setSelected();
	}

}
