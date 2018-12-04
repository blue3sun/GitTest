package com.xy.bizportdemo.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import android.widget.Toast;


import com.xy.bizportdemo.model.MsgInfo;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import cn.com.xy.sms.sdk.log.LogManager;

/**
 * @ClassName FileUtils
 * @Describe 文件的工具类
 * @Author
 * @Time 2018/11/18 9:24
 */
public class FileUtils {
	private static final String TAG = FileUtils.class.getSimpleName();
	private static String encoding = null;
	private static long msgId = 1000000;
	private static final String NODE_NAME= "#cdata-section";

	// param path 文件夹完整绝对路径
	public static boolean delAllFile(String path) {
		boolean flag = false;
		File file = new File(path);
		if (!file.exists()) {
			return flag;
		}
		if (!file.isDirectory()) {
			return flag;
		}
		String[] tempList = file.list();
		if(tempList==null){
			return flag;
		}
		File temp = null;
		for (int i = 0; i < tempList.length; i++) {
			if (path.endsWith(File.separator)) {
				temp = new File(path + tempList[i]);
			} else {
				temp = new File(path + File.separator + tempList[i]);
			}
			if (temp.isFile()) {
				temp.delete();
			}
			if (temp.isDirectory()) {
				delAllFile(path + "/" + tempList[i]);// 先删除文件夹里面的文件
				delFolder(path + "/" + tempList[i]);// 再删除空文件夹
				flag = true;
			}
		}
		return flag;
	}

