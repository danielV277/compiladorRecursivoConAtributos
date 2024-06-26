
package compiladorrecursivo;
// este aplicativo utiliza el metodo recursivo descendente para reconocer
// secuencias aritmeticas lógicas y relacionales


import java.util.*;
import java.io.*;

public class compiladorRecursivo {

    
    static Lexico lexico = new Lexico();
    //static String cad = "((5*4)+2)*5.4¬";
    //static String cad = "2^3>=2|3==2+4&3<1¬";
    static String cad = "((5.5/3)+10)>20&20.5<10¬";
    static String cad1 = "0123456789.";
    // variable indice es global y controla el indice del objwro lex1
    static int indice=0;
    static char sim=' ';
    static Lexico lex1 = new Lexico();
    static String cadavance="";
    
    public static void main(String[] args) {
       /* InputStreamReader isr= new InputStreamReader(System.in);
        BufferedReader flujoE = new BufferedReader(isr);*/
        analisisLexico();
        cad=lex1.cadenaLexico();
        sim=lex1.darElemento(indice).darTipo();
        cadavance=cadavance+sim;
        
        procS();
        if (sim=='¬')
            System.out.println("Se acepta la secuencia ");
        else
            System.out.println("Se rechaza la secuencia ");
    
    }
    
    public static void procS() {
        //<S> --> <ELO> 
        
        boolean res=false;
        
        switch (sim) {
            case 'i':case '(':
                    NoTerminal s1 = new NoTerminal("s1",0,0);
                    procELO(s1);
                    resultado(s1.getValor(),s1.getRelacional(),s1.getValorLogico());
                    return;
            default: 
                    System.out.println("Secuencia"+cad+" no se acepta");
                    rechace();
        }    
    }

    public static void procELO(NoTerminal s1){
        //<ELO>     --> <EL2><ELO_L>
        
         switch (sim) {
            case 'i':case '(':
                NoTerminal s2 = new NoTerminal("s2",0,0);
                procEL2(s2);
                NoTerminal s3 = new NoTerminal("s3", 0, 0);
                NoTerminal i1 = s2;
                procELO_L(i1, s1);    
                    return;
            default: 
                    System.out.println("Secuencia"+cad+" no se acepta");
                    rechace();
        }   
        
        
        
    }

    public static void procELO_L(NoTerminal i1,NoTerminal s1) {
        // <ELO_L>    --> |<EL2><ELO_L>
        //<ELO_L>    --> e
        
        switch (sim) {
            case '|':
                    avance();
                    NoTerminal s2 = new NoTerminal("s2",0, 0);
                    procEL2(s2);
                    esRelacional(i1,s2);
                    NoTerminal s4 = new NoTerminal("s4", 0, 0);
                    procOR(i1,s2, s4);
                    procELO_L(s4, s1);
                    
                    return;
            case ')': case '¬':
                    asignacionValores(s1,i1);
                    //s1.setValor(i1);
                    return;

            default: 
                    System.out.println("Secuencia procel "+cad+" no se acepta");
                    rechace();
        }
        
    }

    public static void procEL2(NoTerminal s1) {
        //<EL2>     --> <ER><EL2_L>
        
        switch (sim) {
            case 'i':case '(':
                NoTerminal s2 = new NoTerminal("s2",0,0);
                procER(s2);
                procEL2_L(s2, s1);
                return;
            default: 
                System.out.println("Secuencia"+cad+" no se acepta");
                rechace();
        } 
    }

    public static void procEL2_L(NoTerminal i1,NoTerminal s1) {
        // <EL2_L>   --> &<ER><EL2_L> 
        //<EL2_L>   -->  
        
        switch (sim) {
            case '&':
                avance();
                NoTerminal s2 = new NoTerminal("s2",0, 0);
                procER(s2);
                esRelacional(i1,s2);
                NoTerminal s4 = new NoTerminal("s4", 0, 0);
                procAND(i1,s2, s4);
                procEL2_L(s4, s1);    
                return;
            case ')': case '¬': case '|':
                asignacionValores(s1,i1);
                //s1.setValor(i1);
                return;

            default: 
                    System.out.println("Secuencia procel "+cad+" no se acepta");
                    rechace();
        }
    }

    public static void procER(NoTerminal s1) {
        //.<ER>      --> <E><ER_L> 
        switch (sim) {
            case 'i':case '(':
                NoTerminal s2 = new NoTerminal("s2",0,0);
                procE(s2);
                procER_L(s2, s1);
                return;
            default: 
                System.out.println("Secuencia"+cad+" no se acepta");
                rechace();
        }
    }

