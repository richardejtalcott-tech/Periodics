package com.richardtalcott.periodic;

import android.content.Context;
import android.graphics.*;
import android.view.View;
import java.util.Random;

public final class ElementHudView extends View {
    private final Paint p=new Paint(Paint.ANTI_ALIAS_FLAG);
    private final ElementData e;
    private final Random random;
    private final float[] dust=new float[240];
    private final long started=System.currentTimeMillis();

    public ElementHudView(Context c,ElementData element){
        super(c);
        e=element;
        random=new Random(element.number*991L);
        for(int i=0;i<dust.length;i++) dust[i]=random.nextFloat();
        setClickable(false);
        setLayerType(View.LAYER_TYPE_SOFTWARE,null);
    }

    @Override protected void onDraw(Canvas c){
        float w=getWidth(),h=getHeight();
        float t=(System.currentTimeMillis()-started)/1000f;

        drawAtmosphere(c,w,h,t);
        drawHeader(c,w,h);
        drawAtomFacts(c,w,h);
        drawMagnificationChain(c,w,h,t);
        drawQuarkPanel(c,w,h,t);
        drawScalePanel(c,w,h);
        drawFooter(c,w,h);
        postInvalidateOnAnimation();
    }

    private void drawAtmosphere(Canvas c,float w,float h,float t){
        for(int i=0;i<dust.length;i+=3){
            float x=dust[i]*w;
            float y=(dust[i+1]*h+t*(2f+dust[i+2]*5f))%h;
            p.setColor(Color.argb(18+(int)(dust[i+2]*52),105,205,255));
            c.drawCircle(x,y,.4f+dust[i+2]*1.15f,p);
        }
    }

    private void drawHeader(Canvas c,float w,float h){
        p.setTextAlign(Paint.Align.LEFT);
        p.setTypeface(Typeface.create(Typeface.DEFAULT,Typeface.BOLD));
        p.setTextSize(h*.052f);
        p.setColor(Color.WHITE);
        p.setShadowLayer(14,0,0,Color.rgb(45,160,255));
        c.drawText("A "+e.name+" Atom",w*.022f,h*.070f,p);
        p.clearShadowLayer();

        p.setTypeface(Typeface.DEFAULT);
        p.setTextSize(h*.026f);
        p.setColor(Color.rgb(255,210,102));
        c.drawText("from the atom to its fundamental particles",w*.022f,h*.108f,p);
    }

    private void drawAtomFacts(Canvas c,float w,float h){
        float x=w*.132f;
        p.setTextAlign(Paint.Align.CENTER);
        p.setTypeface(Typeface.create(Typeface.DEFAULT,Typeface.BOLD));
        p.setTextSize(h*.031f);
        p.setColor(Color.WHITE);
        c.drawText(e.name+" Atom",x,h*.175f,p);
        p.setTextSize(h*.026f);
        c.drawText("("+e.symbol+")",x,h*.208f,p);

        p.setTextAlign(Paint.Align.LEFT);
        p.setTypeface(Typeface.DEFAULT);
        p.setTextSize(h*.024f);
        float lx=w*.032f, y=h*.650f, gap=h*.043f;
        drawFact(c,lx,y,"Atomic Number:",String.valueOf(e.number),Color.WHITE);
        drawFact(c,lx,y+gap,"Protons:",String.valueOf(e.number),Color.rgb(255,110,145));
        drawFact(c,lx,y+gap*2,"Neutrons*:",String.valueOf(e.estimatedNeutrons),Color.rgb(205,195,255));
        drawFact(c,lx,y+gap*3,"Electrons:",String.valueOf(e.number),Color.rgb(105,205,255));
        drawFact(c,lx,y+gap*4,"Shells:",shells(),Color.rgb(120,220,255));
    }

    private void drawFact(Canvas c,float x,float y,String label,String value,int color){
        p.setColor(Color.rgb(205,220,232));
        c.drawText(label,x,y,p);
        p.setColor(color);
        c.drawText(value,x+getWidth()*.112f,y,p);
    }

