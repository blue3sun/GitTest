package com.xy.bizportdemo;

import java.util.HashMap;

import android.app.Application;
import android.content.Context;
import android.telephony.TelephonyManager;

import com.xy.bizportdemo.util.LogXY;

import cn.com.xy.sms.sdk.constant.Constant;
import cn.com.xy.sms.sdk.log.LogManager;
import cn.com.xy.sms.sdk.util.StringUtils;
import cn.com.xy.sms.util.ParseManager;

public class MyApplication extends Application {
	private static MyApplication mInstance = null;
	public MySdkDoAction mySdkDoAction = null;
	final static String  DUOQU_SDK_CHANNEL ="h5aE3uBwM_DEMO";
	final static String DUOQU_SDK_RSAKEY = "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQCy1q5yc5M+vOowgQw4E3OyNcqgno6wqqCZ5YU0WTGuN9JeSk/9AqiNdkWu6qHwPq7WnYB1yMbmWYQ62viX5FYfKzfaTs2+X83h8Iosy23pCGQ4Y8Ar8Sm+FSxJD0mC40ZDCC9QSVAoWGij8HZk5J58EnnHo7YiGTcpmAaUeCA8HkPta68hTLnuBczDzNtWGK3oridwtAc2XSrckNxbKgjKx9HVkn1rKetHznlqMLW6J1vDTGOaBfwdtVYgQIoFHlxTuw2HCnsRYzePdv56QYk26skUKjwVfGnJ782mU1l6aZgRxzPgfLI0vAdcTodNWBcBDD52s7GZ5/+7v8T7TUiXAgMBAAECggEBAJeYx/OY6R5rgZFwMu1t/8r3MjaJcadsXAtYtluzqBtoklj/YWK12C9iLJujpXZDjG28wWIWuhQVmbNSXxxSoHZ3ajcSSfGwwJNgFaD3KMo7JMlwNTyKh7nrtMiRvtzGz46O55yFKP+qQbmGYrYP3hCKkNScgA3TliEaD6nXUmviSIZNSONSibuJLwdwSYf3NKUrPZtQ6YnJFqlydgtfnw2e/xdXP2JVdbSrWSSRMwfdBuiuH7M4oYzF+tlITu8IlVjesuZRjbFbSN33fn7GJs+aq+I2YWjRyRsUD/ju7W+rmyGHcA0y2yJpBiNh7XPQ2BZAnh9RhLTeQF1m8KVozkECgYEA4ypTStrUSWm9DCCU/rWADlM/TMOYWrr4u3DQg7sbmS+CoOAypqN4oZkXEbH61EfsXxBdKG7Rk8PBbUhgYQI6tQxDcrX0hJEsyk8Nu9/sJ6/seAC2FYt1owNVp9ZgI+rvOeK2ioYKchxAr72ju7A3PM+DwhrK48kmMMcC1k0LakcCgYEAyYn+AmCcRHjORAygsToP2e4cxUhfO/UeJmXk05SAMLWEmId8w/ZedgyId7XNrvkcLszFqf9OYB++u8UbIlBo0UCln2IhEYTJ4Gvh8edxZnpCSfmzyAVaWY8kfPTnMIQzo358+RIta/yhKctwf6ntqA1PXJdDhPyfPeTJslcaBzECgYAKG4QBPKNdcNr8gyad/q1n9cRHQhyxTMhsbqSYd+bOdhK13cPjLjrb1df07Zpff6PAxxWeCvBARuGAX0wKhPLfw1/s5rOuglcf2UyfI2N6kJVQ8ZcodkARtMBG637zmixywuekWjp0wVUPfLygSULr7b/LDy4f3H9ahEBYEK2FWQKBgQDGk97h/Ms8dEPYrRCQT4Na2dEjWeHsM+LXNsK27rU4SUIzAByhAVmlu0ejnFDjvLRWMJH6pIJXGDBY1yebMdt1gtsuJgQrPMmlBOeF8OO9c21pck4AIbYaVH5BWRWKqlgvGeyNhamXoz4w7jEUuRxf321479opaLiGxa0uub/9oQKBgCovxQDFb2SFTzOazd7CWnjPowPk8NqgwmLF19pSJPzVRvBMp/zSY6/7/3ey8XznCOGvikH3f7KMjs5X0OGBWbZZ+XgztH/ekWO0lJBqvdn5xu85IrNSW7zbkleg/sck8TCk1UiXybZ34lmqgXg6mgToiLeEmG6XNa00GVH1tHp+";
	final static String  DUOQU_SDK_SECRETKEY ="MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAKQh82GqxYnnQp9pGDgGkvMC4BApznKNg0jNepjkoi9PIRDB9Bq+QtLv41SxuXc0/DzbbVpzSgMtBpSWPKj+YV1SKnn/8jnlYGB6oMBOSSaOyDkzKUJsUyJ5KaJiu0ZVdBUSQPZGz4NV/w0fgWOnpmkZupKhfxCqXwsjmHloTe4HAgMBAAECgYAWW2WKlTdpLG4fxrH817Bml6qrqPYtFmeeoBamAuawqQeo/7JffjEeCH0fyUGpIjeFlqITowae3iA6VyiWIGhklGzXy4heObtFrDQ/81cbY99NTRN4zGcR9cPdHqBIijJj6NQ4P/bs3U+QCuvSkOdRk2bsvP9VR86Pf9O0S4WZCQJBAOuciwAB7Xs4YYfyJeGPC9jNGdK8BuWInyaAFuoMiyiZnd7pRhmpxiRvdYjTMoBe33rYnE38SqVidV241e80dl0CQQCyVfMeVOeN/P9V9tYVsMNrJIb52vux7NQ5vzpb0maQbA5SgsJEK4llZZH0OpSzBqgGvJA1oJb02JYpzqiDgyezAkB/kXbBPkny2YgVL0rLYcQsUoCU1TF2vg5NrjS57Ki8BtCvjOZjpsSdnauptZA2aSffP8EBiIRyH2kkoZtTtLgRAkEAr4kicOypr30j5327ZEZFVCT0JuOBc7TlgKHV06PaCLYTsuu6RFeGOiQr8fXngABBS8A3QlH7xK2bwTMFc82ZOQJBALaK65SbjdxDZaywWSpxm3FDPig3o1C2i/VxAGTYevvKuxs3ebhdJycVa+FS5pvG8IxjD5JC8IMyOcyPFHRGKos=";
	public static MyApplication getMyApplication() {
		return mInstance;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		mInstance = this;
		LogXY.enableDebug(true);
		LogManager.debug = true;

		try {
			mySdkDoAction = new MySdkDoAction();
			ParseManager.setSdkDoAction(mySdkDoAction);// 设置动作执行的实例

			// extend初始化的扩展参数
			HashMap<String, String> extend = new HashMap<String, String>();
			extend.put(Constant.ONLINE_UPDATE_SDK, "1");// 是否支持 在线升级sdk 0:不支持 1:支持,默认是支持
			extend.put(Constant.SUPPORT_NETWORK_TYPE, "2");// 支持网络 0:不支持 1:wifi 2:3g及wifi
			extend.put(Constant.SMARTSMS_ENHANCE, Boolean.TRUE.toString());
			extend.put(Constant.SECRETKEY, DUOQU_SDK_SECRETKEY);
			extend.put(Constant.RSAPRVKEY, DUOQU_SDK_RSAKEY);
			LogManager.debug = true;
			ParseManager.initSdk(MyApplication.this, DUOQU_SDK_CHANNEL, null, true, true, extend);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static MyApplication getInstance() {
		return mInstance;
	}

	public static String getICCID(Context context) {
		try {
			TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
			if (!StringUtils.isNull(manager.getSimSerialNumber())) {
				return manager.getSimSerialNumber();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return "";
	}

}
