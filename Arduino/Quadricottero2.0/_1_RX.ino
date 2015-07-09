void readRX() {
  throttle = map(PCintPulseIn(RXpins[0]), signalMin[0], signalMax[0], 0, THROTTLE_MAX);
  if (throttle < -100) {
    throttle = THROTTLE_LOCK;
  }
  else if (throttle < 20) {
    throttle = LOW_THROTTLE;
  }
  if (flightMode == ACRO_MODE) {
    acroSet[0] = map(PCintPulseIn(RXpins[1]), signalMin[1], signalMax[1], -PR_ACRO_MAX, PR_ACRO_MAX);
    acroSet[1] = map(PCintPulseIn(RXpins[2]), signalMin[2], signalMax[2], -PR_ACRO_MAX, PR_ACRO_MAX);
  }
  else if (flightMode == STABILIZED_MODE) {
    stabilizeSet[0] = map(PCintPulseIn(RXpins[1]), signalMin[1], signalMax[1], -PR_ANGLE_MAX, PR_ANGLE_MAX);
    stabilizeSet[1] = map(PCintPulseIn(RXpins[2]), signalMin[2], signalMax[2], -PR_ANGLE_MAX, PR_ANGLE_MAX);
  }
  acroSet[2] = map(PCintPulseIn(RXpins[3]), signalMin[3], signalMax[3], -YAW_MAX, YAW_MAX);
}

void setupRX() {
  int channelCount = sizeof(RXpins) / sizeof(int);
  for (int i = 0; i < channelCount; i++) {
    int pin = RXpins[i];
    Serial.println(pin);
    pinMode(pin, INPUT);
    attachPulseIn(pin);
  }
}
