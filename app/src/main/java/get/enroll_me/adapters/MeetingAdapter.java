package get.enroll_me.adapters;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.Adapter;
import get.enroll_me.R;

public class MeetingAdapter extends Adapter<MeetingAdapter.MeetingAdapterAdapterViewHolder> {
	JSONArray list;
	private JSONArray clientlist;
	 final long limit = 3600000*24;
	public interface OnClick{
		void onClick(JSONObject ob,int client);
	}
	OnClick clic;
	public void setOnClickListener(OnClick r) {
		clic = r;
	}
	public MeetingAdapter(JSONArray list , JSONArray clientlist) {
		super();
		this.list = list;
		this.clientlist = clientlist;
	}
	
	@Override
	public MeetingAdapterAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		 LayoutInflater inflater = LayoutInflater.from(parent.getContext());
	        View view = inflater.inflate(R.layout.item_client, parent, false);
	        return new MeetingAdapterAdapterViewHolder(view);
	}
	

	class MeetingAdapterAdapterViewHolder extends RecyclerView.ViewHolder{
		TextView name,phone; View root; ImageView icon;
		public MeetingAdapterAdapterViewHolder(View itemView) {
			super(itemView);
			root=itemView;
			icon =	root.findViewById(R.id.icon);
			name =	root.findViewById(R.id.name);
			phone = root.findViewById(R.id.phone);
			root.findViewById(R.id.icon_del).setVisibility(View.VISIBLE);
		}
	
	}
 
	  

	@Override
	public void onBindViewHolder(MeetingAdapterAdapterViewHolder holder, int position) {
		// TODO Auto-generated method stub
		final JSONObject  item = list.optJSONObject(list.length() -1 -position);
		holder.icon.setImageResource(R.drawable.widget_pin);
		try {
			String createDate = item.getString("meetupDate");
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			try {
				Date date = dateFormat.parse(createDate);
				Date d =new Date();
				if(d.getTime() - date.getTime() > limit ) {
					holder.root.setBackgroundColor(0x70707070);
				}
				else holder.root.setBackgroundColor(0xffffffff);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			holder.phone.setText(createDate);
			int userId = item.getInt("userId");
			
			for(int i=0;i<clientlist.length();i++) {
				JSONObject client = clientlist.optJSONObject(i);
				if(client.getInt("id") == userId ) {
					String name = client.getString("name");
					holder.name.setText(name); 
					break;
				}
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		 holder.root.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					
					try {
						int userId = item.getInt("userId");
					if(clic != null) clic.onClick(item  ,  userId );
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
			});
	}



	@Override
	public int getItemCount() {
		
		return list.length();
	}
}
