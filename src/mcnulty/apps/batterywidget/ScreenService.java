package mcnulty.apps.batterywidget;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

public class ScreenService extends Service {
	
	BroadcastReceiver screenOnReceiver;
	BroadcastReceiver screenOffReceiver;
	BroadcastReceiver userPresentReceiver;
	
	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		registerScreenOnReceiver();
		registerScreenOffReceiver();
		registerUserPresentReceiver();
		return super.onStartCommand(intent, flags, startId);
	}
	
	private void registerScreenOnReceiver() {
		screenOnReceiver = new BroadcastReceiver() {
	 
			@Override
			public void onReceive(Context context, Intent intent) {
				Log.w("WIDGET", intent.getAction());
				BatteryWidgetProvider.toggleAlarm(context, true);
			}
	 
		};
	 
		registerReceiver(screenOnReceiver, new IntentFilter(Intent.ACTION_SCREEN_ON));
	}

	private void registerScreenOffReceiver() {
		screenOffReceiver = new BroadcastReceiver() {
	 
			@Override
			public void onReceive(Context context, Intent intent) {
				Log.w("WIDGET", intent.getAction());
				BatteryWidgetProvider.toggleAlarm(context, false);
			}
	 
		};
	 
		registerReceiver(screenOffReceiver, new IntentFilter(Intent.ACTION_SCREEN_OFF));
	}
	
	private void registerUserPresentReceiver() {
		userPresentReceiver = new BroadcastReceiver() {
	 
			@Override
			public void onReceive(Context context, Intent intent) {
				Log.w("WIDGET", intent.getAction());
	 
				BatteryWidgetProvider.toggleAlarm(context, true);
			}
	 
		};
	 
		registerReceiver(userPresentReceiver, new IntentFilter(Intent.ACTION_USER_PRESENT));
	}
	
}
