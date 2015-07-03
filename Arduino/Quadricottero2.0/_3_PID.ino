void setPIDk() {
  //Stabilized PID
  for (int i = 0; i < 2; i++) {
    float pk = stabilizePIDk[i * 3];
    float ik = stabilizePIDk[i * 3 + 1];
    float dk = stabilizePIDk[i * 3 + 2];
    stabilizePID[i].SetTunings(pk, ik, dk);
    stabilizePID[i].SetOutputLimits(-PR_ACRO_MAX, PR_ACRO_MAX);
  }
  //Acro PID
  for (int i = 0; i < 3; i++) {
    float pk = acroPIDk[i * 3];
    float ik = acroPIDk[i * 3 + 1];
    float dk = acroPIDk[i * 3 + 2];
    acroPID[i].SetTunings(pk, ik, dk);
    acroPID[i].SetOutputLimits(-MAX_MOTOR_DELTA, MAX_MOTOR_DELTA);
  }
}

void computePID() {
  if (lowThrottle) {
    //If throttle is low, do not apply any Pitch Roll or Yaw
    rawPRY[0] = 0;
    rawPRY[1] = 0;
    rawPRY[2] = 0;
    return;
  }
  if (flightMode == STABILIZED_MODE) {
    //Compute angular velocities from stabilized PID
    for (int i = 0; i < 2; i++) {
      stabilizePID[i].Compute();
    }
  }
  //Compute motor delta values from acro PID
  for (int i = 0; i < 3; i++) {
    acroPID[i].Compute();
  }
}
