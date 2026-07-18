import React from 'react';
import {Pressable,ScrollView,Text,View} from 'react-native';
import AtomView from './AtomView';
import {FAMILY_COLORS,MASS_NUMBERS,NAMES,SYMBOLS,bonds,family,familyLabel,shells} from './data';
import s from './styles';

export default function DetailScreen({number,back}){
 const i=number-1,sh=shells(number),kind=family(number),accent=FAMILY_COLORS[kind];
 const neutrons=Math.max(0,MASS_NUMBERS[i]-number);
 return <ScrollView contentContainerStyle={s.detail}>
  <View style={s.panel}>
   <Pressable onPress={back}><Text style={s.back}>‹ PERIODIC TABLE</Text></Pressable>
   <Text style={s.big}>{NAMES[i]} <Text style={s.gold}>{SYMBOLS[i]}</Text></Text>
   <View style={[s.familyPill,{backgroundColor:accent}]}><Text style={s.familyText}>{familyLabel(kind).toUpperCase()}</Text></View>
   <Text style={s.fact}>Atomic number: {number}</Text>
   <Text style={s.fact}>Representative mass number: {MASS_NUMBERS[i]}</Text>
   <Text style={s.fact}>Protons: {number}</Text>
   <Text style={s.fact}>Neutrons: {neutrons}</Text>
   <Text style={s.fact}>Electrons in a neutral atom: {number}</Text>
   <Text style={s.fact}>Electron shells: {sh.join(' • ')}</Text>
   <View style={s.bond}><Text style={s.gold}>COMMON BONDS</Text><Text style={s.bondText}>{bonds(SYMBOLS[i])}</Text></View>
   <View style={s.journey}>
    {[['ATOM','≈10⁻¹⁰ m'],['NUCLEUS','≈10⁻¹⁵ m'],['NUCLEON','≈10⁻¹⁵ m'],['QUARKS','<10⁻¹⁸ m']].map(([title,scale])=><View key={title} style={s.journeyNode}><Text style={s.journeyTitle}>{title}</Text><Text style={s.journeyScale}>{scale}</Text></View>)}
   </View>
   <View style={s.aiButton}><Text style={s.aiButtonTitle}>ASK PERIODIC AI</Text><Text style={s.aiButtonText}>Reserved for the contextual chemistry tutor and voice agent.</Text></View>
  </View>
  <View style={[s.panel,{alignItems:'center'}]}>
   <Text style={s.gold}>INTERACTIVE ATOM</Text>
   <AtomView number={number} shells={sh}/>
   <Text style={s.fact}>Compact rotating nucleus • visible oval orbital paths</Text>
   <Text style={s.hint}>Electron markers are distributed by the element’s calculated shell population.</Text>
  </View>
 </ScrollView>;
}
