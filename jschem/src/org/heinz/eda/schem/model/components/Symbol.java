
package org.heinz.eda.schem.model.components;



public class Symbol extends Component {

	public static final String KEY_NET_NAME = "NET_NAME";

	public Symbol() {
	}

	public Symbol(int x, int y) {
		super(x, y);
	}

	public Symbol(Symbol c) {
		super(c);
	}

	@Override
	public AbstractComponent duplicate() {
		return new Symbol(this);
	}

	@Override
	public boolean supportsAutoIdAssignment() {
		return false;
	}

	@Override
	protected void initTexts() {
		group = true;
		int y = 0;
		addAttributeText(KEY_NET_NAME, "", y, false);
	}

}
