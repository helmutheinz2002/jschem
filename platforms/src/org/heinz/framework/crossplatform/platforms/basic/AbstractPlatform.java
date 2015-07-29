
package org.heinz.framework.crossplatform.platforms.basic;

import org.heinz.framework.crossplatform.Platform;
import org.heinz.framework.utils.cmdline.CommandLineOption;
import org.heinz.framework.utils.cmdline.CommandLineParser;

public abstract class AbstractPlatform implements Platform {

	private CommandLineOption MDI_OPTION;

	private boolean isDefaultMDI = false;

	protected AbstractPlatform(String[] args, boolean defaultMDI) {
		try {
			MDI_OPTION = new CommandLineOption("mdi", Boolean.class, defaultMDI);
		} catch(Exception e) {
			// impossible
		}

		CommandLineParser.addOption(MDI_OPTION);
		CommandLineParser.instance().parseArguments(args);
		Boolean b = (Boolean) CommandLineParser.instance().getOptionValue(MDI_OPTION);
		isDefaultMDI = b;
	}

	@Override
	public boolean isDefaultMDI() {
		return isDefaultMDI;
	}

}
