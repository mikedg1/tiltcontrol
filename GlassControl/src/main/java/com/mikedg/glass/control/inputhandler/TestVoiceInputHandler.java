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
package com.mikedg.glass.control.inputhandler;

import android.speech.tts.TextToSpeech;
import com.mikedg.glass.control.TiltControlApplication;

//Other implementations could be an implementation that requires root
//A system signed API level implementation
public class TestVoiceInputHandler extends BaseInputHandler {
    private TextToSpeech mTextToSpeech;

    public TestVoiceInputHandler() {
        onStateChanged(OnStateChangedListener.State.NOT_READY);
    }

    public void start() {
        mTextToSpeech = new TextToSpeech(TiltControlApplication.getContext(), new TextToSpeech.OnInitListener() {

            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    onStateChanged(OnStateChangedListener.State.READY);
                } else {
                    onStateChanged(OnStateChangedListener.State.CATASTROPHIC_FAILURE);
                }
            }
        });
    }

    public void stop() {
        onStateChanged(OnStateChangedListener.State.NOT_READY);

        mTextToSpeech.stop();
    }

    public void select() {
        mTextToSpeech.speak("select", TextToSpeech.QUEUE_ADD, null);
    }

    public void left() {
        mTextToSpeech.speak("left", TextToSpeech.QUEUE_ADD, null);
    }

    public void right() {
        mTextToSpeech.speak("right", TextToSpeech.QUEUE_ADD, null);
    }

    public void back() {
        mTextToSpeech.speak("back", TextToSpeech.QUEUE_ADD, null);
    }
}
