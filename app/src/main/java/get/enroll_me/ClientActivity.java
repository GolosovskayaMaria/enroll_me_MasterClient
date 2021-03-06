package get.enroll_me;

import android.annotation.SuppressLint;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import get.enroll_me.adapters.ClientAdapter;
import get.enroll_me.model.Client;
import get.enroll_me.net.NetworkHelper;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ClientActivity extends Base_Activity {
    Client client;

    //private BottomNavigationView mBottomNavigationView;
    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        
        super.onCreate(savedInstanceState);
        client = (Client) getIntent().getSerializableExtra("client");

        LoadFrame(R.layout.client);
        TextView name = findViewById(R.id.name);
        name.setText(client.name);
        TextView tel = findViewById(R.id.tel);
        tel.setText(client.phone);

        if (shar.getString("name", null) != null)
            main_name.setText("Страница клиента");
        //	main_name.setText("Здравствуйте, мастер "+shar.getString("name", "") +" " + shar.getString("last_nameedit", ""));
        findViewById(R.id.see_send).setVisibility(View.GONE);

        ImageView soc = findViewById(R.id.social_icon);
        ClientAdapter.seeIcon(client, soc);
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
                dl.setTitle("Внимание!");
                dl.setMessage("Вы точно хотите удалить этого клиента?");
                dl.setPositiveButton("Да", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        
                        String urlbase = shar.getString("base", "http://192.168.1.2:8888");

                        NetworkHelper.getInstance(urlbase).getWebService().del_clients(client.id).enqueue(new Callback<ResponseBody>() {

                            @Override
                            public void onResponse(Call<ResponseBody> arg0, Response<ResponseBody> arg1) {
                                
                                finish();
                            }

                            @Override
                            public void onFailure(Call<ResponseBody> arg0, Throwable arg1) {
                                
                                Toast.makeText(getApplicationContext(), "Не удалось удалить клиента", Toast.LENGTH_SHORT).show();
                            }
                        });

                    }

                });
                dl.setNegativeButton("Нет", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        

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
