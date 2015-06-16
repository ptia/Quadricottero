void setup() {
  Serial.begin(115200);
  setupDMP();
  setupRX();
  readPIDk();
  armMotors();
  //Non partire con throttle alto
  while (!(lowThrottle | throttleLock)) {
    Serial.println(F("Can't start with high throttle"));
    readRX();
  }
  /*Il pin 13 è usato per segnalare
  * la modalità di volo corrente
  */
  pinMode(13, OUTPUT);
  digitalWrite(13, flightMode);
}

void loop() {
  /*Leggi IMU e interrompi
  * se ci sono errori
  */
  if (updateIMU() == 1) {
    return;
  }
  readRX();
  if (throttleLock) {
    sendToMotors(MOTOR_MIN_SPEED);
    readSerialPreferences();
    if(shouldPrintBookmark()) {
      printBookmark();
    }
    return;
  }
  computePID();
  sendMotorSpeeds();
  log();
}
