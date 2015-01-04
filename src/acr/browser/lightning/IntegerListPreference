package acr.browser.lightning;

import android.content.Context;
import android.preference.ListPreference;
import android.util.AttributeSet;

/**
 * @author Devin
 * 
 * A ListPreference that uses integers for values instead of Strings.
 */
public class IntegerListPreference extends ListPreference {

	public IntegerListPreference(Context context, AttributeSet attrs) {

		super(context, attrs);
	}

	public IntegerListPreference(Context context) {

		super(context);
	}

	@Override
	protected boolean persistString(String value) {

		if (value == null) {
			return false;
		} else {
			return persistInt(Integer.valueOf(value));
		}
	}

	@Override
	protected String getPersistedString(String defaultReturnValue) {

		if (getSharedPreferences().contains(getKey())) {
			int intValue = getPersistedInt(0);
			return String.valueOf(intValue);
		} else {
			return defaultReturnValue;
		}
	}
}
