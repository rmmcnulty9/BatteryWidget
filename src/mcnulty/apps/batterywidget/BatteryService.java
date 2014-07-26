package mcnulty.apps.batterywidget;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

public class BatteryService extends Service {
	
	BroadcastReceiver updateReceiver;
	
	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.w("WIDGET", "Starting Battery Service!");
		registerReceivers();
		return super.onStartCommand(intent, flags, startId);
	}
	
	@Override
	public void onDestroy() {
		unregisterReceiver(updateReceiver);
	}
	
	private void registerReceivers() {
		updateReceiver = new BroadcastReceiver() {
	 
			@Override
			public void onReceive(Context context, Intent intent) {
				String action = intent.getAction();
				
				Log.w("WIDGET", intent.getAction());
				Notifier.notify(context, action);
				
				if(action.equals(Intent.ACTION_SCREEN_ON) || action.equals(Intent.ACTION_USER_PRESENT))
					BatteryWidgetProvider.toggleAlarm(context, true);
				else if(action.equals(Intent.ACTION_SCREEN_OFF))
					BatteryWidgetProvider.toggleAlarm(context, false);
				else if(action.equals(Intent.ACTION_POWER_CONNECTED))
					BatteryWidgetProvider.fireUpdateNowWithExtra(context, true);
				else if(action.equals(Intent.ACTION_POWER_DISCONNECTED))
					BatteryWidgetProvider.fireUpdateNowWithExtra(context, true);
				else if(action.equals(Intent.ACTION_BATTERY_CHANGED))
					BatteryWidgetProvider.fireUpdateNow(context);
				else
					Log.e("WIDGET", "Intent not supported: " + action);
			}
	 
		};
		
		IntentFilter filter = new IntentFilter();
		filter.addAction(Intent.ACTION_SCREEN_ON);
		filter.addAction(Intent.ACTION_SCREEN_OFF);
		filter.addAction(Intent.ACTION_USER_PRESENT);
		
		filter.addAction(Intent.ACTION_POWER_CONNECTED);
		filter.addAction(Intent.ACTION_POWER_DISCONNECTED);
//		filter.addAction(Intent.ACTION_BATTERY_CHANGED);
		
		registerReceiver(updateReceiver, filter);
	}
	
}
