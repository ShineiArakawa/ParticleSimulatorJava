package simurator.physics;

import java.io.File;
import java.util.logging.Logger;

import simurator.common.Common;
import simurator.environment.Cube;
import simurator.io.ProjectSaver;
import simurator.particle.ParticleModel;

public class Physics {
  // @formatter:off
  private static final int   FPS = 1000;
  private static final float COEFFICIENT_OF_RESTITUTION = 1.0f;
  // @formatter:on
  
  private ParticleModel _particleModel;
  private Cube _environment;
  private Logger _logger;
  private float _timeSpan;
  
  public Physics(ParticleModel particleModel, Cube environment) {
    _particleModel = particleModel;
    _environment = environment;
    _logger = Logger.getLogger(getClass().getName());
    _timeSpan = 1.0f / FPS;
  }
  
  private void update() {
    int nParticles = _particleModel.getNumParticles();
    _particleModel.step(_timeSpan);
    
    int[][] lsCombi = calcCombination(nParticles);
    for (int iCombi = 0; iCombi < lsCombi.length; iCombi++) {
      int i = lsCombi[iCombi][0];
      int j = lsCombi[iCombi][1];
      boolean isHit = _particleModel.isHit(i, j);
      if (isHit) {
        reboundWithOtherParticle(i, j);
      }
    }
    //float[] coord278 = _particleModel.getParticleCoord(0);
    //float[] velocity278 = _particleModel.getParticleVelocity(0);
    
    _environment.calcHitWall(_particleModel);
    //_logger.info("x= " + coord278[0]+", y= " + coord278[1]+", z= " + coord278[2]);
    //_logger.info("vx= " + velocity278[0]+", vy= " + velocity278[1]+", vz= " + velocity278[2]);
  }
  
  public void simulate(float maxTime, float boxSize, String pathDirSave) {
    File dir = new File(pathDirSave);
    if (dir.exists() == false) {
      dir.mkdirs();
    }
    int maxIter = (int) (maxTime * FPS);
    for (int iter = 1; iter <= maxIter; iter++) {
      update();
      String pathSave = pathDirSave + "/Iter_" + iter + ".dau";
      ProjectSaver.save(_particleModel, boxSize, pathSave);
      if (iter % 10 == 0) {
        _logger.info("Iter= " + iter + ", Finished!");
      }
    }
  }
  
  private void reboundWithOtherParticle(int i, int j) {
    float[] iCoord = _particleModel.getParticleCoord(i);
    float[] jCoord = _particleModel.getParticleCoord(j);
    float[] iVelocity = _particleModel.getParticleVelocity(i);
    float[] jVelocity = _particleModel.getParticleVelocity(j);
    float iRadius = _particleModel.getParticleRadius(i);
    float jRadius = _particleModel.getParticleRadius(j);
    float iMass = _particleModel.getParticleMass(i);
    float jMass = _particleModel.getParticleMass(j);
    float[] actionLineVector = new float[3];
    for (int k = 0; k < 3; k++) {
      actionLineVector[k] = jCoord[k] - iCoord[k];
    }
    float[] unitActionLineVector = calcUnitVec(actionLineVector);
    float v1PreParallel = unitActionLineVector[0] * iVelocity[0] + unitActionLineVector[1] * iVelocity[1]
        + unitActionLineVector[2] * iVelocity[2];
    float v2PreParallel = -unitActionLineVector[0] * iVelocity[0] - unitActionLineVector[1] * iVelocity[1]
        - unitActionLineVector[2] * iVelocity[2];
    float[] v1AfterParallel = new float[3];
    float[] v2AfterParallel = new float[3];
    float[] iVelocityAfter = new float[3];
    float[] jVelocityAfter = new float[3];
    
    float[] tmp1 = new float[3];
    for (int k = 0; k < 3; k++) {
      tmp1[k] = iVelocity[k] + jVelocity[k];
    }
    float lenPre = Common.calcL2Norm(tmp1);
    
    v1AfterParallel[0] = (v1PreParallel
        + jMass * (1 + COEFFICIENT_OF_RESTITUTION) * (v2PreParallel - v1PreParallel) / (iMass + jMass))
        * unitActionLineVector[0];
    v1AfterParallel[1] = (v1PreParallel
        + jMass * (1 + COEFFICIENT_OF_RESTITUTION) * (v2PreParallel - v1PreParallel) / (iMass + jMass))
        * unitActionLineVector[1];
    v1AfterParallel[2] = (v1PreParallel
        + jMass * (1 + COEFFICIENT_OF_RESTITUTION) * (v2PreParallel - v1PreParallel) / (iMass + jMass))
        * unitActionLineVector[2];
    v2AfterParallel[0] = (v1PreParallel
        - iMass * (1 + COEFFICIENT_OF_RESTITUTION) * (v2PreParallel - v1PreParallel) / (iMass + jMass))
        * unitActionLineVector[0];
    v2AfterParallel[1] = (v1PreParallel
        - iMass * (1 + COEFFICIENT_OF_RESTITUTION) * (v2PreParallel - v1PreParallel) / (iMass + jMass))
        * unitActionLineVector[1];
    v2AfterParallel[2] = (v1PreParallel
        - iMass * (1 + COEFFICIENT_OF_RESTITUTION) * (v2PreParallel - v1PreParallel) / (iMass + jMass))
        * unitActionLineVector[2];
    iVelocityAfter[0] = iVelocity[0] - unitActionLineVector[0] * v1PreParallel + v1AfterParallel[0];
    iVelocityAfter[1] = iVelocity[1] - unitActionLineVector[1] * v1PreParallel + v1AfterParallel[1];
    iVelocityAfter[2] = iVelocity[2] - unitActionLineVector[2] * v1PreParallel + v1AfterParallel[2];
    jVelocityAfter[0] = jVelocity[0] + unitActionLineVector[0] * v2PreParallel + v2AfterParallel[0];
    jVelocityAfter[1] = jVelocity[1] + unitActionLineVector[1] * v2PreParallel + v2AfterParallel[1];
    jVelocityAfter[2] = jVelocity[2] + unitActionLineVector[2] * v2PreParallel + v2AfterParallel[2];
    
    for (int k = 0; k < 3; k++) {
      tmp1[k] = iVelocityAfter[k] + jVelocityAfter[k];
    }
    float lenAfter = Common.calcL2Norm(tmp1);
    
    if (Math.abs(lenPre - lenAfter) > 0.5f) {
      //_logger.info("Error particle: pre= " + lenPre + ", after= " + lenAfter);
    }
    
    _particleModel.setParticleVelocity(i, iVelocityAfter);
    _particleModel.setParticleVelocity(j, jVelocityAfter);
  }
  
  private static int[][] calcCombination(int n) {
    int num = 0;
    for (int i = 0; i < n; i++) {
      for (int j = i + 1; j < n; j++) {
        num += 1;
      }
    }
    int[][] combination = new int[num][2];
    num = 0;
    for (int i = 0; i < n; i++) {
      for (int j = i + 1; j < n; j++) {
        combination[num][0] = i;
        combination[num][1] = j;
        num += 1;
      }
    }
    return combination;
  }
  
  private static float[] calcUnitVec(float[] vector) {
    float[] unitVector = new float[3];
    float length = (float) Math.sqrt(vector[0] * vector[0] + vector[1] * vector[1] + vector[2] * vector[2]);
    unitVector[0] = vector[0] / length;
    unitVector[1] = vector[1] / length;
    unitVector[2] = vector[2] / length;
    return unitVector;
  }
}
