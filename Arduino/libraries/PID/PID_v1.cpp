/**********************************************************************************************
 * Arduino PID Library - Version 1.1.1
 * by Brett Beauregard <br3ttb@gmail.com> brettbeauregard.com
 *
 * This Library is licensed under a GPLv3 License
 **********************************************************************************************/

#if ARDUINO >= 100
#include "Arduino.h"
#else
#include "WProgram.h"
#endif

#include <PID_v1.h>

/*Constructor (...)*********************************************************
 *    The parameters specified here are those for for which we can't set up
 *    reliable defaults, so we need to have the user set them.
 ***************************************************************************/
PID::PID(float Kp, float Ki, float Kd, int ControllerDirection)
{
	//default output limit corresponds to the arduino pwm limits
  PID::SetOutputLimits(0, 255);       

  SampleTime = 100;             //default Controller Sample Time is 0.1 seconds

  PID::SetControllerDirection(ControllerDirection);
  PID::SetTunings(Kp, Ki, Kd);

  lastTime = millis() - SampleTime;
}


/* Compute() **********************************************************************
 *     This, as they say, is where the magic happens.  this function should be called
 *   every time "void loop()" executes.  the function will decide for itself whether a new
 *   pid Output needs to be computed.  returns true when the output is computed,
 *   false when nothing has been done.
 **********************************************************************************/
float PID::Compute(float input, float setpoint)
{
  unsigned long now = millis();
  unsigned int timeChange = (now - lastTime);
  if (timeChange >= SampleTime)
  {
    /*Compute all the working error variables*/
    float error = setpoint - input;
    ITerm += (ki * error);
    if (ITerm > outMax) ITerm = outMax;
    else if (ITerm < outMin) ITerm = outMin;
    float dInput = (input - lastInput);

    /*Compute PID Output*/
    float output = kp * error + ITerm - kd * dInput;

    if (output > outMax) output = outMax;
    else if (output < outMin) output = outMin;

    /*Remember some variables for next time*/
    lastInput = input;
    lastTime = now;
    lastOutput = output;
    return output;
  }
  return lastOutput;
}


/* SetTunings(...)*************************************************************
 * This function allows the controller's dynamic performance to be adjusted.
 * it's called automatically from the constructor, but tunings can also
 * be adjusted on the fly during normal operation
 ******************************************************************************/
void PID::SetTunings(float Kp, float Ki, float Kd)
{
  if (Kp < 0 || Ki < 0 || Kd < 0) return;

  dispKp = Kp; dispKi = Ki; dispKd = Kd;

  float SampleTimeInSec = ((float)SampleTime) / 1000;
  kp = Kp;
  ki = Ki * SampleTimeInSec;
  kd = Kd / SampleTimeInSec;

  if (controllerDirection == REVERSE)
  {
    kp = (0 - kp);
    ki = (0 - ki);
    kd = (0 - kd);
  }
}

/* SetSampleTime(...) *********************************************************
 * sets the period, in Milliseconds, at which the calculation is performed
 ******************************************************************************/
void PID::SetSampleTime(int NewSampleTime)
{
  if (NewSampleTime > 0)
  {
    float ratio  = (float)NewSampleTime
                   / (float)SampleTime;
    ki *= ratio;
    kd /= ratio;
    SampleTime = (unsigned long)NewSampleTime;
  }
}

/* SetOutputLimits(...)****************************************************
 *     This function will be used far more often than SetInputLimits.  while
 *  the input to the controller will generally be in the 0-1023 range (which is
 *  the default already,)  the output will be a little different.  maybe they'll
 *  be doing a time window and will need 0-8000 or something.  or maybe they'll
 *  want to clamp it from 0-125.  who knows.  at any rate, that can all be done
 *  here.
 **************************************************************************/
void PID::SetOutputLimits(float Min, float Max)
{
  if (Min >= Max) return;
  outMin = Min;
  outMax = Max;
  if (ITerm > outMax) ITerm = outMax;
  else if (ITerm < outMin) ITerm = outMin;
}

/* SetControllerDirection(...)*************************************************
 * The PID will either be connected to a DIRECT acting process (+Output leads
 * to +Input) or a REVERSE acting process(+Output leads to -Input.)  we need to
 * know which one, because otherwise we may increase the output when we should
 * be decreasing.  This is called from the constructor.
 ******************************************************************************/
void PID::SetControllerDirection(int Direction)
{
  if (Direction != controllerDirection)
  {
    kp = (0 - kp);
    ki = (0 - ki);
    kd = (0 - kd);
  }
  controllerDirection = Direction;
}

/* Status Funcions*************************************************************
 * Just because you set the Kp=-1 doesn't mean it actually happened.  these
 * functions query the internal state of the PID.  they're here for display
 * purposes.  this are the functions the PID Front-end uses for example
 ******************************************************************************/
float PID::GetKp() {
  return  dispKp;
}
float PID::GetKi() {
  return  dispKi;
}
float PID::GetKd() {
  return  dispKd;
}
int PID::GetDirection() {
  return controllerDirection;
}

