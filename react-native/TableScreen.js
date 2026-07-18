import React from 'react';
import {Pressable,ScrollView,Text,View} from 'react-native';
import {FAMILY_COLORS,NAMES,SYMBOLS,family,position} from './data';
import s from './styles';

const TILE_W=68,TILE_H=72,GAP=7,LEFT=32,TOP=34;
const TABLE_W=LEFT*2+18*(TILE_W+GAP);
const TABLE_H=TOP*2+9*(TILE_H+GAP);

function ElementTile({number,open}){
 const i=number-1,pos=position(number),kind=family(number),accent=FAMILY_COLORS[kind];
 const left=LEFT+(pos.group-1)*(TILE_W+GAP),top=TOP+(pos.period-1)*(TILE_H+GAP);
 return <Pressable onPress={()=>open(number)} style={[s.tile,{left,top,width:TILE_W,height:TILE_H,borderColor:accent,shadowColor:accent}]}>
  <View style={[s.tileGlow,{backgroundColor:accent}]}/><View style={s.tileGlass}/>
  <Text style={s.num}>{number}</Text><Text style={s.sym}>{SYMBOLS[i]}</Text><Text numberOfLines={1} style={s.name}>{NAMES[i]}</Text>
 </Pressable>;
}

export default function TableScreen({open}){
 return <View style={s.screen}>
  <View style={s.labCeiling}><View style={s.ceilingRing}/></View>
  <View style={s.header}>
   <View><Text style={s.title}>PERIODIC</Text><Text style={s.sub}>THE INTERACTIVE ELEMENT LABORATORY</Text></View>
   <View style={{alignItems:'flex-end'}}><Text style={s.elementCount}>118 ELEMENTS</Text><Text style={s.sub}>TAP A 3D ELEMENT TILE TO EXPLORE</Text></View>
  </View>
  <ScrollView horizontal showsHorizontalScrollIndicator={false} contentContainerStyle={s.tableScroll}>
   <View style={[s.tableStage,{width:TABLE_W,height:TABLE_H}]}>
    <View style={s.museumGlow}/><View style={s.labFloor}/>
    {SYMBOLS.map((_,i)=><ElementTile key={i+1} number={i+1} open={open}/>)}
    <Text style={[s.seriesLabel,{left:LEFT,top:TOP+7*(TILE_H+GAP)+26}]}>LANTHANIDES</Text>
    <Text style={[s.seriesLabel,{left:LEFT,top:TOP+8*(TILE_H+GAP)+26}]}>ACTINIDES</Text>
   </View>
  </ScrollView>
  <View style={s.controlDock}><Text style={s.dockItem}>✋  DRAG</Text><Text style={s.dockDivider}>│</Text><Text style={s.dockItem}>⌖  PAN</Text><Text style={s.dockDivider}>│</Text><Text style={s.dockItem}>⌕  ZOOM</Text><Text style={s.dockDivider}>│</Text><Text style={s.dockItem}>↻  RESET</Text></View>
 </View>;
}