    public static void procER_L(NoTerminal i1,NoTerminal s1) {
        // <ER_L>    --> <OR><E> 
        //<ER_L>   --> 
        switch (sim) {
            case '>': case '<': case '=': case '!':
                    
                    NoTerminal s2 = new NoTerminal("s2",0,0);
                    procOR(s2);
                    NoTerminal s3 = new NoTerminal("s3",0,0);
                    procE(s3);
                    pComparar(i1,s3,s2,s1);
                    
                    return;
            case ')': case '¬': case '|': case '&':
                    //s1.setValor(i1);
                    asignacionValores(s1, i1);
                    return;

            default: 
                    System.out.println("Secuencia procel "+cad+" no se acepta");
                    rechace();
        }
    }

    public static void procOR(NoTerminal s1) {
        // <OR>    --> < <ME>
        //<OR>    --> > <MA>
        //<OR>    --> =<IG>
        //<OR>    --> !<DI>   
        
        switch (sim) {
            case '<':
                avance();
                s1.setRelacional(true);
                procME(s1);
                return;                
            case '>': 
                avance();
                s1.setRelacional(true);
                procMA(s1);
                return;
            case '=': 
                avance();
                s1.setRelacional(true);
                procIG(s1);
                return;
            case '!':
                avance();
                s1.setRelacional(true);
                procDI(s1);
                return;
            default: 
                    System.out.println("Secuencia procel "+cad+" no se acepta");
                    rechace();
        }
    }

    public static void procME(NoTerminal s1) {
        // .<ME>     --> = 
        // .<ME>     -->  
        switch (sim) {
            case '=':
                avance();
                s1.setValor(1);
                return;
            case 'i': case '(':
                s1.setValor(2);
                return;
            default: 
                    System.out.println("Secuencia procel "+cad+" no se acepta");
                    rechace();
        }
    }

    public static void procMA(NoTerminal s1) {
        //.<MA>     --> =   
        //<MA>     -->   
        switch (sim) {
            case '=':
                avance();
                s1.setValor(3);
                return;
            case 'i': case '(':
                s1.setValor(4);
                return;
            default: 
                System.out.println("Secuencia procel "+cad+" no se acepta");
                rechace();
        }
    }

    public static void procIG(NoTerminal s1) {
        // .<IG>     --> =
        switch (sim) {
            case '=':
                avance();
                s1.setValor(5);
                return;
            default: 
                System.out.println("Secuencia procel "+cad+" no se acepta");
                rechace();
        }
    }

    public static void procDI(NoTerminal s1) {
        // .<DI>     --> =  
        switch (sim) {
            case '=':
                avance();
                s1.setValor(6);    
                return;
            default: 
                    System.out.println("Secuencia procel "+cad+" no se acepta");
                    rechace();
        }
    }

    public static void procE(NoTerminal s1) {
        // .<E>      --> <T><E_L> 
        switch (sim) {
            case 'i':case '(':
                NoTerminal s2 = new NoTerminal("s2",0,0);
                procT(s2);
                procE_L(s2,s1);
                return;
            default: 
                System.out.println("Secuencia"+cad+" no se acepta");
                rechace();
        }
    }

    public static void procE_L(NoTerminal i1,NoTerminal s1) {
        // .<E_L>    --> + <T><E_L>  
        //<E_L>    --> - <T><E_L> 
        //<E_L>    -->    
        
         switch (sim) {
            case '+': case '-':
                char auxSim = sim;
                avance();
                NoTerminal s2 = new NoTerminal("s2",0,0);
                procT(s2);
                NoTerminal s3 = new NoTerminal("s3",0,0);
                if(auxSim == '-')resta(i1.getValor(),s2.getValor(), s3);
                if(auxSim == '+')suma(i1.getValor(),s2.getValor(),s3);
                procE_L(s3,s1);
                return;
            case '&': case '¬': case '|': case '>': case '<': case '=':
            case '!': case ')':
                    s1.setValor(i1.getValor());
                    return;
            default: 
                    System.out.println("Secuencia procel "+cad+" no se acepta");
                    rechace();
        }
    }

    public static void procT(NoTerminal s1) {
        // <T>      --> <P><T_L> 
        switch (sim) {
            case 'i':case '(':
                NoTerminal s2 = new NoTerminal("s2",0,0);
                procP(s2);
                System.out.println("Valor de <P> "+s2.getValor());
                procT_L(s2,s1);
                return;
                    

            default: 
                System.out.println("Secuencia"+cad+" no se acepta");
                rechace();
        }
    }

