package get.shoplist.fragments;

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
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener;
import get.shoplist.AddClientActivity;
import get.shoplist.ClientActivity;
import get.shoplist.MainActivity;
import get.shoplist.MeetingsActivity;
import get.shoplist.R;
import get.shoplist.adapters.ClientAdapter;
import get.shoplist.adapters.ClientAdapter.OnClick;
import get.shoplist.model.Client;
import get.shoplist.net.NetworkHelper;
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
		// TODO Auto-generated method stub
		super.onViewCreated(view, savedInstanceState);
		main_name=view.findViewById(R.id.main_name);
		if(shar.getString("name", null) != null)
		main_name.setText("Ваше имя: "+shar.getString("name", "") +" " + shar.getString("last_nameedit", ""));
		recycler = view.findViewById(R.id.recycler);
        recycler.setHasFixedSize(true);
        recycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        DividerItemDecoration itemDecorator = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);;
          recycler.addItemDecoration(itemDecorator);
		search = view.findViewById(R.id.Search);
		search.addTextChangedListener(new TextWatcher() {
				
				@Override
				public void onTextChanged(CharSequence s, int start, int before, int count) {
					// TODO Auto-generated method stub
					reset(s.toString());
				}
				
			

				@Override
				public void beforeTextChanged(CharSequence s, int start, int count, int after) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void afterTextChanged(Editable s) {
					// TODO Auto-generated method stub
					
				}
			});
		
		   view.findViewById(R.id.add).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
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
							// TODO Auto-generated method stub
							try {
								JSONArray arr=new JSONArray(arg1.body().string());
								Intent intent = new Intent(getActivity(), MeetingsActivity.class);
								intent.putExtra("schedule_clients", arr.toString());
								startActivity(intent);
							Log.e("sssssss", "" + arr);
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						
						}
						
						@Override
						public void onFailure(Call<ResponseBody> arg0, Throwable arg1) {
							// TODO Auto-generated method stub
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private String getlocation(JSONObject ob) {
		// TODO Auto-generated method stub
		try {
			return	ob.getString("location");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				return null;
			}
	}

	private String getsocilaMedia(JSONObject ob) {
	try {
	return	ob.getString("socilaMedia");
	} catch (JSONException e) {
		// TODO Auto-generated catch block
		return null;
	}
		
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		String urlbase = shar.getString("base","");
		NetworkHelper.getInstance(urlbase).getWebService().get_clients(shar.getString("UUID", "1")).enqueue(new Callback<ResponseBody>() {
			
			@Override
			public void onResponse(Call<ResponseBody> arg0, Response<ResponseBody> arg1) {
				// TODO Auto-generated method stub
				if(arg1.message().equalsIgnoreCase("OK")) {
					try {
						shar.edit().putString("list", arg1.body().string()).commit();
						
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					reset("");
				}
				
			}
			
			@Override
			public void onFailure(Call<ResponseBody> arg0, Throwable arg1) {
				// TODO Auto-generated method stub
		
			}
		});
		
	}
	
}
