import React,{useState} from 'react';
import {SafeAreaView,StatusBar} from 'react-native';
import DetailScreen from './DetailScreen';
import TableScreen from './TableScreen';
import s from './styles';
export default function App(){const [selected,setSelected]=useState(null);return <SafeAreaView style={[s.root,s.safe]}><StatusBar hidden/>{selected?<DetailScreen number={selected} back={()=>setSelected(null)}/>:<TableScreen open={setSelected}/>}</SafeAreaView>}
