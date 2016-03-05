package com.example.androidstudio.kalkulaatori;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Created by AndroidStudio on 02.03.2016.
 */
public class MathNode {
    double val;
    String op;
    MathNode left;
    MathNode right;

    public MathNode (double value, String op, MathNode left, MathNode right){
        val = value;
        this.op = op;
        this.left = left;
        this.right = right;
    }

    public static MathNode parse(String s){
        if(s.indexOf("++") != -1 || s.indexOf("--") != -1){
            throw new RuntimeException("invalid operation");
        }
        String[] data = s.split("\\+", 2);
        if(data.length == 2){
            if(data[0].indexOf("(") == -1){
                if(data[0].trim().equals("")){
                    return parse(data[1].trim());
                }
                return new MathNode(0,"+", parse(data[0]), parse(data[1]));
            }
        }

        data = s.split("-",2);
        if(data.length == 2){
            if(data[0].indexOf("(") == -1){
                if(data[0].trim().equals("")){
                    String[] data1 = data[1].split("-",2);
                    if(data1.length == 1){
                        if(data[1].indexOf("(") == -1){
                            String[] data2 = data[1].split("(\\*|\\:)",2);
                            if(data2.length == 1){
                                return new MathNode((-1)*stringToDouble(data[1].trim()),"", null, null);
                            }
                        }
                    }
                    else{
                        int firstValIndex = s.indexOf(data1[0]);
                        String dataSub = s.substring(firstValIndex + data1[0].length());
                        int secondValIndex = dataSub.indexOf(data1[1]);
                        String op = dataSub.substring(0,secondValIndex).trim();
                        if(data1[0].indexOf("(") == -1){
                            if(op.equals("-")){
                                return  new MathNode(0, "+", parse("-" + data1[0].trim()), parse("-" + data1[1].trim()));
                            }
                            return new MathNode(0, op, parse("-" + data1[0].trim()), parse("-" + data1[1].trim()));
                        }
                    }
                }
                else{
                    String[] data1 = s.split("-");
                    String leftData = "";
                    for(int i = 0; i < data1.length; i++){
                        String[] data2 = data[1].split("(\\*|\\:)");
                        if(data2.length == 2){
                            if(i == 0){
                                leftData += data1[i];
                            }
                            else{
                                leftData += data1[i];
                            }
                        }
                    }
                    int firstValIndex = s.indexOf(data[0]);
                    String dataSub = s.substring(firstValIndex + data[0].length());
                    int secondValIndex = dataSub.indexOf(data[1]);
                    String op = dataSub.substring(0,secondValIndex).trim();
                    if(op.trim().equals("-")){
                        return new MathNode(0,"+", parse(data[0].trim()),parse("-" + data[1].trim()));
                    }
                    return new MathNode(0, op,parse("-" + data[0].trim()),parse(data[1].trim()));
                }
            }
        }
        data = s.split("\\*", 2);
        if(data.length == 2){
            if(data[0].indexOf("(") == -1){
                return new MathNode(0,"*",parse(data[0].trim()),parse(data[1].trim()));
            }
        }
        data = s.split(":", 2);
        if(data.length == 2){
            if(data[0].indexOf("(") == -1){
                return new MathNode(0,":",parse(data[0].trim()),parse(data[1].trim()));
            }
        }
        int leftBracketIndex = s.indexOf('(');
        int rightBracketIndex = s.indexOf(')');
        if (leftBracketIndex != -1 && rightBracketIndex != -1){
            String leftBracketSub = s.substring(0, leftBracketIndex).trim();
            int leftBracketCount = 0;
            int rightBracketCount = 0;
            for (int i = leftBracketIndex; i < s.length(); i++){
                if (s.charAt(i) == '('){
                    leftBracketCount++;
                }
                if (s.charAt(i) == ')'){
                    rightBracketCount++;
                }
                if (i != 0 && leftBracketCount == rightBracketCount){
                    String firstBracket = s.substring(0, i + 1).trim();
                    rightBracketIndex = i;
                    String bracketOut = s.substring(rightBracketIndex + 1).trim();
                    String bracketIn = s.substring(leftBracketIndex + 1, i).trim();
                    if(leftBracketSub.equals("")){
                        if(bracketOut.trim().equals("")){
                            return parse(bracketIn);
                        }
                        else{
                            String op = "*";
                            if(bracketOut.charAt(0) == '+'){
                                op = "+";
                            }
                            else if(bracketOut.charAt(0) == ':'){
                                op = ":";
                            }
                            else if(bracketOut.charAt(0) == '-'){
                                return new MathNode(0, "+", parse(firstBracket),parse("-" + bracketOut.substring(1)));
                            }
                            if(bracketOut.charAt(0) == '('){
                                return new MathNode(0, op, parse(firstBracket), parse(bracketOut));
                            }
                            return new MathNode(0,op,parse(firstBracket),parse(bracketOut.substring(1)));
                        }
                    }
                    else{
                        if(bracketOut.trim().equals("")){
                            if(leftBracketSub.equals("-")){
                                return new MathNode(0,"*", parse("-1"),parse(bracketIn));
                            }
                            else{
                                return new MathNode(0, "*",parse(leftBracketSub),parse(bracketIn));
                            }
                        }
                        else{
                            String op = "*";
                            if(bracketOut.charAt(0) == '+'){
                                op = "+";
                            }
                            else if(bracketOut.charAt(0) == ':'){
                                op = ":";
                            }
                            else if(bracketOut.charAt(0) == '-'){
                                return new MathNode(0, "+", parse(firstBracket),parse("-" + bracketOut.substring(1)));
                            }
                            if(bracketOut.charAt(0) == '('){
                                return new MathNode(0, op, parse(firstBracket), parse(bracketOut));
                            }
                            return new MathNode(0,op,parse(firstBracket),parse(bracketOut.substring(1)));
                        }
                    }
                }
            }
        }
        return new MathNode(stringToDouble(s.trim()), "",null,null);
    }

