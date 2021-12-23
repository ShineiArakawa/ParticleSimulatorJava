package simurator.particle;

public class ParticleModel {
  private SingleParticle[] _particles;
  private int _nParticles;
  
  public ParticleModel(int nParticles, float[][] initPosition, float[][] initVelocity, float[] radius, float[] mass) {
    _nParticles = nParticles;
    _particles = new SingleParticle[_nParticles];
    for (int i = 0; i < _nParticles; i++) {
      _particles[i] = new SingleParticle(initPosition[i], initVelocity[i], radius[i], mass[i]);
    }
  }
  
  public boolean isHit(int i, int j) {
    boolean isHit = false;
    float[] iParticleCoord = _particles[i].getPosition();
    float[] jParticleCoord = _particles[j].getPosition();
    float iParticleRadius = _particles[i].getRadius();
    float jParticleRadius = _particles[j].getRadius();
    float distance = (float) Math.sqrt((iParticleCoord[0] - jParticleCoord[0]) * (iParticleCoord[0] - jParticleCoord[0])
        + (iParticleCoord[1] - jParticleCoord[1]) * (iParticleCoord[1] - jParticleCoord[1])
        + (iParticleCoord[2] - jParticleCoord[2]) * (iParticleCoord[2] - jParticleCoord[2]));
    if (distance <= iParticleRadius + jParticleRadius) {
      isHit = true;
    }
    return isHit;
  }
  
  public void step(float timeSpan) {
    float[] velocity = new float[3];
    float[] tmpCoord = new float[3];
    for (int i = 0; i < _nParticles; i++) {
      velocity = _particles[i].getVelocity();
      tmpCoord = _particles[i].getPosition();
      for (int j = 0; j < 3; j++) {
        tmpCoord[j] += velocity[j] * timeSpan;
      }
      _particles[i].setPosition(tmpCoord);
    }
  }
  
  public int getNumParticles() {
    return _nParticles;
  }
  
  public float[] getParticleCoord(int index) {
    float[] coord = _particles[index].getPosition();
    return coord;
  }
  
  public void setParticleCoord(int index, float[] coord) {
    _particles[index].setPosition(coord);
  }
  
  public float[] getParticleVelocity(int index) {
    float[] coord = _particles[index].getVelocity();
    return coord;
  }
  
  public void setParticleVelocity(int index, float[] coord) {
    _particles[index].setVelocity(coord);
  }
  
  public float getParticleRadius(int index) {
    float radius = _particles[index].getRadius();
    return radius;
  }
  
  public float getParticleMass(int index) {
    float mass = _particles[index].getMass();
    return mass;
  }
}
