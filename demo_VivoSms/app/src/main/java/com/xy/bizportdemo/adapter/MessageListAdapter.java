package com.xy.bizportdemo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.xy.bizportdemo.R;
import com.xy.bizportdemo.model.MsgInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lanjing on 2018/11/15.
 */

public class MessageListAdapter extends BaseAdapter {

    private Context mConversationListContext;
    private final LayoutInflater mInflater;
    private List<MsgInfo> mMessageList;

    public MessageListAdapter(Context context) {
        mConversationListContext = context;
        mInflater = LayoutInflater.from(context);
        mMessageList = new ArrayList<MsgInfo>();
    }
    public void setData(List<MsgInfo> msgInfos){
        this.mMessageList = msgInfos;
        notifyDataSetChanged();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getCount() {
        if(mMessageList ==null){
            return 0;
        }else{
            return mMessageList.size();
        }
    }

    @Override
    public MsgInfo getItem(int position) {
        if(position>=0 && mMessageList !=null && mMessageList.size()>position){
            return mMessageList.get(position);
        }else{
            return null;
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh = null;
        if(convertView==null){
            convertView = mInflater.inflate(R.layout.item_message_list,parent,false);
            vh = new ViewHolder();
            vh.tvPhone = (TextView)convertView.findViewById(R.id.tv_phone);
            vh.tvContent = (TextView)convertView.findViewById(R.id.tv_content);
            convertView.setTag(vh);
        }else{
            vh = (ViewHolder) convertView.getTag();
        }
        MsgInfo msgInfo = getItem(position);
        if(msgInfo!=null){
            convertView.setVisibility(View.VISIBLE);
            vh.tvPhone.setText(msgInfo.getPhone());
            vh.tvContent.setText(msgInfo.getContent());
        }else{
            convertView.setVisibility(View.GONE);
        }
        return convertView;
    }

    public class ViewHolder{
        public TextView tvPhone;
        public TextView tvContent;
    }
}
