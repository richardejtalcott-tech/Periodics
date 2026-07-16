package com.richardtalcott.periodic;
import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
public final class ElementDetailActivity extends Activity {
    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(new ElementDetailView(this, ElementData.byNumber(getIntent().getIntExtra("atomicNumber", 8))));
    }
}
