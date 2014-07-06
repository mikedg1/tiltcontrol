package com.mikedg.android.btcomm.messages;//package com.mikedg.tiltcontrolcontroller.common.messages;
//
//import android.content.Context;
//import android.content.Intent;
//import android.speech.RecognizerIntent;
//
//import com.mikedg.android.glass.ptgc.glass.ScreenHelper;
//
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.util.ArrayList;
//
///**
// * Created by Michael on 12/15/13.
// */
//public class MusicMessage extends PTGCMessage {
//    private static final String QUERY_STRING = "query";
//    public static final String VAL_TYPE = "music";
//    private final String mQueryString;
//
//    public MusicMessage(String queryString) {
//        mQueryString = queryString;
//    }
//
//    public MusicMessage(JSONObject object) throws JSONException {
//        this(object.getString(QUERY_STRING));
//    }
//
//    public String getQueryString() {
//        return mQueryString;
//    }
//
//    public String getType() {
//        return VAL_TYPE;
//    }
//
//    @Override
//    public JSONObject getPayload() {
//        JSONObject object = new JSONObject();
//        try {
//            object.put(QUERY_STRING, mQueryString);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//        return object;
//    }
//
//    @Override
//    public void doMyThing(Context context) {
//        ScreenHelper.turnOnScreen(context);
//
//        Intent i = new Intent();
//        i.setPackage("com.google.glass.musicplayer");
//
//        i.setClassName("com.google.glass.musicplayer", "com.google.glass.musicplayer.ListenToQueryActivity");
//        i.setAction("com.google.android.glass.action.VOICE_TRIGGER");
//
//        ArrayList<String> results = new ArrayList<String>();
//        results.add(getQueryString());
//        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//
//        i.putExtra(RecognizerIntent.EXTRA_RESULTS, results); //Maybe wrong intent stuff?
//        context.startActivity(i); //FIXME: capture, no activity to handle
//    }
//}
