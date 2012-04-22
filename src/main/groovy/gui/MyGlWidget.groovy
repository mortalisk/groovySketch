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

class MyGlWidget extends AWTGLCanvas implements MouseInputListener, MouseWheelListener{
    Scene scene = new Scene();
    Map<Integer,Boolean> keys;
    Map<Integer,Boolean> mouse;
    int previousMouseX;
    int previousMouseY;
    boolean mouseMoved;
    float aspect;
    float move;
    Stack<Scene> stack;
    Vector4d light_diffuse = new Vector4d(0.33, 0.33, 0.33, 1.0);
    Vector4d light_ambient = new Vector4d(0.2, 0.2, 0.2, 1.0);  /* light. */
    Vector4d light_specular = new Vector4d(1.0,1.0,1.0, 1.0);
    Vector4d light_position1 = new Vector4d(0.0, 1.0, 1.0, 0.0);  /* Infinite light location. */
    Vector4d light_position2 = new Vector4d(1.0, 1.0, -1.0, 0.0);
    Vector4d light_position3 = new Vector4d(-1.0, 1.0, -1.0, 0.0);


    MyGlWidget() {
        addMouseListener(this)
        addMouseMotionListener(this)
        addMouseWheelListener(this)
    }

    void mouseEntered(MouseEvent e) {

    }

    void mouseExited(MouseEvent mouseEvent) {

    }

    void mouseClicked(MouseEvent mouseEvent) {

    }

    void mousePressed(MouseEvent e) {

    }

    void mouseReleased(MouseEvent e) {

    }

    boolean isMousePressed(button) {

    }

    void addPoint(MouseEvent e) {

    }

    void mouseMoved(MouseEvent e) {

    }

    void mouseDragged(MouseEvent e) {

    }

    void mouseWheelMoved(MouseWheelEvent e) {

    }

    Vector3 findMouseDirection(MouseEvent e) {

    }

    void checkInput() {

    }

    void pushScene() {

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

    }
    void newLayer() {
        scene.activeNode = scene.boxNode;
    }

    void undo() {

    }

    protected void initGL() {
        glClearColor(1,1,1,0)
    }

    protected void paintGL() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        println("Width : "+width+" Height: "+height);
        if(height==0)height=1;
        glViewport(0, 0, width, height);                       // Reset The Current Viewport And Perspective Transformation
        glMatrixMode(GL_PROJECTION);                           // Select The Projection Matrix
        glLoadIdentity();                                      // Reset The Projection Matrix
        gluPerspective(45.0f, width / height, 0.1f, 100.0f);  // Calculate The Aspect Ratio Of The Window
        glMatrixMode(GL_MODELVIEW);                            // Select The Modelview Matrix
        glLoadIdentity();

        scene.getRootNode().draw();
        println 'paintGL'
//        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);       //Clear The Screen And The Depth Buffer
//        glLoadIdentity();                                         //Reset The View
//        glTranslatef(-1.5f,0.0f,-8.0f);						// Move Left 1.5 Units And Into The Screen 8(not 6.0 like VC.. not sure why)
//        glBegin(GL_TRIANGLES);								// Drawing Using Triangles
//        glVertex3f( 0.0f, 1.0f, 0.0f);					// Top
//        glVertex3f(-1.0f,-1.0f, 0.0f);					// Bottom Left
//        glVertex3f( 1.0f,-1.0f, 0.0f);					// Bottom Right
//        glEnd();											// Finished Drawing The Triangle
//        glTranslatef(3.0f,0.0f,0.0f);						// Move Right 3 Units
//        glBegin(GL_QUADS);									// Draw A Quad
//        glVertex3f(-1.0f, 1.0f, 0.0f);					// Top Left
//        glVertex3f( 1.0f, 1.0f, 0.0f);					// Top Right
//        glVertex3f( 1.0f,-1.0f, 0.0f);					// Bottom Right
//        glVertex3f(-1.0f,-1.0f, 0.0f);					// Bottom Left
//        glEnd();


        swapBuffers();
        repaint();
    }

 static main(args) {
     def count = 0
     new SwingBuilder().edt {
         frame(title:'Frame', size:[300,300], show: true,defaultCloseOperation: WindowConstants.EXIT_ON_CLOSE) {
             borderLayout()
             label('ehih',constraints: BL.WEST)
             widget(new MyGlWidget(), constraints: BL.CENTER)
             box = comboBox(items: ["hei", "ho", "hallal"], constraints: BL.NORTH)
         }
     }
 }
 
 }
