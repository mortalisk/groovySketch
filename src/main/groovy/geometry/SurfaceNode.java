package geometry;

import javax.vecmath.Vector2d;
import javax.vecmath.Vector4d;
import java.util.ArrayList;
import java.util.List;

class SurfaceNode extends BaseNode
{
    Spline front, right, back, left;
    SurfaceNode below;
    // this vairable enables contruction of triangles before drawing
    // in stead of when copying which made interaction laggy
    boolean hasContructedLayer = false;

    enum Axis {X, Y, Z};

    int resolution = 50;
    int skip = 1;

    List<Vector2d> uvCoordinateSpline = new ArrayList<Vector2d>();
    List < List < Vector3 > > rows = new ArrayList<List<Vector3>>();
    float[][] intersectRows = new float [resolution][resolution*3];


    SurfaceNode(String name, Spline front, Spline right, Spline back, Spline left, SurfaceNode below) {
        super(name);
        this.front = front.copy();
        this.right = right.copy();
        this.back = back.copy();
        this.left = left.copy();
        this.below = (below != null)?below.copy():null;
    }
    SurfaceNode(SurfaceNode other) {
        super(other);
        this.front = other.front.copy();
        this.right = other.right.copy();
        this.back = other.back.copy();
        this.left = other.left.copy();
        this.below = (below != null)?below.copy():null;

    }

    void invalidate() {
        hasContructedLayer = false;
        setShape(null);
    }

    boolean similar(double a, double b) {
        return Math.abs(a-b) < 0.001;
    }

    boolean similar(double a, double b, double c) {
        return similar(a,b) && similar(b,c);
    }

    static float EPSILON=0.0000000001f;

    float Area(List<Vector3> contour)
    {

        int n = contour.size();

        float A=0.0f;

        int p=n-1;
        int q=0;
        for(; q<n; p=q++)
        {
            A+= contour.get(p).getX()*contour.get(q).getY() - contour.get(q).getX()*contour.get(p).getY();
        }
        return A*0.5f;
    }

    /*
      InsideTriangle decides if a point P is Inside of the triangle
      defined by A, B, C.
    */
    boolean InsideTriangle(float Ax, float Ay,
    float Bx, float By,
    float Cx, float Cy,
    float Px, float Py)

    {
        float ax, ay, bx, by, cx, cy, apx, apy, bpx, bpy, cpx, cpy;
        float cCROSSap, bCROSScp, aCROSSbp;

        ax = Cx - Bx;  ay = Cy - By;
        bx = Ax - Cx;  by = Ay - Cy;
        cx = Bx - Ax;  cy = By - Ay;
        apx= Px - Ax;  apy= Py - Ay;
        bpx= Px - Bx;  bpy= Py - By;
        cpx= Px - Cx;  cpy= Py - Cy;

        aCROSSbp = ax*bpy - ay*bpx;
        cCROSSap = cx*apy - cy*apx;
        bCROSScp = bx*cpy - by*cpx;

        return ((aCROSSbp >= 0.0f) && (bCROSScp >= 0.0f) && (cCROSSap >= 0.0f));
    };

    boolean Snip(List<Vector3> contour,int u,int v,int w,int n,int[] V)
    {
        int p;
        float Ax, Ay, Bx, By, Cx, Cy, Px, Py;

        Ax = contour.get(V[u]).getX();
        Ay = contour.get(V[u]).getY();

        Bx = contour.get(V[v]).getX();
        By = contour.get(V[v]).getY();

        Cx = contour.get(V[w]).getX();
        Cy = contour.get(V[w]).getY();

        if ( EPSILON > (((Bx-Ax)*(Cy-Ay)) - ((By-Ay)*(Cx-Ax))) ) return false;

        for (p=0;p<n;p++)
        {
            if( (p == u) || (p == v) || (p == w) ) continue;
            Px = contour.get(V[p]).getX();
            Py = contour.get(V[p]).getY();
            if (InsideTriangle(Ax,Ay,Bx,By,Cx,Cy,Px,Py)) return false;
        }

        return true;
    }

