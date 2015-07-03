#include <PID_v1.h>

#include <EEPROM.h>
#include <PCintPulseIn.h>
#include <Servo.h>
#include "I2Cdev.h"
#include "MPU6050_6Axis_MotionApps20.h"
#include "Wire.h"

//#######RX######

/*Arrays related to RX channels,
* always sorted this way:
* THROTTLE, PITCH, ROLL, YAW, POT_A
*/
const int RXpins[] = {A0, A1, A2, A3, 8};
const int signalMin[] = {1172, 1176, 1160, 1132, 2008};
const int signalMax[] = {1816, 1816, 1944, 1940, 996};
int16_t throttle;
bool throttleLock, lowThrottle;
#define THROTTLE_MAX 1000

//###SETPOINTS###

/*Array of Pitch-Roll-Yaw
* angular velocities to reach
*/
float acroSet[3];
/*Array of Pitch-Roll inclinations
* (relative to ground) to reach
*/
float stabilizeSet[2];
//Max and min for angular velocity
#define PR_ACRO_MAX 45
#define YAW_MAX 10
//Max and min for inclination
#define PR_ANGLE_MAX 45


//######IMU######

//Array of IMU data, sorted Pitch-Roll(-Yaw)
float gyro[3];
float angle[2];
float angleOffset[2];


//#####MOTORS#####

/*Array of Servo objects for the
* motors. Always sorted counter-
* clockwise from top left
*/
Servo motors[4];

const int motorPins[] = {3, 4, 5, 6};

/*Max delta of motor speed.
* Notice that every delta is
* double, as it is applied twice
*/
#define MAX_MOTOR_DELTA 100
//Delta values for PRY
float rawPRY[3];

#define MOTOR_MIN_SPEED 1100
#define MOTOR_ARM 1000
//Speeds of single motors
int speeds[4];


//######PID######

/*Following costants are
* sorted this way:
* Pitch: P, I, D
* Roll: P, I, D
* (Yaw: P, I, D)
*/
float acroPIDk[9];
float stabilizePIDk[6];

//Array of PIDs PITCH, ROLL(, YAW)
PID acroPID[] = {
  PID(&gyro[0], &rawPRY[0], &acroSet[0], acroPIDk[0], acroPIDk[1], acroPIDk[2], REVERSE),
  PID(&gyro[1], &rawPRY[1], &acroSet[1], acroPIDk[3], acroPIDk[4], acroPIDk[5], REVERSE),
  PID(&gyro[2], &rawPRY[2], &acroSet[2], acroPIDk[6], acroPIDk[7], acroPIDk[8], REVERSE)
};
PID stabilizePID[] = {
  PID(&angle[0], &acroSet[0], &stabilizeSet[0], stabilizePIDk[0], stabilizePIDk[1], stabilizePIDk[2], REVERSE),
  PID(&angle[1], &acroSet[1], &stabilizeSet[1], stabilizePIDk[3], stabilizePIDk[4], stabilizePIDk[5], REVERSE),
};

//#####OTHERS#####

#define ACRO_MODE 0
#define STABILIZED_MODE 1
int flightMode = ACRO_MODE;

bool showLog = false;

//#define LOG_LOOP_SPEED
