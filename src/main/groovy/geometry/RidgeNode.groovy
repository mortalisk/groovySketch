package geometry
class RidgeNode extends BaseNode implements ISurfaceFeature {
    Spline baseSpline;

    RidgeNode(RidgeNode o) { super(o) }

    RidgeNode(Spline spline) {
        super("Ridgenode")
    }

    BaseNode copy() {
        return new RidgeNode(this);
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