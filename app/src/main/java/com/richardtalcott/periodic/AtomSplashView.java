package com.richardtalcott.periodic;
import android.content.Context;
import android.graphics.*;
import android.view.View;
import java.util.Random;
public final class AtomSplashView extends View {
    private final Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Random random = new Random(8);
    private final float[] stars = new float[180];
    private final long start = System.currentTimeMillis();
    public AtomSplashView(Context c) {
        super(c);
        for (int i=0;i<stars.length;i++) stars[i]=random.nextFloat();
    }
    @Override protected void onDraw(Canvas c) {
        float w=getWidth(), h=getHeight(); c.drawColor(Color.rgb(3,6,16));
        p.setColor(Color.WHITE);
        for(int i=0;i<stars.length;i+=3){ p.setAlpha(70+(int)(stars[i+2]*180)); c.drawCircle(stars[i]*w,stars[i+1]*h,0.7f+stars[i+2]*1.8f,p); }
        p.setAlpha(255); float cx=w/2f, cy=h*.42f, r=Math.min(w,h)*.24f, t=(System.currentTimeMillis()-start)/1000f;
        p.setStyle(Paint.Style.STROKE); p.setStrokeWidth(4f);
        int[] colors={Color.rgb(75,199,255),Color.rgb(255,65,99),Color.WHITE};
        for(int i=0;i<3;i++){
            p.setColor(colors[i]); c.save(); c.rotate(i*60+20,cx,cy);
            RectF oval=new RectF(cx-r,cy-r*.38f,cx+r,cy+r*.38f); c.drawOval(oval,p);
            double a=t*(1.4+i*.25)+i*2.1; float ex=cx+(float)Math.cos(a)*r, ey=cy+(float)Math.sin(a)*r*.38f;
            p.setStyle(Paint.Style.FILL); c.drawCircle(ex,ey,9,p); p.setStyle(Paint.Style.STROKE); c.restore();
        }
        p.setStyle(Paint.Style.FILL); p.setColor(Color.rgb(255,57,88)); c.drawCircle(cx-8,cy,22,p);
        p.setColor(Color.rgb(70,153,255)); c.drawCircle(cx+13,cy+4,20,p);
        p.setColor(Color.WHITE); p.setTextAlign(Paint.Align.CENTER); p.setTypeface(Typeface.create(Typeface.DEFAULT,Typeface.BOLD));
        p.setTextSize(Math.min(w,h)*.10f); c.drawText("PERIODIC",cx,h*.76f,p);
        p.setTypeface(Typeface.DEFAULT); p.setTextSize(Math.min(w,h)*.026f); p.setColor(Color.rgb(125,205,255));
        c.drawText("EXPLORE MATTER FROM ELEMENTS TO QUARKS",cx,h*.82f,p);
        invalidate();
    }
}
