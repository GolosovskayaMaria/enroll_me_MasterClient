package get.enroll_me;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.core.content.ContextCompat;

public abstract class Base_Activity extends Activity{
	
public static Activity act;
TextView main_name;
SharedPreferences shar;
@SuppressLint("NewApi")
@Override
protected void onCreate(Bundle savedInstanceState) {
	
	super.onCreate(savedInstanceState);
	if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.brand_background));
	shar =	getSharedPreferences("name", 0); 
	setContentView(R.layout.base);

	main_name= findViewById(R.id.main_name);
}

public void LoadFrame(int res) {
	FrameLayout root = findViewById(R.id.content);
	LayoutInflater.from(getApplicationContext()).inflate(res, root);
	
	main_name= findViewById(R.id.main_name);
}
@Override
protected void onStart() {
	
	super.onStart();
	act = this;
}

@Override
protected void onStop() {
	
	super.onStop();
	act = null;
}
public void back(View v) {
	finish();
}
}
