package com.yyunikov.dimblock.ui;

import android.content.IntentFilter;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;

import com.yyunikov.dimblock.R;
import com.yyunikov.dimblock.base.Logger;
import com.yyunikov.dimblock.broadcast.BatteryLevelReceiver;
import com.yyunikov.dimblock.controller.DimPreferenceController;

/**
 * Author: yyunikov
 * Date: 12/19/13
 */
public class DimPreferenceActivity extends ActionBarActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Display the fragment as the main content.
        getFragmentManager().beginTransaction().replace(android.R.id.content,
                new DimPreferenceFragment()).commit();
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        final MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_dim_preference, menu);

        return super.onCreateOptionsMenu(menu);
    }

    /**
     * This fragment shows the preferences for dim block.
     */
    public static class DimPreferenceFragment extends PreferenceFragment implements Preference.OnPreferenceClickListener, Preference.OnPreferenceChangeListener {

        /**
         * Dim preference controller object.
         */
        private DimPreferenceController dimPreferenceController;

        private static final BatteryLevelReceiver batteryLevelReceiver = new BatteryLevelReceiver();

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            // Load the preferences from an XML resource
            addPreferencesFromResource(R.xml.preference_activity_dim);
            PreferenceManager.setDefaultValues(getActivity(), R.xml.preference_activity_dim, false);

            initialize();
        }

        @Override
        public void onResume() {
            super.onResume();
            ensureDimOff();
        }

        @Override
        public boolean onPreferenceClick(Preference preference) {
            final String preferenceKey = preference.getKey();
            // if device display settings clicked
            if (preferenceKey != null && preferenceKey.equals(getString(R.string.key_pref_display_settings))) {
                dimPreferenceController.openDisplaySettings();
            }

            return true;
        }

        @Override
        public boolean onPreferenceChange(Preference preference, Object o) {
            final String preferenceKey = preference.getKey();

            // If dim enabled preference clicked
            if (preferenceKey != null && preferenceKey.equals(getString(R.string.key_pref_dim_block_enabled))) {
                changeDimPreference((SwitchPreference) preference, (Boolean) o);
            }

            // If disable on battery low preference clicked
            if (preferenceKey != null && preferenceKey.equals(getString(R.string.key_pref_unblock_battery))) {
                if (o.equals(Boolean.TRUE)) {
                    final IntentFilter filter = new IntentFilter();
                    filter.addAction("android.intent.action.BATTERY_LOW");
                    getActivity().registerReceiver(batteryLevelReceiver, filter);
                } else {
                    try {
                        getActivity().unregisterReceiver(batteryLevelReceiver);
                    } catch (final IllegalArgumentException e) { // If receiver is not registered yet
                        Logger.debug("Receiver is not registered yet."); // TODO remove
                    }
                }
            }

            return true;
        }

        /**
         * Fragment initialization.
         */
        private void initialize() {
            dimPreferenceController = new DimPreferenceController(getActivity());

            final String displaySettingsKey = getString(R.string.key_pref_display_settings);
            final String dimBlockEnabledKey = getString(R.string.key_pref_dim_block_enabled);
            final String unBlockOnBatteryLowKey = getString(R.string.key_pref_unblock_battery);

            if (displaySettingsKey != null && dimBlockEnabledKey != null) {
                findPreference(displaySettingsKey).setOnPreferenceClickListener(this);
                findPreference(dimBlockEnabledKey).setOnPreferenceChangeListener(this);
                findPreference(unBlockOnBatteryLowKey).setOnPreferenceChangeListener(this);
            } else {
                Logger.error("Error: No preference key specified.");
            }
        }

        /**
         * Changes dim preference (on/off) and starts or stop dim block service.
         *
         * @param preference the dim preference
         */
        private void changeDimPreference(final SwitchPreference preference, boolean isSwitchedOn) {
            dimPreferenceController.setDimEnabled(isSwitchedOn);
        }

        /**
         * Checks if dim lock is held and sets preference off if not.
         */
        private void ensureDimOff() {
            final String dimBlockEnabledKey = getString(R.string.key_pref_dim_block_enabled);
            if (!dimPreferenceController.isDimBlocked()) {
                ((SwitchPreference) findPreference(dimBlockEnabledKey)).setChecked(false);
            }
        }
    }
}