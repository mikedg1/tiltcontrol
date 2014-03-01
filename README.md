tilt control
===========
Tilt Control is designed to give access to hands-free and voice-free navigation of the entire Glass experience. With just voice, usage of Glass is greatly limited to a small subset of commands that are not available from all areas of the Glass interface.

##Why do we need this?
- This gives users with motor disabilities full access to Glass functionality in the many cases where voice commands are not available.

##Installation
- This assumes that you have a complete development computer setup and are familiar with ADB usage.
- This also assumes that you have setup Glass in debug mode and have verified that you can connect to it from ADB.
- Enable and calibrate the "Wink for picture" setting in the Glass settings screen. Try this a few times, make sure it's comfortable, since this is how we will trigger Tilt Control usage.
- Build the APK from the source code.
- Install the APK as you normally would and verify that the "Tilt Control" command shows up in the "Ok, Glass" menu.

##Setup
- Run the following ADB command, "adb tcpip 5555"
 - This tells ADB to restart on Glass so it can be accessed by TCP on port 5555. The port was selected since it's a port that we can get to from a normal Android app. If you need to deactivate this, the easiest way is to restart Glass. Once you run this command you will not be able to connect to Glass via ADB over USB until this is deactivated. You will need to run this command any time Glass is restarted if you wish to enable Tilt Control. I've previously had ways of enabling this through the use of a rooted Android phone connected to your Glass device, but it's not a publicly available project yet. 

##Use
- We have 2 ways to execute Tilt Control. If the "adb tcpip 5555" command was successful, running tilt control should automatically start the necessary connections to make things work.
 - "Ok, Glass, Tilt Control, Touch Setup" will allow you to setup Tilt Control by using the touchpad on the side of Glass. This is the recommended way to setup Tilt Control if you have any mobility in your hands. You should momentarily see a start command upon running this, and it will change to a stop command and stay that way if everything is good. If it does not change to stop, you need to try the "adb tcpip 5555" command again.
 - "Ok, Glass, Tilt Control, Voice Setup" will allow you to setup Tilt Control by using your voice. This can be problematic due to the requirement of an internet connection and due to limitations of the GDK voice recognition in Glass. However, this is your only alternative if you can not control Glass via the touchpad. At this point, I do not recommend using this as it has had veyr limited testing and is a generally bad interface.
- Once Tilt Control is installed and enabled, winking while the screen of Glass is off, will turn on the screen an allow you to use Tilt Control to control Glass. A wink while the screen is already on, will not activate Tilt Control.
- Tilt your head to the right or left to move the timeline in that direction. You should hear a little knock from the speaker right before it moves.
- There's 2 degrees of tilt activation to either side before you have to reset your head in the center.
- Look down to simulate the back button.
- Wink again to select something. This is potentially destructive, so Winking tends to be the best way of selecting stuff, because it's not easily activated accidentally.

- If you have problems, you can try backing out of the Tilt Control app, then reopening it. You might try tapping in the app to start/stop it or try giving a voice command to enable/disable it(experimental).

##Technical Considerations
- Tilt Control uses developer tools. After every Glass device restart, Tilt Control needs access to a computer to run setup before it will work again.
- Uses really basic accelerometer data so it turns into a mess while walking. Patches welcome :)
- It only activates via a wink, it will not activate if you turn Glass on with a head nod or tap to the side. This just makes it less annoying for development purposes, but it's not a technical limitation.

##How did you do this?
Glass is like a typical Android device, which means developer tools are capable of doing some extra things that you can't do with a normal app. Tilt Control interfaces with ADB over TCP and sends movement commands to simulate navigation around the Glass interface by the use of the accelerometer.

##Who is Mike DiGiovanni?  
Mike is the Emerging Technology Lead at Isobar. Mike has interests in all areas of mobile development and wearable computing.
Isobar is a global digital marketing agency with a total of 63 offices in 37 markets worldwide
Learn more about Isobar at http://www.isobar.com/us/about

---
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