package morten

import org.lwjgl.opengl.AWTGLCanvas
import static org.lwjgl.opengl.GL11.*
import static org.lwjgl.util.glu.GLU.*
import groovy.swing.SwingBuilder
import java.awt.BorderLayout as BL

class MyGlWidget extends AWTGLCanvas{

    MyGlWidget() {
        println 'hello'
    }

    protected void initGL() {
        glClearColor(0,0,0,0)
    }

    protected void paintGL() {

        println("Width : "+width+" Height: "+height);
        if(height==0)height=1;
        glViewport(0, 0, width, height);                       // Reset The Current Viewport And Perspective Transformation
        glMatrixMode(GL_PROJECTION);                           // Select The Projection Matrix
        glLoadIdentity();                                      // Reset The Projection Matrix
        gluPerspective(45.0f, width / height, 0.1f, 100.0f);  // Calculate The Aspect Ratio Of The Window
        glMatrixMode(GL_MODELVIEW);                            // Select The Modelview Matrix
        glLoadIdentity();


        println 'paintGL'
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);       //Clear The Screen And The Depth Buffer
        glLoadIdentity();                                         //Reset The View
        glTranslatef(-1.5f,0.0f,-8.0f);						// Move Left 1.5 Units And Into The Screen 8(not 6.0 like VC.. not sure why)
        glBegin(GL_TRIANGLES);								// Drawing Using Triangles
        glVertex3f( 0.0f, 1.0f, 0.0f);					// Top
        glVertex3f(-1.0f,-1.0f, 0.0f);					// Bottom Left
        glVertex3f( 1.0f,-1.0f, 0.0f);					// Bottom Right
        glEnd();											// Finished Drawing The Triangle
        glTranslatef(3.0f,0.0f,0.0f);						// Move Right 3 Units
        glBegin(GL_QUADS);									// Draw A Quad
        glVertex3f(-1.0f, 1.0f, 0.0f);					// Top Left
        glVertex3f( 1.0f, 1.0f, 0.0f);					// Top Right
        glVertex3f( 1.0f,-1.0f, 0.0f);					// Bottom Right
        glVertex3f(-1.0f,-1.0f, 0.0f);					// Bottom Left
        glEnd();


        swapBuffers();
        repaint();
    }

 static main(args) {
     def count = 0
     new SwingBuilder().edt {
         frame(title:'Frame', size:[300,300], show: true) {
             borderLayout()
             label('ehih',constraints: BL.WEST)
             widget(new MyGlWidget(), constraints: BL.CENTER)
             box = comboBox(items: ["hei", "ho", "hallal"], constraints: BL.NORTH)
         }
     }
 }
 
 }
