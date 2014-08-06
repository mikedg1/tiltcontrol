package com.mikedg.android.btcomm.messages;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Michael on 12/15/13.
 */
public abstract class PTGCMessage {
    //Should probably have just used GSON and pojo deserialization, oh well

    public static final String KEY_TYPE = "type";
    public static final String KEY_PAYLOAD = "payload";

//    private JSONObject payload;
//    private JSONObject jsonRepresentation;
//    private String mType;

//    public Message(String string) throws JSONException {
//        jsonRepresentation = new JSONObject(string);
//        mType = getType();
//    }

    public byte[] getTypedBytes() throws JSONException {
        JSONObject object = new JSONObject();
        object.put(KEY_TYPE, getType());
        object.put(KEY_PAYLOAD, getPayload());
        return object.toString().getBytes();
    }
    
    public abstract String getType();
    public abstract JSONObject getPayload();
}
