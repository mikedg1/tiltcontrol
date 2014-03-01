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

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

public class MainVoiceableActivity extends MainActivity {

    private HashMap<String, Integer> mCommandMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        createCommandMapping();
        displaySpeechRecognizer();
    }

    private void createCommandMapping() {
        mCommandMap = new HashMap<String, Integer>();
        addToMap(R.string.disable);
        addToMap(R.string.enable);
    }

    private void addToMap(int id) {
        mCommandMap.put(getString(id).toUpperCase(), id);
    }

    @Override
    protected void onCommandsChanged() {
        //Change the visual
        ((TextView) findViewById(R.id.textView_enabler)).setText(getPresentationModel().getEnablerCommand());
    }

    private static final int SPEECH_REQUEST = 12345;

    private void displaySpeechRecognizer() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        startActivityForResult(intent, SPEECH_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        if (requestCode == SPEECH_REQUEST && resultCode == RESULT_OK) {
            List<String> results = data.getStringArrayListExtra(
                    RecognizerIntent.EXTRA_RESULTS);
            if (results.size() > 0) {
                String spokenText = results.get(0);
                handleCommandFromText(spokenText);
            } else {
                L.d("Voice came back with no results");
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void handleCommandFromText(String spokenText) {
        //FIXME: Fuzzy mapping
        Object command = mCommandMap.get(spokenText.toUpperCase());
        if (command != null) {
            onItemSelected((Integer) command);
        }
    }
}
