package com.mikedg.android.btcomm.messages;//package com.mikedg.tiltcontrolcontroller.common.messages;
//
//import android.content.Context;
//import android.content.Intent;
//
//import com.mikedg.android.glass.ptgc.glass.ScreenHelper;
//
//import org.json.JSONException;
//import org.json.JSONObject;
//
///**
// * Created by Michael on 12/15/13.
// */
//public class CameraMessage extends PTGCMessage {
//    public static final String VAL_TYPE = "camera";
//
//    public CameraMessage() {
//    }
//
//    public CameraMessage(JSONObject object) throws JSONException {
//        this();
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
//        return object;
//    }
//
//    @Override
//    public void doMyThing(Context context) {
////        12-16 12:54:02.488    189-26811/? I/ActivityManager﹕ START {act=com.google.glass.action.TAKE_PICTURE flg=0x18000000 cmp=com.google.glass.camera/.TakePictureActivity (has extras)} from pid 560
//
//
//        ScreenHelper.turnOnScreen(context);
////        Intent i = new Intent("com.google.glass.action.TAKE_PICTURE"); //Seems to be pretty damn quick! but doesnt turn screen on, so maybe take from screen off then this?
//        Intent i = new Intent("com.google.glass.action.TAKE_PICTURE_FROM_SCREEN_OFF");
//        i.putExtra("should_finish_turn_screen_off", true);
////        i.putExtra("should_play_initial_sound", true);
//        i.putExtra("should_take_picture", true);
//
////        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//i.setFlags(0x18000000);
//        //None of tyhis shit works!!!
//
////        Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
////        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        //Need to tap to accept and keep
//        context.startActivity(i); //FIXME: capture, no activity to handle
//    }
//}
