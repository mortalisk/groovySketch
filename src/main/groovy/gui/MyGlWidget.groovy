package gui

import org.lwjgl.opengl.AWTGLCanvas
import static org.lwjgl.opengl.GL11.*

import static org.lwjgl.util.glu.GLU.*
import groovy.swing.SwingBuilder
import java.awt.BorderLayout as BL
import javax.vecmath.Vector3d
import geometry.Scene
import javax.vecmath.Vector4d
import java.awt.event.MouseListener
import java.awt.event.KeyListener
import java.awt.event.MouseEvent
import java.awt.event.MouseMotionListener
import javax.swing.event.MouseInputListener
import java.awt.event.MouseWheelListener
import java.awt.event.MouseWheelEvent
import geometry.Vector3
import java.awt.Color
import javax.swing.WindowConstants
import java.nio.FloatBuffer
import org.lwjgl.BufferUtils
import java.awt.event.ComponentListener
import java.awt.event.ComponentEvent
import geometry.Camera
import java.awt.Cursor
import java.awt.Point
import java.awt.image.BufferedImage

class MyGlWidget extends AWTGLCanvas implements MouseInputListener, MouseWheelListener, ComponentListener{
    Scene scene = new Scene();
    Map<Integer,Boolean> keys = [:]
    Map<Integer,Boolean> mouse = [:]
    int previousMouseX;
    int previousMouseY;
    boolean mouseMoved;
    float aspect = 1;
    float move;
    Stack<Scene> stack = new Stack<Scene>()
    float[] light_diffuse = [0.33, 0.33, 0.33, 1.0]
    float[]  light_ambient = [0.2, 0.2, 0.2, 1.0]  /* light. */
    float[]  light_specular = [1.0,1.0,1.0, 1.0]
    float[]  light_position1 = [0.0, 1.0, 1.0, 0.0] /* Infinite light location. */
    float[]  light_position2 = [1.0, 1.0, -1.0, 0.0]
    float[]  light_position3 = [-1.0, 1.0, -1.0, 0.0]


    MyGlWidget() {
        addMouseListener(this)
        addMouseMotionListener(this)
        addMouseWheelListener(this)
        addComponentListener(this)
        cursor = toolkit.createCustomCursor(new BufferedImage(3, 3, BufferedImage.TYPE_INT_ARGB), new Point(0, 0),"null")
        pushScene()
    }

    void componentResized(ComponentEvent e) {
        aspect = width/height;
    }

    void mouseEntered(MouseEvent e) {

    }

    void mouseExited(MouseEvent mouseEvent) {

    }

    void mouseClicked(MouseEvent mouseEvent) {

    }

    void mousePressed(MouseEvent e) {
        int button = e.button
        mouse[button] = true

        mouseMoved = false
        previousMouseX = e.x
        previousMouseY = e.y
        if (isMousePressed(MouseEvent.BUTTON1)) {
            addPoint(e)
        }
    }

    void mouseReleased(MouseEvent e) {
        int button = e.button
        mouse[button] = false

        if ((!mouseMoved) && button == MouseEvent.BUTTON3) {
            Vector3 dir = findMouseDirection(e)
            scene.selectActiveNode(scene.camera.position, dir)
        }

        if (mouseMoved && button == MouseEvent.BUTTON1) {
            scene.activeNode.determineActionOnStoppedDrawing()
            println "pushing scene to stack"

            pushScene()
        }
    }

    boolean isMousePressed(int button) {
        return mouse[button]
    }

    void addPoint(MouseEvent e) {
        Vector3 dir = findMouseDirection(e)
        scene.addPoint(scene.camera.position, dir)
    }

    void mouseMoved(MouseEvent e) {
        if (isMousePressed(MouseEvent.BUTTON3)) {
            int movex = e.x -previousMouseX;
            int movey = e.y -previousMouseY;
            scene.camera.goUp(movey/100.0);
            scene.camera.goRight(-movex/100.0);
        }

        Vector3 dir = findMouseDirection(e);
        scene.showCursor(scene.camera.position,dir);
        mouseMoved = true;

        if (isMousePressed(MouseEvent.BUTTON1)) {
            addPoint(e);
        }

        previousMouseX = e.x
        previousMouseY = e.y
    }

    void mouseDragged(MouseEvent e) {
        if (isMousePressed(MouseEvent.BUTTON3)) {
            int movex = e.x -previousMouseX;
            int movey = e.y -previousMouseY;
            scene.camera.goUp(movey/100.0);
            scene.camera.goRight(-movex/100.0);
        }

        Vector3 dir = findMouseDirection(e);
        scene.showCursor(scene.camera.position,dir);
        mouseMoved = true;

        if (isMousePressed(MouseEvent.BUTTON1)) {
            addPoint(e);
        }

        previousMouseX = e.x
        previousMouseY = e.y
    }

    void mouseWheelMoved(MouseWheelEvent e) {
        scene.camera.goForward(e.wheelRotation);
    }