    public static void procT_L(NoTerminal i1, NoTerminal s1) {
        // .<T_L>    --> * <P><T_L>  
        //.<T_L>    --> / <P><T_L> 
        //.<T_L>    -->  
        
        switch (sim) {
            case '*': case '/':
                char auxSim = sim;
                avance();
                NoTerminal s2 = new NoTerminal("s2",0,0);
                procP(s2);
                NoTerminal s3 = new NoTerminal("s3",0,0);
                if(auxSim == '*') mul(i1.getValor(),s2.getValor(),s3);
                if(auxSim == '/') div(i1.getValor(),s2.getValor(),s3);
                procT_L(s3,s1);
                return;
                 
            case '&': case '|': case '>': case '<': case '=':
            case '!': case ')': case '+': case '-': case '¬':
                    s1.setValor(i1.getValor());
                    return;
       
            default: 
                    System.out.println("Secuencia"+cad+" no se acepta");
                    rechace();
        }
    }

    public static void procP(NoTerminal s1) {
        // .<P>      --> <F><P_L>
        switch (sim) {
            case 'i':case '(':
                NoTerminal s2 = new NoTerminal("s1",0,0);
                procF(s2);
                procP_L(s2, s1);
                return;
            default: 
                System.out.println("Secuencia"+cad+" no se acepta");
                rechace();
        } 
    }

    public static void procP_L(NoTerminal i1,NoTerminal s1) {
        // .<P_L>    --> ^<F><P_L> 
        //<P_L>    -->     
         switch (sim) {
            case '^':
                avance();
                NoTerminal s2 = new NoTerminal("s2",0,0);
                procF(s2);
                NoTerminal s3 = new NoTerminal("s3",0,0);
                exp(i1,s2,s3);
                procT_L(s3,s1);
                return;
                 
            case '&': case '|': case '>': case '<': case '=': case '*':
            case '!': case ')': case '+': case '-': case '/': case '¬':
                s1.setValor(i1.getValor());
                return;
       
            default: 
                    System.out.println("Secuencia"+cad+" no se acepta");
                    rechace();
        }
    }

    public static void procF(NoTerminal s1) {
        // .<F>      --> ( <ELO> )  
       //<F>      --> I  
        switch (sim) {
            case 'i':
                    Elemento ele= lex1.darElemento(indice);
                    s1.setValor(ele.darValor());
                    System.out.println("Valor del i = "+ele.darValor());
                    avance();
                    return;

            case '(':
                    avance();
                    procELO(s1);
                    if (sim==')'){
                        avance();}
                    else {
                        System.out.println("Secuencia"+cad+" no se acepta");
                        rechace();
                    }
                    break;    
            default: 
                System.out.println("Secuencia"+cad+" no se acepta");
                rechace();
        }
    }
    
    public static void asignacionValores(NoTerminal s1,NoTerminal s2){
        if(s1.getRelacional() || s2.getRelacional()){
            s1.setValorLogico(s2.getValorLogico());
            s1.setRelacional(true);
        }else{
             s1.setValor(s2.getValor());
        }
    }
    public static void esRelacional(NoTerminal i2,NoTerminal i3){
        if(!i2.getRelacional() || !i3.getRelacional())rechace();
        
    }
    
    public static void pComparar(NoTerminal i2,NoTerminal i3,NoTerminal i4,NoTerminal s4){
        switch ((int)i4.getValor()) {
            case 1://<=
                s4.setRelacional(true);
                s4.setValorLogico(i2.getValor() <= i3.getValor());
                break;
            case 2://<
                s4.setRelacional(true);
                s4.setValorLogico(i2.getValor() < i3.getValor());
                break;
            case 3://>=
                s4.setRelacional(true);
                s4.setValorLogico(i2.getValor() >= i3.getValor());
                break;
            case 4://>
                s4.setRelacional(true);
                s4.setValorLogico(i2.getValor() > i3.getValor());
                break;
            case 5: //==
                s4.setRelacional(true);
                s4.setValorLogico(i2.getValor() == i3.getValor());
                break;
            case 6://!= 
                s4.setRelacional(true);
                s4.setValorLogico(i2.getValor() != i3.getValor());
                break;
            default:
                throw new AssertionError();
        }
    }
    
    public static void exp(NoTerminal i2,NoTerminal i3,NoTerminal s3){
        s3.setValor(Math.pow(i2.getValor(), i3.getValor())); 
    }
    public static void procOR(NoTerminal i4,NoTerminal i5,NoTerminal s4){
        System.out.println("Elemtos OR "+i4.getValorLogico()+"  "+i5.getValorLogico());
        boolean nuevoValorLogico = true;
        s4.setRelacional(true);
        if(i4.getValorLogico() == false && i5.getValorLogico() == false) nuevoValorLogico = false;
        s4.setValorLogico(nuevoValorLogico);
    }
    
