
package org.heinz.framework.utils;

import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.heinz.framework.crossplatform.CrossPlatform;
import org.heinz.framework.crossplatform.utils.Translator;

public class AboutHelper {

	public static String getAboutBoxText(String programDescription, String date, String copyright, String license, List libraries) {
		String l = license;
		String c = copyright;

		try {
			c = Translator.translate(copyright);
		} catch(Exception e) {
		}
		try {
			l = Translator.translate(license);
		} catch(Exception e) {
		}

		String text = "<html><h2>"
				+ programDescription
				+ "</h2>"
				+ "<table>"
				+ "<tr><td><b>" + Translator.translate("BUILD") + "</td><td>" + date + "</td></tr>"
				+ "<tr><td><b>" + Translator.translate("OPERATING_SYSTEM") + "</td><td>" + CrossPlatform.getOsInfo() + "</td></tr>"
				+ "<tr><td><b>" + Translator.translate("JAVA_RUNTIME") + "</td><td>" + CrossPlatform.getJavaInfo() + "</td></tr>"
				+ "</table>"
				+ "<br>" + c
				+ "<p><hr><br>"
				+ l
				+ "<p><hr><br>"
				+ Translator.translate("ABOUT_CREDITS")
				+ "<p>" + getLibraries(libraries);

		Map translators = Translator.instance().getAvailableTranslations();
		if(translators.size() > 0) {
			text += "<p><hr><br>" + Translator.translate("TRANSLATION_CREDITS") + "<p>" + getTranslationCredits(translators) + "<br>";
		}

		return text;
	}

	private static String getLibraries(List libraries) {
		String libs = "<table>";
		for(Iterator it = libraries.iterator(); it.hasNext();) {
			LibraryInfo li = (LibraryInfo) it.next();
			libs += "<tr><td><b>" + li.name + "</b></td><td>" + li.website + "</td></tr>";
		}

		libs += "</table>";
		return libs;
	}

	private static String getTranslationCredits(Map translators) {
		String translationCredits = "<table>";
		for(Iterator it = translators.keySet().iterator(); it.hasNext();) {
			Locale locale = (Locale) it.next();
			String language = locale.getDisplayLanguage();
			String nativeLanguage = locale.getDisplayLanguage(locale);
			String translator = (String) translators.get(locale);
			translationCredits += "<tr><td><b>" + nativeLanguage + "</b></td><td><b>(" + language + ")</b></td><td>" + translator + "</td></tr>";
		}
		translationCredits += "</table>";
		return translationCredits;
	}

	public static class LibraryInfo {

		public final String name;

		public final String website;

		public LibraryInfo(String name, String website) {
			this.name = name;
			this.website = website;
		}

	}

}
