/*
 * Copyright (c) 2016 Kaede Akatsuki (kidhaibara@gmail.com)
 */

package moe.studio.frontia;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.Build;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import org.apache.commons.io.IOUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import dalvik.system.DexClassLoader;
import moe.studio.frontia.ext.PluginError;

import static org.apache.commons.io.FileUtils.deleteQuietly;

/**
 * 包名内部工具类。
 * 開けよ、我が闇の力！
 * <p>
 * {@linkplain FileUtils}       文件操作
 * {@linkplain SoLibUtils}      SO库操作
 * {@linkplain ApkUtils}        APK操作
 * {@linkplain SignatureUtils}  签名验证
 *
 * @author kaede
 * @version date 16/10/20
 */

final class Internals {

    final static class FileUtils {

        private static final String TAG = "plugin.files";

        static void closeQuietly(Closeable closeable) {
            IOUtils.closeQuietly(closeable);
        }

        static boolean delete(String path) {
            if (TextUtils.isEmpty(path)) {
                return false;
            }
            return delete(new File(path));
        }

        static boolean delete(File file) {
            return deleteQuietly(file);
        }

        static boolean exist(String path) {
            return !TextUtils.isEmpty(path) && (new File(path).exists());
        }

        @SuppressWarnings("ResultOfMethodCallIgnored")
        static void checkCreateFile(File file) throws IOException {
            if (file == null) {
                throw new IOException("File is null.");
            }
            if (file.exists()) {
                delete(file);
            }
            File parentFile = file.getParentFile();
            if (!parentFile.exists()) {
                parentFile.mkdirs();
            }
            if (!file.createNewFile()) {
                throw new IOException("Create file fail, file already exists.");
            }
        }

        @SuppressWarnings("ResultOfMethodCallIgnored")
        static void checkCreateDir(File file) throws IOException {
            if (file == null) {
                throw new IOException("Dir is null.");
            }
            if (file.exists()) {
                if (file.isDirectory()) {
                    return;
                }
                if (!delete(file)) {
                    throw new IOException("Fail to delete existing file, file = "
                            + file.getAbsolutePath());
                }
                file.mkdir();
            } else {
                file.mkdirs();
            }
            if (!file.exists() || !file.isDirectory()) {
                throw new IOException("Fail to create dir, dir = " + file.getAbsolutePath());
            }
        }

        static void copyFile(File sourceFile, File destFile) throws IOException {
            if (sourceFile == null) {
                throw new IOException("Source file is null.");
            }
            if (destFile == null) {
                throw new IOException("Dest file is null.");
            }
            if (!sourceFile.exists()) {
                throw new IOException("Source file not found.");
            }

            checkCreateFile(destFile);
            InputStream in = null;
            OutputStream out = null;

            try {
                in = new FileInputStream(sourceFile);
                out = new FileOutputStream(destFile);
                FileDescriptor fd = ((FileOutputStream) out).getFD();
                out = new BufferedOutputStream(out);
                IOUtils.copy(in, out);
                out.flush();
                fd.sync();
            } catch (IOException e) {
                Logger.w(TAG, e);
            } finally {
                closeQuietly(in);
                closeQuietly(out);
            }
        }

        static void copyFileFromAsset(Context context, String pathAssets, File destFile)
                throws IOException {
            if (TextUtils.isEmpty(pathAssets)) {
                throw new IOException("Asset path is empty.");
            }

            checkCreateFile(destFile);
            InputStream in = null;
            OutputStream out = null;

            try {
                in = context.getAssets().open(pathAssets);
                out = new FileOutputStream(destFile);
                FileDescriptor fd = ((FileOutputStream) out).getFD();
                out = new BufferedOutputStream(out);
                IOUtils.copy(in, out);
                out.flush();
                fd.sync();
            } catch (IOException e) {
                Logger.w(TAG, e);
            } finally {
                closeQuietly(in);
                closeQuietly(out);
            }
        }

