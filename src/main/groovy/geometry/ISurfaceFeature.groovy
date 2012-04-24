package geometry
interface ISurfaceFeature
{
    void doTransformSurface(List<List<Vector3>> rows, float resolution, float size);
    void repositionOnSurface(SurfaceNode surfacenode);
}
