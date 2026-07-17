package com.richardtalcott.periodic

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.compose.BackHandler
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
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

class MainActivity : ComponentActivity() {
  override fun onCreate(state: Bundle?) {
    super.onCreate(state)
    WindowCompat.setDecorFitsSystemWindows(window,false)
    WindowInsetsControllerCompat(window,window.decorView).apply {
      hide(WindowInsetsCompat.Type.systemBars())
      systemBarsBehavior=WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
    }
    setContent { Studio() }
  }
}
private val Ink=Color(0xFF020711); private val Panel=Color(0xD9071827); private val Cyan=Color(0xFF52D7FF)
private val Gold=Color(0xFFFFC768); private val Red=Color(0xFFFF557F); private val Blue=Color(0xFF639EFF)
private enum class Page(val title:String){TABLE("Periodic Table"),INFO("Element Information"),ATOM("Atom Explorer"),ISOTOPE("Isotope Lab"),ION("Ion Builder"),BOND("Bond Builder"),STATES("States of Matter Lab"),COMPARE("Compare Elements")}
private data class Element(val n:Int,val symbol:String,val name:String,val row:Int,val col:Int,val mass:Int,val kind:Int)
private val symbols="H He Li Be B C N O F Ne Na Mg Al Si P S Cl Ar K Ca Sc Ti V Cr Mn Fe Co Ni Cu Zn Ga Ge As Se Br Kr Rb Sr Y Zr Nb Mo Tc Ru Rh Pd Ag Cd In Sn Sb Te I Xe Cs Ba La Ce Pr Nd Pm Sm Eu Gd Tb Dy Ho Er Tm Yb Lu Hf Ta W Re Os Ir Pt Au Hg Tl Pb Bi Po At Rn Fr Ra Ac Th Pa U Np Pu Am Cm Bk Cf Es Fm Md No Lr Rf Db Sg Bh Hs Mt Ds Rg Cn Nh Fl Mc Lv Ts Og".split(" ")
private fun pos(n:Int)=when{n==1->1 to 1;n==2->1 to 18;n<=10->2 to listOf(1,2,13,14,15,16,17,18)[n-3];n<=18->3 to listOf(1,2,13,14,15,16,17,18)[n-11];n<=36->4 to n-18;n<=54->5 to n-36;n==55->6 to 1;n==56->6 to 2;n in 57..71->8 to n-54;n<=86->6 to n-68;n==87->7 to 1;n==88->7 to 2;n in 89..103->9 to n-86;else->7 to n-100}
private fun all()=(1..118).map{n->val p=pos(n);Element(n,symbols[n-1],ELEMENT_NAMES[n-1],p.first,p.second,REPRESENTATIVE_MASS_NUMBERS[n-1],when{n in 57..71->5;n in 89..103->6;p.second==18->4;p.second==17->3;p.second in 3..12->2;else->1})}

