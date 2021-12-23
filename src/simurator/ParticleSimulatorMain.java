package simurator;

import java.io.IOException;
import java.util.Random;
import java.util.logging.Logger;

import simurator.environment.Cube;
import simurator.io.ParticleReader;
import simurator.particle.ParticleModel;
import simurator.physics.Physics;

public class ParticleSimulatorMain {
  // @formatter:off
  private static final long  SEED               = 10;
  private static final int   NUM_PARTICLES      = 2000;
  public static final float  RHO                = 1.0f;
  private static final float BOX_SIZE           = 100.0f;
  private static final float MAX_TIME           = 1.0f;
  public static final float  COEFFI_VELOCITY    = 100.0f;
  
  public static final float PI    = (float) Math.PI;
  // @formatter:on
  
  private Logger _logger;
  private Random _random;
  
  public static void main(String[] args) {
    new ParticleSimulatorMain(args);
  }
  
  public ParticleSimulatorMain(String[] args) {
    _logger = Logger.getLogger(getClass().getName());
    _logger.info("Started!");
    _random = new Random(SEED);
    
    if (args.length < 1) {
      start();
    } else {
      String path = args[0];
      _logger.info("path= " + path);
      startWithFile(path);
    }
  }
  
  private void start() {
    float[][] initPosition = new float[NUM_PARTICLES][3];
    float[][] initVelocity = new float[NUM_PARTICLES][3];
    float[] radius = new float[NUM_PARTICLES];
    float[] mass = new float[NUM_PARTICLES];
    
    for (int i = 0; i < NUM_PARTICLES; i++) {
      initPosition[i][0] = _random.nextFloat() * BOX_SIZE / 2 - BOX_SIZE / 4;
      initPosition[i][1] = _random.nextFloat() * BOX_SIZE / 2 - BOX_SIZE / 4;
      initPosition[i][2] = _random.nextFloat() * BOX_SIZE / 2 - BOX_SIZE / 4;
      initVelocity[i][0] = (_random.nextFloat() - 0.5f) * COEFFI_VELOCITY;
      initVelocity[i][1] = (_random.nextFloat() - 0.5f) * COEFFI_VELOCITY;
      initVelocity[i][2] = (_random.nextFloat() - 0.5f) * COEFFI_VELOCITY;
      radius[i] = _random.nextFloat();
      mass[i] = 4 / 3 * PI * radius[i] * radius[i] * radius[i] * RHO;
    }
    
    //initPosition[0][0] = -5.0f;
    //initPosition[0][1] = 0.0f;
    //initPosition[0][2] = 0.0f;
    //initPosition[1][0] = 5.0f;
    //initPosition[1][1] = 0.0f;
    //initPosition[1][2] = 0.0f;
    //initVelocity[0][0] = 20.0f;
    //initVelocity[0][1] = 0.0f;
    //initVelocity[0][2] = 0.0f;
    //initVelocity[1][0] = -20.0f;
    //initVelocity[1][1] = 0.0f;
    //initVelocity[1][2] = 0.0f;
    //radius[0] = 2.0f;
    //radius[1] = 3.0f;
    //mass[0] = 1.0f;
    //mass[1] = 1.0f;
    
    ParticleModel particleModel = new ParticleModel(NUM_PARTICLES, initPosition, initVelocity, radius, mass);
    float[] boxCenter = { 0.0f, 0.0f, 0.0f };
    Cube cube = new Cube(BOX_SIZE, boxCenter);
    Physics physics = new Physics(particleModel, cube);
    long startTime = System.currentTimeMillis();
    physics.simulate(MAX_TIME, BOX_SIZE, "./data");
    long endTime = System.currentTimeMillis();
    _logger.info("Elapsed time= " + (endTime - startTime) / 1000 + " [sec]");
  }
  
  private void startWithFile(String path) {
    ParticleModel particleModel = null;
    
    try {
      particleModel = ParticleReader.read(path, _random);
    } catch (IOException e) {
      e.printStackTrace();
    }
    
    if (particleModel != null) {
      float[] boxCenter = { 0.0f, 0.0f, 0.0f };
      Cube cube = new Cube(BOX_SIZE, boxCenter);
      Physics physics = new Physics(particleModel, cube);
      long startTime = System.currentTimeMillis();
      physics.simulate(MAX_TIME, BOX_SIZE, "./data");
      long endTime = System.currentTimeMillis();
      _logger.info("Elapsed time= " + (endTime - startTime) / 1000 + " [sec]");
    } else {
      _logger.info("Failed to read file: " + path);
    }
  }
}
