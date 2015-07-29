
package org.heinz.framework.crossplatform.utils;

import java.awt.Component;

import javax.swing.JOptionPane;

public class DefaultMessages {

	public static final int OPTION_CANCEL = 0;

	public static final int OPTION_SAVE = 1;

	public static final int OPTION_DONT_SAVE = 2;

	public static final int OPTION_DISCARD_ALL = 3;

	public static final int OPTION_REVIEW = 4;

	public static int askSave(Component document) {
		Object[] options = {
			Translator.translate("OPTION_SAVE"),
			Translator.translate("OPTION_CANCEL"),
			Translator.translate("OPTION_DONT_SAVE")
		};

		int ret = JOptionPane.showOptionDialog(document, Translator.translate("CLOSE_CONFIRMATION_DOCUMENT"),
				Translator.translate("TITLE_CLOSE"),
				JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
				null, options, options[0]);

		switch(ret) {
			case 0:
				return OPTION_SAVE;
			case 2:
				return OPTION_DONT_SAVE;
			default:
				break;
		}
		return OPTION_CANCEL;
	}

	public static int askQuit(Component application) {
		Object[] options = {
			Translator.translate("OPTION_REVIEW"),
			Translator.translate("OPTION_CANCEL"),
			Translator.translate("OPTION_DISCARD_ALL")
		};

		int ret = JOptionPane.showOptionDialog(application, Translator.translate("CLOSE_CONFIRMATION_APPLICATION"),
				Translator.translate("TITLE_QUIT"),
				JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
				null, options, options[0]);

		switch(ret) {
			case 0:
				return OPTION_REVIEW;
			case 2:
				return OPTION_DISCARD_ALL;
			default:
				break;
		}
		return OPTION_CANCEL;
	}

}
