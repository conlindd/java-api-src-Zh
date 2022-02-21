/***** Lobxxx Translate Finished ******/
/*
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

/*
 *
 *
 *
 *
 *
 * Written by Doug Lea with assistance from members of JCP JSR-166
 * Expert Group and released to the public domain, as explained at
 * http://creativecommons.org/publicdomain/zero/1.0/
 * <p>
 *  由Doug Lea在JCP JSR-166专家组成员的帮助下撰写,并发布到公共领域,如http://creativecommons.org/publicdomain/zero/1.0/
 * 
 */

package java.util.concurrent;

import java.io.ObjectStreamField;
import java.util.Random;
import java.util.Spliterator;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.DoubleConsumer;
import java.util.function.IntConsumer;
import java.util.function.LongConsumer;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.StreamSupport;

/**
 * A random number generator isolated to the current thread.  Like the
 * global {@link java.util.Random} generator used by the {@link
 * java.lang.Math} class, a {@code ThreadLocalRandom} is initialized
 * with an internally generated seed that may not otherwise be
 * modified. When applicable, use of {@code ThreadLocalRandom} rather
 * than shared {@code Random} objects in concurrent programs will
 * typically encounter much less overhead and contention.  Use of
 * {@code ThreadLocalRandom} is particularly appropriate when multiple
 * tasks (for example, each a {@link ForkJoinTask}) use random numbers
 * in parallel in thread pools.
 *
 * <p>Usages of this class should typically be of the form:
 * {@code ThreadLocalRandom.current().nextX(...)} (where
 * {@code X} is {@code Int}, {@code Long}, etc).
 * When all usages are of this form, it is never possible to
 * accidently share a {@code ThreadLocalRandom} across multiple threads.
 *
 * <p>This class also provides additional commonly used bounded random
 * generation methods.
 *
 * <p>Instances of {@code ThreadLocalRandom} are not cryptographically
 * secure.  Consider instead using {@link java.security.SecureRandom}
 * in security-sensitive applications. Additionally,
 * default-constructed instances do not use a cryptographically random
 * seed unless the {@linkplain System#getProperty system property}
 * {@code java.util.secureRandomSeed} is set to {@code true}.
 *
 * <p>
 *  一个随机数发生器隔离到当前线程。
 * 与{@link java.lang.Math}类使用的全局{@link java.util.Random}生成器类似,{@code ThreadLocalRandom}用内部生成的种子初始化,否则不能被
 * 修改。
 *  一个随机数发生器隔离到当前线程。如果适用,在并发程序中使用{@code ThreadLocalRandom}而不是共享的{@code Random}对象通常会遇到更少的开销和争用。
 * 当多个任务(例如,每个{@link ForkJoinTask})在线程池中并行使用随机数时,使用{@code ThreadLocalRandom}是特别合适的。
 * 
 *  <p>这个类的用法通常应该是：{@code ThreadLocalRandom.current()。
 * nextX(...)}(其中{@code X}是{@code Int},{@code Long}等等)。
 * 当所有的用法都是这种形式时,永远不可能意外地跨多个线程共享一个{@code ThreadLocalRandom}。
 * 
 *  <p>此类还提供了其他常用的有界随机生成方法。
 * 
 * <p> {@code ThreadLocalRandom}的实例不是加密安全的。请考虑在安全敏感的应用程序中使用{@link java.security.SecureRandom}。
 * 此外,默认构造的实例不使用加密随机种子,除非{@linkplain System#getProperty系统属性} {@code java.util.secureRandomSeed}设置为{@code true}
 * 。
 * <p> {@code ThreadLocalRandom}的实例不是加密安全的。请考虑在安全敏感的应用程序中使用{@link java.security.SecureRandom}。
 * 
 * 
 * @since 1.7
 * @author Doug Lea
 */
