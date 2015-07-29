
package org.heinz.framework.utils.cmdline;

import java.lang.reflect.Constructor;

public class CommandLineOption {

	private final String name;

	private final Class clazz;

	private Constructor constructor;

	private final Object defaultValue;

	public CommandLineOption(String name, Class clazz, Object defaultValue) throws Exception {
		this.name = name;
		this.clazz = clazz;
		this.defaultValue = defaultValue;

		if(clazz != null) {
			constructor = clazz.getConstructor(new Class[]{String.class});
		}
	}

	public Class getArgumentClass() {
		return clazz;
	}

	public String getName() {
		return name;
	}

	public Object parse(String stringValue) throws Exception {
		return constructor.newInstance(new Object[]{stringValue});
	}

	public Object getDefaultValue() {
		return defaultValue;
	}

}
