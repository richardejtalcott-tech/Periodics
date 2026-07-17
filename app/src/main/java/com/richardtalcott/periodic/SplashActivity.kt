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
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.*

class SplashActivity : ComponentActivity() {
 private val handler=Handler(Looper.getMainLooper())
 override fun onCreate(state:Bundle?){super.onCreate(state);setContent{ClassicAtomSplash()};handler.postDelayed({startActivity(Intent(this,MainActivity::class.java));finish()},3200)}
 override fun onDestroy(){handler.removeCallbacksAndMessages(null);super.onDestroy()}
}
@Composable private fun ClassicAtomSplash(){
 val rotation by rememberInfiniteTransition(label="atom").animateFloat(0f,360f,infiniteRepeatable(tween(5200,easing=LinearEasing)),label="rotation")
 Box(Modifier.fillMaxSize().background(Brush.radialGradient(listOf(Color(0xFF0A2B46),Color(0xFF01050D)),radius=1000f)),contentAlignment=Alignment.Center){
  Canvas(Modifier.fillMaxSize()){
   repeat(100){i->drawCircle(Color.White.copy(.15f+(i%4)*.08f),1f+(i%3),Offset(((i*83)%997)/997f*size.width,((i*137)%991)/991f*size.height))}
   val c=Offset(center.x,center.y-size.height*.08f);val atom=min(size.width,size.height)*.23f
   drawCircle(Brush.radialGradient(listOf(Color(0xFFBFEAFF),Color(0xFF174A78),Color.Transparent),c,atom*1.65f),atom*1.65f,c)
   repeat(16){i->val a=i*2.399963;val rr=sqrt((i+.5)/16.0).toFloat()*atom*.32f;val p=Offset(c.x+cos(a).toFloat()*rr,c.y+sin(a).toFloat()*rr);drawCircle(if(i%2==0)Color(0xFFFF4E79) else Color(0xFF579DFF),atom*.075f,p)}
   val angles=floatArrayOf(-24f,55f,92f)
   angles.forEachIndexed{orbit,tilt->rotate(tilt,c){drawOval(if(orbit==0)Color(0xFF57CFFF) else if(orbit==1)Color(0xFFFF4F7B) else Color.White,topLeft=Offset(c.x-atom,c.y-atom*.38f),size=androidx.compose.ui.geometry.Size(atom*2,atom*.76f),style=Stroke(4f))}}
   repeat(8){i->val orbit=i%3;val a=Math.toRadians((rotation*(if(orbit==1)-1 else 1)+i*137).toDouble());val x=cos(a).toFloat()*atom;val y=sin(a).toFloat()*atom*.38f;val rad=Math.toRadians(angles[orbit].toDouble());val p=Offset(c.x+x*cos(rad).toFloat()-y*sin(rad).toFloat(),c.y+x*sin(rad).toFloat()+y*cos(rad).toFloat());drawCircle(Color(0xFF70E4FF),11f,p);drawCircle(Color.White,4f,p)}
  }
  Column(Modifier.align(Alignment.BottomCenter).padding(bottom=42.dp),horizontalAlignment=Alignment.CenterHorizontally){
   Text("PERIODIC",fontSize=54.sp,fontWeight=FontWeight.Black,color=Color.White)
   Text("EXPLORE MATTER • FROM ELEMENTS TO QUARKS",fontSize=16.sp,color=Color(0xFF77D5FF))
   Text("OXYGEN • 8 PROTONS • 8 NEUTRONS • 8 ELECTRONS",fontSize=10.sp,color=Color(0xFF7894A8))
  }
 }
}
