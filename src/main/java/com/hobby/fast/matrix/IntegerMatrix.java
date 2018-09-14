/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hobby.fast.matrix;

import java.io.Serializable;
import java.util.Arrays;
import java.util.stream.IntStream;

/**
 *
 * @author Marcio
 */
public class IntegerMatrix implements Serializable{
    public final int L, W, H;
    private final long data[][];  //store the data on bits
    public IntegerMatrix(int L, int W, int H) {
        check(W, 0, 63, "W");
        check(H, 0, Integer.MAX_VALUE, "H");
        check(L, 1, 32, "L");
        this.L = L;
        this.W = W;
        this.H = H;
        this.data = new long[L][H];
    }
    private void check(int param, int lower, int upper, String name){
        if(param>upper || param<lower){
            throw new IllegalArgumentException(name+" invalid: "+param);
        }
    }

    public IntegerMatrix(int L, int W, int H, String...data) {
        this(L,W,H);
        for(int y=0; y<H; y++){
            for(int x=0; x<W; x++){
                if(data[y].charAt(x)>='0'&&data[y].charAt(x)<='9'){
                    set(y, x, data[y].charAt(x)-'0');
                }
            }
        }
    }
    public boolean contains(int y, int x){
        return x>=0 && x<W && y>=0 && y<H;
    }
    /**
     * complexity: O(L) 
     */
    public void set(int y, int x, long val) {
        for(int l=0; l<L; l++){
            this.data[l][y] = (this.data[l][y] & ~(0x1L<<x)) | (val & 0x1L)<<x;
            val = val>>1;
        }
    }
    /**
     * complexity: O(L) 
     * @return this[y][x]
     */
    public int get(int y, int x) {
        long val = 0;
        for(int l=0; l<L; l++){
            val = val | (this.data[l][y]>>x & 0x1L)<<l;
        }
        //long c = this.data[y] >> x & 0x1L;
        return (int)val;
    }
    
    /**
     * complexity: O(H*L)
     * @return this & other
     */
    public IntegerMatrix interc(BinaryMatrix other) {
        IntegerMatrix result = new IntegerMatrix(L, W, H);
        for(int l=0; l<L; l++){
            for(int y=0; y<H; y++){
                result.data[l][y] = this.data[l][y] & other.data[y];
            }
        }
        return result;
    }
    /**
     * complexity: O(H*L)
     * @return this & ~other
     */
    public IntegerMatrix left(BinaryMatrix other) {
        IntegerMatrix result = new IntegerMatrix(L, W, H);
        for(int l=0; l<L; l++){
            for(int y=0; y<H; y++){
                result.data[l][y] = this.data[l][y] & ~other.data[y];
            }
        }
        return result;
    }
    /**
     * complexity: O(H*L)
     * @return ~this & other
     */
    public BinaryMatrix rigth(BinaryMatrix other) {
        BinaryMatrix result = new BinaryMatrix(W, H);
        for(int y=0; y<H; y++){
            long r = 0L;
            for(int l=0; l<L; l++){
                r = r | this.data[l][y];
            }
            result.data[y] = ~r & other.data[y];
        }
        return result;
    }
    /**
     * complexity: O(H*L)
     * @return ~this & ~other
     */
    public BinaryMatrix outside(BinaryMatrix other) {
        BinaryMatrix result = new BinaryMatrix(W, H);
        for(int y=0; y<H; y++){
            long r = 0L;
            for(int l=0; l<L; l++){
                r = r | this.data[l][y];
            }
            result.data[y] = ~r & ~other.data[y] & ~(-0x1L<<W);
        }
        return result;
    }
    /**
     * @return this | other
     */
    public IntegerMatrix union(BinaryMatrix other) {
        IntegerMatrix result = new IntegerMatrix(L, W, H);
        for(int l=1; l<L; l++){
            System.arraycopy(this.data[l], 0, result.data[l], 0, H);
        }
        for(int y=0; y<H; y++){
            long r = 0L;
            for(int l=0; l<L; l++){
                r = r | this.data[l][y];
            }
            result.data[0][y] = (r & this.data[0][y]) | (~r & other.data[y]);
        }
        return result;
    }
    /**
     * @return this ^ other
     */
    public IntegerMatrix exclusion(BinaryMatrix other) {
        IntegerMatrix result = new IntegerMatrix(L, W, H);
//        for(int l=1; l<L; l++){
//            System.arraycopy(this.data[l], 0, result.data[l], 0, H);
//        }
        for(int y=0; y<H; y++){
            for(int l=0; l<L; l++){
                result.data[l][y] = this.data[l][y] & ~other.data[y];
            }
            long r = 0L;
            for(int l=0; l<L; l++){
                r = r | this.data[l][y];
            }
            result.data[0][y] = result.data[0][y] | ~r & other.data[y];
        }
//        for(int y=0; y<H; y++){
//            long r = 0L;
//            for(int l=0; l<L; l++){
//                r = r | this.data[l][y];
//            }
//            result.data[0][y] = (this.data[0][y] | this.data[1][y] | this.data[2][y]) ^ ( other.data[y]);
//            result.data[1][y] = this.data[1][y] & ~ other.data[y];//(this.data[0][y] | this.data[1][y] | this.data[2][y]) ^ ( other.data[y]);
//        }
        return result;
    }
    




