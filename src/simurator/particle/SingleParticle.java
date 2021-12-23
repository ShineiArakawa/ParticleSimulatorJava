package simurator.particle;

public class SingleParticle {
  private float[] _position;
  private float[] _velocity;
  private float _radius;
  private float _mass;
  
  public SingleParticle(float[] position, float[] velocity, float radius, float mass) {
    _position = position;
    _velocity = velocity;
    _radius = radius;
    _mass = mass;
  }
  
  public float[] getPosition() {
    return _position;
  }
  
  public void setPosition(float[] position) {
    _position = position;
  }
  
  public float[] getVelocity() {
    return _velocity;
  }
  
  public void setVelocity(float[] velocity) {
    _velocity = velocity;
  }
  
  public float getRadius() {
    return _radius;
  }
  
  public float getMass() {
    return _mass;
  }
}
