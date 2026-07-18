import React,{useEffect,useRef} from 'react';
import {Animated,Text,View} from 'react-native';
import s from './styles';

export default function SplashScreen({done}){
 const pulse=useRef(new Animated.Value(.72)).current;
 const spin=useRef(new Animated.Value(0)).current;
 useEffect(()=>{
  Animated.loop(Animated.sequence([Animated.timing(pulse,{toValue:1,duration:1200,useNativeDriver:true}),Animated.timing(pulse,{toValue:.72,duration:1200,useNativeDriver:true})])).start();
  Animated.loop(Animated.timing(spin,{toValue:1,duration:7000,useNativeDriver:true})).start();
  const t=setTimeout(done,2600);return()=>clearTimeout(t);
 },[done,pulse,spin]);
 const rotation=spin.interpolate({inputRange:[0,1],outputRange:['0deg','360deg']});
 return <View style={s.splash}>
  <View style={s.starField}>{Array.from({length:34},(_,i)=><View key={i} style={[s.star,{left:`${(i*29)%97}%`,top:`${(i*47)%91}%`,opacity:.25+(i%5)*.14}]}/>)}</View>
  <Animated.View style={[s.splashGlow,{transform:[{scale:pulse}]}]}/>
  <View style={s.splashAtom}>
   {[0,1,2].map((r)=><Animated.View key={r} style={[s.splashOrbit,{transform:[{rotate:`${r*58}deg`},{rotateZ:rotation}]}]}><View style={[s.splashElectron,{backgroundColor:r===1?'#ff4d78':r===2?'#f5f7ff':'#5ed7ff'}]}/></Animated.View>)}
   <View style={s.splashNucleus}>{Array.from({length:16},(_,i)=><View key={i} style={[s.splashNucleon,{backgroundColor:i%2?'#2f83ff':'#ff4169'}]}/>)}</View>
  </View>
  <Text style={s.splashTitle}>PERIODIC</Text>
  <Text style={s.splashSub}>EXPLORE MATTER • FROM ELEMENTS TO QUARKS</Text>
  <Text style={s.splashMeta}>OXYGEN • 8 PROTONS • 8 NEUTRONS • 8 ELECTRONS</Text>
 </View>;
}