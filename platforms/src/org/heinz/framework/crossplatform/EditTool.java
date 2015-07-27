package org.heinz.framework.crossplatform;

import java.util.List;

public interface EditTool {
	void addEditToolListener(EditToolListener listener);
	void removeEditToolListener(EditToolListener listener);
	public List getToolbarObjects();
}
