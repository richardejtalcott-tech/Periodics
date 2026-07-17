package com.richardtalcott.periodic;

import android.app.Activity;
import android.content.Context;
import android.graphics.*;
import android.view.*;
import java.util.Random;

public final class ElementHudView extends View {
    private final Paint p=new Paint(Paint.ANTI_ALIAS_FLAG);
    private final ElementData e;
    private final float[] dust=new float[180];
    private final long started=System.currentTimeMillis();

    public ElementHudView(Context c,ElementData element){
        super(c);e=element;Random r=new Random(element.number*991L);
        for(int i=0;i<dust.length;i++)dust[i]=r.nextFloat();
        setLayerType(View.LAYER_TYPE_SOFTWARE,null);setClickable(true);
    }

    @Override protected void onDraw(Canvas c){
        float w=getWidth(),h=getHeight(),t=(System.currentTimeMillis()-started)/1000f;
        drawAtmosphere(c,w,h,t);drawHeader(c,w,h);drawIdentity(c,w,h);
        drawFacts(c,w,h);drawJourney(c,w,h,t);drawModeBar(c,w,h);
        postInvalidateOnAnimation();
    }

    private void drawAtmosphere(Canvas c,float w,float h,float t){
        for(int i=0;i<dust.length;i+=3){
            float x=dust[i]*w,y=(dust[i+1]*h+t*(1+dust[i+2]*3))%h;
            p.setColor(Color.argb(20+(int)(dust[i+2]*40),80,180,255));c.drawCircle(x,y,.5f+dust[i+2],p);
        }
        LinearGradient g=new LinearGradient(w*.46f,0,w,0,Color.TRANSPARENT,Color.argb(210,2,10,20),Shader.TileMode.CLAMP);
        p.setShader(g);c.drawRect(w*.42f,0,w,h,p);p.setShader(null);
    }

    private void drawHeader(Canvas c,float w,float h){
        panel(c,new RectF(w*.018f,h*.020f,w*.070f,h*.105f),0xFF45B9FF);
        p.setTextAlign(Paint.Align.CENTER);p.setTypeface(Typeface.create(Typeface.DEFAULT,Typeface.BOLD));p.setTextSize(h*.050f);p.setColor(Color.WHITE);
        c.drawText("‹",w*.044f,h*.079f,p);
        p.setTextAlign(Paint.Align.LEFT);p.setTextSize(h*.042f);p.setShadowLayer(12,0,0,0xFF258DE0);
        c.drawText(e.name+"  •  "+e.symbol,w*.085f,h*.068f,p);p.clearShadowLayer();
        p.setTypeface(Typeface.DEFAULT);p.setTextSize(h*.019f);p.setColor(0xFF78CFFF);
        c.drawText("INTERACTIVE ATOM EXPLORER",w*.086f,h*.101f,p);
    }

    private void drawIdentity(Canvas c,float w,float h){
        float cx=w*.255f;
        p.setTextAlign(Paint.Align.CENTER);p.setTypeface(Typeface.create(Typeface.DEFAULT,Typeface.BOLD));
        p.setTextSize(h*.026f);p.setColor(Color.WHITE);c.drawText(e.name+" atom",cx,h*.178f,p);
        p.setTextSize(h*.018f);p.setTypeface(Typeface.DEFAULT);p.setColor(0xFF8CCFFF);
        c.drawText("Drag to rotate  •  Pinch to zoom",cx,h*.208f,p);
    }

    private void drawFacts(Canvas c,float w,float h){
        RectF box=new RectF(w*.535f,h*.135f,w*.965f,h*.555f);panel(c,box,0xFF2CAFFF);
        float x=box.left+w*.025f,y=box.top+h*.060f;
        p.setTextAlign(Paint.Align.LEFT);p.setTypeface(Typeface.create(Typeface.DEFAULT,Typeface.BOLD));p.setColor(Color.WHITE);p.setTextSize(h*.060f);
        c.drawText(e.symbol,x,y,p);
        p.setTextSize(h*.025f);p.setColor(categoryColor());c.drawText(e.category,x+w*.080f,y-h*.012f,p);
        p.setTypeface(Typeface.DEFAULT);p.setTextSize(h*.020f);p.setColor(0xFFBCD2E1);
        c.drawText("Atomic number",x,y+h*.055f,p);value(c,String.valueOf(e.number),box.right-w*.025f,y+h*.055f);
        c.drawText("Protons",x,y+h*.098f,p);value(c,String.valueOf(e.number),box.right-w*.025f,y+h*.098f);
        c.drawText("Neutrons*",x,y+h*.141f,p);value(c,String.valueOf(e.estimatedNeutrons),box.right-w*.025f,y+h*.141f);
        c.drawText("Electrons (neutral)",x,y+h*.184f,p);value(c,String.valueOf(e.number),box.right-w*.025f,y+h*.184f);
        c.drawText("Electron shells",x,y+h*.227f,p);value(c,shells(),box.right-w*.025f,y+h*.227f);
        p.setTextSize(h*.016f);p.setColor(0xFF7995A8);
        c.drawText("*Representative isotope / rounded atomic mass",x,box.bottom-h*.025f,p);
    }

    private void value(Canvas c,String s,float x,float y){
        p.setTextAlign(Paint.Align.RIGHT);p.setTypeface(Typeface.create(Typeface.DEFAULT,Typeface.BOLD));p.setColor(Color.WHITE);c.drawText(s,x,y,p);
        p.setTextAlign(Paint.Align.LEFT);p.setTypeface(Typeface.DEFAULT);
    }