public class ThreadLocalRandom extends Random {
    /*
     * This class implements the java.util.Random API (and subclasses
     * Random) using a single static instance that accesses random
     * number state held in class Thread (primarily, field
     * threadLocalRandomSeed). In doing so, it also provides a home
     * for managing package-private utilities that rely on exactly the
     * same state as needed to maintain the ThreadLocalRandom
     * instances. We leverage the need for an initialization flag
     * field to also use it as a "probe" -- a self-adjusting thread
     * hash used for contention avoidance, as well as a secondary
     * simpler (xorShift) random seed that is conservatively used to
     * avoid otherwise surprising users by hijacking the
     * ThreadLocalRandom sequence.  The dual use is a marriage of
     * convenience, but is a simple and efficient way of reducing
     * application-level overhead and footprint of most concurrent
     * programs.
     *
     * Even though this class subclasses java.util.Random, it uses the
     * same basic algorithm as java.util.SplittableRandom.  (See its
     * internal documentation for explanations, which are not repeated
     * here.)  Because ThreadLocalRandoms are not splittable
     * though, we use only a single 64bit gamma.
     *
     * Because this class is in a different package than class Thread,
     * field access methods use Unsafe to bypass access control rules.
     * To conform to the requirements of the Random superclass
     * constructor, the common static ThreadLocalRandom maintains an
     * "initialized" field for the sake of rejecting user calls to
     * setSeed while still allowing a call from constructor.  Note
     * that serialization is completely unnecessary because there is
     * only a static singleton.  But we generate a serial form
     * containing "rnd" and "initialized" fields to ensure
     * compatibility across versions.
     *
     * Implementations of non-core methods are mostly the same as in
     * SplittableRandom, that were in part derived from a previous
     * version of this class.
     *
     * The nextLocalGaussian ThreadLocal supports the very rarely used
     * nextGaussian method by providing a holder for the second of a
     * pair of them. As is true for the base class version of this
     * method, this time/space tradeoff is probably never worthwhile,
     * but we provide identical statistical properties.
     * <p>
     *  这个类实现了java.util.Random API(和子类Random),使用一个访问Thread类(主要是字段threadLocalRandomSeed)中保存的随机数状态的单个静态实例。
     * 这样做,它还提供了一个管理包私有实用程序的家,这些实用程序依赖于维护ThreadLocalRandom实例所需的完全相同的状态。
     * 我们利用对初始化标志字段的需要,还将其用作"探测器" - 用于争用避免的自调整线程哈希以及保守地用于避免另外令人惊讶的次要简单(xorShift)随机种子用户通过劫持ThreadLocalRandom序
     * 列。
     * 这样做,它还提供了一个管理包私有实用程序的家,这些实用程序依赖于维护ThreadLocalRandom实例所需的完全相同的状态。
     * 双重用途是方便的婚姻,但是是减少大多数并发程序的应用程序级开销和占用空间的简单而有效的方法。
     * 
     *  即使这个类子类化java.util.Random,它使用与java.util.SplittableRandom相同的基本算法。 (有关说明,请参阅其内部文档,因为这里不再重复。
     * )因为ThreadLocalRandoms不是可拆分的,所以我们只使用一个64位的gamma。
     * 
     * 因为这个类在与Thread类不同的包中,所以字段访问方法使用Unsafe来绕过访问控制规则。
     * 为了符合随机超类构造函数的要求,公共静态ThreadLocalRandom维护一个"已初始化"字段,以便拒绝对setSeed的用户调用,同时仍然允许从构造函数调用。
     * 注意,序列化是完全不必要的,因为只有一个静态单例。但是我们生成一个包含"rnd"和"initialized"字段的串行格式,以确保版本之间的兼容性。
     * 
     *  非核心方法的实现与SplittableRandom中的大部分相同,部分派生自该类的先前版本。
     * 
     *  下一个本地高斯螺纹本地支持非常少用的下一个高斯方法,通过为它们的一对中的第二个提供保持器。对于这个方法的基类版本是真的,这个时间/空间权衡可能是不值得的,但我们提供相同的统计属性。
     * 
     */

    /** Generates per-thread initialization/probe field */
    private static final AtomicInteger probeGenerator =
        new AtomicInteger();

    /**
     * The next seed for default constructors.
     * <p>
     *  默认构造函数的下一个种子。
     * 
     */
    private static final AtomicLong seeder = new AtomicLong(initialSeed());

    private static long initialSeed() {
        String pp = java.security.AccessController.doPrivileged(
                new sun.security.action.GetPropertyAction(
                        "java.util.secureRandomSeed"));
        if (pp != null && pp.equalsIgnoreCase("true")) {
            byte[] seedBytes = java.security.SecureRandom.getSeed(8);
            long s = (long)(seedBytes[0]) & 0xffL;
            for (int i = 1; i < 8; ++i)
                s = (s << 8) | ((long)(seedBytes[i]) & 0xffL);
            return s;
        }
        return (mix64(System.currentTimeMillis()) ^
                mix64(System.nanoTime()));
    }

    /**
     * The seed increment
     * <p>
     *  种子增量
     * 
     */
    private static final long GAMMA = 0x9e3779b97f4a7c15L;

    /**
     * The increment for generating probe values
     * <p>
     *  生成探针值的增量
     * 
     */
    private static final int PROBE_INCREMENT = 0x9e3779b9;

    /**
     * The increment of seeder per new instance
     * <p>
     *  每个新实例的seeder增量
     * 
     */
    private static final long SEEDER_INCREMENT = 0xbb67ae8584caa73bL;

    // Constants from SplittableRandom
    private static final double DOUBLE_UNIT = 0x1.0p-53;  // 1.0  / (1L << 53)
    private static final float  FLOAT_UNIT  = 0x1.0p-24f; // 1.0f / (1 << 24)

    /** Rarely-used holder for the second of a pair of Gaussians */
    private static final ThreadLocal<Double> nextLocalGaussian =
        new ThreadLocal<Double>();

    private static long mix64(long z) {
        z = (z ^ (z >>> 33)) * 0xff51afd7ed558ccdL;
        z = (z ^ (z >>> 33)) * 0xc4ceb9fe1a85ec53L;
        return z ^ (z >>> 33);
    }

    private static int mix32(long z) {
        z = (z ^ (z >>> 33)) * 0xff51afd7ed558ccdL;
        return (int)(((z ^ (z >>> 33)) * 0xc4ceb9fe1a85ec53L) >>> 32);
    }

    /**
     * Field used only during singleton initialization.
     * True when constructor completes.
     * <p>
     *  仅在单例初始化期间使用字段。构造函数完成时为True。
     * 
     */
    boolean initialized;

    /** Constructor used only for static singleton */
    private ThreadLocalRandom() {
        initialized = true; // false during super() call
    }

    /** The common ThreadLocalRandom */
    static final ThreadLocalRandom instance = new ThreadLocalRandom();

    /**
     * Initialize Thread fields for the current thread.  Called only
     * when Thread.threadLocalRandomProbe is zero, indicating that a
     * thread local seed value needs to be generated. Note that even
     * though the initialization is purely thread-local, we need to
     * rely on (static) atomic generators to initialize the values.
     * <p>
     * 初始化当前线程的线程字段。只有当Thread.threadLocalRandomProbe为零时才调用,表示需要生成线程本地种子值。
     * 注意,即使初始化是纯线程本地的,我们需要依靠(静态)原子发生器来初始化值。
     * 
     */
    static final void localInit() {
        int p = probeGenerator.addAndGet(PROBE_INCREMENT);
        int probe = (p == 0) ? 1 : p; // skip 0
        long seed = mix64(seeder.getAndAdd(SEEDER_INCREMENT));
        Thread t = Thread.currentThread();
        UNSAFE.putLong(t, SEED, seed);
        UNSAFE.putInt(t, PROBE, probe);
    }

