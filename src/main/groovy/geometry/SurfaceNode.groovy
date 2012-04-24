package geometry

import javax.vecmath.Vector2d
import javax.vecmath.Vector4d

class SurfaceNode extends BaseNode
{
    Spline front, right, back, left
    SurfaceNode below
    // this vairable enables contruction of triangles before drawing
    // in stead of when copying which made interaction laggy
    boolean hasContructedLayer = false

    enum Axis {X, Y, Z}

    float resolution = 50
    int skip = 1

    List<Vector2d> uvCoordinateSpline = []
    List < List < Vector3 > > rows = []
    List < List < Vector3 > > intersectRows = []


    SurfaceNode(String name, Spline front, Spline right, Spline back, Spline left, SurfaceNode below = null) {
        super(name)
        this.front = front.copy()
        this.right = right.copy()
        this.back = back.copy()
        this.left = left.copy()
        this.below = below?.copy()
    }
    SurfaceNode(SurfaceNode other) {
        super(other)
        this.front = other.front.copy()
        this.right = other.right.copy()
        this.back = other.back.copy()
        this.left = other.left.copy()
        this.below = other.below?.copy()

    }

    void invalidate() {
        hasContructedLayer = false;
        shape = null;
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

        int p=n-1
        int q=0
        for(; q<n; p=q++)
        {
            A+= contour[p].x*contour[q].y - contour[q].x*contour[p].y;
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

        Ax = contour[V[u]].x;
        Ay = contour[V[u]].y;

        Bx = contour[V[v]].x;
        By = contour[V[v]].y;

        Cx = contour[V[w]].x;
        Cy = contour[V[w]].y;

        if ( EPSILON > (((Bx-Ax)*(Cy-Ay)) - ((By-Ay)*(Cx-Ax))) ) return false;

        for (p=0;p<n;p++)
        {
            if( (p == u) || (p == v) || (p == w) ) continue;
            Px = contour[V[p]].x;
            Py = contour[V[p]].y;
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
        int m=0
        int v=nv-1
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
                result.add( contour3d[a] );
                result.add( contour3d[b] );
                result.add( contour3d[c] );

                m++;

                /* remove v from remaining polygon */
                s=v
                t=v+1
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
                P2d.add(new Vector3(P[j].y,P[j].z,0.0D));
            } else if (axis == Axis.Y) {
                P2d.add(new Vector3(P[j].x,P[j].z,0.0D));
            } else if (axis == Axis.Z) {
                P2d.add(new Vector3(P[j].x,P[j].y,0.0D));
            }
        }

        return Process(P2d, contour, result);

    }

