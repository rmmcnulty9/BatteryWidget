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
import android.content.SharedPreferences;
import android.os.BatteryManager;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;


public class RingBatteryWidgetProvider extends AppWidgetProvider {
    protected static final String UPDATE = "mcnulty.apps.batterywidget.UPDATE";
    protected static final String UPDATE_EXTRA = "mcnulty.apps.batterywidget.UPDATE_EXTRA";

    private int m_level = 100;
    private boolean m_showCharging = false;
    
    @Override
    public void onEnabled(Context context) {
        
        context.startService(new Intent(context, BatteryService.class));
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context, this.getClass()));
        this.onUpdate(context, appWidgetManager, appWidgetIds);
        fireUpdateNow(context);
        toggleAlarm(context, true);
        super.onEnabled(context);
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
    }

    @Override
    public void onDisabled(Context context) {
        context.stopService(new Intent(context, BatteryService.class));
        toggleAlarm(context, false);
        super.onDisabled(context);
    }

    @Override
     public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        
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
            
            m_showCharging = intent.getBooleanExtra(UPDATE_EXTRA, false);
            
            Intent batteryIntent = context.getApplicationContext().registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
            getBatteryInfo(context, batteryIntent);
            this.onUpdate(context, appWidgetManager, appWidgetIds);
        }
    }
    
    public void getBatteryInfo(Context context, Intent batteryIntent) {
     
        int level = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
        int scale = batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE, 100);
        m_level = level * 100 / scale;
        
        int status = batteryIntent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);

        m_showCharging = BatteryManager.BATTERY_STATUS_CHARGING == status ? true : false;
    }
    
    public void updateWidget(Context context, AppWidgetManager appWidgetManager, int widgetId ) {
        RemoteViews view = new RemoteViews(context.getPackageName(), R.layout.battery_widget_ring_layout);
        //Update percentage
        view.setTextViewText(R.id.value, m_level + "%");

        //Update background
        int resourceId = getDrawableResourceId(m_level);
        view.setImageViewResource(R.id.ring_value, resourceId);

        //Update charging indicator
        int state = View.INVISIBLE;
        if(m_showCharging)
            state = View.VISIBLE;
        view.setInt(R.id.charging_indicator, "setVisibility", state);
        appWidgetManager.updateAppWidget(widgetId, view);
    }    

    /*
     * Static methods
     */
    
    public static void fireUpdateNowWithExtra(Context context, boolean extra_value) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(UPDATE);
        intent.putExtra(UPDATE_EXTRA, extra_value);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME, 0, pendingIntent);
    }
    
    public static void fireUpdateNow(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(UPDATE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME, 0, pendingIntent);
    }
    
    public static void toggleAlarm(Context context, boolean turnOn) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(UPDATE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
        fireUpdateNow(context);
        if (turnOn) { // Add extra 1 sec because sometimes ACTION_BATTERY_CHANGED is called after the first alarm
            alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + 1000, 300 * 1000, pendingIntent);
        } else {
            alarmManager.cancel(pendingIntent);
        }
    }
    
    static private int getDrawableResourceId(int level) {
        
        if(level > 90) {
            return R.drawable.ring_color_22aa00;
        }
        if(level > 80) {
            return R.drawable.ring_color_44aa00;
        }
        if(level > 70) {
            return R.drawable.ring_color_66aa00;
        }
        if(level > 60) {
            return R.drawable.ring_color_88aa00;
        }
        if(level > 50) {
            return R.drawable.ring_color_aaaa00;
        }
        if(level > 40) {
            return R.drawable.ring_color_aa8800;
        }
        if(level > 30) {
            return R.drawable.ring_color_aa6600;
        }
        if(level > 20) {
            return R.drawable.ring_color_aa4400;
        }
        if(level > 10) {
            return R.drawable.ring_color_aa2200;
        }

        return R.drawable.ring_color_aa0000;
    }
}
