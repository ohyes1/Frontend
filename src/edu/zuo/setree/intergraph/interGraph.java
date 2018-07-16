package edu.zuo.setree.intergraph;

import java.io.*;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * Created by wangyifei on 2018/5/1.
 */
class pair extends Object{
    private int left;
    private int right;
    public pair(int left, int right){
        this.left = left;
        this.right = right;
    }
    public void setLeft(int left){ this.left = left; }
    public void setRight(int right){ this.right = right; }
    public void set(int left, int right){
        this.left = left;
        this.right = right;
    }
    public String toString(){
        return "("+left+","+right+")";
    }

    @Override
    public int hashCode() {
        return this.toString().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof pair) {
            pair o = (pair) obj;
            if (left == o.left && right == o.right){
                return true;
            }
        }
        return false;
    }
}

public class interGraph {
    public static final File consEdgeGraphFile = new File("intraOutput/consEdgeGraph");
    public static final File var2indexMapFile = new File("intraOutput/var2indexMap");
    public static final File conditionalSmt2File = new File("intraOutput/conditionalSmt2");
    public static int funcIndex = -1;

    private static Map<pair, Integer> pair2indexMap = new LinkedHashMap<>();
    private static Map<Integer, String> index2varMap = new LinkedHashMap<>();
    private static Map<String, Integer> func2indexMap = new LinkedHashMap<>();
    private static Map<Integer, Map<String, Integer>> funcParamReturn = new LinkedHashMap<>();

    /** each line of pair2varMapFile is
     * (funcIndex,inFuncVarIndex) : outFuncVarIndex
     */
    public static final File pair2indexMapFile = new File("interOutput/pair2indexMap");

    /** each line of index2varMapFile is
     * outFuncVarIndex : funcIndex.inFuncVarIndex.nodeIndex.varName
     */
    public static final File index2varMapFile = new File("interOutput/index2varMap");

    /** each line of func2indexMapFile is
     * funcIndex : funcName
     */
    public static final File func2indexMapFile = new File("interOutput/func2indexMap");

    /** each line of interGraphFile is
     * outFuncVarIndex  outFuncVarIndex label   constraint
     * label: a|e|l|n|o|p|r|s
     * constraint: [T]|[pair,pair]
     * pair: (funcIndex, nodeIndex)
     *
     * [Assign] a
     * [Load]   l
     * [New]    n
     * [Callee] c
     * [Param]  p
     * [Return] r
     * [Store]  s
     * other    e
     */
    public static final File interGraphFile = new File("interOutput/interGraph");

    /** each line of interSmt2File is
     * (funcIndex, nodeIndex):constraintString
     */
    public static final File interSmt2File = new File("interOutput/interSmt2");

    public static void genMap(){
        try {
            if (!consEdgeGraphFile.exists()) {
                System.out.println("Error: consEdgeGraph file not exists.");
                return;
            }
            if (! var2indexMapFile.exists()) {
                System.out.println("Error: var2indexMap file not exists.");
                return;
            }
            Scanner consEdgeGraphInput = new Scanner(consEdgeGraphFile);
            Scanner var2indexMapInput = new Scanner(var2indexMapFile);
            int varIndex = 0;
            while(var2indexMapInput.hasNextLine()){
                String line = var2indexMapInput.nextLine();
                //System.out.println("#"+line+"#");
                if(line.length()==0){

                }else if(line.startsWith("<")){
                    ++funcIndex;
                    func2indexMap.put(line,funcIndex);
                }else {
                    String[] tokens = line.split(" : ");
                    int right = Integer.parseInt(tokens[0]);
                    pair2indexMap.put(new pair(funcIndex,right), varIndex);
                    index2varMap.put(varIndex,funcIndex+"."+tokens[0]+"."+tokens[1]);
                    varIndex++;
                }

            }

            funcIndex=-1;
            while(consEdgeGraphInput.hasNextLine()) {
                String line = consEdgeGraphInput.nextLine();
                if (line.length() == 0) {

                } else if (line.startsWith("<")) {
                    ++funcIndex;
                    funcParamReturn.put(funcIndex,new LinkedHashMap<String, Integer>());
                } else {
                    String[] tokens = line.split(", ");
                    if (tokens.length == 2) {
                        int i = Integer.parseInt(tokens[0]);
                        funcParamReturn.get(funcIndex).put(tokens[1], i);
                    }
                }
            }
            consEdgeGraphInput.close();
            var2indexMapInput.close();
        }
        catch(IOException e) {
            e.printStackTrace();
        }
    }

