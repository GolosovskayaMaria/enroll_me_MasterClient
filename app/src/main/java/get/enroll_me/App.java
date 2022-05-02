package get.enroll_me;

import android.app.Application;
import android.content.Intent;
import android.content.SharedPreferences;


public class App extends Application{
public static App inst;
public  SharedPreferences sp;
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		try {
		inst=this;
		sp = getSharedPreferences("Acc_data", 0);
		
	    Intent i = new Intent(this, RegistrationService.class);
        if(RegistrationService.registrationService==null)
            startService(i);
        Intent iw = new Intent(this, NotificationsListenerService.class);
        if(NotificationsListenerService.notificationsListenerService==null)
            startService(iw);
     
		}catch(Exception e) {}
	}


}
