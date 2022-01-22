/***** Lobxxx Translate Finished ******/
/*
 * Copyright (c) 1999, 2007, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 */

package java.math;

/**
 * A simple bit sieve used for finding prime number candidates. Allows setting
 * and clearing of bits in a storage array. The size of the sieve is assumed to
 * be constant to reduce overhead. All the bits of a new bitSieve are zero, and
 * bits are removed from it by setting them.
 *
 * To reduce storage space and increase efficiency, no even numbers are
 * represented in the sieve (each bit in the sieve represents an odd number).
 * The relationship between the index of a bit and the number it represents is
 * given by
 * N = offset + (2*index + 1);
 * Where N is the integer represented by a bit in the sieve, offset is some
 * even integer offset indicating where the sieve begins, and index is the
 * index of a bit in the sieve array.
 *
 * <p>
 *  用于寻找素数候选的简单位筛。允许在存储阵列中设置和清除位。筛的尺寸被假定为恒定以减少开销。一个新的bitSieve的所有位都为零,通过设置它们来删除位。
 * 
 *  为了减少存储空间和提高效率,在筛中不表示偶数(筛中的每个位表示奇数)。
 * 位的索引与其表示的数字之间的关系由N = offset +(2 * index + 1)给出;其中N是由筛中的位表示的整数,offset是指示筛开始位置的一些偶数整数偏移,并且index是筛阵列中的位的
 * 索引。
 *  为了减少存储空间和提高效率,在筛中不表示偶数(筛中的每个位表示奇数)。
 * 
 * 
 * @see     BigInteger
 * @author  Michael McCloskey
 * @since   1.3
 */
class BitSieve {
    /**
     * Stores the bits in this bitSieve.
     * <p>
     *  将比特存储在此bitSieve。
     * 
     */
    private long bits[];

    /**
     * Length is how many bits this sieve holds.
     * <p>
     *  长度是这个筛所持有的位数。
     * 
     */
    private int length;

    /**
     * A small sieve used to filter out multiples of small primes in a search
     * sieve.
     * <p>
     *  用于筛选搜索筛中多个小素数的小筛子。
     * 
     */
    private static BitSieve smallSieve = new BitSieve();

    /**
     * Construct a "small sieve" with a base of 0.  This constructor is
     * used internally to generate the set of "small primes" whose multiples
     * are excluded from sieves generated by the main (package private)
     * constructor, BitSieve(BigInteger base, int searchLen).  The length
     * of the sieve generated by this constructor was chosen for performance;
     * it controls a tradeoff between how much time is spent constructing
     * other sieves, and how much time is wasted testing composite candidates
     * for primality.  The length was chosen experimentally to yield good
     * performance.
     * <p>
     * 构造一个以0为底的"小筛子"。
     * 这个构造函数在内部用于生成一组"小素数",它们的倍数不包括在由main(package private)构造函数BitSieve(BigInteger base,int searchLen)生成的筛子中
     * , 。
     * 构造一个以0为底的"小筛子"。选择由该构造器产生的筛的长度以用于性能;它控制在构造其他筛子花费多少时间和浪费测试复合候选对原始性的时间之间的权衡。通过实验选择长度以产生良好的性能。
     * 
     */
    private BitSieve() {
        length = 150 * 64;
        bits = new long[(unitIndex(length - 1) + 1)];

        // Mark 1 as composite
        set(0);
        int nextIndex = 1;
        int nextPrime = 3;

        // Find primes and remove their multiples from sieve
        do {
            sieveSingle(length, nextIndex + nextPrime, nextPrime);
            nextIndex = sieveSearch(length, nextIndex + 1);
            nextPrime = 2*nextIndex + 1;
        } while((nextIndex > 0) && (nextPrime < length));
    }

