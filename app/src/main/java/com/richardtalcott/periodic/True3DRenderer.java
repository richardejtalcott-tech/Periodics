package com.richardtalcott.periodic;

import android.content.Context;
import android.opengl.*;
import java.nio.*;
import java.util.*;

public final class True3DRenderer implements android.opengl.GLSurfaceView.Renderer {
    private final Context context;
    private final boolean atomMode;
    private final int atomicNumber;
    private int program;
    private Mesh cube,sphere,plane;
    private int width,height;
    private float yaw=0f,pitch=0f,zoom=1f,panX=0f,panY=0f;
    private final float[] projection=new float[16],view=new float[16],vp=new float[16],model=new float[16],mvp=new float[16],normal=new float[16];
    private final float[][] projected=new float[119][3];
    private long start=System.nanoTime();

    private static final String VS=
        "#version 300 es\n"+
        "layout(location=0) in vec3 aPos;layout(location=1) in vec3 aNormal;\n"+
        "uniform mat4 uMVP;uniform mat4 uModel;out vec3 vN;out vec3 vP;\n"+
        "void main(){vec4 w=uModel*vec4(aPos,1.0);vP=w.xyz;vN=mat3(uModel)*aNormal;gl_Position=uMVP*vec4(aPos,1.0);}";
    private static final String FS=
        "#version 300 es\nprecision highp float;in vec3 vN;in vec3 vP;uniform vec3 uColor;uniform float uMetal;out vec4 outColor;\n"+
        "void main(){vec3 n=normalize(vN);vec3 l=normalize(vec3(-.35,.8,.55));float d=max(dot(n,l),0.0);float rim=pow(1.0-max(dot(n,normalize(vec3(0.0,.4,1.0))),0.0),2.2);"+
        "vec3 c=uColor*(.18+.82*d)+vec3(.12,.35,.55)*rim*(.35+uMetal);float spec=pow(max(dot(reflect(-l,n),normalize(vec3(0.0,.3,1.0))),0.0),28.0);"+
        "outColor=vec4(c+spec*(.25+.65*uMetal),1.0);}";

    public True3DRenderer(Context c,boolean atomMode,int atomicNumber){this.context=c;this.atomMode=atomMode;this.atomicNumber=atomicNumber;}