     /**
      * complexity: O(H*L)
     * @return bitCount( this )
     */
    public int count(int val){
        long count = 0;
        for(int y=0; y<H; y++){
            long r = -0x1L;
            long temp = val;
            for(int l=0; l<L; l++){
                r = r & ((this.data[l][y] & (-(temp & 0x1L))) | (~this.data[l][y] & (-(~temp & 0x1L))));
                temp = temp>>1;
            }
            r = r & ~(-0x1L<<W);
            count += Long.bitCount(r);
        }
        return (int)count;
    }
    /**
     * complexity: O(H*L)
     * @return bitCount( this & other)
     */
    public int count_interc(BinaryMatrix other, int val) {
        long count = 0;
        for(int y=0; y<H; y++){
            long r = -0x1L;
            long temp = val;
            for(int l=0; l<L; l++){
//                if((temp & 0x1L) == 1){
//                    r = r & this.data[l][y];
//                }else{
//                    r = r & ~this.data[l][y];
//                }
                r = r & ((this.data[l][y] & (-(temp & 0x1L))) | (~this.data[l][y] & (-(~temp & 0x1L))));
                temp = temp>>1;
            }
            r = r & other.data[y];
            count += Long.bitCount(r);
        }
        return (int)count;
    }
    /**
     * complexity: O(H*L)
     * @return bitCount( this & ~ other)
     */
    public int count_left(BinaryMatrix other, int val) {
        long count = 0;
        for(int y=0; y<H; y++){
            long r = -0x1L;
            long temp = val;
            for(int l=0; l<L; l++){
                r = r & ((this.data[l][y] & (-(temp & 0x1L))) | (~this.data[l][y] & (-(~temp & 0x1L))));
                temp = temp>>1;
            }
            r = r & ~other.data[y] & ~(-0x1L<<W);
            count += Long.bitCount(r);
        }
        return (int)count;
    }
    /**
     * complexity: O(H*L)
     * @return bitCount( ~this & other)
     */
    public int count_rigth(BinaryMatrix other) {
        long count = 0;
        for(int y=0; y<H; y++){
            long r = 0L;
            for(int l=0; l<L; l++){
                r = r | this.data[l][y];
            }
            r = ~r & other.data[y];
            count += Long.bitCount(r);
        }
        return (int)count;
    }
    /**
     * complexity: O(H*L)
     * @return bitCount( ~this & ~other)
     */
    public int count_outside(BinaryMatrix fast) {
        long count = 0;
        for(int y=0; y<H; y++){
            long r = 0L;
            for(int l=0; l<L; l++){
                r = r | this.data[l][y];
            }
            r = ~r & ~fast.data[y];
            count += Long.bitCount(r);
        }
        count -= H*(64-W);
        return (int)count;
    }
    public void print(String name){
        System.out.println("---------------------[ "+name+" ]--------------------");
        System.out.println("val  :"+IntStream.range(0, 8).mapToObj((l)->"\t"+l).reduce(String::concat).toString().substring(8));
        System.out.println("count:"+IntStream.range(0, (int)Math.pow(2, L)).mapToObj((v)->"\t"+count(v)).reduce(String::concat).toString().substring(8));
        for(int y=0; y<H; y++){
            for(int x=0; x<W; x++){
                int c = get(y, x);
                System.out.print(c==0 ? ".": (c==1 ? "#" : c));
            }
            System.out.print("|");
            for(int x=W; x<64; x++){
                int c = get(y, x);
                System.out.print(c==0 ? ".": (c==1 ? "#" : c));
            }
            System.out.println();
        }
    }

    @Override
    public String toString() {
        return "{" + "L=" + L + ", W=" + W + ", H=" + H + ", data=" + ("["+Arrays.stream(data).map((l)->Arrays.toString(l)).reduce("", (a,e)->a+","+e).substring(1)+"]") + '}';
    }
    
    
    public static void main(String[] args) {
        IntegerMatrix a = new IntegerMatrix(3,10, 5, 
            "...1......",
            ".12221....",
            "1234321...",
            ".12221....",
            "...1......"
        );
        BinaryMatrix b = new BinaryMatrix(10, 5, 
            "......1...",
            "....11111.",
            "...1111111",
            "....11111.",
            "......1..."
        );

        a.print("a");
        b.print("b");
        a.union(b).print("union");
        a.exclusion(b).print("exclusion");
        a.interc(b).print("interc");
        System.out.println(IntStream.range(0, 8).mapToObj((l)->"\t"+l).reduce(String::concat).toString().substring(8));
        System.out.println(IntStream.range(0, 8).mapToObj((l)->"\t"+a.count_interc(b,l)).reduce(String::concat).toString().substring(8));

        a.left(b).print("left");
        System.out.println(IntStream.range(0, 8).mapToObj((l)->"\t"+l).reduce(String::concat).toString().substring(8));
        System.out.println(IntStream.range(0, 8).mapToObj((l)->"\t"+a.count_left(b,l)).reduce(String::concat).toString().substring(8));

        a.rigth(b).print("rigth");
        System.out.println("count = "+a.count_rigth(b));
        a.outside(b).print("outside");
        System.out.println("count = "+a.count_outside(b));
        
    }
    
}