    private void drawJourney(Canvas c,float w,float h,float t){
        String[] title={"ATOM","NUCLEUS","NUCLEON","QUARKS"};
        String[] scale={"≈ 10⁻¹⁰ m","≈ 10⁻¹⁵ m","≈ 10⁻¹⁵ m","< 10⁻¹⁸ m"};
        String[] note={"electron structure","protons + neutrons","proton or neutron","quarks + gluons"};
        int[] col={0xFF42C8FF,0xFFFFC857,0xFFFF6694,0xFF8CEB79};
        float left=w*.035f,right=w*.965f,gap=w*.012f,cell=(right-left-gap*3)/4f,top=h*.625f,bottom=h*.900f;
        for(int i=0;i<4;i++){
            RectF r=new RectF(left+i*(cell+gap),top,left+i*(cell+gap)+cell,bottom);panel(c,r,col[i]);
            float cx=r.centerX(),cy=top+h*.105f,rad=h*.045f;
            RadialGradient rg=new RadialGradient(cx-rad*.25f,cy-rad*.25f,rad,col[i],0xFF07111D,Shader.TileMode.CLAMP);
            p.setShader(rg);p.setShadowLayer(12,0,0,col[i]);c.drawCircle(cx,cy,rad,p);p.clearShadowLayer();p.setShader(null);
            if(i==1) drawNucleons(c,cx,cy,rad*.78f,t);
            if(i>=2) drawQuarks(c,cx,cy,rad*.80f,t,i==2);
            p.setTextAlign(Paint.Align.CENTER);p.setTypeface(Typeface.create(Typeface.DEFAULT,Typeface.BOLD));p.setTextSize(h*.021f);p.setColor(col[i]);c.drawText(title[i],cx,top+h*.040f,p);
            p.setTypeface(Typeface.DEFAULT);p.setTextSize(h*.017f);p.setColor(0xFFFFD477);c.drawText(scale[i],cx,bottom-h*.050f,p);
            p.setColor(0xFFB9D0DE);c.drawText(note[i],cx,bottom-h*.020f,p);
            if(i<3){p.setColor(0xFF73CFFF);p.setTextSize(h*.035f);c.drawText("›",r.right+gap*.5f,cy+h*.010f,p);}
        }
    }

    private void drawNucleons(Canvas c,float cx,float cy,float r,float t){
        for(int i=0;i<10;i++){double a=i*2.4+t*.12;float rr=(float)Math.sqrt((i+.5)/10)*r;
            p.setColor(i%2==0?0xFFFF4E79:0xFF778CFF);c.drawCircle(cx+(float)Math.cos(a)*rr,cy+(float)Math.sin(a)*rr*.75f,r*.22f,p);}
    }
    private void drawQuarks(Canvas c,float cx,float cy,float r,float t,boolean nucleon){
        String[] q=nucleon?new String[]{"u","u","d"}:new String[]{"u","d","g"};int[] col={0xFFFF4E61,0xFF4E8FFF,0xFF6DDF70};
        for(int i=0;i<3;i++){double a=t*.5+i*Math.PI*2/3;float x=cx+(float)Math.cos(a)*r*.55f,y=cy+(float)Math.sin(a)*r*.55f;
            p.setColor(col[i]);c.drawCircle(x,y,r*.24f,p);p.setTextAlign(Paint.Align.CENTER);p.setTextSize(r*.25f);p.setColor(Color.WHITE);c.drawText(q[i],x,y+r*.08f,p);}
    }

    private void drawModeBar(Canvas c,float w,float h){
        p.setTextAlign(Paint.Align.CENTER);p.setTypeface(Typeface.create(Typeface.DEFAULT,Typeface.BOLD));p.setTextSize(h*.017f);
        String[] m={"3D ATOM","BOHR SHELLS","QUANTUM CLOUD","ISOTOPES","IONS","COMPOUNDS"};
        float cell=w*.14f,x=w*.08f,y=h*.965f;
        for(int i=0;i<m.length;i++){p.setColor(i==0?0xFF4DC5FF:0xFF8199A9);c.drawText(m[i],x+i*cell,y,p);}
    }

    private void panel(Canvas c,RectF r,int accent){
        p.setStyle(Paint.Style.FILL);p.setColor(0xD9071423);c.drawRoundRect(r,18,18,p);
        p.setStyle(Paint.Style.STROKE);p.setStrokeWidth(1.5f);p.setColor(Color.argb(150,Color.red(accent),Color.green(accent),Color.blue(accent)));c.drawRoundRect(r,18,18,p);p.setStyle(Paint.Style.FILL);
    }

    private int categoryColor(){String s=e.category;if(s.contains("Noble"))return 0xFF66A6FF;if(s.contains("Halogen"))return 0xFFE271E5;if(s.contains("metal"))return 0xFFFFC75A;return 0xFF6EE0BF;}
    private String shells(){int[] s=ElementData.shells(e.number);StringBuilder b=new StringBuilder();for(int v:s){if(v==0)break;if(b.length()>0)b.append(" • ");b.append(v);}return b.toString();}

    @Override public boolean onTouchEvent(MotionEvent event){
        if(event.getAction()==MotionEvent.ACTION_UP&&event.getX()<getWidth()*.08f&&event.getY()<getHeight()*.14f){
            if(getContext() instanceof Activity)((Activity)getContext()).finish();return true;
        }
        return false;
    }
}
