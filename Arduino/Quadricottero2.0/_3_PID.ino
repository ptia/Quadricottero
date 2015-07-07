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
  if (flightMode == STABILIZED_MODE) {
    //Compute angular velocities from stabilized PID
    stabilizePID[0].Compute();
    stabilizePID[1].Compute();
  }
  //Compute motor delta values from acro PID
  stabilizePID[0].Compute();
  stabilizePID[1].Compute();
  stabilizePID[2].Compute();
}