    boolean Process(List<Vector3> contour, List<Vector3> contour3d,List<Vector3> result)
    {
        /* allocate and initialize list of Vertices in polygon */

        int n = contour.size();
        if ( n < 3 ) return false;

        int [] V = new int[n];

        /* we want a counter-clockwise polygon in V */

        if ( 0.0f < Area(contour) )
            for (int v=0; v<n; v++) V[v] = v;
        else
            for(int v=0; v<n; v++) V[v] = (n-1)-v;

        int nv = n;

        /*  remove nv-2 Vertices, creating 1 triangle every time */
        int count = 2*nv;   /* error detection */
        int m=0;
        int v=nv-1;
        while( nv>2)
        {
            /* if we loop, it is probably a non-simple polygon */
            if (0 >= (count--))
            {
                //** Triangulate: ERROR - probable bad polygon!
                return false;
            }

            /* three consecutive vertices in current polygon, <u,v,w> */
            int u = v  ; if (nv <= u) u = 0;     /* previous */
            v = u+1; if (nv <= v) v = 0;     /* new v    */
            int w = v+1; if (nv <= w) w = 0;     /* next     */

            if ( Snip(contour,u,v,w,nv,V) )
            {
                int a,b,c,s,t;

                /* true names of the vertices */
                a = V[u]; b = V[v]; c = V[w];

                /* output Triangle */
                result.add( contour3d.get(a) );
                result.add( contour3d.get(b) );
                result.add( contour3d.get(c) );

                m++;

                /* remove v from remaining polygon */
                s=v;
                t=v+1;
                while(t<nv) {V[s] = V[t]; nv--;s++;t++;}

                /* resest error detection counter */
                count = 2*nv;
            }
        }

        return true;
    }

    boolean triangulate(List<Vector3> contour,List<Vector3> result, Axis axis ) {
        List<Vector3> P = contour;
        List<Vector3> P2d = new ArrayList<Vector3>();

        for (int j = 0; j < P.size(); ++j) {
            if (axis == Axis.X) {
                P2d.add(new Vector3(P.get(j).getY(),P.get(j).getZ(),0.0f));
            } else if (axis == Axis.Y) {
                P2d.add(new Vector3(P.get(j).getX(),P.get(j).getZ(),0.0f));
            } else if (axis == Axis.Z) {
                P2d.add(new Vector3(P.get(j).getX(),P.get(j).getY(),0.0f));
            }
        }

        return Process(P2d, contour, result);

    }

    void makeSide(Spline belowSpline, Spline spline,List<Vector3> normals, List<Vector3> triangles) {

        List<Vector3> front1 = new ArrayList<Vector3>();
        List<Vector3> front2 = new ArrayList<Vector3>();
        for(float i = 0.0f; i < 1.0; i+= 0.02) {
            Vector3 a = belowSpline.getPoint(i);
            Vector3 b = belowSpline.getPoint(i+0.02);

            Vector3 c = spline.getPoint(i);
            Vector3 d = spline.getPoint(i+0.02);

            front1.add(a);
            front2.add(c);

        }
        for (int i = front2.size()-1; i>= 0; --i) {
            front1.add(front2.get(i));
        }

        front2.clear();
        Axis axis = Axis.X;
        boolean similarZ = similar(front1.get(0).getZ(),front1.get(front1.size()-1).getZ(),front1.get(2).getZ());
        if (similarZ) {
            axis = Axis.Z;
        }

        triangulate(front1,front2, axis);


        for (int i = 0; i < front2.size(); i+=3) {
            Vector3 a = front2.get(i);
            Vector3 b = front2.get(i+1);
            Vector3 c = front2.get(i+2);
            Vector3 normal = (b.minus(a)).cross(c.minus(a)).normalize();

            normals.add(normal);
            normals.add(normal);
            normals.add(normal);
            triangles.add(a);
            triangles.add(b);
            triangles.add(c);
        }

    }

