package test;

import client.devicelist.DeviceInfo;
import client.devicelist.DeviceList;
import client.servercomm.ConnectException;
import device.Device;
import device.DeviceType;
import device.MotorControl;
import device.SensorDevice;

public class AvoidCollision
{
    static String ip = "127.0.0.1";
    static int port = java.rmi.registry.Registry.REGISTRY_PORT;

    static DeviceList list;

    public static void initList()
    {
        list = new DeviceList(ip,port);
        try
        {
            list.getServiceInfo();
        }
        catch(ConnectException ex)
        {
            ex.printStackTrace();
            System.exit(0);
        }
    }

    static MotorControl motor;    
    static double initAcceleration = 50.;

    public static void initMotor()
    {
        try
        {
            DeviceInfo[] motors = list.getDeviceInfoArray(DeviceType.MOTOR_CONTROL);
            if(motors.length > 0)
            {
                motor = (MotorControl) motors[0].getDevice();
                try
                {
                    motor.setVelocity(0);
                    motor.setAcceleration(initAcceleration);
                }
                catch(Exception ex)
                {
                    ex.printStackTrace();
                }
            }
            else
            {
                System.err.println("No motors where found in this server");
                System.exit(0);
            }
        }
        catch(ConnectException ex)
        {
            ex.printStackTrace();
        }
    }

    static SensorDevice frontSensor;
    static int sensorID = 0;

    public static void initSensor()
    {
        try
        {
            Device dev = list.getDeviceInfo(sensorID).getDevice();
            if(dev != null && dev instanceof SensorDevice)
            {
                frontSensor = (SensorDevice)dev;
            }
            else
            {
                System.err.println("No sensors whith ID " + sensorID + " where found in this server");
                System.exit(0);
            }
        }
        catch(ConnectException ex)
        {
            ex.printStackTrace();
        }
        
        if(frontSensor == null)
        {
            System.err.println("No sensors whith ID " + sensorID + " where found in this server");
            System.exit(0);
        }
    }

    static double velocity = 40.;    
    static double stopDistance = 30.;

    public static void avoidCollision()
    {
        try
        {
            motor.setVelocity(velocity);
            while(true)
            {
                Double distance = (Double)frontSensor.update();
                if(distance > 0)
                {
                    System.out.println("Obstacle detected at " + distance + " cm");
                    if(distance <= stopDistance)
                    {
                        motor.stopMotor();
                        System.out.println("Stopping vehicle");
                        Thread.sleep(250);
                        return;
                    }
                }
            }
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
            return;
        }
    }

    public static void close()
    {
        try
        {
            motor.stopMotor();
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
    }
    
    public static void main(String[] args)
    {
        if(args.length == 4)
        {
            ip = args[0];            
            sensorID = Integer.valueOf(args[1]);
            velocity = Integer.valueOf(args[2]);
            stopDistance = Integer.valueOf(args[3]);
        }
        else
        {
            System.out.println("Use: $IP $SENSOR_ID $VELOCITY $DISTANCE");
            System.exit(1);
        }
        initList();
        initMotor();
        initSensor();
        avoidCollision();
        close();
    }

}
