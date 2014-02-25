/*
Copyright 2013 Michael DiGiovanni glass@mikedg.com

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
package com.mikedg.glass.control;

import android.util.Log;
import android.view.KeyEvent;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;

/**
 * Created by mdigiovanni on 10/24/13.
 */
//FIXME: pull out to an interface
//Other implementations could be an implementation that requires root
//A system signed API level implementation
public class AdbTcpInputHandler {
    private Process process;
    private BufferedWriter out;

    public static boolean isConnected = false; //Hacky thing to let us quickly check, just for UX purposes

    public AdbTcpInputHandler() {
        tryConnectingLocally();
    }

    private void tryOpeningAdb() {
        try {
            process = new ProcessBuilder(new String[]{"adb","-s","127.0.0.1:5555", "shell"}).start();
            out = new BufferedWriter(
                    new OutputStreamWriter(process.getOutputStream()));

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            try {
                int exit = process.exitValue();
                isConnected = false;
                throw new RuntimeException();
            } catch (IllegalThreadStateException ex) {
                //Hack
                //If we actually connected this should throw an exception so proceed
                //If we didn't connect, we manuall throw the exception to crash
            }
            //FIXME: for some reason the below hack only worked in debug mode :/
//            try {
//                //Hack to force an easy way to know if we connected or not
//                right();
//            } catch (RuntimeException ex) {
//                //If this happened we didn't connect correctly, so shut down service and update
//                isConnected = false;
//                //FIXME: shut down the service, maybe just send a broadcast saying failure?
//                //LocalBroadcastManager.getInstance().sendBroadcast();
//                throw ex; //FIXME: right now we just crash, since no effort to maintain state was made
//            }
            isConnected = true;
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed initial hack to check if we are actually connected to local ADB via TCP"); //FIXME: handle gracefully
        }
    }

    private void tryConnectingLocally() {
     Thread t = new Thread() {
            public void run() {
                try {
                    process = new ProcessBuilder(new String[]{"adb","connect", "127.0.0.1"}).start();
                    int exitValue = process.waitFor(); //Wait for this to finish, should be nearly instant
                    Log.d("test", "exitval:" + exitValue); // on success it's 0, on a failure, it's 0 too :(
                } catch (IOException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e); //FIXME: handle gracefully
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                tryOpeningAdb();
            }
        };
      t.start();
    }

    public void select() {
        try {
            out.write("input keyevent " + KeyEvent.KEYCODE_ENTER);
            out.newLine();
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e); //FIXME: handle gracefully
        }
    }

    public void left() {
        try {
            out.write("input keyevent " + KeyEvent.KEYCODE_DPAD_LEFT);
            out.newLine();
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e); //FIXME: handle gracefully
        }
    }

    public void right() {
        try {
            out.write("input keyevent " + KeyEvent.KEYCODE_DPAD_RIGHT);
            out.newLine();
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e); //FIXME: handle gracefully
        }
    }

    public void back() {
        try {
            out.write("input keyevent " + KeyEvent.KEYCODE_BACK);
            out.newLine();
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e); //FIXME: handle gracefully
        }
    }
}
