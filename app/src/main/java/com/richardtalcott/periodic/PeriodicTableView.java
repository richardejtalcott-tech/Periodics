package com.richardtalcott.periodic;
import android.content.*;
import android.graphics.*;
import android.view.*;
import java.util.*;
public final class PeriodicTableView extends View {
    private final Paint p=new Paint(Paint.ANTI_ALIAS_FLAG);
    private final ArrayList<RectF> cells=new ArrayList<>();
    private final ArrayList<Integer> nums=new ArrayList<>();
    private float scale=1f, lastX,lastY, offX=0,offY=0; private boolean moved;
    public PeriodicTableView(Context c){super(c); p.setTypeface(Typeface.create(Typeface.DEFAULT,Typeface.BOLD));}
    @Override protected void onDraw(Canvas c){
        c.drawColor(Color.rgb(3,6,16)); float pad=18, header=66;
        p.setTextAlign(Paint.Align.LEFT);p.setTextSize(34);p.setColor(Color.WHITE);c.drawText("PERIODIC",pad,42,p);
        p.setTextSize(15);p.setColor(Color.rgb(92,196,255));c.drawText("PINCH TO ZOOM • DRAG • TAP AN ELEMENT",190,39,p);
        c.save();c.translate(offX,offY);c.scale(scale,scale);
        float cw=(getWidth()-pad*2)/18f, ch=Math.min(66,(getHeight()-header-25)/9f);
        cells.clear();nums.clear();
        for(int n=1;n<=118;n++){
            ElementData e=ElementData.byNumber(n); float left=pad+(e.group-1)*cw, top=header+(e.row-1)*ch;
            RectF r=new RectF(left+2,top+2,left+cw-2,top+ch-2); cells.add(r);nums.add(n);
            int base=color(e.category); p.setStyle(Paint.Style.FILL);p.setColor(base);c.drawRoundRect(r,7,7,p);
            p.setStyle(Paint.Style.STROKE);p.setStrokeWidth(1.5f);p.setColor(Color.argb(170,210,238,255));c.drawRoundRect(r,7,7,p);
            p.setStyle(Paint.Style.FILL);p.setColor(Color.argb(120,255,255,255));c.drawRect(r.left+3,r.top+3,r.right-3,r.top+6,p);
            p.setTextAlign(Paint.Align.LEFT);p.setTextSize(Math.max(9,ch*.19f));p.setColor(Color.rgb(195,225,245));c.drawText(String.valueOf(n),r.left+5,r.top+13,p);
            p.setTextAlign(Paint.Align.CENTER);p.setTextSize(Math.max(14,ch*.38f));p.setColor(Color.WHITE);c.drawText(e.symbol,r.centerX(),r.centerY()+7,p);
        }
        c.restore();
    }
    private int color(String cat){
        if(cat.contains("Noble"))return Color.rgb(45,72,128); if(cat.contains("Halogen"))return Color.rgb(100,43,90);
        if(cat.contains("Alkali"))return Color.rgb(116,48,55); if(cat.contains("Transition"))return Color.rgb(31,83,112);
        if(cat.contains("Lanthanide"))return Color.rgb(85,49,116); if(cat.contains("Actinide"))return Color.rgb(112,43,76);
        if(cat.equals("Nonmetal"))return Color.rgb(27,103,86); if(cat.equals("Metalloid"))return Color.rgb(95,91,35);
        return Color.rgb(54,68,86);
    }
    @Override public boolean onTouchEvent(android.view.MotionEvent ev){
        if(ev.getPointerCount()==2){
            float dx=ev.getX(0)-ev.getX(1),dy=ev.getY(0)-ev.getY(1),dist=(float)Math.hypot(dx,dy);
            if(ev.getActionMasked()==MotionEvent.ACTION_POINTER_DOWN)lastX=dist;
            else if(ev.getActionMasked()==MotionEvent.ACTION_MOVE&&lastX>0){scale=Math.max(.75f,Math.min(2.5f,scale*dist/lastX));lastX=dist;invalidate();}
            return true;
        }
        float x=ev.getX(),y=ev.getY();
        if(ev.getAction()==MotionEvent.ACTION_DOWN){lastX=x;lastY=y;moved=false;return true;}
        if(ev.getAction()==MotionEvent.ACTION_MOVE){if(Math.abs(x-lastX)+Math.abs(y-lastY)>5)moved=true;offX+=x-lastX;offY+=y-lastY;lastX=x;lastY=y;invalidate();return true;}
        if(ev.getAction()==MotionEvent.ACTION_UP&&!moved){
            float tx=(x-offX)/scale,ty=(y-offY)/scale;
            for(int i=0;i<cells.size();i++)if(cells.get(i).contains(tx,ty)){
                Intent in=new Intent(getContext(),ElementDetailActivity.class);in.putExtra("atomicNumber",nums.get(i));getContext().startActivity(in);break;
            }
        }
        return true;
    }
}
