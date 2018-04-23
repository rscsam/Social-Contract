# Social Contract

This is the Android version of an application that integrates 
many types of social media into one application that then
allows users to gain following and follow new people
utilizing a centralized virtual currency called "coins."

The code for the Social Contract Android client is located at https://github.com/rscsam/Social-Contract
The code for the Social Contract server is located at https://github.com/rscsam/Social-Contract-Server

<h2>Release Notes version SocialContract 1.0</h2>

<h4>Features in this release</h4>
<ul>
  <li>Added a “Forgot Password” feature</li>
  </ul>

<h4>Bug fixes</h4>
<ul>
  <li>Application no longer crashes on Twitter call if there is no active Twitter session</li>
  <li>Fixed incorrect text on account management screen</li>
  <li>Grow keyboard no longer stays on screen if a follow is requested</li>
  <li>Fixed bug where account profile picture and name did not match account ID</li>
</ul>
<h4>Known Bugs</h4>
<ul>
  <li>Keyboard stays open when switching between certain fragments</li>
  <li>In some conditions, Discover will display no content even when applicable content exists</li>
</ul>
<h2>Installation</h2>
Social Contract uses a client-server architecture, meaning the installation will be twofold.
<h3>Android client</h3>
<ol>
<li>Download and install Android Studio.
You can follow the instructions here: <a>https://developer.android.com/studio/install.html</a></li>
<br />
<li>Download the zip file from the Social-Contract github.</li>
<li>From Android Studio, open the extracted folder.
<li>Once you have opened the project, you should be able to build and install.
Click the "Play" button on the Android Studio toolbar.</li>
<li>Select the device you would like to install onto.  You can either choose an emulator
or your own Android device, connected to the computer via USB.  If you are using a real device,
you should ensure that USB debugging is enabled.  You may accomplish this by following
this guide: https://developer.android.com/studio/debug/dev-options.html
</ol>
<h3>Server</h3>
The server is a Node.js server that is built to run on an Amazon aws instance.
There is an existing instance which will host the Social Contract server
until August 2018.  If using before this date, it will suffice to just install
the Android app.  If using after, follow the following steps to setup your own instance.

<ol>
<li>Sign up for and create an Amazon ec2 instance, following the official Amazon
Web Services instructions: https://docs.aws.amazon.com/efs/latest/ug/gs-step-one-create-ec2-resources.html</li>
<li>SSH into your instance via the instructions provided by AWS</li>
<li>Ensure git and npm are installed on your instance.  This process depend on what type of instance you have</li>
<li>Clone the server repository onto the instance using the following command: git clone https://github.com/rscsam/Social-Contract-Server.git</li>
<li>Run the command: npm i</li>
<li>Once the repository is cloned, start the server by running the command: node index.js</li>
<li>Now, you must edit one file in the Android client so it knows where your server is located.
Open src/main/java/jd7337/socialcontract/controller/delegate/ServerDelegate.java and change line 28
to indicate the proper DNS server location for your new AWS instance.</li>
</ol>
Once you have completed these steps, you should be able to connect to your own
copy of the server via the Android app!
<h3>Installation troubleshooting</h3>
<ul>
<li>If you set up your own AWS instance and are unable to connect to the Android app, ensure
that you rebuilt (pressed "play") the project after editing the line</li>
<li>If you are still unable to connect after trying the previous step, it is possible that you have added IP restrictions
to your server, preventing access.  Go to your EC2 dashboard and edit your
security groups to allow the IP address of your Android device to connect</li>
<li>If you are unable to get the app to install on your Android device, ensure
that your device runs Android api version 21+.  Older versions of Android
cannot run Social Contract</li>