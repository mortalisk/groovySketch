package geometry

class Camera {

    Vector3 tmp = new Vector3();

    enum TrackMode {FIRST_PERSON, SPHERE_TRACK, BOX_TRACK}
    TrackMode trackMode;
    Vector3 trackCenter = new Vector3();
    Vector3 trackUp = new Vector3();
    float trackDistance;
    float trackAngleXZ;
    float trackAngleXY;
    Vector3 position = new Vector3();
    Vector3 forward = new Vector3();
    Vector3 up = new Vector3();
    float fov;
    double PI2 = Math.PI *2

    Camera() {
        position.set(10, 10, 10)
        forward.set(-1, -1, -1)
        up.set(-1, 1, -1)
        fov = Math.PI/2
        forward.normalize();
        up.normalize();
        fix();
        trackMode = TrackMode.FIRST_PERSON;
    }
    Camera(Vector3 pos, Vector3 forward, Vector3 up) {
        position.set(pos)
        this.forward.set(forward)
        this.up.set(up)
        fov = Math.PI/2
        fix()
    }

    void updateCamera() {

    }

    void setTrackMode(TrackMode mode, Vector3 lookAt, Vector3 position) {
        trackMode = mode;
        trackCenter.set(lookAt);
        this.position.set(position);
        this.forward.set(lookAt) - position;
        this.up.set(0,1,0);
        Vector3 a = tmp.set(lookAt) - position;
        trackAngleXZ = Math.atan(a.z/a.x);
        if (a.z < 0.0) trackAngleXZ += Math.PI;
        trackAngleXY = Math.atan(a.y/a.x);
        if (a.y < 0.0) trackAngleXZ += Math.PI;
        trackDistance = a.lenght();
        fix();
    }

    Vector3 looksAtTmp = new Vector3()
    Vector3 looksAt() {
        return (looksAtTmp.set(forward) * 100) + position;
    }

    void goForward(double length) {
        if (trackMode == TrackMode.SPHERE_TRACK) {
            trackDistance += length;
        } else {
            (position + (tmp.set(forward) * length));
        }
        fix();
    }


    void goUp(double length) {
        if (trackMode == TrackMode.SPHERE_TRACK) {
            trackAngleXY += length;
        }else {
            tmp.set(up) * length;
            position + tmp;
        }
        fix();
    }

    void goRight(double length) {
        if (trackMode == TrackMode.SPHERE_TRACK) {
            trackAngleXZ += length;
        } else {
            Vector3 right = tmp.set(forward).cross(up);
            position + (right * length);
        }
        fix();

    }

    void turn(double angle) {
        if (trackMode == TrackMode.SPHERE_TRACK) {
            trackAngleXZ += angle;
        } else {
            forward.rotate(tmp.set(0,1,0), angle).normalize();
            up.rotate(tmp.set(0,1,0), angle).normalize();
        }
        fix();

    }

    void pitch(double angle) {
        if (trackMode == TrackMode.SPHERE_TRACK) {
            trackAngleXY += angle;
        } else {
            Vector3 right = tmp.set(forward).cross(up);
            forward.rotate(right, angle).normalize();
            up.rotate(right, angle);
        }
        fix();

    }

    void yaw(double angle) {
        if (trackMode == TrackMode.SPHERE_TRACK) {

        } else {
            up.rotate(forward, angle);
        }
        fix();

    }


    Vector3 posAtSphere = new Vector3();
    Vector3 tmpFix = new Vector3()
    void fix() {

        if (trackAngleXZ > PI2) {
            trackAngleXZ -= PI2;
        }
        if (trackAngleXZ < PI2) {
            trackAngleXZ += PI2;
        }
        if (trackAngleXY > -0.1) {
            trackAngleXY = -0.1;
        }
        if (trackAngleXY < -Math.PI) {
            trackAngleXY = -Math.PI;
        }

        if (trackMode == TrackMode.SPHERE_TRACK) {
            posAtSphere.set((float)Math.sin(trackAngleXZ)*Math.abs(Math.sin(trackAngleXY)), (float)Math.cos(trackAngleXY), (float)Math.cos(trackAngleXZ)*Math.abs(Math.sin(trackAngleXY)))
            (position.set(posAtSphere) * trackDistance) + trackCenter;
            (forward.set(trackCenter)-position).normalize()
            up.set(0,1 , 0);
        }

        double cos = (forward * up) / forward.lenght() * up.lenght();
        (tmpFix.set(forward) * -cos)
        up + tmpFix;
        up.normalize()

        //double aftercos = forward * up / forward.lenght() * up.lenght();
        //			std::cout << "Camera changed forward:" << forward << " up:" << up
        //					<< std::endl << " cos:" << cos << " after:" << aftercos
        //					<< std::endl;
        //assert(dataInvariant());



    }

}
