package morten

import org.lwjgl.opengl.AWTGLCanvas
import groovy.swing.SwingBuilder
import java.awt.BorderLayout as BL
import awt.AWTGearsCanvas

class First extends AWTGLCanvas{

    First() {
        println 'hello'
    }

 static main(args) {
     def count = 0
     new SwingBuilder().edt {
         frame(title:'Frame', size:[300,300], show: true) {
             borderLayout()
             label('ehih',constraints: BL.WEST)
             widget(new AWTGearsCanvas(), constraints: BL.CENTER)
             textlabel = label(text:"Click the button!", constraints: BL.NORTH)
             button(text:'Click Me',
                     actionPerformed: {count++; textlabel.text = "Clicked ${count} time(s)."; println "clicked"},
                     constraints:BL.SOUTH)
         }
     }
 }
 
 }
