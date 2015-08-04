package akatuki.kaede.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URI;
import java.net.URL;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Environment;
import android.os.StatFs;
import android.os.storage.StorageManager;
import android.text.format.Formatter;
import android.util.Log;

public class KaedeUtil {
	final public static String TAG = "AK_KaedeUtil";

	/******************** Model Class ********************/
	public static class StorageModel {
		public Boolean isEnable;
		public String path;
		public long totalCapacity;
		public String totalCapacityMessage;
		public long availableCapacity;
		public String availableCapacityMessage;

		public StorageModel(Boolean isEnable, String path, long totalCapacity, String totalCapacityMessage, long availableCapacity,
				String availableCapacityMessage) {
			this.isEnable = isEnable;
			this.path = path;
			this.totalCapacity = totalCapacity;
			this.totalCapacityMessage = totalCapacityMessage;
			this.availableCapacity = availableCapacity;
			this.availableCapacityMessage = availableCapacityMessage;
		}

		@Override
		public String toString() {
			// TODO Auto-generated method stub
			String string = String.format("enable:%s ,path:%s, total_capacity:%s, total_message:%s, available_capacity:%s, available_message:%s",
					new Object[] { isEnable.toString(), path, String.valueOf(totalCapacity), totalCapacityMessage, String.valueOf(availableCapacity),
							availableCapacityMessage });
			return string;
		}

	}

	public static class SSLSocketFactoryEx extends SSLSocketFactory {
		SSLContext sslContext = SSLContext.getInstance("TLS");

