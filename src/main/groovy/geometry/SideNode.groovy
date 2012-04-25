package geometry

import javax.vecmath.Vector4d

class SideNode extends BaseNode {
    SideNode opposite;
    SideNode left;
    SideNode right;
    Vector3 lowerLeft;
    Vector3 lowerRigth;
    Vector3 upperRigth;
    Vector3 upperLeft;
    SideNode(Vector3 lowerLeft,Vector3 lowerRigth, Vector3 upperRigth,Vector3 upperLeft) {

        super("sidenode")
        this.lowerLeft = lowerLeft
        this.lowerRigth = lowerRigth
        this.upperRigth = upperRigth
        this.upperLeft = upperLeft

        List<Vector3> vertices = [];
        List<Vector3> normals = [];

        List<Vector3> lineVertices = [];
        Vector3 normal = (lowerRigth - lowerLeft).cross((upperRigth-lowerLeft)).normalize();
        Vector4d c = new Vector4d(1.0, 1.0, 1.0, 0.5);

        //Front
        vertices.add(lowerLeft);
        vertices.add(lowerRigth);
        vertices.add(upperLeft);

        vertices.add(upperLeft);
        vertices.add(lowerRigth);
        vertices.add(upperRigth);

        normals.add(normal);
        normals.add(normal);
        normals.add(normal);
        normals.add(normal);
        normals.add(normal);
        normals.add(normal);

        lineVertices.add(lowerLeft);
        lineVertices.add(lowerRigth);
        lineVertices.add(upperRigth);
        lineVertices.add(upperLeft);
        lineVertices.add(lowerLeft);

        this.shape = new Surface(vertices,normals, lineVertices);
    }
    SideNode(SideNode o) {
        super(o)
        lowerLeft = o.lowerLeft
        lowerRigth = o.lowerRigth
        upperRigth = o.upperRigth
        upperLeft = o.upperLeft
    }

    BaseNode copy() {
        return new SideNode(this)
    }

    void projectPoints(Vector3 diff,List<Vector3> points) {
        points.each{
            spline.addPoint(it+diff)
        }
    }

    boolean isPointNearerSide(Vector3 point, int indexInSpline) {
        def  leftSide = new Vector3(lowerLeft.x, point.y, lowerLeft.z)
        def  rightSide = new Vector3(lowerRigth.x, point.y, lowerRigth.z)
        Vector3 inSpline = spline.points[indexInSpline]

        float distLeft = (leftSide-point).lenght()
        float distRight = (rightSide-point).lenght()
        float distSpline = (inSpline-point).lenght()
        return distLeft < distSpline || distRight < distSpline
    }

    void addInterpolatedSuggestion(float yLeft, float yRight) {
        ensureLeftToRigth()
        if(spline.isSuggestion) {
            spline.clear()

            def pointA = new Vector3(lowerLeft.x, yLeft, lowerLeft.z)
            def pointB = new Vector3(lowerRigth.x, yRight, lowerRigth.z)
            for (float i = 0.0; i<1.01; i+=0.05) {
                Vector3 add = interpolate(pointA, pointB, i)
                spline.addPoint(add)
            }
            spline.isSuggestion = true
        }else {
            def points = spline.points;
            Vector3 first = points[0]
            Vector3 last = points[points.size()-1]
            float length = 0.0
            for (int i = 1; i< points.size();++i) {
                length += (points[i-1]-points[i]).lenght()
            }
            float along = 0.0;
            Vector3 previous = first;
            for (int i = 0; i <points.size(); ++i) {
                along += (previous-points[i]).lenght();
                previous = points[i];
                float w = along/length;
                float targetY = yLeft*(1.0-w) + yRight*w;
                float lineY = first.y*(1.0-w)+last.y*w;
                float diff = points[i].y - lineY;
                float newY = targetY + diff;
                spline.setPoint(i, new Vector3(points[i].x, newY, points[i].z))
            }
        }
    }

    Vector3 interpolate(Vector3 pointA, Vector3 pointB, float t) {
        return pointA*t + pointB*(1.0-t)
    }

    void ensureLeftToRigth() {
        if (!spline.isLeftToRight()) {
            spline.reverse()
        }
    }

    void assignOpposites(SideNode node) {
        this.opposite = node;
        node.opposite = this;
    }
    void assignLeft(SideNode node) {
        this.left = node;
        node.right = this;
    }
    void makeSuggestionLines() {
        ensureLeftToRigth();
        List<Vector3> points = spline.points
        Vector3 first = points[0];
        Vector3 last = points[points.size()-1];
        Vector3 left = new Vector3(lowerLeft.x,first.y,lowerLeft.z);
        Vector3 right = new Vector3(lowerRigth.x,last.y,lowerRigth.z);

        float rdist = (right-last).lenght();
        float ldist = (left-first).lenght();
        float dist = (left-right).lenght();
        float totalPoints = 20.0;
        if (ldist > 0.001) {
            float lPoints = totalPoints*ldist/dist;
            float lInc = ldist/lPoints;
            for (float i = lInc; i< lPoints+lInc; i+=lInc) {
                if (i > lPoints) i= lPoints;
                spline.addPointFront(interpolate(left, first, (float)i/lPoints));
                if (i == lPoints) break;
            }
        }
        if (rdist > 0.001) {
            float rPoints = totalPoints*rdist/dist;
            float rInc = rdist/rPoints;
            for (float i = rInc; i< rPoints+rInc; i+=rInc) {
                if (i > rPoints) i= rPoints;
                spline.addPoint(interpolate(right, last,(float)i/rPoints));
                if (i == rPoints) break;
            }
        }
    }

}