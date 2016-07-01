/*
 * Copyright (c) 2015-2016 BiliBili Inc.
 */

package me.kaede.frontia.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.text.TextUtils;
import me.kaede.frontia.core.PluginConstants;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Created by Kaede on 2016/4/26.
 * 获取插件的签名信息，校验签名。
 * Copy from SDk.
 */
public class SignatureUtil {
    private static String TAG = "SignatureUtil";
    private static WeakReference<byte[]> mReadBuffer;

    /**
     * 获取当前APP的签名
     *
     * @param context
     * @return
     */
    public static Signature[] collectCertificates(Context context) {
        Signature[] signatures = null;
        try {
            PackageInfo pkgInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_SIGNATURES);
            signatures = pkgInfo.signatures;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            PluginLogUtil.w(TAG, "获取签名失败");
        }
        return signatures;
    }

    /**
     * 获取指定APK的签名
     *
     * @param context
     * @param sourcePath
     * @return
     */
    public static Signature[] collectCertificates(Context context, String sourcePath) {
        Signature[] signatures = null;
        try {
            PackageInfo pkgInfo = context.getPackageManager().getPackageInfo(sourcePath, PackageManager.GET_SIGNATURES);
            signatures = pkgInfo.signatures;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            PluginLogUtil.w(TAG, "获取签名失败");
        }
        return signatures;
    }

    /**
     * 获取指定文件的签名
     *
     * @param sourcePath
     * @param simpleMode
     * @return
     */
    public static Signature[] collectCertificates(String sourcePath, boolean simpleMode) {
        // 另一种获取APK签名信息的方式
        // getPackageManager().getPackageArchiveInfo(,PackageManager.GET_SIGNATURES)获取插件的签名信息
        Signature mSignatures[] = null;
        WeakReference<byte[]> readBufferRef;
        byte[] readBuffer = null;
        synchronized (SignatureUtil.class) {
            readBufferRef = mReadBuffer;
            if (readBufferRef != null) {
                mReadBuffer = null;
                readBuffer = readBufferRef.get();
            }
            if (readBuffer == null) {
                readBuffer = new byte[8192];
                readBufferRef = new WeakReference<byte[]>(readBuffer);
            }
        }
        try {
            JarFile jarFile = new JarFile(sourcePath);
            Certificate[] certs = null;
            if (simpleMode) {
                // if SIMPLE MODE,, then we
                // can trust it...  we'll just use the AndroidManifest.xml
                // to retrieve its signatures, not validating all of the
                // files.
                JarEntry jarEntry = jarFile.getJarEntry("AndroidManifest.xml");
                certs = loadCertificates(jarFile, jarEntry, readBuffer);
                if (certs == null) {
                    PluginLogUtil.w(TAG, "Package "
                            + " has no certificates at entry "
                            + jarEntry.getName() + "; ignoring!");
                    jarFile.close();

                    PluginLogUtil.w(TAG, "INSTALL_PARSE_FAILED_NO_CERTIFICATES");
                    return null;
                }
                if (PluginConstants.DEBUG) {
                    PluginLogUtil.v(TAG, "File " + sourcePath + ": entry=" + jarEntry
                            + " certs=" + (certs != null ? certs.length : 0));
                    if (certs != null) {
                        final int N = certs.length;
                        for (int i = 0; i < N; i++) {
                            PluginLogUtil.d(TAG, "  Public key: "
                                    + certs[i].getPublicKey().getEncoded()
                                    + " " + certs[i].getPublicKey());
                        }
                    }
                }
            } else {
                Enumeration entries = jarFile.entries();
                while (entries.hasMoreElements()) {
                    JarEntry je = (JarEntry) entries.nextElement();
                    if (je.isDirectory()) continue;
                    if (je.getName().startsWith("META-INF/")) continue;
                    Certificate[] localCerts = loadCertificates(jarFile, je,
                            readBuffer);
                    if (PluginConstants.DEBUG) {
                        PluginLogUtil.v(TAG, "File " + sourcePath + " entry " + je.getName()
                                + ": certs=" + certs + " ("
                                + (certs != null ? certs.length : 0) + ")");
                    }
                    if (localCerts == null) {
                        PluginLogUtil.w(TAG, "Package "
                                + " has no certificates at entry "
                                + je.getName() + "; ignoring!");
                        jarFile.close();

                        PluginLogUtil.w(TAG, "INSTALL_PARSE_FAILED_NO_CERTIFICATES");
                        return null;
                    } else if (certs == null) {
                        certs = localCerts;
                    } else {
                        // Ensure all certificates match.
                        for (int i = 0; i < certs.length; i++) {
                            boolean found = false;
                            for (int j = 0; j < localCerts.length; j++) {
                                if (certs[i] != null &&
                                        certs[i].equals(localCerts[j])) {
                                    found = true;
                                    break;
                                }
                            }
                            if (!found || certs.length != localCerts.length) {
                                PluginLogUtil.w(TAG, "Package "
                                        + " has mismatched certificates at entry "
                                        + je.getName() + "; ignoring!");
                                jarFile.close();

                                PluginLogUtil.w(TAG, "INSTALL_PARSE_FAILED_INCONSISTENT_CERTIFICATES");
                                return null;
                            }
                        }
                    }
                }
            }
            jarFile.close();
            synchronized (SignatureUtil.class) {
                mReadBuffer = readBufferRef;
            }
            if (certs != null && certs.length > 0) {
                final int N = certs.length;
                mSignatures = new Signature[certs.length];
                for (int i = 0; i < N; i++) {
                    mSignatures[i] = new Signature(
                            certs[i].getEncoded());
                }
            } else {
                PluginLogUtil.w(TAG, "Package "
                        + " has no certificates; ignoring!");
                PluginLogUtil.w(TAG, "INSTALL_PARSE_FAILED_NO_CERTIFICATES");
                return null;
            }
        } catch (CertificateEncodingException e) {
            e.printStackTrace();
            PluginLogUtil.w(TAG, "Exception reading " + sourcePath);
            PluginLogUtil.w(TAG, "INSTALL_PARSE_FAILED_CERTIFICATE_ENCODING");
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            PluginLogUtil.w(TAG, "Exception reading " + sourcePath);
            PluginLogUtil.w(TAG, "INSTALL_PARSE_FAILED_CERTIFICATE_ENCODING");
            return null;
        } catch (RuntimeException e) {
            e.printStackTrace();
            PluginLogUtil.w(TAG, "Exception reading " + sourcePath);
            PluginLogUtil.w(TAG, "INSTALL_PARSE_FAILED_UNEXPECTED_EXCEPTION");
            return null;
        }
        return mSignatures;
    }


    private static Certificate[] loadCertificates(JarFile jarFile, JarEntry je, byte[] readBuffer) {
        try {
            // We must read the stream for the JarEntry to retrieve
            // its certificates.
            InputStream is = new BufferedInputStream(jarFile.getInputStream(je));
            while (is.read(readBuffer, 0, readBuffer.length) != -1) {
                // not using
            }
            is.close();
            return je != null ? je.getCertificates() : null;
        } catch (IOException e) {
            e.printStackTrace();
            PluginLogUtil.w(TAG, "Exception reading " + je.getName() + " in "
                    + jarFile.getName());
        } catch (RuntimeException e) {
            e.printStackTrace();
            PluginLogUtil.w(TAG, "Exception reading " + je.getName() + " in "
                    + jarFile.getName());
        }
        return null;
    }

    public static boolean isSignaturesSame(Signature[] s1, Signature[] s2) {
        if (s1 == null) {
            return false;
        }
        if (s2 == null) {
            return false;
        }
        HashSet<Signature> set1 = new HashSet<Signature>();
        for (Signature sig : s1) {
            set1.add(sig);
        }
        HashSet<Signature> set2 = new HashSet<Signature>();
        for (Signature sig : s2) {
            set2.add(sig);
        }
        // Make sure s2 contains all signatures in s1.
        if (set1.equals(set2)) {
            return true;
        }
        return false;
    }

    public static boolean isSignaturesSame(String s1, Signature[] s2) {
        if (TextUtils.isEmpty(s1)) return false;
        if (s2 == null) return false;
        for (int i = 0; i < s2.length; i++) {
            String item = s2[i].toCharsString().toLowerCase();
            if (item.equalsIgnoreCase(s1)) return true;
        }
        return false;
    }

    public static void printSignature(Signature[] s) {
        int length = s.length;
        for (int i = 0; i < length; i++) {
            PluginLogUtil.v(TAG, "signature " + i + " : " + s[i].toCharsString().toLowerCase());
        }
    }
}
