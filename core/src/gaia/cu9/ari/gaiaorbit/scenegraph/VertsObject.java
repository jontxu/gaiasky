package gaia.cu9.ari.gaiaorbit.scenegraph;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.math.Matrix4;
import gaia.cu9.ari.gaiaorbit.data.util.PointCloudData;
import gaia.cu9.ari.gaiaorbit.render.IGPUVertsRenderable;
import gaia.cu9.ari.gaiaorbit.scenegraph.camera.ICamera;
import gaia.cu9.ari.gaiaorbit.util.math.Vector3d;
import gaia.cu9.ari.gaiaorbit.util.time.ITimeFrameProvider;

/**
 * Represents a group of vertices which are sent to the GPU in a VBO
 *
 * @author tsagrista
 */
public class VertsObject extends AbstractPositionEntity implements IGPUVertsRenderable {

    /** GPU rendering attributes **/
    protected boolean inGpu = false;
    /** Indicates the index of the mesh data in the renderer **/
    protected int offset = -1;
    protected int count;

    protected boolean blend = true, depth = true;

    /** The render group **/
    protected RenderGroup renderGroup;

    /** Whether to close the polyline (connect end point to start point) or not **/
    protected boolean closedLoop = true;

    // Line width
    protected float primitiveSize = 1f;

    protected PointCloudData pointCloudData;

    public VertsObject(RenderGroup rg) {
        super();
        this.renderGroup = rg;
        this.localTransform = new Matrix4();
    }

    @Override
    protected void addToRenderLists(ICamera camera) {
        if (pointCloudData != null && pointCloudData.getNumPoints() > 0)
            addToRender(this, renderGroup);
    }

    @Override
    public void updateLocalValues(ITimeFrameProvider time, ICamera camera) {
        translation.getMatrix(localTransform);
    }

    /**
     * Sets the 3D points of the line in the internal reference system.
     *
     * @param points Vector with the points. If length is not multiple of 3, some points are discarded.
     */
    public void setPoints(double[] points) {
        int n = points.length;
        if (n % 3 != 0) {
            n = n - n % 3;
        }
        if (pointCloudData == null)
            pointCloudData = new PointCloudData(n / 3);
        else
            pointCloudData.clear();

        pointCloudData.addPoints(points);
        markForUpdate();
    }

    /**
     * Adds the given points to this data
     *
     * @param points The points to add
     */
    public void addPoints(double[] points) {
        if (pointCloudData == null) {
            setPoints(points);
        } else {
            pointCloudData.addPoints(points);
            markForUpdate();
        }
    }

    /**
     * Adds the given point ot this data
     *
     * @param point The point to add
     */
    public void addPoint(Vector3d point) {
        if (pointCloudData == null) {
            setPoints(point.values());
        } else {
            pointCloudData.addPoint(point);
            markForUpdate();
        }

    }

    public boolean isEmpty() {
        return pointCloudData.isEmpty();
    }

    /**
     * Clears the data from this object, both in RAM and VRAM
     */
    public void clear() {
        setPoints(new double[] {});
    }

    @Override
    public boolean inGpu() {
        return inGpu;
    }

    @Override
    public int getOffset() {
        return offset;
    }

    @Override
    public int getCount() {
        return count;
    }

    @Override
    public PointCloudData getPointCloud() {
        return pointCloudData;
    }

    @Override
    public float[] getColor() {
        return cc;
    }

    @Override
    public double getAlpha() {
        return cc[3];
    }

    @Override
    public Matrix4 getLocalTransform() {
        return localTransform;
    }

    @Override
    public SceneGraphNode getParent() {
        return parent;
    }

    @Override
    public void setInGpu(boolean inGpu) {
        this.inGpu = inGpu;
    }

    @Override
    public void setOffset(int offset) {
        this.offset = offset;
    }

    @Override
    public void setCount(int count) {
        this.count = count;
    }

    @Override
    public void setPrimitiveSize(float lineWidth) {
        this.primitiveSize = lineWidth;
    }

    @Override
    public float getPrimitiveSize() {
        return primitiveSize;
    }

    @Override
    public boolean isClosedLoop() {
        return closedLoop;
    }

    @Override
    public void setClosedLoop(boolean closedLoop) {
        this.closedLoop = closedLoop;
    }

    public void setBlend(boolean blend) {
        this.blend = blend;
    }

    public void setDepth(boolean depth) {
        this.depth = depth;
    }

    @Override
    public void blend() {
        if (blend) {
            Gdx.gl20.glEnable(GL20.GL_BLEND);
            Gdx.gl20.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        } else {
            Gdx.gl20.glDisable(GL20.GL_BLEND);
        }
    }

    @Override
    public void depth() {
        Gdx.gl20.glDepthMask(depth);
        if (depth) {
            Gdx.gl20.glEnable(GL20.GL_DEPTH_TEST);
        } else {
            Gdx.gl20.glDisable(GL20.GL_DEPTH_TEST);
        }
    }

    @Override
    public void markForUpdate() {
        this.inGpu = false;
    }

    public boolean isLine(){
        return renderGroup == RenderGroup.LINE_GPU;
    }

    public boolean isPoint(){
        return renderGroup == RenderGroup.POINT_GPU;
    }
}

