package com.richardtalcott.periodic;
import android.content.Context;
import android.graphics.*;
import android.view.View;
import java.util.Random;

public final class AtomSplashView extends View {
    private final Paint p=new Paint(Paint.ANTI_ALIAS_FLAG);
    private final float[] stars=new float[240];
    private final long start=System.currentTimeMillis();
    public AtomSplashView(Context c){super(c);Random r=new Random(8);for(int i=0;i<stars.length;i++)stars[i]=r.nextFloat();setLayerType(View.LAYER_TYPE_SOFTWARE,null);}

    @Override protected void onDraw(Canvas c){
        float w=getWidth(),h=getHeight(),cx=w*.5f,cy=h*.40f,r=Math.min(w,h)*.245f,t=(System.currentTimeMillis()-start)/1000f;
        c.drawColor(0xFF020711);
        RadialGradient bg=new RadialGradient(cx,cy,r*2.5f,0xFF0A2540,0xFF020711,Shader.TileMode.CLAMP);p.setShader(bg);c.drawCircle(cx,cy,r*2.5f,p);p.setShader(null);
        for(int i=0;i<stars.length;i+=3){p.setColor(Color.argb(50+(int)(stars[i+2]*170),170,220,255));c.drawCircle(stars[i]*w,stars[i+1]*h,.5f+stars[i+2]*1.4f,p);}
        // Oxygen nucleus: eight protons and eight neutrons, shown as a compact animated cluster.
        for(int i=0;i<16;i++){double a=i*2.399963+t*.12;float rr=(float)Math.sqrt((i+.4)/16)*r*.24f;
            float x=cx+(float)Math.cos(a)*rr,y=cy+(float)Math.sin(a*1.17)*rr*.72f;
            int col=i%2==0?0xFFFF456D:0xFF4B9CFF;RadialGradient g=new RadialGradient(x-5,y-5,r*.055f,Color.WHITE,col,Shader.TileMode.CLAMP);
            p.setShader(g);p.setShadowLayer(12,0,0,col);c.drawCircle(x,y,r*.055f,p);p.clearShadowLayer();p.setShader(null);
        }
        p.setStyle(Paint.Style.STROKE);p.setStrokeWidth(Math.max(2,h*.0035f));
        int[] ring={0xFF4CC7FF,0xFFFF4D73,0xFFFFFFFF};
        for(int j=0;j<3;j++){c.save();c.rotate(j*58+16,cx,cy);RectF o=new RectF(cx-r,cy-r*.36f,cx+r,cy+r*.36f);
            p.setColor(ring[j]);p.setShadowLayer(8,0,0,ring[j]);c.drawOval(o,p);p.clearShadowLayer();c.restore();}
        p.setStyle(Paint.Style.FILL);
        // Eight electrons: 2 inner, 6 outer. The continuous angle never resets visually.
        for(int i=0;i<8;i++){boolean inner=i<2;float rr=inner?r*.48f:r;double a=t*(inner?1.45:.82)+i*(Math.PI*2/(inner?2:6));
            int tilt=inner?0:(i%3);float x=cx+(float)Math.cos(a)*rr,y=cy+(float)Math.sin(a)*rr*(.30f+.04f*tilt);
            p.setColor(0xFF7FE5FF);p.setShadowLayer(13,0,0,0xFF2BBEFF);c.drawCircle(x,y,h*.011f,p);p.clearShadowLayer();}
        p.setTextAlign(Paint.Align.CENTER);p.setTypeface(Typeface.create(Typeface.DEFAULT,Typeface.BOLD));p.setColor(Color.WHITE);p.setTextSize(h*.092f);
        p.setShadowLayer(18,0,0,0xFF217CC5);c.drawText("PERIODIC",cx,h*.755f,p);p.clearShadowLayer();
        p.setTypeface(Typeface.DEFAULT);p.setTextSize(h*.024f);p.setColor(0xFF83D4FF);c.drawText("EXPLORE MATTER  •  FROM ELEMENTS TO QUARKS",cx,h*.815f,p);
        p.setTextSize(h*.016f);p.setColor(0xFF5D8198);c.drawText("OXYGEN  •  8 PROTONS  •  8 NEUTRONS  •  8 ELECTRONS",cx,h*.855f,p);
        postInvalidateOnAnimation();
    }
}
