package mcnulty.apps.batterywidget;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;

public class Notifier {


    public static void notify(Context context, String message) {
        
        
        Notification n  = new Notification.Builder(context)
        .setContentTitle("Notifier")
        .setContentText(message)
        .setSmallIcon(R.drawable.charging)
        .setAutoCancel(true).build();
    

        NotificationManager notificationManager = (NotificationManager) 
                  context.getSystemService(Context.NOTIFICATION_SERVICE); 
        
        notificationManager.notify(0, n); 
    }
}
