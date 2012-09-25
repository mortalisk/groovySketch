package geometry


import static org.lwjgl.opengl.GL11.*
import javax.vecmath.Vector4d
import java.nio.FloatBuffer
import org.lwjgl.BufferUtils

abstract class BaseNode {
    boolean active
    Shape shape
    Vector3 position = new Vector3(0,0,0)
    List<BaseNode> children = []
    Spline spline = new Spline()
    Spline sketchingSpline = new Spline()
    boolean drawing
    boolean splineDone
    boolean visible = true
    String name

    BaseNode parent

    def diffuse = new Vector4d(1.0,1.0,1.0,1.0)
    def ambient = new Vector4d(0.5,0.5,0.5,1.0)

    BaseNode(String name) {
          this.name = name
    }
    BaseNode(Shape shape, String name) {
        this.shape = shape
        this.name = name
    }
    BaseNode(BaseNode other) {
        position.set(other.position)
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

    Vector3 tmp = new Vector3()
    BaseNode findIntersectingNode(Vector3 from, Vector3 direction) {
        BaseNode found = null;
        float distance = Float.MAX_VALUE;
        children.each { child ->
            BaseNode foundLast = child.findIntersectingNode(from, direction,tmp);
            float distLast = (tmp - from).lenght();
            if (foundLast && distLast < distance) {
                found = foundLast;
                distance = distLast;
            }
        }
        if (found == null) {
            List<Vector3> points = intersectionPoints(from, direction);
            if (points?.size() > 0) {
                return this;
            } else {
                return null;
            }
        } else {
            return found;
        }
    }

    Vector3 fromNew = new Vector3()
    Vector3 directionNew = new Vector3()
    List<Vector3> intersectionPoints(Vector3 from,Vector3 direction) {
        fromNew.set(from) - position;
        directionNew.set(direction) - position;
        if (shape) {
            return shape.intersectionPoints(fromNew, direction);
        } else {
            return new ArrayList<Vector3>();
        }
    }

    /** adds a point to currend spline */
    void addPoint(Vector3 from, Vector3 direction) {
        List<Vector3> points = intersectionPoints(from, direction);
        if (points.size() > 0) {
            sketchingSpline.addPoint(points[0]);
        }
    }
    /** stops drawing on current spline */
    void determineActionOnStoppedDrawing() {
        correctSketchingDirection();

        doOversketch();
    }

    void draw() {

        glPushMatrix();

        prepareForDrawing();
        drawChildren();

        drawSelf();


        glPopMatrix();

    }

    Vector4d red = new Vector4d(1.0,0.0,0.0,1.0);
    void drawSelf() {

        glTranslated(position.x, position.y, position.z);

        if (shape) {
            shape.drawLines(!visible);
            if (visible) {
                Vector4d color = diffuse;
                if (active) {
                    color = red;
                }
                shape.drawShape(ambient, color);
            }
        }
        drawSplines();

    }
    void drawChildren() {
        children.each {
            it.draw();
        }
    }
    void prepareForDrawing() {

    }

    BaseNode makeLayer() {

    }


    void drawSplines() {
        drawSpline(sketchingSpline, 1);
        drawSpline(spline, 0);
    }
    void drawSpline(Spline spline, float r) {
        if (spline?.points?.size() >= 1) {
            for (int i = 0; i < spline.points.size() - 1; ++i) {

                glLineWidth(2.0f);
                glPointSize(3.0f);
                glBegin(GL_POINT);
                float[] a = [0.0,0.0,0.0,1.0]
                FloatBuffer c = BufferUtils.createFloatBuffer(4)
                c.put(a)
                c.rewind()
                glMaterial(GL_FRONT, GL_AMBIENT_AND_DIFFUSE, c);
                glVertex3d(spline.points[i].x,spline.points[i].y,spline.points[i].z);
                glEnd();
                glBegin(GL_LINES);
                glColor3f(r, 0, 0);
                glVertex3d(spline.points[i].x, spline.points[i].y,
                        spline.points[i].z);
                glVertex3d(spline.points[i + 1].x, spline.points[i + 1].y,
                        spline.points[i + 1].z);
                glEnd();
            }
        }
    }


    void correctSketchingDirection() {
        boolean isOpposite = spline.isLeftToRight() != sketchingSpline.isLeftToRight();

        if (isOpposite) {
            sketchingSpline.reverse()
        }

        sketchingSpline.smooth()
    }

    void doOversketch() {
        if (sketchingSpline.points.size() < 2) {
            sketchingSpline.clear();
            return;
        }
        Vector3 first = sketchingSpline.points[0];
        int nearestFirst = spline.findNearestPoint(first);
        Vector3 last = sketchingSpline.points[sketchingSpline.points.size() - 1];
        int nearestLast = spline.findNearestPoint(last);

        oversketchSide(first, nearestFirst, true);
        oversketchSide(last, nearestLast, false);

        moveSketchingPointsToSpline();
    }
    void oversketchSide(Vector3 pointInSketch, int nearest, boolean first) {
        if (isPointNearerSide(pointInSketch, nearest)) return;

        if (nearest != 0) {
            if (first) {
                for (int i = nearest; i >= 0; --i) {
                    sketchingSpline.addPointFront(spline.points[i])
                }
            }else {
                for (int i = nearest; i < spline.points.size()-1; ++i) {

                    sketchingSpline.addPoint(spline.points[i]);
                }
            }
        }
    }
    boolean isPointNearerSide(Vector3 point, int indexInSpline) {
        return false;
    }

    void moveSketchingPointsToSpline() {
        spline.clear();
        spline.addAll(sketchingSpline.points);
        sketchingSpline.clear();
    }

}
