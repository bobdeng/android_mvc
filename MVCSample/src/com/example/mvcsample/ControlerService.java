package com.example.mvcsample;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.androidannotations.annotations.EService;
import org.androidannotations.annotations.ViewById;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.TextView;

@EService
public class ControlerService extends Service {

	BroadcastReceiver receiver;

	private Map<String, Method> actionMethod = new HashMap<String, Method>();

	@Override
	public IBinder onBind(Intent arg0) {

		return null;
	}

	@Override
	public void onStart(Intent intent, int startId) {
		// TODO Auto-generated method stub
		receiver = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				doAction(context, intent);

			}
		};
		Method[] methods = this.getClass().getMethods();
		for (Method m : methods) {
			if (m.isAnnotationPresent(Action.class)) {
				Action action = m.getAnnotation(Action.class);
				actionMethod.put(action.action(), m);
				LocalBroadcastManager.getInstance(this).registerReceiver(
						receiver, new IntentFilter(action.action()));
			}
		}
	}

	public void doAction(Context context, Intent intent) {

		try {
			Method method = actionMethod.get(intent.getAction());

			if (method != null) {
				method.invoke(this, context, intent);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	

	@Action(action = "action1")
	public void doAction1(Context context, Intent intent) {
		Log.d("Controler", "doAction1");
		Intent rlt = new Intent();
		rlt.putExtra("name", "hello");
		rlt.setAction(intent.getAction()+"_resp");
		LocalBroadcastManager.getInstance(this).sendBroadcast(rlt);
	}

	@Action(action = "action2")
	public void doAction2(Context context, Intent intent) {
		Log.d("Controler", "doAction2");
		Intent rlt = new Intent();
		rlt.putExtra("name", "hello2");
		rlt.setAction(intent.getAction() + "_resp");
		LocalBroadcastManager.getInstance(this).sendBroadcast(rlt);
	}

}
