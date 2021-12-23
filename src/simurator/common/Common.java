package simurator.common;

public class Common {
  public static float calcL2Norm(float[] vector) {
    float tmp = 0.0f;
    for (int i = 0; i < vector.length; i++) {
      tmp += vector[i] * vector[i];
    }
    tmp = (float) Math.sqrt(tmp);
    return tmp;
  }
}
