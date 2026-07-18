import React from 'react';
import {Pressable,ScrollView,Text,View} from 'react-native';
import {FAMILY_COLORS,NAMES,SYMBOLS,family,position} from './data';
import s from './styles';

const TILE_W=68,TILE_H=72,GAP=7,LEFT=24,TOP=24;
const TABLE_W=LEFT*2+18*(TILE_W+GAP);
const TABLE_H=TOP*2+9*(TILE_H+GAP);

function ElementTile({number,open}){
  const index=number-1,pos=position(number),kind=family(number),accent=FAMILY_COLORS[kind];
  const left=LEFT+(pos.group-1)*(TILE_W+GAP);
  const top=TOP+(pos.period-1)*(TILE_H+GAP);
  return <Pressable accessibilityRole="button" accessibilityLabel={`${NAMES[index]}, atomic number ${number}`} onPress={()=>open(number)} style={[s.tile,{left,top,width:TILE_W,height:TILE_H,borderColor:accent,shadowColor:accent}]}>
    <View style={[s.tileGlow,{backgroundColor:accent}]}/>
    <Text style={s.num}>{number}</Text>
    <Text style={s.sym}>{SYMBOLS[index]}</Text>
    <Text numberOfLines={1} style={s.name}>{NAMES[index]}</Text>
  </Pressable>;
}

export default function TableScreen({open}){
  return <View style={s.screen}>
    <View style={s.header}>
      <View><Text style={s.title}>PERIODIC</Text><Text style={s.sub}>EXPLORE THE BUILDING BLOCKS OF MATTER</Text></View>
      <View style={s.headerBadge}><Text style={s.headerBadgeTop}>118</Text><Text style={s.headerBadgeBottom}>ELEMENTS</Text></View>
    </View>
    <ScrollView horizontal showsHorizontalScrollIndicator={false} contentContainerStyle={s.tableScroll}>
      <View style={[s.tableStage,{width:TABLE_W,height:TABLE_H}]}>
        <View style={s.museumGlow}/>
        {SYMBOLS.map((_,i)=><ElementTile key={i+1} number={i+1} open={open}/>)}
        <Text style={[s.seriesLabel,{left:LEFT,top:TOP+7*(TILE_H+GAP)+24}]}>LANTHANIDES</Text>
        <Text style={[s.seriesLabel,{left:LEFT,top:TOP+8*(TILE_H+GAP)+24}]}>ACTINIDES</Text>
      </View>
    </ScrollView>
    <View style={s.footerBar}><Text style={s.hint}>TAP AN ELEMENT  •  SWIPE TO MOVE THROUGH THE TABLE</Text><Text style={s.version}>REACT NATIVE • V5</Text></View>
  </View>;
}
