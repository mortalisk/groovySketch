package geometry

import javax.vecmath.Vector4d

class Shape {

    int displayList;
    List<Triangle> triangles;
    List<Vertex> lineVertices;

    Shape() {

    }
    void drawLines(boolean stipple) {

    }
    void drawShape(Vector4d ambient, Vector4d diffuse) {

    }
    List<Vector3> intersectionPoints(Vector3 from,Vector3 direction) {

    }

    List<Triangle> getTriangles() {
        return triangles;
    }
    List<Vertex> getLineVertices() {
        return lineVertices;
    }
}