    void constructLayer() {
        if (hasContructedLayer) return;

        List<Vector3> triangles = new ArrayList<Vector3>();
        List<Vector3> previousRow = new ArrayList<Vector3>() ;


        Vector3 frontRight = right.getPoint(0.0);
        Vector3 frontLeft = left.getPoint(1.0);
        Vector3 backLeft = left.getPoint(0.0);
        Vector3 backRight = right.getPoint(1.0);

        rows.clear();

        float resolution = (float)this.resolution;

        for (int zi = 0;zi<resolution;++zi) {
            List<Vector3> row = new ArrayList<Vector3>();
            float zif = zi/resolution;
            Vector3 rowLeft = frontLeft.multiply(1.0-zif).plus(backLeft.multiply(zif));
            Vector3 rowRigth = frontRight.multiply(1.0-zif).plus(backRight.multiply(zif));

            Vector3 leftp = left.getPoint(1.0-zif);
            Vector3 rightp = right.getPoint(zif);
            for (int xi = 0;xi<resolution;xi++) {
                float xif = xi/resolution;
                Vector3 colInt = rowLeft.multiply(1.0-xif).plus(rowRigth.multiply(xif));
                Vector3 frontp = front.getPoint(xif);
                Vector3 backp = back.getPoint(1.0-xif);
                Vector3 frontBack = frontp.multiply(1.0-zif).plus(backp.multiply(zif));
                Vector3 diff = frontBack.minus(colInt);

                Vector3 leftRight = leftp.multiply(1.0-xif).plus(rightp.multiply(xif));
                Vector3 point = leftRight.plus(diff);
                row.add(point);
            }
            rows.add(row);
        }

        for ( BaseNode child : getChildren()) {
            if (child instanceof ISurfaceFeature) {
                ISurfaceFeature feature = (ISurfaceFeature)(child);
                feature.doTransformSurface(rows, resolution, 10);
                feature.repositionOnSurface(this);
            }
        }

        for (int z = 0; z<this.resolution; z++) {
            float[] row = new float[this.resolution*3];
            for (int x = 0; x < this.resolution; x++) {
                Vector3 p = rows.get(z).get(x);
                row[x*3] = p.getX();
                row[x*3+1] = p.getY();
                row[x*3+2] = p.getZ();
            }
            intersectRows[z] =row;
        }

        //compute normals
        List < List < Vector3 > > normalRows = new ArrayList<List<Vector3>>();
        for (int i = 0; i< rows.size(); ++i) {
            List<Vector3> row = new ArrayList<Vector3>();
            for (int j = 0; j < rows.get(i).size(); ++j) {
                Vector3 a; // = new Vector3(0,0,0)
                Vector3 b; //= new Vector3(0,0,0)
                Vector3 c; //= new Vector3(0,0,0)
                Vector3 d; //= new Vector3(0,0,0)
                if (i == 0) {
                    a = rows.get(i+1).get(j).minus(rows.get(i).get(j));
                    a = a.negative();
                }else {
                    a = rows.get(i-1).get(j) .minus( rows.get(i).get(j));
                }
                if (i == rows.size()-1) {
                    b = rows.get(i-1).get(j) .minus( rows.get(i).get(j));
                    b = b.negative();
                }else {
                    b = rows.get(i+1).get(j) .minus( rows.get(i).get(j));
                }
                if (j == 0) {
                    c = rows.get(i).get(j+1) .minus( rows.get(i).get(j));
                    c = c.negative();
                }else {
                    c = rows.get(i).get(j-1) .minus( rows.get(i).get(j));
                }
                if (j == rows.get(i).size()-1) {
                    d = rows.get(i).get(j-1) .minus( rows.get(i).get(j));
                    d = d.negative();
                }else {
                    d = rows.get(i).get(j+1) .minus( rows.get(i).get(j));
                }
                Vector3 n = (a.cross(d) .plus(d.cross(b)).plus(b.cross(c)).plus(c.cross(a)).normalize());
                row.add( n);
            }
            normalRows.add(row);
        }

        List<Vector3> normals = new ArrayList<Vector3>();
        //create triangles
        int tris = 0;
        for (int i = 1; i< rows.size(); ++i) {
            for (int j = 1; j < rows.get(i).size(); ++j) {
                Vector3 a = rows.get(i-1).get(j - 1);
                Vector3 na = normalRows.get(i-1).get(j - 1);
                Vector3 b = rows.get(i-1).get(j);
                Vector3 nb = normalRows.get(i-1).get(j);
                Vector3 c = rows.get(i).get(j - 1);
                Vector3 nc = normalRows.get(i).get(j - 1);
                Vector3 d = rows.get(i).get(j);
                Vector3 nd = normalRows.get(i).get(j);
                triangles.add(a);
                triangles.add(b);
                triangles.add(c);
                triangles.add(b);
                triangles.add(d);
                triangles.add(c);
                normals.add(na);
                normals.add(nb);
                normals.add(nc);
                normals.add(nb);
                normals.add(nd);
                normals.add(nc);
                tris += 2;
            }
        }
        System.out.println("created " + tris + "triangles");

        List<Vector3> outline = new ArrayList<Vector3>();

        for (double i = 0.0;i<=1.01;i+=0.02) {
            outline.add(front.getPoint(i));
        }
        for (double i = 0.0;i<=1.01;i+=0.02) {
            outline.add(right.getPoint(i));
        }
        for (double i = 0.0;i<=1.01;i+=0.02) {
            outline.add(back.getPoint(i));
        }
        for (double i = 0.0;i<=1.01;i+=0.02) {
            outline.add(left.getPoint(i));
        }

        if (below != null) {
            makeSide(below.front, front, normals, triangles);
            makeSide(below.left, left, normals, triangles);
            makeSide(below.back, back, normals, triangles);
            makeSide(below.right, right, normals, triangles);
        }


        hasContructedLayer = true;
        setShape(new Surface(triangles, normals, outline));
    }