        static void dumpFiles(File file) {
            if (!Logger.DEBUG) {
                return;
            }

            boolean isDirectory = file.isDirectory();
            Logger.v(TAG, "path = " + file.getAbsolutePath() + ", isDir = " + isDirectory);
            if (isDirectory) {
                File[] childFiles = file.listFiles();
                if (childFiles != null && childFiles.length > 0) {
                    for (File childFile : childFiles) {
                        dumpFiles(childFile);
                    }
                }
            }
        }
    }

    final static class SoLibUtils {
        private static final String TAG = "plugin.so";
        private static final int BUFFER_SIZE = 1024 * 4;

        /**
         * 将SO库解压到指定路径并返回所有解压好的SO库文件名字的集合。
         *
         * @return SO库文件名集合
         */
        static Set<String> extractSoLib(File apkFile, File destDir) throws IOException {
            if (apkFile == null || !apkFile.exists()) {
                throw new IOException("Apk file not found.");
            }

            HashSet<String> result = new HashSet<>(4);
            FileUtils.checkCreateDir(destDir);
            Logger.v(TAG, "copy so file to " + destDir.getAbsolutePath()
                    + ", apk = " + apkFile.getName());

            ZipFile zipFile = null;
            InputStream in = null;
            OutputStream out = null;

            try {
                zipFile = new ZipFile(apkFile);
                ZipEntry zipEntry;
                Enumeration entries = zipFile.entries();
                while (entries.hasMoreElements()) {
                    zipEntry = (ZipEntry) entries.nextElement();
                    String relativePath = zipEntry.getName();

                    if (relativePath == null || relativePath.contains("../")) {
                        // Abort zip file injection hack.
                        continue;
                    }

                    if (!relativePath.startsWith("lib" + File.separator)) {
                        Logger.v(TAG, "not lib dir entry, skip " + relativePath);
                        continue;
                    }

                    if (zipEntry.isDirectory()) {
                        File folder = new File(destDir, relativePath);
                        Logger.v(TAG, "create dir " + folder.getAbsolutePath());
                        FileUtils.checkCreateDir(folder);
                    } else {
                        File soLibFile = new File(destDir, relativePath);
                        Logger.v(TAG, "unzip soLib file " + soLibFile.getAbsolutePath());
                        FileUtils.checkCreateFile(soLibFile);

                        byte[] buffer = new byte[BUFFER_SIZE];
                        out = new FileOutputStream(soLibFile);
                        FileDescriptor fd = ((FileOutputStream) out).getFD();
                        out = new BufferedOutputStream(out);
                        in = new BufferedInputStream(zipFile.getInputStream(zipEntry));
                        int count;
                        while ((count = in.read(buffer)) != -1) {
                            out.write(buffer, 0, count);
                        }
                        out.flush();
                        fd.sync();

                        result.add(soLibFile.getName());
                    }
                }
            } catch (IOException e) {
                Logger.w(TAG, e);
                throw new IOException("Unzip soLibs fail.", e);
            } finally {
                IOUtils.closeQuietly(in);
                IOUtils.closeQuietly(out);
                if (zipFile != null) {
                    try {
                        zipFile.close();
                    } catch (IOException e) {
                        if (Logger.DEBUG) {
                            Logger.w(TAG, e);
                        }
                    }
                }
            }

            if (Logger.DEBUG) {
                Logger.v(TAG, "--");
                for (String item : result) {
                    Logger.v(TAG, item);
                }
                Logger.v(TAG, "--");
            }
            return result;
        }