    void makeSide(Spline belowSpline, Spline spline,List<Vector3> normals, List<Vector3> triangles) {

        List<Vector3> front1 = new ArrayList<Vector3>()
        List<Vector3> front2 = new ArrayList<Vector3>()
        for(float i = 0.0; i < 1.0; i+= 0.02) {
            Vector3 a = belowSpline.getPoint(i);
            Vector3 b = belowSpline.getPoint(i+0.02);

            Vector3 c = spline.getPoint(i);
            Vector3 d = spline.getPoint(i+0.02);

            front1.add(a);
            front2.add(c);

        }
        for (int i = front2.size()-1; i>= 0; --i) {
            front1.add(front2[i]);
        }

        front2.clear();
        Axis axis = Axis.X;
        boolean similarZ = similar(front1[0].z,front1[front1.size()-1].z,front1[2].z);
        if (similarZ) {
            axis = Axis.Z;
        }

        triangulate(front1,front2, axis);


        for (int i = 0; i < front2.size(); i+=3) {
            Vector3 a = front2[i];
            Vector3 b = front2[i+1];
            Vector3 c = front2[i+2];
            Vector3 normal = (b-a).cross(c-a).normalize();

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

        List<Vector3> triangles = [];
        List<Vector3> previousRow = [];


        Vector3 frontRight = right.getPoint(0.0);
        Vector3 frontLeft = left.getPoint(1.0);
        Vector3 backLeft = left.getPoint(0.0);
        Vector3 backRight = right.getPoint(1.0);

        rows.clear();
        intersectRows.clear();

        for (int zi = 0;zi<resolution;++zi) {
            List<Vector3> row = [];
            float zif = zi/resolution;
            Vector3 rowLeft = frontLeft*(1.0-zif) + backLeft *(zif);
            Vector3 rowRigth = frontRight*(1.0-zif) + backRight * (zif);

            Vector3 leftp = left.getPoint(1.0-zif);
            Vector3 rightp = right.getPoint(zif);
            for (int xi = 0;xi<resolution;xi++) {
                float xif = xi/resolution;
                Vector3 colInt = rowLeft * (1.0-xif) + rowRigth * xif;
                Vector3 frontp = front.getPoint(xif);
                Vector3 backp = back.getPoint(1.0-xif);
                Vector3 frontBack = frontp*(1.0-zif)+backp*zif;
                Vector3 diff = frontBack - colInt;

                Vector3 leftRight = leftp*(1.0-xif) + rightp*xif;
                Vector3 point = leftRight + diff;
                row.add(point);
            }
            rows.add(row);
        }

//    for (int z = 0; z<resolution; z+=10) {
//        QVector<Vector3> row;
//        for (int x = 0; x < resolution; z+=10) {
//            row.push_back(rows[z][x]);
//        }
//        intersectRows.push_back(row);
//    }

        children.each { child ->

            if (child instanceof ISurfaceFeature) {
                ISurfaceFeature feature = (ISurfaceFeature)(child);
                feature.doTransformSurface(rows, resolution, 10);
                feature.repositionOnSurface(this);
            }
        }

        //compute normals
        List < List < Vector3 > > normalRows = [];
        for (int i = 0; i< rows.size(); ++i) {
            List<Vector3> row = [];
            for (int j = 0; j < rows[i].size(); ++j) {
                Vector3 a // = new Vector3(0,0,0)
                Vector3 b //= new Vector3(0,0,0)
                Vector3 c //= new Vector3(0,0,0)
                Vector3 d //= new Vector3(0,0,0)
                if (i == 0) {
                    a = rows[i+1][j] - rows[i][j];
                    a = -a;
                }else {
                    a = rows[i-1][j] - rows[i][j];
                }
                if (i == rows.size()-1) {
                    b = rows[i-1][j] - rows[i][j];
                    b = -b;
                }else {
                    b = rows[i+1][j] - rows[i][j];
                }
                if (j == 0) {
                    c = rows[i][j+1] - rows[i][j];
                    c = -c;
                }else {
                    c = rows[i][j-1] - rows[i][j];
                }
                if (j == rows[i].size()-1) {
                    d = rows[i][j-1] - rows[i][j];
                    d = -d;
                }else {
                    d = rows[i][j+1] - rows[i][j];
                }
                Vector3 n = (a.cross(d) + d.cross(b) + b.cross(c) + c.cross(a)).normalize();
                row.add( n);
            }
            normalRows.add(row);
        }

        List<Vector3> normals = [];
        //create triangles
        for (int i = 1; i< rows.size(); ++i) {
            for (int j = 1; j < rows[i].size(); ++j) {
                Vector3 a = rows[i-1][j-1];
                Vector3 na = normalRows[i-1][j-1];
                Vector3 b = rows[i-1][j];
                Vector3 nb = normalRows[i-1][j];
                Vector3 c = rows[i][j-1];
                Vector3 nc = normalRows[i][j-1];
                Vector3 d = rows[i][j];
                Vector3 nd = normalRows[i][j];
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

            }
        }

        List<Vector3> outline = [];

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

        if (below) {
            makeSide(below.front, front, normals, triangles);
            makeSide(below.left, left, normals, triangles);
            makeSide(below.back, back, normals, triangles);
            makeSide(below.right, right, normals, triangles);
        }


        hasContructedLayer = true;
        shape = new Surface(triangles, normals, outline);
    }

    BaseNode copy() {
        return new SurfaceNode(this)
    }

    void prepareForDrawing() {
        constructLayer();
    }

    void determineActionOnStoppedDrawing() {
        super.determineActionOnStoppedDrawing();

        makeRidgeNode();
    }

    void makeRidgeNode() {
        if (spline.getPoints().size() < 2)
            return;
        RidgeNode ridge = new RidgeNode(uvCoordinateSpline, this);
        ridge.parent = this;
        children.add(ridge);

        spline.clear();
        sketchingSpline.clear();
        uvCoordinateSpline.clear();

        ridge.makeWall();

        hasContructedLayer = false;
        shape = null;
    }

    def intersect(Vector3 p, Vector3 dir, Vector3 v0, Vector3 v1, Vector3 v2) {
        // hentet fra http://softsurfer.com/Archive/algorithm_0105/algorithm_0105.htm#intersect_RayTriangle%28%29
        //return values

        Vector3    u, v, n;             // triangle vectors
        Vector3    w0, w;          // ray vectors
        float      r, a, b;             // params to calc ray-plane intersect

        // get triangle edge vectors and plane normal
        u = v1 - v0;
        v = v2 - v0;
        n = u.cross(v);             // cross product
        if (n.x == 0 && n.y== 0 && n.z == 0)            // triangle is degenerate
            return [-1, null, 0, 0];               // do not deal with this case

        //dir = R.P1 - R.P0;             // ray direction vector
        w0 = p - v0;
        a = -(n * w0);
        b = n * dir;
        if (Math.abs(b) < 0.01) {     // ray is parallel to triangle plane
            if (a == 0)                // ray lies in triangle plane
                return [2, null, 0, 0];
            else return [0, null, 0, 0];             // ray disjoint from plane
        }

        // get intersect point of ray with triangle plane
        r = a / b;
        if (r < 0.0)                   // ray goes away from triangle
            return [0, null, 0,0]                  // => no intersect
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
            return [0, I, s, t];
        t = (uv * wu - uu * wv) / D;
        if (t < 0.0 || (s + t) > 1.0)  // I is outside T
            return [0, I, s, t];

        return [1, I, s, t]                      // I is in T
    }

    List<Vector3> intersectionPoints(Vector3 from,Vector3 direction) {
        List<Vector3> cand = [];
        for (int i = 0; i < rows.size()-skip; i+=skip) {
            for (int j = 0; j < rows[0].size()-skip; j+=skip) {
                Vector3 a = rows[i][j];
                Vector3 b = rows[i][j+skip];
                Vector3 c = rows[i+skip][j];
                Vector3 d = rows[i+skip][j+skip];

                Vector3 result;
                int r;
                float s,t;
                (r, result, s, t) = intersect(from, direction, a, b, c);
                if (r==1) {
                    cand.add(result);
                }

                (r, result, s, t) = intersect(from, direction, d, c, b);
                if (r==1) {
                    cand.add(result);
                }

            }
        }
        float nearestDist = 10000000;
        int nearest = -1;
        for (int i = 0; i< cand.size(); ++i) {
            float dist = (cand[i]-from).lenght();
            if (dist < nearestDist) {
                nearest = i;
                nearestDist = dist;
            }
        }
        if (cand.size() >1) {
            Vector3 tmp = cand[0];
            cand[0] = cand[nearest];
            cand[nearest] = tmp;
        }
        return cand;
    }

    void addPoint(Vector3 from, Vector3 direction) {
        List<Vector3> cand = [];
        List<Vector2d> uvCand = [];
        float resolution = 1.0/resolution;
        for (int i = 0; i < rows.size()-skip; i+=skip) {
            for (int j = 0; j < rows[0].size()-skip; j+=skip) {
                Vector3 a = rows[i][j];
                Vector3 b = rows[i][j+skip];
                Vector3 c = rows[i+skip][j];
                Vector3 d = rows[i+skip][j+skip];

                Vector3 result;
                int r;
                float s,t;
                (r, result, s, t) = intersect(from, direction, a, b, c);
                if (r==1) {
                    cand.add(result);
                    s*=resolution;
                    t*=resolution;
                    uvCand.add(new Vector2d(j*resolution+s*skip, i*resolution+t*skip));
                }

                (r, result, s, t) = intersect(from, direction, d, c, b);
                if (r==1) {
                    cand.add(result);
                    s = 1.0-s;
                    t = 1.0-t;
                    s*=resolution;
                    t*=resolution;
                    uvCand.add(new Vector2d(j*resolution+s*skip, i*resolution+t*skip));
                }

            }
        }
        float nearestDist = 10000000;
        int nearest = -1;
        for (int i = 0; i< cand.size(); ++i) {
            float dist = (cand[i]-from).lenght();
            if (dist < nearestDist) {
                nearest = i;
                nearestDist = dist;
            }
        }

        if (nearest != -1) {
            sketchingSpline.addPoint(cand[nearest]);
            uvCoordinateSpline.add(uvCand[nearest]);
        }
    }

    Vector3 getPointFromUv(Vector2d uv) {
        Vector3 frontRight = right.getPoint(0.0);
        Vector3 frontLeft = left.getPoint(1.0);
        Vector3 backLeft = left.getPoint(0.0);
        Vector3 backRight = right.getPoint(1.0);

        float xi = uv.x;
        float zi = uv.y;


        Vector3 rowLeft = frontLeft*(1.0-zi) + backLeft *(zi);
        Vector3 rowRigth = frontRight*(1.0-zi) + backRight * (zi);

        Vector3 leftp = left.getPoint(1.0-zi);
        Vector3 rightp = right.getPoint(zi);

        Vector3 colInt = rowLeft * (1.0-xi) + rowRigth * xi;
        Vector3 frontp = front.getPoint(xi);
        Vector3 backp = back.getPoint(1.0-xi);
        Vector3 frontBack = frontp*(1.0-zi)+backp*zi;
        Vector3 diff = frontBack - colInt;

        Vector3 leftRight = leftp*(1.0-xi) + rightp*xi;
        Vector3 point = leftRight + diff;
        return point;
    }
}