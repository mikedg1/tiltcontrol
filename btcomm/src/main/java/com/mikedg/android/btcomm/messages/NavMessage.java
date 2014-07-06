package com.mikedg.android.btcomm.messages;//package com.mikedg.tiltcontrolcontroller.common.messages;
//
//import android.content.Context;
//import android.content.Intent;
//import android.net.Uri;
//
//import com.mikedg.android.glass.ptgc.glass.ScreenHelper;
//
//import org.json.JSONException;
//import org.json.JSONObject;
//
///**
// * Created by Michael on 12/15/13.
// */
//public class NavMessage extends PTGCMessage {
//    private static final String QUERY_STRING = "query";
//    public static final String VAL_TYPE = "nav";
//    private final String mQueryString;
//
//    public NavMessage(String queryString) {
//        mQueryString = queryString;
//    }
//
//    public NavMessage(JSONObject object) throws JSONException {
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
//        i.setPackage("com.google.glass.maps");
//        i.setClassName("com.google.glass.maps", "com.google.glass.maps.NavigationActivity");
//        i.setAction(Intent.ACTION_VIEW);
//
//        i.setData(Uri.parse("google.navigation:q=" + getQueryString()));
//        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//
//        context.startActivity(i);
//    }
//}
