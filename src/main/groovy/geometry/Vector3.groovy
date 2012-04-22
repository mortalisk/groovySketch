package geometry

import groovy.transform.Immutable

@Immutable
class Vector3 {
    double x = 0
    double y = 0
    double z = 0

    Vector3 cross(Vector3 v) {
        return new Vector3(y * v.z - z * v.y, z * v.x - x * v.z, x * v.y - y * v.x);
    }

    Vector3 plus( Vector3  v)  {
        return new Vector3(x + v.x, y + v.y, z + v.z)
    }

    Vector3 negative()  {
        return new Vector3(-x, -y, -z);
    }

    Vector3 multiply(double v)  {
        return new Vector3(x * v, y * v, z * v);
    }

    double multiply( Vector3  v)  {
        return x * v.x + y * v.y + z * v.z;
    }

    Vector3 minus( Vector3  v)  {
        return new Vector3(x - v.x, y - v.y, z - v.z);
    }
    //	Vector3 operator-(){
    //		return Vector3(-x, -y, -z);
    //	}
    Vector3 div(double s)  {
        return new Vector3(x / s, y / s, z / s);
    }
    double lenght()  {
        return Math.sqrt(x * x + y * y + z * z);
    }
    Vector3 normalize()  {
        double l = lenght()
        if (l > 0.0001)
            return this / l
        else
            return this
    }

    /**
     * mutiplies this vector vith a 3x3 matrix in a 1D array, assumes matrix is 9 long,
     * returns a new vector which is the result
     */
    Vector3 multMatrix(double [] m)  {
        return new Vector3(//
                x * m[0] + y * m[1] + z * m[2],//
                x * m[3] + y * m[4] + z * m[5],//
                x * m[6] + y * m[7] + z * m[8]//
        );
    }

    Vector3 rotate( Vector3  axis, double angle)  {
        double s = Math.sin(angle);
        double c = Math.cos(angle);
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

    boolean isLeftOf(Vector3 o) {
        return this.cross(o).y > 0
    }
}