    private void drawMagnificationChain(Canvas c,float w,float h,float t){
        float cy=h*.355f;
        float[] xs={w*.355f,w*.530f,w*.700f,w*.865f};
        float[] rs={h*.176f,h*.158f,h*.158f,h*.158f};

        // Light cones connect each level of scale.
        float startX=w*.205f,startY=h*.385f;
        for(int i=0;i<xs.length;i++){
            Path beam=new Path();
            beam.moveTo(startX,startY-h*.045f);
            beam.lineTo(xs[i]-rs[i]*.82f,cy-rs[i]*.52f);
            beam.lineTo(xs[i]-rs[i]*.82f,cy+rs[i]*.52f);
            beam.close();
            LinearGradient g=new LinearGradient(startX,startY,xs[i],cy,
                    Color.argb(12,190,230,255),Color.argb(80,220,240,255),Shader.TileMode.CLAMP);
            p.setShader(g);p.setStyle(Paint.Style.FILL);c.drawPath(beam,p);p.setShader(null);
            startX=xs[i]+rs[i]*.84f;
            startY=cy;
        }

        String[] scale={"10⁻¹⁵–10⁻¹⁴ m","≈10⁻¹⁵ m","≈10⁻¹⁵ m","<10⁻¹⁸ m"};
        String[] title={"Nucleus","Proton","Neutron","Quarks"};
        int[] accent={0xFFFFC857,0xFFFF6C9A,0xFFC99CFF,0xFF9FE870};

        for(int i=0;i<4;i++){
            drawLens(c,xs[i],cy,rs[i],accent[i]);
            p.setTextAlign(Paint.Align.CENTER);
            p.setTypeface(Typeface.create(Typeface.DEFAULT,Typeface.BOLD));
            p.setTextSize(h*.025f);
            p.setColor(Color.rgb(255,211,105));
            c.drawText(scale[i],xs[i],h*.166f,p);
            p.setTextSize(h*.031f);
            p.setColor(accent[i]);
            c.drawText(title[i],xs[i],h*.207f,p);
        }

        drawNucleus(c,xs[0],cy,rs[0]*.58f,t);
        drawProton(c,xs[1],cy,rs[1]*.53f,t);
        drawNeutron(c,xs[2],cy,rs[2]*.53f,t);
        drawQuarkField(c,xs[3],cy,rs[3]*.56f,t);

        p.setTypeface(Typeface.DEFAULT);
        p.setTextSize(h*.022f);
        p.setColor(Color.WHITE);
        drawCenteredLines(c,xs[0],h*.585f,new String[]{"The nucleus contains","protons and neutrons."},h*.031f);
        drawCenteredLines(c,xs[1],h*.585f,new String[]{"A proton contains","two up quarks and","one down quark."},h*.030f);
        drawCenteredLines(c,xs[2],h*.585f,new String[]{"A neutron contains","one up quark and","two down quarks."},h*.030f);
        drawCenteredLines(c,xs[3],h*.585f,new String[]{"Quarks interact through","the strong force, carried","by gluons."},h*.030f);
    }

    private void drawLens(Canvas c,float cx,float cy,float r,int accent){
        RadialGradient inside=new RadialGradient(cx,cy,r,
                new int[]{Color.argb(55,Color.red(accent),Color.green(accent),Color.blue(accent)),
                        Color.argb(155,5,14,27),Color.argb(230,1,5,12)},null,Shader.TileMode.CLAMP);
        p.setShader(inside);p.setStyle(Paint.Style.FILL);c.drawCircle(cx,cy,r,p);p.setShader(null);

        p.setStyle(Paint.Style.STROKE);
        p.setStrokeWidth(r*.075f);
        p.setColor(Color.rgb(145,150,160));
        p.setShadowLayer(12,0,0,accent);
        c.drawCircle(cx,cy,r,p);
        p.clearShadowLayer();
        p.setStrokeWidth(r*.025f);
        p.setColor(Color.rgb(245,225,188));
        c.drawCircle(cx,cy,r*.94f,p);
        p.setStyle(Paint.Style.FILL);

        c.save();
        c.rotate(-38,cx,cy);
        RectF handle=new RectF(cx-r*.10f,cy+r*.92f,cx+r*.10f,cy+r*1.75f);
        LinearGradient hg=new LinearGradient(handle.left,handle.top,handle.right,handle.bottom,
                Color.rgb(119,73,43),Color.rgb(51,27,18),Shader.TileMode.CLAMP);
        p.setShader(hg);c.drawRoundRect(handle,r*.09f,r*.09f,p);p.setShader(null);
        c.restore();
    }

