package mcnulty.apps.batterywidget;

import mcnulty.apps.batterywidget.R;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.SystemClock;
import android.util.Log;
import android.widget.RemoteViews;


public class BatteryWidgetProvider extends AppWidgetProvider {
	protected static final String UPDATE = "mcnulty.apps.batterywidget.UPDATE";
	private int m_level;
	
	@Override
	public void onEnabled(Context context) {
		context.startService(new Intent(context, ScreenService.class));
		AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
		int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context, this.getClass()));
		this.onUpdate(context, appWidgetManager, appWidgetIds);
		toggleAlarm(context, true);
	}

	@Override
	public void onDisabled(Context context) {
		context.stopService(new Intent(context, ScreenService.class));
		toggleAlarm(context, false);
	}
	
	public static void toggleAlarm(Context context, boolean turnOn) {
		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		Intent intent = new Intent(UPDATE);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
	 
		if (turnOn) { // Add extra 1 sec because sometimes ACTION_BATTERY_CHANGED is called after the first alarm
			alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + 1000, 300 * 1000, pendingIntent);
			Log.w("WIDGET","Alarm set");
		} else {
			alarmManager.cancel(pendingIntent);
			Log.w("WIDGET","Alarm disabled");
		}
	}

	@Override
	 public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		
		getBatteryLevel(context);
		
        for(int widgetId : appWidgetIds) {
        	updateWidget(context, appWidgetManager, widgetId);
        }
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		super.onReceive(context, intent);
	 
		if (intent.getAction().equals(UPDATE)) {
			AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
			int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context, this.getClass()));
			this.onUpdate(context, appWidgetManager, appWidgetIds);
		}
	}
	
	public void getBatteryLevel(Context context) {

		Intent batteryIntent = context.getApplicationContext().registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
	 
		int level = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
		int scale = batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE, 100);
		m_level = level * 100 / scale;
		Log.e("WIDGET", "UPDATE --> " + m_level);
	}
	
	public void updateWidget(Context context, AppWidgetManager appWidgetManager, int widgetId ) {

        RemoteViews view = new RemoteViews(context.getPackageName(), R.layout.battery_widget_layout);
        view.setTextViewText(R.id.value, m_level + "%");
        int resourceId = getDrawableResourceId();
        view.setImageViewResource(R.id.box_value, resourceId);
        appWidgetManager.updateAppWidget(widgetId, view);
	}

	private int getDrawableResourceId() {
		if(m_level > 90) {
            return R.drawable.color_7722aa00;
        }
        if(m_level > 80) {
            return R.drawable.color_7744aa00;
        }
        if(m_level > 70) {
            return R.drawable.color_7766aa00;
        }
        if(m_level > 60) {
            return R.drawable.color_7788aa00;
        }
        if(m_level > 50) {
            return R.drawable.color_77aaaa00;
        }
        if(m_level > 40) {
            return R.drawable.color_77aa8800;
        }
        if(m_level > 30) {
            return R.drawable.color_77aa6600;
        }
        if(m_level > 20) {
            return R.drawable.color_77aa4400;
        }
        if(m_level > 10) {
            return R.drawable.color_77aa2200;
        }

        return R.drawable.color_77aa0000;
	}
}
