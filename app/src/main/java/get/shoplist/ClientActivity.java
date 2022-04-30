package get.shoplist;

import java.io.IOException;

import org.json.JSONArray;
import android.annotation.SuppressLint;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import get.shoplist.adapters.ClientAdapter;
import get.shoplist.model.Client;
import get.shoplist.net.NetworkHelper;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ClientActivity extends Base_Activity 
{
	Client client;
 
	//private BottomNavigationView mBottomNavigationView;
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		client=(Client) getIntent().getSerializableExtra("client");

		LoadFrame(R.layout.client);
		TextView name = findViewById(R.id.name);
		name.setText(client.name);
		TextView tel = findViewById(R.id.tel);
		tel.setText(client.phone);
		  
		if(shar.getString("name", null) != null)
			main_name.setText("Страница клиетеа " + client.name);
	//	main_name.setText("Ваше имя: "+shar.getString("name", "") +" " + shar.getString("last_nameedit", ""));
	findViewById(R.id.see_send).setVisibility(View.GONE);
      
		ImageView soc = findViewById(R.id.social_icon);
		ClientAdapter.seeIcon(client,soc);
		findViewById(R.id.create_meeting).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(), Create_Meeting_Activity.class);
				intent.putExtra("client", client);
				intent.putExtra("type", 2);
				startActivity(intent);
			}
		});  
		findViewById(R.id.del_client).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				AlertDialog.Builder dl = new AlertDialog.Builder(ClientActivity.this);
				dl.setTitle("Вниманте!");
				dl.setMessage("Вы действительно хотите удалить " + client.name);
				dl.setPositiveButton("Да", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						String urlbase = shar.getString("base","http://192.168.1.2:8888");
						
						NetworkHelper.getInstance(urlbase).getWebService().del_clients(client.id).enqueue(new Callback<ResponseBody>() {
							
							@Override
							public void onResponse(Call<ResponseBody> arg0, Response<ResponseBody> arg1) {
								// TODO Auto-generated method stub
							finish();
							}
							
							@Override
							public void onFailure(Call<ResponseBody> arg0, Throwable arg1) {
								// TODO Auto-generated method stub
								Toast.makeText(getApplicationContext(), "Не удалось удалить клиента", Toast.LENGTH_SHORT).show();
							}
						}); 
						 
					} 
					 
				});
				dl.setNegativeButton("Нет", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						
					}
				});
				dl.show();
			} 
		});

		
findViewById(R.id.send).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(), Create_Meeting_Activity.class);
				intent.putExtra("client", client);
				intent.putExtra("type", 1);
				startActivity(intent);
		
			}
		});

	}
	
	
	
	


}
