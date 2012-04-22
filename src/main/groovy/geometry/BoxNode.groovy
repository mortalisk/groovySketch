package geometry

import static org.lwjgl.opengl.GL11.*
class BoxNode extends BaseNode {
    BoxNode() {
       super("boxnode")
    }

    BoxNode(BoxNode other) {
        super(other);
        activeSurface = NULL;
        width = 10;
        depth = 10;
        heigth = 10;
        topF = heigth/2;
        bottomF = -topF;
        rightF = width/2;
        leftF = -rightF;
        farF = -depth/2;
        nearF = -farF;

        /*           H ___________ G
                   /|          /|
                  / |         / |
                 /  |        /  |
                /  E|______ /___|F
               /___/______ /   /
             D|   /       |C  /
              |  /        |  /(depth)
      (height)| /         | /
              |/__________|/
             A    (width)  B
         (0,0,0)

        */
        Vector3 A = new Vector3 (leftF,bottomF,nearF);
        Vector3 B  = new Vector3 (rightF,bottomF,nearF);
        Vector3 C = new Vector3  (rightF,topF,nearF);
        Vector3 D  = new Vector3 (leftF,topF,nearF);

        Vector3 E  = new Vector3 (leftF,bottomF,farF);
        Vector3 F  = new Vector3 (rightF,bottomF,farF);
        Vector3 G  = new Vector3 (rightF,topF,farF);
        Vector3 H = new Vector3  (leftF,topF,farF);

        frontNode = new SideNode(A, B, C, D);
        backNode = new SideNode(F, E, H, G );
        topNode = new SideNode(D, C, G, H);
        bottomNode = new SideNode(E, F, B, A);
        leftNode = new SideNode(E, A, D, H);
        rightNode = new SideNode(B, F, G, C);

        setUpSurfaces();
    }

    void setUpSurfaces() {
        frontNode.setOpposite(backNode);
        leftNode.setOpposite(rightNode);
        topNode.setOpposite(bottomNode);

        frontNode.setLeft(leftNode);
        leftNode.setLeft(backNode);
        backNode.setLeft(rightNode);
        rightNode.setLeft(frontNode);

        surfaces.add(frontNode);
        surfaces.add(backNode);
        surfaces.add(leftNode);
        surfaces.add(rightNode);
        surfaces.add(topNode);
        surfaces.add(bottomNode);
    }

    float width, depth, heigth

    SideNode frontNode,  backNode,  leftNode,  rightNode,  topNode,  bottomNode

    float topF, bottomF ,rightF ,leftF ,farF , nearF
    List<SideNode> surfaces = []
    SideNode activeSurface;
    void addPoint(Vector3 from, Vector3 direction) {

    }
    void determineActionOnStoppedDrawing() {

    }
    void draw() {
        glPushMatrix();
        glDisable(GL_CULL_FACE);


        drawChildren();

        glColor4f(0.5,0.5,0.5,1.0);
        glEnable(GL_CULL_FACE);
        glCullFace(GL_FRONT);
        drawSelf();
        glDisable(GL_CULL_FACE);

        glPopMatrix();
    }
    void drawSelf() {
        //Node::drawSelf();
        glTranslated(position.x, position.y, position.z);

        glDisable(GL_LIGHTING);
        glDisable(GL_LIGHT0);
        surfaces.each {
            it.drawSplines();
            it.shape.drawLines(false);
        }
        surfaces.each {
            glColor4f(0.5,0.5,0.5,0.5);
            it.shape.drawShape(ambient, diffuse);
        }

        glEnable(GL_LIGHTING);
        glEnable(GL_LIGHT0);
    }
    BaseNode makeLayer() {

    }
    void makeSuggestionFor(SideNode side) {

    }

    List<Vector3> intersectionPoints(Vector3 from, Vector3 direction) {

    }
    float intersectionPoint(Vector3 from, Vector3 direction) {

    }

    BoxNode copy() {
        return new BoxNode(*this);
    }
}
