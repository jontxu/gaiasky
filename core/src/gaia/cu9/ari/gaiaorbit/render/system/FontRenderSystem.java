package gaia.cu9.ari.gaiaorbit.render.system;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.Array;
import gaia.cu9.ari.gaiaorbit.render.ComponentType;
import gaia.cu9.ari.gaiaorbit.render.I3DTextRenderable;
import gaia.cu9.ari.gaiaorbit.render.IAnnotationsRenderable;
import gaia.cu9.ari.gaiaorbit.render.IRenderable;
import gaia.cu9.ari.gaiaorbit.scenegraph.SceneGraphNode.RenderGroup;
import gaia.cu9.ari.gaiaorbit.scenegraph.camera.ICamera;
import gaia.cu9.ari.gaiaorbit.util.GlobalConf;
import gaia.cu9.ari.gaiaorbit.util.comp.DistToCameraComparator;

import java.util.Comparator;

public class FontRenderSystem extends AbstractRenderSystem {

    private SpriteBatch batch;
    public BitmapFont fontDistanceField, font2d, fontTitles;
    private Comparator<IRenderable> comp;
    private float[] red;

    public FontRenderSystem(RenderGroup rg, float[] alphas, SpriteBatch batch, ShaderProgram program) {
        super(rg, alphas, new ShaderProgram[] { program });
        this.batch = batch;
        // Init comparator
        comp = new DistToCameraComparator<>();
        red = new float[] { 1f, 0f, 0f, 1f };
    }

    public FontRenderSystem(RenderGroup rg, float[] alphas, SpriteBatch batch, ShaderProgram program, BitmapFont fontDistanceField, BitmapFont font2d, BitmapFont fontTitles) {
        this(rg, alphas, batch, program);

        this.fontDistanceField = fontDistanceField;
        this.font2d = font2d;
        this.fontTitles = fontTitles;

    }

    @Override
    public void renderStud(Array<IRenderable> renderables, ICamera camera, double t) {
        renderables.sort(comp);
        batch.begin();

        int size = renderables.size;
        ShaderProgram program = programs[0];
        if (program == null) {
            for (int i = 0; i < size; i++) {
                IAnnotationsRenderable s = (IAnnotationsRenderable) renderables.get(i);
                // Render sprite
                s.render(batch, camera, getAlpha(s));
            }
        } else {
            float lalpha = alphas[ComponentType.Labels.ordinal()];
            fontDistanceField.getData().setScale(0.6f);
            for (int i = 0; i < size; i++) {
                I3DTextRenderable lr = (I3DTextRenderable) renderables.get(i);

                // Label color
                program.setUniform4fv("u_color", GlobalConf.program.isUINightMode() ? red : lr.textColour(), 0, 4);
                // Component alpha
                program.setUniformf("u_componentAlpha", getAlpha(lr) * (!lr.isLabel() ? 1 : lalpha));
                // Font opacity multiplier, take into account element opacity
                program.setUniformf("u_opacity", 0.75f * lr.getTextOpacity());

                lr.render(batch, program, this, rc, camera);
            }
        }
        batch.end();

    }

    @Override
    public void resize(int w, int h) {
        super.resize(w, h);
        updateBatchSize(w, h);
    }

    @Override
    public void updateBatchSize(int w, int h) {
        batch.setProjectionMatrix(batch.getProjectionMatrix().setToOrtho2D(0, 0, w, h));
    }

}