    /**
     * Returns the current thread's {@code ThreadLocalRandom}.
     *
     * <p>
     *  返回当前线程的{@code ThreadLocalRandom}。
     * 
     * 
     * @return the current thread's {@code ThreadLocalRandom}
     */
    public static ThreadLocalRandom current() {
        if (UNSAFE.getInt(Thread.currentThread(), PROBE) == 0)
            localInit();
        return instance;
    }

    /**
     * Throws {@code UnsupportedOperationException}.  Setting seeds in
     * this generator is not supported.
     *
     * <p>
     *  引发{@code UnsupportedOperationException}。不支持在此生成器中设置种子。
     * 
     * 
     * @throws UnsupportedOperationException always
     */
    public void setSeed(long seed) {
        // only allow call from super() constructor
        if (initialized)
            throw new UnsupportedOperationException();
    }

    final long nextSeed() {
        Thread t; long r; // read and update per-thread seed
        UNSAFE.putLong(t = Thread.currentThread(), SEED,
                       r = UNSAFE.getLong(t, SEED) + GAMMA);
        return r;
    }

    // We must define this, but never use it.
    protected int next(int bits) {
        return (int)(mix64(nextSeed()) >>> (64 - bits));
    }

    // IllegalArgumentException messages
    static final String BadBound = "bound must be positive";
    static final String BadRange = "bound must be greater than origin";
    static final String BadSize  = "size must be non-negative";

    /**
     * The form of nextLong used by LongStream Spliterators.  If
     * origin is greater than bound, acts as unbounded form of
     * nextLong, else as bounded form.
     *
     * <p>
     *  LongStream Spliterators使用的nextLong的形式。如果origin大于bound,则作为nextLong的无界形式,否则作为有界形式。
     * 
     * 
     * @param origin the least value, unless greater than bound
     * @param bound the upper bound (exclusive), must not equal origin
     * @return a pseudorandom value
     */
    final long internalNextLong(long origin, long bound) {
        long r = mix64(nextSeed());
        if (origin < bound) {
            long n = bound - origin, m = n - 1;
            if ((n & m) == 0L)  // power of two
                r = (r & m) + origin;
            else if (n > 0L) {  // reject over-represented candidates
                for (long u = r >>> 1;            // ensure nonnegative
                     u + m - (r = u % n) < 0L;    // rejection check
                     u = mix64(nextSeed()) >>> 1) // retry
                    ;
                r += origin;
            }
            else {              // range not representable as long
                while (r < origin || r >= bound)
                    r = mix64(nextSeed());
            }
        }
        return r;
    }

    /**
     * The form of nextInt used by IntStream Spliterators.
     * Exactly the same as long version, except for types.
     *
     * <p>
     *  IntStream Spliterators使用的nextInt的形式。完全与长版本相同,除了类型。
     * 
     * 
     * @param origin the least value, unless greater than bound
     * @param bound the upper bound (exclusive), must not equal origin
     * @return a pseudorandom value
     */
    final int internalNextInt(int origin, int bound) {
        int r = mix32(nextSeed());
        if (origin < bound) {
            int n = bound - origin, m = n - 1;
            if ((n & m) == 0)
                r = (r & m) + origin;
            else if (n > 0) {
                for (int u = r >>> 1;
                     u + m - (r = u % n) < 0;
                     u = mix32(nextSeed()) >>> 1)
                    ;
                r += origin;
            }
            else {
                while (r < origin || r >= bound)
                    r = mix32(nextSeed());
            }
        }
        return r;
    }

    /**
     * The form of nextDouble used by DoubleStream Spliterators.
     *
     * <p>
     *  DoubleStream Spliterators使用的nextDouble的形式。
     * 
     * 
     * @param origin the least value, unless greater than bound
     * @param bound the upper bound (exclusive), must not equal origin
     * @return a pseudorandom value
     */
    final double internalNextDouble(double origin, double bound) {
        double r = (nextLong() >>> 11) * DOUBLE_UNIT;
        if (origin < bound) {
            r = r * (bound - origin) + origin;
            if (r >= bound) // correct for rounding
                r = Double.longBitsToDouble(Double.doubleToLongBits(bound) - 1);
        }
        return r;
    }

    /**
     * Returns a pseudorandom {@code int} value.
     *
     * <p>
     *  返回伪随机{@code int}值。
     * 
     * 
     * @return a pseudorandom {@code int} value
     */
    public int nextInt() {
        return mix32(nextSeed());
    }

    /**
     * Returns a pseudorandom {@code int} value between zero (inclusive)
     * and the specified bound (exclusive).
     *
     * <p>
     *  返回零(包括)和指定的bound(exclusive)之间的伪随机{@code int}值。
     * 
     * 
     * @param bound the upper bound (exclusive).  Must be positive.
     * @return a pseudorandom {@code int} value between zero
     *         (inclusive) and the bound (exclusive)
     * @throws IllegalArgumentException if {@code bound} is not positive
     */
    public int nextInt(int bound) {
        if (bound <= 0)
            throw new IllegalArgumentException(BadBound);
        int r = mix32(nextSeed());
        int m = bound - 1;
        if ((bound & m) == 0) // power of two
            r &= m;
        else { // reject over-represented candidates
            for (int u = r >>> 1;
                 u + m - (r = u % bound) < 0;
                 u = mix32(nextSeed()) >>> 1)
                ;
        }
        return r;
    }

    /**
     * Returns a pseudorandom {@code int} value between the specified
     * origin (inclusive) and the specified bound (exclusive).
     *
     * <p>
     *  返回指定原点(包括)和指定的bound(exclusive)之间的伪随机{@code int}值。
     * 
     * 
     * @param origin the least value returned
     * @param bound the upper bound (exclusive)
     * @return a pseudorandom {@code int} value between the origin
     *         (inclusive) and the bound (exclusive)
     * @throws IllegalArgumentException if {@code origin} is greater than
     *         or equal to {@code bound}
     */
    public int nextInt(int origin, int bound) {
        if (origin >= bound)
            throw new IllegalArgumentException(BadRange);
        return internalNextInt(origin, bound);
    }

