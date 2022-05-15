package get.enroll_me;

import java.util.ArrayList;
import java.util.List;


import a.b.framehelper.RpinnerAdapter;

import android.view.View;
import android.widget.AdapterView;

import android.widget.AdapterView.OnItemSelectedListener;

import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import get.enroll_me.net.NetworkHelper;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import android.os.Bundle;


public class AddClientActivity extends Base_Activity{
	 
	String  uniqueID;
	TextView social;
	EditText contact_name,phone_number,location;
	@Override    
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		 
		uniqueID =	shar.getString("UUID", "1");

		LoadFrame(R.layout.add_client);
		 
		 contact_name = findViewById(R.id.contact_name);
		 phone_number = findViewById(R.id.phone_number);
		 location = findViewById(R.id.location);
		 social = findViewById(R.id.social);
		 findViewById(R.id.see_send).setVisibility(View.GONE);
	//	 name.setText("Клиент :" );
		 main_name.setText("Добавить клиента" );
		 Spinner social_array = findViewById(R.id.social_array);
		String[] obj = getResources().getStringArray(R.array.socialnames);
		List<String> objects = new ArrayList();
		for(String s : obj ) objects.add(s);
		//	 ArrayAdapter<CharSequence> adapter  = ArrayAdapter.createFromResource(this, R.array.socialnames, android.R.layout.simple_spinner_item);
		int[] arr_icon = getResources().getIntArray(R.array.social_icons);
		arr_icon[0] = 0;
		arr_icon[1] = R.drawable.whatsapp;
		arr_icon[2] = R.drawable.icon_viber_message;
		arr_icon[3] = R.drawable.logo_middle;
		arr_icon[4] = R.drawable.com_facebook_button_icon_blue;

		//getResources().getA
		RpinnerAdapter adapter = new RpinnerAdapter(this, R.layout.spinner_item, R.id.text1, objects,
				 arr_icon);
		 social_array.setOnItemSelectedListener(new OnItemSelectedListener() {
			
			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
			
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				
				String[] arr = getResources().getStringArray(R.array.socialnames);
				String soc = arr[(int) id];
				social.setText(soc);
			} 
		});
		 

		 social_array.setAdapter(adapter);
		 findViewById(R.id.check).setOnClickListener(new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
		if(contact_name.getText().toString().length()==0) {
			Toast.makeText(getBaseContext(),"Поле contact name обязательно для заполнения", Toast.LENGTH_LONG).show();
			return;
		}
		if(phone_number.getText().toString().length()==0) {
			Toast.makeText(getBaseContext(),"Поле phone number обязательно для заполнения", Toast.LENGTH_LONG).show();
				return;
			}
		createClient();
		}

		
	});
	}
	private void createClient() {
		String urlbase = shar.getString("base","");
		String socialname = null;
		String locationname = null;
		if(location.getText().length()>0)locationname = location.getText().toString();
		if(social.getText().length()>0)socialname = social.getText().toString();
		NetworkHelper.getInstance(urlbase).getWebService().add_clients(uniqueID, contact_name.getText().toString(),
				phone_number.getText().toString(), socialname ,locationname).enqueue(new Callback<ResponseBody>() {
					
					@Override
					public void onResponse(Call<ResponseBody> arg0, Response<ResponseBody> arg1) {
						
					
						if(arg1.message().equalsIgnoreCase("OK"))
						
							
							finish();
						
					}
					
					@Override
					public void onFailure(Call<ResponseBody> arg0, Throwable arg1) {
						
						
					} 
				});
		
	}

}
