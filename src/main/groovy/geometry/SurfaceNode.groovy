package geometry
class SurfaceNode extends BaseNode
{
    Spline front, right, back, left;
    SurfaceNode below;
    // this vairable enables contruction of triangles before drawing
    // in stead of when copying which made interaction laggy
    boolean hasContructedLayer;
    void invalidate() {

    }
    SurfaceNode(String name, Spline front, Spline right, Spline back, Spline left, SurfaceNode below = null) {
        super("Surfacenode")
    }
    SurfaceNode(SurfaceNode other) {
        super(other)

    }
    void constructLayer() {

    }
    BaseNode copy() {

    }

    void prepareForDrawing() {

    }

    void determineActionOnStoppedDrawing() {

    }

    void makeRidgeNode() {

    }

    void drawChildren() {

    }
}