    /**
     * Returns a pseudorandom {@code long} value.
     *
     * <p>
     *  返回伪随机{@code long}值。
     * 
     * 
     * @return a pseudorandom {@code long} value
     */
    public long nextLong() {
        return mix64(nextSeed());
    }

    /**
     * Returns a pseudorandom {@code long} value between zero (inclusive)
     * and the specified bound (exclusive).
     *
     * <p>
     *  返回零(包括)和指定的bound(exclusive)之间的伪随机{@code long}值。
     * 
     * 
     * @param bound the upper bound (exclusive).  Must be positive.
     * @return a pseudorandom {@code long} value between zero
     *         (inclusive) and the bound (exclusive)
     * @throws IllegalArgumentException if {@code bound} is not positive
     */
    public long nextLong(long bound) {
        if (bound <= 0)
            throw new IllegalArgumentException(BadBound);
        long r = mix64(nextSeed());
        long m = bound - 1;
        if ((bound & m) == 0L) // power of two
            r &= m;
        else { // reject over-represented candidates
            for (long u = r >>> 1;
                 u + m - (r = u % bound) < 0L;
                 u = mix64(nextSeed()) >>> 1)
                ;
        }
        return r;
    }

    /**
     * Returns a pseudorandom {@code long} value between the specified
     * origin (inclusive) and the specified bound (exclusive).
     *
     * <p>
     *  返回指定的原点(包括)和指定的bound(exclusive)之间的伪随机{@code long}值。
     * 
     * 
     * @param origin the least value returned
     * @param bound the upper bound (exclusive)
     * @return a pseudorandom {@code long} value between the origin
     *         (inclusive) and the bound (exclusive)
     * @throws IllegalArgumentException if {@code origin} is greater than
     *         or equal to {@code bound}
     */
    public long nextLong(long origin, long bound) {
        if (origin >= bound)
            throw new IllegalArgumentException(BadRange);
        return internalNextLong(origin, bound);
    }

    /**
     * Returns a pseudorandom {@code double} value between zero
     * (inclusive) and one (exclusive).
     *
     * <p>
     *  返回零(包括)和一(不包括)之间的伪随机{@code double}值。
     * 
     * 
     * @return a pseudorandom {@code double} value between zero
     *         (inclusive) and one (exclusive)
     */
    public double nextDouble() {
        return (mix64(nextSeed()) >>> 11) * DOUBLE_UNIT;
    }

    /**
     * Returns a pseudorandom {@code double} value between 0.0
     * (inclusive) and the specified bound (exclusive).
     *
     * <p>
     * 返回0.0(包括)和指定的bound(exclusive)之间的伪随机{@code double}值。
     * 
     * 
     * @param bound the upper bound (exclusive).  Must be positive.
     * @return a pseudorandom {@code double} value between zero
     *         (inclusive) and the bound (exclusive)
     * @throws IllegalArgumentException if {@code bound} is not positive
     */
    public double nextDouble(double bound) {
        if (!(bound > 0.0))
            throw new IllegalArgumentException(BadBound);
        double result = (mix64(nextSeed()) >>> 11) * DOUBLE_UNIT * bound;
        return (result < bound) ?  result : // correct for rounding
            Double.longBitsToDouble(Double.doubleToLongBits(bound) - 1);
    }

    /**
     * Returns a pseudorandom {@code double} value between the specified
     * origin (inclusive) and bound (exclusive).
     *
     * <p>
     *  返回指定原点(包括)和bound(exclusive)之间的伪随机{@code double}值。
     * 
     * 
     * @param origin the least value returned
     * @param bound the upper bound (exclusive)
     * @return a pseudorandom {@code double} value between the origin
     *         (inclusive) and the bound (exclusive)
     * @throws IllegalArgumentException if {@code origin} is greater than
     *         or equal to {@code bound}
     */
    public double nextDouble(double origin, double bound) {
        if (!(origin < bound))
            throw new IllegalArgumentException(BadRange);
        return internalNextDouble(origin, bound);
    }

    /**
     * Returns a pseudorandom {@code boolean} value.
     *
     * <p>
     *  返回伪随机{@code boolean}值。
     * 
     * 
     * @return a pseudorandom {@code boolean} value
     */
    public boolean nextBoolean() {
        return mix32(nextSeed()) < 0;
    }

    /**
     * Returns a pseudorandom {@code float} value between zero
     * (inclusive) and one (exclusive).
     *
     * <p>
     *  返回零(包括)和一(排它)之间的伪随机{@code float}值。
     * 
     * 
     * @return a pseudorandom {@code float} value between zero
     *         (inclusive) and one (exclusive)
     */
    public float nextFloat() {
        return (mix32(nextSeed()) >>> 8) * FLOAT_UNIT;
    }

    public double nextGaussian() {
        // Use nextLocalGaussian instead of nextGaussian field
        Double d = nextLocalGaussian.get();
        if (d != null) {
            nextLocalGaussian.set(null);
            return d.doubleValue();
        }
        double v1, v2, s;
        do {
            v1 = 2 * nextDouble() - 1; // between -1 and 1
            v2 = 2 * nextDouble() - 1; // between -1 and 1
            s = v1 * v1 + v2 * v2;
        } while (s >= 1 || s == 0);
        double multiplier = StrictMath.sqrt(-2 * StrictMath.log(s)/s);
        nextLocalGaussian.set(new Double(v2 * multiplier));
        return v1 * multiplier;
    }

