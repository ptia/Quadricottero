#include <EEPROM.h>

#include <PCintPulseIn.h>

#include <Servo.h>

#include <PIDCont.h>

#include "I2Cdev.h"
#include "MPU6050_6Axis_MotionApps20.h"
#if I2CDEV_IMPLEMENTATION == I2CDEV_ARDUINO_WIRE
#include "Wire.h"
#endif

//#######RX######

/*Liste relative ai canali RX
* in queste liste l'ordine è sempre:
* THROTTLE, PITCH, ROLL, YAW, POT_A
*/
const int RXpins[] = {A0, A1, A2, A3, 8};
const int signalMin[] = {1172, 1176, 1160, 1132, 2008};
const int signalMax[] = {1816, 1816, 1944, 1940, 996};
int16_t throttle;
bool throttleLock, lowThrottle;
#define THROTTLE_MAX 1000

//###SETPOINTS###

/*Array in pry delle velocità angolari
* da raggiungere
*/
float acroSet[3];
/* Array in PR (no yaw) degli angoli
* relativi al suolo da raggiungere
*/
float stabilizeSet[2];
/*I massimi e i minimi per le velocità
* angolari da raggiungere
*/
#define PR_ACRO_MAX 45
#define YAW_MAX 5
/*I massimi e i minimi per gli angoli
* da raggiungere
*/
#define PR_ANGLE_MAX 45


//######IMU######

/*Array dall'IMU
* si rispetta l'ordine PRY
*/
float gyro[3];
float angle[2];
float angleOffset[2];

//######PID######

/*Lista di PID per modalità
* acrobatica. Ordine sempre:
* PITCH, ROLL, YAW
*/
PIDCont acroPID[3];
PIDCont stabilizePID[2];
/*Ordine delle costanti seguenti
* Pitch: P, I, D
* Roll: P, I, D
* Yaw: P, I, D
*/
//Configurazioni per PID acrobatico.
float acroPIDk[9];
//Configurazioni per PID acrobatico.
float stabilizePIDk[6];


//#####MOTORI#####

/*Lista di classi Servo per i motori.
* L'ordine dei motori è in senso
* antiorario da avanti a sx,
*/
Servo motors[4];
/*Lista di pin dei motori, l'ordine
* è sempre antiorario da avanti a sx
*/
const int motorPins[] = {3, 4, 5, 6};
/*Tutti i delta sono in realtà doppi
* perchè sono applicati due volte
*/
/*Differenziale di velocità
* dei motori, cioà il valore
* massimo (e quindi minimo) di
* PRY come delta di velocità
* dei motori
*/
#define MAX_MOTOR_DELTA 100
//Lista di valori delta PRY
int rawPRY[3];
//Capisaldi di segnali per i motori
#define MOTOR_MIN_SPEED 1100
#define MOTOR_ARM 1000
//Velocità dei singoli motori
int speeds[4];

//#######ALTRO######

/*Tipo di stabilizzazione da
* attuare
*/
#define ACRO_MODE 0
#define STABILIZED_MODE 1
int flightMode = 0;
bool showLog = false;
