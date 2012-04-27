package jaseimov.client.controlcarB.car;

import jaseimov.client.controlcarB.CurrentDevices;

/**
this class contains the unique parameters of each Car
 * theese parameters are contained in the configuration file
 * It is static because it is unique and it is accessed from
 * several modules
 */
public class Car
{
  private static int motor_upper_limit = 100;  // units in %
  private static int motor_lower_limit = 0;    // units in %
  private static int encoder_tics_in_the_wheel = 0;
  private static double car_max_speed = 0; // let it be in m/s

  public static void set_motor_upper_limit(int upper)
  {
    motor_upper_limit = upper;
    CurrentDevices.motor_upper_limit = motor_upper_limit;
    System.out.println("[car]:motor_upper_limit: " + motor_upper_limit);
  }

  public static void set_motor_lower_limit(int lower)
  {
    motor_lower_limit = lower;
    CurrentDevices.motor_lower_limit = motor_lower_limit;
    System.out.println("[car]:motor_lower_limit: " + motor_lower_limit);
  }

  public static void set_encoder_tics_in_the_wheel(int tics)
  {
    encoder_tics_in_the_wheel = tics;
    CurrentDevices.ticsinthewheel = encoder_tics_in_the_wheel;
    System.out.println("[car]:encoder_tics_in_the_wheel: " + encoder_tics_in_the_wheel);
  }

  public static void set_car_max_speed(double val)
  {
    car_max_speed = val;
    System.out.println("[car]:car_max_speed: " + car_max_speed);
  }

  public static double get_car_max_speed()
  {
    return car_max_speed;
  }
}