    // stream methods, coded in a way intended to better isolate for
    // maintenance purposes the small differences across forms.

    /**
     * Returns a stream producing the given {@code streamSize} number of
     * pseudorandom {@code int} values.
     *
     * <p>
     *  返回产生给定{@code streamSize}伪随机{@code int}值的流。
     * 
     * 
     * @param streamSize the number of values to generate
     * @return a stream of pseudorandom {@code int} values
     * @throws IllegalArgumentException if {@code streamSize} is
     *         less than zero
     * @since 1.8
     */
    public IntStream ints(long streamSize) {
        if (streamSize < 0L)
            throw new IllegalArgumentException(BadSize);
        return StreamSupport.intStream
            (new RandomIntsSpliterator
             (0L, streamSize, Integer.MAX_VALUE, 0),
             false);
    }

    /**
     * Returns an effectively unlimited stream of pseudorandom {@code int}
     * values.
     *
     * @implNote This method is implemented to be equivalent to {@code
     * ints(Long.MAX_VALUE)}.
     *
     * <p>
     *  返回一个有效无限的伪随机流{@code int}值。
     * 
     *  @implNote此方法实现为等效于{@code ints(Long.MAX_VALUE)}。
     * 
     * 
     * @return a stream of pseudorandom {@code int} values
     * @since 1.8
     */
    public IntStream ints() {
        return StreamSupport.intStream
            (new RandomIntsSpliterator
             (0L, Long.MAX_VALUE, Integer.MAX_VALUE, 0),
             false);
    }

    /**
     * Returns a stream producing the given {@code streamSize} number
     * of pseudorandom {@code int} values, each conforming to the given
     * origin (inclusive) and bound (exclusive).
     *
     * <p>
     *  返回生成给定{@code streamSize}伪随机{@code int}值的流,每个值都符合给定的起点(包含)和bound(exclusive)。
     * 
     * 
     * @param streamSize the number of values to generate
     * @param randomNumberOrigin the origin (inclusive) of each random value
     * @param randomNumberBound the bound (exclusive) of each random value
     * @return a stream of pseudorandom {@code int} values,
     *         each with the given origin (inclusive) and bound (exclusive)
     * @throws IllegalArgumentException if {@code streamSize} is
     *         less than zero, or {@code randomNumberOrigin}
     *         is greater than or equal to {@code randomNumberBound}
     * @since 1.8
     */
    public IntStream ints(long streamSize, int randomNumberOrigin,
                          int randomNumberBound) {
        if (streamSize < 0L)
            throw new IllegalArgumentException(BadSize);
        if (randomNumberOrigin >= randomNumberBound)
            throw new IllegalArgumentException(BadRange);
        return StreamSupport.intStream
            (new RandomIntsSpliterator
             (0L, streamSize, randomNumberOrigin, randomNumberBound),
             false);
    }

    /**
     * Returns an effectively unlimited stream of pseudorandom {@code
     * int} values, each conforming to the given origin (inclusive) and bound
     * (exclusive).
     *
     * @implNote This method is implemented to be equivalent to {@code
     * ints(Long.MAX_VALUE, randomNumberOrigin, randomNumberBound)}.
     *
     * <p>
     *  返回一个有效无限的伪随机流{@code int}值,每个值都符合给定的起点(包含)和bound(exclusive)。
     * 
     *  @implNote此方法实现为等同于{@code ints(Long.MAX_VALUE,randomNumberOrigin,randomNumberBound)}。
     * 
     * 
     * @param randomNumberOrigin the origin (inclusive) of each random value
     * @param randomNumberBound the bound (exclusive) of each random value
     * @return a stream of pseudorandom {@code int} values,
     *         each with the given origin (inclusive) and bound (exclusive)
     * @throws IllegalArgumentException if {@code randomNumberOrigin}
     *         is greater than or equal to {@code randomNumberBound}
     * @since 1.8
     */
    public IntStream ints(int randomNumberOrigin, int randomNumberBound) {
        if (randomNumberOrigin >= randomNumberBound)
            throw new IllegalArgumentException(BadRange);
        return StreamSupport.intStream
            (new RandomIntsSpliterator
             (0L, Long.MAX_VALUE, randomNumberOrigin, randomNumberBound),
             false);
    }

    /**
     * Returns a stream producing the given {@code streamSize} number of
     * pseudorandom {@code long} values.
     *
     * <p>
     *  返回生成给定{@code streamSize}伪随机数{@code long}值的流。
     * 
     * 
     * @param streamSize the number of values to generate
     * @return a stream of pseudorandom {@code long} values
     * @throws IllegalArgumentException if {@code streamSize} is
     *         less than zero
     * @since 1.8
     */
    public LongStream longs(long streamSize) {
        if (streamSize < 0L)
            throw new IllegalArgumentException(BadSize);
        return StreamSupport.longStream
            (new RandomLongsSpliterator
             (0L, streamSize, Long.MAX_VALUE, 0L),
             false);
    }

    /**
     * Returns an effectively unlimited stream of pseudorandom {@code long}
     * values.
     *
     * @implNote This method is implemented to be equivalent to {@code
     * longs(Long.MAX_VALUE)}.
     *
     * <p>
     *  返回有效无限的伪随机{@code long}值流。
     * 
     *  @implNote此方法实现为等效于{@code longs(Long.MAX_VALUE)}。
     * 
     * 
     * @return a stream of pseudorandom {@code long} values
     * @since 1.8
     */
    public LongStream longs() {
        return StreamSupport.longStream
            (new RandomLongsSpliterator
             (0L, Long.MAX_VALUE, Long.MAX_VALUE, 0L),
             false);
    }

