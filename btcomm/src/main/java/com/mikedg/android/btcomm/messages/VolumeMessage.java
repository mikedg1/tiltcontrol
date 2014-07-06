package com.mikedg.android.btcomm.messages;//package com.mikedg.tiltcontrolcontroller.common.messages;
//
//import android.content.Context;
//import android.media.AudioManager;
//
//import org.json.JSONException;
//import org.json.JSONObject;
//
///**
// * Created by Michael on 12/15/13.
// */
//public class VolumeMessage extends PTGCMessage {
//    public static final String VAL_TYPE = "volume";
//    private static final String KEY_DIRECTION = "direction";
//    private final int mDirection;
//
//    public VolumeMessage(int direction) {
//        mDirection = direction;
//    }
//
//    public VolumeMessage(JSONObject object) throws JSONException {
//        this(object.getInt(KEY_DIRECTION));
//    }
//
//    public String getType() {
//        return VAL_TYPE;
//    }
//
//    @Override
//    public JSONObject getPayload() {
//        JSONObject object = new JSONObject();
//        //No payload
//        try {
//            object.put(KEY_DIRECTION, mDirection);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        return object;
//    }
//
//    @Override
//    public void doMyThing(Context context) {
//        AudioManager manager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
//        manager.adjustVolume(mDirection, AudioManager.FLAG_PLAY_SOUND | AudioManager.FLAG_SHOW_UI);
//    }
//}