    Vector3 tmp = new Vector3()
    Vector3 tmp2 = new Vector3()
    Vector3 tmp3 = new Vector3()
    Vector3 findMouseDirection(MouseEvent e) {
        // cursor move
        float a = scene.camera.fov/2.0f
        float h = this.height/2.0f
        float w = this.width/2.0f
        float l = h/Math.tan(a)
        Vector3 forw = tmp.set(scene.camera.forward).normalize()*l
        float u = h - e.y
        float r = e.x - w
        Vector3 up = tmp2.set(scene.camera.up).normalize()*u
        Vector3 right = tmp3.set(forw).cross(scene.camera.up).normalize()*r
        return ((forw + up) + right) + scene.camera.position
    }

    void checkInput() {

    }

    void pushScene() {
        stack.push(new Scene(scene));
        sceneChanged(scene);
    }

    void toggleVisibility(int i) {

    }

    void setColor(int i, Color c) {

    }

    void sceneChanged(Scene s) {

    }

    void setLayer(int l) {

    }
    void animate() {

    }
    void makeLayer() {
        scene.makeLayer()

    }
    void newLayer() {
        scene.activeNode = scene.boxNode;
    }

    void undo() {
        Camera c = scene.camera;
        if (stack.size() > 1) {
            scene = stack.pop();
            scene = stack.pop();
        }
        pushScene();

        scene.camera = c;

        sceneChanged(scene);
    }

    protected void initGL() {
        glClearColor(0.8,0.8,1,0)

        glEnable (GL_BLEND);
        glBlendFunc (GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glEnable(GL_DEPTH_TEST);
        //glColorMaterial ( GL_FRONT_AND_BACK, GL_AMBIENT_AND_DIFFUSE ) ;

        glEnable(GL_LIGHTING);
        glEnable(GL_LIGHT0);
        glEnable(GL_LIGHT1);
        glEnable(GL_LIGHT2);
        glEnable(GL_NORMALIZE);



        def lightDiffuse = makeBuffer(light_diffuse)  /* diffuse light. */
        def lightAmbient = makeBuffer(light_ambient)
        def lightSpecular = makeBuffer(light_specular)
        def lightPosition1 = makeBuffer(light_position1)
        def lightPosition2 = makeBuffer(light_position2)
        def lightPosition3 = makeBuffer(light_position3)


        glLight(GL_LIGHT0, GL_DIFFUSE, lightDiffuse);
        glLight(GL_LIGHT0, GL_AMBIENT, lightAmbient);
        glLight(GL_LIGHT0, GL_SPECULAR, lightSpecular);
        glLight(GL_LIGHT0, GL_POSITION, lightPosition1);

        glLight(GL_LIGHT1, GL_DIFFUSE, lightDiffuse);
        glLight(GL_LIGHT1, GL_AMBIENT, lightAmbient);
        glLight(GL_LIGHT1, GL_SPECULAR, lightSpecular);
        glLight(GL_LIGHT1, GL_POSITION, lightPosition2);

        glLight(GL_LIGHT2, GL_DIFFUSE, lightDiffuse);
        glLight(GL_LIGHT2, GL_AMBIENT, lightAmbient);
        glLight(GL_LIGHT2, GL_SPECULAR, lightSpecular);
        glLight(GL_LIGHT2, GL_POSITION, lightPosition3);
    }

    FloatBuffer makeBuffer(float []c) {
        FloatBuffer buf = BufferUtils.createFloatBuffer(4)
        buf.put(c[0])
        buf.put(c[1])
        buf.put(c[2])
        buf.put(c[3])
        buf.rewind()
        return buf
    }

    protected void paintGL() {

        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        gluPerspective((float)scene.camera.fov*180/Math.PI, aspect, 0.1f, 1000f);

        glViewport(0,0,width,height);

        glMatrixMode(GL_MODELVIEW);
        glLoadIdentity();
        gluLookAt((float)scene.camera.position.x,
                (float)scene.camera.position.y,
                (float)scene.camera.position.z,
                (float)scene.camera.looksAt().x,
                (float)scene.camera.looksAt().y,
                (float)scene.camera.looksAt().z,
                (float)scene.camera.up.x,
                (float)scene.camera.up.y,
                (float)scene.camera.up.z);


        scene.getRootNode().draw()
        scene.cursor.draw()


        glLightModelf(GL_LIGHT_MODEL_LOCAL_VIEWER, 1.0f);

        swapBuffers();
        repaint();
    }

 static main(args) {
     def count = 0
     new SwingBuilder().edt {
         frame(title:'Frame', size:[800,600], show: true,defaultCloseOperation: WindowConstants.EXIT_ON_CLOSE) {
             glwidget = new MyGlWidget()

             borderLayout()

             panel(constraints: BL.NORTH, alignmentX: LEFT_ALIGNMENT) {
                makeLayer = button(text: 'Make Layer', actionPerformed: {glwidget.makeLayer()})
                box = comboBox(items: ["hei", "ho", "hallal"])
             }
             widget(glwidget, constraints: BL.CENTER)

         }
     }
 }
 
 }
