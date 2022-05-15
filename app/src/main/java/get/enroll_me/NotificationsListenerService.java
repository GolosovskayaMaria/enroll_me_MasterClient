package get.enroll_me;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;

import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;

import androidx.core.app.NotificationCompat;


@SuppressLint("NewApi")
public class NotificationsListenerService extends GcmListenerService {
    static int num;
    static String in="";
 //   public static String mainHeadeer;
    static NotificationsListenerService notificationsListenerService;
 //   boolean intents=true;
    private Thread traa;
    @Override
    public void onMessageReceived(String s, Bundle bundle) {
        Log.e("push", "received " + s);
        super.onMessageReceived(s, bundle);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        notificationsListenerService=this;
     
    }

    @Override
    public void handleIntent(Intent arg0) {
        Log.e("push", "intent: " + arg0);
        // super.handleIntent(arg0);
        try {
            Bundle bundle = arg0.getExtras();
            Log.e("push", "bundle: " + bundle);
            if(bundle == null) return;
         String tag = bundle.getString("gcm.notification.tag");
         String type = bundle.getString("type");
         SharedPreferences sp = getSharedPreferences("name", 0);
         NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
         notificationManager.cancelAll(); 
         switch(type) {
         case "1" :{
        	 String  uuid = bundle.getString("name").trim(); 
        	 String  base = bundle.getString("ip");
             Log.e("push", "new server found: " + uuid + " " + base);
        	 if(uuid.substring(0 ,4).equalsIgnoreCase(sp.getString("UUID", "UUID").substring(0 ,4).trim())) {
        		 sp.edit().putString("base", base).commit();
        	     Intent intent = new Intent(this, MainActivity.class);
        	        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        	        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);  
        	        startActivity(intent);
        	 }
         }return;
         case "2":{
        	 String name = bundle.getString("name");
             Log.e("push", "new message for: " + name);
        	 if(name.equalsIgnoreCase(sp.getString("UUID", ""))) {
        		 String  base = bundle.getString("ip"); 
        		 sp.edit().putString("base", base).commit();
        		 if(MainActivity.main != null) {
        			 Intent intent = new Intent(this, MainActivity.class);
        		       intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        		        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
        		        intent.putExtra("title","Сообщение для " + sp.getString("name", ""));
        		        intent.putExtra("meeting", bundle.getString("meeting"));
        		        intent.putExtra("user_name", bundle.getString("gcm.notification.title"));
        		        intent.putExtra("createDate", bundle.getString("gcm.notification.body"));
        		        startActivity(intent);
        		 }
        		 else	
        	        notifi(bundle);
        	 }
         }
         break;
         }
         
  

        } catch (Exception e2) {
            e2.printStackTrace();
          //  ((NotificationManager) getSystemService("notification")).cancelAll();
        } 
    }
    
    @SuppressWarnings("deprecation")
	public void notifi(Bundle bundle) throws Exception {
       NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
     //  notificationManager.cancelAll();
       SharedPreferences sp = getSharedPreferences("name", 0);
        Intent intent = new Intent(this, MainActivity.class);
     //   intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
        intent.putExtra("title","Сообщение для " + sp.getString("name", ""));
        intent.putExtra("meeting", bundle.getString("meeting"));
        intent.putExtra("user_name", bundle.getString("gcm.notification.title"));
        intent.putExtra("createDate", bundle.getString("gcm.notification.body"));
      PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        Uri SoundUri = Uri.parse(getSharedPreferences("TELEPHONY_SERVICE", 0).getString("NotifiR", "content://settings/system/notification_sound"));
       // String[] aaas = SoundUri.toString().split("/");
        String id = "NotificationId";
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, id)
        		.setSmallIcon(R.drawable.ic_launcher)
        		.setContentTitle(bundle.getString("gcm.notification.title"))
        		.setContentText("Сообщение для " + sp.getString("name", ""))
        		.setAutoCancel(true).
        		setVibrate(new long[]{100, 100, 100, 100, 100}).setPriority(4)
        		.setSound(SoundUri)
        		.setContentIntent(pendingIntent);
       Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), SoundUri);
   	long[] dd={100, 100, 100, 100,100 };
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        NotificationChannel serviceChannel = new NotificationChannel(
        		id,
                "master",
                NotificationManager.IMPORTANCE_DEFAULT
        ); 
        serviceChannel.setDescription("get.enroll_me");
        serviceChannel.enableLights(true);
        serviceChannel.enableVibration(true);
   //     serviceChannel.setVibrationPattern(dd);
        notificationManager.createNotificationChannel(serviceChannel);
}
       
	
    
     	Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
     
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        	AlarmManager alarms =
        			(AlarmManager)getSystemService(Context.ALARM_SERVICE);
        	String ALARM_ACTION = "ALARM_ACTION";
        	//Intent intentToFire = new Intent(ALARM_ACTION);
        	PendingIntent pendingIntent1 = PendingIntent.getBroadcast(this, 0, new Intent(ALARM_ACTION), 0);
        	alarms.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, 1, pendingIntent1);
        	//ComponentName jobService = new ComponentName(getApplicationContext(), ExerciseJobService.class);
        	//JobInfo.Builder exerciseJobBuilder = new JobInfo.Builder(1, jobService);
        	//exerciseJobBuilder.setTransientExtras(bundle);
        //	r.play();
        	
        	VibrationEffect vibe=VibrationEffect.createOneShot(500, 100);
      //
     	v.vibrate(vibe);
        	//v.vibrate(dd); 
        }//else  v.vibrate(500);
 
        else {
        play(r);	
       
       	vibrate(dd,v);
        }
    	
     	
       notificationManager.notify(1251, notificationBuilder.build());
    }

	private void vibrate(long[] dd,Vibrator v) {
		try {
		v.vibrate(dd, VibrationEffect.DEFAULT_AMPLITUDE);
	}catch (Exception e) {
        
        e.printStackTrace();
    }
	}

	private void play(Ringtone r) {
		try {
		r.play(); 
		}catch (Exception e) {
            
            e.printStackTrace();
        }
	}

	 @Override
	public void onDestroy() {
		
		super.onDestroy();
		notificationsListenerService = null;
	}


}
