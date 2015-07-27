package org.heinz.eda.schem.model.xml;

public class XmlFormatVersion {
	public static final XmlFormatUpdater[] FORMAT_UPDATERS = {
		new XmlFormatUpdater0(),
		new XmlFormatUpdater1(),
		//new XmlFormatUpdater2()
	};
	public static final int VERSION = FORMAT_UPDATERS.length;
	public static final String VERSION_ATTRIBUTE = "version";
}
