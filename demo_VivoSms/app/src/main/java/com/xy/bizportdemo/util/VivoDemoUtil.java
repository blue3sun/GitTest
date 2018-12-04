package com.xy.bizportdemo.util;

import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import android.content.Context;
import android.text.TextUtils;

import com.xy.bizportdemo.R;
import com.xy.bizportdemo.model.MsgInfo;

import org.apache.commons.text.StringEscapeUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class VivoDemoUtil {
	private static final String TAG = VivoDemoUtil.class.getSimpleName();
	private static SimpleDateFormat mSimpleDateFormat;
	private Context mContext;
	public static final boolean NEED_ERROR_REPORT = true;//将错误的结果保存起来，方便测试人员查看 【外发demo可设置成false】
	private static final double ONE_SECOND = 1000;//一秒 1000毫秒
	private static final String YYYYMMDDHHMMSS = "yyyy-MM-dd HH:mm:ss";
	public static final int EXCEL_MEMORY_LINES = 100;//并不是指100行保存在内存里，而是说100的屏幕尺寸下可见的行数保存在内存中。
	public static String VIVO_SUMMARY_PARSE_RESULT_FILE;
	public static String VIVO_DETAIL_PARSE_RESULT_FILE;
	public static String VIVO_EEROR_SMS_RESULT_FILE;
	public static String VIVO_EEROR_RECOGNISE_RESULT_FILE;
	public static final String SUMMARY_RESULT_FILENAEM = "解析结果分析报告.csv";
	public static final String DETAIL_RESULT_FILENAME = "解析结果详情报告.csv";
	public static final String ERROR_SMS_RESULT_FILENAEM = "短信解析失败结果报告.csv";
	public static final String ERROR_RECOGNISE_RESULT_FILENAEM = "特征值解析失败结果报告.csv";

	public static String VIVO_SUMMARY_PARSE_RESULT_FILE_EXCEL;
	public static String VIVO_DETAIL_PARSE_RESULT_FILE_EXCEL;
	public static String VIVO_EEROR_SMS_RESULT_FILE_EXCEL;
	public static String VIVO_EEROR_RECOGNISE_RESULT_FILE_EXCEL;
	private static final String SUMMARY_RESULT_FILENAEM_EXCEL = "解析结果分析报告.xlsx";
	private static final String DETAIL_RESULT_FILENAME_EXCEL = "解析详情结果报告.xlsx";
	private static final String ERROR_SMS_RESULT_FILENAEM_EXCEL = "短信解析失败结果报告.xlsx";
	private static final String ERROR_RECOGNISE_RESULT_FILENAEM_EXCEL = "特征值解析失败结果报告.xlsx";

	public static final String TOTAL_RESULT_SHEETNAME = "总体分析报告";
	public static final String DETAIL_RESULT_SHEETNAME = "解析详情报告";
	public static final String ERROR_SMS_RESULT_SHEETNAEM = "短信解析失败结果报告";
	public static final String ERROR_RECOGNISE_RESULT_SHEETNAEM = "特征值解析失败结果报告";
	/**
	 * 是否需要脚本解析 默认需要
	 */
	public static boolean NEED_SCRIPT = true;
	/**
	 * 是否使用虚拟的号码进行样例的解析
	 */
	public static boolean IS_USE_VIRTURE_PHONE = false;
	/**
	 * 用于样例解析的虚拟号码
	 */
	public static String VIRTURE_PHONE;

    public VivoDemoUtil(Context mContext) {
		this.mContext = mContext;
	}

	public static String getPhone(MsgInfo msgInfo){
    	if(IS_USE_VIRTURE_PHONE){
    		return VIRTURE_PHONE;
		}else{
    		if(msgInfo==null){
    			return "";
			}else{
				return msgInfo.getPhone();
			}
		}
	}

	public static void setParseFileNameTxt(String rootPath,String fileName){
		VIVO_SUMMARY_PARSE_RESULT_FILE = fileName + SUMMARY_RESULT_FILENAEM;
		VIVO_DETAIL_PARSE_RESULT_FILE = fileName + DETAIL_RESULT_FILENAME;
		FileUtils.createFile(rootPath,VIVO_SUMMARY_PARSE_RESULT_FILE);
		FileUtils.createFile(rootPath,VIVO_DETAIL_PARSE_RESULT_FILE);
		if(NEED_ERROR_REPORT){
			VIVO_EEROR_SMS_RESULT_FILE = fileName + ERROR_SMS_RESULT_FILENAEM;
			FileUtils.createFile(rootPath, VIVO_EEROR_SMS_RESULT_FILE);
			VIVO_EEROR_RECOGNISE_RESULT_FILE = fileName + ERROR_RECOGNISE_RESULT_FILENAEM;
			FileUtils.createFile(rootPath, VIVO_EEROR_RECOGNISE_RESULT_FILE);
		}
	}
	public static void setParseFileName(String rootPath,String fileName){
//		VIVO_SUMMARY_PARSE_RESULT_FILE_EXCEL = fileName + SUMMARY_RESULT_FILENAEM_EXCEL;
		VIVO_DETAIL_PARSE_RESULT_FILE_EXCEL = fileName + DETAIL_RESULT_FILENAME_EXCEL;
//		FileUtils.createFile(rootPath,VIVO_SUMMARY_PARSE_RESULT_FILE_EXCEL);
		FileUtils.createFile(rootPath,VIVO_DETAIL_PARSE_RESULT_FILE_EXCEL);
//		if(NEED_ERROR_REPORT){
//			VIVO_EEROR_SMS_RESULT_FILE_EXCEL = fileName + ERROR_SMS_RESULT_FILENAEM_EXCEL;
//			FileUtils.createFile(rootPath, VIVO_EEROR_SMS_RESULT_FILE_EXCEL);
//			VIVO_EEROR_RECOGNISE_RESULT_FILE_EXCEL = fileName + ERROR_RECOGNISE_RESULT_FILENAEM_EXCEL;
//			FileUtils.createFile(rootPath, VIVO_EEROR_RECOGNISE_RESULT_FILE_EXCEL);
//		}
	}


	public static String getDouble(int mCount, int mTotalCount) {
		if (mTotalCount == 0) {
			return "0.0";
		}
		double d = (double) mCount / (double) mTotalCount;
		d = (double) ((int) (d * 10000 * 100)) / (double) 10000;
		return String.valueOf(d);
	}

	public static Map<String,Object> changeJsonToMap(JSONObject jsonObject){
		Map<String,Object> map = new HashMap<>();
		try{
			if(jsonObject!=null){
				Iterator<String> keys = jsonObject.keys();
				while(keys.hasNext()){
					String key = keys.next();
					Object value = jsonObject.get(key);
					map.put(key,value);
				}
			}
		}catch (Exception e){
			e.printStackTrace();
		}
		return map;
	}
	public static JSONObject changeMapToJson(Map<String,Object> map){
		JSONObject jsonObject = new JSONObject();
		try{
			jsonObject = new JSONObject(map);
//			if(map!=null){//不要这样写，否则put的null值打印不出来
//				Set<String> keys = map.keySet();
//				Iterator<String> keyIterator = keys.iterator();
//				while(keyIterator.hasNext()){
//					String key = keyIterator.next();
//					Object value = map.get(key);
//					jsonObject.put(key,value);
//				}
//			}
		}catch (Exception e){
			e.printStackTrace();
		}
		return jsonObject;
	}

    public static String getResultStrByJson(Context context,JSONObject jsonObject){
        try{
            if(jsonObject==null){
                return context.getString(R.string.parse_empty_tip);
            }else{
                //json会自动转义斜杠 例如："repayment_date": "2016/07/26"------->"repayment_date":"2016\/07\/26"
                //因此这里需要处理下
                String strUnescapeJson = StringEscapeUtils.unescapeJson(jsonObject.toString(0));
                return strUnescapeJson;
            }
        }catch (Exception e){
            e.printStackTrace();
            return e.getMessage();
        }
    }

    public static String getResultStrByMap(Context context, Map<String, Object> result){
        try{
            if(result==null){
                return context.getString(R.string.parse_empty_tip);
            }else{
                JSONObject jsonObject = new JSONObject(result);
                return getResultStrByJson(context,jsonObject);
            }
        }catch (Exception e){
            e.printStackTrace();
            return e.getMessage();
        }
    }

    public static String getTimeTxt(double milliSecondTime){
        double second = milliSecondTime/ ONE_SECOND;
        if(second<60){
            return second+"秒";
        }
        double minute = second/60;
        if(minute<60){
            return minute+"分";
        }
        double hour = minute/60;
        return hour+"小时";
    }

	/**
	 * 将解析结果中d_t涉及到的时间戳进行格式化，方便测试人员测试。正式不需要调用该方法
	 * @param map 解析的map结果
	 * @param jsonObject 因为该方法是递归的，jsonObjct是在处理飞机火车时才需要传值
	 * @return
	 */
    public static Map<String,Object> formatMapTime(Map<String,Object> map,JSONObject jsonObject){
    	try{
			int size = 0;
			if(map != null){
				size = map.size();
			}
			if(size == 0){
				return map;
			}
			Set<Map.Entry<String, Object>> entrySet = map.entrySet();
			Iterator<Map.Entry<String, Object>> iterator = entrySet.iterator();
			HashMap<String,Object> addTimeFormat = new HashMap<String,Object>();
			while(iterator.hasNext()){
				Map.Entry<String, Object> entry = iterator.next();
				String key = entry.getKey();
				Object value = entry.getValue();
				try{
					if(TextUtils.isEmpty(key)){
						continue;
					}
					if(key.equals("flight_data_arr")||key.equals("train_data_arr")){
						JSONArray jsonArray = (JSONArray)value;
						int length = jsonArray.length();
						for(int i=0;i<length;i++){
							JSONObject itemJsonObject = jsonArray.optJSONObject(i);
							Map<String, Object> mapItem = changeJsonToMap(itemJsonObject);
							formatMapTime(mapItem,itemJsonObject);
						}
						continue;
					}
					if(key.startsWith("d_t_")){
						String formatTimeStr = "";
						if(value instanceof Long || value instanceof  String) {
							try{
								long time = Long.parseLong(value.toString());
								formatTimeStr = formatTime(time, YYYYMMDDHHMMSS);
							}catch (Exception e){
//								e.printStackTrace();//d_t_开头的也未必全部都是时间戳
								formatTimeStr = "";
							}
						}
						if(!TextUtils.isEmpty(formatTimeStr)){
							StringBuffer newValue = new StringBuffer(String.valueOf(value));
							newValue.append("(").append(formatTimeStr).append(")");
							addTimeFormat.put(key+"_demo", newValue.toString());//在原来key的后面增加"_demo"显示格式转换后的值，同时不修改原来的key值
						}
					}
				}catch (Exception e){
					e.printStackTrace();
				}
			}
			if(addTimeFormat!=null && addTimeFormat.size()>0){
				if(jsonObject!=null){
					Set<Map.Entry<String, Object>> addTimeFormatEntrySet = addTimeFormat.entrySet();
					Iterator<Map.Entry<String, Object>> addTimeFormatIterator = addTimeFormatEntrySet.iterator();
					while(addTimeFormatIterator.hasNext()){
						Map.Entry<String, Object> addTimeFormatEntry = addTimeFormatIterator.next();
						try {
							jsonObject.put(addTimeFormatEntry.getKey(),addTimeFormatEntry.getValue());
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				}else{
					map.putAll(addTimeFormat);
				}
			}
		}catch (Exception e){
    		e.printStackTrace();
		}
		return map;
	}


	public static String formatTime(long time,String format){
		String formatTimeStr = "";
    	try{
			if(mSimpleDateFormat==null){
				mSimpleDateFormat = new SimpleDateFormat(format);
			}
			Date date = new Date(time);
			formatTimeStr = mSimpleDateFormat.format(date);
		}catch (Exception e){
    		e.printStackTrace();
		}
		return formatTimeStr;
	}

	public static Map<String,Object> sortMap(Map<String,Object> map){
    	if(map==null || map.size()<=1){
    		return map;
		}
		TreeMap<String,Object> treeMap = new TreeMap<>(new Comparator<String>() {
			@Override
			public int compare(String obj1, String obj2) {
				if(obj1!=null&&obj2!=null){
					return obj1.compareTo(obj2);
				}
				return 1;
			}
		});
		treeMap.putAll(map);
		return treeMap;
	}



}

