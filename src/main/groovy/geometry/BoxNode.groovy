package geometry

import static org.lwjgl.opengl.GL11.*
class BoxNode extends BaseNode {

    float width, depth, heigth

    SideNode frontNode,  backNode,  leftNode,  rightNode,  topNode,  bottomNode

    BoxNode() {
       super("boxnode")
        activeSurface = null;
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

    BoxNode(BoxNode other) {
        super(other);

        frontNode =other.frontNode.copy()
        backNode = other.backNode.copy()
        leftNode = other.leftNode.copy()
        rightNode =other.rightNode.copy()
        topNode =other.topNode.copy()
        bottomNode =other.bottomNode.copy()

        setUpSurfaces();

        activeSurface = null;
    }

    void setUpSurfaces() {
        frontNode.assignOpposites(backNode);
        leftNode.assignOpposites(rightNode);
        topNode.assignOpposites(bottomNode);

        frontNode.assignLeft(leftNode);
        leftNode.assignLeft(backNode);
        backNode.assignLeft(rightNode);
        rightNode.assignLeft(frontNode);

        surfaces.add(frontNode);
        surfaces.add(backNode);
        surfaces.add(leftNode);
        surfaces.add(rightNode);
        surfaces.add(topNode);
        surfaces.add(bottomNode);

        diffuse.w = 0.1
    }


    float topF, bottomF ,rightF ,leftF ,farF , nearF
    List<SideNode> surfaces = []
    SideNode activeSurface;
    Vector3 tmp = new Vector3();
    void addPoint(Vector3 from, Vector3 direction) {
        // we must find the nearest intersection point
        float candidateDistance = Float.MAX_VALUE;
        Vector3 candidatePoint;
        SideNode candidate = null;

        surfaces.each { s ->
            List<Vector3> points = s.intersectionPoints(from, direction);
            if (points.size() >0 ) {
                float dist = (tmp.set(points[0])-from).lenght();
                if ((activeSurface == null || s == activeSurface) && dist < candidateDistance) {
                    candidateDistance = dist;
                    candidatePoint = points[0];
                    candidate=s;
                }

            }
        }

        if (candidate) {
            candidate.sketchingSpline.addPoint(candidatePoint);


            activeSurface = candidate;
        }
    }
    void determineActionOnStoppedDrawing() {
        if (activeSurface.is(topNode)||activeSurface.is(bottomNode)){
            activeSurface.spline.clear();
            activeSurface.sketchingSpline.clear();
            activeSurface = null;
            return;
        }
        if (activeSurface) {
            activeSurface.correctSketchingDirection();

            if (activeSurface.spline.getPoints().size() == 0) {
                activeSurface.moveSketchingPointsToSpline();
            } else {
                activeSurface.doOversketch();
            }

            activeSurface.makeSuggestionLines();

            makeSuggestionFor(activeSurface);

            activeSurface = null;
        }
    }
    void draw() {

        glPushMatrix();
        glDisable(GL_CULL_FACE);


        drawChildren();

        //glColor4f(0.5,0.5,0.5,1.0);
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
            //glColor4f(0.5,0.5,0.5,0.5);
            it.shape.drawShape(ambient, diffuse);
        }


        glEnable(GL_LIGHTING);
        glEnable(GL_LIGHT0);

    }
    BaseNode makeLayer() {
        if (frontNode.spline.getPoints().size() < 1||rightNode.spline.getPoints().size() <1
                ||backNode.spline.getPoints().size() <1||leftNode.spline.getPoints().size() <1)
        return this;

        SurfaceNode below = null;
        if(children.size() > 0) {
            below = (SurfaceNode)children[children.size()-1];
        }else {
            Spline front = new Spline();
            Spline right = new Spline();
            Spline back = new Spline();
            Spline left = new Spline();
            front.addPoint(frontNode.lowerLeft);
            front.addPoint(frontNode.lowerRigth);
            right.addPoint(rightNode.lowerLeft);
            right.addPoint(rightNode.lowerRigth);
            back.addPoint(backNode.lowerLeft);
            back.addPoint(backNode.lowerRigth);
            left.addPoint(leftNode.lowerLeft);
            left.addPoint(leftNode.lowerRigth);
            below = new SurfaceNode("Bottom", front, right, back, left, null);
        }
        SurfaceNode n = new SurfaceNode( "Layer", frontNode.spline, rightNode.spline, backNode.spline, leftNode.spline, below);
        children.add(n);

        surfaces.each { s ->
            s.spline.clear();
            s.spline.isSuggestion = true;
        }

        return n;
    }
    void makeSuggestionFor(SideNode side) {
        if (side.opposite.spline.isSuggestion && side.left.spline.isSuggestion && side.right.spline.isSuggestion) {
            side.opposite.spline.clear();

            // project points from this side to opposite
            Vector3 direction = tmp.set(side.opposite.lowerRigth) - side.lowerLeft;
            side.opposite.projectPoints(direction, side.spline.getPoints());

            side.opposite.spline.isSuggestion = true;
        }

        Vector3 left = side.spline.getLeftPoint();
        Vector3 right = side.spline.getRightPoint();
        Vector3 leftOpposite = side.opposite.spline.getLeftPoint();
        Vector3 rightOpposite = side.opposite.spline.getRightPoint();

        side.left.addInterpolatedSuggestion(rightOpposite.y, left.y);
        side.right.addInterpolatedSuggestion(right.y, leftOpposite.y);

        frontNode.ensureLeftToRigth();
        rightNode.ensureLeftToRigth();
        backNode.ensureLeftToRigth();
        leftNode.ensureLeftToRigth();

        side.spline.isSuggestion = false;
    }

    List<Vector3> intersectionPoints(Vector3 from, Vector3 direction) {
        List<Vector3> p = new ArrayList<Vector3>();
        float d = intersectionPoint(from, direction);
        if (d < Float.MAX_VALUE) {
            Vector3 result = new Vector3(direction).normalize()*d + from
            p.add(result);
        }
        return p;
    }
    float intersectionPoint(Vector3 from, Vector3 direction) {
        float dist = Float.MAX_VALUE
        surfaces.each { s ->
            List<Vector3> points = s.intersectionPoints(from, direction);
            if (points.size() >0) {
                Vector3 p = points[0]
                float d = (p-from).lenght()
                if (d < dist) dist = d
            }
        }
        return dist;
    }

    BoxNode copy() {
        return new BoxNode(this)
    }
}