	public static void delFolder(String folderPath) {
		try {
			delAllFile(folderPath); // 删除完里面所有内容
			String filePath = folderPath;
			filePath = filePath.toString();
			File myFilePath = new File(filePath);
			myFilePath.delete(); // 删除空文件夹
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public static void createFile(String rootPath,String fileName){
		try {
			File file = new File(rootPath, fileName);
			if(!file.exists()) {
				File dir = file.getParentFile();
				if (dir != null && !dir.exists()) {
					dir.mkdirs();
				}
			}else{
				file.delete();
			}

//			File file = new File(rootPath, fileName);
//			if(!file.exists()) {
//				File dir = file.getParentFile();
//				if (dir != null && !dir.exists()) {
//					dir.mkdirs();
//				}
//				file.createNewFile();
//			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public static long getMsgId() {
		msgId++;
		return msgId;
	}
    /**
     *
     * @param file  样例所在的文件
     * @param start 开始位置的样例 如果不需要控制直接传传递-1
     * @param end  结束位置的样例 如果不需要控制直接传传递-1
     * @return  样例列表
     */
    public static synchronized List<MsgInfo> readMmsTextFile(File file, int start, int end) {
        if (file == null || !file.exists()) {
            return null;
        }
        List<MsgInfo> result = new ArrayList<MsgInfo>();
        InputStream inStream = null;
        InputStreamReader inputReader = null;
        BufferedReader buffReader = null;
        if (file.exists()) {
            try {
                inStream = new FileInputStream(file);
                if (inStream != null) {
                    encoding = CharsetUtils.getCode(file.getPath());
                    inputReader = new InputStreamReader(inStream,encoding);
                    buffReader = new BufferedReader(inputReader);
                    int index = 0;
                    String line;
                    String[] sp;
                    MsgInfo mg;
                    while ((line = buffReader.readLine()) != null) {
                        if(start>=0&&end>=0&&(index<start||index>end)){
                            break;
                        }
                        index++;
                        mg = new MsgInfo();
                        try {
                            sp = line.split("\t");
                            mg.setMsgId(getMsgId());
                            mg.setPhone(sp[0]==null?"":sp[0].trim());
                            if (sp.length > 1) {
                                mg.setContent(sp[1]);
                            }
                            mg.setRecieveTime(System.currentTimeMillis());
                            result.add(mg);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (buffReader != null) {
                        buffReader.close();
                        buffReader = null;
                    }
                    if (inputReader != null) {
                        inputReader.close();
                        inputReader = null;
                    }
                    if (inStream != null) {
                        inStream.close();
                        inStream = null;
                    }
                } catch (Exception e2) {
                    e2.printStackTrace();
                }

            }
        }

        return result;
    }

    /**
     *
     * @param file  样例所在的文件
     * @param start 开始位置的样例 如果不需要控制直接传传递-1
     * @param end  结束位置的样例 如果不需要控制直接传传递-1
     * @return  样例列表
     */
    public static synchronized List<MsgInfo> readMmsCSVFile(File file, int start, int end) {
        if (file == null || !file.exists()) {
            return null;
        }
        List<MsgInfo> result = new ArrayList<MsgInfo>();
        InputStream inStream = null;
        InputStreamReader inputReader = null;
        BufferedReader buffReader = null;
        if (file.exists()) {
            try {
                inStream = new FileInputStream(file);
                if (inStream != null) {
                    encoding = CharsetUtils.getCode(file.getPath());
                    inputReader = new InputStreamReader(inStream,encoding);
                    buffReader = new BufferedReader(inputReader);
                    int index = 0;
                    String line;
                    MsgInfo mg;
                    while ((line = buffReader.readLine()) != null) {
                        if(start>=0&&end>=0&&(index<start||index>end)){
                            break;
                        }
                        index++;
                        mg = new MsgInfo();
                        try {
                            String text = "";
                            String[] sp = line.split(",");
                            if (sp.length == 2) {
                                text = sp[1];
                            } else if (sp.length > 2) {
                                StringBuilder sb = new StringBuilder();
                                for (int i = 1; i < sp.length; i++) {
                                    if (i == sp.length - 1) {
                                        sb.append(sp[i]);
                                    } else {
                                        sb.append(sp[i]).append(",");
                                    }
                                }
                                text = sb.toString();
                            }
                            mg.setMsgId(getMsgId());
                            mg.setPhone((sp[0] == null ? "" : sp[0].replace("'", "").trim()));
                            mg.setContent(text);

                            mg.setRecieveTime(System.currentTimeMillis());
                            result.add(mg);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (buffReader != null) {
                        buffReader.close();
                        buffReader = null;
                    }
                    if (inputReader != null) {
                        inputReader.close();
                        inputReader = null;
                    }
                    if (inStream != null) {
                        inStream.close();
                        inStream = null;
                    }
                } catch (Exception e2) {
                    e2.printStackTrace();
                }

            }
        }
        return result;
    }
	public static synchronized int getFileLineCount(File file) {
		int count = 0;
		if (file.exists()) {
			try {
				InputStream inStream = new FileInputStream(file);
				if (inStream != null) {
					InputStreamReader inputReader = new InputStreamReader(inStream);
					BufferedReader buffReader = new BufferedReader(inputReader);
					String line;
					while ((line = buffReader.readLine()) != null) {
						if (!TextUtils.isEmpty(line)) {
							count++;
						}

					}
					inStream.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return count;
	}

	public static List<MsgInfo> loadMessageInfoXML(String path) {
		List<MsgInfo> infoList = new ArrayList<>();
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document dom = builder.parse(new File(path));
			Element root = dom.getDocumentElement();
			NodeList nodeLists = root.getElementsByTagName("string");
			for (int i = 0; i < nodeLists.getLength(); i++) {
				Element item = (Element) nodeLists.item(i);
				String name = item.getAttribute("name");
				String scene = item.getAttribute("scene");
//				LogXY.e(TAG,"解析xml文件1：item.getNodeName()："+item.getNodeName()+"      item.getNodeValue():"+item.getNodeValue());
				String content = getTextContent(item);
				if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(content)) {
					MsgInfo msgInfo = new MsgInfo();
					msgInfo.setMsgId(getMsgId());
					msgInfo.setPhone(name);
					msgInfo.setContent(content);
					msgInfo.setScene(scene);
					msgInfo.setRecieveTime( System.currentTimeMillis());
					LogXY.e(TAG,"msgInfo："+ msgInfo.toString());
					infoList.add(msgInfo);
				}
			}
			return infoList;
		} catch (Exception e) {
			e.printStackTrace();
			return infoList;
		}
	}
	public static String getTextContent(Node item){
		String content = "";
		try {
//			不应该直接就取第一个字节点，否则读取以下两种格式的xml文件会出现问题 第一种情况读取不到正确的content 因为第一种格式对应了三个子节点 #text #cdata-section #text
//				<string name="com.chinatelecom.bestpayclient" scene="73001">
//						<![CDATA[您的账户发起一笔充值 您的账户通过银行卡充值21.00元。]]>
//					</string>
//				<string name="com.chinatelecom.bestpayclient" scene="73001"><![CDATA[您的账户发起一笔充值 您的账户通过银行卡充值21.00元。]]></string>
//				Node textNode = item.getFirstChild();
//				if(textNode != null){
//					content = textNode.getNodeValue();
//				}
			NodeList childNodes = item.getChildNodes();
			int k = childNodes==null?0:childNodes.getLength();
			for(int index=0;index<k;index++){
				Node node = childNodes.item(index);
				if(node==null){
					continue;
				}
				String nodeName = node.getNodeName();
				String nodeValue = node.getNodeValue();
				short nodetype = node.getNodeType();
//				if(!TextUtils.isEmpty(nodeName)&& nodeName.equals(NODE_NAME)){
				if(nodetype == Node.CDATA_SECTION_NODE){
					content = nodeValue;
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return content;
	}

	//向指定的文件中写入指定的数据
      public static void writeFileData(String dir,String fileName, String content){
		try {
      		File file = new File(dir,fileName);
            FileOutputStream fos = new FileOutputStream(file,true);//获得FileOutputStream
            //将要写入的字符串转换为byte数组
             byte[]  bytes = content.getBytes();
             fos.write(bytes);//将byte数组写入文件
		     fos.flush();
		     fos.close();//关闭文件输出流
         } catch (Exception e) {
            e.printStackTrace();
         }
     }

	public static void saveMsgList(List<MsgInfo> msglist, String xmlFilePath){
		try {
			if(msglist==null || msglist.size()==0){
				return;
			}
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			//创建DocumentBuilder对象
			DocumentBuilder documentBuilder = factory.newDocumentBuilder();
			//生成一个Dom树
			Document document =	documentBuilder.newDocument();
			//去掉standalone="no"声明,说明只是一个简单的xml,没有特殊DTD(document type definition文档类型定义)规范
			document.setXmlStandalone(true);
			Element rootElement = document.createElement("resources");
			for(MsgInfo msgInfo :msglist){
				String name = msgInfo.getPhone();
				String content = msgInfo.getContent();
				Element stringElement = document.createElement("string");
				stringElement.setAttribute("name",name);
				CDATASection cdataSection = document.createCDATASection(content);
				stringElement.appendChild(cdataSection);
				rootElement.appendChild(stringElement);
			}
			document.appendChild(rootElement);
			try {
//				FileOutputStream fos = new FileOutputStream(xmlFilePath);
//				OutputStreamWriter osw = new OutputStreamWriter(fos,"UTF-8"); // 注意。。。
				TransformerFactory transFactory = TransformerFactory.newInstance();
				//创建transformer对象
				Transformer transformer = transFactory.newTransformer();
				//设置换行
				transformer.setOutputProperty(OutputKeys.INDENT, "yes");//小写
				transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
				//构造转换,参数都是抽象类，要用的却是更具体的一些类，这些的类的命名有一些规律的。
				transformer.transform(new DOMSource(document), new StreamResult(xmlFilePath));
			} catch (TransformerConfigurationException e) {
				e.printStackTrace();
			} catch (TransformerException e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return;
	}

	// 读取方式暂时定位两种 一种txt 一种 excel
	public static synchronized List<MsgInfo> readFile(File file, int start, int end) {
		if (file.toString().endsWith(".csv")) {
			return FileUtils.readMmsCSVFile(file, start, end);
		} else if (file.toString().endsWith(".txt")) {
			return FileUtils.readMmsTextFile(file, start, end);
		}
		return new ArrayList<MsgInfo>();
	}
	// 通过工具选择excel文件
	public static void selectSmsFile(Activity activity,int reqCode) {
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.setType("text/plain");
		intent.addCategory(Intent.CATEGORY_OPENABLE);
		try {
			activity.startActivityForResult(Intent.createChooser(intent, "Select a File to Upload"), reqCode);
		} catch (android.content.ActivityNotFoundException ex) {
			Toast.makeText(activity, "Please install a File Manager.", Toast.LENGTH_SHORT).show();
			return;
		}
	}
	// 获取file路径
	public static String getPath(Context context, Uri uri) {
		if ("content".equalsIgnoreCase(uri.getScheme())) {
			String[] projection = { "_data" };
			Cursor cursor = null;
			try {
				cursor = context.getContentResolver().query(uri, projection, null, null, null);
				int column_index = cursor.getColumnIndexOrThrow("_data");
				if (cursor.moveToFirst()) {
					return cursor.getString(column_index);
				}
			} catch (Exception e) {
				LogManager.e(TAG,e.getMessage());
			}
		} else if ("file".equalsIgnoreCase(uri.getScheme())) {
			return uri.getPath();
		}
		return null;
	}

	public static String getFileName(String pathName){
		String name;
		int lastindex = pathName.lastIndexOf(".");
		int startindex = pathName.lastIndexOf("/");
		if (lastindex > 0) {
			name = pathName.substring(startindex, lastindex);
		} else {
			name = pathName;
			name = name.replace(".", "_");
		}
		return name;
	}
	public static void writeExcelFileData(Workbook wb, String dir, String fileName){
		try {
			File file = new File(dir,fileName);
			writeExcelFileData(wb,file);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public static void writeExcelFileData(Workbook wb, File file){
		try {
			if(wb==null || file==null){
				return;
			}
			if(!file.exists()){
				file.createNewFile();
			}
			//创建文件流
			OutputStream stream = new FileOutputStream(file);
			//写入数据
			wb.write(stream);
			//关闭文件流
			stream.close();
			if(wb instanceof SXSSFWorkbook){
				((SXSSFWorkbook)wb).dispose();//清除临时缓存
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