    private void drawNucleus(Canvas c,float cx,float cy,float r,float t){
        int count=Math.min(30,Math.max(7,e.number+e.estimatedNeutrons));
        for(int i=0;i<count;i++){
            double a=i*2.399963+t*.18;
            float rr=(float)Math.sqrt((i+.5f)/count)*r;
            float x=cx+(float)Math.cos(a)*rr;
            float y=cy+(float)Math.sin(a*1.13)*rr*.78f;
            int color=(i%2==0)?Color.rgb(239,73,121):Color.rgb(160,155,205);
            p.setColor(color);p.setShadowLayer(9,0,0,color);
            c.drawCircle(x,y,r*.18f,p);
        }
        p.clearShadowLayer();
    }

    private void drawProton(Canvas c,float cx,float cy,float r,float t){
        RadialGradient g=new RadialGradient(cx-r*.18f,cy-r*.20f,r,
                Color.rgb(255,118,158),Color.rgb(91,17,55),Shader.TileMode.CLAMP);
        p.setShader(g);c.drawCircle(cx,cy,r,p);p.setShader(null);
        p.setTextAlign(Paint.Align.CENTER);p.setTypeface(Typeface.create(Typeface.DEFAULT,Typeface.BOLD));
        p.setTextSize(r*.65f);p.setColor(Color.WHITE);c.drawText("+",cx,cy+r*.22f,p);
        drawOrbitingQuarks(c,cx,cy,r*.58f,t,new String[]{"u","u","d"});
    }

    private void drawNeutron(Canvas c,float cx,float cy,float r,float t){
        RadialGradient g=new RadialGradient(cx-r*.20f,cy-r*.22f,r,
                Color.rgb(190,190,205),Color.rgb(43,46,60),Shader.TileMode.CLAMP);
        p.setShader(g);c.drawCircle(cx,cy,r,p);p.setShader(null);
        drawOrbitingQuarks(c,cx,cy,r*.58f,t,new String[]{"u","d","d"});
    }

    private void drawOrbitingQuarks(Canvas c,float cx,float cy,float r,float t,String[] labels){
        int[] colors={Color.rgb(245,83,92),Color.rgb(76,139,245),Color.rgb(113,210,93)};
        for(int i=0;i<3;i++){
            double a=t*.55+i*Math.PI*2/3;
            float x=cx+(float)Math.cos(a)*r*.58f;
            float y=cy+(float)Math.sin(a)*r*.58f;
            p.setColor(colors[i]);p.setShadowLayer(7,0,0,colors[i]);
            c.drawCircle(x,y,r*.22f,p);p.clearShadowLayer();
            p.setTextAlign(Paint.Align.CENTER);p.setTypeface(Typeface.create(Typeface.DEFAULT,Typeface.BOLD));
            p.setTextSize(r*.23f);p.setColor(Color.WHITE);c.drawText(labels[i],x,y+r*.08f,p);
        }
    }

    private void drawQuarkField(Canvas c,float cx,float cy,float r,float t){
        RadialGradient g=new RadialGradient(cx,cy,r,
                Color.rgb(104,218,103),Color.rgb(12,44,29),Shader.TileMode.CLAMP);
        p.setShader(g);p.setShadowLayer(18,0,0,Color.rgb(92,235,117));c.drawCircle(cx,cy,r*.55f,p);
        p.clearShadowLayer();p.setShader(null);
        p.setStyle(Paint.Style.STROKE);p.setStrokeWidth(1.4f);
        for(int i=0;i<14;i++){
            float a=t*.18f+i*.45f;
            p.setColor(Color.argb(55+i*7,80,230,120));
            c.drawOval(new RectF(cx-r*(.68f+i*.012f),cy-r*(.35f+i*.008f),
                    cx+r*(.68f+i*.012f),cy+r*(.35f+i*.008f)),p);
            c.rotate(13,cx,cy);
        }
        p.setStyle(Paint.Style.FILL);
    }

    private void drawQuarkPanel(Canvas c,float w,float h,float t){
        RectF box=new RectF(w*.438f,h*.715f,w*.750f,h*.958f);
        drawPanel(c,box,Color.rgb(255,185,75));
        p.setTextAlign(Paint.Align.CENTER);p.setTypeface(Typeface.create(Typeface.DEFAULT,Typeface.BOLD));
        p.setTextSize(h*.025f);p.setColor(Color.rgb(255,205,96));
        c.drawText("INSIDE THE PROTON AND NEUTRON",box.centerX(),box.top+h*.042f,p);

        float cy=box.top+h*.135f;
        drawMiniQuarks(c,w*.515f,cy,h*.050f,new String[]{"u","u","d"},"Proton (uud)");
        drawMiniQuarks(c,w*.675f,cy,h*.050f,new String[]{"u","d","d"},"Neutron (udd)");

        p.setTextAlign(Paint.Align.CENTER);p.setTypeface(Typeface.DEFAULT);p.setTextSize(h*.017f);
        p.setColor(Color.rgb(190,210,225));
        c.drawText("u = up quark    •    d = down quark    •    wavy links represent gluon exchange",box.centerX(),box.bottom-h*.018f,p);
    }

