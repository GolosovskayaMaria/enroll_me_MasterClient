package get.enroll_me.adapters;


import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.Adapter;
import get.enroll_me.R;
import get.enroll_me.model.Client;
public class ClientAdapter  extends Adapter<ClientAdapter.ClientAdapterViewHolder> {
	List<Client> list;
	public interface OnClick{
		void onClick(Client r , View v);
	}
	OnClick clic;
	public void setOnClickListener(OnClick r) {
		clic = r;
	}
	public ClientAdapter( List<Client> list) {
		super();
		this.list = list;
	}
	
	@Override
	public ClientAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		 LayoutInflater inflater = LayoutInflater.from(parent.getContext());
	        View view = inflater.inflate(R.layout.item_client, parent, false);
	        return new ClientAdapterViewHolder(view);
	}
	
	class ClientAdapterViewHolder extends RecyclerView.ViewHolder{
		TextView name,phone; View root ; ImageView del;
		public ClientAdapterViewHolder(View itemView) {
			super(itemView);
			root=itemView;
			name =	root.findViewById(R.id.name);
			phone = root.findViewById(R.id.phone);
			del = root.findViewById(R.id.icon_del);
		}
	
	}
	public static void seeIcon(Client client, ImageView del) {

			String SocialMedia = client.socilaMedia;
			if( SocialMedia  !=null){
				del.setVisibility(View.VISIBLE);
				switch (SocialMedia){
					case "WhatsApp":
del.setImageResource(R.drawable.whatsapp);
					break;
					case "Viber":
						del.setImageResource(R.drawable.icon_viber_message);
						break;
					case "Telegram":
						del.setImageResource(R.drawable.logo_middle);
						break;
					case "Facebook":
						del.setImageResource(R.drawable.com_facebook_button_icon_blue);
						break;
				}
			}
			else{
				del.setVisibility(View.GONE);
			}

	}
	  

	@Override
	public void onBindViewHolder(ClientAdapterViewHolder holder, int position) {
		// TODO Auto-generated method stub
		final Client item = list.get(position);
		 holder.name.setText(item.name); 
	//	holder.id.setText(""+item.id);
		holder.phone.setText(item.phone);
		seeIcon(item , holder.del);
		 holder.root.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if(clic != null) clic.onClick(item ,v);
				}
			});
	}

	@Override
	public int getItemCount() {
		
		return list.size();
	}
}
