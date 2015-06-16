## Quadricottero - An Arduino quadcopter

Quadricottero is an Arduino-based quadcopter flight controller. 
Its main features are:
* Acrobatic and stabilized flight mode
* Serial (bluetooth) settings manager for enabling log, changing PID constants, and so on
* Easily editable, EEPROM saved PID constants
* Time-saving, interrupt-driven radio reading
* Some basic safety features
* Android client (beta)

I personally wrote most of the code, but I used external libraries to interface with the MPU-6050 and wrote my own library for interrupt-driven pulseIn (used to read radio signal) based on PCint Library. The PID library used is from this awesome quadcopter flight controller: https://github.com/baselsw/BlueCopter

The Android client is currently being developed. Its features are:
* Basic serial terminal (send and receive data)
* Quadcopter log save in .txt files

This is its to-do list (sorted by predicted priority and ease):
* Better settings manager graphical representation, e.g. charts for PID values
* Better incoming log data represention, e.g. graphics
* Mobile, bluetooth-driven direct control of the quadcopter

The code is mainly written by me, but it is based on this stackoverflow answer: http://stackoverflow.com/questions/10327506/android-arduino-bluetooth-data-transfer
