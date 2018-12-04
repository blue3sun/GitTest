package com.xy.bizportdemo;

import android.content.Context;


import java.util.Map;

import cn.com.xy.sms.sdk.action.AbsSdkDoAction;

/**
 * @Classname MySdkDoAction
 * @Describe
 * @Author zenglanjing
 * @Time 2018/11/14 18:00
 */
public class MySdkDoAction extends AbsSdkDoAction{
    public final String TAG = MySdkDoAction.class.getSimpleName();

    public static boolean INIT_FINISHED = false;

    @Override
    public void onEventCallback(int eventType, Map<String, Object> extend) {
        if(eventType == AbsSdkDoAction.SDK_EVENT_LOAD_COMPLETE){
            INIT_FINISHED = true;
        }
    }


    /**
     * 发短信, 双卡需要指定卡位发短信.
     *
     * @param context
     * @param phoneNum 接收者号码
     * @param sms      短信内容
     * @param simIndex 卡位 此值需要在 调用parseMsgToPopupWindow方法的时候
     *                 在其extend函数中.加入simIndex的key将当前sim卡位传入. -1表示没有传入卡位
     * @param params
     */
    @Override
    public void sendSms(Context context, String phoneNum, String sms, int simIndex, Map<String, String> params) {

    }

    /**
     * 打开短信原文进入会话
     *
     * @param context
     * @param phoneNum 短信号码
     * @param params
     */
    @Override
    public void openSms(Context context, String phoneNum, Map<String, String> params) {

    }

    /**
     * 根据号码获取联系人名称
     *
     * @param context
     * @param phoneNum
     * @return
     */
    @Override
    public String getContactName(Context context, String phoneNum) {
        return null;
    }

    /**
     * 标记短信已读
     *
     * @param context
     * @param msgId
     */
    @Override
    public void markAsReadForDatabase(Context context, String msgId) {

    }

    /**
     * 删除短信
     *
     * @param context
     * @param msgId
     */
    @Override
    public void deleteMsgForDatabase(Context context, String msgId) {

    }
}
