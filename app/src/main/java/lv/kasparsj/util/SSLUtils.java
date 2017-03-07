package lv.kasparsj.util;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

public class SSLUtils
{
    public static SSLSocketFactory getTrustAllSocketFactory() throws NoSuchAlgorithmException, KeyManagementException {
        return getTrustAllSocketFactory(new TrustAllManager());
    }

    public static SSLSocketFactory getTrustAllSocketFactory(TrustManager trustManager) throws NoSuchAlgorithmException, KeyManagementException {
        final SSLContext sslContext = SSLContext.getInstance("SSL");
        sslContext.init(null, new TrustManager[] { trustManager }, new java.security.SecureRandom());
        return sslContext.getSocketFactory();
    }

    public static TrustManagerFactory getSelfSignedTrustManagerFactory(Certificate ca) throws CertificateException, NoSuchAlgorithmException, KeyStoreException, IOException {
        return getSelfSignedTrustManagerFactory(ca, "ca");
    }

    public static TrustManagerFactory getSelfSignedTrustManagerFactory(Certificate ca, String alias) throws KeyStoreException, CertificateException, NoSuchAlgorithmException, IOException {
        String keyStoreType = KeyStore.getDefaultType();
        KeyStore keyStore = KeyStore.getInstance(keyStoreType);
        keyStore.load(null, null);
        keyStore.setCertificateEntry(alias, ca);

        String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
        tmf.init(keyStore);
        return tmf;
    }

    public static SSLSocketFactory getSelfSignedSocketFactory(TrustManagerFactory tmf) throws NoSuchAlgorithmException, KeyManagementException {
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, tmf.getTrustManagers(), null);

        return sslContext.getSocketFactory();
    }

    public static Certificate generateCertificate(InputStream inputStream) throws CertificateException {
        return generateCertificate("X.509", inputStream);
    }

    public static Certificate generateCertificate(String type, InputStream inputStream) throws CertificateException {
        CertificateFactory cf = CertificateFactory.getInstance(type);
        try {
            return cf.generateCertificate(inputStream);
        } finally {
            try {
                inputStream.close();
            }
            catch (IOException ignored) {

            }
        }
    }

    public static class TrustAllManager implements X509TrustManager {
        @Override
        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
        }

        @Override
        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
        }

        @Override
        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
            return new java.security.cert.X509Certificate[]{};
        }
    }

    public static class TrustAllHostnameVerifier implements HostnameVerifier {
        @Override
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    }
}
