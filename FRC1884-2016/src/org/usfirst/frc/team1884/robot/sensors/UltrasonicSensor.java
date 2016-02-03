package org.usfirst.frc.team1884.robot.sensors;

public class UltrasonicSensor {
  public static final int ultronPortNumber = 0;

  public static final UltrasonicSensor instance = new UltrasonicSensor;
  private AnalogInput ultron;

  private UltrasonicSensor() {
    ultron = new AnalogInput(ultronPortNumber);
  }

  public double getDistanceInches() {
    return (double) ultron.get() / 7.2436;
  }
}