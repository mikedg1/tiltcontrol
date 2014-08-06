package com.mikedg.android.btcomm.messages;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Michael on 6/30/2014.
 */
public class SimWinkMessage extends PTGCMessage {
    public static final String VAL_TYPE = "sim_wink";
    public SimWinkMessage() {

    }
    public SimWinkMessage(JSONObject object) throws JSONException {
        this();
    }

    public String getType() {
        return VAL_TYPE;
    }

    @Override
    public JSONObject getPayload() {
        JSONObject object = new JSONObject();
        //No payload
        return object;
    }

}