    /**
     * Returns a stream producing the given {@code streamSize} number of
     * pseudorandom {@code long}, each conforming to the given origin
     * (inclusive) and bound (exclusive).
     *
     * <p>
     *  返回产生给定{@code streamSize}伪随机{@code long}的数据流,每个数据都符合给定的原点(包括)和bound(exclusive)。
     * 
     * 
     * @param streamSize the number of values to generate
     * @param randomNumberOrigin the origin (inclusive) of each random value
     * @param randomNumberBound the bound (exclusive) of each random value
     * @return a stream of pseudorandom {@code long} values,
     *         each with the given origin (inclusive) and bound (exclusive)
     * @throws IllegalArgumentException if {@code streamSize} is
     *         less than zero, or {@code randomNumberOrigin}
     *         is greater than or equal to {@code randomNumberBound}
     * @since 1.8
     */
    public LongStream longs(long streamSize, long randomNumberOrigin,
                            long randomNumberBound) {
        if (streamSize < 0L)
            throw new IllegalArgumentException(BadSize);
        if (randomNumberOrigin >= randomNumberBound)
            throw new IllegalArgumentException(BadRange);
        return StreamSupport.longStream
            (new RandomLongsSpliterator
             (0L, streamSize, randomNumberOrigin, randomNumberBound),
             false);
    }

    /**
     * Returns an effectively unlimited stream of pseudorandom {@code
     * long} values, each conforming to the given origin (inclusive) and bound
     * (exclusive).
     *
     * @implNote This method is implemented to be equivalent to {@code
     * longs(Long.MAX_VALUE, randomNumberOrigin, randomNumberBound)}.
     *
     * <p>
     * 返回有效无限的伪随机{@code long}值流,每个值都符合给定的起点(包含)和bound(exclusive)。
     * 
     *  @implNote此方法实现为等效于{@code longs(Long.MAX_VALUE,randomNumberOrigin,randomNumberBound)}。
     * 
     * 
     * @param randomNumberOrigin the origin (inclusive) of each random value
     * @param randomNumberBound the bound (exclusive) of each random value
     * @return a stream of pseudorandom {@code long} values,
     *         each with the given origin (inclusive) and bound (exclusive)
     * @throws IllegalArgumentException if {@code randomNumberOrigin}
     *         is greater than or equal to {@code randomNumberBound}
     * @since 1.8
     */
    public LongStream longs(long randomNumberOrigin, long randomNumberBound) {
        if (randomNumberOrigin >= randomNumberBound)
            throw new IllegalArgumentException(BadRange);
        return StreamSupport.longStream
            (new RandomLongsSpliterator
             (0L, Long.MAX_VALUE, randomNumberOrigin, randomNumberBound),
             false);
    }

    /**
     * Returns a stream producing the given {@code streamSize} number of
     * pseudorandom {@code double} values, each between zero
     * (inclusive) and one (exclusive).
     *
     * <p>
     *  返回产生给定{@code streamSize}伪随机{@code double}值的流,每个值在零(包括)和一个(不包括)之间。
     * 
     * 
     * @param streamSize the number of values to generate
     * @return a stream of {@code double} values
     * @throws IllegalArgumentException if {@code streamSize} is
     *         less than zero
     * @since 1.8
     */
    public DoubleStream doubles(long streamSize) {
        if (streamSize < 0L)
            throw new IllegalArgumentException(BadSize);
        return StreamSupport.doubleStream
            (new RandomDoublesSpliterator
             (0L, streamSize, Double.MAX_VALUE, 0.0),
             false);
    }

    /**
     * Returns an effectively unlimited stream of pseudorandom {@code
     * double} values, each between zero (inclusive) and one
     * (exclusive).
     *
     * @implNote This method is implemented to be equivalent to {@code
     * doubles(Long.MAX_VALUE)}.
     *
     * <p>
     *  返回有效无限的伪随机{@code double}值,每个值在零(包括)和一个(排除)之间。
     * 
     *  @implNote此方法实现为等效于{@code doubles(Long.MAX_VALUE)}。
     * 
     * 
     * @return a stream of pseudorandom {@code double} values
     * @since 1.8
     */
    public DoubleStream doubles() {
        return StreamSupport.doubleStream
            (new RandomDoublesSpliterator
             (0L, Long.MAX_VALUE, Double.MAX_VALUE, 0.0),
             false);
    }

    /**
     * Returns a stream producing the given {@code streamSize} number of
     * pseudorandom {@code double} values, each conforming to the given origin
     * (inclusive) and bound (exclusive).
     *
     * <p>
     *  返回产生给定{@code streamSize}伪随机{@code double}值的流,每个值都符合给定的起点(包括)和bound(exclusive)。
     * 
     * 
     * @param streamSize the number of values to generate
     * @param randomNumberOrigin the origin (inclusive) of each random value
     * @param randomNumberBound the bound (exclusive) of each random value
     * @return a stream of pseudorandom {@code double} values,
     *         each with the given origin (inclusive) and bound (exclusive)
     * @throws IllegalArgumentException if {@code streamSize} is
     *         less than zero
     * @throws IllegalArgumentException if {@code randomNumberOrigin}
     *         is greater than or equal to {@code randomNumberBound}
     * @since 1.8
     */
    public DoubleStream doubles(long streamSize, double randomNumberOrigin,
                                double randomNumberBound) {
        if (streamSize < 0L)
            throw new IllegalArgumentException(BadSize);
        if (!(randomNumberOrigin < randomNumberBound))
            throw new IllegalArgumentException(BadRange);
        return StreamSupport.doubleStream
            (new RandomDoublesSpliterator
             (0L, streamSize, randomNumberOrigin, randomNumberBound),
             false);
    }

