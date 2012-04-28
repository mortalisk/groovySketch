package geometry

import org.lwjgl.BufferUtils

class Surface extends Shape
{
    /** takes a list of vertices that reprecent trinagles of surfaces, and one normal for each vertice*/
    Surface(List<Vector3> triangles, List<Vector3> normals, List<Vector3> outline,boolean strip = false) {

        this.triangles = BufferUtils.createFloatBuffer(triangles.size()*6);
        for (int i = 0; i<triangles.size(); ++i) {
            this.triangles.put(triangles[i].x)
            this.triangles.put(triangles[i].y)
            this.triangles.put(triangles[i].z)
            this.triangles.put(normals[i].x)
            this.triangles.put(normals[i].y)
            this.triangles.put(normals[i].z)
        }

        this.lineVertices = BufferUtils.createFloatBuffer(outline.size()*3);
        for (int i = 0; i<outline.size(); ++i) {
            this.lineVertices.put(outline[i].x)
            this.lineVertices.put(outline[i].y)
            this.lineVertices.put(outline[i].z)
        }

        this.strip = strip;
    }

    Surface(float[] triangles, List<Vector3> outline,boolean strip = false) {

        this.triangles = BufferUtils.createFloatBuffer(triangles.size()*3);
        this.triangles.put(triangles);

        this.lineVertices = BufferUtils.createFloatBuffer(outline.size()*3);
        for (int i = 0; i<outline.size(); ++i) {
            this.lineVertices.put(outline[i].x)
            this.lineVertices.put(outline[i].y)
            this.lineVertices.put(outline[i].z)
        }

        this.strip = strip;
    }
}