		public SSLSocketFactoryEx(KeyStore truststore) throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException,
				UnrecoverableKeyException {
			super(truststore);
			TrustManager tm = new X509TrustManager() {
				public java.security.cert.X509Certificate[] getAcceptedIssuers() {
					return null;
				}

				@Override
				public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType)
						throws java.security.cert.CertificateException {
				}

				@Override
				public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType)
						throws java.security.cert.CertificateException {
				}
			};
			sslContext.init(null, new TrustManager[] { tm }, null);
		}

		@Override
		public Socket createSocket(Socket socket, String host, int port, boolean autoClose) throws IOException, UnknownHostException {
			return sslContext.getSocketFactory().createSocket(socket, host, port, autoClose);
		}

		@Override
		public Socket createSocket() throws IOException {
			return sslContext.getSocketFactory().createSocket();
		}
	}

	/******************** Method ********************/
	public static Boolean downloadImageToSd(String urlSource, String pathDest) throws IOException {
		HttpURLConnection mHttpURLConnection = null;
		FileOutputStream mFileOutputStream = null;
		BufferedOutputStream mBufferedOutputStream = null;
		BufferedInputStream mBufferedInputStream = null;
		File imageFile = null;

		URL url = new URL(urlSource);
		mHttpURLConnection = (HttpURLConnection) url.openConnection();
		mHttpURLConnection.setConnectTimeout(5 * 1000);
		mHttpURLConnection.setReadTimeout(15 * 1000);
		mHttpURLConnection.setDoInput(true);
		mHttpURLConnection.setDoOutput(true);
		mBufferedInputStream = new BufferedInputStream(mHttpURLConnection.getInputStream());
		imageFile = new File(pathDest);
		mFileOutputStream = new FileOutputStream(imageFile);
		mBufferedOutputStream = new BufferedOutputStream(mFileOutputStream);
		byte[] buffer = new byte[1024];
		int length;
		while ((length = mBufferedInputStream.read(buffer)) != -1) {
			mBufferedOutputStream.write(buffer, 0, length);
			mBufferedOutputStream.flush();
		}
		return true;

	}

	public static Boolean downloadImageToSd(String urlSource, String pathDest, String referer) throws IOException {
		HttpURLConnection mHttpURLConnection = null;
		FileOutputStream mFileOutputStream = null;
		BufferedOutputStream mBufferedOutputStream = null;
		BufferedInputStream mBufferedInputStream = null;
		File imageFile = null;

		URL url = new URL(urlSource);
		mHttpURLConnection = (HttpURLConnection) url.openConnection();
		mHttpURLConnection.setRequestProperty("referer", referer);
		mHttpURLConnection.setConnectTimeout(5 * 1000);
		mHttpURLConnection.setReadTimeout(15 * 1000);
		mHttpURLConnection.setDoInput(true);
		mHttpURLConnection.setDoOutput(true);
		mBufferedInputStream = new BufferedInputStream(mHttpURLConnection.getInputStream());
		imageFile = new File(pathDest);
		mFileOutputStream = new FileOutputStream(imageFile);
		mBufferedOutputStream = new BufferedOutputStream(mFileOutputStream);
		byte[] buffer = new byte[1024];
		int length;
		while ((length = mBufferedInputStream.read(buffer)) != -1) {
			mBufferedOutputStream.write(buffer, 0, length);
			mBufferedOutputStream.flush();
		}
		return true;

	}

	public static String getSdPath() {
		File pathSdDir = null;
		boolean sdCardExist = Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED); // 判断sd卡是否存在
		if (sdCardExist) {
			pathSdDir = Environment.getExternalStorageDirectory();// 获取跟目录，最后没有加File.seperator
		} else {
			return null;
		}
		return pathSdDir.toString();
	}

	public static class Comparator_StorageModel implements Comparator<StorageModel> {

		@Override
		public int compare(StorageModel lhs, StorageModel rhs) {

			if (lhs.availableCapacity > rhs.availableCapacity) {
				return -1;
			}
			if (lhs.availableCapacity == rhs.availableCapacity) {
				return 0;
			}

			return 1;

		}
	}

	public static ArrayList<StorageModel> getSdPathMutil(Context context) throws Exception {
		ArrayList<StorageModel> list_StorageModel = new ArrayList<StorageModel>();
		StorageManager mStorageManager = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);

		String[] paths = (String[]) mStorageManager.getClass().getMethod("getVolumePaths", null).invoke(mStorageManager, null);
		for (int i = 0; i < paths.length; i++) {
			StatFs mStatFs = new StatFs(paths[i]);
			long blockSize = mStatFs.getBlockSize();
			long totalBlocks = mStatFs.getBlockCount();
			long availableBlocks = mStatFs.getAvailableBlocks();
			long totalCapacity = blockSize * totalBlocks;
			long availableCapacity = availableBlocks * blockSize;
			Boolean isEnable = false;
			String totalCapacityMessage = Formatter.formatFileSize(context, totalCapacity);
			String availableCapacityMessage = Formatter.formatFileSize(context, availableCapacity);

			String status = (String) mStorageManager.getClass().getMethod("getVolumeState", String.class).invoke(mStorageManager, paths[i]);
			if (status.equals(android.os.Environment.MEDIA_MOUNTED)) {
				isEnable = true;
			}
			StorageModel storageModel = new StorageModel(isEnable, paths[i], totalCapacity, totalCapacityMessage, availableCapacity,
					availableCapacityMessage);
			list_StorageModel.add(storageModel);
		}
		Comparator_StorageModel comparoter = new Comparator_StorageModel();
		Collections.sort(list_StorageModel, comparoter);
		return list_StorageModel;

	}

	public static boolean copyFileFromAssetsToSd(Context context, String fileName, String path) {
		boolean copyIsFinish = false;
		try {
			InputStream is = context.getAssets().open(fileName);
			File file = new File(path);
			file.createNewFile();
			FileOutputStream fos = new FileOutputStream(file);
			byte[] temp = new byte[1024];
			int i = 0;
			while ((i = is.read(temp)) > 0) {
				fos.write(temp, 0, i);
			}
			fos.close();
			is.close();
			copyIsFinish = true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return copyIsFinish;
	}

	public static boolean isApkInstalled(Context context, String packageName) {
		PackageInfo packageInfo = null;

		try {
			packageInfo = context.getPackageManager().getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
		} catch (NameNotFoundException e) {
			packageInfo = null;
			e.printStackTrace();
		}
		if (packageInfo == null) {
			return false;
		} else {
			Log.e(TAG, packageInfo.toString());
			return true;
		}
	}

	public static String getHtml(String url) throws IOException {
		URL mUrl = new URL(url);
		HttpURLConnection connection = (HttpURLConnection) mUrl.openConnection();
		// 设置请求方式为GET方式，就是相当于浏览器打开百度网页
		connection.setRequestMethod("GET");
		// 接着设置超时时间为10秒，10秒内若连接不上，则通知用户网络连接有问题
		connection.setReadTimeout(10000);
		// 若连接上之后，得到网络的输入流，内容就是网页源码的字节码
		InputStream mInputStream = connection.getInputStream();
		// 必须将其转换为字符串才能够正确的显示给用户
		ByteArrayOutputStream mByteArrayOutputStream = new ByteArrayOutputStream();// 新建一字节数组输出流
		byte[] buffer = new byte[1024];// 在内存中开辟一段缓冲区，接受网络输入流
		int len = 0;
		while ((len = mInputStream.read(buffer)) != -1) {
			mByteArrayOutputStream.write(buffer, 0, len);// 缓冲区满了之后将缓冲区的内容写到输出流
		}
		mInputStream.close();
		connection.disconnect();
		return new String(mByteArrayOutputStream.toByteArray(), "utf-8");
	}

	public static String getHtml(String str, String referer) throws IOException {

		URL mUrl = new URL(str);
		HttpURLConnection connection = (HttpURLConnection) mUrl.openConnection();
		// 设置请求方式为GET方式，就是相当于浏览器打开百度网页
		connection.setRequestMethod("GET");
		// 接着设置超时时间为5秒，5秒内若连接不上，则通知用户网络连接有问题
		connection.setReadTimeout(10000);
		// 若连接上之后，得到网络的输入流，内容就是网页源码的字节码
		// connection.setRequestProperty("User-Agent",
		// "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1; Maxthon;)");
		// connection.setRequestProperty("Accept-Encoding", "gzip");
		connection.setRequestProperty("referer", referer);
		// connection.setRequestProperty("cookie",
		// "http://control.blog.sina.com.cn");
		InputStream mInputStream = connection.getInputStream();
		// 必须将其转换为字符串才能够正确的显示给用户
		ByteArrayOutputStream mByteArrayOutputStream = new ByteArrayOutputStream();// 新建一字节数组输出流
		byte[] buffer = new byte[1024];// 在内存中开辟一段缓冲区，接受网络输入流
		int len = 0;
		while ((len = mInputStream.read(buffer)) != -1) {
			mByteArrayOutputStream.write(buffer, 0, len);// 缓冲区满了之后将缓冲区的内容写到输出流
		}
		mInputStream.close();
		return new String(mByteArrayOutputStream.toByteArray(), "utf-8");
	}

	public static String getHtmlHttps(Context context, String url) throws Exception {
		InputStream mInputStream = null;
		String html = null;

		mInputStream = context.getAssets().open("yandere.cer"); // 下载的证书放到项目中的assets目录中
		CertificateFactory mCertificateFactory = CertificateFactory.getInstance("X.509");
		Certificate certificate = mCertificateFactory.generateCertificate(mInputStream);
		KeyStore keyStore = KeyStore.getInstance("PKCS12", "BC");
		keyStore.load(null, null);
		keyStore.setCertificateEntry("trust", certificate);

		SSLSocketFactory socketFactory = new SSLSocketFactory(keyStore);
		Scheme scheme = new Scheme("https", socketFactory, 443);
		HttpClient mHttpClient = new DefaultHttpClient();
		mHttpClient.getConnectionManager().getSchemeRegistry().register(scheme);

		BufferedReader mBufferedReader = null;

		Log.e(TAG, "get https: " + url);
		HttpGet httpGet = new HttpGet();
		httpGet.setURI(new URI(url));
		HttpResponse response = mHttpClient.execute(httpGet);
		if (response.getStatusLine().getStatusCode() != 200) {
			httpGet.abort();
			return html;
		}

		mBufferedReader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
		StringBuffer buffer = new StringBuffer();
		String line = null;
		while ((line = mBufferedReader.readLine()) != null) {
			buffer.append(line);
		}
		html = buffer.toString();

		return html;
	}

	public static String getHtmlHttps(String url) {
		String html = null;
		// 创建httpClient对象
		HttpClient httpClient = getHttpsClient();
		// 以get方式请求该URL
		HttpGet httpget = new HttpGet(url);
		try {
			// 得到responce对象
			HttpResponse responce = httpClient.execute(httpget);
			// 返回码
			int resStatu = responce.getStatusLine().getStatusCode();
			// 200正常 其他就不对
			if (resStatu == HttpStatus.SC_OK) {
				// 获得相应实体
				HttpEntity entity = responce.getEntity();
				if (entity != null) {
					// 获得html源代码
					html = EntityUtils.toString(entity);
				}
			}
		} catch (Exception e) {
			System.out.println("访问【" + url + "】出现异常!");
			e.printStackTrace();
		} finally {
			httpClient.getConnectionManager().shutdown();
		}
		return html;
	}

	public static HttpClient getHttpsClient() {
		try {
			KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
			trustStore.load(null, null);

			SSLSocketFactory sf = new SSLSocketFactoryEx(trustStore);
			sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

			HttpParams params = new BasicHttpParams();
			HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
			HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);

			SchemeRegistry registry = new SchemeRegistry();
			registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
			registry.register(new Scheme("https", sf, 443));

			ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, registry);

			return new DefaultHttpClient(ccm, params);
		} catch (Exception e) {
			return new DefaultHttpClient();
		}
	}

}
