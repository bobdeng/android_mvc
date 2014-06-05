package com.example.mvcsample;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.androidannotations.annotations.EService;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

@EService
public class ControlerService extends Service {

	BroadcastReceiver receiver;

	private Map<String, Method> actionMethod = new HashMap<String, Method>();
	private Executor executor=new ThreadPoolExecutor(2, 6, 60, TimeUnit.SECONDS, new LinkedBlockingDeque<Runnable>(20));

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

	public void doAction(final Context context, final Intent intent) {

			final Method method = actionMethod.get(intent.getAction());

			if (method != null) {
				executor.execute(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						try {
							method.invoke(ControlerService.this, context, intent);
						} catch (IllegalAccessException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IllegalArgumentException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (InvocationTargetException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				});
				
				
			}
	
	}
	
	void sendResponse(Intent src,Intent rlt){
		rlt.setAction(src.getAction()+"_resp");
		LocalBroadcastManager.getInstance(this).sendBroadcast(rlt);
	}

	@Action(action = "action1")
	public void doAction1(Context context, Intent intent) {
		Log.d("Controler", "doAction1");
		Intent rlt = new Intent();
		rlt.putExtra("name", "hello");
		sendResponse(intent,rlt);
	}

	@Action(action = "action2")
	public void doAction2(Context context, Intent intent) {
		Log.d("Controler", "doAction2");
		Intent rlt = new Intent();
		rlt.putExtra("name", "hello2");
		sendResponse(intent,rlt);
	}
	

}