    public SurfaceNode copy() {
        return new SurfaceNode(this);
    }

    public void prepareForDrawing() {
        constructLayer();
    }

    public void determineActionOnStoppedDrawing() {
        super.determineActionOnStoppedDrawing();

        makeRidgeNode();
    }

    void makeRidgeNode() {
        if (getSpline().getPoints().size() < 2)
            return;
        RidgeNode ridge = new RidgeNode((Spline)null);
        ridge.setParent(this);
        getChildren().add(ridge);

        getSpline().clear();
        getSketchingSpline().clear();
        uvCoordinateSpline.clear();

        ridge.makeWall();

        hasContructedLayer = false;
        setShape( null);
    }

    Object[] intersect(Vector3 p, Vector3 dir, Vector3 v0, Vector3 v1, Vector3 v2) {
        // hentet fra http://softsurfer.com/Archive/algorithm_0105/algorithm_0105.htm#intersect_RayTriangle%28%29
        //return values

        Vector3    u, v, n;             // triangle vectors
        Vector3    w0, w;          // ray vectors
        float      r, a, b;             // params to calc ray-plane intersect

        // get triangle edge vectors and plane normal
        u = v1.minus(v0);
        v = v2.minus(v0);
        n = u.cross(v);             // cross product
        if (n.getX() == 0 && n.getY()== 0 && n.getZ() == 0)            // triangle is degenerate
            return new Object[]{-1, null, 0f, 0f};               // do not deal with this case

        //dir = R.P1 - R.P0;             // ray direction vector
        w0 = p.minus(v0);
        a = -(n.multiply(w0));
        b = n.multiply(dir);
        if (Math.abs(b) < 0.01) {     // ray is parallel to triangle plane
            if (a == 0)                // ray lies in triangle plane
                return new Object[]{2, null, 0f, 0f};
            else return new Object[]{0, null, 0f, 0f};             // ray disjoint from plane
        }

        // get intersect point of ray with triangle plane
        r = a / b;
        if (r < 0.0)                   // ray goes away from triangle
            return new Object[]{0, null, 0f,0f};                  // => no intersect
        // for a segment, also test if (r > 1.0) => no intersect

        Vector3 I = p.plus(dir.multiply(r));           // intersect point of ray and plane

        // is I inside T?
        float    uu, uv, vv, wu, wv, D;
        uu = u.multiply(u);
        uv = u.multiply(v);
        vv = v.multiply(v);
        w = I.minus(v0);
        wu = w.multiply(u);
        wv = w.multiply(v);
        D = uv * uv - uu * vv;

        // get and test parametric coords
        float s, t;
        s = (uv * wv - vv * wu) / D;
        if (s < 0.0 || s > 1.0)        // I is outside T
            return new Object[]{0, I, 0f, 0f};
        t = (uv * wu - uu * wv) / D;
        if (t < 0.0 || (s + t) > 1.0)  // I is outside T
            return new Object[]{0, I, 0f, 0f};

        return new Object[]{1, I, s, t};                      // I is in T
    }