        /**
         * 根据so库的名字以及当前系统的CPU类型，复制最合适的so库到目标路径。
         *
         * @param sourceDir so库所在目录
         * @param soLibName so库名字
         * @param destDir   目标so库目录
         * @return 成功复制的so库
         */
        @SuppressWarnings("deprecation")
        static File copySoLib(Context context, File sourceDir, String soLibName, File destDir)
                throws IOException {
            Logger.v(TAG, "--");

            File matchSoLib = null;
            List<String> capableAbis = getCapableAbis(context);

            if (capableAbis != null) {
                for (String abi : capableAbis) {
                    Logger.v(TAG, "Try install soLib, supported abi = " + abi);
                    String name = "lib" + File.separator + abi + File.separator + soLibName;
                    File sourceFile = new File(sourceDir, name);
                    if (sourceFile.exists()) {
                        File destFile = new File(destDir, soLibName);
                        if (sourceFile.renameTo(destFile)) {
                            Logger.v(TAG, "Rename soLib, from = " + sourceFile.getAbsolutePath()
                                    + ", to = " + destFile.getAbsolutePath());
                        } else {
                            throw new IOException("Rename soLib fail.");
                        }
                        matchSoLib = destFile;
                        break;
                    }
                }
            } else {
                Logger.w(TAG, "Cpu abis is null.");
            }

            if (matchSoLib == null) {
                Logger.d(TAG, "Can not install " + soLibName + ", NO_MATCHING_ABIS");
            }

            Logger.v(TAG, "--");
            return matchSoLib;
        }

        static List<String> getCapableAbis(Context context) {
            ApplicationInfo appInfo = ApkUtils.getAppInfo(context);
            Set<String> apkAbis = new HashSet<>();
            List<String> buildAbis = new ArrayList<>();

            if (appInfo != null) {
                String apkPath = appInfo.sourceDir;
                ZipFile zipFile = null;

                try {
                    zipFile = new ZipFile(apkPath);
                    ZipEntry zipEntry;
                    Enumeration entries = zipFile.entries();
                    while (entries.hasMoreElements()) {
                        zipEntry = (ZipEntry) entries.nextElement();
                        String path = zipEntry.getName();

                        if (path == null || path.contains("../")) {
                            continue;
                        }

                        String startSymbol = "lib" + File.separator;
                        String endSymbol = String.valueOf(File.separator);

                        if (!path.startsWith(startSymbol)) {
                            continue;
                        }

                        int start = path.indexOf(startSymbol) + startSymbol.length();
                        int end = path.indexOf(endSymbol, startSymbol.length());

                        if (end > start && end < path.length()) {
                            apkAbis.add(path.substring(start, end));
                        } else {
                            Logger.w(TAG, "Substring bounded, length = " + path.length()
                                    + ", start = " + start + ", end = " + end);
                        }
                    }
                } catch (IOException e) {
                    Logger.w(TAG, e);
                } finally {
                    if (zipFile != null) {
                        try {
                            zipFile.close();
                        } catch (IOException e) {
                            Logger.w(TAG, e);
                        }
                    }
                }
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                String[] abis = Build.SUPPORTED_ABIS;
                if (abis != null) {
                    Collections.addAll(buildAbis, abis);
                } else {
                    Logger.w(TAG, "Cpu abis is null.");
                }

            } else {
                buildAbis.add(Build.CPU_ABI);
                buildAbis.add(Build.CPU_ABI2);
            }

            Logger.d(TAG, "Build cpu abis = " + buildAbis);
            Logger.d(TAG, "Apk cpu abis = " + apkAbis);

            if (apkAbis.size() > 0) {
                Iterator<String> iterator = buildAbis.iterator();
                while (iterator.hasNext()) {
                    String next = iterator.next();
                    if (!apkAbis.contains(next)) {
                        iterator.remove();
                    }
                }
            }

            Logger.d(TAG, "Capable cpu abis = " + buildAbis);
            return buildAbis;
        }
    }

    final static class ApkUtils {

        private static final String TAG = "plugin.apk";

