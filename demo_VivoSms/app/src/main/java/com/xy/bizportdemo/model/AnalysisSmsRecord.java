package com.xy.bizportdemo.model;

import org.json.JSONObject;

public class AnalysisSmsRecord extends Record {
	public boolean parseMethod;// 识别方式
	public JSONObject smsParseValue;// 短信识别结果
	public JSONObject recognizeParseValue;//特征值识别结果


	@Override
    public String toString() {
		StringBuilder mSbResult = new StringBuilder();
		mSbResult.append(getPhoneNumber())
				.append(SYMBOL_TAB)
				.append(SYMBOL_COMMA)
				.append(getContent() == null ? "null": getContent().replace("  ", "").replace("\r\n", "").replace("\r","").replace(",", "，"))
				.append(SYMBOL_COMMA);

		mSbResult.append(smsParseValue == null ? "null" : smsParseValue.toString().replace("  ", "").replace("\r\n", "").replace(",", "，"))
				.append(SYMBOL_COMMA);
		mSbResult.append(recognizeParseValue == null ? "null" : recognizeParseValue.toString().replace("  ", "").replace("\r\n", "").replace(",", "，"))
		.append(SYMBOL_COMMA);
		mSbResult .append(getSmsParseTime()+" ms")
    	 .append(SYMBOL_COMMA);
		mSbResult .append(getRecogniseParseTime()+" ms")
				.append(SYMBOL_COMMA);
    	 
        return mSbResult.toString();
    }



}
