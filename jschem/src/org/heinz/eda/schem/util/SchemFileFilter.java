
package org.heinz.eda.schem.util;

import org.heinz.framework.crossplatform.utils.CustomFileFilter;
import org.heinz.framework.crossplatform.utils.Translator;


public class SchemFileFilter extends CustomFileFilter {

	public static final String SCHEM_EXTENSION = "jsch";

	public static final String EXPRESS_EXTENSION = "sch";

	private static final String[] extensions = {SCHEM_EXTENSION, EXPRESS_EXTENSION};

	private static SchemFileFilter saveInstance;

	private static SchemFileFilter loadInstance;

	public static SchemFileFilter instance(boolean save) {
		if(save) {
			if(saveInstance == null) {
				saveInstance = new SchemFileFilter(save);
			}
			return saveInstance;
		}

		if(loadInstance == null) {
			loadInstance = new SchemFileFilter(save);
		}

		return loadInstance;
	}

	private SchemFileFilter(boolean save) {
		super(save ? new String[]{SCHEM_EXTENSION} : extensions, Translator.translate("SCHEMATICS"));
	}

}
