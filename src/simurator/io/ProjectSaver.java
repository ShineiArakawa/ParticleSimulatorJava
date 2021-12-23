package simurator.io;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import simurator.particle.ParticleModel;

public class ProjectSaver {
  public static void save(ParticleModel particleModel, float boxSize, String savePath) {
    try {
      File file = new File(savePath);
      if (file.exists() == false) {
        file.createNewFile();
      }
      OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(file), "UTF-8");
      BufferedWriter bw = new BufferedWriter(osw);
      
      int nParticles = particleModel.getNumParticles();
      bw.write(Integer.toString(nParticles));
      bw.newLine();
      
      bw.write(Float.toString(boxSize));
      ;
      bw.newLine();
      
      for (int i = 0; i < nParticles; i++) {
        float radius = particleModel.getParticleRadius(i);
        float mass = particleModel.getParticleMass(i);
        float[] coord = particleModel.getParticleCoord(i);
        float[] velocity = particleModel.getParticleVelocity(i);
        
        String line = Float.toString(radius) + "," + Float.toString(mass) + "," + Float.toString(coord[0]) + ","
            + Float.toString(coord[1]) + "," + Float.toString(coord[2]) + "," + Float.toString(velocity[0]) + ","
            + Float.toString(velocity[1]) + "," + Float.toString(velocity[2]);
        bw.write(line);
        bw.newLine();
      }
      
      bw.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