    int intersect2(float px, float py, float pz,
                   float dirx, float diry, float dirz,
                    float v0x, float v0y, float v0z,
                    float v1x, float v1y, float v1z,
                    float v2x, float v2y, float v2z, float[] result) {
        // hentet fra http://softsurfer.com/Archive/algorithm_0105/algorithm_0105.htm#intersect_RayTriangle%28%29
        //return values

        float    ux, uy, uz, vx, vy, vz, nx, ny, nz;             // triangle vectors
        float    w0x, w0y, w0z, wx, wy, wz;          // ray vectors
        float      r, a, b;             // params to calc ray-plane intersect

        // get triangle edge vectors and plane normal
        ux = v1x - v0x;
        uy = v1y - v0y;
        uz = v1z - v0z;
        vx = v2x - v0x;
        vy = v2y - v0y;
        vz = v2z - v0z;
        nx = uy * vz - uz * vy;
        ny = uz * vx - ux * vz;
        nz = ux * vy - uy * vx; //u.cross(v);             // cross product
        if (nx == 0 && ny== 0 && nz == 0)            // triangle is degenerate
            return -1;               // do not deal with this case

        //dir = R.P1 - R.P0;             // ray direction vector
        w0x = px - v0x;
        w0y = py - v0y;
        w0z = pz - v0z;
        a = -(nx * w0x + ny * w0y + nz * w0z);//-(n * w0);  // dot
        b = nx * dirx + ny * diry + nz * dirz;//n * dir;
        float babs = b > 0f? b : -b;
        if (babs < 0.01) {     // ray is parallel to triangle plane
            if (a == 0)                // ray lies in triangle plane
                return 2;
            else return 0;             // ray disjoint from plane
        }

        // get intersect point of ray with triangle plane
        r = a / b;
        if (r < 0.0)                   // ray goes away from triangle
            return 0;                  // => no intersect
        // for a segment, also test if (r > 1.0) => no intersect

        float Ix = px + dirx*r;           // intersect point of ray and plane
        float Iy = py + diry*r;
        float Iz = pz + dirz*r;

        // is I inside T?
        float    uu, uv, vv, wu, wv, D;
        uu = ux*ux+uy*uy+uz*uz;
        uv = ux*vx+uy*vy+uz*vz;
        vv = vx*vx+vy*vy+vz*vz;
        wx = Ix - v0x;
        wy = Iy - v0y;
        wz = Iz - v0z;
        wu = wx*ux+wy*uy+wz*uz;
        wv = wx*vx+wy*vy+wz*vz;
        D = uv * uv - uu * vv;

        // get and test parametric coords
        float s, t;
        s = (uv * wv - vv * wu) / D;
        if (s < 0.0 || s > 1.0)        // I is outside T
            return 0;
        t = (uv * wu - uu * wv) / D;
        if (t < 0.0 || (s + t) > 1.0)  // I is outside T
            return 0;

        result[0] = Ix;
        result[1] = Iy;
        result[2] = Iz;
        result[3] = s;
        result[4] = t;
        return 1;                      // I is in T
    }

    public List<Vector3> intersectionPoints(Vector3 from,Vector3 direction) {
        List<Vector3> cand = new ArrayList<Vector3>();
        int skip2 = skip*3;
        float[] result = new float[5];
        for (int i = 0; i < intersectRows.length-skip; i+=skip) {
            for (int j = 0; j < intersectRows[0].length-skip2; j+=skip2) {
                float ax = intersectRows[i][j];
                float ay = intersectRows[i][j+1];
                float az = intersectRows[i][j+2];
                float bx = intersectRows[i][j+skip2];
                float by = intersectRows[i][j+skip2+1];
                float bz = intersectRows[i][j+skip2+2];
                float cx = intersectRows[i+skip][j];
                float cy = intersectRows[i+skip][j+1];
                float cz = intersectRows[i+skip][j+2];
                float dx = intersectRows[i+skip][j+skip2];
                float dy = intersectRows[i+skip][j+skip2+1];
                float dz = intersectRows[i+skip][j+skip2+2];


                int r;
                float s,t;
                r = intersect2(from.getX(),from.getY(),from.getZ(),
                        direction.getX(),direction.getY(),direction.getZ(),
                        ax,ay,az, bx,by,bz, cx, cy, cz, result);
                if (r==1) {
                    cand.add(new Vector3(result[0], result[1], result[2]));
                }

                r = intersect2(from.getX(),from.getY(),from.getZ(),
                        direction.getX(),direction.getY(),direction.getZ(),
                        dx,dy,dz, cx, cy, cz, bx,by,bz, result);
                if (r==1) {
                    cand.add(new Vector3(result[0], result[1], result[2]));
                }

            }
        }
        float nearestDist = 10000000;
        int nearest = -1;
        for (int i = 0; i< cand.size(); ++i) {
            float dist = (cand.get(i).minus(from).lenght());
            if (dist < nearestDist) {
                nearest = i;
                nearestDist = dist;
            }
        }
        if (cand.size() >1) {
            Vector3 tmp = cand.get(0);
            cand.set(0,cand.get(nearest));
            cand.set(nearest,tmp);
        }
        return cand;
    }

