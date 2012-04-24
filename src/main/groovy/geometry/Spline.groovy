package geometry
class Spline {


    List<Vector3> points = [];
    boolean isSuggestion = true;


    Spline(Spline other) {
        points.addAll(other.points)
        isSuggestion = other.isSuggestion
    }
    Spline() {

    }
    void addPoint(Vector3 point) {
        points.add(point);
        isSuggestion = false;
    }


    void addPointFront(Vector3 point) {
        points.add(0,point);
        isSuggestion = false;
    }

    Vector3 getPoint(double at) {
        if (at > 1.0) {
            at = 1.0;
        }else if (at < 0.0) {
            at = 0.0;
        }

        float length = 0.0;
        for (int i = 0; i < points.size()-1; i++) {
            length += (points[i]-points[i+1]).lenght();
        }
        float pos = 0.0;
        float target = length*at;
        Vector3 r;
        for (int i = 0; i < points.size()-1; i++) {
            pos += (points[i]-points[i+1]).lenght();
            if(pos >= target) {
                float overshoot = pos-target;
                float lengthBetween = (points[i]-points[i+1]).lenght();
                float a = overshoot/lengthBetween;
                r = points[i+1]*(1-a) + (points[i]*(a));
                return r;
            }
        }
        return r;
    }

    void clear() {
        points.clear()
    }

    void addAll(List<Vector3> a) {
        points.addAll(a)
    }

    void changeLastPoint(Vector3 pos) {
        points[points.size()-1] = pos;
    }

    void setPoint(int i, Vector3 value) {
        points[i] = value
    }

    void reverse() {
        points = points.reverse()
    }

    void smooth() {
        if (this.points.size()<1) return;
        List<Vector3> newPoints = [];
        for (float p = 0; p <1.001;p+=0.01) {
            def points =new ArrayList<Vector3>( this.points)
            for (int i = 1; i< points.size(); ++i) {
                for (int j = 0;j <points.size()-i;++j) {
                    points[j] = points[j]*(1-p) + points[j+1]*p;
                }
            }
            newPoints.add(points[0]);
        }
        this.points = newPoints;
    }

    Vector3 lastPoint() {
        if (points.size() > 1) {
            return points[points.size()-2];
        }else {
            return new Vector3(-10000,-10000,-10000)
        }
    }

    int length() {
        return points.size();
    }

    boolean isLeftToRight() {
        if (points.size() == 0) return false;
        return points[0].isLeftOf(points[points.size()-1]);
    }

    Vector3 getLeftPoint() {
        if (isLeftToRight())
            return points[0]
        else if(points.size()>0)
            return points[points.size()-1]
        else {
            println "getLeftPoint called, but there are no points"
            return new Vector3(0,0,0)
        }
    }

    Vector3 getRightPoint() {
        if (isLeftToRight())
            return points[points.size()-1]
        else if (points.size() > 0)
            return points[0];
        else {
            println "getLeftPoint called, but there are no points"
            return new Vector3(0,0,0)
        }
    }

    int findNearestPoint(Vector3 searchPoint) {
        int nearest = -1
        float distance = Float.MAX_VALUE
        for (int i = 0; i < points.size(); ++i) {
            Vector3 point = points[i];
            float distanceThisFirst = (point - searchPoint).lenght();
            if (distanceThisFirst < distance) {
                nearest = i;
                distance = distanceThisFirst;
            }
        }

        return nearest;
    }

    Spline copy() {
        return new Spline(this)
    }
}