package org.easystogu.http.util;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * HTTP������.
 * 
 * @author David.Huang
 */
public class HttpUtil {

	private static Logger log = LoggerFactory.getLogger(HttpUtil.class);

	/** Ĭ�ϱ��뷽ʽ -UTF8 */
	private static final String DEFAULT_ENCODE = "utf-8";

	// ��������վ��
	static {
		SSLUtil.trustAllHostnames();
		SSLUtil.trustAllHttpsCertificates();
	}

	/**
	 * ���췽��
	 */
	public HttpUtil() {
		// empty constructor for some tools that need an instance object of the
		// class
	}

	/**
	 * GET����, ������ַ�����ʽ����.
	 * 
	 * @param url
	 *            �����ַ
	 * @return �����ַ���
	 */
	public static String getUrlAsString(String url) throws Exception {
		return getUrlAsString(url, null, DEFAULT_ENCODE);
	}

	/**
	 * GET����, ������ַ�����ʽ����.
	 * 
	 * @param url
	 *            �����ַ
	 * @param params
	 *            �������
	 * @return �����ַ���
	 */
	public static String getUrlAsString(String url, Map<String, String> params)
			throws Exception {
		return getUrlAsString(url, params, DEFAULT_ENCODE);
	}

	/**
	 * GET����, ������ַ�����ʽ����.
	 * 
	 * @param url
	 *            �����ַ
	 * @param params
	 *            �������
	 * @param encode
	 *            ���뷽ʽ
	 * @return �����ַ���
	 */
	public static String getUrlAsString(String url, Map<String, String> params,
			String encode) throws Exception {
		// ��ʼʱ��
		long t1 = System.currentTimeMillis();
		// ���HttpGet����
		HttpGet httpGet = getHttpGet(url, params, encode);
		// ������Ϣ
		log.debug("url:" + url);
		log.debug("params:" + params.toString());
		log.debug("encode:" + encode);
		// ��������
		String result = executeHttpRequest(httpGet, null);
		// ����ʱ��
		long t2 = System.currentTimeMillis();
		// ������Ϣ
		log.debug("result:" + result);
		log.debug("consume time:" + ((t2 - t1)));
		// ���ؽ��
		return result;
	}

	/**
	 * POST����, ������ַ�����ʽ����.
	 * 
	 * @param url
	 *            �����ַ
	 * @return �����ַ���
	 */
	public static String postUrlAsString(String url) throws Exception {
		return postUrlAsString(url, null, null, null);
	}

	/**
	 * POST����, ������ַ�����ʽ����.
	 * 
	 * @param url
	 *            �����ַ
	 * @param params
	 *            �������
	 * @return �����ַ���
	 */
	public static String postUrlAsString(String url, Map<String, String> params)
			throws Exception {
		return postUrlAsString(url, params, null, null);
	}

	/**
	 * POST����, ������ַ�����ʽ����.
	 * 
	 * @param url
	 *            �����ַ
	 * @param params
	 *            �������
	 * @param reqHeader
	 *            ����ͷ����
	 * @return �����ַ���
	 * @throws Exception
	 */
	public static String postUrlAsString(String url,
			Map<String, String> params, Map<String, String> reqHeader)
			throws Exception {
		return postUrlAsString(url, params, reqHeader, null);
	}

	/**
	 * POST����, ������ַ�����ʽ����.
	 * 
	 * @param url
	 *            �����ַ
	 * @param params
	 *            �������
	 * @param reqHeader
	 *            ����ͷ����
	 * @param encode
	 *            ���뷽ʽ
	 * @return �����ַ���
	 * @throws Exception
	 */
	public static String postUrlAsString(String url,
			Map<String, String> params, Map<String, String> reqHeader,
			String encode) throws Exception {
		// ��ʼʱ��
		long t1 = System.currentTimeMillis();
		// ���HttpPost����
		HttpPost httpPost = getHttpPost(url, params, encode);
		// ��������
		String result = executeHttpRequest(httpPost, reqHeader);
		// ����ʱ��
		long t2 = System.currentTimeMillis();
		// ������Ϣ
		log.debug("url:" + url);
		log.debug("params:" + params.toString());
		log.debug("reqHeader:" + reqHeader);
		log.debug("encode:" + encode);
		log.debug("result:" + result);
		log.debug("consume time:" + ((t2 - t1)));
		// ���ؽ��
		return result;
	}

	/**
	 * ���HttpGet����
	 * 
	 * @param url
	 *            �����ַ
	 * @param params
	 *            �������
	 * @param encode
	 *            ���뷽ʽ
	 * @return HttpGet����
	 */
	private static HttpGet getHttpGet(String url, Map<String, String> params,
			String encode) {
		StringBuffer buf = new StringBuffer(url);
		if (params != null) {
			// ��ַ����?����&
			String flag = (url.indexOf('?') == -1) ? "?" : "&";
			// ��Ӳ���
			for (String name : params.keySet()) {
				buf.append(flag);
				buf.append(name);
				buf.append("=");
				try {
					String param = params.get(name);
					if (param == null) {
						param = "";
					}
					buf.append(URLEncoder.encode(param, encode));
				} catch (UnsupportedEncodingException e) {
					log.error("URLEncoder Error,encode=" + encode + ",param="
							+ params.get(name), e);
				}
				flag = "&";
			}
		}
		HttpGet httpGet = new HttpGet(buf.toString());
		return httpGet;
	}

