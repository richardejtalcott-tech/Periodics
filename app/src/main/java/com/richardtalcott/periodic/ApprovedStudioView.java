package com.richardtalcott.periodic;

import android.app.Activity;
import android.content.Context;
import android.graphics.*;
import android.util.Base64;
import android.view.*;
import java.io.*;
import java.util.*;

/** Pixel-faithful approved-screen shell. Interactive overlays are kept separate
 * from the approved artwork so the visual contract cannot drift. */
public final class ApprovedStudioView extends View {
    private static final String[] ASSETS={
        "table","element","atom","isotope","ion","bond","states","compare"
    };
    private final Paint paint=new Paint(Paint.ANTI_ALIAS_FLAG|Paint.FILTER_BITMAP_FLAG);
    private final Paint overlay=new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Bitmap[] screens=new Bitmap[ASSETS.length];
    private int page=0;

    public ApprovedStudioView(Context context){
        super(context);
        setBackgroundColor(Color.rgb(2,7,14));
        for(int i=0;i<ASSETS.length;i++) screens[i]=load(context,ASSETS[i]);
        setFocusable(true);
    }

    private Bitmap load(Context c,String name){
        try(InputStream in=c.getAssets().open("approved/"+name+".b64");
            ByteArrayOutputStream out=new ByteArrayOutputStream()){
            byte[] b=new byte[8192]; int n;
            while((n=in.read(b))>0) out.write(b,0,n);
            byte[] raw=Base64.decode(out.toByteArray(),Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(raw,0,raw.length);
        }catch(IOException e){throw new IllegalStateException("Missing approved screen "+name,e);}
    }

    @Override protected void onDraw(Canvas c){
        super.onDraw(c);
        Bitmap b=screens[page];
        float scale=Math.max(getWidth()/(float)b.getWidth(),getHeight()/(float)b.getHeight());
        float w=b.getWidth()*scale,h=b.getHeight()*scale;
        RectF dst=new RectF((getWidth()-w)/2f,(getHeight()-h)/2f,(getWidth()+w)/2f,(getHeight()+h)/2f);
        c.drawBitmap(b,null,dst,paint);
        // A subtle edge vignette preserves the dark immersive approved presentation.
        RadialGradient vignette=new RadialGradient(getWidth()/2f,getHeight()/2f,
            Math.max(getWidth(),getHeight())*.72f,
            new int[]{Color.TRANSPARENT,Color.argb(115,0,0,0)},null,Shader.TileMode.CLAMP);
        overlay.setShader(vignette); c.drawRect(0,0,getWidth(),getHeight(),overlay); overlay.setShader(null);
    }

    @Override public boolean onTouchEvent(android.view.MotionEvent e){
        if(e.getAction()!=MotionEvent.ACTION_UP)return true;
        float x=e.getX()/getWidth(),y=e.getY()/getHeight();
        if(page!=0 && x<.12f && y<.18f){page=page==1?0:1;invalidate();return true;}
        if(page==0){page=1;invalidate();return true;}
        if(page==1){
            // Approved element-information navigation strip.
            if(y>.78f){
                if(x<.17f)page=2;
                else if(x<.33f)page=3;
                else if(x<.49f)page=4;
                else if(x<.65f)page=5;
                else if(x<.82f)page=6;
                else page=7;
                invalidate(); return true;
            }
            page=2;invalidate();return true;
        }
        // Tap lower-right to advance through approved labs; upper-left returns.
        if(x>.72f&&y>.72f){page=page==7?0:page+1;invalidate();}
        return true;
    }
}
