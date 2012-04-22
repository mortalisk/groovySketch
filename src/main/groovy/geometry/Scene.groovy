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

    void showCursor(Vector3 point, Vector3 dir) {

    }

    void addPoint(Vector3 from, Vector3 direction) {

    }

    void selectActiveNode(Vector3 from, Vector3 direction) {

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
