package com.xy.bizportdemo.activities.base;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.xy.bizportdemo.R;
import com.xy.bizportdemo.model.MsgInfo;
import com.xy.bizportdemo.util.VivoDemoUtil;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class BaseActivity extends Activity {
	private final String TAG = BaseActivity.class.getSimpleName();
	public List<MsgInfo> mMessageInboxList;
	public Map<String, String> mSmsExtendMap;
	public BaseActivity mContext;
	public ProgressDialog myDialog;
	public boolean isInboxModel;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
	}

	protected void clearTextViewStr(TextView... textViews){
		if(textViews==null && textViews.length==0){
			return;
		}
		for(TextView tv : textViews){
			if(tv!=null){
				tv.setText("");
			}
		}
	}


	public void setViewEnable(Button btn,boolean enable) {
		if(btn==null){
			return;
		}
		btn.setEnabled(enable);
		if (enable) {
			btn.setTextColor(getResources().getColor(R.color.black));
		} else {
			btn.setTextColor(getResources().getColor(R.color.bar_gray));
		}
	}



	public void readSMSByUri() {
	    if(mMessageInboxList==null){
			mMessageInboxList = new ArrayList<>();
        }
		mMessageInboxList.clear();
		Cursor cursor = null;
		try {
			String[] projection = new String[] { "_id", "body", "address", "type" };
			cursor = mContext.getContentResolver().query(Uri.parse("content://sms"), projection, "type = ?",
					new String[] { "1" }, null);
			if (cursor != null && cursor.getCount() > 0) {
				while (cursor.moveToNext()) {
					MsgInfo info = new MsgInfo();
					long msgId = cursor.getLong(cursor.getColumnIndex("_id"));
					String smsBody = cursor.getString(cursor.getColumnIndex("body"));
					String phone = cursor.getString(cursor.getColumnIndex("address"));
					info.setMsgId(msgId);
					info.setContent(smsBody);
					info.setPhone(phone);
					mMessageInboxList.add(info);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
			}
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(mMessageInboxList!=null){
			mMessageInboxList.clear();
			mMessageInboxList = null;
		}
	}

	/**
	 * 初始化环境
	 */
	public void initEnvironment() {
		// 防止休眠
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
	}


	public void showToast(final String msg) {
		Toast.makeText(BaseActivity.this, msg, Toast.LENGTH_LONG).show();
	}

	public void showDialog(String title,String message,DialogInterface.OnCancelListener listner){
		if(myDialog==null){
			myDialog = new ProgressDialog(BaseActivity.this);
			myDialog.setIndeterminate(true);
		}
		myDialog.setTitle(title);
		myDialog.setMessage(message);
		if(listner!=null){
			myDialog.setOnCancelListener(listner);
		}
		if(!myDialog.isShowing()&& !isFinishing()){
			myDialog.show();
		}
	}

	public void showDialog(String title,String message){
		showDialog(title,message,null);
	}
	public void showDialog(){
		showDialog(getString(R.string.dialog_title),getString(R.string.dialog_msg),null);
	}


	public void setDialogMessage(String message){
		if(myDialog!=null){
			myDialog.setMessage(message);
		}
	}

	public void dismissDialog(){
		if(myDialog!=null && myDialog.isShowing()&& !isFinishing()){
			myDialog.dismiss();
		}
	}
	

	public Map<String, String> getSmsExtendMap(MsgInfo msgInfo) {
		if(mSmsExtendMap ==null){
			mSmsExtendMap = new HashMap<String, String>();
		}
		mSmsExtendMap.clear();
		mSmsExtendMap.put("msgTime", String.valueOf(msgInfo.getRecieveTime()));
		if(VivoDemoUtil.NEED_SCRIPT){
			mSmsExtendMap.put("ALG_BASEPARSE_ENABLE", Boolean.TRUE.toString());
		}else{
			mSmsExtendMap.put("ALG_BASEPARSE_ENABLE", Boolean.FALSE.toString());
		}
		return mSmsExtendMap;
	}

}
