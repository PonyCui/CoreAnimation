package com.yy.opensource.coreanimation;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.opengl.GLUtils;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by cuiminghui on 2016/10/31.
 */

public class CALayer extends CALayerTexture {

    /**
     * Describe a layer's position and size. x and y relate to Left-Top coordinate.
     */
    public CGRect frame = new CGRect(0,0,0,0);

    /**
     * Describe layer content's transforming. Base on 3D matrix.
     * Use it for content scale, skew, rotate, translate.
     */
    public CATransform3D transform = new CATransform3D();

    /**
     * Describe the transform anchor point.
     */
    public CGPoint anchorPoint = new CGPoint(0.5f, 0.5f);

    /**
     * If true, layer will not be rendered.
     */
    public boolean hidden = false;

    /**
     * A hint marking that the layer contents provided by -drawInContext:
     * is completely opaque. Defaults to NO. Note that this does not affect
     * the interpretation of the `contents' property directly.
     */
    public boolean opaque = false;

    /**
     * The opacity of the layer, as a value between zero and one. Defaults
     * to one. Specifying a value outside the [0,1] range will give undefined
     * results.
     */
    public float opacity = 1.0f;

    /**
     * The background color of the layer. Default value is nil. Colors
     * created from tiled patterns are supported.
     */
    public CGColor backgroundColor = null;

    /**
     * A string defining how the contents of the layer is mapped into its
     * bounds rect. Options are `resize', `resizeAspect', `resizeAspectFill'.
     */
    public String contentsGravity = "resize";

    /**
     * When true an implicit mask matching the layer bounds is applied to
     * the layer (including the effects of the `cornerRadius' property). If
     * both `mask' and `masksToBounds' are non-nil the two masks are
     * multiplied to get the actual mask values. Defaults to NO.
     */
    public Boolean masksToBounds = false;

    public void setContents(Bitmap bitmap) {
        this.contents = bitmap;
        this.contentSize.width = (float)bitmap.getWidth();
        this.contentSize.height = (float)bitmap.getHeight();
        textureLoaded = false;
    }

    public CALayer[] getSubLayers() {
        return subLayers;
    }

    public void addSublayer(CALayer layer) {
        CALayer[] oldValues = subLayers;
        subLayers = new CALayer[oldValues.length + 1];
        for (int i = 0; i < oldValues.length; i++) {
            subLayers[i] = oldValues[i];
        }
        subLayers[oldValues.length] = layer;
        layer.superLayer = this;
    }

    public void removeFromSuperLayer() {
        if (superLayer != null) {
            CALayer[] newElements = new CALayer[superLayer.subLayers.length - 1];
            int j = 0;
            boolean found = false;
            for (int i = 0; i < superLayer.subLayers.length; i++) {
                CALayer element = superLayer.subLayers[i];
                if (element != this) {
                    newElements[j] = element;
                    j++;
                }
                else {
                    found = true;
                }
            }
            if (found) {
                superLayer.subLayers = newElements;
            }
        }
    }

    /**
     * Private props and methods!!!
     */

    /**
     * Describe root window bounds.
     */
    protected CGRect windowBounds = new CGRect(0, 0, 0, 0);

    /**
     * Provide a bitmap, so layer will render this bitmap as content.
     */
    private Bitmap contents = null;

    /**
     * Describe the size of content.
     */
    private CGSize contentSize = new CGSize(0, 0);

    /**
     * Gets superLayer here.
     */
    private CALayer superLayer = null;

    /**
     * Gets subLayers here.
     */
    private CALayer[] subLayers = new CALayer[0];

