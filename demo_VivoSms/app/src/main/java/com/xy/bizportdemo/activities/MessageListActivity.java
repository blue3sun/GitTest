package com.xy.bizportdemo.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.xy.bizportdemo.R;
import com.xy.bizportdemo.activities.base.BaseActivity;
import com.xy.bizportdemo.adapter.MessageListAdapter;
import com.xy.bizportdemo.model.MsgInfo;
import com.xy.bizportdemo.util.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
/**
 * @ClassName MessageListActivity
 * @Describe 短信列表页
 * @Author zenglanjing
 * @Time 2018/11/15 21:46
 */
public class MessageListActivity extends BaseActivity {
    public static final String EXTRA_IS_INBOX = "IS_INBOX";
    public static final String EXTRA_FILE_PATH = "FILE_PATH";
    private ListView mLvMsgs;
    private MessageListAdapter mMessageListAdapter;
    private List<MsgInfo> mMessageList;
    private String mFilePath;
    private boolean mIsInbox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_list);
        getData();
        initView();
        initMessageList();
    }

    private void getData() {
        Intent intent = getIntent();
        if(intent!=null){
            mIsInbox = intent.getBooleanExtra(EXTRA_IS_INBOX,false);
            mFilePath = intent.getStringExtra(EXTRA_FILE_PATH);
        }
    }

    private void initView() {
        mLvMsgs = (ListView)findViewById(R.id.lv_msgs);
        mMessageListAdapter = new MessageListAdapter(this);
        mLvMsgs.setAdapter(mMessageListAdapter);
        //点击item进入卡片映射解析页面
        mLvMsgs.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MsgInfo msgInfo = mMessageListAdapter.getItem(position);
                if(msgInfo==null){
                    showToast(getString(R.string.msg_is_null));
                }else{
                    Intent intent = new Intent(MessageListActivity.this,ParseResultActivity.class);
                    intent.putExtra(ParseResultActivity.EXTRA_MSG_OBJECT,msgInfo);
                    startActivity(intent);
                }
            }
        });
    }
    private void initMessageList() {
        if(mIsInbox){
            loadMsg();
        }else{
            try {
                if (!TextUtils.isEmpty(mFilePath)) {
                    File file = new File(mFilePath);
                    if (file.exists()) {
                        if (mFilePath.endsWith(".csv") || mFilePath.endsWith(".txt")) {
                            mMessageList = FileUtils.readFile(file, -1, -1);
                        }else if(mFilePath.endsWith(".xml")){
                            mMessageList = FileUtils.loadMessageInfoXML(mFilePath);
                        }
                    }
                }
                setAdapterListData(mMessageList);
            } catch (Exception e) {
                showToast(getString(R.string.msg_file_read_error));
                e.printStackTrace();
            }
        }
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
                if(mMessageList==null){
                    mMessageList = new ArrayList<>();
                }
                mMessageList.clear();
                mMessageList.addAll(mMessageInboxList);
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                dismissDialog();
                setAdapterListData(mMessageList);
            }
        };
        task.execute();
    }
    private void setAdapterListData(List<MsgInfo> mssageList){
        if (mssageList == null || mssageList.size() == 0) {
            showToast(getString(R.string.empty_msg_list));
        }else{
            mMessageListAdapter.setData(mssageList);
        }
    }

}
