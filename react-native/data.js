import {SYMBOLS1,NAMES1} from './elements1';
import {SYMBOLS2,NAMES2} from './elements2';
import {SYMBOLS3,NAMES3} from './elements3';
import {SYMBOLS4A,NAMES4A} from './elements4a';
import {SYMBOLS4B,NAMES4B} from './elements4b';

export const SYMBOLS=[...SYMBOLS1,...SYMBOLS2,...SYMBOLS3,...SYMBOLS4A,...SYMBOLS4B];
export const NAMES=[...NAMES1,...NAMES2,...NAMES3,...NAMES4A,...NAMES4B];

export const MASS_NUMBERS=[1,4,7,9,11,12,14,16,19,20,23,24,27,28,31,32,35,40,39,40,45,48,51,52,55,56,59,58,63,64,69,74,75,80,79,84,85,88,89,90,93,98,98,102,103,106,107,114,115,120,121,130,127,132,133,138,139,140,141,144,145,150,152,157,159,164,165,166,169,174,175,180,181,184,186,190,192,195,197,202,205,208,209,209,210,222,223,226,227,232,231,238,237,244,243,247,247,251,252,257,258,259,266,267,268,269,270,269,281,282,285,286,289,290,293,294,294];

export const FAMILY_COLORS={
  alkali:'#ff4f70',alkaline:'#ff934d',transition:'#2aa8df',post:'#6377d8',metalloid:'#d5b735',nonmetal:'#2dcf9f',halogen:'#b667db',noble:'#5075ff',lanthanide:'#9a63e8',actinide:'#e052a0',unknown:'#62758e'
};

const periodGroups={
  1:[1,18],
  2:[1,2,13,14,15,16,17,18],
  3:[1,2,13,14,15,16,17,18],
  4:[1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18],
  5:[1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18],
  6:[1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18],
  7:[1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18],
};
const ranges=[[1,2],[3,10],[11,18],[19,36],[37,54],[55,86],[87,118]];
export function position(n){
  const period=ranges.findIndex(([a,b])=>n>=a&&n<=b)+1;
  if(n>=58&&n<=71)return {period:8,group:n-55,fBlock:'lanthanide'};
  if(n>=90&&n<=103)return {period:9,group:n-87,fBlock:'actinide'};
  let index=n-ranges[period-1][0];
  if(period===6&&n>=72)index=n-55;
  if(period===7&&n>=104)index=n-87;
  return {period,group:periodGroups[period][index]};
}

export function family(n){
  if(n>=58&&n<=71)return 'lanthanide';
  if(n>=90&&n<=103)return 'actinide';
  if([3,11,19,37,55,87].includes(n))return 'alkali';
  if([4,12,20,38,56,88].includes(n))return 'alkaline';
  if([9,17,35,53,85,117].includes(n))return 'halogen';
  if([2,10,18,36,54,86,118].includes(n))return 'noble';
  if([1,6,7,8,15,16,34].includes(n))return 'nonmetal';
  if([5,14,32,33,51,52,84].includes(n))return 'metalloid';
  if((n>=21&&n<=30)||(n>=39&&n<=48)||(n>=72&&n<=80)||(n>=104&&n<=112))return 'transition';
  if([13,31,49,50,81,82,83,113,114,115,116].includes(n))return 'post';
  return 'unknown';
}

export function shells(n){
  const order=[[1,2],[2,2],[2,6],[3,2],[3,6],[4,2],[3,10],[4,6],[5,2],[4,10],[5,6],[6,2],[4,14],[5,10],[6,6],[7,2],[5,14],[6,10],[7,6]];
  const totals=[0,0,0,0,0,0,0];let left=n;
  for(const [shell,cap] of order){if(left<=0)break;const used=Math.min(left,cap);totals[shell-1]+=used;left-=used;}
  return totals.filter((v,i)=>v>0||totals.slice(i+1).some(Boolean));
}

const BONDS={
H:'H–H, O–H, C–H, N–H',He:'Normally no stable bonds',Li:'Li–F, Li–O, Li–C',Be:'Be–F, Be–O, Be–Cl',B:'B–O, B–H, B–N',C:'C–C, C–H, C–O, C–N',N:'N≡N, N–H, N–O, C–N',O:'O=O, O–H, C=O, Si–O',F:'H–F, C–F, Li–F',Ne:'Normally no stable bonds',Na:'Na–Cl, Na–O, Na–H',Mg:'Mg–O, Mg–Cl, Mg–C',Al:'Al–O, Al–Cl, Al–C',Si:'Si–O, Si–Si, Si–C',P:'P–O, P–H, P–Cl',S:'S–O, S–H, C–S',Cl:'Na–Cl, H–Cl, C–Cl',Ar:'Normally no stable bonds',K:'K–Cl, K–O, K–H',Ca:'Ca–O, Ca–C, Ca–Cl',Fe:'Fe–O, Fe–C, Fe–S',Co:'Co–O, Co–N, Co–C',Ni:'Ni–O, Ni–C, Ni–S',Cu:'Cu–O, Cu–S, Cu–Cl',Zn:'Zn–O, Zn–S, Zn–Cl',Br:'H–Br, C–Br, Na–Br',I:'H–I, C–I, I–O',Ag:'Ag–S, Ag–Cl, Ag–N',Sn:'Sn–O, Sn–Cl, Sn–C',Au:'Au–Au, Au–S, Au–Cl',Hg:'Hg–S, Hg–Cl, Hg–C',Pb:'Pb–O, Pb–S, Pb–C',U:'U–O, U–F, U–C'};
export const bonds=s=>BONDS[s]||'Common metallic, ionic, coordination, or covalent bonding depends on oxidation state.';
export const familyLabel=f=>({alkali:'Alkali metal',alkaline:'Alkaline-earth metal',transition:'Transition metal',post:'Post-transition metal',metalloid:'Metalloid',nonmetal:'Reactive nonmetal',halogen:'Halogen',noble:'Noble gas',lanthanide:'Lanthanide',actinide:'Actinide',unknown:'Synthetic / unclassified'}[f]);
