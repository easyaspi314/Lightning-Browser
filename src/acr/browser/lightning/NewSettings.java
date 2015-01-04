package acr.browser.lightning;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.provider.Browser;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.preference.PreferenceFragment;
import android.text.TextUtils;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebIconDatabase;
import android.webkit.WebView;
import android.webkit.WebViewDatabase;
import de.psdev.licensesdialog.LicensesDialogFragment;

public class NewSettings extends FragmentActivity {

	private static final int	API	= android.os.Build.VERSION.SDK_INT;

	private boolean				mSystemBrowser;

	private Handler				messageHandler;

	private Context				context;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		messageHandler = new MessageHandler(context);
		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		ft.replace(android.R.id.content, new SettingsFragment());
		ft.commit();
	}

	private class SettingsFragment extends PreferenceFragment implements
			SharedPreferences.OnSharedPreferenceChangeListener {

		private int	mEasterEggCounter;

		@Override
		public void onSharedPreferenceChanged(SharedPreferences sharedPrefs, String key) {

			Preference pref = findPreference(key);
			if (pref instanceof IntegerListPreference) {
				// Update display title
				// Write the description for the newly selected preference
				// in the summary field.
				IntegerListPreference listPref = (IntegerListPreference) pref;
				CharSequence listDesc = listPref.getEntry();
				if (!TextUtils.isEmpty(listDesc)) {
					pref.setSummary(listDesc);
				}
			}
		}

		@Override
		public void onCreate(Bundle savedInstanceState) {

			super.onCreate(savedInstanceState);
			mSystemBrowser = PreferenceManager.getDefaultSharedPreferences(getActivity()).getBoolean(
					PreferenceConstants.SYSTEM_BROWSER_PRESENT, false);
			addPreferencesFromResource(R.xml.preferences);

			/**
			 * Check if the stock browser is available.
			 */
			CheckBoxPreference sync = (CheckBoxPreference) findPreference("syncHistory");
			Preference importPref = findPreference("importfrombrowser");
			if (mSystemBrowser == true) {
				sync.setSummary(R.string.stock_browser_available);
				importPref.setSummary(R.string.stock_browser_available);
				sync.setEnabled(true);
				importPref.setEnabled(true);
			} else {
				sync.setSummary(R.string.stock_browser_unavailable);
				importPref.setSummary(R.string.stock_browser_unavailable);
				sync.setEnabled(false);
				importPref.setEnabled(false);
			}

			/**
			 * Easter egg and version name.
			 */

			Preference version = findPreference("version");
			String code = "HOLO";

			try {
				PackageInfo p = getPackageManager().getPackageInfo(getPackageName(), 0);
				code = p.versionName;
			} catch (NameNotFoundException e) {
				// TODO add logging
				e.printStackTrace();
			}

			version.setSummary(code + "");
			version.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

				@Override
				public boolean onPreferenceClick(Preference pref) {

					mEasterEggCounter++;
					if (mEasterEggCounter == 10) {

						startActivity(new Intent(Intent.ACTION_VIEW, Uri
								.parse("http://imgs.xkcd.com/comics/compiling.png"), getActivity(), MainActivity.class));
						finish();
						mEasterEggCounter = 0;
					}
					return true;
				}
			});

			/**
			 * Clear Cache
			 */
			Preference mClearHistory = findPreference("clearCache");
			mClearHistory.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

				@Override
				public boolean onPreferenceClick(Preference v) {

					AlertDialog.Builder builder = new AlertDialog.Builder(getActivity()); // dialog
					builder.setTitle(getResources().getString(R.string.title_clear_history));
					builder.setMessage(getResources().getString(R.string.dialog_history))
							.setPositiveButton(getResources().getString(R.string.action_yes),
									new DialogInterface.OnClickListener() {

										@Override
										public void onClick(DialogInterface arg0, int arg1) {

											Thread clear = new Thread(new Runnable() {

												@Override
												public void run() {

													clearHistory();
												}
											});
											clear.start();
										}
									})
							.setNegativeButton(getResources().getString(R.string.action_no),
									new DialogInterface.OnClickListener() {

										@Override
										public void onClick(DialogInterface arg0, int arg1) {

											// TODO Auto-generated method stub
										}
									}).show();
					return true;
				}
			});

			/**
			 * Clear Cookies
			 */
			Preference mClearCookies = findPreference("clearCookies");
			mClearCookies.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

				@Override
				public boolean onPreferenceClick(Preference v) {

					AlertDialog.Builder builder = new AlertDialog.Builder(getActivity()); // dialog
					builder.setTitle(getResources().getString(R.string.title_clear_cookies));
					builder.setMessage(getResources().getString(R.string.dialog_cookies))
							.setPositiveButton(getResources().getString(R.string.action_yes),
									new DialogInterface.OnClickListener() {

										@Override
										public void onClick(DialogInterface arg0, int arg1) {

											Thread clear = new Thread(new Runnable() {

												@Override
												public void run() {

													clearCookies();
												}
											});
											clear.start();
										}
									})
							.setNegativeButton(getResources().getString(R.string.action_no),
									new DialogInterface.OnClickListener() {

										@Override
										public void onClick(DialogInterface arg0, int arg1) {

										}
									}).show();
					return true;
				}
			});

			/**
			 * Clear Cache
			 */
			Preference mClearCache = findPreference("clearCache");
			mClearCache.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

				@Override
				public boolean onPreferenceClick(Preference mPref) {

					clearCache();
					// TODO Auto-generated method stub
					return false;
				}
			});

			/**
			 * Licenses
			 */
			Preference mLicenses = findPreference("licenses");
			mLicenses.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

				@Override
				public boolean onPreferenceClick(Preference mPref) {

					final LicensesDialogFragment fragment = LicensesDialogFragment.newInstance(R.raw.notices, false);
					fragment.show(getChildFragmentManager(), null);
					// TODO Auto-generated method stub
					return false;
				}
			});
		}

		@SuppressWarnings("deprecation")
		public void clearHistory() {

			deleteDatabase(HistoryDatabaseHandler.DATABASE_NAME);
			WebViewDatabase m = WebViewDatabase.getInstance(getActivity());
			m.clearFormData();
			m.clearHttpAuthUsernamePassword();
			if (API < 18) {
				m.clearUsernamePassword();
				WebIconDatabase.getInstance().removeAllIcons();
			}
			if (mSystemBrowser) {
				try {
					Browser.clearHistory(getContentResolver());
				} catch (Exception ignored) {
				}
			}
			SettingsController.setClearHistory(true);
			Utils.trimCache(getActivity());
			messageHandler.sendEmptyMessage(1);
		}

		@SuppressWarnings("deprecation")
		public void clearCookies() {

			CookieManager c = CookieManager.getInstance();
			CookieSyncManager.createInstance(getActivity());
			c.removeAllCookie();
			messageHandler.sendEmptyMessage(2);
		}

		public void clearCache() {

			WebView webView = new WebView(getActivity());
			webView.clearCache(true);
			webView.destroy();
			Utils.showToast(getActivity(), getResources().getString(R.string.message_cache_cleared));
		}
	}

	private static class MessageHandler extends Handler {

		Context	mHandlerContext;

		public MessageHandler(Context context) {

			this.mHandlerContext = context;
		}

		@Override
		public void handleMessage(Message msg) {

			switch (msg.what) {
			case 1:
				Utils.showToast(mHandlerContext,
						mHandlerContext.getResources().getString(R.string.message_clear_history));
				break;
			case 2:
				Utils.showToast(mHandlerContext,
						mHandlerContext.getResources().getString(R.string.message_cookies_cleared));
				break;
			}
			super.handleMessage(msg);
		}
	}
}