    public void addPoint(Vector3 from, Vector3 direction) {
        List<Vector3> cand = new ArrayList<Vector3>();
        List<Vector2d> uvCand = new ArrayList<Vector2d>();
        float resolution = 1.0f/this.resolution;
        for (int i = 0; i < rows.size()-skip; i+=skip) {
            for (int j = 0; j < rows.get(0).size()-skip; j+=skip) {
                Vector3 a = rows.get(i).get(j);
                Vector3 b = rows.get(i).get(j+skip);
                Vector3 c = rows.get(i+skip).get(j);
                Vector3 d = rows.get(i+skip).get(j+skip);

                Vector3 result;
                int r;
                float s,t;
                Object[] objs = intersect(from, direction, a, b, c);
                r = (Integer)objs[0];
                result = (Vector3)objs[1];
                s = (Float)objs[2];
                t = (Float)objs[3];
                if (r==1) {
                    cand.add(result);
                    s*=resolution;
                    t*=resolution;
                    uvCand.add(new Vector2d(j*resolution+s*skip, i*resolution+t*skip));
                }

                objs = intersect(from, direction, d, c, b);
                r = (Integer)objs[0];
                result = (Vector3)objs[1];
                s = (Float)objs[2];
                t = (Float)objs[3];
                if (r==1) {
                    cand.add(result);
                    s = 1.0f-s;
                    t = 1.0f-t;
                    s*=resolution;
                    t*=resolution;
                    uvCand.add(new Vector2d(j*resolution+s*skip, i*resolution+t*skip));
                }

            }
        }
        float nearestDist = 10000000;
        int nearest = -1;
        for (int i = 0; i< cand.size(); ++i) {
            float dist = (cand.get(i).minus(from)).lenght();
            if (dist < nearestDist) {
                nearest = i;
                nearestDist = dist;
            }
        }

        if (nearest != -1) {
            getSketchingSpline().addPoint(cand.get(nearest));
            uvCoordinateSpline.add(uvCand.get(nearest));
        }
    }

    Vector3 getPointFromUv(Vector2d uv) {
        Vector3 frontRight = right.getPoint(0.0);
        Vector3 frontLeft = left.getPoint(1.0);
        Vector3 backLeft = left.getPoint(0.0);
        Vector3 backRight = right.getPoint(1.0);

        float xi = (float)uv.x;
        float zi = (float)uv.y;


        Vector3 rowLeft = frontLeft.multiply(1.0-zi).plus(backLeft.multiply(zi));
        Vector3 rowRigth = frontRight.multiply(1.0-zi).plus(backRight.multiply(zi));

        Vector3 leftp = left.getPoint(1.0-zi);
        Vector3 rightp = right.getPoint(zi);

        Vector3 colInt = rowLeft.multiply(1.0-xi).plus( rowRigth.multiply( xi));
        Vector3 frontp = front.getPoint(xi);
        Vector3 backp = back.getPoint(1.0-xi);
        Vector3 frontBack = frontp.multiply(1.0-zi).plus(backp.multiply(zi));
        Vector3 diff = frontBack.minus(colInt);

        Vector3 leftRight = leftp.multiply(1.0-xi) .plus( rightp.multiply(xi));
        Vector3 point = leftRight .plus(diff);
        return point;
    }
}