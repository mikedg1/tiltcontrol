package com.mikedg.android.btcomm.tiltcontrol;

import com.mikedg.android.btcomm.messages.PTGCMessage;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Michael on 6/24/2014.
 */
public class TiltControlEvents {
    public static final class LeftEvent extends PTGCMessage {
        public static final String VAL_TYPE = "left";

        public LeftEvent(JSONObject object) throws JSONException {
            this();
        }

        public LeftEvent() {

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

    public static final class RightEvent extends PTGCMessage {
        public static final String VAL_TYPE = "left";

        public RightEvent(JSONObject object) throws JSONException {
            this();
        }

        public RightEvent() {

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

    public static final class TapEvent extends PTGCMessage {
        public static final String VAL_TYPE = "left";

        public TapEvent(JSONObject object) throws JSONException {
            this();
        }

        public TapEvent() {

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

    public static final class DownEvent extends PTGCMessage {
        public static final String VAL_TYPE = "left";

        public DownEvent(JSONObject object) throws JSONException {
            this();
        }

        public DownEvent() {

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

}
