package com.richardtalcott.periodic

import android.os.Bundle
import android.view.WindowInsets
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.*

class MainActivity : ComponentActivity() {
  override fun onCreate(state: Bundle?) { super.onCreate(state); window.insetsController?.hide(WindowInsets.Type.systemBars()); setContent { Studio() } }
}
private val Ink=Color(0xFF020711); private val Panel=Color(0xD9071827); private val Cyan=Color(0xFF52D7FF)
private val Gold=Color(0xFFFFC768); private val Red=Color(0xFFFF557F); private val Blue=Color(0xFF639EFF)
private enum class Page(val title:String){TABLE("Periodic Table"),INFO("Element Information"),ATOM("Atom Explorer"),ISOTOPE("Isotope Lab"),ION("Ion Builder"),BOND("Bond Builder"),STATES("States of Matter Lab"),COMPARE("Compare Elements")}
private data class Element(val n:Int,val symbol:String,val name:String,val row:Int,val col:Int,val mass:Int,val kind:Int)
private val symbols="H He Li Be B C N O F Ne Na Mg Al Si P S Cl Ar K Ca Sc Ti V Cr Mn Fe Co Ni Cu Zn Ga Ge As Se Br Kr Rb Sr Y Zr Nb Mo Tc Ru Rh Pd Ag Cd In Sn Sb Te I Xe Cs Ba La Ce Pr Nd Pm Sm Eu Gd Tb Dy Ho Er Tm Yb Lu Hf Ta W Re Os Ir Pt Au Hg Tl Pb Bi Po At Rn Fr Ra Ac Th Pa U Np Pu Am Cm Bk Cf Es Fm Md No Lr Rf Db Sg Bh Hs Mt Ds Rg Cn Nh Fl Mc Lv Ts Og".split(" ")
private val names=mapOf(1 to "Hydrogen",2 to "Helium",3 to "Lithium",6 to "Carbon",7 to "Nitrogen",8 to "Oxygen",26 to "Iron",29 to "Copper",35 to "Bromine",47 to "Silver",79 to "Gold",92 to "Uranium")
private fun pos(n:Int)=when{n==1->1 to 1;n==2->1 to 18;n<=10->2 to listOf(1,2,13,14,15,16,17,18)[n-3];n<=18->3 to listOf(1,2,13,14,15,16,17,18)[n-11];n<=36->4 to n-18;n<=54->5 to n-36;n==55->6 to 1;n==56->6 to 2;n in 57..71->8 to n-54;n<=86->6 to n-68;n==87->7 to 1;n==88->7 to 2;n in 89..103->9 to n-86;else->7 to n-100}
private fun all()=(1..118).map{n->val p=pos(n);Element(n,symbols[n-1],names[n]?:"Element $n",p.first,p.second,when(n){1->1;2->4;26->56;29->64;35->80;79->197;92->238;else->max(n,(n*2.45).roundToInt())},when{n in 57..71->5;n in 89..103->6;p.second==18->4;p.second==17->3;p.second in 3..12->2;else->1})}

