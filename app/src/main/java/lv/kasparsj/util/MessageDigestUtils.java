package lv.kasparsj.util;

import android.util.Base64;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MessageDigestUtils
{
    public static MessageDigest factory(final Algorithm algorithm) {
        if (algorithm == null) {
            throw new IllegalArgumentException("Algorithm is 'null'.");
        }
        try {
            return MessageDigest.getInstance(algorithm.getAlgorithm());
        }
        catch (final NoSuchAlgorithmException ignored) {
            return null;
        }
    }

    public static String hash(Algorithm algorithm, String text) {
        MessageDigest md = factory(algorithm);
        md.update(text.getBytes());
        byte[] digest = md.digest();
        return Base64.encodeToString(digest, Base64.NO_WRAP);
    }

    public static String MD2(String text) {
        return hash(Algorithm.MD2, text);
    }

    public static String MD5(String text) {
        return hash(Algorithm.MD5, text);
    }

    public static String SHA1(String text) {
        return hash(Algorithm.SHA1, text);
    }

    public static String SHA256(String text) {
        return hash(Algorithm.SHA256, text);
    }

    public static String SHA384(String text) {
        return hash(Algorithm.SHA384, text);
    }

    public static String SHA512(String text) {
        return hash(Algorithm.SHA512, text);
    }

    public enum Algorithm {

        MD2("MD2"), MD5("MD5"), SHA1("SHA-1"), SHA256("SHA-256"),
        SHA384("SHA-384"), SHA512("SHA-512");

        /** Algorithm name as defined in
         {@link MessageDigest#getInstance(String)} */
        private final String algorithm;

        private Algorithm(final String algorithm) {
            this.algorithm = algorithm;
        }

        public String getAlgorithm() {
            return this.algorithm;
        }
    }
}
