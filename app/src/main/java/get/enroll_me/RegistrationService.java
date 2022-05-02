package get.enroll_me;

import android.app.IntentService;
import android.content.Intent;

import com.google.android.gms.gcm.GcmPubSub;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import java.io.IOException;

public class RegistrationService extends IntentService {
    private static final String[] TOPICS = {"global"};
    static String registrationToken;
    static RegistrationService registrationService;
    public RegistrationService() {
        super("RegistrationService");
    }

    public RegistrationService(String name) {
        super(name);
        //   Log.e("String name",""+name);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        registrationService=this;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        InstanceID myID = InstanceID.getInstance(this);
        
        try {
            registrationToken = myID.getToken("951792751705", GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
   
            GcmPubSub subscription = GcmPubSub.getInstance(this);
            subscription.subscribe(registrationToken, "/topics/my_little_topic", null);
            //  subscription.subscribe(registrationToken, "/topics/global", null);  Log.e("String",""+myID);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
