package com.xy.bizportdemo.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;


import com.xy.bizportdemo.R;
import com.xy.bizportdemo.activities.base.BaseActivity;
import com.xy.bizportdemo.model.MsgInfo;
import com.xy.bizportdemo.util.VivoDemoUtil;

import org.apache.commons.text.StringEscapeUtils;
import org.json.JSONObject;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import cn.com.xy.sms.util.ParseCardManager;
import cn.com.xy.sms.util.ParseManager;

/**
 * 
 * @author Administrator 展示识别api和企业查询api的结果
 */
public class ParseResultActivity extends BaseActivity {
    public static final String EXTRA_MSG_OBJECT = "MSG";
    private static HashMap<String, String> mExtendsMap;
    private final int WHAT_START_PARSE = 1;
    private final int WHAT_PARSING = 2;
    private final int WHAT_END_PARSE = 3;
    private TextView mTvResultTitle;
    private TextView mTvResult;
    private Executor executor = Executors.newSingleThreadExecutor();
    private MsgInfo mMsgInfo;
    private TextView mTvMsgTitle;
    private StringBuffer mResultSb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parse_result);
        getData();
        initView();
        parseMessage(mMsgInfo);
    }

    private void getData() {
        Intent intent = getIntent();
        if(intent!=null){
            mMsgInfo = (MsgInfo) intent.getParcelableExtra(EXTRA_MSG_OBJECT);
        }
    }

    private void initView() {
        mTvResultTitle = (TextView) findViewById(R.id.tv_parse_result_title);
        mTvMsgTitle = (TextView) findViewById(R.id.tv_msg);
        mTvResult = (TextView) findViewById(R.id.tv_parse_result);
        mTvResultTitle.setText(R.string.result_title);
        mTvMsgTitle.setText("\n"+mMsgInfo.getPhone()+"\n"+ mMsgInfo.getContent());
    }

    private void parseMessage(final MsgInfo msgInfo) {
        mHandler.sendEmptyMessage(WHAT_START_PARSE);
        executor.execute(new Runnable() {
            @Override
            public void run() {
                if(msgInfo==null){
                    Message msg = mHandler.obtainMessage();
                    msg.what = WHAT_END_PARSE;
                    msg.obj = "";
                    msg.sendToTarget();
                    return;
                }
                mResultSb = new StringBuffer();
                long start = System.currentTimeMillis();
                try{
                    String parsePhone = VivoDemoUtil.getPhone(msgInfo);
                    Map<String, Object> resultMap = ParseCardManager.parseMsgForCard(ParseResultActivity.this, parsePhone, msgInfo.getSmsCenterNum(), msgInfo.getContent(),getSmsExtendMap(msgInfo));
                    long end = System.currentTimeMillis();
                    resultMap = VivoDemoUtil.sortMap(resultMap);
                    mResultSb.append("短信解析结果：   ("+VivoDemoUtil.getTimeTxt(end-start)+")\n");
                    mResultSb.append(VivoDemoUtil.getResultStrByMap(ParseResultActivity.this,resultMap));
                    mResultSb.append("\n\n");
                    parseRecognize(msgInfo);//解析特征值
                } catch (Exception e) {
                    e.printStackTrace();
                    mResultSb.append("短信解析结果：\n");
                    mResultSb.append(VivoDemoUtil.getResultStrByMap(ParseResultActivity.this,null));
                    mResultSb.append("\n\n");
                    parseRecognize( msgInfo);
                }
            }
        });
    }
    /**
     * 解析特征值
     * @param msgInfo
     */
    private void parseRecognize(final MsgInfo msgInfo) {
        try{
            long start = System.currentTimeMillis();
            String parsePhone = VivoDemoUtil.getPhone(msgInfo);
            JSONObject recogniseData = ParseManager.parseRecogniseValue(parsePhone, msgInfo.getContent(), msgInfo.getRecieveTime(), getSmsExtendMap(msgInfo));
            long end = System.currentTimeMillis();

            mResultSb.append("特征值解析结果：("+VivoDemoUtil.getTimeTxt(end-start)+")\n");
            mResultSb.append(VivoDemoUtil.getResultStrByJson(ParseResultActivity.this,recogniseData));

            Message msg = mHandler.obtainMessage();
            msg.what = WHAT_END_PARSE;
            msg.obj = mResultSb.toString();
            msg.sendToTarget();

        }catch (Exception e){
           e.printStackTrace();
            mResultSb.append("特征值解析结果：\n");
            mResultSb.append(VivoDemoUtil.getResultStrByJson(ParseResultActivity.this,null));
        }
    }

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            int what = msg.what;
            switch (what){
                case WHAT_START_PARSE:
                    showDialog();
                    break;
                case WHAT_PARSING:
                    break;
                case WHAT_END_PARSE:
                    String result = (String)msg.obj;
                    endParseView(result);
                    break;

            }
        }
    };

    private void endParseView(String result) {
        dismissDialog();
        mTvResult.setText(result);
    }

}