    @Override
    protected void loadTexture(GL10 gl) {
        if (textureLoaded) {
            return;
        }
        if (null != contents) {
            gl.glGenTextures(1, textureIdentifier, 0);
            gl.glBindTexture(GL10.GL_TEXTURE_2D, textureIdentifier[0]);
            gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);
            gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
            GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, contents, 0);
            textureLoaded = true;
        }
    }

    @Override
    protected void draw(GL10 gl) {
        super.draw(gl);
        gl.glFrontFace(GL10.GL_CW);
        resetVertices();
        setTransform(combineTransform(), anchorPoint, frame, windowBounds);
        setVertexBufferNeedsUpdate();
        drawMask(gl);
        drawBackgroundColor(gl);
        if (textureLoaded) {
            if (hidden || opacity <= 0.0) {
                return;
            }
            resetVertices();
            setContentTransform(combineTransform(), anchorPoint, frame, windowBounds);
            setVertexBufferNeedsUpdate();
            gl.glBindTexture(GL10.GL_TEXTURE_2D, textureIdentifier[0]);
            enableTextureFeatures(gl);
            gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer);
            gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, textureBuffer);
            gl.glColor4f(1.0f, 1.0f, 1.0f, combineOpacity());
            gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, vertices.length / 3);
            gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
            disableTextureFeatures(gl);
        }
        for (int i = 0; i < subLayers.length; i++) {
            CALayer layer = subLayers[i];
            layer.windowBounds = windowBounds;
            layer.draw(gl);
        }
        gl.glDisable(GL10.GL_STENCIL_TEST);
    }

    protected void setContentTransform(CATransform3D transform, CGPoint anchorPoint, CGRect frame, CGRect windowBounds) {
        if (contentsGravity.equals("resizeAspect")) {
            CGRect bounds;
            float moveX = 0, moveY = 0;
            if (frame.width / frame.height >= contentSize.width / contentSize.height) {
                // vertical
                bounds = new CGRect(
                        0,
                        0,
                        contentSize.width / contentSize.height * frame.height,
                        frame.height);
                moveX = (float)((frame.width - contentSize.width / contentSize.height * frame.height) / 2.0);
            }
            else {
                // horizon
                bounds = new CGRect(
                        0,
                        0,
                        frame.width,
                        contentSize.height / contentSize.width * frame.width);
                moveY = (float)((frame.height - contentSize.height / contentSize.width * frame.width) / 2.0);
            }
            bounds.x = -frame.width * anchorPoint.x;
            bounds.y = -frame.height * anchorPoint.y;
            float ltx = transform.a * bounds.x + transform.c * bounds.y + transform.tx - bounds.x + moveX;
            float lty = transform.b * bounds.x + transform.d * bounds.y + transform.ty - bounds.y + moveY;
            float rtx = transform.a * (bounds.x + bounds.width) + transform.c * bounds.y + transform.tx - bounds.x + moveX;
            float rty = transform.b * (bounds.x + bounds.width) + transform.d * bounds.y + transform.ty - bounds.y + moveY;
            float lbx = transform.a * bounds.x + transform.c * (bounds.y + bounds.height) + transform.tx - bounds.x + moveX;
            float lby = transform.b * bounds.x + transform.d * (bounds.y + bounds.height) + transform.ty - bounds.y + moveY;
            float rbx = transform.a * (bounds.x + bounds.width) + transform.c * (bounds.y + bounds.height) + transform.tx - bounds.x + moveX;
            float rby = transform.b * (bounds.x + bounds.width) + transform.d * (bounds.y + bounds.height) + transform.ty - bounds.y + moveY;
            vertices[3] = -1.0f + (ltx / windowBounds.width * 2);
            vertices[4] = 1.0f - (lty / windowBounds.height * 2);
            vertices[9] = -1.0f + (rtx / windowBounds.width * 2);
            vertices[10] = 1.0f - (rty / windowBounds.height * 2);
            vertices[0] = -1.0f + (lbx / windowBounds.width * 2);
            vertices[1] = 1.0f - (lby / windowBounds.height * 2);
            vertices[6] = -1.0f + (rbx / windowBounds.width * 2);
            vertices[7] = 1.0f - (rby / windowBounds.height * 2);
        }
        else if (contentsGravity.equals("resizeAspectFill")) {
            CGRect bounds;
            float moveX = 0, moveY = 0;
            if (frame.width / frame.height >= contentSize.width / contentSize.height) {
                // horizon
                bounds = new CGRect(
                        0,
                        0,
                        frame.width,
                        frame.width / (contentSize.width / contentSize.height));
                moveY = (float)((frame.height - bounds.height) / 2.0);
            }
            else {
                // vertical
                bounds = new CGRect(
                        0,
                        0,
                        frame.height * (contentSize.width / contentSize.height),
                        frame.height);
                moveX = (float)((frame.width - bounds.width) / 2.0);
            }
            bounds.x = -frame.width * anchorPoint.x;
            bounds.y = -frame.height * anchorPoint.y;
            float ltx = transform.a * bounds.x + transform.c * bounds.y + transform.tx - bounds.x + moveX;
            float lty = transform.b * bounds.x + transform.d * bounds.y + transform.ty - bounds.y + moveY;
            float rtx = transform.a * (bounds.x + bounds.width) + transform.c * bounds.y + transform.tx - bounds.x + moveX;
            float rty = transform.b * (bounds.x + bounds.width) + transform.d * bounds.y + transform.ty - bounds.y + moveY;
            float lbx = transform.a * bounds.x + transform.c * (bounds.y + bounds.height) + transform.tx - bounds.x + moveX;
            float lby = transform.b * bounds.x + transform.d * (bounds.y + bounds.height) + transform.ty - bounds.y + moveY;
            float rbx = transform.a * (bounds.x + bounds.width) + transform.c * (bounds.y + bounds.height) + transform.tx - bounds.x + moveX;
            float rby = transform.b * (bounds.x + bounds.width) + transform.d * (bounds.y + bounds.height) + transform.ty - bounds.y + moveY;
            vertices[3] = -1.0f + (ltx / windowBounds.width * 2);
            vertices[4] = 1.0f - (lty / windowBounds.height * 2);
            vertices[9] = -1.0f + (rtx / windowBounds.width * 2);
            vertices[10] = 1.0f - (rty / windowBounds.height * 2);
            vertices[0] = -1.0f + (lbx / windowBounds.width * 2);
            vertices[1] = 1.0f - (lby / windowBounds.height * 2);
            vertices[6] = -1.0f + (rbx / windowBounds.width * 2);
            vertices[7] = 1.0f - (rby / windowBounds.height * 2);
        }
        else {
            setTransform(transform, anchorPoint, frame, windowBounds);
        }
    }

    // Backgrounds

    private void drawBackgroundColor(GL10 gl) {
        if (hidden || combineOpacity() <= 0.0 || backgroundColor == null || backgroundColor.isClearColor()) {
            return;
        }
        enableBackgroundFeatures(gl);
        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer);
        if (combineOpacity() < 1.0) {
            gl.glColorPointer(4, GL10.GL_FLOAT, 0, backgroundColor.colorWithAlpha(combineOpacity()).colorBuffer);
        }
        else {
            gl.glColorPointer(4, GL10.GL_FLOAT, 0, backgroundColor.colorBuffer);
        }
        gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, vertices.length / 3);
        disableBackgroundFeatures(gl);
    }

    private void enableBackgroundFeatures(GL10 gl) {
        gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        if (!opaque) {
            gl.glEnable(GL10.GL_BLEND);
            gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
            gl.glEnable(GL10.GL_DEPTH_TEST);
            gl.glEnable(GL10.GL_ALPHA_TEST);
            gl.glAlphaFunc(GL10.GL_GREATER, 0.0f);
        }
    }

    private void disableBackgroundFeatures(GL10 gl) {
        gl.glDisableClientState(GL10.GL_COLOR_ARRAY);
        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
        if (!opaque) {
            gl.glDisable(GL10.GL_BLEND);
            gl.glDisable(GL10.GL_DEPTH_TEST);
            gl.glDisable(GL10.GL_ALPHA_TEST);
        }
    }

    // Textures

    private void enableTextureFeatures(GL10 gl) {
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
        gl.glEnable(GL10.GL_TEXTURE_2D);
        if (!opaque) {
            gl.glEnable(GL10.GL_BLEND);
            gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
            gl.glEnable(GL10.GL_DEPTH_TEST);
            gl.glEnable(GL10.GL_ALPHA_TEST);
            gl.glAlphaFunc(GL10.GL_GREATER, 0.0f);
        }
    }

    private void disableTextureFeatures(GL10 gl) {
        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
        gl.glDisable(GL10.GL_TEXTURE_2D);
        if (!opaque) {
            gl.glDisable(GL10.GL_BLEND);
            gl.glDisable(GL10.GL_DEPTH_TEST);
            gl.glDisable(GL10.GL_ALPHA_TEST);
        }
    }

    // Masks

    private void drawMask(GL10 gl) {
        if (hidden || combineOpacity() <= 0.0 || !masksToBounds) {
            return;
        }
        enableMaskFeatures(gl);
        if (!drawSuperMask(gl)) {
            gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer);
            gl.glColorPointer(4, GL10.GL_FLOAT, 0, CGColor.whiteColor.colorBuffer);
            gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, vertices.length / 3);
        }
        disableMaskFeatures(gl);
    }

    private boolean drawSuperMask(GL10 gl) {
        CALayer currentLayer = superLayer;
        while (currentLayer != null) {
            if (currentLayer.superLayer != null && !currentLayer.superLayer.masksToBounds) {
                break;
            }
            currentLayer = currentLayer.superLayer;
        }
        if (currentLayer != null && currentLayer.masksToBounds) {
            currentLayer.resetVertices();
            currentLayer.setTransform(superLayer.combineTransform(), currentLayer.anchorPoint, currentLayer.frame, currentLayer.windowBounds);
            currentLayer.setVertexBufferNeedsUpdate();
            gl.glVertexPointer(3, GL10.GL_FLOAT, 0, currentLayer.vertexBuffer);
            gl.glColorPointer(4, GL10.GL_FLOAT, 0, CGColor.whiteColor.colorBuffer);
            gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, currentLayer.vertices.length / 3);
        }
        return currentLayer != null && currentLayer.masksToBounds;
    }

    private void enableMaskFeatures(GL10 gl) {
        gl.glEnable(GL10.GL_STENCIL_TEST);
        gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glClearStencil(0);
        gl.glColorMask(false, false, false, false);
        gl.glDepthMask(false);
        gl.glStencilFunc(GL10.GL_ALWAYS, 1, 1);
        gl.glStencilOp(GL10.GL_REPLACE, GL10.GL_REPLACE, GL10.GL_REPLACE);
    }

    private void disableMaskFeatures(GL10 gl) {
        gl.glDisableClientState(GL10.GL_COLOR_ARRAY);
        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glColorMask(true, true, true, true);
        gl.glDepthMask(true);
        gl.glStencilFunc(GL10.GL_EQUAL, 1, 1);
        gl.glStencilOp(GL10.GL_KEEP, GL10.GL_KEEP, GL10.GL_KEEP);
    }


    // Helpers

    private float combineOpacity() {
        float opacity = this.opacity;
        CALayer currentLayer = superLayer;
        while (null != currentLayer) {
            opacity *= currentLayer.opacity;
            currentLayer = currentLayer.superLayer;
        }
        return opacity;
    }

    private CATransform3D combineTransform() {
        Matrix matrix = transform.requestMatrix();
        matrix.postTranslate(frame.x, frame.y);
        CALayer currentLayer = superLayer;
        while (null != currentLayer) {
            Matrix currentMatrix = currentLayer.transform.requestMatrix();
            matrix.postConcat(currentMatrix);
            matrix.postTranslate(currentLayer.frame.x, currentLayer.frame.y);
            currentLayer = currentLayer.superLayer;
        }
        CATransform3D finalTransform = new CATransform3D();
        finalTransform.setMatrix(matrix);
        return finalTransform;
    }

}
