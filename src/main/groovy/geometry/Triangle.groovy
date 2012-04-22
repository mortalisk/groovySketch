package geometry

import javax.vecmath.Vector3d

class Triangle {
    Vertex p1
    Vertex p2
    Vertex p3

    /** expects points in CCW order to make normal */
    Triangle(Vector3d v1, Vector3d v2, Vector3d v3) {
        Vector3d n = v2.sub(v1).cross(v3.sub(v1)).normalize();
        p1 = Vertex(v1.x(), v1.y(), v1.z(), n.x(), n.y(), n.z());
        p2 = Vertex(v2.x(), v2.y(), v2.z(), n.x(), n.y(), n.z());
        p3 = Vertex(v3.x(), v3.y(), v3.z(), n.x(), n.y(), n.z());
    }

    Triangle(Vector3 v1, Vector3 v2, Vector3 v3, Vector3 n1,  Vector3 n2,  Vector3 n3) {
        p1 = Vertex(v1.x(), v1.y(), v1.z(), n1.x(), n1.y(), n1.z());
        p2 = Vertex(v2.x(), v2.y(), v2.z(), n2.x(), n2.y(), n2.z());
        p3 = Vertex(v3.x(), v3.y(), v3.z(), n3.x(), n3.y(), n3.z());
    }
}