    /**
     * Returns an effectively unlimited stream of pseudorandom {@code
     * double} values, each conforming to the given origin (inclusive) and bound
     * (exclusive).
     *
     * @implNote This method is implemented to be equivalent to {@code
     * doubles(Long.MAX_VALUE, randomNumberOrigin, randomNumberBound)}.
     *
     * <p>
     *  返回有效无限的伪随机{@code double}值流,每个值都符合给定的起点(包含)和bound(exclusive)。
     * 
     *  @implNote这个方法被实现为等同于{@code doubles(Long.MAX_VALUE,randomNumberOrigin,randomNumberBound)}。
     * 
     * 
     * @param randomNumberOrigin the origin (inclusive) of each random value
     * @param randomNumberBound the bound (exclusive) of each random value
     * @return a stream of pseudorandom {@code double} values,
     *         each with the given origin (inclusive) and bound (exclusive)
     * @throws IllegalArgumentException if {@code randomNumberOrigin}
     *         is greater than or equal to {@code randomNumberBound}
     * @since 1.8
     */
    public DoubleStream doubles(double randomNumberOrigin, double randomNumberBound) {
        if (!(randomNumberOrigin < randomNumberBound))
            throw new IllegalArgumentException(BadRange);
        return StreamSupport.doubleStream
            (new RandomDoublesSpliterator
             (0L, Long.MAX_VALUE, randomNumberOrigin, randomNumberBound),
             false);
    }

    /**
     * Spliterator for int streams.  We multiplex the four int
     * versions into one class by treating a bound less than origin as
     * unbounded, and also by treating "infinite" as equivalent to
     * Long.MAX_VALUE. For splits, it uses the standard divide-by-two
     * approach. The long and double versions of this class are
     * identical except for types.
     * <p>
     *  用于int流的分割器。我们将四个int版本复用到一个类中,通过将一个小于origin的bound作为无界处理,还将"infinite"视为等同于Long.MAX_VALUE。
     * 对于分割,它使用标准的二分法。除类型外,此类的long和double版本是相同的。
     * 
     */
    static final class RandomIntsSpliterator implements Spliterator.OfInt {
        long index;
        final long fence;
        final int origin;
        final int bound;
        RandomIntsSpliterator(long index, long fence,
                              int origin, int bound) {
            this.index = index; this.fence = fence;
            this.origin = origin; this.bound = bound;
        }

        public RandomIntsSpliterator trySplit() {
            long i = index, m = (i + fence) >>> 1;
            return (m <= i) ? null :
                new RandomIntsSpliterator(i, index = m, origin, bound);
        }

        public long estimateSize() {
            return fence - index;
        }

        public int characteristics() {
            return (Spliterator.SIZED | Spliterator.SUBSIZED |
                    Spliterator.NONNULL | Spliterator.IMMUTABLE);
        }

        public boolean tryAdvance(IntConsumer consumer) {
            if (consumer == null) throw new NullPointerException();
            long i = index, f = fence;
            if (i < f) {
                consumer.accept(ThreadLocalRandom.current().internalNextInt(origin, bound));
                index = i + 1;
                return true;
            }
            return false;
        }

        public void forEachRemaining(IntConsumer consumer) {
            if (consumer == null) throw new NullPointerException();
            long i = index, f = fence;
            if (i < f) {
                index = f;
                int o = origin, b = bound;
                ThreadLocalRandom rng = ThreadLocalRandom.current();
                do {
                    consumer.accept(rng.internalNextInt(o, b));
                } while (++i < f);
            }
        }
    }

    /**
     * Spliterator for long streams.
     * <p>
     *  长流的分离器。
     * 
     */
    static final class RandomLongsSpliterator implements Spliterator.OfLong {
        long index;
        final long fence;
        final long origin;
        final long bound;
        RandomLongsSpliterator(long index, long fence,
                               long origin, long bound) {
            this.index = index; this.fence = fence;
            this.origin = origin; this.bound = bound;
        }

        public RandomLongsSpliterator trySplit() {
            long i = index, m = (i + fence) >>> 1;
            return (m <= i) ? null :
                new RandomLongsSpliterator(i, index = m, origin, bound);
        }

        public long estimateSize() {
            return fence - index;
        }

        public int characteristics() {
            return (Spliterator.SIZED | Spliterator.SUBSIZED |
                    Spliterator.NONNULL | Spliterator.IMMUTABLE);
        }

        public boolean tryAdvance(LongConsumer consumer) {
            if (consumer == null) throw new NullPointerException();
            long i = index, f = fence;
            if (i < f) {
                consumer.accept(ThreadLocalRandom.current().internalNextLong(origin, bound));
                index = i + 1;
                return true;
            }
            return false;
        }

        public void forEachRemaining(LongConsumer consumer) {
            if (consumer == null) throw new NullPointerException();
            long i = index, f = fence;
            if (i < f) {
                index = f;
                long o = origin, b = bound;
                ThreadLocalRandom rng = ThreadLocalRandom.current();
                do {
                    consumer.accept(rng.internalNextLong(o, b));
                } while (++i < f);
            }
        }

    }

    /**
     * Spliterator for double streams.
     * <p>
     *  双流的分离器。
     * 
     */
    static final class RandomDoublesSpliterator implements Spliterator.OfDouble {
        long index;
        final long fence;
        final double origin;
        final double bound;
        RandomDoublesSpliterator(long index, long fence,
                                 double origin, double bound) {
            this.index = index; this.fence = fence;
            this.origin = origin; this.bound = bound;
        }

        public RandomDoublesSpliterator trySplit() {
            long i = index, m = (i + fence) >>> 1;
            return (m <= i) ? null :
                new RandomDoublesSpliterator(i, index = m, origin, bound);
        }

        public long estimateSize() {
            return fence - index;
        }

        public int characteristics() {
            return (Spliterator.SIZED | Spliterator.SUBSIZED |
                    Spliterator.NONNULL | Spliterator.IMMUTABLE);
        }

        public boolean tryAdvance(DoubleConsumer consumer) {
            if (consumer == null) throw new NullPointerException();
            long i = index, f = fence;
            if (i < f) {
                consumer.accept(ThreadLocalRandom.current().internalNextDouble(origin, bound));
                index = i + 1;
                return true;
            }
            return false;
        }

