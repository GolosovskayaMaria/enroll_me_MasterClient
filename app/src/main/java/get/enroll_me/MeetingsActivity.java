package get.enroll_me;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import get.enroll_me.adapters.MeetingAdapter;
import get.enroll_me.net.NetworkHelper;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MeetingsActivity extends Activity {
	JSONArray arr;
	private SharedPreferences shar;
	private RecyclerView recycler;
	private JSONArray clientlist; 
	 final long limit = 3600000*24;
	private boolean all= true;
	DatePicker datePicker;
	@SuppressLint("NewApi") 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.brand_background));
		setContentView(R.layout.client_list);
		shar =	getSharedPreferences("name", 0);
 
		
	TextView	main_name= findViewById(R.id.main_name);  
	findViewById(R.id.Search).setVisibility(View.GONE);
	ImageView  im =findViewById(R.id.add);
	im.setImageResource(R.drawable.ic_perm_group_calendar);
	im.setOnClickListener(new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			datePicker.setVisibility(View.VISIBLE);
		}
	});
	//findViewById(R.id.see_send).setVisibility(View.GONE); 
	recycler = findViewById(R.id.recycler);
    recycler.setHasFixedSize(true);
    recycler.setLayoutManager(new LinearLayoutManager(this));
    DividerItemDecoration itemDecorator = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);;
      recycler.addItemDecoration(itemDecorator);
	selectAll();
	findViewById(R.id.see_send).setOnClickListener(new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			selectAll();
		}
	});
		if(shar.getString("name", null) != null)
		//main_name.setText("Ваше имя: "+shar.getString("name", "") +" " + shar.getString("last_nameedit", ""));
			main_name.setText("Список записи");
		
		datePicker = findViewById(R.id.pada_picker);
		if(android.os.Build.VERSION.SDK_INT > 22)
			 try {
				 
				 datePicker.setOnDateChangedListener(new DatePicker.OnDateChangedListener() {

					 @Override
					 public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
						 // TODO Auto-generated method stub
						 //	date.setYear(year);
							int getMonth = datePicker.getMonth();
							 int getDate = datePicker.getDayOfMonth();
							// Log.e("dddddd",""  + getMonth +" " + getDate);
							view.setVisibility(View.GONE);
							loadnewDate(getMonth ,getDate );
					 }
				 });
			 } catch(Exception e){}
		else
		datePicker.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				int getMonth = datePicker.getMonth();
				 int getDate = datePicker.getDayOfMonth();
				
				v.setVisibility(View.GONE);
				loadnewDate(getMonth ,getDate );
			}

		});
		
	}

	private void loadnewDate(int getMonth, int  day) {
		// TODO Auto-generated method stub
		String urlbase = shar.getString("base","http://192.168.1.2:8888");
		NetworkHelper.getInstance(urlbase).getWebService().schedule_day(shar.getString("UUID", "1"), getMonth, day).enqueue(new Callback<ResponseBody>() {
			
			@Override
			public void onResponse(Call<ResponseBody> arg0, Response<ResponseBody> arg1) {
				// TODO Auto-generated method stub
				 try {
				arr = new JSONArray( arg1.body().string());
				cleanarr();
				new Handler(Looper.getMainLooper()).post(new Runnable() {
					
					@Override
					public void run() {
						findViewById(R.id.see_send).setVisibility(View.VISIBLE);
						load();
					}
				});
				
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			@Override
			public void onFailure(Call<ResponseBody> arg0, Throwable arg1) {
				// TODO Auto-generated method stub
				
			}
		});
	}
	private void selectAll() {
		TextView t = findViewById(R.id.see_send);
		if(all) { 
			all=false;
			t.setText("Вывести все записи");
			
		}else {
			all=true;
			t.setText("Вывести свежие записи");
		}
		rerloadTable(all);
		load();
	}

	private void rerloadTable(boolean all) {
		try {
			if(arr == null)
			arr = new JSONArray(getIntent().getStringExtra("schedule_clients"));
cleanarr();
if(!all) cleanoldarr();
			 clientlist = new JSONArray(shar.getString("list", "[]"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			finish();
		}
	}
	@TargetApi(Build.VERSION_CODES.KITKAT)
	private JSONArray cleanoldarr() throws Exception {
		try {
			Date d =new Date();
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			for(int i= arr.length()-1;i>-1;i--) {
				JSONObject ob = arr.getJSONObject(i);
				Date date = dateFormat.parse(ob.getString("meetupDate"));
				if(d.getTime() - date.getTime() > limit ) {
					arr.remove(i);
					return  cleanoldarr();
				}
			}
			return arr;
		} catch (Exception e) {
			Log.e("schedule response failed", "" + e.toString());
			e.printStackTrace();
			return arr;
		}

	}
	@SuppressLint("NewApi")
	private JSONArray cleanarr() throws JSONException {
		for(int i= arr.length()-1;i>-1;i--) {
			JSONObject ob = arr.getJSONObject(i);
			if(ob.getString("meetupDate").endsWith("00:00:00") ) {arr.remove(i);
			return cleanarr();
			}
			
			}
		return arr;
	}
	private void load() {
		// TODO Auto-generated method stub
	      MeetingAdapter adapter = new MeetingAdapter(arr, clientlist);
          recycler.setAdapter(adapter);
          adapter.setOnClickListener(new MeetingAdapter.OnClick() {
			
			@Override
			public void onClick(JSONObject obt,int client) {
			
				for(int i=0;i <clientlist.length();i++) {
					try {
						JSONObject ob = clientlist.getJSONObject(i);
					
						if(ob.getInt("id")==client) {
							
							delete_meeting(ob.getString("name"), obt);
							break;
						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					}
				
			}

		
		});
	}
	private void delete_meeting(String name , final JSONObject obt) throws JSONException {
		// TODO Auto-generated method stub
		Log.e("ssssss", "" +obt);
		AlertDialog.Builder dl = new AlertDialog.Builder(MeetingsActivity.this);
		dl.setTitle("Запись " + name);
		dl.setMessage("Вы действительно хотите удалить запись на " + obt.getString("createDate"));
		dl.setPositiveButton("Да", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub

				String urlbase = shar.getString("base","http://192.168.1.2:8888");
				
			try {
				NetworkHelper.getInstance(urlbase).getWebService().del_meeting(obt.getInt("id")).enqueue(new Callback<ResponseBody>() {
						
						@Override
						public void onResponse(Call<ResponseBody> arg0, Response<ResponseBody> arg1) {
							// TODO Auto-generated method stub
						reload();
							try {
								send_message(obt.getString("createDate"));
							} catch (JSONException e) {
								e.printStackTrace();
							}
						}
						
					

						@Override
						public void onFailure(Call<ResponseBody> arg0, Throwable arg1) {
							// TODO Auto-generated method stub
							Toast.makeText(getApplicationContext(), "Не удалось удалить запись", Toast.LENGTH_LONG).show();
						}
					});
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
				
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

	private void reload() {
		// TODO Auto-generated method stub
		String urlbase = shar.getString("base","http://192.168.1.2:8888");
		NetworkHelper.getInstance(urlbase).getWebService().schedule_clients(shar.getString("UUID", "1")).enqueue(new Callback<ResponseBody>() {
			
			@Override
			public void onResponse(Call<ResponseBody> arg0, Response<ResponseBody> arg1) {
				// TODO Auto-generated method stub
				try {
					arr =new JSONArray(arg1.body().string());
					Log.d("schedule", "response " + arr);
					cleanarr();
					new Handler(Looper.getMainLooper()).post(new Runnable() {
						
						@Override
						public void run() {
							// TODO Auto-generated method stub
							load();
						}
					});
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}	
			}
			
			@Override
			public void onFailure(Call<ResponseBody> arg0, Throwable arg1) {
				// TODO Auto-generated method stub
				
			}
		});
	}
void	send_message(String time ){
		AlertDialog.Builder dl = new AlertDialog.Builder(this);
		dl.setTitle("Отправить уведомление слиенту?");
		dl.setMessage("Выбор носителя");
		dl.setPositiveButton("CMC", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				send_sms(time);

			}
		});
		dl.setNeutralButton("Соц. сети", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				send_soc(time);

			}
		});
		dl.setNegativeButton("Mail", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				composeEmail(time);

			}
		});
		dl.show();
	}
	private void send_soc(String time) {
		Intent sendIntent = new Intent(Intent.ACTION_SEND);
		// intent.putExtra(Intent.EXTRA_TITLE,
		//		 shar.getString("name", "") +" " + shar.getString("last_nameedit", "")+
		//			" Приглашает Вас записаться на прием");
		sendIntent.putExtra(Intent.EXTRA_TEXT,
				shar.getString("name", "") +" " + shar.getString("last_nameedit", "")+
						" Удалил апись  на прием в "+time
		);

		sendIntent.setType("text/plain");
		Intent shareIntent = Intent.createChooser(sendIntent, null);
		startActivity(shareIntent);
	}
	void composeEmail(String time) {
		Intent intent = new Intent(Intent.ACTION_SENDTO);
		intent.setData(Uri.parse("mailto:")); // only email apps should handle this
		//  intent.putExtra(Intent.EXTRA_EMAIL, addresses);
		intent.putExtra(Intent.EXTRA_SUBJECT,
				shar.getString("name", "") +" " + shar.getString("last_nameedit", "")+
						" Удалил апись  на прием");
		intent.putExtra(Intent.EXTRA_TEXT, "Время записи " + time);

		if (intent.resolveActivity(getPackageManager()) != null) {
			startActivity(intent);
		}
	}
	private void send_sms(String time //,Client client
	) {
		Uri uri = Uri.parse("smsto:");
		Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
		intent.putExtra(Intent.EXTRA_SUBJECT,
				shar.getString("name", "") +" " + shar.getString("last_nameedit", "")+
						" Удалил апись  на прием");
		intent.putExtra(Intent.EXTRA_TEXT, "Время записи " + time);
		startActivity(intent);
	}
}
