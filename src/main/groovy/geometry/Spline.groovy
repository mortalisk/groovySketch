package geometry
class Spline extends Shape {

    Spline() {

    }
    List<Vector3> points;
    boolean isSuggestion;
    void addPoint(Vector3 point) {
        points.add(point);
        isSuggestion = false;
    }

    Vector3 getPoint(float at) {

    }

    void changeLastPoint(Vector3 pos) {
        points[points.size()-1] = pos;
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
            return new Vector3()
        }
    }

    Vector3 getRightPoint() {
        if (isLeftToRight())
            return points[points.size()-1]
        else if (points.size() > 0)
            return points[0];
        else {
            println "getLeftPoint called, but there are no points"
            return new Vector3()
        }
    }

    int findNearestPoint(Vector3 first) {

    }
}