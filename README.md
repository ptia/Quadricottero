# Quadricottero - An Arduino quadcopter with an Android client

##Arduino sketch
Its main features are:
* Acrobatic and stabilized flight mode
* Serial (bluetooth) settings manager for enabling log, changing PID constants, and so on
* Easily editable, EEPROM saved PID constants
* Android client
* Time-saving, interrupt-driven radio reading
* Some basic safety features

I personally wrote most of the code, but I used external libraries to interface with the MPU-6050 and wrote my own library for interrupt-driven pulseIn (used to read radio signal) based on PCint Library. The PID library used is this one: https://github.com/br3ttb/Arduino-PID-Library/

##Android app
Its features are:
* Basic serial terminal (send and receive data)
* Quadcopter log save in .txt files
* Graphic and easy to use remote settings manager to edit PID constants and change flight mode

This is its to-do list (sorted by predicted priority and ease):
* Better incoming log data represention, e.g. graphics
* Mobile, bluetooth-driven direct control of the quadcopter

The code is mainly written by me, but it is based on this stackoverflow answer: http://stackoverflow.com/questions/10327506/android-arduino-bluetooth-data-transfer

##Components used
The whole project is ment to be as DIY and cheap as possible.

Controller electronics:
* Original Arduino UNO
* 6 channels radio controller and receiver €21.34: http://hobbyking.com/hobbyking/store/uh_viewItem.asp?idProduct=9042
* 6 axis IMU (MPU-6050) €5.00: http://www.ebay.it/itm/Modulo-GY-521-con-MPU-6050-Giroscopio-Accelerometro-3-assi-Arduino-6-DOF-MPU6050-/171075512717?pt=LH_DefaultDomain_101&hash=item27d4e5298d#shpCntId

Power electronics, propellers and frame:
* Kit of motors, propellers and frame €33.71: http://hobbyking.com/hobbyking/store/__28592__ST360_Quadcopter_Frame_w_Motors_and_Propellers_360mm.html
* Motor controller €25.43: http://www.hobbyking.com/hobbyking/store/__36674__q_brain_4_x_20a_brushless_quadcopter_esc_2_4s_3a_sbec.html
* Battery €11.48: http://www.hobbyking.com/hobbyking/store/__9394__Turnigy_2200mAh_3S_30C_Lipo_Pack.html?strSearch=Turnigy%202200mAh%203S%2030C%20Lipo%20Pa
* Battery charger €16.14: http://www.hobbyking.com/hobbyking/store/__11060__HobbyKing_ECO6_50W_5A_Balancer_Charger_w_accessories.html
