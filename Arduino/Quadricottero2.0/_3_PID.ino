void setPIDk() {
  //Imposta costanti per PID stabilizzazione
  for (int i = 0; i < 2; i++) {
    float pk = stabilizePIDk[i * 3];
    float ik = stabilizePIDk[i * 3 + 1];
    float dk = stabilizePIDk[i * 3 + 2];
    stabilizePID[i].ChangeParameters(pk, ik, dk, -PR_ACRO_MAX, PR_ACRO_MAX);
  }
  //Imposta costanti per PID acro
  for (int i = 0; i < 3; i++) {
    float pk = acroPIDk[i * 3];
    float ik = acroPIDk[i * 3 + 1];
    float dk = acroPIDk[i * 3 + 2];
    acroPID[i].ChangeParameters(pk, ik, dk, -MAX_MOTOR_DELTA, MAX_MOTOR_DELTA);
  }
}

void computePID() {
  if (lowThrottle) {
    //Se il throttle è basso, non applicare PRY
    rawPRY[0] = 0;
    rawPRY[1] = 0;
    rawPRY[2] = 0;
    return;
  }
  if (flightMode == STABILIZED_MODE) {
    //Computa le velocità angolari da PID stabilizzato
    for (int i = 0; i < 2; i++) {
      acroSet[i] = stabilizePID[i].Compute((float) stabilizeSet[i] - angle[i]);
    }
  }
  //Computa i valori delta da PID acrobatico
  for (int i = 0; i < 3; i++) {
    rawPRY[i] = (int) acroPID[i].Compute((float) acroSet[i] - gyro[i]);
  }
}
