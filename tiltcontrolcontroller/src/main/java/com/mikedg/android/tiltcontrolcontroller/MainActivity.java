package com.mikedg.android.tiltcontrolcontroller;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.mikedg.android.btcomm.connector.ConnectorHelper;
import com.mikedg.android.btcomm.messages.CommandMessage;
import com.mikedg.android.btcomm.messages.PTGCMessage;

import org.json.JSONException;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.main, menu);
//        return true;
//    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//        if (id == R.id.action_settings) {
//            return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }

    public void onClick_start(View view) {
        ControllerService.startService(this);
    }
    public void onClick_stop(View view) {
        ControllerService.stopService(this);
    }

    public void onClick_left(View view) {
        Application.getBus().post(new ControlEvents.LeftEvent());
    }
    public void onClick_right(View view) {
        Application.getBus().post(new ControlEvents.RightEvent());
    }
    public void onClick_tap(View view) {
        Application.getBus().post(new ControlEvents.TapEvent());
    }
    public void onClick_down(View view) {
        Application.getBus().post(new ControlEvents.DownEvent());
    }

    public void onClick_loopTap(View view) {
        prepLoop(CommandMessage.Command.TAP.ordinal());

    }

    public void onClick_loopDown(View view) {
        prepLoop(CommandMessage.Command.DOWN.ordinal());

    }

    public void onClick_loopLeft(View view) {
        prepLoop(CommandMessage.Command.LEFT.ordinal());

    }

    public void onClick_loopRight(View view) {
        prepLoop(CommandMessage.Command.RIGHT.ordinal());
    }

    private void prepLoop(int command) {
        //make a new messgae;
        PTGCMessage message = new CommandMessage(command);

        //get the bytes;
        try {
            byte[] buffer = message.getTypedBytes();

            //recreate from bytes
            message = new ConnectorHelper().getPtgcFromBytes(buffer);

            //publish
            Application.getBus().post(message);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void onClick_appInfo(View view) {
        AppUtil.startMyGlassAppInfo(this);
    }

    public void onClick_simWink(View view) {
        Application.getBus().post(new SimWinkEvent());
    }
}
