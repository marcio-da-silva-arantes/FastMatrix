/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hobby.fast.matrix;

import java.io.Serializable;
import java.util.Arrays;

/**
 *
 * @author Marcio
 */
public class BinaryMatrix implements Serializable{
    public final int W, H;
    protected final long data[];  //store the data on bits
    public BinaryMatrix(int W, int H) {
        if(W>64 || W<0){
            throw new IllegalArgumentException("W invalid: "+W);
        }
        this.W = W;
        this.H = H;
        this.data = new long[H];
    }

    public BinaryMatrix(int W, int H, String...data) {
        this(W,H);
        for(int y=0; y<H; y++){
            for(int x=0; x<W; x++){
                if(data[y].charAt(x)=='1'){
                    enable(y, x);
                }
            }
        }
    }
    
    /**
     * complexity: O(1)
     */
    public void enable(int y, int x) {
        this.data[y] = this.data[y] | 0x1L<<x;
        //System.out.printf("set(%2d,%2d,%2d) -> %s\n", y, x, i, Long.toBinaryString(this.rows[i][y]));
    }
    /**
     * complexity: O(1)
     */
    public void disable(int y, int x) {
        this.data[y] = this.data[y] & ~(0x1L<<x);
    }
    /**
     * complexity: O(1)
     * @return true if is enable or false otherwise
     */
    public boolean get(int y, int x) {
        long c = this.data[y] >> x & 0x1L;
        //System.out.printf("get(%2d,%2d,%2d) -> %s\n", y, x, i, Long.toBinaryString(c));
        return c>0;
    }
    
    /**
     * complexity: O(H)
     * @return this | other
     */
    public BinaryMatrix union(BinaryMatrix other) {
        BinaryMatrix inter = new BinaryMatrix(W, H);
        for(int y=0; y<H; y++){
            inter.data[y] = this.data[y] | other.data[y];
        }
        return inter;
    }
    /**
     * complexity: O(H)
     * @return this ^ other
     */
    public BinaryMatrix exclusion(BinaryMatrix other) {
        BinaryMatrix inter = new BinaryMatrix(W, H);
        for(int y=0; y<H; y++){
            inter.data[y] = this.data[y] ^ other.data[y];
        }
        return inter;
    }
    
    /**
     * complexity: O(H)
     * @return this & other
     */
    public BinaryMatrix interc(BinaryMatrix other) {
        BinaryMatrix inter = new BinaryMatrix(W, H);
        for(int y=0; y<H; y++){
            inter.data[y] = this.data[y] & other.data[y];
        }
        return inter;
    }
    /**
     * complexity: O(H)
     * @return this & ~other
     */
    public BinaryMatrix left(BinaryMatrix other) {
        BinaryMatrix inter = new BinaryMatrix(W, H);
        for(int y=0; y<H; y++){
            inter.data[y] = this.data[y] & ~other.data[y];
        }
        return inter;
    }
    /**
     * complexity: O(H)
     * @return ~this & other
     */
    public BinaryMatrix rigth(BinaryMatrix other) {
        BinaryMatrix inter = new BinaryMatrix(W, H);
        for(int y=0; y<H; y++){
            inter.data[y] = ~this.data[y] & other.data[y];
        }
        return inter;
    }
    /**
     * complexity: O(H)
     * @return ~this & ~other
     */
    public BinaryMatrix outside(BinaryMatrix other) {
        BinaryMatrix inter = new BinaryMatrix(W, H);
        for(int y=0; y<H; y++){
            inter.data[y] = ~this.data[y] & ~other.data[y] & ~(-0x1L<<W);
        }
        return inter;
    }
     /**
      * complexity: O(H)
     * @return bitCount( this )
     */
    public int count(){
        long count = 0;
        for(int y=0; y<H; y++){
            count += Long.bitCount(this.data[y]);
        }
        return (int)count;
    }
    /**
     * complexity: O(H)
     * @return bitCount( this & other)
     */
    public int count_interc(BinaryMatrix other) {
        long count = 0;
        for(int y=0; y<H; y++){
            long r = this.data[y] & other.data[y];
            count += Long.bitCount(r);
        }
        return (int)count;
    }
    /**
     * complexity: O(H)
     * @return bitCount( this & ~ other)
     */
    public int count_left(BinaryMatrix other) {
        long count = 0;
        for(int y=0; y<H; y++){
            long r = this.data[y] & ~other.data[y];
            count += Long.bitCount(r);
        }
        return (int)count;
    }
    /**
     * complexity: O(H)
     * @return bitCount( ~this & other)
     */
    public int count_rigth(BinaryMatrix other) {
        long count = 0;
        for(int y=0; y<H; y++){
            long r = ~this.data[y] & other.data[y];
            count += Long.bitCount(r);
        }
        return (int)count;
    }
    /**
     * complexity: O(H)
     * @return bitCount( ~this & ~other)
     */
    public int count_outside(BinaryMatrix fast) {
        long count = 0;
        for(int y=0; y<H; y++){
            long r = ~this.data[y] & ~fast.data[y];
            count += Long.bitCount(r);
        }
        count -= H*(64-W);
        return (int)count;
    }
    public void print(String name){
        System.out.println("---------------------[ "+name+" : count = "+count()+" ]--------------------");
        for(int y=0; y<H; y++){
            for(int x=0; x<W; x++){
                System.out.print(get(y, x) ? "#": ".");
            }
            System.out.print("|");
            for(int x=W; x<64; x++){
                System.out.print(get(y, x) ? "#": ".");
            }
            System.out.println();
        }
    }
    
    @Override
    public String toString() {       
        return "{" + "W=" + W + ", H=" + H + ", data=" + Arrays.toString(data) + '}';
    }
    
    public static void main(String[] args) {
        BinaryMatrix a = new BinaryMatrix(10, 5, 
            "...1......",
            ".11111....",
            "1111111...",
            ".11111....",
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
        System.out.println("count = "+a.count_interc(b));
        a.left(b).print("left");
        System.out.println("count = "+a.count_left(b));
        a.rigth(b).print("rigth");
        System.out.println("count = "+a.count_rigth(b));
        a.outside(b).print("outside");
        System.out.println("count = "+a.count_outside(b));
        
        int val = 13;
        int l=0;
        while(l<6){
            int r = (val & 0x1);
            System.out.printf("%d -> %d | %32s -> %32s\n", val, r, Integer.toBinaryString(val), Integer.toBinaryString(r));
            val = val>>1;
            l++;
        }
    }
}





