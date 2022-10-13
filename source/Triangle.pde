class Triangle implements Comparable<Triangle> {
  PVector pos1;
  PVector pos2;
  PVector pos3;
  PVector col;
  float zVal;
  
  //Constructor
  Triangle(PVector pos1, PVector pos2, PVector pos3, PVector col, float zVal) {
    this.pos1 = pos1;
    this.pos2 = pos2;
    this.pos3 = pos3;
    this.col = col;
    this.zVal = zVal;
  }
  @Override
  public int compareTo(Triangle other) {
    return Float.compare(this.zVal, other.zVal);
  }
  
  
}
