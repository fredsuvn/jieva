package xyz.srclab.common.codec.sm2;

import java.math.BigInteger;

/**
 * SM2 params.
 *
 * @author sunqian
 */
public class Sm2Params {

    /**
     * Returns default params.
     *
     * @return default params
     */
    public static Sm2Params defaultParams() {
        return new Sm2Params(N, P, A, B, GX, GY);
    }

    private static final BigInteger N = new BigInteger(
            "FFFFFFFE" + "FFFFFFFF" + "FFFFFFFF" + "FFFFFFFF" + "7203DF6B" + "21C6052B" + "53BBF409" + "39D54123", 16);
    private static final BigInteger P = new BigInteger(
            "FFFFFFFE" + "FFFFFFFF" + "FFFFFFFF" + "FFFFFFFF" + "FFFFFFFF" + "00000000" + "FFFFFFFF" + "FFFFFFFF", 16);
    private static final BigInteger A = new BigInteger(
            "FFFFFFFE" + "FFFFFFFF" + "FFFFFFFF" + "FFFFFFFF" + "FFFFFFFF" + "00000000" + "FFFFFFFF" + "FFFFFFFC", 16);
    private static final BigInteger B = new BigInteger(
            "28E9FA9E" + "9D9F5E34" + "4D5A9E4B" + "CF6509A7" + "F39789F5" + "15AB8F92" + "DDBCBD41" + "4D940E93", 16);
    private static final BigInteger GX = new BigInteger(
            "32C4AE2C" + "1F198119" + "5F990446" + "6A39C994" + "8FE30BBF" + "F2660BE1" + "715A4589" + "334C74C7", 16);
    private static final BigInteger GY = new BigInteger(
            "BC3736A2" + "F4F6779C" + "59BDCEE3" + "6B692153" + "D0A9877C" + "C62A4740" + "02DF32E5" + "2139F0A0", 16);

    private final BigInteger n;
    private final BigInteger p;
    private final BigInteger a;
    private final BigInteger b;
    private final BigInteger gx;
    private final BigInteger gy;

    /**
     * Creates with given params.
     *
     * @param n  param n
     * @param p  param p
     * @param a  param a
     * @param b  param b
     * @param gx param gx
     * @param gy param gy
     */
    public Sm2Params(
            BigInteger n,
            BigInteger p,
            BigInteger a,
            BigInteger b,
            BigInteger gx,
            BigInteger gy) {
        this.n = n;
        this.p = p;
        this.a = a;
        this.b = b;
        this.gx = gx;
        this.gy = gy;
    }

    /**
     * Returns param n.
     *
     * @return param n
     */
    public BigInteger n() {
        return n;
    }

    /**
     * Returns param p.
     *
     * @return param p
     */
    public BigInteger p() {
        return p;
    }

    /**
     * Returns param a.
     *
     * @return param a
     */
    public BigInteger a() {
        return a;
    }

    /**
     * Returns param b.
     *
     * @return param b
     */
    public BigInteger b() {
        return b;
    }

    /**
     * Returns param gx.
     *
     * @return param gx
     */
    public BigInteger gx() {
        return gx;
    }

    /**
     * Returns param gy.
     *
     * @return param gy
     */
    public BigInteger gy() {
        return gy;
    }
}