@Composable private fun Studio(){var page by remember{mutableStateOf(Page.TABLE)};var selected by remember{mutableStateOf(all()[25])};var compare by remember{mutableStateOf(all()[28])};BackHandler(enabled=page!=Page.TABLE){page=if(page==Page.INFO)Page.TABLE else Page.INFO};MaterialTheme(colorScheme=darkColorScheme(primary=Cyan,background=Ink,surface=Panel,onBackground=Color.White,onSurface=Color.White)){CompositionLocalProvider(LocalContentColor provides Color.White){Box(Modifier.fillMaxSize().safeDrawingPadding().background(Ink)){ReactorBackdrop();Stars();AnimatedContent(page,label="page"){p->when(p){Page.TABLE->Table{selected=it;compare=all().first{x->x.n!=it.n};page=Page.INFO};Page.INFO->Info(selected,{page=Page.TABLE}){page=it};Page.ATOM->AtomExplorer(selected){page=Page.INFO};Page.ISOTOPE->Isotope(selected){page=Page.INFO};Page.ION->Ion(selected){page=Page.INFO};Page.BOND->Bond{page=Page.INFO};Page.STATES->States(selected){page=Page.INFO};Page.COMPARE->Compare(selected,compare,{compare=it}){page=Page.INFO}}}}}}}
@Composable private fun Stars(){Canvas(Modifier.fillMaxSize()){repeat(90){i->drawCircle(Color.White.copy(.12f+(i%5)*.05f),1f+(i%3),Offset(((i*83)%997)/997f*size.width,((i*137)%991)/991f*size.height))}}}
@Composable private fun ReactorBackdrop(){Canvas(Modifier.fillMaxSize()){drawRect(Brush.verticalGradient(listOf(Color(0xFF03101A),Color(0xFF071D2D),Ink)));val horizon=size.height*.72f;repeat(12){i->val x=size.width*i/11f;drawLine(Cyan.copy(.09f),Offset(size.width/2,horizon),Offset(x,size.height),1f)};repeat(5){i->drawOval(Cyan.copy(.08f),topLeft=Offset(-size.width*.1f,horizon+i*38f),size=androidx.compose.ui.geometry.Size(size.width*1.2f,70f+i*20f),style=Stroke(2f))};repeat(9){i->val x=size.width*i/8f;drawRect(Brush.horizontalGradient(listOf(Color.Transparent,Cyan.copy(.08f),Color.Transparent)),Offset(x-35f,0f),androidx.compose.ui.geometry.Size(70f,horizon))};drawLine(Cyan.copy(.18f),Offset(0f,horizon),Offset(size.width,horizon),3f)}}
@Composable private fun Header(title:String,sub:String,back:(()->Unit)?=null){Row(Modifier.fillMaxWidth().padding(18.dp),verticalAlignment=Alignment.CenterVertically){if(back!=null)OutlinedButton(back){Text("‹ Back")};Column(Modifier.padding(start=16.dp)){Text(title.uppercase(),fontSize=26.sp,fontWeight=FontWeight.Black);Text(sub,fontSize=12.sp,color=Cyan)}}}
private fun color(k:Int)=listOf(Color.Gray,Color(0xFF27A88B),Color(0xFF2C9EC8),Color(0xFF9B4AA5),Color(0xFF386FC0),Color(0xFF8B55C7),Color(0xFFC34B87))[k]
@Composable private fun Table(open:(Element)->Unit){Column(Modifier.fillMaxSize()){
 Row(Modifier.fillMaxWidth().padding(start=28.dp,top=13.dp,end=28.dp,bottom=4.dp),verticalAlignment=Alignment.CenterVertically){Column{Text("PERIODIC",fontSize=30.sp,fontWeight=FontWeight.Black,letterSpacing=2.sp);Text("THE INTERACTIVE ELEMENT LABORATORY",fontSize=11.sp,color=Cyan)};Spacer(Modifier.weight(1f));Column(horizontalAlignment=Alignment.End){Text("118 ELEMENTS",fontSize=12.sp,fontWeight=FontWeight.Bold);Text("TAP A 3D ELEMENT TILE TO EXPLORE",fontSize=8.sp,color=Cyan)}}
 BoxWithConstraints(Modifier.fillMaxSize().padding(horizontal=28.dp,vertical=7.dp)){val w=maxWidth/18;val h=maxHeight/9;all().forEach{e->Box(Modifier.offset(w*(e.col-1),h*(e.row-1)).size(w,h).clickable{open(e)}){val shape=RoundedCornerShape(7.dp);Box(Modifier.fillMaxSize().padding(start=5.dp,top=6.dp,end=1.dp,bottom=1.dp).background(Color.Black.copy(.72f),shape).border(1.dp,color(e.kind).copy(.35f),shape));Box(Modifier.fillMaxSize().padding(start=2.dp,top=2.dp,end=4.dp,bottom=5.dp).background(Brush.verticalGradient(listOf(Color.White.copy(.30f),color(e.kind),color(e.kind).copy(.65f),Color(0xFF03101A))),shape).border(1.dp,Color.White.copy(.42f),shape)){Text("${e.n}",fontSize=6.sp,color=Color.White.copy(.80f),modifier=Modifier.align(Alignment.TopStart).padding(start=4.dp,top=2.dp));Column(Modifier.align(Alignment.Center),horizontalAlignment=Alignment.CenterHorizontally){Text(e.symbol,fontSize=15.sp,fontWeight=FontWeight.Black,color=Color.White);Text(e.name.uppercase(),fontSize=4.sp,color=Color.White.copy(.82f),maxLines=1)};Box(Modifier.fillMaxWidth().height(1.dp).align(Alignment.TopCenter).background(Color.White.copy(.35f)))}}}}
}}
@Composable private fun Glass(mod:Modifier=Modifier,body:@Composable ColumnScope.()->Unit){Column(mod.background(Brush.verticalGradient(listOf(Color(0xED0A2133),Color(0xF0061725),Color(0xF0020A12))),RoundedCornerShape(18.dp)).border(1.dp,Cyan.copy(.42f),RoundedCornerShape(18.dp)).padding(20.dp),horizontalAlignment=Alignment.CenterHorizontally,verticalArrangement=Arrangement.SpaceEvenly,content=body)}
@Composable private fun Metric(label:String,value:String,tint:Color=Cyan){Row(Modifier.fillMaxWidth().padding(vertical=3.dp),horizontalArrangement=Arrangement.SpaceBetween){Text(label.uppercase(),fontSize=9.sp,color=Color.White.copy(.55f));Text(value,fontSize=11.sp,fontWeight=FontWeight.SemiBold,color=tint)}}
private fun kelvin(raw:String):Float?=raw.toFloatOrNull()
private fun fahrenheit(raw:String):String=kelvin(raw)?.let{"${(((it-273.15f)*9f/5f)+32f).roundToInt()} °F"}?:"Not established"
@Composable private fun Info(e:Element,back:()->Unit,go:(Page)->Unit){
 val p=ELEMENT_PROPERTIES[e.n-1]
 Column(Modifier.fillMaxSize()){Header("${e.name} (${e.symbol})","ATOMIC NUMBER ${e.n} • ${p.category.uppercase()}",back)
  Row(Modifier.fillMaxSize().padding(24.dp),horizontalArrangement=Arrangement.spacedBy(24.dp)){
   Glass(Modifier.weight(1f).fillMaxHeight()){
    Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceBetween){Text("${e.n}",fontSize=18.sp,color=Cyan);Text(p.category.uppercase(),fontSize=11.sp,color=Gold)}
    Text(e.symbol,fontSize=82.sp,fontWeight=FontWeight.Black,color=Color.White);Text(e.name,fontSize=27.sp,color=Color.White)
    HorizontalDivider(color=Cyan.copy(.25f))
    Text("ATOMIC MASS   ${p.atomicMass} u\nDENSITY   ${p.density} g/cm³\nMELTING POINT   ${fahrenheit(p.meltingPoint)}\nBOILING / SUBLIMATION   ${fahrenheit(p.boilingPoint)}\nELECTRONEGATIVITY   ${p.electronegativity}\nOXIDATION STATES   ${p.oxidationStates}\nELECTRON CONFIGURATION   ${p.configuration}",fontSize=12.sp,lineHeight=17.sp,color=Color.White)
   }
   Glass(Modifier.weight(1.5f).fillMaxHeight()){Box(Modifier.weight(1f).fillMaxWidth()){Atom(e.n);Text("INTERACTIVE ATOM MODEL",Modifier.align(Alignment.TopCenter),fontSize=11.sp,color=Cyan)};Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.spacedBy(5.dp)){listOf("ATOM","ISOTOPES","ION","BONDS","MATTER","COMPARE").zip(Page.entries.drop(2)).forEach{(label,target)->OutlinedButton({go(target)},Modifier.weight(1f),contentPadding=PaddingValues(horizontal=3.dp)){Text(label,fontSize=7.sp,color=Color.White,maxLines=1)}}}}
  }
 }
}
@Composable private fun Lab(p:Page,e:Element,back:()->Unit,body:@Composable ()->Unit){Column(Modifier.fillMaxSize()){Header(p.title,"${e.name} • INTERACTIVE SCIENCE LAB",back);Glass(Modifier.fillMaxSize().padding(24.dp)){body()}}}
private val nobleCore=mapOf("He" to 2,"Ne" to 10,"Ar" to 18,"Kr" to 36,"Xe" to 54,"Rn" to 86)
private fun shellCounts(z:Int):List<Int>{
 if(z<=0)return emptyList()
 if(z>118){val base=shellCounts(118).toMutableList();base[base.lastIndex]+=z-118;return base}
 val counts=IntArray(7);val configuration=ELEMENT_PROPERTIES[z-1].configuration
 Regex("\\[([A-Z][a-z]?)\\]").find(configuration)?.groupValues?.get(1)?.let{core->nobleCore[core]?.let{coreZ->shellCounts(coreZ).forEachIndexed{i,v->counts[i]+=v}}}
 Regex("([1-7])[spdf](\\d+)").findAll(configuration.substringAfter(']',configuration)).forEach{m->counts[m.groupValues[1].toInt()-1]+=m.groupValues[2].toInt()}
 return counts.toList().dropLastWhile{it==0}
}
@Composable
private fun ScientificAtom(
 protons:Int,
 neutrons:Int,
 electrons:Int,
 cloud:Boolean=false,
 running:Boolean=true
){
 val t by rememberInfiniteTransition(label="atom3d").animateFloat(
  0f,360f,infiniteRepeatable(tween(9500,easing=LinearEasing)),label="orbit"
 )
 Canvas(Modifier.fillMaxSize().padding(8.dp)){
  val c=center
  val base=min(size.width,size.height)*.115f
  val frame=if(running)t else 32f
  drawCircle(Brush.radialGradient(listOf(Cyan.copy(.22f),Color.Transparent),c,base*4.7f),base*4.7f,c)
  val visibleNucleons=min((protons+neutrons).coerceAtLeast(1),90)
  repeat(visibleNucleons){i->
   val a=i*2.399963f
   val layer=sqrt((i+.5f)/visibleNucleons.toFloat())
   val d=base*.72f*layer
   val p=Offset(c.x+cos(a)*d,c.y+sin(a)*d*.78f)
   val tint=if(i<protons)Red else Blue
   drawCircle(Brush.radialGradient(listOf(Color.White,tint,tint.copy(.45f)),Offset(p.x-base*.05f,p.y-base*.06f),base*.22f),base*.18f,p)
  }
  if(electrons>0){
   shellCounts(electrons).forEachIndexed{s,count->
    val rx=base*(1.75f+s*.66f)
    val ry=rx*(.54f+(s%2)*.08f)
    val tiltDegrees=if(s%2==0)-12f+s*7f else 18f-s*4f
    if(!cloud){
     rotate(tiltDegrees,c){drawOval(Cyan.copy(.38f),Offset(c.x-rx,c.y-ry),androidx.compose.ui.geometry.Size(rx*2,ry*2),style=Stroke(2.2f))}
     repeat(count){i->
      val orbitalAngle=Math.toRadians((frame*(if(s%2==0)1 else -1)*(1+s*.035f)+i*360f/count).toDouble())
      val tilt=Math.toRadians(tiltDegrees.toDouble())
      val x=cos(orbitalAngle).toFloat()*rx
      val y=sin(orbitalAngle).toFloat()*ry
      val p=Offset(c.x+x*cos(tilt).toFloat()-y*sin(tilt).toFloat(),c.y+x*sin(tilt).toFloat()+y*cos(tilt).toFloat())
      drawCircle(Cyan.copy(.18f),base*.16f,p)
      drawCircle(Brush.radialGradient(listOf(Color.White,Cyan),p,base*.10f),base*.085f,p)
     }
    }else{
     repeat(140){i->
      val a=i*2.399963f+s*.7f
      val radial=rx*(.65f+((i*47)%35)/100f)
      val p=Offset(c.x+cos(a)*radial,c.y+sin(a)*radial*.62f)
      drawCircle(Cyan.copy(.035f+(i%5)*.012f),1.5f+(i%3),p)
     }
    }
   }
  }
 }
}
@Composable private fun Atom(n:Int){ScientificAtom(n,(REPRESENTATIVE_MASS_NUMBERS.getOrElse(n-1){n}-n).coerceAtLeast(0),n)}
@Composable private fun AtomExplorer(e:Element,back:()->Unit){var cloud by remember{mutableStateOf(false)};var running by remember{mutableStateOf(true)};Column(Modifier.fillMaxSize()){Header("${e.name} (${e.symbol})","ATOM EXPLORER • INTERACTIVE STRUCTURE AND ELECTRON ENERGY LEVELS",back);Row(Modifier.fillMaxSize().padding(14.dp),horizontalArrangement=Arrangement.spacedBy(12.dp)){Glass(Modifier.width(185.dp).fillMaxHeight()){Text(e.symbol,fontSize=62.sp,fontWeight=FontWeight.Black);Metric("Atomic number","${e.n}");Metric("Protons","${e.n}",Red);Metric("Neutrons","${e.mass-e.n}",Blue);Metric("Electrons","${e.n}");Metric("Mass number","${e.mass}",Gold);Text("SHELL POPULATION",fontSize=9.sp,color=Cyan);Text(shellCounts(e.n).joinToString(" • "),fontSize=14.sp,fontWeight=FontWeight.Bold)};Glass(Modifier.weight(1f).fillMaxHeight()){Box(Modifier.weight(1f).fillMaxWidth()){ScientificAtom(e.n,e.mass-e.n,e.n,cloud,running);Text(if(cloud)"ELECTRON PROBABILITY CLOUD" else "3D SHELL MODEL",Modifier.align(Alignment.TopCenter),fontSize=11.sp,color=Cyan)};Row(horizontalArrangement=Arrangement.spacedBy(7.dp)){if(!cloud)Button({cloud=false}){Text("Shells")}else OutlinedButton({cloud=false}){Text("Shells")};if(cloud)Button({cloud=true}){Text("Cloud")}else OutlinedButton({cloud=true}){Text("Cloud")};OutlinedButton({running=!running}){Text(if(running)"Pause" else "Resume")}}};Glass(Modifier.width(205.dp).fillMaxHeight()){Text("SCIENTIFIC MODEL",fontSize=12.sp,color=Cyan);Text("Nucleus and particles are enlarged. Shells represent principal energy levels; the cloud view better represents electron probability.",fontSize=10.sp,lineHeight=15.sp);HorizontalDivider(color=Cyan.copy(.2f));Metric("Configuration",ELEMENT_PROPERTIES[e.n-1].configuration);Metric("Neutral charge","0");Metric("Model scale","illustrative",Gold)}}}}
@Composable private fun Isotope(e:Element,back:()->Unit){var m by remember{mutableFloatStateOf(e.mass.toFloat())};val lower=max(e.n,e.mass-4);val upper=e.mass+4;val mass=m.roundToInt().coerceIn(lower,upper);val neutrons=mass-e.n;Column(Modifier.fillMaxSize()){Header("Isotope Lab","${e.name} • CHANGE NEUTRONS AND INSPECT THE NUCLEUS",back);Row(Modifier.fillMaxSize().padding(14.dp),horizontalArrangement=Arrangement.spacedBy(12.dp)){Glass(Modifier.width(205.dp).fillMaxHeight()){Text("ISOTOPE CONTROLS",fontSize=11.sp,color=Cyan);Text("${e.symbol}-$mass",fontSize=44.sp,color=Gold);Metric("Protons","${e.n}",Red);Metric("Neutrons","$neutrons",Blue);Metric("Mass number","$mass");Text("ADJUST NEUTRONS",fontSize=9.sp,color=Color.White.copy(.65f));Slider(value=mass.toFloat(),onValueChange={m=it.roundToInt().toFloat()},valueRange=lower.toFloat()..upper.toFloat(),steps=(upper-lower-1).coerceAtLeast(0))};Glass(Modifier.weight(1f).fillMaxHeight()){Box(Modifier.weight(1f).fillMaxWidth()){ScientificAtom(e.n,neutrons,e.n);Text("NUCLEUS • ${e.n} PROTON${if(e.n==1)"" else "S"} • $neutrons NEUTRON${if(neutrons==1)"" else "S"}",Modifier.align(Alignment.TopCenter),color=Cyan,fontSize=10.sp)};Text("Atomic number stays ${e.n}; changing neutrons creates a different isotope of ${e.name}.",fontSize=10.sp,color=Color.White.copy(.75f))};Glass(Modifier.width(215.dp).fillMaxHeight()){Text("NUCLIDE DATA",fontSize=11.sp,color=Cyan);Metric("Nuclide","${e.symbol}-$mass");Metric("Atomic number","${e.n}");Metric("Mass number","$mass");Metric("Neutron number","$neutrons");Metric("N/Z ratio",if(e.n>0)"%.2f".format(neutrons.toFloat()/e.n) else "—",Gold);Text("Nuclear stability requires evaluated nuclide data; this visualizer does not assume stability from neutron count alone.",fontSize=9.sp,lineHeight=14.sp,color=Gold)}}}}
private fun ionMark(charge:Int):String{if(charge==0)return "";val digits=abs(charge).toString().map{mapOf('0' to '⁰','1' to '¹','2' to '²','3' to '³','4' to '⁴','5' to '⁵','6' to '⁶','7' to '⁷','8' to '⁸','9' to '⁹')[it]}.joinToString("");return (if(abs(charge)==1)"" else digits)+if(charge>0)"⁺" else "⁻"}
@Composable private fun Ion(e:Element,back:()->Unit){var q by remember{mutableFloatStateOf(0f)};val maximumPositive=min(3,e.n);val charge=q.roundToInt().coerceIn(-3,maximumPositive);val electrons=e.n-charge;val ion="${e.symbol}${ionMark(charge)}";Column(Modifier.fillMaxSize()){Header("${e.name} ($ion)","ION BUILDER • ELECTRON TRANSFER AND NET CHARGE",back);Row(Modifier.fillMaxSize().padding(14.dp),horizontalArrangement=Arrangement.spacedBy(12.dp)){Glass(Modifier.width(205.dp).fillMaxHeight()){Text("ION CONTROLS",fontSize=11.sp,color=Cyan);Text(ion,fontSize=50.sp,color=Cyan);Metric("Net charge",if(charge>0)"+$charge" else "$charge",Gold);Metric("Protons","${e.n}",Red);Metric("Electrons","$electrons",Cyan);Text("REMOVE e⁻     ←     →     ADD e⁻",fontSize=8.sp);Slider(value=charge.toFloat(),onValueChange={q=it.roundToInt().coerceIn(-3,maximumPositive).toFloat()},valueRange=-3f..maximumPositive.toFloat(),steps=(maximumPositive+2).coerceAtLeast(0))};Glass(Modifier.weight(1f).fillMaxHeight()){Box(Modifier.weight(1f).fillMaxWidth()){ScientificAtom(e.n,e.mass-e.n,electrons);Text("$electrons ELECTRON${if(electrons==1)"" else "S"} • ${shellCounts(electrons).joinToString(" • ")}",Modifier.align(Alignment.TopCenter),fontSize=10.sp,color=Cyan)};Text(if(charge>0)"${e.symbol} → $ion + ${charge}e⁻" else if(charge<0)"${e.symbol} + ${-charge}e⁻ → $ion" else "${e.symbol}: neutral atom • protons = electrons",fontSize=13.sp,color=Gold)};Glass(Modifier.width(205.dp).fillMaxHeight()){Text("CHARGE ACCOUNTING",fontSize=11.sp,color=Cyan);Metric("Nuclear charge","+${e.n}");Metric("Electron charge","−$electrons");Metric("Net charge",if(charge>0)"+$charge" else "$charge",Gold);Metric("Ion type",if(charge>0)"cation" else if(charge<0)"anion" else "neutral");Text("Ion formation changes electrons, not the nucleus or elemental identity.",fontSize=9.sp,lineHeight=14.sp)}}}}
private data class MoleculeSpec(val formula:String,val name:String,val center:String,val outer:String,val count:Int,val geometry:String,val electronGeometry:String,val angle:String,val polarity:String,val lewis:String,val note:String)
private val molecules=listOf(
 MoleculeSpec("H₂O","Water","O","H",2,"bent","tetrahedral","104.5°","polar","H—O—H • 2 lone pairs","Two bonding pairs and two lone pairs repel into a bent shape."),
 MoleculeSpec("CO₂","Carbon dioxide","C","O",2,"linear","linear","180°","nonpolar","O=C=O","Two polar C=O bonds cancel because the molecule is symmetric."),
 MoleculeSpec("NH₃","Ammonia","N","H",3,"trigonal pyramidal","tetrahedral","107°","polar","3 N—H bonds • 1 lone pair","One lone pair compresses the H—N—H bond angle below 109.5°."),
 MoleculeSpec("CH₄","Methane","C","H",4,"tetrahedral","tetrahedral","109.5°","nonpolar","4 C—H single bonds","Four bonding pairs arrange tetrahedrally and cancel bond dipoles.")
)
private fun atomColor(symbol:String)=when(symbol){"O"->Red;"H"->Color(0xFFE8F5FF);"C"->Color(0xFF394758);"N"->Blue;else->Gold}
@Composable private fun Molecule(spec:MoleculeSpec,rotation:Float,labels:Boolean,density:Boolean){Canvas(Modifier.fillMaxSize()){val c=center;val radius=min(size.width,size.height)*.31f;val points=when(spec.count){2->if(spec.geometry=="linear")listOf(Offset(c.x-radius,c.y),Offset(c.x+radius,c.y))else listOf(Offset(c.x-radius*.72f,c.y+radius*.65f),Offset(c.x+radius*.72f,c.y+radius*.65f));3->listOf(Offset(c.x,c.y-radius),Offset(c.x-radius*.82f,c.y+radius*.55f),Offset(c.x+radius*.82f,c.y+radius*.55f));else->listOf(Offset(c.x,c.y-radius),Offset(c.x-radius*.86f,c.y+radius*.36f),Offset(c.x+radius*.86f,c.y+radius*.36f),Offset(c.x,c.y+radius*.78f))};if(density)drawCircle(Brush.radialGradient(listOf(Cyan.copy(.25f),Color.Transparent),c,radius*1.65f),radius*1.65f,c);points.forEachIndexed{i,p0->val a=Math.toRadians(rotation.toDouble());val dx=p0.x-c.x;val dy=p0.y-c.y;val p=Offset(c.x+dx*cos(a).toFloat()-dy*sin(a).toFloat(),c.y+dx*sin(a).toFloat()+dy*cos(a).toFloat());drawLine(Color(0xFFB8CAD7),c,p,if(spec.formula=="CO₂")14f else 11f);drawLine(Color.White.copy(.45f),Offset(c.x,c.y-3f),Offset(p.x,p.y-3f),3f);drawCircle(Brush.radialGradient(listOf(Color.White,atomColor(spec.outer),atomColor(spec.outer).copy(.45f)),Offset(p.x-8f,p.y-10f),34f),28f,p);if(labels)drawContext.canvas.nativeCanvas.drawText(spec.outer,p.x-6f,p.y+7f,android.graphics.Paint().apply{color=android.graphics.Color.BLACK;textSize=18f;isFakeBoldText=true})};drawCircle(Brush.radialGradient(listOf(Color.White,atomColor(spec.center),atomColor(spec.center).copy(.38f)),Offset(c.x-10f,c.y-12f),48f),42f,c);if(labels)drawContext.canvas.nativeCanvas.drawText(spec.center,c.x-9f,c.y+8f,android.graphics.Paint().apply{color=android.graphics.Color.WHITE;textSize=24f;isFakeBoldText=true})}}
@Composable private fun Bond(back:()->Unit){var selected by remember{mutableIntStateOf(0)};var rotation by remember{mutableFloatStateOf(0f)};var labels by remember{mutableStateOf(true)};var density by remember{mutableStateOf(true)};val spec=molecules[selected];Column(Modifier.fillMaxSize()){Header("Bond Builder","INTERACTIVE MOLECULAR GEOMETRY • VSEPR • POLARITY",back);Row(Modifier.fillMaxSize().padding(14.dp),horizontalArrangement=Arrangement.spacedBy(12.dp)){Glass(Modifier.width(190.dp).fillMaxHeight()){Text("MOLECULE LIBRARY",fontSize=11.sp,color=Cyan);molecules.forEachIndexed{i,m->if(i==selected)Button({selected=i;rotation=0f},Modifier.fillMaxWidth()){Text("${m.formula}  ${m.name}",fontSize=9.sp)}else OutlinedButton({selected=i;rotation=0f},Modifier.fillMaxWidth()){Text("${m.formula}  ${m.name}",fontSize=9.sp)}};Text("ROTATE MODEL",fontSize=8.sp,color=Color.White.copy(.6f));Slider(value=rotation,onValueChange={rotation=it},valueRange=0f..360f)};Glass(Modifier.weight(1f).fillMaxHeight()){Box(Modifier.weight(1f).fillMaxWidth()){Molecule(spec,rotation,labels,density);Column(Modifier.align(Alignment.TopCenter),horizontalAlignment=Alignment.CenterHorizontally){Text("${spec.name.uppercase()} • ${spec.formula}",fontSize=12.sp,color=Cyan);Text(spec.angle,fontSize=10.sp,color=Gold)}};Row(horizontalArrangement=Arrangement.spacedBy(7.dp)){OutlinedButton({labels=!labels}){Text(if(labels)"Hide labels" else "Show labels",fontSize=9.sp)};OutlinedButton({density=!density}){Text(if(density)"Hide cloud" else "Electron cloud",fontSize=9.sp)}};Text(spec.lewis,fontSize=13.sp,color=Color.White)};Glass(Modifier.width(215.dp).fillMaxHeight()){Text("MOLECULE DATA",fontSize=11.sp,color=Cyan);Metric("Formula",spec.formula);Metric("Bond type","covalent");Metric("Molecular geometry",spec.geometry);Metric("Electron geometry",spec.electronGeometry);Metric("Bond angle",spec.angle);Metric("Polarity",spec.polarity,if(spec.polarity=="polar")Gold else Cyan);Text("VSEPR EXPLANATION",fontSize=8.sp,color=Cyan);Text(spec.note,fontSize=9.sp,lineHeight=14.sp,color=Color.White.copy(.82f))}}}}
@Composable private fun States(e:Element,back:()->Unit){
 val p=ELEMENT_PROPERTIES[e.n-1]
 val mpK=kelvin(p.meltingPoint);val bpK=kelvin(p.boilingPoint)
 val mpF=mpK?.let{(it-273.15f)*9f/5f+32f};val bpF=bpK?.let{(it-273.15f)*9f/5f+32f}
 val sublimes=mpK!=null&&bpK!=null&&bpK<mpK
 val low=(listOfNotNull(mpF,bpF).minOrNull()?.minus(250f)?:-100f).coerceAtLeast(-459f)
 val high=(listOfNotNull(mpF,bpF).maxOrNull()?.plus(250f)?:500f).coerceAtLeast(low+100f)
 var temp by remember(e.n){mutableFloatStateOf(77f.coerceIn(low,high))}
 val phase=when{mpK==null&&bpK==null->"DATA LIMITED";sublimes->if(temp<(bpF?:0f))"SOLID" else "GAS";mpF!=null&&temp<mpF->"SOLID";bpF!=null&&temp<bpF->"LIQUID";else->"GAS"}
 val tint=when(phase){"SOLID"->Blue;"LIQUID"->Cyan;"GAS"->Gold;else->Color.LightGray}
 Column(Modifier.fillMaxSize()){
  Header("States of Matter Lab","${e.name.uppercase()} (${e.symbol}) • PHASE BEHAVIOR AT 1 ATM",back)
  Row(Modifier.fillMaxSize().padding(14.dp),horizontalArrangement=Arrangement.spacedBy(12.dp)){
   Glass(Modifier.width(205.dp).fillMaxHeight()){
    Text("TEMPERATURE",fontSize=10.sp,color=Cyan);Text("${temp.roundToInt()} °F",fontSize=34.sp,color=tint)
    Slider(value=temp,onValueChange={temp=it},valueRange=low..high)
    Metric("Pressure","1.00 atm");Metric("State",phase,tint)
    Metric(if(sublimes)"Sublimation point" else "Melting point",if(sublimes)fahrenheit(p.boilingPoint) else fahrenheit(p.meltingPoint))
    if(!sublimes)Metric("Boiling point",fahrenheit(p.boilingPoint))
   }
   Glass(Modifier.weight(1f).fillMaxHeight()){
    Text(phase,fontSize=36.sp,color=tint);ElementParticles(phase,e)
    Text(when(phase){"SOLID"->"Particles occupy an ordered, closely packed structure.";"LIQUID"->"Particles remain close while moving past one another.";"GAS"->"Particles are widely separated with rapid random motion.";else->"Experimental phase-transition data are not established."},fontSize=9.sp,color=Gold)
   }
   Glass(Modifier.width(210.dp).fillMaxHeight()){
    Text("ELEMENT PHASE DATA",fontSize=10.sp,color=Cyan);Metric("Element","${e.name} (${e.symbol})");Metric("Atomic number","${e.n}");Metric("Atomic mass","${p.atomicMass} u");Metric("Density","${p.density} g/cm³");Metric("Category",p.category)
    Text(if(sublimes)"At 1 atm this element sublimes; the listed higher-pressure melting value is not used as an ordinary liquid boundary." else "Phase boundaries are displayed in Fahrenheit; verified source values remain stored internally in Kelvin.",fontSize=8.sp,lineHeight=13.sp,color=Color.White.copy(.72f))
   }
  }
 }
}
@Composable private fun ElementParticles(phase:String,e:Element){
 val t by rememberInfiniteTransition(label="matter").animateFloat(0f,1f,infiniteRepeatable(tween(if(phase=="GAS")700 else 1700,easing=LinearEasing),RepeatMode.Restart),label="motion")
 val diatomic=e.n in setOf(1,7,8,9,17,35,53)
 Canvas(Modifier.fillMaxWidth().height(215.dp)){
  drawRoundRect(Cyan.copy(.16f),style=Stroke(3f),cornerRadius=androidx.compose.ui.geometry.CornerRadius(18f))
  repeat(36){i->
   val base=when(phase){"SOLID"->Offset(size.width*(.10f+(i%9)*.10f),size.height*(.18f+(i/9)*.20f));"LIQUID"->Offset(size.width*(.07f+((i*47)%88)/100f),size.height*(.45f+((i*29)%44)/100f));else->Offset(size.width*(.06f+((i*37)%89)/100f),size.height*(.08f+((i*61)%84)/100f))}
   val motion=when(phase){"SOLID"->2.5f;"LIQUID"->8f;else->22f}
   val pos=Offset(base.x+sin(i*1.7f+t*6.28f)*motion,base.y+cos(i*2.1f+t*5.4f)*motion)
   val radius=if(phase=="GAS")5.5f else 7f;val tint=color(e.kind)
   drawCircle(Brush.radialGradient(listOf(Color.White,tint,tint.copy(.45f)),Offset(pos.x-2f,pos.y-2f),radius*2),radius,pos)
   if(diatomic&&i%2==0){val mate=Offset(pos.x+radius*1.7f,pos.y+radius*.55f);drawLine(tint,pos,mate,3f);drawCircle(Brush.radialGradient(listOf(Color.White,tint),mate,radius*2),radius,mate)}
  }
 }
}
@Composable private fun CompareRow(label:String,left:String,right:String){Row(Modifier.fillMaxWidth().height(28.dp).border(0.5.dp,Cyan.copy(.18f)),verticalAlignment=Alignment.CenterVertically){Text(label.uppercase(),Modifier.width(125.dp).padding(start=8.dp),fontSize=8.sp,color=Color.White.copy(.55f));Text(left,Modifier.weight(1f),fontSize=10.sp,color=Cyan,maxLines=1);Text(right,Modifier.weight(1f),fontSize=10.sp,color=Gold,maxLines=1)}}
@Composable private fun Compare(a:Element,b:Element,set:(Element)->Unit,back:()->Unit){val pa=ELEMENT_PROPERTIES[a.n-1];val pb=ELEMENT_PROPERTIES[b.n-1];Column(Modifier.fillMaxSize()){Header("Compare Elements","COMPLETE SIDE-BY-SIDE SCIENTIFIC PROFILE",back);Glass(Modifier.weight(1f).fillMaxWidth().padding(horizontal=18.dp)){Row(Modifier.fillMaxWidth().height(74.dp),verticalAlignment=Alignment.CenterVertically){Spacer(Modifier.width(125.dp));Row(Modifier.weight(1f),verticalAlignment=Alignment.CenterVertically){Text(a.symbol,fontSize=43.sp,fontWeight=FontWeight.Black);Column(Modifier.padding(start=10.dp)){Text(a.name,fontSize=17.sp);Text(pa.category.uppercase(),fontSize=8.sp,color=Cyan)}};Row(Modifier.weight(1f),verticalAlignment=Alignment.CenterVertically){Text(b.symbol,fontSize=43.sp,fontWeight=FontWeight.Black,color=Gold);Column(Modifier.padding(start=10.dp)){Text(b.name,fontSize=17.sp);Text(pb.category.uppercase(),fontSize=8.sp,color=Gold)}}};CompareRow("Atomic number","${a.n}","${b.n}");CompareRow("Atomic mass","${pa.atomicMass} u","${pb.atomicMass} u");CompareRow("Electron configuration",pa.configuration,pb.configuration);CompareRow("Electronegativity",pa.electronegativity,pb.electronegativity);CompareRow("Density","${pa.density} g/cm³","${pb.density} g/cm³");CompareRow("Melting point",fahrenheit(pa.meltingPoint),fahrenheit(pb.meltingPoint));CompareRow("Boiling / sublimation",fahrenheit(pa.boilingPoint),fahrenheit(pb.boilingPoint));CompareRow("Oxidation states",pa.oxidationStates,pb.oxidationStates)};Row(Modifier.fillMaxWidth().padding(8.dp),horizontalArrangement=Arrangement.Center){Text("COMPARE ${a.symbol} WITH",Modifier.align(Alignment.CenterVertically).padding(end=8.dp),fontSize=8.sp,color=Cyan);all().filter{it.n in listOf(6,8,13,26,29,47,79,92)}.forEach{if(it.n==b.n)Button({set(it)}){Text(it.symbol)}else OutlinedButton({set(it)}){Text(it.symbol,color=Color.White)}}}}}
