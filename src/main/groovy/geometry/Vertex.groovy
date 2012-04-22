package geometry

import javax.vecmath.Vector3d

class Vertex {

    float x,y,z
    float n1, n2, n3

    Vertex(Vector3d v, Vector3d n) {
        x = v.x;
        y = v.y;
        z = v.z;
        n1 = n.x;
        n2 = n.y;
        n3 = n.z;
    }

}