        public void forEachRemaining(DoubleConsumer consumer) {
            if (consumer == null) throw new NullPointerException();
            long i = index, f = fence;
            if (i < f) {
                index = f;
                double o = origin, b = bound;
                ThreadLocalRandom rng = ThreadLocalRandom.current();
                do {
                    consumer.accept(rng.internalNextDouble(o, b));
                } while (++i < f);
            }
        }
    }


    // Within-package utilities

    /*
     * Descriptions of the usages of the methods below can be found in
     * the classes that use them. Briefly, a thread's "probe" value is
     * a non-zero hash code that (probably) does not collide with
     * other existing threads with respect to any power of two
     * collision space. When it does collide, it is pseudo-randomly
     * adjusted (using a Marsaglia XorShift). The nextSecondarySeed
     * method is used in the same contexts as ThreadLocalRandom, but
     * only for transient usages such as random adaptive spin/block
     * sequences for which a cheap RNG suffices and for which it could
     * in principle disrupt user-visible statistical properties of the
     * main ThreadLocalRandom if we were to use it.
     *
     * Note: Because of package-protection issues, versions of some
     * these methods also appear in some subpackage classes.
     * <p>
     * 下面的方法的用法的描述可以在使用它们的类中找到。简而言之,线程的"探测"值是一个非零的哈希码,(相对于两个冲突空间的任何幂,它(可能)不会与其他现有线程发生冲突。
     * 当它碰撞时,它是伪随机调整(使用Marsaglia XorShift)。
     *  nextSecondarySeed方法在与ThreadLocalRandom相同的上下文中使用,但仅用于瞬态使用,例如随机自适应自旋/块序列,其中便宜的RNG就足够了,并且原则上可以破坏主Thread
     * LocalRandom的用户可见统计属性,如果我们是使用它。
     * 当它碰撞时,它是伪随机调整(使用Marsaglia XorShift)。
     * 
     *  注意：由于包保护问题,一些这些方法的版本也出现在一些子包类中。
     * 
     */

    /**
     * Returns the probe value for the current thread without forcing
     * initialization. Note that invoking ThreadLocalRandom.current()
     * can be used to force initialization on zero return.
     * <p>
     *  返回当前线程的探测值,而不强制初始化。注意,调用ThreadLocalRandom.current()可以用于在零返回时强制初始化。
     * 
     */
    static final int getProbe() {
        return UNSAFE.getInt(Thread.currentThread(), PROBE);
    }

    /**
     * Pseudo-randomly advances and records the given probe value for the
     * given thread.
     * <p>
     *  伪随机前进并记录给定线程的给定探针值。
     * 
     */
    static final int advanceProbe(int probe) {
        probe ^= probe << 13;   // xorshift
        probe ^= probe >>> 17;
        probe ^= probe << 5;
        UNSAFE.putInt(Thread.currentThread(), PROBE, probe);
        return probe;
    }

    /**
     * Returns the pseudo-randomly initialized or updated secondary seed.
     * <p>
     *  返回伪随机初始化或更新的辅助种子。
     * 
     */
    static final int nextSecondarySeed() {
        int r;
        Thread t = Thread.currentThread();
        if ((r = UNSAFE.getInt(t, SECONDARY)) != 0) {
            r ^= r << 13;   // xorshift
            r ^= r >>> 17;
            r ^= r << 5;
        }
        else {
            localInit();
            if ((r = (int)UNSAFE.getLong(t, SEED)) == 0)
                r = 1; // avoid zero
        }
        UNSAFE.putInt(t, SECONDARY, r);
        return r;
    }

    // Serialization support

    private static final long serialVersionUID = -5851777807851030925L;

    /**
    /* <p>
    /* 
     * @serialField rnd long
     *              seed for random computations
     * @serialField initialized boolean
     *              always true
     */
    private static final ObjectStreamField[] serialPersistentFields = {
            new ObjectStreamField("rnd", long.class),
            new ObjectStreamField("initialized", boolean.class),
    };

    /**
     * Saves the {@code ThreadLocalRandom} to a stream (that is, serializes it).
     * <p>
     *  将{@code ThreadLocalRandom}保存到流(即序列化它)。
     * 
     * 
     * @param s the stream
     * @throws java.io.IOException if an I/O error occurs
     */
    private void writeObject(java.io.ObjectOutputStream s)
        throws java.io.IOException {

        java.io.ObjectOutputStream.PutField fields = s.putFields();
        fields.put("rnd", UNSAFE.getLong(Thread.currentThread(), SEED));
        fields.put("initialized", true);
        s.writeFields();
    }

    /**
     * Returns the {@link #current() current} thread's {@code ThreadLocalRandom}.
     * <p>
     *  返回{@link #current()current}线程的{@code ThreadLocalRandom}。
     * 
     * @return the {@link #current() current} thread's {@code ThreadLocalRandom}
     */
    private Object readResolve() {
        return current();
    }

    // Unsafe mechanics
    private static final sun.misc.Unsafe UNSAFE;
    private static final long SEED;
    private static final long PROBE;
    private static final long SECONDARY;
    static {
        try {
            UNSAFE = sun.misc.Unsafe.getUnsafe();
            Class<?> tk = Thread.class;
            SEED = UNSAFE.objectFieldOffset
                (tk.getDeclaredField("threadLocalRandomSeed"));
            PROBE = UNSAFE.objectFieldOffset
                (tk.getDeclaredField("threadLocalRandomProbe"));
            SECONDARY = UNSAFE.objectFieldOffset
                (tk.getDeclaredField("threadLocalRandomSecondarySeed"));
        } catch (Exception e) {
            throw new Error(e);
        }
    }
}
