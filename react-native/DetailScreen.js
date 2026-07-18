import React from 'react';
import {Pressable,ScrollView,Text,View} from 'react-native';
import AtomView from './AtomView';
import {FAMILY_COLORS,MASS_NUMBERS,NAMES,SYMBOLS,bonds,family,familyLabel,shells} from './data';
import s from './styles';

export default function DetailScreen({number,back}){
 const i=number-1,sh=shells(number),kind=family(number),accent=FAMILY_COLORS[kind],mass=MASS_NUMBERS[i],neutrons=Math.max(0,mass-number);
 return <ScrollView contentContainerStyle={s.explorer}>
  <View style={s.explorerHeader}><Pressable onPress={back} style={s.backCircle}><Text style={s.backArrow}>‹</Text></Pressable><View style={{alignItems:'center'}}><Text style={s.explorerTitle}>{NAMES[i].toUpperCase()} ({SYMBOLS[i]})</Text><Text style={s.explorerSub}>ATOM EXPLORER • INTERACTIVE STRUCTURE AND ENERGY LEVELS</Text></View><View style={{width:58}}/></View>
  <View style={s.explorerBody}>
   <View style={s.infoPanel}>
    <View style={s.identityRow}><View><Text style={s.identitySymbol}>{SYMBOLS[i]}</Text><Text style={s.identityName}>{NAMES[i]}</Text></View><View style={s.atomicBadge}><Text style={s.atomicBadgeNumber}>{number}</Text><Text style={s.atomicBadgeText}>ATOMIC{`\n`}NUMBER</Text></View></View>
    {[['PROTONS',number,'#ff466f'],['NEUTRONS',neutrons,'#367fff'],['ELECTRONS',number,'#2cc8ff'],['MASS NUMBER',mass,'#dcecff']].map(([label,value,color])=><View key={label} style={s.statRow}><View style={[s.statOrb,{backgroundColor:color}]}/><Text style={s.statLabel}>{label}</Text><Text style={s.statValue}>{value}</Text></View>)}
    <Text style={s.neutralNote}>Neutral atom: protons = electrons = {number}</Text>
   </View>
   <View style={s.atomStage}><View style={s.holoGlow}/><AtomView number={number} shells={sh}/><View style={s.holoBase}/><Text style={s.rotateHint}>↶  DRAG TO ROTATE  ↷</Text></View>
   <View style={s.sciencePanel}>
    <Text style={s.panelHeading}>SCIENTIFIC MODEL</Text>
    <Text style={s.scienceCopy}>Nucleus and particles are enlarged. Shells represent principal energy levels; electron paths are illustrative, not literal planetary orbits.</Text>
    <View style={s.dataRow}><Text style={s.dataLabel}>FAMILY</Text><Text style={[s.dataValue,{color:accent}]}>{familyLabel(kind)}</Text></View>
    <View style={s.dataRow}><Text style={s.dataLabel}>SHELLS</Text><Text style={s.dataValue}>{sh.join(' • ')}</Text></View>
    <View style={s.dataRow}><Text style={s.dataLabel}>NET CHARGE</Text><Text style={s.dataValue}>0</Text></View>
    <View style={s.dataRow}><Text style={s.dataLabel}>COMMON BONDS</Text><Text style={s.dataValue}>{bonds(SYMBOLS[i])}</Text></View>
    <Text style={s.modelNote}>Illustrative model • not to scale</Text>
   </View>
  </View>
  <View style={s.modeDock}>{['SHELLS','CLOUD','NUCLEUS','PAUSE'].map((x,j)=><View key={x} style={[s.modeButton,j===0&&s.modeActive]}><Text style={[s.modeText,j===0&&s.modeTextActive]}>{x}</Text></View>)}</View>
 </ScrollView>;
}