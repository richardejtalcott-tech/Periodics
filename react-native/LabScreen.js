import React,{useMemo,useState} from 'react';
import {Pressable,Text,View} from 'react-native';
import AtomView from './AtomView';
import {MASS_NUMBERS,NAMES,SYMBOLS,bonds,shells} from './data';

const C={bg:'#03101d',panel:'#071a2beF',line:'#2d79a5',cyan:'#32c8ff',gold:'#ffad37',white:'#f4fbff',muted:'#9ec3da'};
const panel={backgroundColor:C.panel,borderWidth:1,borderColor:C.line,borderRadius:18,padding:18};
const button={borderWidth:1,borderColor:C.cyan,borderRadius:10,paddingHorizontal:18,paddingVertical:10,backgroundColor:'#092b46'};
const title={color:C.white,fontSize:34,fontWeight:'900',textAlign:'center'};
const sub={color:C.cyan,fontSize:15,letterSpacing:1.5,textAlign:'center',marginTop:3};
const label={color:C.muted,fontSize:14,marginTop:10};
const value={color:C.white,fontSize:19,fontWeight:'800'};

export default function LabScreen({mode,number}){
 const i=number-1,name=NAMES[i],symbol=SYMBOLS[i],baseMass=MASS_NUMBERS[i];
 const [mass,setMass]=useState(baseMass),[electrons,setElectrons]=useState(number),[temp,setTemp]=useState(70);
 const neutron=Math.max(0,mass-number),charge=number-electrons;
 const phase=temp<100?'SOLID':temp<2500?'LIQUID':temp<6000?'GAS':'PLASMA';
 const heading=mode==='ISOTOPES'?'ISOTOPE LAB':mode==='IONS'?'ION BUILDER':mode==='BONDS'?'BOND BUILDER':'STATES OF MATTER LAB';
 const subtitle=mode==='ISOTOPES'?`${symbol}-${mass} • ${number} PROTONS • ${neutron} NEUTRONS`:mode==='IONS'?`${electrons} ELECTRONS • NET CHARGE ${charge>0?'+':''}${charge}`:mode==='BONDS'?`${name.toUpperCase()} • COMMON MOLECULAR STRUCTURES`:`${name.toUpperCase()} AT 1 ATM • PHASE AND PARTICLE MOTION`;
 return <View style={{flex:1,backgroundColor:C.bg,padding:18}}>
  <Text style={title}>{heading}</Text><Text style={sub}>{subtitle}</Text>
  <View style={{flex:1,flexDirection:'row',gap:18,marginTop:16}}>
   <View style={[panel,{width:'29%'}]}>
    <Text style={{color:C.white,fontSize:42,fontWeight:'900'}}>{mode==='ISOTOPES'?`${symbol}-${mass}`:mode==='IONS'?`${symbol}${charge===0?'':charge>0?`${charge}+`:`${Math.abs(charge)}−`}`:symbol}</Text>
    {mode==='ISOTOPES'&&<><Text style={label}>MASS NUMBER</Text><Text style={value}>{mass}</Text><View style={{flexDirection:'row',gap:12,marginTop:18}}><Pressable style={button} onPress={()=>setMass(Math.max(number,mass-1))}><Text style={value}>−</Text></Pressable><Pressable style={button} onPress={()=>setMass(mass+1)}><Text style={value}>+</Text></Pressable></View><Text style={label}>Protons: {number}</Text><Text style={label}>Neutrons: {neutron}</Text></>}
    {mode==='IONS'&&<><Text style={label}>PROTONS</Text><Text style={value}>{number}</Text><Text style={label}>ELECTRONS</Text><Text style={value}>{electrons}</Text><View style={{flexDirection:'row',gap:12,marginTop:18}}><Pressable style={button} onPress={()=>setElectrons(Math.max(0,electrons-1))}><Text style={value}>REMOVE e⁻</Text></Pressable><Pressable style={button} onPress={()=>setElectrons(electrons+1)}><Text style={value}>ADD e⁻</Text></Pressable></View></>}
    {mode==='BONDS'&&<><Text style={label}>COMMON BONDS</Text><Text style={[value,{color:C.gold}]}>{bonds(symbol)}</Text><Text style={label}>The final builder will restrict choices to chemically plausible structures for the selected element.</Text></>}
    {mode==='MATTER'&&<><Text style={label}>TEMPERATURE</Text><Text style={[value,{fontSize:38}]}>{temp} °F</Text><View style={{flexDirection:'row',gap:12,marginTop:18}}><Pressable style={button} onPress={()=>setTemp(Math.max(-400,temp-100))}><Text style={value}>−100</Text></Pressable><Pressable style={button} onPress={()=>setTemp(temp+100)}><Text style={value}>+100</Text></Pressable></View><Text style={label}>CURRENT STATE</Text><Text style={[value,{color:C.gold}]}>{phase}</Text></>}
   </View>
   <View style={[panel,{flex:1,alignItems:'center',justifyContent:'center'}]}><View style={{position:'absolute',width:380,height:380,borderRadius:190,backgroundColor:'#07508c',opacity:.22}}/><AtomView number={mode==='IONS'?electrons:number} shells={shells(mode==='IONS'?electrons:number)}/><Text style={{color:C.cyan,fontSize:14,marginTop:8}}>INTERACTIVE ILLUSTRATIVE MODEL • NOT TO SCALE</Text></View>
   <View style={[panel,{width:'27%'}]}><Text style={{color:C.cyan,fontSize:18,fontWeight:'900'}}>LIVE DATA</Text><Text style={label}>Element</Text><Text style={value}>{name} ({symbol})</Text><Text style={label}>Atomic number</Text><Text style={value}>{number}</Text>{mode==='ISOTOPES'&&<><Text style={label}>Mass number</Text><Text style={value}>{mass}</Text><Text style={label}>Stability</Text><Text style={[value,{color:Math.abs(mass-baseMass)<=2?'#65e46f':C.gold}]}>{Math.abs(mass-baseMass)<=2?'LIKELY STABLE RANGE':'UNSTABLE / RARE'}</Text></>}{mode==='IONS'&&<><Text style={label}>Charge accounting</Text><Text style={value}>+{number} − {electrons} = {charge>0?'+':''}{charge}</Text><Text style={label}>Ion type</Text><Text style={[value,{color:C.gold}]}>{charge===0?'NEUTRAL':charge>0?'CATION':'ANION'}</Text></>}{mode==='BONDS'&&<><Text style={label}>Selected library</Text><Text style={value}>{bonds(symbol)}</Text><Text style={label}>Geometry and polarity panels will populate from the selected molecule.</Text></>}{mode==='MATTER'&&<><Text style={label}>Phase</Text><Text style={[value,{color:C.gold}]}>{phase}</Text><Text style={label}>Particle behavior</Text><Text style={value}>{phase==='SOLID'?'Ordered; vibrates in place':phase==='LIQUID'?'Close together; flows':phase==='GAS'?'Far apart; rapid motion':'Ionized high-energy matter'}</Text></>}</View>
  </View>
 </View>;
}