    private void drawMiniQuarks(Canvas c,float cx,float cy,float r,String[] labels,String title){
        p.setTextAlign(Paint.Align.CENTER);p.setTypeface(Typeface.create(Typeface.DEFAULT,Typeface.BOLD));
        p.setTextSize(getHeight()*.020f);p.setColor(Color.rgb(225,180,245));c.drawText(title,cx,cy-r*1.25f,p);
        int[] col={Color.rgb(239,76,88),Color.rgb(70,135,245),Color.rgb(110,205,92)};
        float[][] pos={{-.62f,-.18f},{.62f,-.18f},{0,.72f}};
        p.setStyle(Paint.Style.STROKE);p.setStrokeWidth(2.5f);p.setColor(Color.rgb(255,207,55));
        c.drawLine(cx+pos[0][0]*r,cy+pos[0][1]*r,cx+pos[1][0]*r,cy+pos[1][1]*r,p);
        c.drawLine(cx+pos[0][0]*r,cy+pos[0][1]*r,cx,cy+pos[2][1]*r,p);
        c.drawLine(cx+pos[1][0]*r,cy+pos[1][1]*r,cx,cy+pos[2][1]*r,p);
        p.setStyle(Paint.Style.FILL);
        for(int i=0;i<3;i++){
            float x=cx+pos[i][0]*r,y=cy+pos[i][1]*r;
            p.setColor(col[i]);c.drawCircle(x,y,r*.34f,p);
            p.setColor(Color.WHITE);p.setTextSize(r*.35f);c.drawText(labels[i],x,y+r*.12f,p);
        }
    }

    private void drawScalePanel(Canvas c,float w,float h){
        RectF box=new RectF(w*.775f,h*.715f,w*.973f,h*.958f);
        drawPanel(c,box,Color.rgb(255,185,75));
        p.setTextAlign(Paint.Align.CENTER);p.setTypeface(Typeface.create(Typeface.DEFAULT,Typeface.BOLD));
        p.setTextSize(h*.026f);p.setColor(Color.rgb(255,205,96));c.drawText("THE SCALE",box.centerX(),box.top+h*.043f,p);
        p.setTypeface(Typeface.DEFAULT);p.setTextSize(h*.020f);p.setColor(Color.WHITE);
        float y=box.top+h*.094f,g=h*.038f;
        String[] lines={"Atom  ≈ 10⁻¹⁰ m","Nucleus  ≈ 10⁻¹⁵–10⁻¹⁴ m","Proton / neutron  ≈ 10⁻¹⁵ m","Quark  < 10⁻¹⁸ m","Electron  < 10⁻¹⁸ m"};
        for(String line:lines){c.drawText(line,box.centerX(),y,p);y+=g;}
    }

    private void drawPanel(Canvas c,RectF r,int accent){
        p.setStyle(Paint.Style.FILL);p.setColor(Color.argb(170,3,10,21));c.drawRoundRect(r,18,18,p);
        p.setStyle(Paint.Style.STROKE);p.setStrokeWidth(1.5f);p.setColor(Color.argb(170,Color.red(accent),Color.green(accent),Color.blue(accent)));
        c.drawRoundRect(r,18,18,p);p.setStyle(Paint.Style.FILL);
    }

    private void drawCenteredLines(Canvas c,float x,float y,String[] lines,float gap){
        p.setTextAlign(Paint.Align.CENTER);
        for(String line:lines){c.drawText(line,x,y,p);y+=gap;}
    }

    private void drawFooter(Canvas c,float w,float h){
        p.setTextAlign(Paint.Align.LEFT);p.setTypeface(Typeface.DEFAULT);p.setTextSize(h*.014f);
        p.setColor(Color.rgb(105,145,168));
        c.drawText("*Neutron count uses rounded atomic mass and is an educational estimate.",w*.024f,h*.982f,p);
    }

    private String shells(){
        int[] s=ElementData.shells(e.number);
        StringBuilder b=new StringBuilder();
        for(int v:s){if(v==0)break;if(b.length()>0)b.append(" • ");b.append(v);}
        return b.toString();
    }
}