        /**
         * 创建一个ClassLoader实例，用于加载插件里的类。
         *
         * @param context       宿主Context实例
         * @param dexPath       插件路径
         * @param optimizedDir  用于释放插件odex的路径
         * @param nativeLibDir  插件so库路径
         * @param isInDependent 插件类是否与宿主隔离
         * @return 插件ClassLoader实例
         */
        static DexClassLoader createClassLoader(Context context, String dexPath,
                                                String optimizedDir, String nativeLibDir,
                                                boolean isInDependent) throws PluginError.LoadError {
            ClassLoader parentClassLoader;

            if (isInDependent) {
                // Separate the new ClassLoader from current app, thus the class loaded by this
                // new ClassLoader will deadly incompatible from the current app.
                parentClassLoader = ClassLoader.getSystemClassLoader().getParent();
            } else {
                // Use the current app's ClassLoader as the new ClassLoader's parent.
                // In this case, the class loaded by the new ClassLoader must regard the
                // "Parent Delegation Model" of ClassLoader.
                parentClassLoader = context.getClassLoader();
            }

            try {
                // TODO: 2016/11/30 Adding MultiDex support for plugin.
                return new DexClassLoader(dexPath, optimizedDir, nativeLibDir, parentClassLoader);

            } catch (Throwable e) {
                Logger.w(TAG, e);
                throw new PluginError.LoadError(e, PluginError.ERROR_LOA_CLASSLOADER);
            }
        }

        /**
         * 创建一个AssetManager实例，用于加载插件的res资源。
         *
         * @param dexPath 插件路径
         * @return 插件AssetManager实例
         */
        static AssetManager createAssetManager(String dexPath) throws PluginError.LoadError {
            try {
                // TODO: 2016/11/25 We may need to support different api levels here.
                AssetManager assetManager = AssetManager.class.newInstance();
                Method addAssetPath = assetManager.getClass().getMethod("addAssetPath", String.class);
                addAssetPath.invoke(assetManager, dexPath);
                return assetManager;

            } catch (Throwable e) {
                Logger.w(TAG, e);
                throw new PluginError.LoadError(e, PluginError.ERROR_LOA_ASSET_MANAGER);
            }
        }

        static Resources createResources(Context context, AssetManager assetManager) {
            Resources superRes = context.getResources();
            return new Resources(assetManager, superRes.getDisplayMetrics(),
                    superRes.getConfiguration());
        }

        static Class<?> loadClass(ClassLoader classLoader, String className) throws Exception {
            return loadClass(classLoader, className, true);
        }

        static Class<?> loadClass(ClassLoader classLoader, String className,
                                  boolean shouldInitialize) throws Exception {
            try {
                return Class.forName(className, shouldInitialize, classLoader);
            } catch (Throwable e) {
                throw new Exception(e);
            }
        }

        @Nullable
        static PackageInfo getLocalPackageInfo(Context context) {
            return getLocalPackageInfo(context, 0);
        }

        @Nullable
        static PackageInfo getLocalPackageInfo(Context context, int flag) {
            PackageManager pm = context.getPackageManager();
            PackageInfo pkgInfo = null;
            try {
                pkgInfo = pm.getPackageInfo(context.getPackageName(), flag);
            } catch (Throwable e) {
                Logger.w(TAG, e);
            }
            return pkgInfo;
        }

        @Nullable
        static PackageInfo getPackageInfo(Context context, String apkPath) {
            return getPackageInfo(context, apkPath,
                    PackageManager.GET_ACTIVITIES | PackageManager.GET_SERVICES);
        }

        @Nullable
        static PackageInfo getPackageInfo(Context context, String apkPath, int flag) {
            return context.getPackageManager().getPackageArchiveInfo(apkPath, flag);
        }

        @Nullable
        static ApplicationInfo getAppInfo(Context context) {
            PackageManager pm;
            String packageName;
            try {
                pm = context.getPackageManager();
                packageName = context.getPackageName();
            } catch (RuntimeException e) {
                Logger.w(TAG, e);
                return null;
            }
            if (pm == null || packageName == null) {
                // This is most likely a mock context, so just return without patching.
                return null;
            }

            ApplicationInfo info = null;
            try {
                info = pm.getApplicationInfo(packageName, PackageManager.GET_META_DATA);
            } catch (PackageManager.NameNotFoundException e) {
                Logger.w(TAG, e);
            }
            return info;
        }
    }