    /**
     * Construct a bit sieve of searchLen bits used for finding prime number
     * candidates. The new sieve begins at the specified base, which must
     * be even.
     * <p>
     *  构造用于查找素数候选的searchLen位的位筛。新筛子从指定的基数开始,必须为偶数。
     * 
     */
    BitSieve(BigInteger base, int searchLen) {
        /*
         * Candidates are indicated by clear bits in the sieve. As a candidates
         * nonprimality is calculated, a bit is set in the sieve to eliminate
         * it. To reduce storage space and increase efficiency, no even numbers
         * are represented in the sieve (each bit in the sieve represents an
         * odd number).
         * <p>
         *  候选者通过筛子中的清除位来指示。作为候选者,计算非主要性,在筛中设置位以消除它。为了减少存储空间和提高效率,在筛中不表示偶数(筛中的每个位表示奇数)。
         * 
         */
        bits = new long[(unitIndex(searchLen-1) + 1)];
        length = searchLen;
        int start = 0;

        int step = smallSieve.sieveSearch(smallSieve.length, start);
        int convertedStep = (step *2) + 1;

        // Construct the large sieve at an even offset specified by base
        MutableBigInteger b = new MutableBigInteger(base);
        MutableBigInteger q = new MutableBigInteger();
        do {
            // Calculate base mod convertedStep
            start = b.divideOneWord(convertedStep, q);

            // Take each multiple of step out of sieve
            start = convertedStep - start;
            if (start%2 == 0)
                start += convertedStep;
            sieveSingle(searchLen, (start-1)/2, convertedStep);

            // Find next prime from small sieve
            step = smallSieve.sieveSearch(smallSieve.length, step+1);
            convertedStep = (step *2) + 1;
        } while (step > 0);
    }

    /**
     * Given a bit index return unit index containing it.
     * <p>
     *  给定包含它的位索引返回单元索引。
     * 
     */
    private static int unitIndex(int bitIndex) {
        return bitIndex >>> 6;
    }

    /**
     * Return a unit that masks the specified bit in its unit.
     * <p>
     *  返回一个屏蔽其单位中指定位的单位。
     * 
     */
    private static long bit(int bitIndex) {
        return 1L << (bitIndex & ((1<<6) - 1));
    }

    /**
     * Get the value of the bit at the specified index.
     * <p>
     *  获取指定索引处的位的值。
     * 
     */
    private boolean get(int bitIndex) {
        int unitIndex = unitIndex(bitIndex);
        return ((bits[unitIndex] & bit(bitIndex)) != 0);
    }

    /**
     * Set the bit at the specified index.
     * <p>
     *  将位设置为指定的索引。
     * 
     */
    private void set(int bitIndex) {
        int unitIndex = unitIndex(bitIndex);
        bits[unitIndex] |= bit(bitIndex);
    }

    /**
     * This method returns the index of the first clear bit in the search
     * array that occurs at or after start. It will not search past the
     * specified limit. It returns -1 if there is no such clear bit.
     * <p>
     *  此方法返回在开始时或之后发生的搜索数组中第一个清除位的索引。它不会搜索超过指定的限制。如果没有这样的清除位,则返回-1。
     * 
     */
    private int sieveSearch(int limit, int start) {
        if (start >= limit)
            return -1;

        int index = start;
        do {
            if (!get(index))
                return index;
            index++;
        } while(index < limit-1);
        return -1;
    }

    /**
     * Sieve a single set of multiples out of the sieve. Begin to remove
     * multiples of the specified step starting at the specified start index,
     * up to the specified limit.
     * <p>
     * 从筛子筛选单组多组分。开始从指定的起始索引开始删除指定步​​骤的倍数,直到指定的限制。
     * 
     */
    private void sieveSingle(int limit, int start, int step) {
        while(start < limit) {
            set(start);
            start += step;
        }
    }

    /**
     * Test probable primes in the sieve and return successful candidates.
     * <p>
     *  在筛子中测试可能的素数并返回成功的候选者。
     */
    BigInteger retrieve(BigInteger initValue, int certainty, java.util.Random random) {
        // Examine the sieve one long at a time to find possible primes
        int offset = 1;
        for (int i=0; i<bits.length; i++) {
            long nextLong = ~bits[i];
            for (int j=0; j<64; j++) {
                if ((nextLong & 1) == 1) {
                    BigInteger candidate = initValue.add(
                                           BigInteger.valueOf(offset));
                    if (candidate.primeToCertainty(certainty, random))
                        return candidate;
                }
                nextLong >>>= 1;
                offset+=2;
            }
        }
        return null;
    }
}
