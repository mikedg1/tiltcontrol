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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;
import com.google.android.glass.widget.CardScrollAdapter;
import com.google.android.glass.widget.CardScrollView;

import java.util.ArrayList;

public class MainTouchableActivity extends MainActivity implements MainPresentationModel.OnCommandsChangedListener {
    private ArrayList<View> mCards;
    private View enablerCard;
    private View statusCard;
    private CardScrollView mCardScrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        createCards();
        createScrollView();

        setContentView(mCardScrollView);

        getPresentationModel().setOnCommandsChangedListener(this);
        //GlassControlService.launch(this);
    }

    @Override
    public void onCommandsChanged() {
        enablerCard.setTag(getPresentationModel().getEnablerCommand());
        ((TextView) enablerCard.findViewById(R.id.textView1)).setText(getString(getPresentationModel().getEnablerCommand()));
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
        enablerCard.setTag(-1);
    }

    private class MainCardScrollAdapter extends CardScrollAdapter {
        @Override
        public int findIdPosition(Object id) {
            return -1;
        }

        @Override
        public int findItemPosition(Object item) {
            return mCards.indexOf(item);
        }

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
            return ((Integer)((View) getItem(position)).getTag()).longValue();
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
