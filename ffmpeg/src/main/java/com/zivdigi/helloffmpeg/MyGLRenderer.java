package com.zivdigi.helloffmpeg;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.DisplayMetrics;
import android.util.Log;

import java.nio.ByteBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by mingsoft on 16/6/23.
 */
public class MyGLRenderer implements GLSurfaceView.Renderer {

    private ByteBuffer y;
    private ByteBuffer u;
    private ByteBuffer v;
    private GLSurfaceView mTargetSurface;
    private GLProgram prog = new GLProgram(0);
    private int mScreenWidth, mScreenHeight;
    private int mVideoWidth = 0, mVideoHeight = 0;
    private int width = 0;
    private int height = 0;
    private int ScreenState ;



    public MyGLRenderer(GLSurfaceView surface, DisplayMetrics dm,Context context) {
        mTargetSurface = surface;
        mScreenWidth = dm.widthPixels;
        mScreenHeight = dm.heightPixels;
    }


    //仅调用一次，用于设置view的OpenGL ES环境。
    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
//        // 设置背景色
        GLES20.glClearColor(0, 0, 0, 0);
        if (!prog.isProgramBuilt()) {
            prog.buildProgram();//初始化创建
//            gl.glMatrixMode(GL10.GL_PROJECTION);
        }
    }

    //当view的几何形状发生变化时调用，比如设备从竖屏变为横屏。
    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        this.width = width;
        this.height = height;
        Log.i("onsurfaceChange", "width==" + width + "height==" + height);
        GLES20.glViewport(0, 0, width, height);
        surfaceChangeing(height, width);
    }


    //每次重绘view时调用。
    @Override
    public void onDrawFrame(GL10 gl) {
        synchronized (this) {
            if (y != null) {
                // reset position, have to be done
                y.position(0);
                u.position(0);
                v.position(0);
                prog.buildTextures(y, u, v, mVideoWidth, mVideoHeight);
                GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
                GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
                prog.drawFrame();
            }
        }
        changeGLViewport(gl);           //配置相机拉近与远离(含移动移动方法)
    }


    /**
     * this method will be called from native code, it happens when the video is about to play or
     * the video size changes.
     */
    public void update(int w, int h) {
        if (w > 0 && h > 0) {
            // 调整比例
            if (mScreenWidth > 0 && mScreenHeight > 0) {
                float f1 = 1f * mScreenHeight / mScreenWidth;
                float f2 = 1f * h / w;
                if (f1 == f2) {
                    prog.createBuffers(GLProgram.squareVertices);
                } else if (f1 < f2) {
                    float widScale = f1 / f2;
                    prog.createBuffers(new float[]{-widScale, -1.0f, widScale, -1.0f, -widScale, 1.0f, widScale,
                            1.0f,});
                } else {
                    float heightScale = f2 / f1;
                    prog.createBuffers(new float[]{-1.0f, -heightScale, 1.0f, -heightScale, -1.0f, heightScale, 1.0f,
                            heightScale,});
                }
            }
            // 初始化容器
            if (w != mVideoWidth && h != mVideoHeight) {
                this.mVideoWidth = w;
                this.mVideoHeight = h;
                int yarraySize = w * h;
                int uvarraySize = yarraySize / 4;
                synchronized (this) {
                    y = ByteBuffer.allocate(yarraySize);
                    u = ByteBuffer.allocate(uvarraySize);
                    v = ByteBuffer.allocate(uvarraySize);
                }
            }
        }
    }

    /**
     * 更新GlSufaceView数据
     */
    public void update(byte[] src, int w, int h) {
        synchronized (this) {
            y.clear();
            u.clear();
            v.clear();
            y.put(src, 0, w * h);
            u.put(ArrayUtil.copyOfRange(src, w * h, w * h * 5 / 4), 0, w * h / 4);
            v.put(ArrayUtil.copyOfRange(src, w * h * 5 / 4, w * h * 3 / 2), 0, w * h / 4);
        }
        // request to render
        mTargetSurface.requestRender();
    }

    /**
     * 横竖屏切换界面适应
     *
     * @param w
     * @param h
     */
    private void surfaceChangeing(int w, int h) {
        // 调整比例
        if (mScreenWidth > 0 && mScreenHeight > 0) {
            float f1 = 1f * mScreenHeight / mScreenWidth;
            float f2 = 1f * h / w;
            if (f1 == f2) {
                prog.createBuffers(GLProgram.squareVertices);
            } else if (f1 < f2) {
                float widScale = f1 / f2;
                prog.createBuffers(new float[]{-widScale, -1.0f, widScale, -1.0f, -widScale, 1.0f, widScale,
                        1.0f,});
            } else {
                float heightScale = f2 / f1;
                prog.createBuffers(new float[]{-1.0f, -heightScale, 1.0f, -heightScale, -1.0f, heightScale, 1.0f,
                        heightScale,});
            }
        }
    }

    public void test(){
        System.out.println("问你");
        prog.createBuffers(new float[]{-1.0f, 0.0f, 0.0f, 0.0f, -1.0f, 1.0f, 0.0f,
               1.0f,});
    }

    private int maxOffset = 100;
    private int viewportOffset;
    private int ScanlteX, ScanlteY;
    private int currentBitmapWidth;     //获取画布当前宽度width
    private int currentBitmapHeight;    //获取画布当前高度height
    private int bitmapWidth;
    private int bitmapHeight;
    public static float ChangeScale;

    public static boolean Translate = false;
    private static final float MIN_SCALE = 1.0f;//恢复缩放比例
    private static final float MAX_SCALE = 8.0f;//最大缩放比例
    private static final String TAG = "MyGLRenderer";

    /**
     * 通过改变gl的视角获取
     *
     * @param gl
     */
    private void  changeGLViewport(GL10 gl) {
        if (ChangeScale <= MIN_SCALE) {
            ChangeScale = MIN_SCALE;
            bitmapWidth = width;
            bitmapHeight = height;
            gl.glViewport(0, 0, width, height);
        } else {
            if (ChangeScale > MAX_SCALE) {
                ChangeScale = MAX_SCALE;
            }
            int k = -2;
            viewportOffset = (int) (80 * ChangeScale);//变量值
//            Log.i(TAG, "ChangeScale: " + ChangeScale);
            ScanlteX = -maxOffset + viewportOffset * k;//也是变量值X
            ScanlteY = -maxOffset + viewportOffset * k;//也是变量值Y

            currentBitmapWidth = this.width - viewportOffset * 2 * k + maxOffset * 2;
            currentBitmapHeight = this.height - viewportOffset * 2 * k + maxOffset * 2;
            //居中缩放函数(放大缩小)
            gl.glViewport(ScanlteX, ScanlteY, currentBitmapWidth, currentBitmapHeight);
//            Log.i("translating", "ScanlteY=" + ScanlteY);
            //调用移动函数
            if (Translate) {
                translateView(gl);          //移动
            } else {
                if (haveUp) {
                    haveUp = false;
                    offsetX = (int) (ScanlteX + transx);
                    offSetY = (int) (ScanlteY - transy);
                    transx = 0;
                    transy = 0;
//                    Log.i("translate", "offsetX =" + offsetX + ",offSetY=" + offSetY);
                }
            }
        }
        // request to render
        mTargetSurface.requestRender();
    }


    private int offsetX, offSetY;   //记录上一次的偏移量
    public static float transx = 0; //X轴偏移量
    public static float transy = 0; //Y轴偏移量
    public static boolean isFirst = true; //第一次应该获取偏移值
    public static boolean Second = false;//第二次
    public static boolean haveUp = false;
    private float aX, aY;

    private void translateView(GL10 gl) {
        if(ScreenState == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE){   //横屏状态
            if(transy > 350){
                transy = 350;
            }else if(transy < -350){
                transy = -350;
            }
        }else {
            if (-ScanlteY - transy - 1720 > bitmapHeight - currentBitmapHeight) {
                Log.i("translating", "ChangeScale=" + ChangeScale);
                transy = 0;
            }
            if(transy > 50){
                transy = 50;
            }else if(transy < -50){
                transy = -50;
            }
        }

        //GLSurfaceView 是以右边为X轴正轴,上边为Y轴上轴    所以的Y的偏移量为-
        if (isFirst) {//NO1 移动
            isFirst = false;
            offsetX = (int) (ScanlteX + transx);
            offSetY = (int) (ScanlteY - transy);
            gl.glViewport(offsetX, offSetY, currentBitmapWidth, currentBitmapHeight);
        } else { //No2移动
            if (Second) {
                Second = false;
                aX = transx;
                aY = transy;
                offsetX = (int) (offsetX + aX);
                offSetY = (int) (offSetY - aY);
                transx = 0;
                transy = 0;
            } else {
                if (offsetX + transx > 0) {
                    gl.glViewport(0, (int) (offSetY - transy), currentBitmapWidth, currentBitmapHeight);
                } else {
                    if (offsetX + transx < bitmapWidth - currentBitmapWidth) {
                        gl.glViewport(bitmapWidth - currentBitmapWidth, (int) (offSetY - transy), currentBitmapWidth, currentBitmapHeight);
                    } else {
                        gl.glViewport((int) (offsetX + transx), (int) (offSetY - transy), currentBitmapWidth, currentBitmapHeight);
                    }
                }

            }
        }
        Log.i("translate", "currentBitmapWidth:" + currentBitmapWidth + ",currentBitmapHeight:" + currentBitmapHeight + "  offsetX=" + offsetX + ",offSetY=" + offSetY);
    }


    public static void Refreshvar() {
        Translate = false;
        ChangeScale = 1.0f;
        transx = 0;
        transy = 0;
    }

    public int getScreenState() {
        return ScreenState;
    }

    public void setScreenState(int screenState) {
        ScreenState = screenState;
    }


}
