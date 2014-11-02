package nf.framework.core.http;

/**
 * $id$
 * Copyright 2012 Inc. All rights reserved.
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import nf.framework.core.exception.NFRuntimeException;
import nf.framework.core.util.io.FileUtils;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.util.EntityUtils;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;

/**
 * 
 * �ӿ���������ĳ�����
 * 
 * @author niufei
 * 
 */
public abstract class AbstractHttpService {

	public static final String UTF_8 = "UTF-8";
	public static final String DESC = "descend";
	public static final String ASC = "ascend";

	private final static int TIMEOUT_CONNECTION = 15000;
	private final static int TIMEOUT_SOCKET = 20000;
	private final static int RETRY_TIME = 2;
	private Context context;
	public AbstractHttpService(Context context) {
		this.context = context;
	}
	
	public void checkNullParams(String... params) throws NFRuntimeException {

		for (String param : params) {
			if (TextUtils.isEmpty(param)) {
				String errorMsg ="�����������Ϊ��";
				throw new NFRuntimeException(errorMsg);
			}
		}
	}

	/**
	 * post������ֵ
	 * 
	 * @param url
	 * @param list
	 * @return
	 * @throws Exception
	 *             1.����HttpGet��HttpPost���󣬽�Ҫ�����URLͨ�����췽������HttpGet��HttpPost����
	 *             2.ʹ��DefaultHttpClient���execute��������HTTP GET��HTTP
	 *             POST���󣬲�����HttpResponse����
	 *             3.ͨ��HttpResponse�ӿڵ�getEntity����������Ӧ��Ϣ����������Ӧ�Ĵ���
	 */
	public String getDataByHttp(String url, ArrayList<NameValuePair> list) {
		String responseBody = "";
		InputStream inputStream = null;
		HttpClient httpClient = null;
		PostMethod httpPost = null;
		int time = 0;
		do {
			try {
				// ����һ��http�ͻ��˶���
				httpClient = getHttpClient();// ��������
				httpPost = getHttpPost(url, null, null);
				if (list != null) {
					NameValuePair[] nameValues = new NameValuePair[] {};
					httpPost.addParameters(list.toArray(nameValues));
				}
//				String saveUrl = getALLInterfaceUrl(url, list);
//				SaveInterfaceUrlToFile(saveUrl);
				int statusCode = httpClient.executeMethod(httpPost);
				if (statusCode == HttpStatus.SC_OK) {
					inputStream = httpPost.getResponseBodyAsStream();
					responseBody = inputStream2String(inputStream);
					// System.out.println("XMLDATA=====>"+responseBody);
					break;
				} else {
					 //��post��ַת��Ϊget��ַ�����ұ��浽�洢���е��б���
					String requestUrl = getALLInterfaceUrl(url, list);
//					throw new XingshulinError(DEBUG_TAG_POST + ":::"+ statusCode);
				}
		
			} catch (HttpException e) {
				time++;
				if (time < RETRY_TIME) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e1) {
					}
					continue;
				}
				// �����������쳣��������Э�鲻�Ի��߷��ص�����������
				e.printStackTrace();
			} catch (IOException e) {
				time++;
				if (time < RETRY_TIME) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e1) {
					}
					continue;
				}
				// ���������쳣
				e.printStackTrace();
			} finally {
				if (inputStream != null) {
					try {
						inputStream.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
					}
				}
				// �ͷ�����
				httpPost.releaseConnection();
				httpClient = null;
			}
		} while (time < RETRY_TIME);
		return responseBody;
	}

	/**
	 * get������ֵ
	 * 
	 * @param uripath
	 * @return
	 * @throws Exception
	 *             1.����HttpGet��HttpPost���󣬽�Ҫ�����URLͨ�����췽������HttpGet��HttpPost����
	 *             2.ʹ��DefaultHttpClient���execute��������HTTP GET��HTTP
	 *             POST���󣬲�����HttpResponse����
	 *             3.ͨ��HttpResponse�ӿڵ�getEntity����������Ӧ��Ϣ����������Ӧ�Ĵ���
	 */
	public String getNet(String uripath, String enconding) throws Exception {
		InputStream inputStream = null;
		// ����һ��http�ͻ��˶���
		HttpClient httpClient = getHttpClient();// ��������
		String responseBody = null;
		GetMethod httpGet = null;
		int time = 0;
		do {
			try {
				httpGet = getHttpGet(uripath, null, null);
				int statusCode = httpClient.executeMethod(httpGet);
				// �ж������Ƿ�ɹ�����
				if (statusCode == HttpStatus.SC_OK) {
					inputStream = httpGet.getResponseBodyAsStream();
					responseBody = inputStream2String(inputStream);
					break;
				} else {
//					throw new XingshulinError(DEBUG_TAG_GET + ":::"
//							+ statusCode);
				}
			} catch (HttpException e) {
				time++;
				if (time < RETRY_TIME) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e1) {
					}
					continue;
				}
				// �����������쳣��������Э�鲻�Ի��߷��ص�����������
				e.printStackTrace();
			} catch (IOException e) {
				time++;
				if (time < RETRY_TIME) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e1) {
					}
					continue;
				}
				// ���������쳣
				e.printStackTrace();
			} finally {
				if (inputStream != null) {
					try {
						inputStream.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
					}
				}
				// �ͷ�����
				httpGet.releaseConnection();
				httpClient = null;
			}
		} while (time < RETRY_TIME);
		return responseBody;
	}


	private static HttpClient getHttpClient() {
		HttpClient httpClient = new HttpClient();
		// ���� HttpClient ���� Cookie,���������һ���Ĳ���
		httpClient.getParams().setCookiePolicy(
				CookiePolicy.BROWSER_COMPATIBILITY);
		// ���� Ĭ�ϵĳ�ʱ���Դ������
		httpClient.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
				new DefaultHttpMethodRetryHandler());
		// ���� ���ӳ�ʱʱ��
		httpClient.getHttpConnectionManager().getParams()
				.setConnectionTimeout(TIMEOUT_CONNECTION);
		// ���� �����ݳ�ʱʱ��
		httpClient.getHttpConnectionManager().getParams()
				.setSoTimeout(TIMEOUT_SOCKET);
		// ���� �ַ���
		httpClient.getParams().setContentCharset(UTF_8);
		return httpClient;
	}

	private static GetMethod getHttpGet(String url, String cookie,
			String userAgent) {
		GetMethod httpGet = new GetMethod(url);
		// ���� ����ʱʱ��
		httpGet.getParams().setSoTimeout(TIMEOUT_SOCKET);
		httpGet.setRequestHeader("Connection", "Keep-Alive");
		httpGet.setRequestHeader("Cookie", cookie);
		httpGet.setRequestHeader("User-Agent", userAgent);
		return httpGet;
	}

	private static PostMethod getHttpPost(String url, String cookie,
			String userAgent) {
		PostMethod httpPost = new PostMethod(url);
		// ���� ����ʱʱ��
		httpPost.getParams().setSoTimeout(TIMEOUT_SOCKET);
		httpPost.setRequestHeader("Connection", "Keep-Alive");
		httpPost.setRequestHeader("Cookie", cookie);
		httpPost.setRequestHeader("User-Agent", userAgent);
		return httpPost;
	}

	protected String getALLInterfaceUrl(String url, List<NameValuePair> list) {
		StringBuffer sb = new StringBuffer();
		if (url == null) {
			throw new NFRuntimeException("�ӿڵ�ַ·������Ϊ��");
		}
		if (list == null) {
			throw new NFRuntimeException("�ӿڵ�ַ�����б������Ϊ��");
		}
		sb.append(url);
		for (NameValuePair nameValue : list) {
			sb.append(nameValue.getName());
			sb.append("=");
			sb.append(nameValue.getValue());
			sb.append("&");
		}
		String urlStr = sb.toString();
		urlStr = urlStr.substring(0, urlStr.length() - 1);
//		System.out.println(urlStr);
		return urlStr;
	}

	/**
	 * post �ļ��ϴ��ӿ�
	 * 
	 * @param url
	 * @param nameValuePairs
	 * @return
	 * @throws IOException 
	 * @throws ClientProtocolException 
	 */
	public String PostFileByHttp(String url, List<NameValuePair> nameValuePairs) throws ClientProtocolException, IOException {
		String result = null;
	
		org.apache.http.client.HttpClient httpClient = new DefaultHttpClient();
		// ����ͨ��Э��汾
		httpClient.getParams().setParameter(
				CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
		HttpPost httpPost = new HttpPost(url);
		httpClient.getParams().setIntParameter(
				CoreConnectionPNames.CONNECTION_TIMEOUT, TIMEOUT_CONNECTION);
		// ���ö����ݳ�ʱʱ��(��λ����)
		httpClient.getParams().setIntParameter(CoreConnectionPNames.SO_TIMEOUT,
				TIMEOUT_SOCKET);
				
		MultipartEntity entity = new MultipartEntity(
				HttpMultipartMode.BROWSER_COMPATIBLE);
		for (int index = 0; index < nameValuePairs.size(); index++) {
			if (nameValuePairs.get(index).getName().equalsIgnoreCase("image")) {
				// If the key equals to "image", we use FileBody to transfer
				// the data
				if (nameValuePairs.get(index).getValue() != null) {
					entity.addPart(nameValuePairs.get(index).getName(),
							new FileBody(new File(nameValuePairs.get(index)
									.getValue())));
				}
			} else {
				// Normal string data
				entity.addPart(nameValuePairs.get(index).getName(),
						new StringBody(nameValuePairs.get(index).getValue()));
			}
		}
		httpPost.setEntity(entity);
		HttpResponse response = httpClient.execute(httpPost);// ������Ӧ
		// �ж������Ƿ�ɹ�����
		if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
			// �������ص�����
			result = EntityUtils.toString(response.getEntity(), "utf-8");
		} else {
			// //��post��ַת��Ϊget��ַ�����ұ��浽�洢���е��б���
			String requestUrl = getALLInterfaceUrl(url, nameValuePairs);
			int errorReponseCode = response.getStatusLine().getStatusCode();
			httpClient.getConnectionManager().shutdown();
		}
		return result;
	}
	
	/**
	 * 
	 * @param urlStr
	 */
	private void SaveInterfaceUrlToFile(String urlStr) {
		FileUtils fileHelper = FileUtils.getInstance();
		try {
			File file = new File(Environment.getExternalStorageDirectory()
					.getPath() + "/url.txt");
			if (!file.exists()) {
				file.createNewFile();
			}
			String fileStr = fileHelper.read(file);
			StringBuffer sb2 = new StringBuffer(fileStr);
			sb2.append("\r\n");
			sb2.append(urlStr);
			fileHelper.write(file, sb2.toString());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public String inputStream2String(InputStream in) throws IOException {
		if (in != null) {
			BufferedReader bf = new BufferedReader(new InputStreamReader(in,
					"UTF-8"));
			// ����ڽ��ֽ���ת��Ϊ�ַ�����ʱ�� ����ת��
			StringBuffer buffer = new StringBuffer();
			String line = "";
			while ((line = bf.readLine()) != null) {
				buffer.append(line);
			}
			bf.close();
			return buffer.toString();
		}
		return "";
	}
}