	/**
	 * ���HttpPost����
	 * 
	 * @param url
	 *            �����ַ
	 * @param params
	 *            �������
	 * @param encode
	 *            ���뷽ʽ
	 * @return HttpPost����
	 */
	private static HttpPost getHttpPost(String url, Map<String, String> params,
			String encode) {
		HttpPost httpPost = new HttpPost(url);
		if (params != null) {
			List<NameValuePair> form = new ArrayList<NameValuePair>();
			for (String name : params.keySet()) {
				form.add(new BasicNameValuePair(name, params.get(name)));
			}
			try {
				UrlEncodedFormEntity entity = new UrlEncodedFormEntity(form,
						encode);
				httpPost.setEntity(entity);
			} catch (UnsupportedEncodingException e) {
				log.error("UrlEncodedFormEntity Error,encode=" + encode
						+ ",form=" + form, e);
			}
		}
		return httpPost;
	}

	/**
	 * ִ��HTTP����
	 * 
	 * @param request
	 *            �������
	 * @param reqHeader
	 *            ����ͷ��Ϣ
	 * @return �����ַ���
	 */
	private static String executeHttpRequest(HttpUriRequest request,
			Map<String, String> reqHeader) throws Exception {
		HttpClient client = null;
		String result = null;
		try {
			// ����HttpClient����
			client = new DefaultHttpClient();
			// �������ӳ�ʱʱ��
			client.getParams().setParameter(
					CoreConnectionPNames.CONNECTION_TIMEOUT, 60);
			// ����Socket��ʱʱ��
			client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT,
					36600);
			// ��������ͷ��Ϣ
			if (reqHeader != null) {
				for (String name : reqHeader.keySet()) {
					request.addHeader(name, reqHeader.get(name));
				}
			}
			// ��÷��ؽ��
			HttpResponse response = client.execute(request);
			// ����ɹ�
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				result = EntityUtils.toString(response.getEntity());
			}
			// ���ʧ��
			else {
				StringBuffer errorMsg = new StringBuffer();
				errorMsg.append("httpStatus:");
				errorMsg.append(response.getStatusLine().getStatusCode());
				errorMsg.append(response.getStatusLine().getReasonPhrase());
				errorMsg.append(", Header: ");
				Header[] headers = response.getAllHeaders();
				for (Header header : headers) {
					errorMsg.append(header.getName());
					errorMsg.append(":");
					errorMsg.append(header.getValue());
				}
				log.error("HttpResonse Error:" + errorMsg);
			}
		} catch (Exception e) {
			log.error("http�����쳣", e);
			throw new Exception("http�����쳣");
		} finally {
			try {
				client.getConnectionManager().shutdown();
			} catch (Exception e) {
				log.error("finally HttpClient shutdown error", e);
			}
		}
		return result;
	}

	/**
	 * �����ļ����浽����
	 * 
	 * @param path
	 *            �ļ�����λ��
	 * @param url
	 *            �ļ���ַ
	 * @throws IOException
	 */
	public static void downloadFile(String path, String url) throws IOException {
		log.debug("path:" + path);
		log.debug("url:" + url);
		HttpClient client = null;
		try {
			// ����HttpClient����
			client = new DefaultHttpClient();
			// ���HttpGet����
			HttpGet httpGet = getHttpGet(url, null, null);
			// ���������÷��ؽ��
			HttpResponse response = client.execute(httpGet);
			// ����ɹ�
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				byte[] result = EntityUtils.toByteArray(response.getEntity());
				BufferedOutputStream bw = null;
				try {
					// �����ļ�����
					File f = new File(path);
					// �����ļ�·��
					if (!f.getParentFile().exists())
						f.getParentFile().mkdirs();
					// д���ļ�
					bw = new BufferedOutputStream(new FileOutputStream(path));
					bw.write(result);
				} catch (Exception e) {
					log.error("�����ļ�����,path=" + path + ",url=" + url, e);
				} finally {
					try {
						if (bw != null)
							bw.close();
					} catch (Exception e) {
						log.error(
								"finally BufferedOutputStream shutdown close",
								e);
					}
				}
			}
			// ���ʧ��
			else {
				StringBuffer errorMsg = new StringBuffer();
				errorMsg.append("httpStatus:");
				errorMsg.append(response.getStatusLine().getStatusCode());
				errorMsg.append(response.getStatusLine().getReasonPhrase());
				errorMsg.append(", Header: ");
				Header[] headers = response.getAllHeaders();
				for (Header header : headers) {
					errorMsg.append(header.getName());
					errorMsg.append(":");
					errorMsg.append(header.getValue());
				}
				log.error("HttpResonse Error:" + errorMsg);
			}
		} catch (ClientProtocolException e) {
			log.error("�����ļ����浽����,http�����쳣,path=" + path + ",url=" + url, e);
			throw e;
		} catch (IOException e) {
			log.error("�����ļ����浽����,�ļ������쳣,path=" + path + ",url=" + url, e);
			throw e;
		} finally {
			try {
				client.getConnectionManager().shutdown();
			} catch (Exception e) {
				log.error("finally HttpClient shutdown error", e);
			}
		}
	}

	public static void main(String[] args) throws IOException {
		// String result = getUrlAsString("http://www.gewara.com/");
		// System.out.println(result);
		downloadFile("F:/logo3w.png",
				"http://www.google.com.hk/images/srpr/logo3w.png");
	}
}