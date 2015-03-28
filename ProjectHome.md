This software connects to Bluetooth-enabled Inertial Measurement Units (IMUs) attached to different limbs of the human body. It displays a stick-model and moves it according to the data received over Bluetooth.

Different body junctions such as the wrist can be tracked and the gestures performed by those points can be recognized using the Android Gesture recognition.

A suitable IMU is the Razor [IMU available at Sparkfun](http://www.sparkfun.com/commerce/product_info.php?products_id=9623), which can connect over the [Bluetooth Mate](http://www.sparkfun.com/commerce/product_info.php?products_id=9358).
It should be loaded with the SF9DOF\_ARHS firmware [available here](http://code.google.com/p/sf9domahrs/source/list).

Hint: You can give the Bluetooth Mate modules a name by hitting "$$$" (without quotes) on a terminal on the COM port. This will get you into configuration mode, where you can set a new device name using e.g. "SN,BodyTrackerRUA". With "---" you get back to the serial data.