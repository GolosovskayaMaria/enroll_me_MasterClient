package get.enroll_me.fragments;

import java.io.IOException;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import get.enroll_me.AddClientActivity;
import get.enroll_me.ClientActivity;
import get.enroll_me.MainActivity;
import get.enroll_me.MeetingsActivity;
import get.enroll_me.R;
import get.enroll_me.adapters.ClientAdapter;
import get.enroll_me.adapters.ClientAdapter.OnClick;
import get.enroll_me.model.Client;
import get.enroll_me.net.NetworkHelper;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Start_Fragment extends Fragment{
	EditText search;
//	SwipeRefreshLayout swiper;
	TextView main_name;
	private SharedPreferences shar;
	private RecyclerView recycler;
	private ArrayList<Client> list; 
	 
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		shar = ((MainActivity)getActivity()).shar;
	return inflater.inflate(R.layout.client_list, container, false);	
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		main_name=view.findViewById(R.id.main_name);
		if(shar.getString("name", null) != null)
		main_name.setText(shar.getString("last_nameedit", ""));
		recycler = view.findViewById(R.id.recycler);
        recycler.setHasFixedSize(true);
        recycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        DividerItemDecoration itemDecorator = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);;
          recycler.addItemDecoration(itemDecorator);
		search = view.findViewById(R.id.Search);
		search.addTextChangedListener(new TextWatcher() {
				
				@Override
				public void onTextChanged(CharSequence s, int start, int before, int count) {
					reset(s.toString());
				}
				
			

				@Override
				public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				}
				
				@Override
				public void afterTextChanged(Editable s) {
				}
			});
		
		   view.findViewById(R.id.add).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent =new Intent(getActivity(), AddClientActivity.class);
				startActivityForResult(intent, 402);
			}
		});
		   view.findViewById(R.id.see_send).setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					String urlbase = shar.getString("base","http://192.168.1.2:8888");
					
					NetworkHelper.getInstance(urlbase).getWebService().schedule_clients(shar.getString("UUID", "1")).enqueue(new Callback<ResponseBody>() {
						
						@Override
						public void onResponse(Call<ResponseBody> arg0, Response<ResponseBody> arg1) {
							try {
								JSONArray arr=new JSONArray(arg1.body().string());
								Intent intent = new Intent(getActivity(), MeetingsActivity.class);
								intent.putExtra("schedule_clients", arr.toString());
								startActivity(intent);
							Log.i("schedule", "response from server" + arr);
							} catch (Exception e) {
								Log.e("enroll_me", "Exception: " + Log.getStackTraceString(e));
							}
						
						}
						
						@Override
						public void onFailure(Call<ResponseBody> arg0, Throwable arg1) {
							Toast.makeText(getActivity(), "Произошла ошибка", 0).show();
						}
					});	
				}
			});	
		   reset("");
	}
	
	private void reset(String string) {
		try {
			 list =new ArrayList<Client>();
			
			JSONArray arr = new JSONArray(shar.getString("list", "[]"));
			for(int i=0;i< arr.length();i++) {
				JSONObject ob = arr.getJSONObject(i);
				Client item = new Client(); 
				item.id = ob.getInt("id") ;
				item.name  = ob.getString("name") ;
				item.phone =  ob.getString("phone") ;
				item.app_id =  ob.getString("app_id") ;
				item.socilaMedia = getsocilaMedia(ob);
				item.location = getlocation(ob);
				if(string.length() == 0)
					list.add(item);
				else if(item.name.toLowerCase().contains(string.toLowerCase()))	
					list.add(item);
			}
			ClientAdapter adapter = new ClientAdapter( list);	
			recycler.setAdapter(adapter);
			adapter.setOnClickListener(new OnClick() {
				
				@Override
				public void onClick(Client client, View v) {
					Intent intent =new Intent(getActivity(), ClientActivity.class);	
					intent.putExtra("client", client);
					startActivityForResult(intent, 402);
				}
			});
		} catch (Exception e) {
			Log.e("enroll_me", "Exception: " + Log.getStackTraceString(e));
		}
	}

	private String getlocation(JSONObject ob) {
		try {
			return	ob.getString("location");
			} catch (JSONException e) {
				return null;
			}
	}

	private String getsocilaMedia(JSONObject ob) {
	try {
	return	ob.getString("socilaMedia");
	} catch (JSONException e) {
		
		return null;
	}
		
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		super.onActivityResult(requestCode, resultCode, data);
		String urlbase = shar.getString("base","");
		NetworkHelper.getInstance(urlbase).getWebService().get_clients(shar.getString("UUID", "1")).enqueue(new Callback<ResponseBody>() {
			
			@Override
			public void onResponse(Call<ResponseBody> arg0, Response<ResponseBody> arg1) {
				
				if(arg1.message().equalsIgnoreCase("OK")) {
					try {
						shar.edit().putString("list", arg1.body().string()).commit();
						
					} catch (IOException e) {
						
						Log.e("enroll_me", "Exception: " + Log.getStackTraceString(e));
					}
					reset("");
				}
				
			}
			
			@Override
			public void onFailure(Call<ResponseBody> arg0, Throwable arg1) {
				
		
			}
		});
		
	}
	
}
