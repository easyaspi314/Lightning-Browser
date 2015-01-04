package de.psdev.licensesdialog.licenses;

import acr.browser.lightning.R;
import android.content.Context;


public class MozillaPublicLicense20 extends License {

	private static final long	serialVersionUID	= 7034342002423961548L;

	@Override
	public String getName() {

		return "Mozilla Public License, version 2.0";
	}

	@Override
	public String readSummaryTextFromResources(Context context) {

		return getContent(context, R.raw.mpl_20_summary);
	}

	@Override
	public String readFullTextFromResources(Context context) {

		return getContent(context, R.raw.mpl_20_full);
	}

	@Override
	public String getVersion() {

		return "2.0";
	}

	@Override
	public String getUrl() {

		return "http://mozilla.org/MPL/2.0/";
	}

}
