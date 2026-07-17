package com.richardtalcott.periodic;
public final class ElementData {
    public final int number, row, group, estimatedNeutrons;
    public final String symbol, name, category;
    private ElementData(int n,String s,String nm,int r,int g,String c){
        number=n;symbol=s;name=nm;row=r;group=g;category=c;
        estimatedNeutrons=MASS_NUMBERS[n-1]-n;
    }
    // Rounded standard atomic weights (or representative isotope mass numbers
    // for elements without a standard atomic weight), indexed by atomic number.
    private static final int[] MASS_NUMBERS = {
        1,4,7,9,11,12,14,16,19,20,23,24,27,28,31,32,35,40,39,40,
        45,48,51,52,55,56,59,59,64,65,70,73,75,79,80,84,85,88,89,91,
        93,96,98,101,103,106,108,112,115,119,122,128,127,131,133,137,
        139,140,141,144,145,150,152,157,159,163,165,167,169,173,175,178,
        181,184,186,190,192,195,197,201,204,207,209,209,210,222,223,226,
        227,232,231,238,237,244,243,247,247,251,252,257,258,259,266,267,
        268,269,270,269,278,281,282,285,286,289,290,293,294,294
    };
    private static final String[] SYMBOLS = "H He Li Be B C N O F Ne Na Mg Al Si P S Cl Ar K Ca Sc Ti V Cr Mn Fe Co Ni Cu Zn Ga Ge As Se Br Kr Rb Sr Y Zr Nb Mo Tc Ru Rh Pd Ag Cd In Sn Sb Te I Xe Cs Ba La Ce Pr Nd Pm Sm Eu Gd Tb Dy Ho Er Tm Yb Lu Hf Ta W Re Os Ir Pt Au Hg Tl Pb Bi Po At Rn Fr Ra Ac Th Pa U Np Pu Am Cm Bk Cf Es Fm Md No Lr Rf Db Sg Bh Hs Mt Ds Rg Cn Nh Fl Mc Lv Ts Og".split(" ");
    private static final String[] COMMON = ("Hydrogen|Helium|Lithium|Beryllium|Boron|Carbon|Nitrogen|Oxygen|Fluorine|Neon|" +
        "Sodium|Magnesium|Aluminium|Silicon|Phosphorus|Sulfur|Chlorine|Argon|Potassium|Calcium|" +
        "Scandium|Titanium|Vanadium|Chromium|Manganese|Iron|Cobalt|Nickel|Copper|Zinc|" +
        "Gallium|Germanium|Arsenic|Selenium|Bromine|Krypton").split("\\|");
    public static ElementData byNumber(int n) {
        n=Math.max(1,Math.min(118,n)); int[] pos=position(n);
        String name=n<=COMMON.length?COMMON[n-1]:"Element "+n;
        return new ElementData(n,SYMBOLS[n-1],name,pos[0],pos[1],category(n,pos[1]));
    }
    private static int[] position(int n){
        if(n==1)return new int[]{1,1}; if(n==2)return new int[]{1,18};
        if(n<=10){int[] g={1,2,13,14,15,16,17,18};return new int[]{2,g[n-3]};}
        if(n<=18){int[] g={1,2,13,14,15,16,17,18};return new int[]{3,g[n-11]};}
        if(n<=36)return new int[]{4,n-18};
        if(n<=54)return new int[]{5,n-36};
        if(n==55)return new int[]{6,1}; if(n==56)return new int[]{6,2};
        if(n>=57&&n<=71)return new int[]{8,n-54};
        if(n<=86)return new int[]{6,n-68};
        if(n==87)return new int[]{7,1}; if(n==88)return new int[]{7,2};
        if(n>=89&&n<=103)return new int[]{9,n-86};
        return new int[]{7,n-100};
    }
    private static String category(int n,int group){
        if(n>=57&&n<=71)return "Lanthanide"; if(n>=89&&n<=103)return "Actinide";
        if(group==18)return "Noble gas"; if(group==17)return "Halogen"; if(group==1&&n!=1)return "Alkali metal";
        if(group==2)return "Alkaline-earth metal"; if(group>=3&&group<=12)return "Transition metal";
        if(n==1||n==6||n==7||n==8||n==15||n==16||n==34)return "Nonmetal";
        if(n==5||n==14||n==32||n==33||n==51||n==52)return "Metalloid";
        return "Post-transition / synthetic";
    }
    public static int[] shells(int n){
        int[] capacity={2,8,18,32,32,18,8}; int[] out=new int[7]; int left=n;
        for(int i=0;i<capacity.length&&left>0;i++){out[i]=Math.min(capacity[i],left);left-=out[i];}
        return out;
    }
}
