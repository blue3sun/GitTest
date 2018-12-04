package com.xy.bizportdemo.activities.base;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.xy.bizportdemo.R;
import com.xy.bizportdemo.util.FileUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainBaseActivity extends BaseActivity {
	private final String TAG = MainBaseActivity.class.getSimpleName();
	public static final int REQ_CODE_SELECT_SMS_FILE = 1;
	public Button mBtImportFileSms,mBtStartParse, mBtSmsInbox;
    public Button mBtOpenMsgList;
    /**
     * 显示导入短信的文件路径
     */
	public TextView mTvSmsParseFile;
    /**
     * 显示解析结果保存的文件夹路径
     */
    public TextView mTvParseResultDirFilePath;
    /**
     * 显示解析结果保存的文件路径
     */
    public TextView mTvParseResultFilePath;
    /**
     * 显示解析的结果
     */
    public TextView mTvParseResult;
    /**
     * 显示成功的数量
     */
    public TextView mTvParseCount;
	public String mParseFilePath;
	public boolean isRunning;
	public String mParentDirPath;

	protected void resetUi(){
		try{
			clearTextViewStr(mTvSmsParseFile, mTvParseResultDirFilePath,mTvParseResultFilePath, mTvParseResult,mTvParseCount);
		}catch (Exception e){
			e.printStackTrace();
		}
	}

	public void selectSmsFile() {
		FileUtils.selectSmsFile(MainBaseActivity.this,REQ_CODE_SELECT_SMS_FILE);
	}


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode!=REQ_CODE_SELECT_SMS_FILE){
			return;
		}
		if (resultCode == RESULT_OK) {
			resetUi();
			Uri uri = data.getData();
			String path = FileUtils.getPath(mContext, uri);
			if (path == null) {
				path = uri.getPath();
			}
			initParentDirPath();
			mParseFilePath = path;
			mTvParseResultDirFilePath.setText("报告生成的路径:" + mParentDirPath);
			isInboxModel = false;
			dataFileComplete();
		}else{
			mTvParseResultDirFilePath.setText("");
			dataFileComplete();
		}
	}

	public void dataFileComplete() {
		if (!TextUtils.isEmpty(mParseFilePath) && (mParseFilePath.endsWith(".csv") || mParseFilePath.endsWith(".txt") || mParseFilePath.endsWith(".xml"))) {
			setViewEnable(mBtStartParse,true);
            setViewEnable(mBtOpenMsgList,true);
			mTvSmsParseFile.setText("导入文件的路径:" + mParseFilePath + "\n");
			Toast.makeText(mContext, "导入完成", Toast.LENGTH_LONG).show();
		} else {
			setViewEnable(mBtStartParse,false);
            setViewEnable(mBtOpenMsgList,false);
			mTvSmsParseFile.append("导入文件的路径:导入无效 。" + "\n");
			Toast.makeText(mContext, "导入无效，请导入正确的文件路径或者格式(.csv .txt .xml)。", Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * 获取根路径
	 *
	 * @return 根路径
	 */
	public void initParentDirPath() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH_mm_ss");
		Date date = new Date(System.currentTimeMillis());
		String dateStr = sdf.format(date);
		StringBuilder path = new StringBuilder();
		if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
			path.append(Environment.getExternalStorageDirectory().getPath());
		}else{
			path.append(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS).getPath());
		}
		path.append(File.separator);
		path.append(getString(R.string.app_name));
		path.append(File.separator);
		path.append(dateStr);
		path.append(File.separator);
		mParentDirPath = path.toString();

        // 删除当前目录下的文件
        FileUtils.delAllFile(mParentDirPath.toString());

	}

	public String initRootPath() {
		if(TextUtils.isEmpty(mParentDirPath)){
			initParentDirPath();
		}
		// 删除当前目录下的文件
		FileUtils.delAllFile(mParentDirPath.toString());
		return mParentDirPath;
	}


	public void loadMsg() {
		AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
			@Override
			protected void onPreExecute() {
				showDialog(getString(R.string.dialog_title),getString(R.string.loading_msg));
			}

			@Override
			protected Void doInBackground(Void... objs) {
				readSMSByUri();
				return null;
			}

			@Override
			protected void onPostExecute(Void result) {
				loadFinish();
			}
		};
		task.execute();
	}


	private void loadFinish() {
		resetUi();
		dismissDialog();
		isRunning = false;
		initParentDirPath();
		mTvParseResultDirFilePath.setText("报告生成的路径:" + mParentDirPath);
		mTvSmsParseFile.setText("导入短信成功\n\n");
		setViewEnable(mBtStartParse,true);
		setViewEnable(mBtOpenMsgList,true);
		isInboxModel = true;
	}
}
