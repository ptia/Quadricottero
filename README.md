## Quadricottero - An Arduino quadcopter

Quadricottero is an Arduino-based quadcopter flight controller. 
Its main features are:
* Acrobatic and stabilized flight mode
* Serial (BlueTooth) settings manager for enabling log, changing PID constants, and so on
* EEPROM saved PID constants
* Time-saving, interrupt-driven radio reading
* Some basic safety features

I wrote most of the code by myself, but I used external libraries for interfacing with the MPU-6050 and wrote my own library for RX based on PCint Library. The PID library used is from this awesome quadcopter flight controller: https://github.com/baselsw/BlueCopter
