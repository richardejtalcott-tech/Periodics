package com.richardtalcott.periodic;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.*;
import android.widget.FrameLayout;

public final class MainActivity extends Activity {
    private True3DView gl;
    @Override public void onCreate(Bundle b){
        super.onCreate(b);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY|View.SYSTEM_UI_FLAG_FULLSCREEN|View.SYSTEM_UI_FLAG_HIDE_NAVIGATION|View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION|View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        FrameLayout root=new FrameLayout(this);
        gl=new True3DView(this,false,0);
        TableHudView hud=new TableHudView(this,gl.renderer());
        gl.setTapListener(n->{Intent i=new Intent(this,ElementDetailActivity.class);i.putExtra("atomicNumber",n);startActivity(i);});
        root.addView(gl,new FrameLayout.LayoutParams(-1,-1));
        root.addView(hud,new FrameLayout.LayoutParams(-1,-1));
        setContentView(root);
    }
    @Override protected void onPause(){super.onPause();gl.onPause();}
    @Override protected void onResume(){super.onResume();gl.onResume();}
}
