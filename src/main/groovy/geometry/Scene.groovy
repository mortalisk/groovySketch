package geometry

import javax.vecmath.Vector4d

class Scene {
    float resolution
    boolean snapToGrid
    boolean onSurface
    int editLayerNo
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
        editLayerNo = -1;

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
        if (editLayerNo == -1) {
            activeNode = boxNode.makeLayer();
        }
    }

    void editLayer() {
        if (activeNode instanceof SurfaceNode && editLayerNo == -1) {
            SurfaceNode sn = (SurfaceNode)(activeNode);
            for (int i = 0; i< boxNode.children.size(); ++i) {
                if (boxNode.children[i] == sn) {
                    editLayerNo = i;
                    break;
                }
            }
            boxNode.frontNode.spline = sn.front;
            boxNode.leftNode.spline = sn.left;
            boxNode.rightNode.spline = sn.right;
            boxNode.backNode.spline = sn.back;
            sn.visible = false;
            activeNode = boxNode;
        } else if(editLayerNo != -1) {


            SurfaceNode node = (SurfaceNode)(boxNode.children[editLayerNo]);
            node.front = boxNode.frontNode.spline;
            node.left = boxNode.leftNode.spline;
            node.back = boxNode.backNode.spline;
            node.right = boxNode.rightNode.spline;
            node.invalidate();
            node.visible = true;
            editLayerNo = -1;
        }
    }

    BaseNode getRootNode() {

        return boxNode;

    }



}
