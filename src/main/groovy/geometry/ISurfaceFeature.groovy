package geometry
interface ISurfaceFeature
{
    void doTransformSurface(List<List<Vector3>> rows);
    void repositionOnSurface(SurfaceNode surfacenode);
}
