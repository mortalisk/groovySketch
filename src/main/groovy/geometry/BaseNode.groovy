package geometry

abstract class BaseNode {
    boolean active
    Shape shape
    Vector3 position = new Vector3(0,0,0)
    List<BaseNode> children
    Spline spline
    Spline sketchingSpline
    boolean drawing
    boolean splineDone
    boolean visible
    String name

    BaseNode parent

    def diffuse = []
    def ambient = []

    BaseNode(String name) {

    }
    BaseNode(Shape shape, String name) {

    }
    BaseNode(BaseNode other) {
        shape = other.shape.copy()
        position = other.position.copy()
        spline = other.spline.copy()
        sketchingSpline = other.sketchingSpline.copy()
        drawing = other.drawing
        splineDone = other.splineDone
        visible = other.visible
        diffuse = other.diffuse
        ambient = other.ambient
        name = other.name;
        other.children.each {
            BaseNode c = it.copy();
            c.parent = this;
            children.add(c);
        }
    }

    abstract BaseNode copy();

    void addChild(BaseNode child) {
        children.add(child);
    }

    void setActive(boolean a) {
        if (a == false) {
            children.each {
                it.setActive(a);
            }
        }
        active = a;
    }

    BaseNode findIntersectingNode(Vector3 from, Vector3 direction, Vector3 point) {

    }

    List<Vector3> intersectionPoints(Vector3 from,Vector3 direction) {

    }

    /** adds a point to currend spline */
    void addPoint(Vector3 from, Vector3 direction) {

    }
    /** stops drawing on current spline */
    void determineActionOnStoppedDrawing() {

    }

    void draw() {

    }

    void drawSelf() {

    }
    void drawChildren() {

    }
    void prepareForDrawing() {

    }

    BaseNode makeLayer() {

    }


    void drawSplines() {

    }
    void drawSpline(Spline spline, float r) {

    }


    void correctSketchingDirection() {

    }

    void doOversketch() {

    }
    void oversketchSide(Vector3 pointInSketch, int nearest, boolean first) {

    }
    boolean isPointNearerSide(Vector3 point, int indexInSpline) {

    }

    void moveSketchingPointsToSpline() {

    }

}
