package get.enroll_me;

import java.io.IOException;

import android.provider.Settings.System;


import java.util.UUID;


import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import get.enroll_me.fragments.Find_Server_Fragment;
import get.enroll_me.fragments.Start_Fragment;
import get.enroll_me.net.NetworkHelper;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {


    EditText nameedit, last_nameedit;
    public SharedPreferences shar;
    public static MainActivity main;
    FragmentManager fm;
    @SuppressLint("NewApi")
    Find_Server_Fragment find_Server_Fragment;
    String uniqueID;
    TextView main_name;
    Start_Fragment start_Fragment;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            getWindow().setStatusBarColor(ContextCompat.getColor(this, android.R.color.white));
        main = this;
        setContentView(R.layout.activity_main);
        shar = getSharedPreferences("name", 0);
        uniqueID = shar.getString("UUID", null);
        if (uniqueID == null) {

            uniqueID = UUID.nameUUIDFromBytes(getDeviceIMEI().getBytes()).toString();
            shar.edit().putString("UUID", uniqueID).commit();
        }

        final String meeting = getIntent().getStringExtra("meeting");
        if (meeting != null) {
            View push = findViewById(R.id.push);
            push.setVisibility(View.VISIBLE);
            TextView title = push.findViewById(R.id.title);
            TextView body = push.findViewById(R.id.body);
            title.setText("" + getIntent().getStringExtra("title"));
            body.setText("Посмотреть");
            push.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    v.setVisibility(View.GONE);
                    showDFialog();
                }
            });

        }


        String name = shar.getString("name", null);
        nameedit = findViewById(R.id.name);
        last_nameedit = findViewById(R.id.last_name);
        fm = getSupportFragmentManager();
        if (name == null) {
            nameedit.setVisibility(View.VISIBLE);
            last_nameedit.setVisibility(View.VISIBLE);
            findViewById(R.id.check).setVisibility(View.VISIBLE);
            findViewById(R.id.check).setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    String n = nameedit.getText().toString();
                    if (n.length() > 0) {
                        shar.edit().putString("name", n).commit();
                        shar.edit().putString("last_nameedit", last_nameedit.getText().toString()).commit();
                        toStart();
                    } else
                        Toast.makeText(getApplicationContext(), "Введите свое имя", Toast.LENGTH_LONG).show();
                }
            });

        } else toStart();
        main_name = findViewById(R.id.main_name);
        if (shar.getString("name", null) != null)
            main_name.setText("Здравствуйте, мастер " + shar.getString("name", "") + " UUID " + uniqueID.substring(0, 4).toUpperCase());
        //*/
    }

    private void showDFialog() {
        // TODO Auto-generated method stub
        NetworkHelper.getInstance(shar.getString("base", "http://192.168.1.2:8888")).getWebService().schedule_clients(uniqueID).enqueue(new Callback<ResponseBody>() {

            @Override
            public void onResponse(Call<ResponseBody> arg0, Response<ResponseBody> arg1) {

                try {
                    AlertDialog.Builder dl = new AlertDialog.Builder(MainActivity.this);
                    Log.i("push", "new push notification: " + getIntent().getExtras().toString());
                    dl.setTitle("Запись " + getIntent().getStringExtra("user_name"));
                    dl.setMessage("Была сделана запись на " + getIntent().getStringExtra("createDate"));
                    dl.setPositiveButton("OK", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });

                    dl.show();

                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> arg0, Throwable arg1) {
                // TODO Auto-generated method stub
                Toast.makeText(getApplicationContext(), "Не удалось удалить клиента", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void toStart() {

        String urlbase = shar.getString("base", "http://192.168.1.2:8888");
        NetworkHelper.getInstance(urlbase).getWebService().get_clients(uniqueID).enqueue(new Callback<ResponseBody>() {

            @Override
            public void onResponse(Call<ResponseBody> arg0, Response<ResponseBody> arg1) {
                // TODO Auto-generated method stub
                if (arg1.message().equalsIgnoreCase("OK")) {
                    try {
                        shar.edit().putString("list", arg1.body().string()).commit();

                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    Normal();
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> arg0, Throwable arg1) {
                // TODO Auto-generated method stub
                nameedit.setVisibility(View.INVISIBLE);
                last_nameedit.setVisibility(View.INVISIBLE);
                main_name.setText("Здравствуйте, мастер " + shar.getString("name", "") + " UUID " + uniqueID.substring(0, 4).toUpperCase());
                TextView ok = findViewById(R.id.check);
                ok.setVisibility(View.VISIBLE);
                ok.setText("Начать поиск");
                ok.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        find_Server_Fragment = new Find_Server_Fragment();
                        FragmentTransaction ft = fm.beginTransaction();
                        ft.replace(R.id.start, find_Server_Fragment);
                        ft.commit();
                    }
                });
            }
        });


    }

    public void Normal() {

        start_Fragment = new Start_Fragment();
        FragmentTransaction ft = fm.beginTransaction();

        if (find_Server_Fragment != null)
            ft.remove(find_Server_Fragment);
        ft.replace(R.id.start, start_Fragment);
        //}
        ft.commit();

    }

    public String getDeviceIMEI() {

        return System.getString(this.getContentResolver(), Secure.ANDROID_ID);
    }

    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
        main = null;
    }


}
