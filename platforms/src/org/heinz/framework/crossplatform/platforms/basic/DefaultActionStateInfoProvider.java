
package org.heinz.framework.crossplatform.platforms.basic;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class DefaultActionStateInfoProvider implements ActionStateInfoProvider {

	private final List infoProviders = new ArrayList();

	public void addStateInfoProvider(ActionStateInfoProvider provider) {
		if(!infoProviders.contains(provider)) {
			infoProviders.add(provider);
		}
	}

	public void removeStateInfoProvider(ActionStateInfoProvider provider) {
		infoProviders.remove(provider);
	}

	@Override
	public void addActionStateInfos(ActionStateInfos stateInfos) {
		for(Iterator it = infoProviders.iterator(); it.hasNext();) {
			ActionStateInfoProvider ap = (ActionStateInfoProvider) it.next();
			ap.addActionStateInfos(stateInfos);
		}
	}

}
