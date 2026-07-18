import React,{useEffect,useMemo,useRef} from 'react';
import {Animated,StyleSheet,View} from 'react-native';

export default function AtomView({number,shells=[]}){
 const spin=useRef(new Animated.Value(0)).current;
 useEffect(()=>{spin.setValue(0);const loop=Animated.loop(Animated.timing(spin,{toValue:1,duration:7200,useNativeDriver:true}));loop.start();return()=>loop.stop();},[number,spin]);
 const rot=spin.interpolate({inputRange:[0,1],outputRange:['0deg','360deg']});
 const nucleusCount=Math.min(26,Math.max(6,Math.round(Math.sqrt(number)*4)));
 const shellData=useMemo(()=>shells.map((count,index)=>({count,index})),[shells]);
 return <View style={s.stage}>
  <Animated.View style={[s.nucleus,{transform:[{rotate:rot}]}]}>{Array.from({length:nucleusCount},(_,i)=><View key={i} style={[s.dot,{backgroundColor:i%2?'#697fff':'#ff4a73'}]}/>)}</Animated.View>
  {shellData.map(({count,index})=>{
   const width=122+index*31,height=46+index*16,visible=Math.min(count,10);
   return <Animated.View key={index} style={[s.orbit,{width,height,transform:[{rotate:`${index*37-58}deg`},{rotateZ:rot}]}]}>
    {Array.from({length:visible},(_,i)=>{
      const angle=(360/visible)*i;
      return <View key={i} style={[s.electronAnchor,{transform:[{rotate:`${angle}deg`}]}]}><View style={[s.electron,{transform:[{translateX:width/2-4},{rotate:`-${angle}deg`}]}]}/></View>;
    })}
   </Animated.View>;
  })}
 </View>;
}

const s=StyleSheet.create({
 stage:{width:360,height:250,alignItems:'center',justifyContent:'center',marginVertical:8},
 nucleus:{width:48,height:48,borderRadius:24,flexDirection:'row',flexWrap:'wrap',alignItems:'center',justifyContent:'center',backgroundColor:'#101a35',zIndex:5,elevation:7,shadowColor:'#ff577c',shadowOpacity:.8,shadowRadius:12},
 dot:{width:9,height:9,borderRadius:5,margin:-1},
 orbit:{position:'absolute',borderWidth:1,borderColor:'#64cfff99',borderRadius:999},
 electronAnchor:{position:'absolute',left:'50%',top:'50%',width:1,height:1},
 electron:{width:8,height:8,borderRadius:4,backgroundColor:'#7de6ff',shadowColor:'#7de6ff',shadowOpacity:1,shadowRadius:7,elevation:5}
});
