
package org.heinz.framework.crossplatform.utils;

import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.StringTokenizer;

import javax.swing.KeyStroke;

import org.heinz.framework.crossplatform.Application;

public class Translator {

	public static final String DEFAULT_RESOURCEBUNDLE_PATH = Application.DEFAULT_DATA_PATH + "translations/";

	private static final String LOCALE_TOKEN = "LOCALE";

	private static final String TRANSLATOR_TOKEN = "TRANSLATOR_NAME";

	private static Translator instance;

	private final List bundles = new ArrayList();

	public static Translator instance() {
		return instance;
	}

	@SuppressWarnings("LeakingThisInConstructor")
	public Translator(String baseName) {
		if(instance == null) {
			instance = this;
		} else {
			throw new UnsupportedOperationException("translator exists");
		}

		addBundle(baseName);
	}

	public final void addBundle(String baseName) {
		ResourceBundle bundle = null;
		try {
			ResourceBundle.getBundle(baseName);
		} catch(MissingResourceException mex) {
			baseName = DEFAULT_RESOURCEBUNDLE_PATH + baseName;
			bundle = ResourceBundle.getBundle(baseName);
		}
		bundles.add(new BundleInfo(baseName, bundle, Locale.getDefault()));
	}

	public String getTranslation(String key) {
		return getTranslation(key, Locale.getDefault());
	}

	public String getTranslation(String key, Locale locale) {
		for(Iterator it = bundles.iterator(); it.hasNext();) {
			BundleInfo bi = (BundleInfo) it.next();
			try {
				return bi.getBundle(locale).getString(key);
			} catch(MissingResourceException ex) {
			}
		}

		throw new MissingResourceException("Not found: " + key, "String", key);
	}

	public static String translate(String key) {
		return instance.getTranslation(key);
	}

	public Map getAvailableTranslations() {
		Locale[] locales = Locale.getAvailableLocales();
		Map localesByLanguage = new HashMap();

		for(Locale l : locales) {
			List ls = (List) localesByLanguage.get(l.getLanguage());
			if(ls == null) {
				ls = new ArrayList();
				localesByLanguage.put(l.getLanguage(), ls);
			}
			ls.add(l);
		}

		String defaultLocale;
		Map translators = new HashMap();
		try {
			defaultLocale = getTranslation(LOCALE_TOKEN);
		} catch(MissingResourceException mex) {
			return translators;
		}

		translators.put(Locale.getDefault(), getTranslation(TRANSLATOR_TOKEN));

		for(Iterator it = localesByLanguage.keySet().iterator(); it.hasNext();) {
			String language = (String) it.next();

			Locale locale = new Locale(language);
			String locStr = getTranslation(LOCALE_TOKEN, locale);
			if(locStr.equals(defaultLocale)) {
				continue;
			}

			translators.put(locale, getTranslation(TRANSLATOR_TOKEN, locale));
		}
		return translators;
	}

	public KeyStroke getAccelerator(String menuToken, String platform) {
		String token = menuToken + "_ACC_" + platform.toUpperCase();
		try {
			String acc = getTranslation(token);
			return parseAccelerator(acc);
		} catch(MissingResourceException mex) {
		}

		return null;
	}

	@SuppressWarnings({"UseSpecificCatch", "CallToPrintStackTrace"})
	private static KeyStroke parseAccelerator(String accString) {
		int modifiers = 0;
		int keycode = 0;

		StringTokenizer st = new StringTokenizer(accString, "-");
		String token = null;
		int menuMask = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();

		while(true) {
			token = st.nextToken().toUpperCase();
			if(!st.hasMoreTokens()) {
				break;
			}

			switch(token) {
				case "CTRL":
					modifiers = modifiers | menuMask;
					break;
				case "ALT":
					modifiers = modifiers | KeyEvent.ALT_DOWN_MASK;
					break;
				case "SHIFT":
					modifiers = modifiers | KeyEvent.SHIFT_DOWN_MASK;
					break;
			}
		}

		String vk = "VK_" + token;
		try {
			Field f = KeyEvent.class.getDeclaredField(vk);
			Integer i = (Integer) f.get(null);
			keycode = i;
		} catch(Exception e) {
			e.printStackTrace();
		}

		return KeyStroke.getKeyStroke(keycode, modifiers);
	}

	//------------------------------------------------------------------------

	class BundleInfo {

		String bundleBaseName;

		Map bundlesByLocale = new HashMap();

		public BundleInfo(String bundleBaseName, ResourceBundle bundle, Locale locale) {
			this.bundleBaseName = bundleBaseName;
			bundlesByLocale.put(locale, bundle);
		}

		public ResourceBundle getBundle(Locale locale) {
			ResourceBundle b = (ResourceBundle) bundlesByLocale.get(locale);
			if(b == null) {
				ResourceBundle rb = ResourceBundle.getBundle(bundleBaseName, locale);
				if(!rb.getLocale().equals(locale)) {
					bundlesByLocale.put(locale, rb);
				}
				b = rb;
			}
			return b;
		}

	}

}
