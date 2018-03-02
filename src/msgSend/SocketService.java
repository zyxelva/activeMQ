package msgSend;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Logger;
import org.dom4j.DocumentException;

import msgSend.xmlsend.XmlUtil;



/**
 * 鍚屾鐘舵?佸嚱鏁? 2017.2.20
 *
 * @author Zyxin
 *
 */
public class SocketService {
	private static Logger log = Logger.getLogger(SocketService.class);
	public final static Properties pps = new Properties();
	private static Properties pps2 = new Properties();
	private static String cmdserverIp;
	private static String cmdserverPort;
	private static int cmdTimeout;
	private static final Log logger =  LogFactory.getLog("INFO");

	static {
		String classpath = SocketService.class.getResource("/").getPath();
		InputStream in = null;
//		try {
//			in = new BufferedInputStream(new FileInputStream(classpath + "msgid.properties"));
//			pps.load(in);
//		} catch (IOException e) {
//			e.printStackTrace();
//		} finally {
//			try {
//				if (null != in) {
//					in.close();
//				}
//			} catch (Exception e) {
//				log.error("The file stream closed and an exception occurred");
//			}
//		}
		try {
			in = new BufferedInputStream(new FileInputStream(classpath + "cmdserverconfig.properties"));
			pps2.load(in);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (null != in) {
					in.close();
				}
			} catch (Exception e) {
				log.error("The file stream closed and an exception occurred");
			}
		}
		SocketService.cmdserverIp = pps2.getProperty("cmdserver.ip");
		SocketService.cmdserverPort = pps2.getProperty("cmdserver.port");
		SocketService.cmdTimeout=Integer.parseInt(pps2.getProperty("cmdtimeout"));
	}


	 @SuppressWarnings({ "unchecked", "rawtypes" })
	 public static void main(String[] args) throws IOException{
		 Map map = new HashMap();
			map.put("txnName", "ATM_KEY_PRINT");
			map.put("termID", "12345678");
			map.put("user", "zyx");
			Map map2=new HashMap();
			map2.put("txnName", "ATM_KEY_PRINT");
			map2.put("termID", "12345678");
			map.put("param", map2);
			try {
				 new SocketService().sendMap(map);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
	 }

	

	/**
	 * 鍔熻兘锛氬彂閫佺鐞嗙被浜ゆ槗銆佹墦鍗板瘑閽ャ?丷eload鎶ユ枃
	 * @param paramsMap
	 * @return
	 * @throws InterruptedException
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Map sendMap(Map paramsMap) throws InterruptedException {
		//20171204 灞忚斀鍒涘缓鏈娇鐢ㄥ璞?
		//XmlUtil xmlUtil = new XmlUtil();
		Map map = new HashMap();

		//璇嗗埆txnName鏄惁涓庨厤缃枃浠剁殑鍖归厤涓?
		if(paramsMap.get("txnName") == null || "".equals(paramsMap.get("txnName").toString().trim())){
			return null;
		}
		String txnID = SocketService.pps.getProperty(paramsMap.get("txnName").toString().trim());
		if(txnID == null || "".equals(txnID) || "null".equals(txnID)){
			//throw new BusinessException("E0000000");
		}
		map.put("txn_id", txnID);
		map.put("param", paramsMap.get("param"));

		//灏嗘姤鏂囪浆鍙樹负xml鏍煎紡鐨勫瓧绗︿覆
		String xml = "";
		Map receivedMap=null;
		try {
			xml = XmlUtil.formatXml(XmlUtil.map2xml(map, "cmdserver"));
			Pattern p = Pattern.compile("\\s*|\t|\r|\n");
			Matcher m = p.matcher(xml);
			xml = m.replaceAll("");
			//System.out.println(String.format("%08d",xml.getBytes("utf-8").length)+xml);

			// 寮?濮嬪彂閫佹姤鏂囷紝骞舵帴鏀跺洖澶嶇殑鎶ユ枃
			receivedMap = sendXml(cmdserverIp, cmdserverPort, String.format("%08d",xml.getBytes("utf-8").length)+xml, txnID);
		} catch (DocumentException e) {
			//throw new BusinessException("E0000000");
		} catch (IOException e) {
			//throw new BusinessException("E0000000");
		}


		if(receivedMap == null|| receivedMap.isEmpty()){
			//throw new BusinessException("E1060005");
		}
		receivedMap.put("txnID", txnID);
		return receivedMap;
	}

	/**
	 * 鍔熻兘锛氬悜CmdServer鍙戦?亁ml鎶ユ枃
	 * @param IP
	 * @param PORT
	 * @param XML
	 * @return
	 * @throws InterruptedException
	 */
	@SuppressWarnings({ "unchecked" })
	public Map sendXml(String IP, String PORT, String XML, String txnID) throws InterruptedException {
		OutputStream os = null;
		InputStream is = null;
		Map<String, Object> map=new HashMap<String, Object>();
		Socket s2 = null;
		// 鎺ユ敹server鍙戦?佽繃鏉ョ殑xml鎶ユ枃
		ByteArrayOutputStream bo = new ByteArrayOutputStream();// 鐢ㄦ潵鏆傛椂瀛樻斁鎺ユ敹鍒扮殑鏁版嵁鐨勫瓧鑺傛暟缁勬祦
		try {
			s2 = new Socket(IP, Integer.parseInt(PORT));
			s2.setSoTimeout(cmdTimeout * 1000);
			// 鍙戦?亁ml鎶ユ枃鍒皊erver
			os = s2.getOutputStream();
			DataOutputStream dos = new DataOutputStream(os);
			dos.write(XML.getBytes("utf-8"));

			int size = 8;
			byte b[] = new byte[size];
			is = s2.getInputStream();

			if (is == null) {
				throw new Exception();
			}
			int length = 0;
			length = is.read(b);
			if(length<0){
				throw new Exception();
			}
			bo.write(b, 0, length);
			String resp = new String(bo.toByteArray());
			size = Integer.parseInt(resp);
			int offset = 0;
			String respbf = new String();
			b = new byte[size];
			while(offset < size){
				bo.reset();
				length = is.read(b);
				offset += length;
				respbf += new String(b).substring(0, length);
			}
			if (StringUtils.isNotEmpty(respbf)) {
				if("601".equals(txnID) && respbf.contains("\n")){
					map.put("log", respbf.substring(respbf.indexOf("<![CDATA") + 9, respbf.lastIndexOf("\n")).replace("\"", "\\\""));
					//System.out.println(map.get("log"));
				} else {
					map = XmlUtil.xml2map(respbf.toString(), false);
				}
			}
		} catch (ConnectException connExc) {
			System.out.println("ConnectException");
			//throw new BusinessException("E1060005");
		} catch (SocketTimeoutException timeOutExc) {
			System.out.println("SocketTimeout");
			map.put("response_code", "-2");
		} catch (IOException e) {
			System.out.println("IOException");
			//throw new BusinessException("E1060005");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			//20171018 fortity 瀵逛簬杩炴帴寮傚父鏃讹紝瑕佸鍏惰祫婧愯繘琛屽叧闂?
			try {
				if(is != null){
					is.close();
					is = null;
				}
				if(bo != null){
					bo.close();
					bo = null;
				}
				if(os != null){
					os.close();
					os = null;
				}
				if(s2 != null){
					s2.close();
					s2 = null;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return map;
	}
}