    final static class SignatureUtils {

        private static String TAG = "plugin.signature";
        private static final int BUFFER_SIZE = 1024 * 4;

        /**
         * 获取当前APP的签名
         */
        @Nullable
        @SuppressLint("PackageManagerGetSignatures")
        static Signature[] getSignatures(Context context) {
            Signature[] signatures = null;
            try {
                PackageInfo pkgInfo = context.getPackageManager()
                        .getPackageInfo(context.getPackageName(), PackageManager.GET_SIGNATURES);
                signatures = pkgInfo.signatures;

            } catch (PackageManager.NameNotFoundException e) {
                Logger.w(TAG, "Can not get signature, error = " + e.getLocalizedMessage());
                Logger.w(TAG, e);
            }
            return signatures;
        }

        /**
         * 获取指定文件的签名
         */
        @Nullable
        static Signature[] getSignatures(Context context, String apkPath) {
            Signature[] signatures = getArchiveSignatures(context, apkPath);
            if (signatures == null) {
                signatures = getArchiveSignatures(apkPath, false);
                if (signatures == null) {
                    signatures = getArchiveSignatures(apkPath, true);
                }
            }
            return signatures;
        }


        @SuppressLint("PackageManagerGetSignatures")
        private static Signature[] getArchiveSignatures(Context context, String apkPath) {
            PackageInfo pkgInfo
                    = context.getPackageManager().getPackageArchiveInfo(apkPath,
                    PackageManager.GET_SIGNATURES);
            return pkgInfo == null ? null : pkgInfo.signatures;
        }

