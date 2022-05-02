package get.enroll_me.fragments;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener;
import get.enroll_me.MainActivity;
import get.enroll_me.R;

import get.enroll_me.net.NetworkHelper;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Find_Server_Fragment extends Fragment{
	TextView text;
	SwipeRefreshLayout swiper;
	private SharedPreferences shar;
	int next;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	return inflater.inflate(R.layout.find_server, container, false);	
	}
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onViewCreated(view, savedInstanceState);
		shar = ((MainActivity)getActivity()).shar;
		next= shar.getInt("shar", 2);
		text = view.findViewById(R.id.text_find);
		swiper = view.findViewById(R.id.swiperefresh);
		swiper.setOnRefreshListener(new OnRefreshListener() {
			
			@Override
			public void onRefresh() {
				// TODO Auto-generated method stub
				next=2;
				swiper.setRefreshing(false);
			}
		});
		try {
			searchAddress();
		} catch (Exception e) {
			e.printStackTrace();
			Toast.makeText(getContext(), "Innet Err", 0).show();
		}
	}
	
	void searchAddress()throws Exception {
		
		String[] BaseAdr = getIpAddress().replace(".", "#").split("#");
		final String urlbase="http://"+BaseAdr[0]+"."+BaseAdr[1]+"."+BaseAdr[2]+"." + next+":8888";
		new Handler(Looper.getMainLooper()).post(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				text.setText("ping " + urlbase);	
				
			}
		});
		
	
		NetworkHelper.getInstance(urlbase).getWebService().get_clients(shar.getString("UUID", "1")).enqueue(new Callback<ResponseBody>() {
			
			@Override
			public void onResponse(Call<ResponseBody> arg0, Response<ResponseBody> arg1) {
				// TODO Auto-generated method stub
				if(arg1.message().equalsIgnoreCase("OK")) {
					shar.edit().putInt("shar", next).commit();
					shar.edit().putString("base",urlbase).commit();
					try {
						shar.edit().putString("list", arg1.body().string()).commit();
						
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					((MainActivity)getActivity()).Normal();
				}
				
			}
			
			@Override
			public void onFailure(Call<ResponseBody> arg0, Throwable arg1) {
				// TODO Auto-generated method stub
				next++;
				if(next < 256)
				try {
					searchAddress();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				else {
					new Handler(Looper.getMainLooper()).post(new Runnable() {
						
						@Override
						public void run() {
							// TODO Auto-generated method stub
							text.setText("Not Connect");	
						}
					});
				}
			}
		});
		;
	}
	
	
	public static String getIpAddress() {
        String ip = "";
        try {
           Enumeration<NetworkInterface> enumNetworkInterfaces = NetworkInterface
                 .getNetworkInterfaces();
           while (enumNetworkInterfaces.hasMoreElements()) {
              NetworkInterface networkInterface = enumNetworkInterfaces
                    .nextElement();
              Enumeration<InetAddress> enumInetAddress = networkInterface
                    .getInetAddresses();
              while (enumInetAddress.hasMoreElements()) {
                 InetAddress inetAddress = enumInetAddress
                       .nextElement();
   
                 if (inetAddress.isSiteLocalAddress()) {
                    ip = inetAddress.getHostAddress();
                 }
              }
           }
   
        } catch (SocketException e) {
           // TODO Auto-generated catch block
       
        }
        return ip;
     }
}
