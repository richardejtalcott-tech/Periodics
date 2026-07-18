import React from 'react';
import {Pressable,ScrollView,Text,View} from 'react-native';
import {COLORS,NAMES,SYMBOLS} from './data';
import s from './styles';
export default function TableScreen({open}){return <View style={{flex:1}}><View style={s.header}><Text style={s.title}>PERIODIC</Text><Text style={s.sub}>AN INTERACTIVE JOURNEY THROUGH MATTER</Text></View><ScrollView contentContainerStyle={s.table}><View style={s.grid}>{SYMBOLS.map((symbol,i)=><Pressable key={symbol} onPress={()=>open(i+1)} style={[s.tile,{borderColor:COLORS[i%COLORS.length],shadowColor:COLORS[i%COLORS.length],shadowOpacity:.55,shadowRadius:7,elevation:7}]}><Text style={s.num}>{i+1}</Text><Text style={s.sym}>{symbol}</Text><Text numberOfLines={1} style={s.name}>{NAMES[i]}</Text></Pressable>)}</View><Text style={s.hint}>Tap an element to enter its atomic world. Scroll and pinch gestures remain available through the Android interface.</Text></ScrollView></View>}
