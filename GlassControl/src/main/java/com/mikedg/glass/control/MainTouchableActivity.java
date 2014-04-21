/*
Copyright 2013 Michael DiGiovanni glass@mikedg.com

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
package com.mikedg.glass.control;

import android.content.pm.PackageManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.format.Formatter;
import android.view.*;
import android.widget.AdapterView;
import android.widget.TextView;
import com.google.android.glass.view.MenuUtils;
import com.google.android.glass.widget.CardScrollAdapter;
import com.google.android.glass.widget.CardScrollView;

import java.util.ArrayList;

public class MainTouchableActivity extends MainActivity implements MainPresentationModel.OnModelChangedListener {
    private ArrayList<View> mCards;
    private View enablerCard;
    private View statusCard;
    private CardScrollView mCardScrollView;
    private View tiltStartSettingCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        createCards();

        getPresentationModel().setOnCommandsChangedListener(this);

        createScrollView();

        setContentView(mCardScrollView);

        //GlassControlService.launch(this);
    }

    Menu mMenu;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings_start_tilt_off:
                Prefs.getInstance().setTiltStartEnabled(false);
                onCommandsChanged();
                return true;
            case R.id.action_settings_start_tilt_on:
                Prefs.getInstance().setTiltStartEnabled(true);
                onCommandsChanged();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void changeStartTiltSetting() {
        openOptionsMenu();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        mMenu.clear();
        //This is such a silly, lazy process
        getMenuInflater().inflate(R.menu.start_tilt_options, mMenu);

        MenuItem selectedItem = menu.findItem(Prefs.getInstance().getTiltStartEnabled() ? R.id.action_settings_start_tilt_off: R.id.action_settings_start_tilt_on);
        MenuUtils.setInitialMenuItem(menu, selectedItem);
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        mMenu = menu;
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onCommandsChanged() {
        enablerCard.setTag(getPresentationModel().getEnablerCommand());
        ((TextView) enablerCard.findViewById(R.id.textView1)).setText(getString(getPresentationModel().getEnablerCommand()));
    }

    @Override
    public void onPrefsChanged() {
        populateTiltStartSettingValue();
    }

    private void createScrollView() {
        mCardScrollView = new CardScrollView(this);
        MainCardScrollAdapter adapter = new MainCardScrollAdapter();
        mCardScrollView.setAdapter(adapter);
        mCardScrollView.activate();

        mCardScrollView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                onItemSelected((int) id);
            }
        });
    }

    private void createCards() {
        mCards = new ArrayList<View>();

        View card;
        LayoutInflater inflater = LayoutInflater.from(this);

//        card = inflater.inflate(R.layout.card_title, null);
//        ((TextView) card.findViewById(R.id.textView_ip)).setText(getIpAddress());
//        mCards.add(card);
//        statusCard = card;
//        statusCard.setTag(-1);

        //Setup the title card
        card = inflater.inflate(R.layout.card_title, null);
        ((TextView) card.findViewById(R.id.textView_ip)).setText(getIpAddress());
        try {
            String version = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
            L.d("Version: " + version);
            ((TextView) card.findViewById(R.id.textView_version)).setText(getPackageManager().getPackageInfo(getPackageName(), 0).versionName);
        } catch (PackageManager.NameNotFoundException e) {
            ((TextView) card.findViewById(R.id.textView_version)).setText("unknown");
            e.printStackTrace();
        }
        mCards.add(card);
        enablerCard = card;

        //Setup the card to change your settings for
        card = inflater.inflate(R.layout.card_start_tilt_setting, null);
        mCards.add(card);
        tiltStartSettingCard = card;
        tiltStartSettingCard.setTag(R.layout.card_start_tilt_setting); //Always this, doesn't change, or should it?
        populateTiltStartSettingValue();
    }

    private void populateTiltStartSettingValue() {
        ((TextView) tiltStartSettingCard.findViewById(R.id.textView_setting_title_value)).setText(getPresentationModel().getTiltStartSettingText());
    }

    private class MainCardScrollAdapter extends CardScrollAdapter {
        @Override
        public int getCount() {
            return mCards.size();
        }

        @Override
        public Object getItem(int position) {
            return mCards.get(position);
        }

        @Override
        public long getItemId(int position) {
            //FIXME: toView() obviously recreates the view, so tags are missing
//            getTag is null, wtf... maybe oncommandaschanged not called before this now? correct, not sure it ever was happneing before though
            return ((Integer)((View) getItem(position)).getTag()).longValue();
        }

        @Override
        public int getPosition(Object o) {
            return mCards.indexOf(o);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return mCards.get(position);
        }
    }

    private String getIpAddress() {
        WifiManager wifiMgr = (WifiManager) getSystemService(WIFI_SERVICE);
        WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
        int ip = wifiInfo.getIpAddress();
        String ipAddress = Formatter.formatIpAddress(ip); //FIXME: this is deprecated
        return ipAddress;
    }
}
