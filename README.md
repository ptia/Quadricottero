## Quadricottero - An Arduino quadcopter with an Android client

###Arduino sketch
Its main features are:
* Acrobatic and stabilized flight mode
* Serial (bluetooth) settings manager for enabling log, changing PID constants, and so on
* Easily editable, EEPROM saved PID constants
* Android client
* Time-saving, interrupt-driven radio reading
* Some basic safety features

I personally wrote most of the code, but I used external libraries to interface with the MPU-6050 and wrote my own library for interrupt-driven pulseIn (used to read radio signal) based on PCint Library. The PID library used is from this awesome quadcopter flight controller: https://github.com/baselsw/BlueCopter

###Android app
Its features are:
* Basic serial terminal (send and receive data)
* Quadcopter log save in .txt files
* Graphic and easy to use remote settings manager to edit PID constants and change flight mode

This is its to-do list (sorted by predicted priority and ease):
* Better incoming log data represention, e.g. graphics
* Mobile, bluetooth-driven direct control of the quadcopter

The code is mainly written by me, but it is based on this stackoverflow answer: http://stackoverflow.com/questions/10327506/android-arduino-bluetooth-data-transfer
