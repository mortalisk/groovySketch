package geometry

import javax.vecmath.Vector4d
import javax.vecmath.Vector3d
import org.lwjgl.BufferUtils

class Sphere extends Shape
{
    float radius
    Sphere(float radius = 1.0f) {
        int antallStykker =16;
        int antallDisker = 8;

        Vector3 top = new Vector3(0,radius,0);
        Vector3[][] punkter = new Vector3[antallDisker][antallStykker];
        Vector3 bunn  = new Vector3(0,-radius,0);
        Vector4d c = new Vector4d(1.0, 0, 0, 1.0);

        for (int d = 0; d<antallDisker; d++) {
            float phi = d/(float)antallDisker*Math.PI
            float r = Math.sin( phi ) * radius;
            float y = Math.cos( phi ) * radius;
            for (int s = 0; s < antallStykker; s++) {
                float theta = s/(float)antallStykker * Math.PI*2;
                float x = Math.cos (theta) * r;
                float z = Math.sin (theta) * r;
                Vector3 v = new Vector3(x,y,z);
                punkter[d][s] = v;
            }
        }

        this.triangles = BufferUtils.createFloatBuffer((antallDisker*antallStykker*2)*6*3);
        this.lineVertices = BufferUtils.createFloatBuffer(0);
        // overste
        for(int s = 0; s<antallStykker; s++) {
            Vector3 v1, v2, v3, n1, n2, n3

            v1 = punkter[1][s]
            v2 = top
            v3 = punkter[1][(s+1)%antallStykker]
            n1 = punkter[1][s]
            n2 = top
            n3 = punkter[1][(s+1)%antallStykker]

            putVertex(v1, n1)
            putVertex(v2, n2)
            putVertex(v3, n2)
        }
        // diskene
        for (int d = 2; d<antallDisker; d++) {
            for(int s = 0; s<antallStykker; s++) {

                Vector3 v1 = punkter[d-1][s]
                Vector3 v2 = punkter[d-1][(s+1)%antallStykker]
                Vector3 v3 = punkter[d][s]
                Vector3 n1 = punkter[d-1][s]
                Vector3 n2 = punkter[d-1][(s+1)%antallStykker]
                Vector3 n3 = punkter[d][s]

                putVertex(v1, n1)
                putVertex(v2, n2)
                putVertex(v3, n2)

                v1 = punkter[d-1][(s+1)%antallStykker]
                v2 = punkter[d][(s+1)%antallStykker]
                v3 = punkter[d][s]
                n1 = punkter[d-1][(s+1)%antallStykker]
                n2 = punkter[d][(s+1)%antallStykker]
                n3 = punkter[d][s]

                putVertex(v1, n1)
                putVertex(v2, n2)
                putVertex(v3, n2)
            }
        }
        // nederste
        for(int s = 0; s<antallStykker; s++) {
            Vector3 v1, v2, v3, n1, n2, n3

            v1 = punkter[antallDisker-1][(s+1)%antallStykker]
            v2 = bunn
            v3 = punkter[antallDisker-1][s]
            n1 = punkter[antallDisker-1][(s+1)%antallStykker]
            n2 = bunn
            n3 = punkter[antallDisker-1][s]

            putVertex(v1, n1)
            putVertex(v2, n2)
            putVertex(v3, n2)
        }
    }

    void putVertex(Vector3 v, Vector3 n) {
        this.triangles.put v.x
        this.triangles.put v.y
        this.triangles.put v.z
        this.triangles.put n.x
        this.triangles.put n.y
        this.triangles.put n.z
    }
}