@Composable private fun Studio(){var page by remember{mutableStateOf(Page.TABLE)};var selected by remember{mutableStateOf(all()[25])};var compare by remember{mutableStateOf(all()[28])};MaterialTheme(colorScheme=darkColorScheme(primary=Cyan,background=Ink,surface=Panel)){Box(Modifier.fillMaxSize().background(Brush.radialGradient(listOf(Color(0xFF0B3652),Ink),radius=1300f))){Stars();AnimatedContent(page,label="page"){p->when(p){Page.TABLE->Table{selected=it;page=Page.INFO};Page.INFO->Info(selected,{page=Page.TABLE}){page=it};Page.ATOM->Lab(p,selected,{page=Page.INFO},body={Atom(selected.n)});Page.ISOTOPE->Isotope(selected){page=Page.INFO};Page.ION->Ion(selected){page=Page.INFO};Page.BOND->Lab(p,selected,{page=Page.INFO},body={Molecule()});Page.STATES->States{page=Page.TABLE};Page.COMPARE->Compare(selected,compare,{compare=it}){page=Page.TABLE}}}}}}
@Composable private fun Stars(){Canvas(Modifier.fillMaxSize()){repeat(90){i->drawCircle(Color.White.copy(.12f+(i%5)*.05f),1f+(i%3),Offset(((i*83)%997)/997f*size.width,((i*137)%991)/991f*size.height))}}}
@Composable private fun Header(title:String,sub:String,back:(()->Unit)?=null){Row(Modifier.fillMaxWidth().padding(18.dp),verticalAlignment=Alignment.CenterVertically){if(back!=null)OutlinedButton(back){Text("‹ Back")};Column(Modifier.padding(start=16.dp)){Text(title.uppercase(),fontSize=26.sp,fontWeight=FontWeight.Black);Text(sub,fontSize=12.sp,color=Cyan)}}}
private fun color(k:Int)=listOf(Color.Gray,Color(0xFF27A88B),Color(0xFF2C9EC8),Color(0xFF9B4AA5),Color(0xFF386FC0),Color(0xFF8B55C7),Color(0xFFC34B87))[k]
@Composable private fun Table(open:(Element)->Unit){Column(Modifier.fillMaxSize()){Header("PERIODIC 4","STRAIGHT-ON ELEMENT MATRIX • TAP AN ELEMENT");BoxWithConstraints(Modifier.fillMaxSize().padding(18.dp)){val w=maxWidth/18;val h=maxHeight/9;all().forEach{e->Box(Modifier.offset(w*(e.col-1),h*(e.row-1)).size(w,h).padding(2.dp).background(Brush.verticalGradient(listOf(color(e.kind),color(e.kind).copy(.42f))),RoundedCornerShape(6.dp)).border(1.dp,Color.White.copy(.2f),RoundedCornerShape(6.dp)).clickable{open(e)},contentAlignment=Alignment.Center){Column(horizontalAlignment=Alignment.CenterHorizontally){Text("${e.n}",fontSize=7.sp,color=Color.White.copy(.65f));Text(e.symbol,fontSize=14.sp,fontWeight=FontWeight.Black)}}}}}}
@Composable private fun Glass(mod:Modifier=Modifier,body:@Composable ColumnScope.()->Unit){Column(mod.background(Panel,RoundedCornerShape(18.dp)).border(1.dp,Cyan.copy(.25f),RoundedCornerShape(18.dp)).padding(20.dp),horizontalAlignment=Alignment.CenterHorizontally,verticalArrangement=Arrangement.SpaceEvenly,content=body)}
@Composable private fun Info(e:Element,back:()->Unit,go:(Page)->Unit){Column(Modifier.fillMaxSize()){Header("${e.name} (${e.symbol})","ATOMIC NUMBER ${e.n}",back);Row(Modifier.fillMaxSize().padding(24.dp),horizontalArrangement=Arrangement.spacedBy(24.dp)){Glass(Modifier.weight(1f).fillMaxHeight()){Text(e.symbol,fontSize=96.sp,fontWeight=FontWeight.Black);Text(e.name,fontSize=28.sp);Text("Atomic number  ${e.n}\nRepresentative mass number  ${e.mass}\nProtons  ${e.n}\nNeutral electrons  ${e.n}\nRepresentative neutrons  ${e.mass-e.n}",lineHeight=28.sp)};Glass(Modifier.weight(1.5f).fillMaxHeight()){Atom(e.n);Row(horizontalArrangement=Arrangement.spacedBy(6.dp)){Page.entries.drop(2).forEach{OutlinedButton({go(it)}){Text(it.title,fontSize=9.sp)}}}}}}}
@Composable private fun Lab(p:Page,e:Element,back:()->Unit,body:@Composable()->Unit){Column(Modifier.fillMaxSize()){Header(p.title,"${e.name} • INTERACTIVE SCIENCE LAB",back);Glass(Modifier.fillMaxSize().padding(24.dp)){body()}}}
@Composable private fun Atom(n:Int){val t by rememberInfiniteTransition(label="atom").animateFloat(0f,360f,infiniteRepeatable(tween(7000,easing=LinearEasing)),label="orbit");Canvas(Modifier.fillMaxSize().padding(16.dp)){val c=center;val r=min(size.width,size.height)*.12f;repeat(min(n,36)){i->val a=i*2.4;drawCircle(if(i%2==0)Red else Blue,r*.15f,Offset(c.x+cos(a).toFloat()*r*.55f,c.y+sin(a).toFloat()*r*.55f))};val shells=when{n<=2->1;n<=10->2;n<=18->3;n<=36->4;else->5};repeat(shells){s->val rr=r*(1.5f+s*.65f);drawCircle(Cyan.copy(.35f),rr,c,style=Stroke(2f));repeat(max(1,min(n,18)/shells)){i->val a=Math.toRadians((t*(1+s*.12f)+i*360f/max(1,min(n,18)/shells)).toDouble());drawCircle(Cyan,7f,Offset(c.x+cos(a).toFloat()*rr,c.y+sin(a).toFloat()*rr))}}}}
@Composable private fun Isotope(e:Element,back:()->Unit){var m by remember{mutableFloatStateOf(e.mass.toFloat())};Column(Modifier.fillMaxSize()){Header("Isotope Lab",e.name,back);Glass(Modifier.fillMaxSize().padding(24.dp)){Text("${e.symbol}-${m.roundToInt()}",fontSize=46.sp,color=Gold);Atom(e.n);Text("Protons ${e.n} • Neutrons ${m.roundToInt()-e.n} • Mass number ${m.roundToInt()}");Slider(m,{m=it},max(e.n,e.mass-8).toFloat()..(e.mass+8).toFloat(),15)}}}
@Composable private fun Ion(e:Element,back:()->Unit){var q by remember{mutableFloatStateOf(0f)};Column(Modifier.fillMaxSize()){Header("Ion Builder",e.name,back);Glass(Modifier.fillMaxSize().padding(24.dp)){Text("${e.symbol}  charge ${q.roundToInt()}",fontSize=46.sp,color=Cyan);Atom((e.n-q.roundToInt()).coerceAtLeast(0));Text("Protons ${e.n} • Electrons ${e.n-q.roundToInt()} • Net charge ${q.roundToInt()}");Slider(q,{q=it.roundToInt().toFloat()},-3f..3f,5)}}}
@Composable private fun Molecule(){Canvas(Modifier.fillMaxSize()){val c=center;val l=Offset(c.x-size.width*.17f,c.y+size.height*.18f);val r=Offset(c.x+size.width*.17f,c.y+size.height*.18f);drawLine(Color.LightGray,c,l,18f);drawLine(Color.LightGray,c,r,18f);drawCircle(Red,size.minDimension*.1f,c);drawCircle(Color.White,size.minDimension*.065f,l);drawCircle(Color.White,size.minDimension*.065f,r)};Text("H₂O • bent geometry • 104.5° • polar covalent",color=Gold)}
@Composable private fun States(back:()->Unit){var temp by remember{mutableFloatStateOf(25f)};val phase=if(temp<0)"SOLID" else if(temp<100)"LIQUID" else "GAS";Column(Modifier.fillMaxSize()){Header("States of Matter Lab","WATER AT 1 ATM • PHASE BOUNDARIES",back);Glass(Modifier.fillMaxSize().padding(24.dp)){Text(phase,fontSize=42.sp,color=if(phase=="SOLID")Blue else if(phase=="LIQUID")Cyan else Gold);Particles(phase);Text("${temp.roundToInt()} °C at 1 atm");Slider(temp,{temp=it},-50f..150f);Text("At 1 atm: melting point 0 °C • boiling point 100 °C")}}}
@Composable private fun Particles(phase:String){val t by rememberInfiniteTransition(label="matter").animateFloat(0f,1f,infiniteRepeatable(tween(if(phase=="GAS")700 else 1800),RepeatMode.Reverse),label="motion");Canvas(Modifier.fillMaxWidth().height(200.dp)){repeat(42){i->val move=if(phase=="SOLID")3f else if(phase=="LIQUID")15f else 55f;drawCircle(if(i%3==0)Red else Blue,8f,Offset(((i*37)%100)/100f*size.width+sin(i+t*6)*move,((i*61)%100)/100f*size.height+cos(i*2+t*5)*move))}}}
@Composable private fun Compare(a:Element,b:Element,set:(Element)->Unit,back:()->Unit){Column(Modifier.fillMaxSize()){Header("Compare Elements","SIDE-BY-SIDE SCIENTIFIC PROPERTIES",back);Row(Modifier.weight(1f).padding(20.dp),horizontalArrangement=Arrangement.spacedBy(16.dp)){listOf(a,b).forEach{e->Glass(Modifier.weight(1f).fillMaxHeight()){Text(e.symbol,fontSize=70.sp,fontWeight=FontWeight.Black);Text(e.name,fontSize=24.sp);Text("Atomic number ${e.n}\nMass number ${e.mass}\nProtons ${e.n}\nNeutral electrons ${e.n}\nRepresentative neutrons ${e.mass-e.n}",lineHeight=26.sp)}}};Row(Modifier.fillMaxWidth().padding(12.dp),horizontalArrangement=Arrangement.Center){all().filter{it.n in listOf(6,8,26,29,47,79)}.forEach{OutlinedButton({set(it)}){Text(it.symbol)}}}}}