    public static void printMap(){
        PrintWriter pair2indexMapOut = null;
        PrintWriter index2varMapOut = null;
        PrintWriter func2indexMapOut = null;
        try {
            if (!pair2indexMapFile.exists()) {
                pair2indexMapFile.createNewFile();
            }
            if (!index2varMapFile.exists()) {
                index2varMapFile.createNewFile();
            }
            if (!func2indexMapFile.exists()) {
                func2indexMapFile.createNewFile();
            }

            pair2indexMapOut = new PrintWriter(new BufferedWriter(new FileWriter(pair2indexMapFile,true)));
            index2varMapOut = new PrintWriter(new BufferedWriter(new FileWriter(index2varMapFile,true)));
            func2indexMapOut = new PrintWriter(new BufferedWriter(new FileWriter(func2indexMapFile,true)));

            for (pair p: pair2indexMap.keySet()) {
                pair2indexMapOut.println(p.toString()+" : "+pair2indexMap.get(p));
            }
            for (Integer i: index2varMap.keySet()) {
                index2varMapOut.println(i+" : "+index2varMap.get(i));
            }
            for (String f: func2indexMap.keySet()) {
                func2indexMapOut.println(func2indexMap.get(f).toString()+" : "+f);
            }
            pair2indexMapOut.close();
            index2varMapOut.close();
            func2indexMapOut.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void printGraph(){

        try {
            Scanner consEdgeGraphInput = new Scanner(consEdgeGraphFile);
            funcIndex=-1;
            PrintWriter interGraphOut = null;
            if(!interGraphFile.exists()){
                interGraphFile.createNewFile();
            }
            interGraphOut = new PrintWriter(new BufferedWriter(new FileWriter(interGraphFile, true)));
            while(consEdgeGraphInput.hasNextLine()){
                String line = consEdgeGraphInput.nextLine();
                System.out.println("#"+line+"#");
                if(line.length()==0){

                }else if(line.startsWith("<")){
                    ++funcIndex;
                }else {
                    String[] tokens = line.split(", ");
                    if(tokens.length==2){
                        System.out.println(2);
                    }else if(tokens.length==3){
                        /* a, b, [Assign]   a
                        * a, b, [Load]  l
                        * a, b, [New]   n
                        * a, b, [Store] s
                        */
                        System.out.println(3);
                        int first_right = Integer.parseInt(tokens[0]);
                        int second_right = Integer.parseInt(tokens[1]);
                        pair p1 = new pair(funcIndex, first_right);
                        pair p2 = new pair(funcIndex, second_right);
                        int i1 = pair2indexMap.get(p1);
                        int i2 = pair2indexMap.get(p2);
                        String label=tokens[2];
                        switch (tokens[2]){
                            case "[Assign]": label="a";break;
                            case "[Load]": label="l";break;
                            case "[New]": label="n";break;
                            case "[Store]": label="s";break;
                        }
                        interGraphOut.println(i1+"\t"+i2+"\t"+label+"\t[T]");
                    }else if(tokens.length==4){
                        /* a, b, [x, y]
                        */
                        System.out.println(4);
                        int first_right = Integer.parseInt(tokens[0]);
                        int second_right = Integer.parseInt(tokens[1]);
                        pair p1 = new pair(funcIndex, first_right);
                        pair p2 = new pair(funcIndex, second_right);
                        int i1 = pair2indexMap.get(p1);
                        int i2 = pair2indexMap.get(p2);
                        int n1_rignt = Integer.parseInt(tokens[2].substring(1));
                        int n2_right = Integer.parseInt(tokens[3].substring(0,tokens[3].length()-1));
                        pair p3 = new pair(funcIndex, n1_rignt);
                        pair p4 = new pair(funcIndex, n2_right);
                        interGraphOut.println(i1+"\t"+i2+"\te\t["+p3.toString()+","+p4.toString()+"]");
                    }else{
                        assert(tokens[3]=="[Call]");
                        // Do not handle system call
                        if(! func2indexMap.containsKey(tokens[2])) continue;
                        int funcNodeIndex = Integer.parseInt(tokens[1]);
                        int callFuncIndex = func2indexMap.get(tokens[2]);
                        // callee   o
                        if(tokens[4].length()>2){
                            int first_right = Integer.parseInt(tokens[4].substring(1,tokens[4].length()-1));
                            int second_right = funcParamReturn.get(callFuncIndex).get("[Callee]");
                            pair p1 = new pair(funcIndex, first_right);
                            pair p2 = new pair(callFuncIndex, second_right);
                            int i1 = pair2indexMap.get(p1);
                            int i2 = pair2indexMap.get(p2);
                            pair p3 = new pair(funcIndex, funcNodeIndex);
                            pair p4 = new pair(callFuncIndex, 0);
                            interGraphOut.println(i1+"\t"+i2+"\to\t["+p3.toString()+","+p4.toString()+"]");
                        }
                        //params    p
                        int paraN = tokens.length - 5;
                        if(paraN == 1){
                            if(tokens[5].length()==2)continue;
                            int first_right = Integer.parseInt(tokens[5].substring(1,tokens[5].length()-1));
                            int second_right = funcParamReturn.get(callFuncIndex).get("[Para0]");
                            pair p1 = new pair(funcIndex, first_right);
                            pair p2 = new pair(callFuncIndex, second_right);
                            int i1 = pair2indexMap.get(p1);
                            int i2 = pair2indexMap.get(p2);
                            pair p3 = new pair(funcIndex, funcNodeIndex);
                            pair p4 = new pair(callFuncIndex, 0);
                            interGraphOut.println(i1+"\t"+i2+"\tp\t["+p3.toString()+","+p4.toString()+"]");
                        }else {
                            int first_right = Integer.parseInt(tokens[5].substring(1,tokens[5].length()));
                            int second_right = funcParamReturn.get(callFuncIndex).get("[Para0]");
                            pair p1 = new pair(funcIndex, first_right);
                            pair p2 = new pair(callFuncIndex, second_right);
                            int i1= pair2indexMap.get(p1);
                            int i2= pair2indexMap.get(p2);
                            pair p3 = new pair(funcIndex, funcNodeIndex);
                            pair p4 = new pair(callFuncIndex, 0);
                            interGraphOut.println(i1+"\t"+i2+"\tp\t["+p3.toString()+","+p4.toString()+"]");
                            for(int i = 1;i<paraN-1;i++){
                                first_right = Integer.parseInt(tokens[5+i].substring(0,tokens[5+i].length()));
                                second_right = funcParamReturn.get(callFuncIndex).get("[Para"+i+"]");
                                p1 = new pair(funcIndex, first_right);
                                p2 = new pair(callFuncIndex, second_right);
                                i1= pair2indexMap.get(p1);
                                i2= pair2indexMap.get(p2);
                                p3 = new pair(funcIndex, funcNodeIndex);
                                p4 = new pair(callFuncIndex, 0);
                                interGraphOut.println(i1+"\t"+i2+"\tp\t["+p3.toString()+","+p4.toString()+"]");
                            }

                            int paraIndex = paraN-1;
                            first_right = Integer.parseInt(tokens[5+paraIndex].substring(0,tokens[5+paraIndex].length()-1));
                            second_right = funcParamReturn.get(callFuncIndex).get("[Para"+paraIndex+"]");
                            p1 = new pair(funcIndex, first_right);
                            p2 = new pair(callFuncIndex, second_right);
                            i1= pair2indexMap.get(p1);
                            i2= pair2indexMap.get(p2);
                            p3 = new pair(funcIndex, funcNodeIndex);
                            p4 = new pair(callFuncIndex, 0);
                            interGraphOut.println(i1+"\t"+i2+"\tp\t["+p3.toString()+","+p4.toString()+"]");
                        }
                        //return    r
                        if(tokens[0].length()>2){
                            int second_right = Integer.parseInt(tokens[0].substring(1,tokens[0].length()-1));
                            pair p2 = new pair(funcIndex, second_right);
                            int i2= pair2indexMap.get(p2);
                            pair p4 = new pair(funcIndex, funcNodeIndex);
                            Map<String, Integer> t = funcParamReturn.get(callFuncIndex);
                            for(String s:t.keySet()){
                                if(s.startsWith("[Return")){
                                    int first_right = t.get(s);
                                    int callFuncRetNode = Integer.parseInt(s.substring(7,s.length()-1));
                                    pair p1 = new pair(callFuncIndex, first_right);
                                    int i1= pair2indexMap.get(p1);
                                    pair p3 = new pair(callFuncIndex, callFuncRetNode);
                                    interGraphOut.println(i1+"\t"+i2+"\tr\t["+p3.toString()+","+p4.toString()+"]");
                                }
                            }

                        }
                    }
                }
            }


            consEdgeGraphInput.close();
            interGraphOut.close();
        }
        catch(IOException e) {
            e.printStackTrace();
        }

    }

    public static void printSmt2() {
        try{
            if (!conditionalSmt2File.exists()) {
                System.out.println("Error: conditionalSmt2 file not exists.");
                return;
            }
            Scanner conditionalSmt2Input = new Scanner(conditionalSmt2File);
            PrintWriter interSmt2Out = null;
            if (!interSmt2File.exists()){
                interSmt2File.createNewFile();
            }
            interSmt2Out = new PrintWriter(new BufferedWriter(new FileWriter(interSmt2File, true)));
            funcIndex = -1;
            while(conditionalSmt2Input.hasNextLine()){
                String line = conditionalSmt2Input.nextLine();
                if(line.length()==0){

                }else if(line.startsWith("<")){
                    ++funcIndex;
                }else {
                    String []tokens=line.split(":");
                    if(tokens[1].startsWith("#")){

                    }else {
                        String t = tokens[1].replace("$I", "$I$" + funcIndex).replace("$L", "$L$" + funcIndex).replace("$F", "$F$" + funcIndex).replace("$D", "$D$" + funcIndex);
                        interSmt2Out.println("(" + funcIndex + "," + tokens[0] + "):" + t);
                    }
                }
            }
            conditionalSmt2Input.close();
            interSmt2Out.close();
        }
        catch(IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String args[]){
        genMap();
        printGraph();
        printMap();
        printSmt2();
    }
}
