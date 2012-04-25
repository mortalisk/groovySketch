package geometry

import javax.vecmath.Vector4d
import java.nio.DoubleBuffer
import static org.lwjgl.opengl.GL11.*
import java.nio.FloatBuffer
import org.lwjgl.BufferUtils

class Shape {

    int displayList
    DoubleBuffer triangles
    DoubleBuffer lineVertices
    boolean strip

    Shape() {

    }
    void drawLines(boolean stipple) {
        glLineWidth(2.0);
        float[] c = [0.0,0.0,0.0,1.0];
        def col = BufferUtils.createFloatBuffer(4);
        col.put(c)
        col.rewind()
        glMaterial(GL_FRONT,GL_AMBIENT_AND_DIFFUSE,col);

        if (stipple) {
            glLineStipple(1, (short)0xAAAA);
            glEnable(GL_LINE_STIPPLE);
        }
        glEnableClientState(GL_VERTEX_ARRAY);
        lineVertices.rewind()
        if (lineVertices.capacity() > 0) {
            glVertexPointer(3,0,lineVertices);
            glDrawArrays(GL_LINE_STRIP,0,(int)(getLineVertices().capacity()/3));
        }
        if (stipple) {
            glDisable(GL_LINE_STIPPLE);
        }
        glDisableClientState(GL_VERTEX_ARRAY);
    }
    void drawShape(Vector4d ambient, Vector4d diffuse) {
        float[] c = [diffuse.x, diffuse.y, diffuse.z, diffuse.w ];
        FloatBuffer col = BufferUtils.createFloatBuffer(4);
        col.put(c)
        col.rewind()
        glMaterial(GL_FRONT_AND_BACK,GL_DIFFUSE,col);
        float[] spec = [diffuse.x+0.5f, diffuse.y+0.5f, diffuse.z+0.5f, diffuse.w];
        FloatBuffer specular = BufferUtils.createFloatBuffer(4);
        specular.put(spec)
        specular.rewind()
        glMaterialf(GL_FRONT_AND_BACK, GL_SHININESS, 128.0f );
        glMaterial(GL_FRONT_AND_BACK, GL_SPECULAR, specular);
        float[] a = [diffuse.x/3, diffuse.y/3, diffuse.z/3, ambient.w ];
        FloatBuffer amb = BufferUtils.createFloatBuffer(4);
        amb.put(a)
        amb.rewind()
        glMaterial(GL_FRONT_AND_BACK,GL_AMBIENT,amb);

        glEnableClientState(GL_VERTEX_ARRAY);

        glEnableClientState(GL_NORMAL_ARRAY);
        triangles.rewind()
        if (triangles.capacity() > 0) {
            int mode = strip?GL_TRIANGLE_STRIP:GL_TRIANGLES
            glVertexPointer(3,6*8,triangles);
            triangles.position(3)
            glNormalPointer(6*8,triangles);
            int number = (int)(triangles.capacity()/6)
            glDrawArrays(mode,0,number);
        }
        glDisableClientState(GL_NORMAL_ARRAY);
        glDisableClientState(GL_VERTEX_ARRAY);
    }
    List<Vector3> intersectionPoints(Vector3 p,Vector3 dir) {
        ArrayList<Vector3> points = new ArrayList<Vector3>();

        int nearest = -1;
        float distance = Float.MAX_VALUE;
        int pointn = 0;
        for(int i=0; i<triangles.capacity(); i+=18) {

            Vector3 v0= new Vector3((float)triangles.get(i),(float)triangles.get(i+1),(float)triangles.get(i+2))
            Vector3 v1= new Vector3((float)triangles.get(i+6),(float)triangles.get(i+7),(float)triangles.get(i+8))
            Vector3 v2= new Vector3((float)triangles.get(i+12),(float)triangles.get(i+13),(float)triangles.get(i+14))
            // hentet fra http://softsurfer.com/Archive/algorithm_0105/algorithm_0105.htm#intersect_RayTriangle%28%29

//            Vector3 v0(triangles[i].p1.x,triangles[i].p1.y,triangles[i].p1.z);
//            Vector3 v1(triangles[i].p2.x,triangles[i].p2.y,triangles[i].p2.z);
//            Vector3 v2(triangles[i].p3.x,triangles[i].p3.y,triangles[i].p3.z);

            Vector3    u, v, n;             // triangle vectors
            Vector3    w0, w;          // ray vectors
            float      r, a, b;             // params to calc ray-plane intersect

            // get triangle edge vectors and plane normal
            u = v1 - v0;
            v = v2 - v0;
            n = u.cross(v);             // cross product
            if (n.x == 0 && n.y== 0 && n.z == 0)            // triangle is degenerate
                continue;  // return -1               // do not deal with this case

            //dir = R.P1 - R.P0;             // ray direction vector
            w0 = p - v0;
            a = -(n * w0);
            b = n * dir;
            if (Math.abs(b) < 0.01) {     // ray is parallel to triangle plane
                continue;
                /*if (a == 0)                // ray lies in triangle plane
return 2;
else return 0;             // ray disjoint from plane*/
            }

            // get intersect point of ray with triangle plane
            r = a / b;
            if (r < 0.0)                   // ray goes away from triangle
                continue; //return 0;                  // => no intersect
            // for a segment, also test if (r > 1.0) => no intersect

            Vector3 I = p + dir*r;           // intersect point of ray and plane

            // is I inside T?
            float    uu, uv, vv, wu, wv, D;
            uu = u*u;
            uv = u*v;
            vv = v*v;
            w = I - v0;
            wu = w*u;
            wv = w*v;
            D = uv * uv - uu * vv;

            // get and test parametric coords
            float s, t;
            s = (uv * wv - vv * wu) / D;
            if (s < 0.0 || s > 1.0)        // I is outside T
                continue; //return 0;
            t = (uv * wu - uu * wv) / D;
            if (t < 0.0 || (s + t) > 1.0)  // I is outside T
                continue; //return 0;

            //return 1;                      // I is in T
            points.add(I);
            if (r < distance) {
                nearest = pointn;
                distance = r;
                pointn++;
            }

        }

        if (pointn > 1) {
            Vector3 tmp = points[0];
            points[0] = points[nearest];
            points[nearest] = tmp;
        }

        return points;
    }
}