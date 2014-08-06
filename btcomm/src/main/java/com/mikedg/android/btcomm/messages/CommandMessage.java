package com.mikedg.android.btcomm.messages;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Michael on 6/30/2014.
 */
public class CommandMessage extends PTGCMessage {
    public static final String VAL_TYPE = "command";
    private static final String KEY_COMMAND = "command";
    private final int mCommand;

    public enum Command { LEFT, RIGHT, TAP, DOWN }

    public CommandMessage(int command) {
        mCommand = command;
    }

    public CommandMessage(Command command) {
        mCommand = command.ordinal();
    }

    public CommandMessage(JSONObject object) throws JSONException {
        this(object.getInt(KEY_COMMAND));
    }

    public String getType() {
        return VAL_TYPE;
    }

    public Command getCommand() {
        return Command.values()[mCommand];
    }

    @Override
    public JSONObject getPayload() {
        JSONObject object = new JSONObject();
        try {
            object.put(KEY_COMMAND, mCommand);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

}
