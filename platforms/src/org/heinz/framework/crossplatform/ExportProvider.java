package org.heinz.framework.crossplatform;

import java.awt.Component;

import org.heinz.framework.utils.AbstractOptions;

public interface ExportProvider {
	AbstractOptions getOptions();
	Component getComponent();
}
