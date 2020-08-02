// File:          ControladorDifuso.java
// Date:
// Description:
// Author:
// Modifications:

// You may need to add other webots classes such as
import com.cyberbotics.webots.controller.DistanceSensor;
import com.cyberbotics.webots.controller.Motor;
import com.cyberbotics.webots.controller.Robot;
import com.cyberbotics.webots.controller.Camera;
import com.cyberbotics.webots.controller.LED;

import net.sourceforge.jFuzzyLogic.plot.JDialogFis;
import net.sourceforge.jFuzzyLogic.FIS;

// Here is the main class of your controller.
// This class defines how to initialize and how to run your controller.
public class ControladorDifuso {

  // This is the main function of your controller.
  // It creates an instance of your Robot instance and
  // it uses its function(s).
  // Note that only one instance of Robot should be created in
  // a controller program.
  // The arguments of the main function can be specified by the
  // "controllerArgs" field of the Robot node

  

  public static void main(String[] args) {

    // create the Robot instance.
    Robot robot = new Robot();
    
    // get the time step of the current world.
    int timeStep = (int) Math.round(robot.getBasicTimeStep());
    
    // Como acceder a los motores del Robot
    String nombresMotores[]=new String[]{"motordd","motorpd","motordi","motorpi"};
    Motor motores[] = new Motor[nombresMotores.length];
    
    // Como hacer que los motores se queden en una posicion inicial
    for(int i=0;i<motores.length;i++){
      motores[i] = robot.getMotor(nombresMotores[i]);
      motores[i].setPosition(Double.POSITIVE_INFINITY);
      motores[i].setVelocity(3.0);
    }
    
    DistanceSensor sensori = robot.getDistanceSensor("sensori");
    DistanceSensor sensorii = robot.getDistanceSensor("sensorii");
    DistanceSensor sensord = robot.getDistanceSensor("sensord");
    DistanceSensor sensordd = robot.getDistanceSensor("sensordd");
    
    LED ledi = robot.getLED("ledi");
    LED ledd = robot.getLED("ledd");
	
    Camera camera = robot.getCamera("camera");
    
    sensori.enable(timeStep);
    sensord.enable(timeStep);
    sensorii.enable(timeStep);
    sensordd.enable(timeStep);
    camera.enable(timeStep);
    
    double di = 0.0;
    double dd = 0.0;
    double dii = 0.0;
    double ddd = 0.0;
    
    // Si se requiere hacer una pausa de 1 segundo y ejecutar algun codigo
    // robot.step(1000);        
    
    // Ejemplo para que robot gire hacia la izquierda
    /*
    motores[0].setVelocity(-3.0);
    motores[1].setVelocity(-3.0);
    motores[2].setVelocity(3.0);
    motores[3].setVelocity(3.0);        
    */
    
    // CONTROL DIFUSO
    FIS _FIS = FIS.load("Controlador.fcl");
    // _FIS.setVariable("distanciai",0.0);
    // _FIS.setVariable("distanciad",0.0);    
    // _FIS.evaluate();
    
    // double vel = _FIS.getVariable("velocidad").getLatestDefuzzifiedValue();
    double veldd = 0.0;
    double velpd = 0.0;
    double veldi = 0.0;
    double velpi = 0.0;
    double vel = 0.0;
    int intensidad = 10;
        
    // System.out.println("Vel: "+vel);
    
    JDialogFis dialogoF = new JDialogFis(_FIS);
    dialogoF.setVisible(true);
    // You should insert a getDevice-like function in order to get the
    // instance of a device of the robot. Something like:
    //  Motor motor = robot.getMotor("motorname");
    //  DistanceSensor ds = robot.getDistanceSensor("dsname");
    //  ds.enable(timeStep);

    // Main loop:
    // - perform simulation steps until Webots is stopping the controller
    
    while (robot.step(timeStep) != -1) {
      // Read the sensors:
      // Enter here functions to read sensor data, like:
      //  double val = ds.getValue();

      // Process sensor data here.

      // Enter here functions to send actuator commands, like:
      //  motor.setPosition(10.0);
        
      di = sensori.getValue();
      dd = sensord.getValue();
      dii = sensorii.getValue();
      ddd = sensordd.getValue();
      
      _FIS.setVariable("distanciai",di);
      _FIS.setVariable("distanciad",dd);    
      _FIS.evaluate();
      
      vel = _FIS.getVariable("velocidad").getLatestDefuzzifiedValue();
      intensidad = (int) _FIS.getVariable("intensidad_luz").getLatestDefuzzifiedValue();
      //System.out.println(di+"||"+dd+"||"+vel);
      
      dialogoF.repaint();
      ledi.set(0);
      ledd.set(0);
      
      // System.out.println("Intensidad:" + intensidad);
      
      if (di <= 290 || dd <= 290) {
        if(di < 110 || dd < 110 || dii < 150 || ddd < 150){
        
          _FIS.setVariable("distanciai", 110);
          _FIS.setVariable("distanciad", 110);
          _FIS.evaluate();
          vel = _FIS.getVariable("velocidad").getLatestDefuzzifiedValue();

          veldd = vel;
          velpd = vel ;
          
          veldi = veldd  * -1.2;
          velpi = velpd  * -1.2;
          
          //System.out.println("Vel:" + vel);
          //ledi.set(255);
          ledd.set(intensidad);
        } else if(dd > di){
            veldi = vel;
            velpi = vel;
          
            veldd = vel - 1;
            velpd = vel - 1;
            
            ledi.set(intensidad);
            
        } else if(dd < di){
            veldd = vel;
            velpd = vel;
          
            veldi = vel - 1;
            velpi = vel - 1;
            
            ledd.set(intensidad);        
        }
        
        if (di < 20 || dd < 20 || ddd < 20 || dii < 20){
            veldd = 0;
            velpd = 0;
            veldi = 0;
            velpi = 0;
        }
        
      }else{
          veldd = vel;
          velpd = vel;
          veldi = vel;
          velpi = vel;
        }
      
      //System.out.println(di+"||"+dd+"||"+veldi+"||"+veldd);
      
      // for(int i=0;i<motores.length;i++){
        // motores[i].setVelocity(vel);
      // }
      motores[0].setVelocity(veldd);
      motores[1].setVelocity(velpd);
      motores[2].setVelocity(veldi);
      motores[3].setVelocity(velpi);
      
    };

    // Enter here exit cleanup code.
  }
}
