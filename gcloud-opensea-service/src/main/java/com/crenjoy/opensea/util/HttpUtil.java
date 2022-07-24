package com.crenjoy.opensea.util;

import java.io.File;
import java.io.IOException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HeaderElement;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.httpclient.util.EncodingUtil;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import com.crenjoy.common.util.HttpClientUtil;

/**
 * 采用HttpClient 实现http请求
 * 
 * @author CGD
 * 
 */
public class HttpUtil extends HttpClientUtil {

	/**
	 * GET请求
	 * @param url
	 * @return
	 * @throws HttpException
	 * @throws IOException
	 */
	public static String sendGet(String url) throws HttpException, IOException{
	   return  sendGet(url,null,"UTF-8");
	}
	
	/**
	 * 发送POST 请求
	 * @param url
	 * @param body
	 * @return
	 * @throws HttpException
	 * @throws IOException
	 */
	public static String sendPost(String url,String body)throws HttpException,IOException{
		  return  sendPost(url,body,"UTF-8");
	}
	/**
	 * 发送POST文件请求
	 * @param url
	 * @param fileParam
	 * @param file
	 * @param params
	 * @param charsetName
	 * @param timeout
	 * @return
	 * @throws HttpException
	 * @throws IOException
	 */
	public static String sendPostFile(String url,String fileParam,File file, Map<String, String> params,String charsetName,Integer timeout) throws HttpException,IOException{
		PostMethod postMethod = new PostMethod(url);
		postMethod.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, charsetName);
		List<Part> list = new ArrayList<Part>();
		// FilePart：用来上传文件的类
		list.add(new FilePart(fileParam, file));
		// ParamPart
		if (null!=params){
			for (Map.Entry<String, String> param : params.entrySet()) {
				list.add(new StringPart(param.getKey(), param.getValue()));
			}
		}
		// 对于MIME类型的请求,HttpClient建议全用MulitPartRequestEntity进行包装
		MultipartRequestEntity mre = new MultipartRequestEntity(list.toArray(new Part[0]), postMethod.getParams());
		postMethod.setRequestEntity(mre);
		HttpClient client = new HttpClient();
		// 设置连接时间
		if (null!=timeout){
			client.getHttpConnectionManager().getParams().setConnectionTimeout(timeout);
		}
		int status = client.executeMethod(postMethod);
		if (status == HttpStatus.SC_OK) {
			if (StringUtils.isEmpty(charsetName)){
    			return postMethod.getResponseBodyAsString();
    		}else{
    			return EncodingUtil.getString(postMethod.getResponseBody(), charsetName);
    		}
		}
		throw new HttpException("HTTP Response Code" + String.valueOf(status));
	}
	/**
	 * 发送POST File
	 * @param url
	 * @param fileParam
	 * @param file
	 * @return
	 * @throws HttpException
	 * @throws IOException
	 */
	public static String sendPostFile(String url,String fileParam,File file) throws HttpException,IOException{
		return sendPostFile(url,fileParam,file,null,"UTF-8",50000);
	}

	/**
	 * 保存文件到某路径
	 * @param url
	 * @param path
	 * @return
	 * @throws IOException
	 */
	public static String  sendGetToFile(String url,String path) throws IOException{
		HttpClient client = new HttpClient();
		GetMethod httpGet = new GetMethod(url);
		String fileName=null;
		client.executeMethod(httpGet);

		Header header = httpGet.getResponseHeader("Content-disposition");
		HeaderElement[] elements = header.getElements();
		if(elements.length==1){
			NameValuePair param = elements[0].getParameterByName("filename");  
			if (null != param) {
				fileName = param.getValue();
			}
		}

		if (StringUtils.isEmpty(fileName)) {
			return httpGet.getResponseBodyAsString();
		}

		String fullFileName=path+File.separator+fileName;

		File file = new File(fullFileName);
		if (file.exists()) {
			return fullFileName;
		}
		
		FileUtils.copyInputStreamToFile(httpGet.getResponseBodyAsStream(), file);
		
	    return fullFileName;
	}


	public static class HttpException extends IOException {

		private static final long serialVersionUID = 1L;

		private HttpException() {
			super();
		}

		private HttpException(String message, Throwable cause) {
			super(message, cause);
		}

		private HttpException(String message) {
			super(message);
		}

		private HttpException(Throwable cause) {
			super(cause);
		}

	}

	@SuppressWarnings("unused")
	private static TrustManager myX509TrustManager = new X509TrustManager() {

		public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {

		}

		public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {

		}

		public X509Certificate[] getAcceptedIssuers() {
			return null;
		}

	};
}