    public static void procAND(NoTerminal i4,NoTerminal i5,NoTerminal s4){
        System.out.println("Elemtos AND "+i4.getValorLogico()+"  "+i5.getValorLogico());
        boolean nuevoValorLogico = false;
        s4.setRelacional(true);
        if(i4.getValorLogico() == true && i5.getValorLogico() == true) nuevoValorLogico = true;
        s4.setValorLogico(nuevoValorLogico);
    }
       
    public static void suma(double i1, double i2,NoTerminal s3){
        System.out.println("Elementos a sumar "+i1+"  "+i2);
        s3.setValor(i1+i2);
    }
    
    public static void resta(double i1, double i2,NoTerminal s3){
        System.out.println("Elementos a restar "+i1+"  "+i2);
        s3.setValor(i1-i2);
    }
    
    public static void mul(double i1, double i2,NoTerminal s3){
        s3.setValor(i1*i2);
    }
    
    public static void div(double i1, double i2,NoTerminal s3){
        s3.setValor(i1/i2);
    }
    
    public static void resultado(double res,boolean relacional,boolean resBool){
        if(relacional){System.out.println("Resultado booleano="+resBool);}
        else{System.out.println("Resultado numerico ="+res);}
        
    }
    
    public static void analisisLexico(){
        // Este analizador es sencillo determina solo constantes enteras y reales positivas
        // Trabaja los diferentes elementos en un ArrayList que trabaja con la clase Clexico
        // la cual define el ArrayList con la clase CElemento
        // Almacen los valores para poder hallar los resultados
        
        Elemento ele1;
        
        int i=0;
        int ind=0;
        char tip=0;
        char sim1=cad.charAt(i);
        double val=0;
        
        while (sim1!='¬'){
            // determina si sim1 esta en la cadena de digitos cad1 que es global
            if (cad1.indexOf(sim1)!=-1){
                String num="";
                while(cad1.indexOf(sim1)!=-1){  
                    num=num+sim1;
                    i++;
                    sim1=cad.charAt(i);
        
                }
                // en el String num se almacena el posible real
                // DeterminarNumero aplica el autómata para enteros y reales
                if (determinarNumero(num)){
                    val=Double.parseDouble(num);
                    tip='i';
                    // se tipifica el valor como i
                }
                else{ 
                    System.out.println("Se rechaza la secuencia");
                    System.exit(0);
                }
               }
            else {
               // si el simbolo de entrada no esta en cad1 lo tipifica como tal ej
               // +,-,* (,) etc.
                
               tip=(char)sim1;
               i++;
               sim1=cad.charAt(i);
               val=0;
             }
        
            // con los elementos establecidos anteriormente se crea el elemento y se lo
            // adicina a lex1 que es el objeto de la clase Clexico
            
            ele1=new Elemento(tip,val,ind);
            lex1.adicionarElemento(ele1);
            
            ind=ind+1;
            //System.out.print("indice ="+ind);
               
        } // después del while se adiciona el fin de secuencia al léxico
        ele1=new Elemento('¬',0,ind);
        lex1.adicionarElemento(ele1);
        lex1.mostrarLexico();
        System.out.println(" cadena"+lex1.cadenaLexico());
    }
    
    public static boolean determinarNumero(String numero){
        // Aplica el autómata para determinar si es un número valido
        // Recibe el estrin numero y entrega una variable boolena
        // con el resultado
        
        int estado=1,i=0;
        char simbolo;
        boolean b=true;
        while (i<numero.length()&&b) {
            simbolo = numero.charAt(i);
            switch (simbolo) {
                case '0':case '1':case '2':case '3':case '4':case '5':case '6':  
                case '7':case '8':case '9':    
                    switch (estado) {
                        case 1:
                           estado=2;
                           i++;
                           break;
                        case 2:
                           estado=2;
                           i++;
                           break;
                        case 3:
                           estado=4;
                           i++;
                           break;
                        case 4:
                            estado=4;
                           i++;
                           break;
                    }
                    break;
                case '.':    
                    switch (estado) {
                        case 1:case 3: case 4:
                           b=false;
                           break;
                        case 2:
                           estado=3;
                           i++;
                           break;
                    }
            }
        }
        return b;
    }
    
    public static void avance(){
           indice++;
       if (indice<cad.length()) {
            sim=lex1.darElemento(indice).darTipo();
            cadavance=cadavance+sim;
            System.out.println("Cadena procesada "+cadavance);
       }
    }
    
    public static void mostrarContador(int i2){
    System.out.println("Cantidad de unos "+i2);
    }
    
    public static void rechace(){
        System.out.println("Se rechaza la secuencia");
        System.exit(0);
    }
    
}
