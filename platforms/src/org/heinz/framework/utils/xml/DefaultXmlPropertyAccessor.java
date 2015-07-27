package org.heinz.framework.utils.xml;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;


public class DefaultXmlPropertyAccessor implements XmlPropertyAccessor {
	private String name;
	private XmlPropertyConverter converter;
	private Map defaultPropertyCache = new HashMap();
	private Map getterMethodCache = new HashMap();
	private Map setterMethodCache = new HashMap();

	
	public DefaultXmlPropertyAccessor(String name, XmlPropertyConverter converter) {
		this.name = name;
		this.converter = converter;
	}
	
	public String formatValue(Object o) {
		return converter.formatValue(o);
	}
	
	public String getValue(Object o) throws IntrospectionException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		Method getter = getGetter(o.getClass());
		Object value = getter.invoke(o, (Object[]) null);
		if(value == null)
			return null;
		
		Object defVal = getDefaultValue(o.getClass());
		if(value.equals(defVal))
			return null;

		return formatValue(value);
	}
	
	public void setValue(Object o, String s) throws IntrospectionException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		Method setter = getSetter(o.getClass());
		setter.invoke(o, new Object[] { converter.parseValue(s) });
	}

	private Method getGetter(Class clazz) throws IntrospectionException {
		Method getter = (Method) getterMethodCache.get(clazz);
		if(getter == null) {
			PropertyDescriptor pd = new PropertyDescriptor(name, clazz);
			getter = pd.getReadMethod();
			getterMethodCache.put(clazz, getter);
		}
		return getter;
	}
	
	private Method getSetter(Class clazz) throws IntrospectionException {
		Method setter = (Method) setterMethodCache.get(clazz);
		if(setter == null) {
			PropertyDescriptor pd = new PropertyDescriptor(name, clazz);
			setter = pd.getWriteMethod();
			setterMethodCache.put(clazz, setter);
		}
		return setter;
	}
	
	private Object getPropertyValue(Object o) throws IntrospectionException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		Method getter = getGetter(o.getClass());
		Object res = getter.invoke(o, (Object[]) null);
		return res;
	}

	private Object getDefaultValue(Class clazz) throws IllegalArgumentException, IntrospectionException, IllegalAccessException, InvocationTargetException {
		Object defVal = defaultPropertyCache.get(clazz);
		if(defVal == null) {
			Object instance = null;
			try {
				instance = clazz.newInstance();
			} catch (Exception e) {
				// impossible by design
			}
			
			defVal = getPropertyValue(instance);
			defaultPropertyCache.put(clazz, defVal);
		}
		
		return defVal;
	}
}
