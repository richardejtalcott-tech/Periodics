package com.richardtalcott.periodic;

import android.content.Context;
import android.graphics.*;
import android.view.View;

public final class TableHudView extends View {
    private final Paint p=new Paint(Paint.ANTI_ALIAS_FLAG);
    private final True3DRenderer renderer;
    public TableHudView(Context c,True3DRenderer r){super(c);renderer=r;setClickable(false);setFocusable(false);}
    @Override protected void onDraw(Canvas c){
        float w=getWidth(),h=getHeight();
        p.setTypeface(Typeface.create(Typeface.DEFAULT,Typeface.BOLD));
        for(int n=1;n<=118;n++){
            float[] xy=renderer.projected(n);
            if(xy[2]<=0)continue;
            ElementData e=ElementData.byNumber(n);
            float size=Math.max(9f,Math.min(18f,h*.025f*(25f/Math.max(12f,xy[2]))));
            p.setTextAlign(Paint.Align.CENTER);p.setTextSize(size);p.setColor(Color.WHITE);
            p.setShadowLayer(5,0,1,Color.BLACK);c.drawText(e.symbol,xy[0],xy[1]+size*.25f,p);
            p.clearShadowLayer();
            p.setTextSize(Math.max(6f,size*.48f));p.setColor(Color.rgb(190,220,238));
            c.drawText(String.valueOf(n),xy[0],xy[1]-size*.66f,p);
        }
        LinearGradient top=new LinearGradient(0,0,0,h*.14f,Color.argb(190,1,5,12),Color.TRANSPARENT,Shader.TileMode.CLAMP);
        p.setShader(top);c.drawRect(0,0,w,h*.15f,p);p.setShader(null);
        p.setTextAlign(Paint.Align.LEFT);p.setTypeface(Typeface.create(Typeface.DEFAULT,Typeface.BOLD));
        p.setTextSize(h*.052f);p.setColor(Color.WHITE);p.setShadowLayer(12,0,0,Color.rgb(45,155,255));
        c.drawText("PERIODIC",w*.035f,h*.075f,p);p.clearShadowLayer();
        p.setTextSize(h*.020f);p.setTypeface(Typeface.DEFAULT);p.setColor(Color.rgb(100,200,245));
        c.drawText("TRUE 3D ELEMENT MATRIX  •  DRAG  •  PINCH  •  TAP",w*.035f,h*.112f,p);
        postInvalidateOnAnimation();
    }
}
