package com.richardtalcott.periodic;

import android.content.Context;
import android.graphics.*;
import android.view.View;

public final class TableHudView extends View {
    private final Paint p=new Paint(Paint.ANTI_ALIAS_FLAG);
    private final True3DRenderer renderer;
    public TableHudView(Context c,True3DRenderer r){super(c);renderer=r;setClickable(false);setFocusable(false);setLayerType(View.LAYER_TYPE_SOFTWARE,null);}

    @Override protected void onDraw(Canvas c){
        float w=getWidth(),h=getHeight();
        drawHeader(c,w,h);
        p.setTypeface(Typeface.create(Typeface.DEFAULT,Typeface.BOLD));
        for(int n=1;n<=118;n++){
            float[] xy=renderer.projected(n);
            if(xy[2]<=0)continue;
            ElementData e=ElementData.byNumber(n);
            float size=Math.max(10f,Math.min(24f,h*.031f*(24f/Math.max(13f,xy[2]))));
            p.setTextAlign(Paint.Align.CENTER);
            p.setTextSize(size);p.setColor(Color.WHITE);p.setShadowLayer(5,0,1,Color.BLACK);
            c.drawText(e.symbol,xy[0],xy[1]+size*.28f,p);p.clearShadowLayer();
            p.setTextSize(Math.max(7f,size*.43f));p.setColor(Color.rgb(214,233,246));
            c.drawText(String.valueOf(n),xy[0],xy[1]-size*.72f,p);
        }
        drawLegend(c,w,h);
        postInvalidateOnAnimation();
    }

    private void drawHeader(Canvas c,float w,float h){
        LinearGradient top=new LinearGradient(0,0,0,h*.16f,Color.argb(235,1,7,15),Color.TRANSPARENT,Shader.TileMode.CLAMP);
        p.setShader(top);c.drawRect(0,0,w,h*.17f,p);p.setShader(null);
        p.setTextAlign(Paint.Align.CENTER);p.setTypeface(Typeface.create(Typeface.DEFAULT,Typeface.BOLD));
        p.setTextSize(h*.052f);p.setColor(Color.WHITE);p.setShadowLayer(16,0,0,Color.rgb(38,166,255));
        c.drawText("PERIODIC",w*.5f,h*.065f,p);p.clearShadowLayer();
        p.setTextSize(h*.020f);p.setTypeface(Typeface.DEFAULT);p.setColor(Color.rgb(126,210,255));
        c.drawText("THE ELEMENTS OF MATTER  •  TAP TO EXPLORE",w*.5f,h*.100f,p);
    }

    private void drawLegend(Canvas c,float w,float h){
        String[] names={"ALKALI","ALKALINE","TRANSITION","POST-TRANSITION","METALLOID","NONMETAL","HALOGEN","NOBLE GAS","LANTHANIDE","ACTINIDE"};
        int[] colors={0xFFB64051,0xFFB96C36,0xFF176B9B,0xFF40566D,0xFF908527,0xFF16856F,0xFF873C89,0xFF2758A4,0xFF7047A8,0xFFA23C75};
        float total=w*.88f,x=w*.06f,y=h*.955f,cell=total/names.length;
        p.setTextAlign(Paint.Align.CENTER);p.setTypeface(Typeface.create(Typeface.DEFAULT,Typeface.BOLD));p.setTextSize(Math.max(7,h*.0125f));
        for(int i=0;i<names.length;i++){
            p.setColor(colors[i]);p.setShadowLayer(7,0,0,colors[i]);c.drawCircle(x+cell*i+cell*.5f,y-h*.018f,h*.009f,p);p.clearShadowLayer();
            p.setColor(Color.rgb(184,210,226));c.drawText(names[i],x+cell*i+cell*.5f,y,p);
        }
        p.setTextAlign(Paint.Align.RIGHT);p.setTypeface(Typeface.DEFAULT);p.setTextSize(h*.014f);p.setColor(Color.rgb(105,155,185));
        c.drawText("DRAG TO PAN  •  PINCH TO ZOOM",w*.975f,h*.985f,p);
    }
}
