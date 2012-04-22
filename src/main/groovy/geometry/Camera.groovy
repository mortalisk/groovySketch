package geometry

class Camera {
    Camera() {

    }
    Camera(Vector3 pos, Vector3 forward, Vector3 up) {

    }
    enum TrackMode {FIRST_PERSON, SPHERE_TRACK, BOX_TRACK}
    TrackMode trackMode;
    Vector3 trackCenter;
    Vector3 trackUp;
    float trackDistance;
    float trackAngleXZ;
    float trackAngleXY;
    Vector3 position;
    Vector3 forward;
    Vector3 up;
    float fov;
    double PI2 = Math.PI *2

    void updateCamera() {

    }

    void setTrackMode(TrackMode mode, Vector3 lookAt, Vector3 position) {
        trackMode = mode;
        trackCenter = lookAt;
        this.position = position;
        this.forward = lookAt - position;
        this.up = new Vector3(0,1,0);
        Vector3 a = lookAt - position;
        trackAngleXZ = Math.atan(a.z/a.x);
        if (a.z < 0.0) trackAngleXZ += Math.PI;
        trackAngleXY = Math.atan(a.y/a.x);
        if (a.y < 0.0) trackAngleXZ += Math.PI;
        trackDistance = a.lenght();
        fix();
    }

    Vector3 looksAt() {
        return (position + forward * 100);
    }

    void goForward(double length) {
        if (trackMode == SPHERE_TRACK) {
            trackDistance += length;
        } else {
            position = (position + (forward * length));
        }
        fix();
    }

    void goUp(double length) {
        if (trackMode == TrackMode.SPHERE_TRACK) {
            trackAngleXY += length;
        }else {
            position = position + (up * length);
        }
        fix();
    }

    void goRight(double length) {
        if (trackMode == TrackMode.SPHERE_TRACK) {
            trackAngleXZ += length;
        } else {
            Vector3 right = forward.cross(up);
            position = position + (right * length);
        }
        fix();

    }

    void turn(double angle) {
        if (trackMode == TrackMode.SPHERE_TRACK) {
            trackAngleXZ += angle;
        } else {
            forward = forward.rotate(Vector3(0,1,0), angle).normalize();
            up = up.rotate(Vector3(0,1,0), angle).normalize();
        }
        fix();

    }

    void pitch(double angle) {
        if (trackMode == TrackMode.SPHERE_TRACK) {
            trackAngleXY += angle;
        } else {
            Vector3 right = forward.cross(up);
            forward = forward.rotate(right, angle).normalize();
            up = up.rotate(right, angle);
        }
        fix();

    }

    void yaw(double angle) {
        if (trackMode == TrackMode.SPHERE_TRACK) {

        } else {
            up = up.rotate(forward, angle);
        }
        fix();

    }

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
            Vector3 posAtSphere = new Vector3(Math.sin(trackAngleXZ)*Math.abs(Math.sin(trackAngleXY)), Math.cos(trackAngleXY), Math.cos(trackAngleXZ)*Math.abs(Math.sin(trackAngleXY)))
            position = trackCenter + posAtSphere*trackDistance;
            forward = (trackCenter-position).normalize();
            up = new Vector3(0,1 , 0);
        }

        double cos = (forward * up) / forward.lenght() * up.lenght();
        up = (up + (forward * -cos)).normalize();

        //double aftercos = forward * up / forward.lenght() * up.lenght();
        //			std::cout << "Camera changed forward:" << forward << " up:" << up
        //					<< std::endl << " cos:" << cos << " after:" << aftercos
        //					<< std::endl;
        //assert(dataInvariant());



    }

}
