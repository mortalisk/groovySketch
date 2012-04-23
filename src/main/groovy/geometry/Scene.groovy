package geometry

import javax.vecmath.Vector4d

class Scene {
    float resolution
    boolean snapToGrid
    boolean onSurface
    Camera camera = new Camera()
    Sphere sphere
    Sphere cursorSphere
    BoxNode boxNode
    BaseNode cursor
    BaseNode activeNode

    Scene() {
        snapToGrid = false;
        resolution = 0.05f;
        sphere = new Sphere(0.05f);
        cursorSphere = new Sphere(0.05f);
        cursor = new GeneralNode(cursorSphere,"cursor");
        cursor.position = new Vector3(0,0,0);
        cursor.ambient = new Vector4d(1.0, 0.0, 0.0, 1.0);
        boxNode = new BoxNode();
        camera.setTrackMode(Camera.TrackMode.SPHERE_TRACK, new Vector3(0,0,0), new Vector3(10,10,10) );
        activeNode = boxNode;

    }

    void showCursor(Vector3 from, Vector3 direction) {
        List<Vector3> points = activeNode.intersectionPoints(from,direction);
        if ( points.size() > 0) {
            float x = points[0].x;
            float y = points[0].y;
            float z = points[0].z;
            if (snapToGrid) {
                x = Math.round( x / resolution)*resolution;
                y = Math.round( y / resolution)*resolution;
                z = Math.round( z / resolution)*resolution;
            }
            cursor.position = new Vector3(x,y,z);
            onSurface = true;
        } else {
            Vector3 pos = camera.position + direction.normalize()*5;
            cursor.position = pos;
            onSurface = false;
        }
    }

    void addPoint(Vector3 from, Vector3 direction) {
        if (onSurface) {
            activeNode.addPoint(from, direction);
        }
    }

    void selectActiveNode(Vector3 from, Vector3 direction) {

        Vector3 point;
        activeNode.setActive(false);
        activeNode = boxNode.findIntersectingNode(from, direction, point);
        if (activeNode == null)
            activeNode = boxNode;
        activeNode.setActive(true);
    }

    void makeLayer() {

    }

    BaseNode getRootNode() {

        return boxNode;

    }

    Scene(Scene scene) {
        this.resolution = scene.resolution

        this.snapToGrid = scene.snapToGrid
        this.onSurface = scene.onSurface
        this.camera = scene.camera
        this.sphere = scene.sphere
        this.cursorSphere = scene.cursorSphere
        this.cursor = scene.cursor
        this.boxNode = scene.boxNode.copy();
        this.cursor = new GeneralNode(this.cursorSphere, "cursor");
        this.cursor.ambient = scene.cursor.ambient;
        this.activeNode = this.boxNode;
    }

}
