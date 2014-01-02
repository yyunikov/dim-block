package com.yyunikov.dimblock.ui;

import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import com.yyunikov.dimblock.R;
import com.yyunikov.dimblock.controller.DimPreferenceController;
import com.yyunikov.dimblock.service.DimBlockService;

/**
 * Author: yyunikov
 * Date: 12/19/13
 */
public class DimPreferenceActivity extends ActionBarActivity{

    private static final String LOG_TAG = "DimPreferenceActivity";

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

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            // Load the preferences from an XML resource
            addPreferencesFromResource(R.xml.preference_activity_dim);

            initialize();
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

            return true;
        }

        /**
         * Fragment initialization.
         */
        private void initialize() {
            dimPreferenceController = new DimPreferenceController(getActivity());

            final String displaySettingsKey = getString(R.string.key_pref_display_settings);
            final String dimBlockEnabledKey = getString(R.string.key_pref_dim_block_enabled);

            if (displaySettingsKey != null && dimBlockEnabledKey != null) {
                findPreference(displaySettingsKey).setOnPreferenceClickListener(this);
                findPreference(dimBlockEnabledKey).setOnPreferenceChangeListener(this);
            } else {
                Log.e(LOG_TAG, "Error: No preference key specified.");
            }

            ensureDimOff();
        }

        /**
         * Changes dim preference (on/off) and starts or stop dim block service.
         *
         * @param preference the dim preference
         */
        private void changeDimPreference(final SwitchPreference preference, boolean isSwitchedOn) {
            final Intent dimBlockIntent = new Intent(getActivity(), DimBlockService.class);
            if (isSwitchedOn) {
                getActivity().startService(dimBlockIntent);
            } else {
                getActivity().stopService(dimBlockIntent);
            }
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