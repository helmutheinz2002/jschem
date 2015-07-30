package org.heinz.eda.schem.util;

import org.heinz.framework.crossplatform.utils.CustomFileFilter;
import org.heinz.framework.crossplatform.utils.Translator;

public class ComponentFileFilter extends CustomFileFilter {

    public static final String COMPONENT_EXTENSION_JSCHEM = "jcmp";

    public static final String COMPONENT_EXTENSION_EXPRESSSCH = "s";

    private static final String[] extensions = {COMPONENT_EXTENSION_JSCHEM, COMPONENT_EXTENSION_EXPRESSSCH};

    private static ComponentFileFilter saveInstance;

    private static ComponentFileFilter loadInstance;

    public static ComponentFileFilter instance(boolean save) {
        if (save) {
            if (saveInstance == null) {
                saveInstance = new ComponentFileFilter(save);
            }
            return saveInstance;
        }

        if (loadInstance == null) {
            loadInstance = new ComponentFileFilter(save);
        }

        return loadInstance;
    }

    private ComponentFileFilter(boolean save) {
        super(save ? new String[]{COMPONENT_EXTENSION_JSCHEM} : extensions, Translator.translate("COMPONENTS"));
    }

}
