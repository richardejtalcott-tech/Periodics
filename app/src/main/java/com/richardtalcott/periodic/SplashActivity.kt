package com.richardtalcott.periodic

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp\nimport androidx.compose.ui.unit.sp
import kotlin.math.*

class SplashActivity : ComponentActivity() {
  private val handler=Handler(Looper.getMainLooper())
  override fun onCreate(state:Bundle?){
    super.onCreate(state)
    setContent { OxygenSplash() }
    handler.postDelayed({startActivity(Intent(this,MainActivity::class.java));finish()},2800)
  }
  override fun onDestroy(){handler.removeCallbacksAndMessages(null);super.onDestroy()}
}

@Composable private fun OxygenSplash(){
 val rotation by rememberInfiniteTransition(label="oxygen").animateFloat(
   0f,360f,infiniteRepeatable(tween(4200,easing=LinearEasing)),label="rotation")
 Box(Modifier.fillMaxSize().background(Brush.radialGradient(listOf(Color(0xFF0B3857),Color(0xFF020611)),radius=1200f)),contentAlignment=Alignment.Center){
  Canvas(Modifier.fillMaxSize()){
   val c=center;val base=min(size.width,size.height)*.17f
   repeat(3){ring->
    val radius=base*(1.18f+ring*.34f)
    drawOval(Color(0xFF54D7FF).copy(alpha=.58f),topLeft=Offset(c.x-radius,c.y-radius*.44f),size=androidx.compose.ui.geometry.Size(radius*2,radius*.88f),style=Stroke(3f))
   }
   repeat(16){i->
    val a=i*2.399963
    val rr=sqrt((i+.5)/16.0).toFloat()*base*.72f
    drawCircle(if(i%2==0)Color(0xFFFF4E79) else Color(0xFF639EFF),base*.105f,Offset(c.x+cos(a).toFloat()*rr,c.y+sin(a).toFloat()*rr))
   }
   val radii=floatArrayOf(base*1.18f,base*1.52f,base*1.86f)
   val counts=intArrayOf(2,4,2)
   var electron=0
   repeat(3){ring->repeat(counts[ring]){i->
    val a=Math.toRadians((rotation*(1f+ring*.12f)+i*360f/counts[ring]+electron*17).toDouble())
    drawCircle(Color(0xFF68E2FF),9f,Offset(c.x+cos(a).toFloat()*radii[ring],c.y+sin(a).toFloat()*radii[ring]*.44f))
    electron++
   }}
  }
  Column(Modifier.align(Alignment.BottomCenter).padding(bottom=54.dp),horizontalAlignment=Alignment.CenterHorizontally){
   Text("PERIODIC",fontSize=54.sp,fontWeight=FontWeight.Black,color=Color.White)
   Text("EXPLORE MATTER • FROM ELEMENTS TO QUARKS",fontSize=16.sp,color=Color(0xFF77D5FF))
   Text("OXYGEN • 8 PROTONS • 8 NEUTRONS • 8 ELECTRONS",fontSize=11.sp,color=Color(0xFF7894A8))
  }
 }
}
