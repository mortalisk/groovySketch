package geometry
class RiverNode extends BaseNode implements ISurfaceFeature {
    Spline baseSpline;

    RiverNode(RiverNode o) { super(o) }

    RiverNode(Spline spline) {
        super("rivernode")
    }

    BaseNode copy() {
        return new RiverNode(this);
    }

    void makeWall() {

    }

    void repositionOnSurface(SurfaceNode surfacenode) {

    }

    void doTransformSurface(List<List<Vector3>> rows) {

    }

    List<Vector3> intersectionPoints(Vector3 from, Vector3 direction) {

    }

    void drawSelf() {

    }

    void determineActionOnStoppedDrawing() {

    }
}