    private static double stringToDouble(String s){
        if(s.equals("\u221E")){
            return Double.POSITIVE_INFINITY;
        }
        else{
            return Double.parseDouble(s);
        }
    }

    private static BigDecimal stringToBigDemical(String s){
        if(s.equals("\u221E")){
            return BigDecimal.valueOf(Double.POSITIVE_INFINITY);
        }
        else{
            return BigDecimal.valueOf(Double.valueOf(s));
        }
    }

    public double operation(double val1, double val2){
        if(Double.isInfinite(val1) || Double.isInfinite(val2)){
            if(op.equals("+")){
                return val1 + val2;
            }
            if(op.equals(":")){
                return val1 / val2;
            }
            if(op.equals("*")){
                return val1*val2;
            }
        }
        BigDecimal bVal1 = BigDecimal.valueOf(val1);
        BigDecimal bVal2 = BigDecimal.valueOf(val2);
        if(op.equals("+")){
            return bVal1.add(bVal2).doubleValue();
        }
        if(op.equals(":")){
            if(val2 == 0){
                if(val1 > 0){
                    return Double.POSITIVE_INFINITY;
                }
                else{
                    return Double.NEGATIVE_INFINITY;
                }
            }
            else{
                try{
                    return bVal1.divide(bVal2).doubleValue();
                }
                catch (Exception e){
                    return val1 / val2;
                }
            }
        }
        if(op.equals("*")){
            return bVal1.multiply(bVal2).doubleValue();
        }
        return 0;
    }

    public double answer(){
        return answer(this);
    }

    public double answer(MathNode treeNode){
        if(treeNode.left != null && treeNode.right != null){
            return treeNode.operation(answer(treeNode.left),answer(treeNode.right));
        }
        return treeNode.val;
    }
}
