/*
 * Copyright (c) 2016 Kaede Akatsuki (kidhaibara@gmail.com)
 */

package moe.studio.frontia;

import android.app.Application;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import moe.studio.frontia.BuildConfig;
import moe.studio.frontia.ext.PluginApk;

/**
 * @author kaede
 * @version date 2016/12/2
 */

final class ManifestUtils {

    public static final String TAG = "plugin.manifest";

    @NonNull
    static PluginApk parse(File apk) throws IOException {
        if (!apk.exists()) {
            throw new IOException("Apk not found.");
        }

        String namespace = null;
        String packageName = null;
        String versionCode = null;
        String versionName = null;
        String application = Application.class.getName();
        Map<String, Integer> dependencies = new HashMap<>();
        PluginApk pkg = new PluginApk();

        try {
            ZipFile zipFile = new ZipFile(apk, ZipFile.OPEN_READ);
            ZipEntry manifestXmlEntry = zipFile.getEntry(ManifestReader.DEFAULT_XML);
            String manifestXml = ManifestReader.getManifestXMLFromAPK(zipFile, manifestXmlEntry);

            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser parser = factory.newPullParser();
            parser.setInput(new StringReader(manifestXml));
            int eventType = parser.getEventType();


            do {
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        break;

                    case XmlPullParser.START_TAG: {
                        String tag = parser.getName();
                        if ("manifest".equals(tag)) {
                            namespace = parser.getNamespace("android");
                            packageName = parser.getAttributeValue(null, "package");
                            versionCode = parser.getAttributeValue(namespace, "versionCode");
                            versionName = parser.getAttributeValue(namespace, "versionName");

                        } else if ("meta-data".equals(tag)) {
                            Logger.v(TAG, "Parse meta-data");
                            String name = parser.getAttributeValue(namespace, "name");
                            String value = parser.getAttributeValue(namespace, "value");

                            if (!TextUtils.isEmpty(value) && !TextUtils.isEmpty(value)
                                    && name.startsWith(BuildConfig.PREFIX)) {
                                computeDependency(name, value, dependencies);
                            }

                        } else if ("exported-fragment".equals(tag)) {
                        } else if ("exported-service".equals(tag)) {
                        } else if ("uses-library".equals(tag)) {
                        } else if ("application".equals(tag)) {
                            String name = parser.getAttributeValue(namespace, "name");

                            if (name != null) {
                                name = computeName(name, packageName);
                                if (!TextUtils.isEmpty(name)) {
                                    application = name;
                                }
                            }

                        } else if ("activity".equals(tag)) {
                            Logger.v(TAG, "Parse activity");

                        } else if ("receiver".equals(tag)) {
                        } else if ("service".equals(tag)) {
                        } else if ("provider".equals(tag)) {
                        }

                        break;
                    }
                    case XmlPullParser.END_TAG: {
                        break;
                    }
                }
                eventType = parser.next();
            } while (eventType != XmlPullParser.END_DOCUMENT);

        } catch (XmlPullParserException | IOException e) {
            Logger.w(TAG, e);
            throw new IOException(e);
        }

        Log.v(TAG, "Parse manifest, namespace = " + namespace
                + ", package = " + packageName
                + ", application = " + application
                + ", versionName = " + versionName
                + ", versionCode = " + versionCode);

        pkg.application = application;
        pkg.packageName = packageName;
        pkg.versionName = versionName;
        pkg.versionCode = versionCode;
        pkg.dependencies = dependencies;

        return pkg;
    }

    @Nullable
    private static String computeName(String nameOrig, String pkgName) {
        if (nameOrig == null) {
            return null;
        }

        try {
            if (nameOrig.startsWith(".")) {
                return pkgName + nameOrig;

            } else if (!nameOrig.contains(".")) {
                return pkgName + '.' + nameOrig;

            } else {
                return nameOrig;
            }

        } catch (Exception e) {
            Log.w(TAG, e);
        }

        return null;
    }

    private static void computeDependency(String name, String value,
                                          Map<String, Integer> dependencies) {
        String library = name.substring(
                name.indexOf(BuildConfig.PREFIX) + BuildConfig.PREFIX.length(),
                name.lastIndexOf(BuildConfig.SUFFIX));
        Integer version = Integer.valueOf(value);
        dependencies.put(library, version);
    }
}
