package simurator.environment;

import java.util.ArrayList;
import java.util.logging.Logger;

import simurator.common.Common;
import simurator.particle.ParticleModel;

public class Cube {
  // @formatter:off
  private static final int LABEL_FACE_INVALID = -1;
  private static final int LABEL_FACE_XY_MIN = 0;
  private static final int LABEL_FACE_XY_MAX = 1;
  private static final int LABEL_FACE_YZ_MIN = 2;
  private static final int LABEL_FACE_YZ_MAX = 3;
  private static final int LABEL_FACE_ZX_MIN = 4;
  private static final int LABEL_FACE_ZX_MAX = 5;
  private static final int[] LIST_LABEL_FACE = {
      LABEL_FACE_XY_MIN, LABEL_FACE_XY_MAX, LABEL_FACE_YZ_MIN,
      LABEL_FACE_YZ_MAX, LABEL_FACE_ZX_MIN, LABEL_FACE_ZX_MAX };
  private static final float[] UNIT_PERPENDICULAR_VECTOR_XY_MIN = {0.0f, 0.0f, 1.0f};
  private static final float[] UNIT_PERPENDICULAR_VECTOR_XY_MAX = {0.0f, 0.0f, -1.0f};
  private static final float[] UNIT_PERPENDICULAR_VECTOR_YZ_MIN = {1.0f, 0.0f, 0.0f};
  private static final float[] UNIT_PERPENDICULAR_VECTOR_YZ_MAX = {-1.0f, 0.0f, 0.0f};
  private static final float[] UNIT_PERPENDICULAR_VECTOR_ZX_MIN = {0.0f, 1.0f, 0.0f};
  private static final float[] UNIT_PERPENDICULAR_VECTOR_ZX_MAX = {0.0f, -1.0f, 0.0f};
  private static final float[][] LIST_UNIT_PERPENDICULAR_VECTOR = {
      UNIT_PERPENDICULAR_VECTOR_XY_MIN,
      UNIT_PERPENDICULAR_VECTOR_XY_MAX,
      UNIT_PERPENDICULAR_VECTOR_YZ_MIN,
      UNIT_PERPENDICULAR_VECTOR_YZ_MAX,
      UNIT_PERPENDICULAR_VECTOR_ZX_MIN,
      UNIT_PERPENDICULAR_VECTOR_ZX_MAX};
  // @formatter:on
  
  private float _boxSize;
  private float[] _boundsX;
  private float[] _boundsY;
  private float[] _boundsZ;
  private Logger _logger;
  
  public Cube(float boxSize, float[] centerCoord) {
    _logger = Logger.getLogger(getClass().getName());
    
    _boxSize = boxSize;
    _boundsX = new float[3];
    _boundsY = new float[3];
    _boundsZ = new float[3];
    _boundsX[0] = -boxSize / 2 + centerCoord[0];
    _boundsX[1] = boxSize / 2 + centerCoord[0];
    _boundsY[0] = -boxSize / 2 + centerCoord[1];
    _boundsY[1] = boxSize / 2 + centerCoord[1];
    _boundsZ[0] = -boxSize / 2 + centerCoord[2];
    _boundsZ[1] = boxSize / 2 + centerCoord[2];
  }
  
  public void calcHitWall(ParticleModel particleModel) {
    int nParticles = particleModel.getNumParticles();
    float[] minCoord = new float[3];
    float[] maxCoord = new float[3];
    float[] coord = new float[3];
    ArrayList<Integer> faceID = new ArrayList<Integer>();
    float[] distanceToWall = new float[6];
    
    for (int i = 0; i < nParticles; i++) {
      coord = particleModel.getParticleCoord(i);
      float radius = particleModel.getParticleRadius(i);
      faceID.clear();
      
      for (int j = 0; j < 3; j++) {
        minCoord[j] = coord[j] - radius;
        maxCoord[j] = coord[j] + radius;
      }
      
      if (minCoord[0] < _boundsX[0]) {
        faceID.add(LABEL_FACE_YZ_MIN);
      }
      if (minCoord[1] < _boundsY[0]) {
        faceID.add(LABEL_FACE_ZX_MIN);
      }
      if (minCoord[2] < _boundsZ[0]) {
        faceID.add(LABEL_FACE_XY_MIN);
      }
      if (maxCoord[0] > _boundsX[1]) {
        faceID.add(LABEL_FACE_YZ_MAX);
      }
      if (maxCoord[1] > _boundsY[1]) {
        faceID.add(LABEL_FACE_ZX_MAX);
      }
      if (maxCoord[2] > _boundsZ[1]) {
        faceID.add(LABEL_FACE_XY_MAX);
      }
      
      if (faceID.size() == 0) {
        continue;
      } else {
        distanceToWall[0] = _boundsZ[0] - minCoord[2];
        distanceToWall[1] = maxCoord[2] - _boundsZ[1];
        distanceToWall[2] = _boundsX[0] - minCoord[0];
        distanceToWall[3] = maxCoord[0] - _boundsX[1];
        distanceToWall[4] = _boundsY[0] - minCoord[1];
        distanceToWall[5] = maxCoord[1] - _boundsY[1];
        int argMax = 0;
        for (int j = 1; j < 6; j++) {
          if (distanceToWall[argMax] < distanceToWall[j]) {
            argMax = j;
          }
        }
        int mostDeepHit = LIST_LABEL_FACE[argMax];
        float[] perpendicularVector = LIST_UNIT_PERPENDICULAR_VECTOR[mostDeepHit];
        float[] preVelocity = particleModel.getParticleVelocity(i);
        float lenPre = Common.calcL2Norm(preVelocity);
        float[] afterVelocity = calcSymmetricVector(preVelocity, perpendicularVector);
        for (int j = 0; j < 3; j++) {
          afterVelocity[j] *= -1;
        }
        float lenAfter = Common.calcL2Norm(afterVelocity);
        particleModel.setParticleVelocity(i, afterVelocity);
        if (Math.abs(lenPre - lenAfter) > 0.5f) {
          _logger.info("Error wall: pre= " + lenPre + ", after= " + lenAfter);
        }
      }
    }
  }
  
  public static float[] calcSymmetricVector(float[] vector, float[] pole) {
    float lenPole = (float) Math.sqrt(pole[0] * pole[0] + pole[1] * pole[1] + pole[1] * pole[1]);
    float len1 = (vector[0] * pole[0] + vector[1] * pole[1] + vector[2] * pole[2]) / lenPole;
    float[] symmetricVector = new float[3];
    
    symmetricVector[0] = -vector[0] + 2 * len1 * pole[0] / lenPole;
    symmetricVector[1] = -vector[1] + 2 * len1 * pole[1] / lenPole;
    symmetricVector[2] = -vector[2] + 2 * len1 * pole[2] / lenPole;
    
    return symmetricVector;
  }
  
  public float getBoxSize() {
    return _boxSize;
  }
}