    @Override public void onSurfaceCreated(javax.microedition.khronos.opengles.GL10 gl, javax.microedition.khronos.egl.EGLConfig config){
        GLES30.glClearColor(.004f,.012f,.027f,1f);
        GLES30.glEnable(GLES30.GL_DEPTH_TEST);GLES30.glEnable(GLES30.GL_CULL_FACE);
        program=program(VS,FS);cube=cube();sphere=sphere(16,24);plane=plane();
    }
    @Override public void onSurfaceChanged(javax.microedition.khronos.opengles.GL10 gl,int w,int h){
        width=w;height=h;GLES30.glViewport(0,0,w,h);Matrix.perspectiveM(projection,0,38f,(float)w/h,.1f,100f);
    }
    @Override public void onDrawFrame(javax.microedition.khronos.opengles.GL10 gl){
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT|GLES30.GL_DEPTH_BUFFER_BIT);
        GLES30.glUseProgram(program);
        if(atomMode) drawAtom(); else drawTable();
    }

    private void camera(float eyeY,float eyeZ){
        if(atomMode){
            Matrix.setLookAtM(view,0,0,eyeY,eyeZ,0,0,0,0,1,0);
            Matrix.rotateM(view,0,pitch,1,0,0);
            Matrix.rotateM(view,0,yaw,0,1,0);
        }else{
            Matrix.setLookAtM(view,0,panX,panY,24f/zoom,panX,panY,0,0,1,0);
        }
        Matrix.multiplyMM(vp,0,projection,0,view,0);
    }
    private void drawTable(){
        camera(0f,24f);
        // Layered laboratory wall: depth without tilting the readable table.
        draw(cube,0,0,-2.2f,15.4f,6.2f,.08f,0x06121F,.18f);
        for(int i=-6;i<=6;i+=2) draw(cube,0,i*.84f,-2.0f,15.0f,.014f,.025f,0x176B94,.35f);
        for(int i=-14;i<=14;i+=2) draw(cube,i,0,-2.0f,.014f,6.0f,.025f,0x123B55,.25f);
        for(int n=1;n<=118;n++){
            ElementData e=ElementData.byNumber(n);
            float x=(e.group-9.5f)*1.28f;
            float y=(5.05f-e.row)*1.10f;
            float pulse=.04f*(float)Math.sin((System.nanoTime()/1_000_000_000.0)+n*.41);
            int col=color(e.category);
            draw(cube,x,y,0,.56f,.47f,.18f+pulse,col,.86f);
            draw(cube,x,y,.205f,.50f,.41f,.025f,brighten(col),.22f);
            project(n,x,y,.28f);
        }
    }
    private void drawFloor(){
        draw(plane,0,-.02f,0,14.6f,1f,7.5f,0x142436,.9f);
        for(int i=-10;i<=10;i+=2) draw(cube,i*1.2f,.05f,5.9f,.035f,.05f,7.1f,0x12304A,.2f);
    }
    private void drawAtom(){
        camera(1.2f/zoom,14.5f/zoom);
        float t=(System.nanoTime()-start)/1_000_000_000f;
        ElementData e=ElementData.byNumber(atomicNumber);
        final float baseX=-4.55f;

        int nucleons=Math.min(64,Math.max(8,e.number+e.estimatedNeutrons));
        for(int i=0;i<nucleons;i++){
            double a=i*2.399963+t*.13;
            float r=(float)Math.sqrt((i+.5f)/nucleons)*1.12f;
            float x=baseX+(float)Math.cos(a)*r;
            float y=(float)Math.sin(a*1.31)*r*.78f;
            float z=(float)Math.sin(a)*r;
            draw(sphere,x,y,z,.23f,.23f,.23f,(i%2==0)?0xFF4167:0x4C95FF,.42f);
        }

        int[] shells=ElementData.shells(e.number);
        for(int s=0;s<shells.length;s++){
            if(shells[s]==0)break;
            int visible=Math.min(shells[s],18);
            float rr=1.95f+s*.63f;
            for(int i=0;i<visible;i++){
                double a=2*Math.PI*i/visible+t*(.76+s*.085)*(s%2==0?1:-1);
                float x=baseX+(float)Math.cos(a)*rr;
                float y=(float)Math.sin(a)*rr*.38f;
                float z=(float)Math.sin(a*.73+s)*rr*.16f;
                draw(sphere,x,y,z,.105f,.105f,.105f,0x59DBFF,.14f);
            }
        }
    }

    private void draw(Mesh mesh,float x,float y,float z,float sx,float sy,float sz,int rgb,float metal){
        Matrix.setIdentityM(model,0);Matrix.translateM(model,0,x,y,z);Matrix.scaleM(model,0,sx,sy,sz);
        Matrix.multiplyMM(mvp,0,vp,0,model,0);
        GLES30.glUniformMatrix4fv(GLES30.glGetUniformLocation(program,"uMVP"),1,false,mvp,0);
        GLES30.glUniformMatrix4fv(GLES30.glGetUniformLocation(program,"uModel"),1,false,model,0);
        GLES30.glUniform3f(GLES30.glGetUniformLocation(program,"uColor"),((rgb>>16)&255)/255f,((rgb>>8)&255)/255f,(rgb&255)/255f);
        GLES30.glUniform1f(GLES30.glGetUniformLocation(program,"uMetal"),metal);
        mesh.draw();
    }
    private void project(int n,float x,float y,float z){
        float[] p={x,y,z,1},clip=new float[4];Matrix.multiplyMV(clip,0,vp,0,p,0);
        if(clip[3]!=0){float nx=clip[0]/clip[3],ny=clip[1]/clip[3];projected[n][0]=(nx*.5f+.5f)*width;projected[n][1]=(-ny*.5f+.5f)*height;projected[n][2]=clip[3];}
    }
    public synchronized float[] projected(int n){return projected[n].clone();}
    public void rotateBy(float dx,float dy){if(atomMode){yaw+=dx;pitch=Math.max(-28,Math.min(28,pitch+dy));}else{panX-=dx*.018f/zoom;panY+=dy*.018f/zoom;}}
    public void zoomBy(float r){zoom=Math.max(.72f,Math.min(2.25f,zoom*r));}
    public int pick(float x,float y,int w,int h){int best=-1;float bd=45f*45f;for(int n=1;n<=118;n++){float[] p=projected[n];float dx=x-p[0],dy=y-p[1],d=dx*dx+dy*dy;if(d<bd){bd=d;best=n;}}return best;}

    private int brighten(int rgb){
        int r=Math.min(255,((rgb>>16)&255)+28),g=Math.min(255,((rgb>>8)&255)+28),b=Math.min(255,(rgb&255)+28);
        return (r<<16)|(g<<8)|b;
    }
    private int color(String c){if(c.contains("Noble"))return 0x2758A4;if(c.contains("Halogen"))return 0x873C89;if(c.contains("Alkali"))return 0xB64051;if(c.contains("Transition"))return 0x176B9B;if(c.contains("Lanthanide"))return 0x7047A8;if(c.contains("Actinide"))return 0xA23C75;if(c.equals("Nonmetal"))return 0x16856F;if(c.equals("Metalloid"))return 0x908527;return 0x40566D;}

    private static int shader(int type,String src){int s=GLES30.glCreateShader(type);GLES30.glShaderSource(s,src);GLES30.glCompileShader(s);int[] ok={0};GLES30.glGetShaderiv(s,GLES30.GL_COMPILE_STATUS,ok,0);if(ok[0]==0)throw new RuntimeException(GLES30.glGetShaderInfoLog(s));return s;}
    private static int program(String vs,String fs){int p=GLES30.glCreateProgram(),v=shader(GLES30.GL_VERTEX_SHADER,vs),f=shader(GLES30.GL_FRAGMENT_SHADER,fs);GLES30.glAttachShader(p,v);GLES30.glAttachShader(p,f);GLES30.glLinkProgram(p);return p;}

    static final class Mesh{
        final int vao,count;
        Mesh(float[] v,short[] idx){int[] a={0};GLES30.glGenVertexArrays(1,a,0);vao=a[0];GLES30.glBindVertexArray(vao);GLES30.glGenBuffers(1,a,0);GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER,a[0]);FloatBuffer vb=ByteBuffer.allocateDirect(v.length*4).order(ByteOrder.nativeOrder()).asFloatBuffer();vb.put(v).position(0);GLES30.glBufferData(GLES30.GL_ARRAY_BUFFER,v.length*4,vb,GLES30.GL_STATIC_DRAW);GLES30.glGenBuffers(1,a,0);GLES30.glBindBuffer(GLES30.GL_ELEMENT_ARRAY_BUFFER,a[0]);ShortBuffer ib=ByteBuffer.allocateDirect(idx.length*2).order(ByteOrder.nativeOrder()).asShortBuffer();ib.put(idx).position(0);GLES30.glBufferData(GLES30.GL_ELEMENT_ARRAY_BUFFER,idx.length*2,ib,GLES30.GL_STATIC_DRAW);int st=6*4;GLES30.glEnableVertexAttribArray(0);GLES30.glVertexAttribPointer(0,3,GLES30.GL_FLOAT,false,st,0);GLES30.glEnableVertexAttribArray(1);GLES30.glVertexAttribPointer(1,3,GLES30.GL_FLOAT,false,st,12);GLES30.glBindVertexArray(0);count=idx.length;}
        void draw(){GLES30.glBindVertexArray(vao);GLES30.glDrawElements(GLES30.GL_TRIANGLES,count,GLES30.GL_UNSIGNED_SHORT,0);}
    }
    private static Mesh cube(){float[] p={-1,-1,1,0,0,1,1,-1,1,0,0,1,1,1,1,0,0,1,-1,1,1,0,0,1,1,-1,-1,0,0,-1,-1,-1,-1,0,0,-1,-1,1,-1,0,0,-1,1,1,-1,0,0,-1,-1,-1,-1,-1,0,0,-1,-1,1,-1,0,0,-1,1,1,-1,0,0,-1,1,-1,-1,0,0,1,-1,1,1,0,0,1,-1,-1,1,0,0,1,1,-1,1,0,0,1,1,1,1,0,0,1,-1,1,1,0,1,0,1,1,1,0,1,0,1,1,-1,0,1,0,-1,1,-1,0,1,0,-1,-1,-1,0,-1,0,1,-1,-1,0,-1,0,1,-1,1,0,-1,0,-1,-1,1,0,-1,0};short[] q=new short[36];int k=0;for(short f=0;f<6;f++){short b=(short)(f*4);q[k++]=b;q[k++]=(short)(b+1);q[k++]=(short)(b+2);q[k++]=b;q[k++]=(short)(b+2);q[k++]=(short)(b+3);}return new Mesh(p,q);}
    private static Mesh plane(){return new Mesh(new float[]{-1,0,-1,0,1,0,1,0,-1,0,1,0,1,0,1,0,1,0,1,0,-1,0,1,0},new short[]{0,1,2,0,2,3});}
    private static Mesh sphere(int st,int sl){ArrayList<Float>v=new ArrayList<>();ArrayList<Short>q=new ArrayList<>();for(int y=0;y<=st;y++){double ph=Math.PI*y/st;for(int x=0;x<=sl;x++){double th=2*Math.PI*x/sl;float a=(float)(Math.sin(ph)*Math.cos(th)),b=(float)Math.cos(ph),c=(float)(Math.sin(ph)*Math.sin(th));Collections.addAll(v,a,b,c,a,b,c);}}for(int y=0;y<st;y++)for(int x=0;x<sl;x++){short a=(short)(y*(sl+1)+x),b=(short)(a+sl+1);Collections.addAll(q,a,b,(short)(a+1),b,(short)(b+1),(short)(a+1));}float[]va=new float[v.size()];for(int i=0;i<va.length;i++)va[i]=v.get(i);short[]ia=new short[q.size()];for(int i=0;i<ia.length;i++)ia[i]=q.get(i);return new Mesh(va,ia);}
}
