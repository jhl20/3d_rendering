import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import java.util.Arrays; 
import java.util.ArrayList; 
import java.util.List; 
import java.util.Collections; 
import java.util.Comparator; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class sketch_3dRendering extends PApplet {







Table mesh;
Table sh0;
Table sh1;
Table sh2;
Table sh3;
Table shEV;
Table tx0;
Table tx1;
Table tx2;
Table tx3;
Table txEV;
int i1,i2,i3;
float r1,r2,r3;

List<Triangle> allTri;
PVector cursorPoint;
boolean inTri;
float pw1, pw2, pw3;
float cw1, cw2, cw3;


// code intended screen size is 1920*1080
public void setup() {
  // Program made to run in fullscreen
  // initialise variables
  
  //frameRate(40) ;
  mesh = loadTable("mesh.csv");
  sh0 = loadTable("sh_000.csv");
  sh1 = loadTable("sh_001.csv");
  sh2 = loadTable("sh_002.csv");
  sh3 = loadTable("sh_003.csv");
  shEV = loadTable("sh_ev.csv");
  tx0 = loadTable("tx_000.csv");
  tx1 = loadTable("tx_001.csv");
  tx2 = loadTable("tx_002.csv");
  tx3 = loadTable("tx_003.csv");
  txEV = loadTable("tx_ev.csv");
  inTri = false;
  
  allTri = new ArrayList<Triangle>();
  
  // point and color weights
  pw1 = shEV.getFloat(0, 0);
  cw1 = txEV.getFloat(0, 0);
  pw2 = shEV.getFloat(1, 0);
  cw2 = txEV.getFloat(1, 0);
  pw3 = shEV.getFloat(2, 0);
  cw3 = txEV.getFloat(2, 0);

}




public void draw() {
  // refresh background so no artifacting
  background(255);
  
  // display ratios
  if (cursorPoint !=null) {
  fill(0);
  text("Ratio for 001: " + r1, 5*width/40, 7*height/40);
  text("Ratio for 002: " + r2, 5*width/40, 8*height/40);
  text("Ratio for 003: " + r3, 5*width/40, 9*height/40);
  }
  
  stroke(0);
  fill(150);
  // triangle for user input
  triangle(200, 500, 400, 500, 300, 700);
  fill(0);
  ellipseMode(CENTER);
  ellipse(200, 500, 10, 10);
  ellipse(400, 500, 10, 10);
  ellipse(300, 700, 10, 10);
  if (inTri) {
    fill(255);
    stroke(0);
    ellipse(cursorPoint.x, cursorPoint.y, 5, 5) ;
    line(200, 500, cursorPoint.x, cursorPoint.y);
    line(400, 500, cursorPoint.x, cursorPoint.y);
    line(300, 700, cursorPoint.x, cursorPoint.y);
  }
  
  for(Triangle t: allTri) {
    stroke(t.col.x, t.col.y, t.col.z);
    fill(t.col.x, t.col.y, t.col.z);
    // offset so face is close to center
    triangle(t.pos1.x+1000, t.pos1.y+500, t.pos2.x+1000, t.pos2.y+500, t.pos3.x+1000, t.pos3.y+500);
  }
  
  
  
  
}

public void mousePressed() {
  if (mouseButton == LEFT) {
    inTri = triPoint(200, 500, 400, 500, 300, 700, mouseX, mouseY);
    if (inTri) {
      cursorPoint = new PVector(mouseX, mouseY);
      calcFace(cursorPoint);
    }
  }
}

public void calcFace(PVector cP) {
  // clear current face
  allTri.clear();
  float d1 = dist(cP.x, cP.y, 200, 500);
  float d2 = dist(cP.x, cP.y, 400, 500);
  float d3 = dist(cP.x, cP.y, 300, 700);
  if (d1 == 0) {
    r1 = 1;
    r2 = 0;
    r3 = 0;
  } else if (d2 == 0) {
    r1 = 0;
    r2 = 1;
    r3 = 0;
  } else if (d3 == 0) {
    r1 = 0;
    r2 = 0;
    r3 = 1;
  } else {
    float sum = 1/d1 + 1/d2 + 1/d3;
    r1 = 1/d1/sum;
    r2 = 1/d2/sum;
    r3 = 1/d3/sum;
  }
  
  for (TableRow rowMesh: mesh.rows()){
    // each mesh row corresponds to a triangle in mesh
    i1 = rowMesh.getInt(0);
    i2 = rowMesh.getInt(1);
    i3 = rowMesh.getInt(2);
    
    // coordinates of average face
    PVector p1 = new PVector((sh0.getFloat(i1-1, 0)), (sh0.getFloat(i1-1, 1)), (sh0.getFloat(i1-1, 2)));
    PVector p2 = new PVector((sh0.getFloat(i2-1, 0)), (sh0.getFloat(i2-1, 1)), (sh0.getFloat(i2-1, 2)));
    PVector p3 = new PVector((sh0.getFloat(i3-1, 0)), (sh0.getFloat(i3-1, 1)), (sh0.getFloat(i3-1, 2)));
    
    // coordinates of face one
    PVector p1_off_1 = new PVector((sh1.getFloat(i1-1, 0)), (sh1.getFloat(i1-1, 1)), (sh1.getFloat(i1-1, 2)));
    PVector p2_off_1 = new PVector((sh1.getFloat(i2-1, 0)), (sh1.getFloat(i2-1, 1)), (sh1.getFloat(i2-1, 2)));
    PVector p3_off_1 = new PVector((sh1.getFloat(i3-1, 0)), (sh1.getFloat(i3-1, 1)), (sh1.getFloat(i3-1, 2)));
    
    // coordinates of face two
    PVector p1_off_2 = new PVector((sh2.getFloat(i1-1, 0)), (sh2.getFloat(i1-1, 1)), (sh2.getFloat(i1-1, 2)));
    PVector p2_off_2 = new PVector((sh2.getFloat(i2-1, 0)), (sh2.getFloat(i2-1, 1)), (sh2.getFloat(i2-1, 2)));
    PVector p3_off_2 = new PVector((sh2.getFloat(i3-1, 0)), (sh2.getFloat(i3-1, 1)), (sh2.getFloat(i3-1, 2)));
    
    // coordinates of face three
    PVector p1_off_3 = new PVector((sh3.getFloat(i1-1, 0)), (sh3.getFloat(i1-1, 1)), (sh3.getFloat(i1-1, 2)));
    PVector p2_off_3 = new PVector((sh3.getFloat(i2-1, 0)), (sh3.getFloat(i2-1, 1)), (sh3.getFloat(i2-1, 2)));
    PVector p3_off_3 = new PVector((sh3.getFloat(i3-1, 0)), (sh3.getFloat(i3-1, 1)), (sh3.getFloat(i3-1, 2)));
    
    // calculated coordinates
    PVector point1 = new PVector((p1.x + p1_off_1.x * pw1 * r1 + p1_off_2.x * pw2 * r2 + p1_off_3.x * pw3 * r3),
                                 (p1.y + p1_off_1.y * pw1 * r1 + p1_off_2.y * pw2 * r2 + p1_off_3.y * pw3 * r3), 
                                 (p1.z + p1_off_1.z * pw1 * r1 + p1_off_2.z * pw2 * r2 + p1_off_3.z * pw3 * r3));
    PVector point2 = new PVector((p2.x + p2_off_1.x * pw1 * r1 + p2_off_2.x * pw2 * r2 + p2_off_3.x * pw3 * r3), 
                                 (p2.y + p2_off_1.y * pw1 * r1 + p2_off_2.y * pw2 * r2 + p2_off_3.y * pw3 * r3), 
                                 (p2.z + p2_off_1.z * pw1 * r1 + p2_off_2.z * pw2 * r2 + p2_off_3.z * pw3 * r3));
    PVector point3 = new PVector((p3.x + p3_off_1.x * pw1 * r1 + p3_off_2.x * pw2 * r2 + p3_off_3.x * pw3 * r3), 
                                 (p3.y + p3_off_1.y * pw1 * r1 + p3_off_2.y * pw2 * r2 + p3_off_3.y * pw3 * r3), 
                                 (p3.z + p3_off_1.z * pw1 * r1 + p3_off_2.z * pw2 * r2 + p3_off_3.z * pw3 * r3));
    
    float[] sortZ = {point1.z, point2.z, point3.z};
    sortZ = sort(sortZ);
    // smallest z for painter's algorithm
    float z = sortZ[0];
    
    // scale downwards to fit and flip because coordinate system for processing starts from top left to bottom right instead of bottom left to top right
    point1.mult(-0.004f);
    point2.mult(-0.004f);
    point3.mult(-0.004f);
    
    // color of average face
    PVector c1 = new PVector((tx0.getFloat(i1-1, 0)), (tx0.getFloat(i1-1, 1)), (tx0.getFloat(i1-1, 2)));
    PVector c2 = new PVector((tx0.getFloat(i2-1, 0)), (tx0.getFloat(i2-1, 1)), (tx0.getFloat(i2-1, 2)));
    PVector c3 = new PVector((tx0.getFloat(i3-1, 0)), (tx0.getFloat(i3-1, 1)), (tx0.getFloat(i3-1, 2)));
    
    // color of face one
    PVector c1_off_1 = new PVector((tx1.getFloat(i1-1, 0)), (tx1.getFloat(i1-1, 1)), (tx1.getFloat(i1-1, 2)));
    PVector c2_off_1 = new PVector((tx1.getFloat(i2-1, 0)), (tx1.getFloat(i2-1, 1)), (tx1.getFloat(i2-1, 2)));
    PVector c3_off_1 = new PVector((tx1.getFloat(i3-1, 0)), (tx1.getFloat(i3-1, 1)), (tx1.getFloat(i3-1, 2)));
    
    // color of face two
    PVector c1_off_2 = new PVector((tx2.getFloat(i1-1, 0)), (tx2.getFloat(i1-1, 1)), (tx2.getFloat(i1-1, 2)));
    PVector c2_off_2 = new PVector((tx2.getFloat(i2-1, 0)), (tx2.getFloat(i2-1, 1)), (tx2.getFloat(i2-1, 2)));
    PVector c3_off_2 = new PVector((tx2.getFloat(i3-1, 0)), (tx2.getFloat(i3-1, 1)), (tx2.getFloat(i3-1, 2)));
    
    // color of face three
    PVector c1_off_3 = new PVector((tx3.getFloat(i1-1, 0)), (tx3.getFloat(i1-1, 1)), (tx3.getFloat(i1-1, 2)));
    PVector c2_off_3 = new PVector((tx3.getFloat(i2-1, 0)), (tx3.getFloat(i2-1, 1)), (tx3.getFloat(i2-1, 2)));
    PVector c3_off_3 = new PVector((tx3.getFloat(i3-1, 0)), (tx3.getFloat(i3-1, 1)), (tx3.getFloat(i3-1, 2)));
    
    // calculated color
    PVector color1 = new PVector((c1.x + c1_off_1.x * cw1 * r1 + c1_off_2.x * cw2 * r2 + c1_off_3.x * cw3 * r3), 
                                 (c1.y + c1_off_1.y * cw1 * r1 + c1_off_2.y * cw2 * r2 + c1_off_3.y * cw3 * r3), 
                                 (c1.z + c1_off_1.z * cw1 * r1 + c1_off_2.z * cw2 * r2 + c1_off_3.z * cw3 * r3));
    PVector color2 = new PVector((c2.x + c2_off_1.x * cw1 * r1 + c2_off_2.x * cw2 * r2 + c2_off_3.x * cw3 * r3), 
                                 (c2.y + c2_off_1.y * cw1 * r1 + c2_off_2.y * cw2 * r2 + c2_off_3.y * cw3 * r3), 
                                 (c2.z + c2_off_1.z * cw1 * r1 + c2_off_2.z * cw2 * r2 + c2_off_3.z * cw3 * r3));
    PVector color3 = new PVector((c3.x + c3_off_1.x * cw1 * r1 + c3_off_2.x * cw2 * r2 + c3_off_3.x * cw3 * r3), 
                                 (c3.y + c3_off_1.y * cw1 * r1 + c3_off_2.y * cw2 * r2 + c3_off_3.y * cw3 * r3), 
                                 (c3.z + c3_off_1.z * cw1 * r1 + c3_off_2.z * cw2 * r2 + c3_off_3.z * cw3 * r3));
    // average color
    PVector averageC = new PVector((color1.x+color2.x+color3.x)/3, (color1.y+color2.y+color3.y)/3, (color1.z+color2.z+color3.z)/3);
    noStroke();
    allTri.add(new Triangle(point1, point2, point3, averageC, z));
  }
  Collections.sort(allTri);
}



// Triangle Collision detection from http://www.jeffreythompson.org/collision-detection/tri-point.php
// TRIANGLE/POINT
public boolean triPoint(float x1, float y1, float x2, float y2, float x3, float y3, float px, float py) {

  // get the area of the triangle
  float areaOrig = abs((x2-x1)*(y3-y1)-(x3-x1)*(y2-y1));

  // get the area of 3 triangles made between the point
  // and the corners of the triangle
  float area1 = abs((x1-px)*(y2-py)-(x2-px)*(y1-py));
  float area2 = abs((x2-px)*(y3-py)-(x3-px)*(y2-py));
  float area3 = abs((x3-px)*(y1-py)-(x1-px)*(y3-py));

  // if the sum of the three areas equals the original,
  // we're inside the triangle!
  if (area1 + area2 + area3 == areaOrig) {
    return true;
  }
  return false;
}
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
  public void settings() {  fullScreen(); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "sketch_3dRendering" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
