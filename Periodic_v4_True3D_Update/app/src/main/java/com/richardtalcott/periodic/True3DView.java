package com.richardtalcott.periodic;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;

public final class True3DView extends GLSurfaceView {
    public interface TapListener { void onElementTap(int atomicNumber); }
    private final True3DRenderer renderer;
    private float lastX,lastY,lastDist;
    private boolean moved;
    private TapListener tapListener;

    public True3DView(Context c, boolean atomMode, int atomicNumber) {
        super(c);
        setEGLContextClientVersion(3);
        renderer = new True3DRenderer(c, atomMode, atomicNumber);
        setRenderer(renderer);
        setRenderMode(RENDERMODE_CONTINUOUSLY);
        setPreserveEGLContextOnPause(true);
    }
    public True3DRenderer renderer(){ return renderer; }
    public void setTapListener(TapListener l){ tapListener=l; }

    @Override public boolean onTouchEvent(MotionEvent e){
        if(e.getPointerCount()==2){
            float dx=e.getX(0)-e.getX(1),dy=e.getY(0)-e.getY(1);
            float d=(float)Math.hypot(dx,dy);
            if(e.getActionMasked()==MotionEvent.ACTION_POINTER_DOWN) lastDist=d;
            else if(e.getActionMasked()==MotionEvent.ACTION_MOVE && lastDist>0){
                float ratio=d/lastDist;
                queueEvent(()->renderer.zoomBy(ratio));
                lastDist=d;
            }
            return true;
        }
        float x=e.getX(),y=e.getY();
        if(e.getActionMasked()==MotionEvent.ACTION_DOWN){lastX=x;lastY=y;moved=false;return true;}
        if(e.getActionMasked()==MotionEvent.ACTION_MOVE){
            float dx=x-lastX,dy=y-lastY;
            if(Math.abs(dx)+Math.abs(dy)>4)moved=true;
            queueEvent(()->renderer.rotateBy(dx*.22f,dy*.16f));
            lastX=x;lastY=y;return true;
        }
        if(e.getActionMasked()==MotionEvent.ACTION_UP && !moved && tapListener!=null){
            int hit=renderer.pick(x,y,getWidth(),getHeight());
            if(hit>0) tapListener.onElementTap(hit);
        }
        return true;
    }
}
