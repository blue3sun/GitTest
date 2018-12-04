package com.xy.bizportdemo.activities;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.xy.bizportdemo.MySdkDoAction;
import com.xy.bizportdemo.R;
import com.xy.bizportdemo.activities.base.MainBaseActivity;
import com.xy.bizportdemo.model.AnalysisSmsRecord;
import com.xy.bizportdemo.model.MsgInfo;
import com.xy.bizportdemo.util.FileUtils;
import com.xy.bizportdemo.util.LogXY;
import com.xy.bizportdemo.util.VivoDemoUtil;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.json.JSONObject;

import cn.com.xy.sms.sdk.log.LogManager;
import cn.com.xy.sms.sdk.util.StringUtils;
import cn.com.xy.sms.util.ParseCardManager;
import cn.com.xy.sms.util.ParseManager;

public class MianActivity extends MainBaseActivity implements OnClickListener {
	private final int WHAR_INIT_FINISHED = 1;
	private final int WHAT_INIT_NOT_FINISHED =2;
	private final int WHAT_START_PARSE = 3;
	private final int WHAT_PARSING = 4;
	private final int WHAT_END_PARSE = 5;
	private final int WHAT_UPDATE_VIEW = 6;
	private final int WHAT_PARSE_COUNT_VIEW = 7;
	private final int WHAT_PARSE_FINISHED_VIEW = 8;
    private final String SYMBOL_COMMA = ",";
    private final String SYMBOL_TAB = "\t";
    private final String SYMBOL_CHANGE_LINE = "\r\n";
	private final String TAG = MianActivity.class.getSimpleName();
    public static ExecutorService pool = Executors.newFixedThreadPool(1);
	private List<MsgInfo> mMsgList;
	private int mCurrent = 0;
	private int mSmsTotalCount;// 总数量
	private int mSmsParseCount;// 短信识别数量
	private int mRecogniseParseCount;// 特征值识别数量
	private long mSmsParseTotalTime;// 识别时间
	private long mRecogniseParseTotalTime;// 识别时间
	private long mSmsPreParseCount;// 短信的平均识别时间
	private int mSmsExceptionCount;// 短信异常数量
	private List<MsgInfo> smsErroList = new ArrayList<MsgInfo>();
	private boolean isStop;
	private String finalReport;
    private Runnable mRunnable;
	private Button mBtClearData;
	private SXSSFWorkbook mWorkBook;//如果已经调用了write()写入文件之后再次写入，该对应必须要重新构造新的对象，否则会出现异常
	private Switch mVirturePhoneSwitch;
	private EditText mEtInputVirturePhone;
	private Switch mNeedScriptSwitch;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_auto_sms);
		initUI();
		initData();
		initEnvironment();
		initFinish();
	}

	private void initData() {
		mNeedScriptSwitch.setChecked(true);
		VivoDemoUtil.NEED_SCRIPT = true;
		mVirturePhoneSwitch.setChecked(false);
		VivoDemoUtil.IS_USE_VIRTURE_PHONE = false;
		mNeedScriptSwitch.setOnCheckedChangeListener(Pnew CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked){
					VivoDemoUtil.NEED_SCRIPT = true;
				}else{
					VivoDemoUtil.NEED_SCRIPT = false;
				}
			}
		});
		mVirturePhoneSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked){
					VivoDemoUtil.IS_USE_VIRTURE_PHONE = true;
					mEtInputVirturePhone.setVisibility(View.VISIBLE);
				}else{
					VivoDemoUtil.IS_USE_VIRTURE_PHONE = false;
					mEtInputVirturePhone.setVisibility(View.INVISIBLE);
				}
			}
		});
	}

	private void initFinish() {
		if(MySdkDoAction.INIT_FINISHED){
			dismissDialog();
		}else{
			showDialog(getString(R.string.dialog_title),getString(R.string.dialog_init_sdk));
			handler.sendEmptyMessageDelayed(WHAT_INIT_NOT_FINISHED,1000);
		}
	}

	public void initUI() {
		mNeedScriptSwitch = (Switch) findViewById(R.id.switch_script);
		mVirturePhoneSwitch = (Switch) findViewById(R.id.switch_virture_pone);
		mEtInputVirturePhone = (EditText) findViewById(R.id.et_input_virture_phone);
		mBtClearData = (Button) findViewById(R.id.bt_clear_data);
		mBtClearData.setOnClickListener(this);
		mBtSmsInbox = (Button) findViewById(R.id.bt_import_inbox);
		mBtImportFileSms = (Button) findViewById(R.id.bt_import_file_sms);
		mBtStartParse = (Button) findViewById(R.id.bt_start_parse);
		mBtOpenMsgList = (Button) findViewById(R.id.bt_open_msg_list);
		mTvSmsParseFile = (TextView) findViewById(R.id.tv_parse_file_path);
		mTvParseResultDirFilePath = (TextView) findViewById(R.id.tv_parse_result_dir_path);
		mTvParseResultFilePath = (TextView) findViewById(R.id.tv_parse_result_file_path);
		mTvParseResult = (TextView) findViewById(R.id.tv_parse_result);
        mTvParseCount = (TextView) findViewById(R.id.tv_parse_count);
		mBtSmsInbox.setOnClickListener(this);
		mBtImportFileSms.setOnClickListener(this);
		mBtStartParse.setOnClickListener(this);
		mBtOpenMsgList.setOnClickListener(this);
		setViewEnable(mBtStartParse,false);
		setViewEnable(mBtOpenMsgList,false);
	}

	private void resetData() {
		mCurrent = 0;
		mSmsTotalCount = 0;// 总数量
		mSmsParseCount = 0;// 短信识别数量
		mRecogniseParseCount = 0;//特征值识别数量
		mSmsParseTotalTime = 0;// 识别时间
		mRecogniseParseTotalTime = 0;// 识别时间
		mSmsPreParseCount = 0;// 短信的平均识别时间
		mSmsExceptionCount=0;
		mWorkBook = null;
		if(smsErroList!=null){
			smsErroList.clear();
		}
		if (mMsgList != null) {
			mMsgList.clear();
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bt_start_parse:
			if (!isRunning) {//点击按钮之前没有开始解析
				if(!checkPhone()){
					return;
				}
				isRunning = true;
				isStop = false;
				setViewEnable(mBtImportFileSms,false);
				setViewEnable(mBtSmsInbox,false);
				setViewEnable(mBtOpenMsgList,false);
				mBtStartParse.setText(getString(R.string.stop_patch_parse));
				// 删除当前目录下的文件
				FileUtils.delAllFile(mParentDirPath.toString());
				initFile();
				startParseSms();
			} else {//点击按钮之前正在解析
				FileUtils.writeExcelFileData(mWorkBook,mParentDirPath,VivoDemoUtil.VIVO_DETAIL_PARSE_RESULT_FILE_EXCEL);
				isRunning = false;
				isStop = true;
				setViewEnable(mBtImportFileSms,true);
				setViewEnable(mBtSmsInbox,true);
				setViewEnable(mBtOpenMsgList,true);
				mBtStartParse.setText(getString(R.string.start_patch_parse));
			}
			break;
			case R.id.bt_import_file_sms:
                setViewEnable(mBtStartParse,false);
                setViewEnable(mBtOpenMsgList,false);
				mParseFilePath = null;
				selectSmsFile();
				break;
			case R.id.bt_import_inbox:
                setViewEnable(mBtStartParse,false);
                setViewEnable(mBtOpenMsgList,false);
				loadMsg();
				break;
			case R.id.bt_open_msg_list:
				if(!checkPhone()){
					return;
				}
				Intent intent = new Intent(MianActivity.this,MessageListActivity.class);
				intent.putExtra(MessageListActivity.EXTRA_IS_INBOX,isInboxModel);
				intent.putExtra(MessageListActivity.EXTRA_FILE_PATH,mParseFilePath);
				startActivity(intent);
				break;
//			case R.id.bt_clear_data://测试内存用的
//				ParseManager.release();
//				resetData();
//				if(mMessageInboxList!=null){
//					mMessageInboxList.clear();
//				}
//				System.gc();
//				Log.e(TAG,"ParseManager.release()执行完成");
//				break;
		}
	}


	private void initFile(){
		if (isInboxModel) {
			VivoDemoUtil.setParseFileName(mParentDirPath,"inbox_");
		} else {
			VivoDemoUtil.setParseFileName(mParentDirPath,FileUtils.getFileName(mParseFilePath)+ "_");
		}

//		writeFileData(VivoDemoUtil.VIVO_DETAIL_PARSE_RESULT_FILE,"号码,短信内容,短信识别结果,特征值识别结果,短信识别时长,特征值识别时长"+SYMBOL_CHANGE_LINE);
//		writeFileData(VivoDemoUtil.VIVO_SUMMARY_PARSE_RESULT_FILE,"定义,单位"+SYMBOL_CHANGE_LINE);

//		mTvParseResultFilePath.setText("具体的解析分析结果见：\n" + VivoDemoUtil.VIVO_SUMMARY_PARSE_RESULT_FILE + "\n"
//				+ VivoDemoUtil.VIVO_DETAIL_PARSE_RESULT_FILE + "\n");

		mTvParseResultFilePath.setText("具体的解析分析结果见：\n" + VivoDemoUtil.VIVO_DETAIL_PARSE_RESULT_FILE_EXCEL + "\n");
	}

	private void writeFileData(String fileName, String content){
		FileUtils.writeFileData(mParentDirPath,fileName,content);
	}

	private boolean checkPhone(){
		if(!VivoDemoUtil.IS_USE_VIRTURE_PHONE){
			return true;
		}
		return checkVirturePhone();
	}

	private boolean checkVirturePhone(){
		String inputPhoneStr = mEtInputVirturePhone.getText().toString();
		if(inputPhoneStr!=null){
			inputPhoneStr = inputPhoneStr.trim();
		}
		if(TextUtils.isEmpty(inputPhoneStr)){
			showToast(getString(R.string.empty_virture_phone));
			return false;
		}else{
			VivoDemoUtil.VIRTURE_PHONE = inputPhoneStr;
			return true;
		}
	}

	private void startParseSms() {
		// 开始解析
        if(mRunnable==null){
            mRunnable = new Runnable(){
                @Override
                public void run() {
					resetData();
                    if (isInboxModel) {//如果导入系统短信的模式
                        parseInboxSms();
                    } else {//如果导入文件的模式
						parseFileSms(mParseFilePath);
                    }
                }
            };
        }
        pool.execute(mRunnable);
	}

	private void parseInboxSms() {
		if (mMsgList == null) {
			mMsgList = new ArrayList<MsgInfo>();
		} else {
			mMsgList.clear();
		}
		if(mMessageInboxList!=null){
			mMsgList.addAll(mMessageInboxList);
		}
		mSmsTotalCount = mMsgList.size();
		parseAllMessage();
	}

	private void parseFileSms(String filePath) {
		if (!TextUtils.isEmpty(filePath)) {
			File file = new File(filePath);
			if (file.exists()) {
				if (filePath.endsWith(".csv") || filePath.endsWith(".txt")) {
//					mSmsTotalCount = FileUtils.getFileLineCount(file);
					mMsgList = FileUtils.readFile(file, -1,-1);
					mSmsTotalCount = mMsgList==null?0:mMsgList.size();
					parseAllMessage();
					if (isStop) {
						return;
					}
				}else if(filePath.endsWith(".xml")){
                    mMsgList = FileUtils.loadMessageInfoXML(filePath);
					mSmsTotalCount = mMsgList.size();
					parseAllMessage();
					if (isStop) {
						return;
					}
                }
			}
		}
	}

	/**
	 * 解析短信数据 不用递归用for循环，递归的话解析大量短信，会出现stackoverflowexception
	 */
	private void parseAllMessage() {
		try{
			for(int i=0;i<mSmsTotalCount;i++){
				if (isStop) {
					break;
				}
				mCurrent = i;
				final MsgInfo msgInfo = mMsgList.get(mCurrent);
				mCurrent++;
				updateView(mCurrent,WHAT_UPDATE_VIEW);
				try{
					parseMessage(msgInfo);
				}catch (Exception e){
					e.printStackTrace();
				}
			}
			if(mCurrent>=mSmsTotalCount){
				updateView(mCurrent,WHAT_PARSE_FINISHED_VIEW);
			}
		}catch ( Exception e){
			e.printStackTrace();
			LogXY.e(TAG,e.getMessage());
		}
	}


	/**
	 * 先解析短信再解析特征值
	 * @param msgInfo
	 */
	private void parseMessage(final MsgInfo msgInfo) {
		if(msgInfo==null){
			return;
		}
		String parsePhone = VivoDemoUtil.getPhone(msgInfo);
		final AnalysisSmsRecord record = new AnalysisSmsRecord();
		record.smsParseValue = new JSONObject();
		record.setPhoneNumber(msgInfo.getPhone());
		record.setContent(msgInfo.getContent());
		record.setMsgId(msgInfo.getMsgId());
		record.setReceiveTime(msgInfo.getRecieveTime());
		long start = System.currentTimeMillis();
		try{
			Map<String, Object> resultMap = ParseCardManager.parseMsgForCard(MianActivity.this, parsePhone, msgInfo.getSmsCenterNum(), msgInfo.getContent(),getSmsExtendMap(msgInfo));
			long end = System.currentTimeMillis();
            resultMap = VivoDemoUtil.sortMap(resultMap);
            record.setSmsParseTime(end-start);
			record.smsParseValue =  VivoDemoUtil.changeMapToJson(resultMap);//短信解析结果

			if(record.smsParseValue!=null && record.smsParseValue.optBoolean("Result")){
				String titleNumber = (String)record.smsParseValue.optString("title_num");
				if(!StringUtils.isNull(titleNumber)){
					record.setTitleNumber(titleNumber);
				}
			}

			parseRecognize(record,msgInfo);//解析特征值
		} catch (Exception e) {
			e.printStackTrace();
			parseRecognize(record,msgInfo);
		}
	}

	/**
	 * 解析特征值
	 * @param record
	 * @param msgInfo
	 */
	private void parseRecognize(final AnalysisSmsRecord record,final MsgInfo msgInfo) {
		try{
			String parsePhone = VivoDemoUtil.getPhone(msgInfo);
            long start = System.currentTimeMillis();
			JSONObject recogniseData = ParseManager.parseRecogniseValue(parsePhone, msgInfo.getContent(), msgInfo.getRecieveTime(), getSmsExtendMap(msgInfo));
			record.recognizeParseValue = recogniseData;//特征值数据

			long end = System.currentTimeMillis();
            record.setRecogniseParseTime(end-start);

            mSmsParseTotalTime = mSmsParseTotalTime + record.getSmsParseTime();//总体分析报告里面只计算短信的解析时间

			dealWithParseResult(record,msgInfo);
		}catch (Exception e){
			dealWithParseResult(record,msgInfo);
		}
	}
	private void dealWithParseResult(AnalysisSmsRecord record,MsgInfo msgInfo){
		//若有解析失败则将解析失败的样例单独保持至一个文件，方面分析问题
		saveFailInfoToFile(record,msgInfo);

		//获取短信和特征值解析的数量，实时显示
		if(!StringUtils.isNull(record.getTitleNumber())){//这里直接将有title_num输出的认为是解析成功，反之失败
			mSmsParseCount++;
		}
		if(record.recognizeParseValue!=null){
			mRecogniseParseCount++;
		}
		updateView(mSmsParseCount,mRecogniseParseCount, WHAT_PARSE_COUNT_VIEW);

		//将解析结果保存至文件
		writeParseResult(record);

		record = null;

	}


	/**
	 * 将短信解析失败或者特征值解析失败的短信单独写入一个文件，方便测试查看
	 * @param record
	 * @param msgInfo
	 */
	public void saveFailInfoToFile(AnalysisSmsRecord record,MsgInfo msgInfo){
		//将解析失败的短信单独保存至一个文件，方便查看
		if(StringUtils.isNull(record.getTitleNumber())){//这里直接将没有title_num输出的认为是解析失败
//			writeFailureResult(VivoDemoUtil.VIVO_EEROR_SMS_RESULT_FILE,msgInfo);
			writeFailResultToExcel(msgInfo,false);
		}

		//将特征值解析失败的短信单独保存至一个文件，方便查看
		if(record.recognizeParseValue == null ){
//			writeFailureResult(VivoDemoUtil.VIVO_EEROR_RECOGNISE_RESULT_FILE,msgInfo);
			writeFailResultToExcel(msgInfo,true);
		}
	}

	private void writeParseResult(AnalysisSmsRecord analysisSmsRecord){
//		writeFileData(VivoDemoUtil.VIVO_DETAIL_PARSE_RESULT_FILE,analysisSmsRecord.toString() + SYMBOL_CHANGE_LINE);
		writeParseResultToExcel(analysisSmsRecord);
		// 当所有的解析结束之后
		if (mCurrent >= mSmsTotalCount) {
			// 输出总体的报告
			if (mSmsParseCount == 0) {
				mSmsPreParseCount = 0;
			} else {
				mSmsPreParseCount = mSmsParseTotalTime / mSmsParseCount;
			}
//			// 输出总体的分析报告
//			writeFileData(VivoDemoUtil.VIVO_SUMMARY_PARSE_RESULT_FILE,
//					"总样例数量," + mSmsTotalCount + "条"+SYMBOL_CHANGE_LINE
//			+"短信识别数量," + mSmsParseCount + "条"+SYMBOL_CHANGE_LINE
//			+"短信识别率," + VivoDemoUtil.getDouble(mSmsParseCount, mSmsTotalCount) + "%"+SYMBOL_CHANGE_LINE
//			+"短信平均识别时长," + mSmsPreParseCount + "ms"+SYMBOL_CHANGE_LINE
//			+"短信识别的总时长," + mSmsParseTotalTime + "ms"+SYMBOL_CHANGE_LINE);

			writeTotalResultToExcel();
			FileUtils.writeExcelFileData(mWorkBook,mParentDirPath,VivoDemoUtil.VIVO_DETAIL_PARSE_RESULT_FILE_EXCEL);

			/*//输出错误的分析报告
			if(mModel.NEED_ERROR_REPORT){
				if(smsErroList!=null && smsErroList.size()>0&&mSmsExceptionCount>0){
					LogManager.e(TAG,"解析失败的样例共："+mSmsExceptionCount+"条");
					StringBuilder sb= null;
					for(MsgInfo errorMsgsginfo :smsErroList){
						sb = new StringBuilder();
						String phoneNumber = errorMsgsginfo.getPhone();
						String content  = errorMsgsginfo.getContent();
						sb.append(phoneNumber).append("\t").append(content).append(SYMBOL_CHANGE_LINE);
						LogManager.e(TAG,sb.toString());
						writeFileData(mModel.VIVO_EEROR_SMS_RESULT_FILE,sb.toString());
					}
				}
			}*/
		}
	}
	private void writeFailureResult(String fileName,MsgInfo errorMsgInfo){
		if(errorMsgInfo==null){
			return;
		}
		mSmsExceptionCount++;
		smsErroList.add(errorMsgInfo);
		//输出错误的分析报告
		if(VivoDemoUtil.NEED_ERROR_REPORT){
			StringBuilder sb = new StringBuilder();
			String phoneNumber = errorMsgInfo.getPhone();
			String content  = errorMsgInfo.getContent();
			sb.append(phoneNumber).append(SYMBOL_TAB).append(SYMBOL_COMMA).append(content).append(SYMBOL_CHANGE_LINE);
			LogManager.e(TAG,sb.toString());
			writeFileData(fileName,sb.toString());
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		isStop = true;
		resetData();
		if(mMsgList!=null){
			mMsgList.clear();
			mMsgList=null;
		}
	}


	
	private Handler handler = new Handler(){
		@Override
		public void handleMessage(android.os.Message msg) {
			int what = msg.what;
			switch (what){
				case WHAT_INIT_NOT_FINISHED:
					initFinish();
					break;
				case WHAT_UPDATE_VIEW:
					updateViewInfo(msg.arg1);
					break;
				case WHAT_PARSE_COUNT_VIEW:
					updateParseCountInfo(msg.arg1,msg.arg2);
					break;
				case WHAT_PARSE_FINISHED_VIEW:
					parseFinished();
					break;
			}
		};
	};

	private void updateParseCountInfo(int smsParseCount,int recogniseParseCount) {
		StringBuilder sb = new StringBuilder();
		sb.append("短信识别数:");
		sb.append(smsParseCount);
		sb.append("条");
		sb.append("\t\t");
		sb.append("特征值识别数:");
		sb.append(recogniseParseCount);
		sb.append("条");
		mTvParseCount.setText(sb.toString());
	}

	/**
	 * 在 UI 线程绑定数据
	 * 
	 */
	private void updateView(int parsingIndex,int what) {
		Message msg = Message.obtain(handler,what);
		msg.arg1 = parsingIndex;
		msg.sendToTarget();
	}
	/**
	 * 在 UI 线程绑定数据
	 *
	 */
	private void updateView(int smsParseCount,int recogniseParseCount,int what) {
		Message msg = Message.obtain(handler,what);
		msg.arg1 = smsParseCount;
		msg.arg2 = recogniseParseCount;
		msg.sendToTarget();
	}

	/**
	 * 更新视图信息
	 */
	protected void updateViewInfo(int parsingIndex) {
		finalReport = "";
		StringBuilder sb = new StringBuilder();
		sb.append("总共有：");
		sb.append(mSmsTotalCount);
		sb.append("条\t");
		sb.append("正在解析：第");
		sb.append(parsingIndex);
		sb.append("条\t");
		mTvParseResult.setText(sb.toString());

		Log.e(TAG, "sb:" + sb.toString());
//		if (parsingIndex == mSmsTotalCount && mSmsTotalCount !=0) {
//			setViewEnable(mBtSmsInbox,true);
//			setViewEnable(mBtImportFileSms,true);
//			setViewEnable(mBtOpenMsgList,true);
//			isRunning = false;
//			mBtStartParse.setText(getString(R.string.start_patch_parse));
//		}
	}
	/**
	 * 解析完成
	 */
	protected void parseFinished() {
		finalReport = "";
		StringBuilder sb = new StringBuilder();
		sb.append("总共有：");
		sb.append(mSmsTotalCount);
		sb.append("条\t");
		sb.append("解析完成\n");
		sb.append("批量解析时长：");
		sb.append(VivoDemoUtil.getTimeTxt(mSmsParseTotalTime));
		mTvParseResult.setText(sb.toString());

		Log.e(TAG, "sb:" + sb.toString());
		setViewEnable(mBtSmsInbox,true);
		setViewEnable(mBtImportFileSms,true);
		setViewEnable(mBtOpenMsgList,true);
		isRunning = false;
		mBtStartParse.setText(getString(R.string.start_patch_parse));
	}

	private void writeParseResultToExcel(AnalysisSmsRecord record) {
		try{
//			File file = new File(mParentDirPath,VivoDemoUtil.DETAIL_RESULT_FILENAME);
//			Workbook wb = WorkbookFactory.create(file);
			Workbook wb = null;
			if(mWorkBook ==null){
				//注:XSSFWorkbook可以追加数据到已经存在的excel表格中，
				//但是SXSSFWorkbook附加数据到已经存在的Excel中的话就是不行的，SXSSFWorkbook只能用在新创建Excel中才行。
//				XSSFWorkbook xssfWorkbook = new XSSFWorkbook(file);
//				mAddtionalWorkBook = new SXSSFWorkbook(xssfWorkbook,EXCEL_MEMORY_LINES);
				//对大量数据进行写入时，这里使用SXSSFWorkbook可以提升excel文件写入的性能
				mWorkBook = new SXSSFWorkbook(VivoDemoUtil.EXCEL_MEMORY_LINES);
			}
			wb = mWorkBook;
			String sheetName = VivoDemoUtil.DETAIL_RESULT_SHEETNAME;
			Sheet sheet = wb.getSheet(sheetName);
			if(sheet==null){
				sheet = wb.createSheet(sheetName);
			}
			int lastRowNum = sheet.getLastRowNum();
			int col = 0;
			if(lastRowNum==0){
				//表头
				Row row = sheet.createRow(0);
				row.createCell(col++).setCellValue("号码");
				row.createCell(col++).setCellValue("短信内容");
				row.createCell(col++).setCellValue("短信识别成功与否");
				row.createCell(col++).setCellValue("短信识别结果");
				row.createCell(col++).setCellValue("特征值识别结果");
				row.createCell(col++).setCellValue("短信解析时间");
				row.createCell(col++).setCellValue("特征值解析时间");
				col=0;
			}
			if(record==null){
				Row newRow = sheet.createRow(++lastRowNum);
				newRow.createCell(col++).setCellValue("短信对象为空");
			}else{
				Row newRow = sheet.createRow(++lastRowNum);
				newRow.createCell(col++).setCellValue(record.getPhoneNumber());
				newRow.createCell(col++).setCellValue(record.getContent());
				if(record.smsParseValue==null){
					newRow.createCell(col++).setCellValue("false");
					newRow.createCell(col++).setCellValue("");
				}else{
					if(StringUtils.isNull(record.getTitleNumber())){//这里直接将没有title_num输出的认为是解析失败
						newRow.createCell(col++).setCellValue("false");
					}else{
						newRow.createCell(col++).setCellValue("true");
					}
					newRow.createCell(col++).setCellValue(VivoDemoUtil.getResultStrByJson(MianActivity.this,record.smsParseValue));
				}
				if(record.recognizeParseValue==null){
					newRow.createCell(col++).setCellValue("");
				}else{
					newRow.createCell(col++).setCellValue(VivoDemoUtil.getResultStrByJson(MianActivity.this,record.recognizeParseValue));
				}
				newRow.createCell(col++).setCellValue(VivoDemoUtil.getTimeTxt(record.getSmsParseTime()));
				newRow.createCell(col++).setCellValue(VivoDemoUtil.getTimeTxt(record.getRecogniseParseTime()));
			}
		}catch (Exception e){
			e.printStackTrace();
		}
	}

	/**
	 * 将失败的样例保存至文件
	 * @param msginfo
	 */
	public void writeFailResultToExcel(MsgInfo msginfo, boolean isRecognise){
		String sheetName = "";
		mSmsExceptionCount++;
//			smsErroList.add(msginfo);
//		是否需要输出错误的分析报告
		if(VivoDemoUtil.NEED_ERROR_REPORT ==false){
			return;
		}
		sheetName = VivoDemoUtil.ERROR_SMS_RESULT_SHEETNAEM;
		if(isRecognise){
			sheetName = VivoDemoUtil.ERROR_RECOGNISE_RESULT_SHEETNAEM;
		}
		try{
//			File file = new File(mParentDirPath,fileName);
//			FileInputStream fileInputStream = new FileInputStream(file);
//			Workbook wb = WorkbookFactory.create(fileInputStream);
			Workbook wb = null;
			if(mWorkBook==null){
				mWorkBook = new SXSSFWorkbook(VivoDemoUtil.EXCEL_MEMORY_LINES);
			}
			wb = mWorkBook;
			Sheet sheet = wb.getSheet(sheetName);
			if(sheet==null){
				sheet = wb.createSheet(sheetName);
			}
			int lastRowNum = sheet.getLastRowNum();
			int col = 0;
			if(lastRowNum==0){
				//表头
				Row row1 = sheet.createRow(lastRowNum);
				row1.createCell(col++).setCellValue("号码");
				row1.createCell(col++).setCellValue("内容");
				col = 0;
			}
			if(msginfo==null){
				Row newRow = sheet.createRow(++lastRowNum);
				newRow.createCell(col++).setCellValue("样例数据是空");
			}else{
				String phoneNumber = msginfo.getPhone();
				String content  = msginfo.getContent();
				Row newRow = sheet.createRow(++lastRowNum);
				newRow.createCell(col++).setCellValue(phoneNumber);
				newRow.createCell(col++).setCellValue(content);
			}
//			FileUtils.writeExcelFileData(wb,mParentDirPath,VIVO_ADDITIONNAL_FILE);
		}catch (Exception e){
//			dealError(e);
			e.printStackTrace();
		}
	}
	/**
	 * 总体分析报告写入excel表格
	 */
	private void writeTotalResultToExcel(){
		try{
//            File file = new File(mParentDirPath,VIVO_SUMMARY_RESULT_FILE);
//            FileInputStream fileInputStream = new FileInputStream(file);
//            Workbook wb = WorkbookFactory.create(fileInputStream);
			//使用SXSSFWorkbook提升性能
			if(mWorkBook ==null){
				//注:XSSFWorkbook可以追加数据到已经存在的excel表格中，
				//但是SXSSFWorkbook附加数据到已经存在的Excel中的话就是不行的，SXSSFWorkbook只能用在新创建Excel中才行。
//				XSSFWorkbook xssfWorkbook = new XSSFWorkbook(file);
//				mAddtionalWorkBook = new SXSSFWorkbook(xssfWorkbook,EXCEL_MEMORY_LINES);
				//对大量数据进行写入时，这里使用SXSSFWorkbook可以提升excel文件写入的性能
				mWorkBook = new SXSSFWorkbook(VivoDemoUtil.EXCEL_MEMORY_LINES);
			}

			Workbook wb = mWorkBook;

			Sheet sheet = wb.getSheet(VivoDemoUtil.TOTAL_RESULT_SHEETNAME);
			if(sheet==null){
				sheet = wb.createSheet(VivoDemoUtil.TOTAL_RESULT_SHEETNAME);
			}
			int lastRowNum = sheet.getLastRowNum();
			int col = 0;
			if(lastRowNum==0){
				//表头
				Row row = sheet.createRow(0);
				row.createCell(col++).setCellValue("种类");
				row.createCell(col++).setCellValue("数量");
				col = 0;
			}
			Row newRow = sheet.createRow(++lastRowNum);
			newRow.createCell(col++).setCellValue("总样例数量");
			newRow.createCell(col++).setCellValue(mSmsTotalCount+"条");
			col = 0;
			newRow = sheet.createRow(++lastRowNum);
			newRow.createCell(col++).setCellValue("短信识别数量");
			newRow.createCell(col++).setCellValue(mSmsParseCount+"条");
			col = 0;
			newRow = sheet.createRow(++lastRowNum);
			newRow.createCell(col++).setCellValue("短信识别率");
			newRow.createCell(col++).setCellValue(VivoDemoUtil.getDouble(mSmsParseCount, mSmsTotalCount) + "%");
			col = 0;
			newRow = sheet.createRow(++lastRowNum);
			newRow.createCell(col++).setCellValue("短信平均识别时长");
			newRow.createCell(col++).setCellValue(VivoDemoUtil.getTimeTxt(mSmsPreParseCount));
			col = 0;
			newRow = sheet.createRow(++lastRowNum);
			newRow.createCell(col++).setCellValue("短信识别的总时长");
			newRow.createCell(col++).setCellValue(VivoDemoUtil.getTimeTxt(mSmsParseTotalTime));
//			FileUtils.writeExcelFileData(wb,mParentDirPath,VivoDemoUtil.VIVO_DETAIL_PARSE_RESULT_FILE_EXCEL);
		}catch (Exception e){
//			dealError(e);
			e.printStackTrace();
		}
	}
}
