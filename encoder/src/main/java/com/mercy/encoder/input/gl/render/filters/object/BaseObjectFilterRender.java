package com.mercy.encoder.input.gl.render.filters.object;

import android.content.Context;
import android.graphics.PointF;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import androidx.annotation.RequiresApi;

import com.mercy.encoder.input.gl.Sprite;
import com.mercy.encoder.input.gl.TextureLoader;
import com.mercy.encoder.input.gl.render.filters.BaseFilterRender;
import com.mercy.encoder.utils.gl.GlUtil;
import com.mercy.encoder.utils.gl.StreamObjectBase;
import com.mercy.encoder.utils.gl.TranslateTo;
import com.mercy.encoder.R;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Created by pedro on 03/08/18.
 */

@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
abstract public class BaseObjectFilterRender extends BaseFilterRender {

  //rotation matrix
  private final float[] squareVertexDataFilter = {
      // X, Y, Z, U, V
      -1f, -1f, 0f, 0f, 0f, //bottom left
      1f, -1f, 0f, 1f, 0f, //bottom right
      -1f, 1f, 0f, 0f, 1f, //top left
      1f, 1f, 0f, 1f, 1f, //top right
  };

  private int program = -1;
  private int aPositionHandle = -1;
  private int aTextureHandle = -1;
  private int aTextureObjectHandle = -1;
  private int uMVPMatrixHandle = -1;
  private int uSTMatrixHandle = -1;
  private int uSamplerHandle = -1;
  private int uObjectHandle = -1;
  protected int uAlphaHandle = -1;

  private FloatBuffer squareVertexObject;

  protected int[] streamObjectTextureId = new int[] { -1 };
  protected TextureLoader textureLoader = new TextureLoader();
  protected StreamObjectBase streamObject;
  private Sprite sprite;
  protected float alpha = 1f;
  protected boolean shouldLoad = false;

  public BaseObjectFilterRender() {
    squareVertex = ByteBuffer.allocateDirect(squareVertexDataFilter.length * FLOAT_SIZE_BYTES)
        .order(ByteOrder.nativeOrder())
        .asFloatBuffer();
    squareVertex.put(squareVertexDataFilter).position(0);
    sprite = new Sprite();
    float[] vertices = sprite.getTransformedVertices();
    squareVertexObject = ByteBuffer.allocateDirect(vertices.length * FLOAT_SIZE_BYTES)
        .order(ByteOrder.nativeOrder())
        .asFloatBuffer();
    squareVertexObject.put(vertices).position(0);
    Matrix.setIdentityM(MVPMatrix, 0);
    Matrix.setIdentityM(STMatrix, 0);
  }

  @Override
  protected void initGlFilter(Context context) {
    String vertexShader = GlUtil.getStringFromRaw(context, R.raw.object_vertex);
    String fragmentShader = GlUtil.getStringFromRaw(context, R.raw.object_fragment);

    program = GlUtil.createProgram(vertexShader, fragmentShader);
    aPositionHandle = GLES20.glGetAttribLocation(program, "aPosition");
    aTextureHandle = GLES20.glGetAttribLocation(program, "aTextureCoord");
    aTextureObjectHandle = GLES20.glGetAttribLocation(program, "aTextureObjectCoord");
    uMVPMatrixHandle = GLES20.glGetUniformLocation(program, "uMVPMatrix");
    uSTMatrixHandle = GLES20.glGetUniformLocation(program, "uSTMatrix");
    uSamplerHandle = GLES20.glGetUniformLocation(program, "uSampler");
    uObjectHandle = GLES20.glGetUniformLocation(program, "uObject");
    uAlphaHandle = GLES20.glGetUniformLocation(program, "uAlpha");
  }

  @Override
  protected void drawFilter() {
    if (shouldLoad) {
      streamObjectTextureId = textureLoader.load();
      shouldLoad = false;
    }

    GLES20.glUseProgram(program);

    squareVertex.position(SQUARE_VERTEX_DATA_POS_OFFSET);
    GLES20.glVertexAttribPointer(aPositionHandle, 3, GLES20.GL_FLOAT, false,
        SQUARE_VERTEX_DATA_STRIDE_BYTES, squareVertex);
    GLES20.glEnableVertexAttribArray(aPositionHandle);

    squareVertex.position(SQUARE_VERTEX_DATA_UV_OFFSET);
    GLES20.glVertexAttribPointer(aTextureHandle, 2, GLES20.GL_FLOAT, false,
        SQUARE_VERTEX_DATA_STRIDE_BYTES, squareVertex);
    GLES20.glEnableVertexAttribArray(aTextureHandle);

    squareVertexObject.position(SQUARE_VERTEX_DATA_POS_OFFSET);
    GLES20.glVertexAttribPointer(aTextureObjectHandle, 2, GLES20.GL_FLOAT, false,
        2 * FLOAT_SIZE_BYTES, squareVertexObject);
    GLES20.glEnableVertexAttribArray(aTextureObjectHandle);

    GLES20.glUniformMatrix4fv(uMVPMatrixHandle, 1, false, MVPMatrix, 0);
    GLES20.glUniformMatrix4fv(uSTMatrixHandle, 1, false, STMatrix, 0);
    //Sampler
    GLES20.glUniform1i(uSamplerHandle, 4);
    GLES20.glActiveTexture(GLES20.GL_TEXTURE4);
    GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, previousTexId);
    //Object
    GLES20.glUniform1i(uObjectHandle, 0);
    GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
  }

  @Override
  public void release() {
    GLES20.glDeleteProgram(program);
    //DeleteTextures should be called in main thread or you will have issues.
    new Handler(Looper.getMainLooper()).post(new Runnable() {
      @Override
      public void run() {
        GLES20.glDeleteTextures(streamObjectTextureId.length, streamObjectTextureId, 0);
        streamObjectTextureId = new int[] { -1 };
      }
    });
    sprite.reset();
    if (streamObject != null) streamObject.recycle();
  }

  public void setAlpha(float alpha) {
    this.alpha = alpha;
  }

  public void setScale(float scaleX, float scaleY) {
    sprite.scale(scaleX, scaleY);
    squareVertexObject.put(sprite.getTransformedVertices()).position(0);
  }

  public void setPosition(float x, float y) {
    sprite.translate(x, y);
    squareVertexObject.put(sprite.getTransformedVertices()).position(0);
  }

  public void setPosition(TranslateTo positionTo) {
    sprite.translate(positionTo);
    squareVertexObject.put(sprite.getTransformedVertices()).position(0);
  }

  public PointF getScale() {
    return sprite.getScale();
  }

  public PointF getPosition() {
    return sprite.getTranslation();
  }

  public void setDefaultScale(int streamWidth, int streamHeight) {
    sprite.scale(streamObject.getWidth() * 100 / streamWidth,
        streamObject.getHeight() * 100 / streamHeight);
    squareVertexObject.put(sprite.getTransformedVertices()).position(0);
  }
}
