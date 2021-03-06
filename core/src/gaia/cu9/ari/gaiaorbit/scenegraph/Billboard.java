package gaia.cu9.ari.gaiaorbit.scenegraph;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import gaia.cu9.ari.gaiaorbit.GaiaSky;
import gaia.cu9.ari.gaiaorbit.scenegraph.camera.ICamera;
import gaia.cu9.ari.gaiaorbit.util.*;
import gaia.cu9.ari.gaiaorbit.util.coord.Coordinates;
import gaia.cu9.ari.gaiaorbit.util.math.MathUtilsd;
import gaia.cu9.ari.gaiaorbit.util.math.Vector3d;
import gaia.cu9.ari.gaiaorbit.util.time.ITimeFrameProvider;

public class Billboard extends ModelBody {

    protected static final double TH_ANGLE_NONE = 0.002;
    protected static final double TH_ANGLE_POINT = ModelBody.TH_ANGLE_POINT / 1e9;
    protected static final double TH_ANGLE_QUAD = ModelBody.TH_ANGLE_POINT / 8;

    protected boolean hidden = false;
    protected double[] fade;

    protected Quaternion q;

    public Billboard() {
        super();
        q = new Quaternion();
    }

    @Override
    public double THRESHOLD_NONE() {
        return TH_ANGLE_NONE;
    }

    @Override
    public double THRESHOLD_POINT() {
        return TH_ANGLE_POINT;
    }

    @Override
    public double THRESHOLD_QUAD() {
        return TH_ANGLE_QUAD;
    }

    @Override
    public void initialize() {
        super.initialize();
    }

    @Override
    public void doneLoading(AssetManager manager) {
        super.doneLoading(manager);
    }

    @Override
    public void updateLocalValues(ITimeFrameProvider time, ICamera camera) {
        forceUpdatePosition(time, false);
        if (fade != null) {
            fadeOpacity = (float) MathUtilsd.lint(distToCamera, fade[0], fade[1], 1, 0.3);
        } else {
            fadeOpacity = 1f;
        }
    }

    /**
     * Default implementation, only sets the result of the coordinates call to
     * pos
     *
     * @param time  Time to get the coordinates
     * @param force Whether to force the update
     */
    protected void forceUpdatePosition(ITimeFrameProvider time, boolean force) {
        if (time.getDt() != 0 || force) {
            coordinatesTimeOverflow = coordinates.getEquatorialCartesianCoordinates(time.getTime(), pos) == null;
            // Convert to cartesian coordinates and put them in aux3 vector
            Vector3d aux3 = aux3d1.get();
            Coordinates.cartesianToSpherical(pos, aux3);
            posSph.set((float) (Nature.TO_DEG * aux3.x), (float) (Nature.TO_DEG * aux3.y));
            DecalUtils.setBillboardRotation(q, new Vector3d(pos).nor(), new Vector3d(0, 1, 0));
        }
    }

    @Override
    protected void updateLocalTransform() {
        setToLocalTransform(localTransform, true);
    }

    /**
     * Sets the local transform of this satellite
     */
    public void setToLocalTransform(Matrix4 localTransform, boolean forceUpdate) {
        if (forceUpdate) {
            translation.getMatrix(localTransform).scl(size).rotate(q);
        } else {
            localTransform.set(this.localTransform);
        }

    }

    @Override
    protected void addToRenderLists(ICamera camera) {
        if (GaiaSky.instance.isOn(ct)) {
            if (viewAngleApparent >= TH_ANGLE_NONE) {
                addToRender(this, RenderGroup.MODEL_NORMAL);
                if (renderText()) {
                    addToRender(this, RenderGroup.FONT_LABEL);
                }
            }
        }
    }

    @Override
    protected float labelFactor() {
        return 1e1f;
    }

    @Override
    public boolean renderText() {
        return !hidden && super.renderText();
    }

    @Override
    public float getTextOpacity() {
        return Math.max(getOpacity(), fadeOpacity);
    }


    @Override
    public float labelSizeConcrete() {
        return size * .5e-2f;
    }

    @Override
    protected float labelMax() {
        return super.labelMax() * 2;
    }

    protected float getViewAnglePow() {
        return 1f;
    }

    protected float getThOverFactorScl() {
        return 5e3f;
    }

    public float getFuzzyRenderSize(ICamera camera) {
        float computedSize = this.size;
        computedSize *= GlobalConf.scene.STAR_BRIGHTNESS * .6e-3;

        return (float) computedSize;
    }

    public void setHidden(String hidden) {
        try {
            this.hidden = Boolean.parseBoolean(hidden);
        } catch (Exception e) {
            Logger.getLogger(this.getClass()).error(e);
        }
    }

    /**
     * Sets the size of this entity in parsecs
     *
     * @param sizePc The size in parsecs
     */
    public void setSizepc(Double sizePc) {
        this.size = (float) (sizePc * 2 * Constants.PC_TO_U);
    }

    public void setFade(double[] fadein) {
        fade = fadein;
        fade[0] *= Constants.PC_TO_U;
        fade[1] *= Constants.PC_TO_U;
    }
}
