package com.richardtalcott.periodic;
import android.content.Context;
import android.graphics.*;
import android.view.View;
public final class ElementDetailView extends View {
    private final Paint p=new Paint(Paint.ANTI_ALIAS_FLAG);
    private final ElementData e;
    private final long start=System.currentTimeMillis();
    public ElementDetailView(Context c,ElementData element){super(c);e=element;}
    @Override protected void onDraw(Canvas c){
        float w=getWidth(),h=getHeight();c.drawColor(Color.rgb(3,6,16));
        p.setColor(Color.WHITE);p.setTypeface(Typeface.create(Typeface.DEFAULT,Typeface.BOLD));
        p.setTextAlign(Paint.Align.LEFT);p.setTextSize(24);c.drawText("‹  "+e.name,24,48,p);
        p.setTextSize(15);p.setColor(Color.rgb(94,198,255));c.drawText(e.category.toUpperCase(),26,75,p);
        float cx=w/2f,cy=h*.38f,maxR=Math.min(w,h)*.28f,t=(System.currentTimeMillis()-start)/1000f;
        int[] shells=ElementData.shells(e.number);
        p.setStyle(Paint.Style.STROKE);p.setStrokeWidth(2);
        for(int s=0;s<shells.length;s++){
            if(shells[s]==0)continue;float r=maxR*(s+1)/7f;p.setColor(Color.argb(100,100,195,255));c.drawCircle(cx,cy,r,p);
            for(int i=0;i<shells[s];i++){
                double a=(Math.PI*2*i/shells[s])+t*(.5f+.08f*s)*(s%2==0?1:-1);
                float x=cx+(float)Math.cos(a)*r,y=cy+(float)Math.sin(a)*r;
                p.setStyle(Paint.Style.FILL);p.setColor(Color.rgb(90,205,255));c.drawCircle(x,y,3.5f,p);p.setStyle(Paint.Style.STROKE);
            }
        }
        p.setStyle(Paint.Style.FILL);p.setColor(Color.rgb(255,64,94));c.drawCircle(cx-8,cy,18,p);
        p.setColor(Color.rgb(74,144,255));c.drawCircle(cx+10,cy+5,17,p);
        p.setTextAlign(Paint.Align.CENTER);p.setColor(Color.WHITE);p.setTextSize(46);c.drawText(e.symbol,cx,cy+maxR+68,p);
        p.setTextSize(18);p.setTypeface(Typeface.DEFAULT);p.setColor(Color.rgb(200,220,235));
        c.drawText("Atomic number  "+e.number,cx,cy+maxR+100,p);
        c.drawText("Protons  "+e.number+"     Electrons  "+e.number,cx,cy+maxR+130,p);
        c.drawText("Electron shells  "+shellText(shells),cx,cy+maxR+160,p);
        p.setTextSize(13);p.setColor(Color.rgb(105,180,220));
        c.drawText("Animated atomic model • Educational shell approximation",cx,h-32,p);
        invalidate();
    }
    private String shellText(int[] s){StringBuilder b=new StringBuilder();for(int v:s){if(v==0)break;if(b.length()>0)b.append(" • ");b.append(v);}return b.toString();}
}
