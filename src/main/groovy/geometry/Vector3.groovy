package geometry

import groovy.transform.Immutable

class Vector3 {
    public float x
    public float y
    public float z

    Vector3() {
        x = 0
        y = 0
        z = 0
    }

    Vector3(Vector3 o) {
        this.x = o.x
        this.y = o.y
        this.z = o.z
    }

    Vector3(float x, float y, float z) {
        if (x == Float.NaN) stop()
        this.x = x
        this.y = y
        this.z = z
    }

    void stop() {
        println "topping"
    }

    float getX() {
        return x
    }

    float getY() {
        return y
    }

    float getZ() {
        return z
    }

    Vector3 set(float x, float y, float z) {

        if (x == Float.NaN) stop()
        this.x = x
        this.y = y
        this.z = z
        return this
    }

    Vector3 set(Vector3 o) {

        if (o.x == Float.NaN) stop()
        this.x = o.x
        this.y = o.y
        this.z = o.z
        return this
    }

    Vector3 cross(Vector3 v) {
        return this.set((float)y * v.z - z * v.y,(float) z * v.x - x * v.z,(float) x * v.y - y * v.x);
    }

    Vector3 plus(Vector3 v)  {
        return this.set((float)x + v.x,(float) y + v.y,(float) z + v.z)
    }

    Vector3 negative()  {
        return this.set(-x, -y, -z);
    }

    Vector3 multiply(double v)  {
        return this.set((float)x * v, (float)y * v, (float)z * v);
    }

    float multiply( Vector3  v)  {
        return x * v.x + y * v.y + z * v.z;
    }

    Vector3 minus( Vector3  v)  {
        return this.set((float)x - v.x,(float) y - v.y,(float) z - v.z);
    }
    //	Vector3 operator-(){
    //		return Vector3(-x, -y, -z);
    //	}
    Vector3 div(float s)  {
        return this.set((float)x / s,(float) y / s,(float) z / s);
    }
    float lenght()  {
        return Math.sqrt(x * x + y * y + z * z);
    }
    Vector3 normalize()  {
        float l = lenght()
        if (l > 0.0001)
            return this / l
        else
            return this
    }

    /**
     * mutiplies this vector vith a 3x3 matrix in a 1D array, assumes matrix is 9 long,
     * returns a new vector which is the result
     */
    Vector3 multMatrix(float [] m)  {
        return this.set(//
                (float)x * m[0] + y * m[1] + z * m[2],//
                (float)x * m[3] + y * m[4] + z * m[5],//
                (float) x * m[6] + y * m[7] + z * m[8]//
        );
    }

    Vector3 rotate( Vector3  axis, float angle)  {
        float s = Math.sin(angle);
        float c = Math.cos(angle);
        Vector3 u = axis.normalize();

        def m = [
            //
            u.x * u.x + (1 - u.x * u.x) * c, //
            u.x * u.y * (1 - c) - u.z * s, //
            u.x * u.z * (1 - c) + u.y * s, //
            u.x * u.y * (1 - c) + u.z * s, //
            u.y * u.y + (1 - u.y * u.y) * c, //
            u.y * u.z * (1 - c) - u.x * s, //
            u.x * u.z * (1 - c) - u.y * s, //
            u.y * u.z * (1 - c) + u.x * s, //
            u.z * u.z + (1 - u.z * u.z) * c //

        ]

        return multMatrix(m);

    }

    static Vector3 tmp = new Vector3()


    boolean isLeftOf(Vector3 o) {
        return tmp.set(this).cross(o).y > 0
    }
}
