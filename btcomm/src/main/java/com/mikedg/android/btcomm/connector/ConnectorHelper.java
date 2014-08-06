package com.mikedg.android.btcomm.connector;

import com.mikedg.android.btcomm.messages.CommandMessage;
import com.mikedg.android.btcomm.messages.PTGCMessage;
import com.mikedg.android.btcomm.messages.SimWinkMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

/**
 * Created by Michael on 6/30/2014.
 */
public class ConnectorHelper {
    private HashMap<String, Class<? extends PTGCMessage>> mRegisteredMessages = new HashMap<String, Class<? extends PTGCMessage>>();

    public ConnectorHelper() {
        registerMessages();
    }

    private void register(Class<? extends PTGCMessage> clazz) {
        try {
            String type = (String) clazz.getField("VAL_TYPE").get(null);
            mRegisteredMessages.put(type, clazz);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
    public Class getRegistedMessage(Object type){
        return mRegisteredMessages.get(type);
    }

    private void registerMessages() {
//        register(NavMessage.class);
//        register(MusicMessage.class);
//        register(CameraMessage.class);
//        register(VolumeMessage.class);
        register(CommandMessage.class);
        register(SimWinkMessage.class);
    }
    public PTGCMessage getPtgcFromBytes(byte[] buffer) {
        try{
            PTGCMessage ptgcMessage = null;
            String readMessage = new String(buffer, 0, buffer.length);
            JSONObject object = new JSONObject(readMessage);
            JSONObject payload = object.getJSONObject(PTGCMessage.KEY_PAYLOAD);
//            fixme make generic with registration
            Class clazz = getRegistedMessage(object.get(PTGCMessage.KEY_TYPE));
            if (clazz != null) {
                Constructor<?> constructor = clazz.getConstructor(JSONObject.class);
                ptgcMessage = (PTGCMessage) constructor.newInstance(payload);
            }
            return ptgcMessage;
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }
}
