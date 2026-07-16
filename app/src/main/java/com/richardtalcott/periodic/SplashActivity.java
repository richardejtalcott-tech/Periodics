package com.richardtalcott.periodic;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Window;
import android.view.WindowManager;
public final class SplashActivity extends Activity {
    private final Handler handler = new Handler(Looper.getMainLooper());
    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(new AtomSplashView(this));
        handler.postDelayed(() -> { startActivity(new Intent(this, MainActivity.class)); finish(); }, 2800);
    }
    @Override protected void onDestroy() { handler.removeCallbacksAndMessages(null); super.onDestroy(); }
}
