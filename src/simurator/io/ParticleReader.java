package simurator.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Random;
import java.util.logging.Logger;

import simurator.ParticleSimulatorMain;
import simurator.particle.ParticleModel;

public class ParticleReader {
  public static ParticleModel read(String path, Random random) throws IOException {
    Logger logger = Logger.getLogger(Logger.class.getClass().getName());
    File file = new File(path);
    FileReader fr = new FileReader(file);
    BufferedReader br = new BufferedReader(fr);
    
    String value = null;
    value = br.readLine();
    value = value.strip();
    int nParticles = Integer.parseInt(value);
    float[][] coords = new float[nParticles][3];
    float[][] velocity = new float[nParticles][3];
    float[] radius = new float[nParticles];
    float[] mass = new float[nParticles];
    
    for (int i = 0; i < nParticles; i++) {
      value = br.readLine().strip();
      String[] line = value.split("[\s]+");
      radius[i] = Float.parseFloat(line[3]);
      coords[i][0] = Float.parseFloat(line[4]);
      coords[i][1] = Float.parseFloat(line[5]);
      coords[i][2] = Float.parseFloat(line[6]);
      
      velocity[i][0] = (random.nextFloat() - 0.5f) * ParticleSimulatorMain.COEFFI_VELOCITY;
      velocity[i][1] = (random.nextFloat() - 0.5f) * ParticleSimulatorMain.COEFFI_VELOCITY;
      velocity[i][2] = (random.nextFloat() - 0.5f) * ParticleSimulatorMain.COEFFI_VELOCITY;
      
      mass[i] = 4 / 3 * ParticleSimulatorMain.PI * radius[i] * radius[i] * radius[i] * ParticleSimulatorMain.RHO;
      
    }
    ParticleModel particleModel = new ParticleModel(nParticles, coords, velocity, radius, mass);
    
    return particleModel;
  }
}