        /**
         * 获取指定文件的签名
         */
        @Nullable
        static Signature[] getArchiveSignatures(String apkPath, boolean simpleMode) {
            Signature signatures[];
            JarFile jarFile = null;

            try {
                byte[] readBuffer = new byte[BUFFER_SIZE];
                jarFile = new JarFile(apkPath);
                Certificate[] certs = null;
                if (simpleMode) {
                    // if SIMPLE MODE,, then we
                    // can trust it...  we'll just use the AndroidManifest.xml
                    // to retrieve its signatures, not validating all of the
                    // files.
                    JarEntry jarEntry = jarFile.getJarEntry("AndroidManifest.xml");
                    certs = loadCertificates(jarFile, jarEntry, readBuffer);
                    if (certs == null) {
                        Logger.w(TAG, "Package "
                                + " has no certificates at entry "
                                + jarEntry.getName() + "; ignoring!");
                        Logger.w(TAG, "INSTALL_PARSE_FAILED_NO_CERTIFICATES");
                        return null;
                    }
                    if (Logger.DEBUG) {
                        Logger.v(TAG, "File " + apkPath + ": entry=" + jarEntry
                                + " certs=" + certs.length);
                        for (Certificate cert : certs) {
                            Logger.d(TAG, "  Public key: "
                                    + Arrays.toString(cert.getPublicKey().getEncoded())
                                    + " " + cert.getPublicKey());
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
                        if (Logger.DEBUG) {
                            Logger.v(TAG, "File " + apkPath + " entry " + je.getName()
                                    + ": certs=" + Arrays.toString(certs) + " ("
                                    + (certs != null ? certs.length : 0) + ")");
                        }
                        if (localCerts == null) {
                            Logger.w(TAG, "Package "
                                    + " has no certificates at entry "
                                    + je.getName() + "; ignoring!");
                            Logger.w(TAG, "INSTALL_PARSE_FAILED_NO_CERTIFICATES");
                            return null;
                        } else if (certs == null) {
                            certs = localCerts;
                        } else {
                            // Ensure all certificates match.
                            for (Certificate cert : certs) {
                                boolean found = false;
                                for (Certificate localCert : localCerts) {
                                    if (cert != null &&
                                            cert.equals(localCert)) {
                                        found = true;
                                        break;
                                    }
                                }
                                if (!found || certs.length != localCerts.length) {
                                    Logger.w(TAG, "Package "
                                            + " has mismatched certificates at entry "
                                            + je.getName() + "; ignoring!");
                                    Logger.w(TAG, "INSTALL_PARSE_FAILED_INCONSISTENT_CERTIFICATES");
                                    return null;
                                }
                            }
                        }
                    }
                }
                if (certs != null && certs.length > 0) {
                    final int N = certs.length;
                    signatures = new Signature[certs.length];
                    for (int i = 0; i < N; i++) {
                        signatures[i] = new Signature(
                                certs[i].getEncoded());
                    }
                } else {
                    Logger.w(TAG, "Package "
                            + " has no certificates; ignoring!");
                    Logger.w(TAG, "INSTALL_PARSE_FAILED_NO_CERTIFICATES");
                    return null;
                }
            } catch (CertificateEncodingException e) {
                Logger.w(TAG, "Exception reading " + apkPath);
                Logger.w(TAG, "INSTALL_PARSE_FAILED_CERTIFICATE_ENCODING");
                Logger.w(TAG, e);
                return null;
            } catch (IOException e) {
                Logger.w(TAG, "Exception reading " + apkPath);
                Logger.w(TAG, "INSTALL_PARSE_FAILED_CERTIFICATE_ENCODING");
                Logger.w(TAG, e);
                return null;
            } catch (RuntimeException e) {
                Logger.w(TAG, "Exception reading " + apkPath);
                Logger.w(TAG, "INSTALL_PARSE_FAILED_UNEXPECTED_EXCEPTION");
                Logger.w(TAG, e);
                return null;
            } finally {
                if (jarFile != null) {
                    FileUtils.closeQuietly(jarFile);
                }
            }
            return signatures;
        }


        @SuppressWarnings("StatementWithEmptyBody")
        private static Certificate[] loadCertificates(JarFile jarFile, JarEntry je, byte[] readBuffer) {
            InputStream in = null;
            try {
                // We must read the stream for the JarEntry to retrieve
                // its certificates.
                in = new BufferedInputStream(jarFile.getInputStream(je));
                while (in.read(readBuffer, 0, readBuffer.length) != -1) {
                    // Do nothing.
                }
                return je != null ? je.getCertificates() : null;

            } catch (IOException | RuntimeException e) {
                Logger.w(TAG, "Exception reading " + je.getName() + " in "
                        + jarFile.getName());
                Logger.w(TAG, e);
            } finally {
                FileUtils.closeQuietly(in);
            }
            return null;
        }

        static boolean isSignaturesSame(Signature[] s1, Signature[] s2) {
            if (s1 == null || s2 == null) {
                return false;
            }
            if (s1.length != s2.length) {
                return false;
            }

            HashSet<Signature> set1 = new HashSet<>();
            Collections.addAll(set1, s1);
            HashSet<Signature> set2 = new HashSet<>();
            Collections.addAll(set2, s2);

            // Make sure s2 contains all signatures in s1.
            return set1.equals(set2);
        }

        static boolean isSignaturesSame(String s1, Signature[] s2) {
            if (TextUtils.isEmpty(s1)) {
                return false;
            }
            if (s2 == null) {
                return false;
            }

            for (Signature signature : s2) {
                String item = signature.toCharsString().toLowerCase();
                if (item.equalsIgnoreCase(s1)) {
                    return true;
                }
            }
            return false;
        }

        static void printSignature(Signature[] s) {
            Logger.v(TAG, "-");
            if (s == null || s.length == 0) {
                Logger.v(TAG, "Signature is empty.");
            } else {
                int length = s.length;
                for (int i = 0; i < length; i++) {
                    Logger.v(TAG, "Signature " + i + " = " + s[i].toCharsString().toLowerCase());
                }
            }
            Logger.v(TAG, "-");
        }
    }
}
