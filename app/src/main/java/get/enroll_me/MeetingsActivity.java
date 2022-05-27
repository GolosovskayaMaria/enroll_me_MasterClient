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
    JSONArray meetingsList;
    private SharedPreferences shar;
    private RecyclerView recycler;
    private JSONArray clientList;
    final long limit = 3600000 * 24;
    private boolean all = true;
    DatePicker datePicker;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.brand_background));
        setContentView(R.layout.client_list);
        shar = getSharedPreferences("name", 0);


        TextView main_name = findViewById(R.id.main_name);
        findViewById(R.id.Search).setVisibility(View.GONE);
        ImageView im = findViewById(R.id.add);
        im.setImageResource(R.drawable.ic_perm_group_calendar);
        im.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                
                datePicker.setVisibility(View.VISIBLE);
            }
        });
        //findViewById(R.id.see_send).setVisibility(View.GONE);
        recycler = findViewById(R.id.recycler);
        recycler.setHasFixedSize(true);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        DividerItemDecoration itemDecorator = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        ;
        recycler.addItemDecoration(itemDecorator);
        selectAll();
        findViewById(R.id.see_send).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                
                selectAll();
            }
        });
        if (shar.getString("name", null) != null)
            //main_name.setText("Здравствуйте, мастер "+shar.getString("name", "") +" " + shar.getString("last_nameedit", ""));
            main_name.setText("Журнал записей");

        datePicker = findViewById(R.id.pada_picker);
        if (android.os.Build.VERSION.SDK_INT > 22)
            try {

                datePicker.setOnDateChangedListener(new DatePicker.OnDateChangedListener() {

                    @Override
                    public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        
                        //	date.setYear(year);
                        int getMonth = datePicker.getMonth();
                        int getDate = datePicker.getDayOfMonth();
                        // Log.e("dddddd",""  + getMonth +" " + getDate);
                        view.setVisibility(View.GONE);
                        loadnewDate(getMonth, getDate);
                    }
                });
            } catch (Exception e) {
            }
        else
            datePicker.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    
                    int getMonth = datePicker.getMonth();
                    int getDate = datePicker.getDayOfMonth();

                    v.setVisibility(View.GONE);
                    loadnewDate(getMonth, getDate);
                }

            });

    }

    private void loadnewDate(int getMonth, int day) {
        
        String urlbase = shar.getString("base", "http://192.168.1.2:8888");
        NetworkHelper.getInstance(urlbase).getWebService().schedule_day(shar.getString("UUID", "1"), getMonth, day).enqueue(new Callback<ResponseBody>() {

            @Override
            public void onResponse(Call<ResponseBody> arg0, Response<ResponseBody> arg1) {

                try {
                    meetingsList = new JSONArray(arg1.body().string());
                    cleanUnconfirmedRecords();
                    new Handler(Looper.getMainLooper()).post(new Runnable() {

                        @Override
                        public void run() {
                            findViewById(R.id.see_send).setVisibility(View.VISIBLE);
                            load();
                        }
                    });

                } catch (Exception e) {
                    
                    Log.e("enroll_me", "Exception: " + Log.getStackTraceString(e));
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> arg0, Throwable arg1) {
                

            }
        });
    }

    private void selectAll() {
        TextView t = findViewById(R.id.see_send);
        if (all) {
            all = false;
            t.setText("Все записи");

        } else {
            all = true;
            t.setText("Свежие записи");
        }
        rerloadTable(all);
        load();
    }

    private void rerloadTable(boolean all) {
        try {
            if (meetingsList == null)
                meetingsList = new JSONArray(getIntent().getStringExtra("schedule_clients"));
            cleanUnconfirmedRecords();
            if (!all) // all это флажок Свежие записи или Все записи
                cleanOlder(); // TODO не работает. Чистятся все записи
            clientList = new JSONArray(shar.getString("list", "[]"));
        } catch (Exception e) {
            
            Log.e("enroll_me", "Exception: " + Log.getStackTraceString(e));
            finish();
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private JSONArray cleanOlder() throws Exception {
        try {
            Date d = new Date();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            for (int i = meetingsList.length() - 1; i > -1; i--) {
                JSONObject ob = meetingsList.getJSONObject(i);
                Date date = dateFormat.parse(ob.getString("meetupDate"));
                if (d.getTime() - date.getTime() > limit) {
                    meetingsList.remove(i);
                    return cleanOlder();
                }
            }
            return meetingsList;
        } catch (Exception e) {
            Log.e("schedule response failed", "" + e.toString());
            Log.e("enroll_me", "Exception: " + Log.getStackTraceString(e));
            return meetingsList;
        }

    }

    // клиент получил приглашение но еще не записался, удалим эти записи из расписания
    @SuppressLint("NewApi")
    private JSONArray cleanUnconfirmedRecords() throws JSONException {
        for (int i = meetingsList.length() - 1; i > -1; i--) {
            JSONObject ob = meetingsList.getJSONObject(i);
            if (ob.getString("meetupDate").endsWith("00:00:00")) {
                meetingsList.remove(i);
                return cleanUnconfirmedRecords();
            }
        }
        return meetingsList;
    }

    private void load() {
        MeetingAdapter adapter = new MeetingAdapter(meetingsList, clientList);
        recycler.setAdapter(adapter);
        adapter.setOnClickListener(new MeetingAdapter.OnClick() {

            @Override
            public void onClick(JSONObject obt, int client) {
                for (int i = 0; i < clientList.length(); i++) {
                    try {
                        JSONObject ob = clientList.getJSONObject(i);
                        if (ob.getInt("id") == client) {
                            delete_meeting(ob.getString("name"), obt);
                            break;
                        }
                    } catch (Exception e) {
                        Log.e("enroll_me", "Exception: " + Log.getStackTraceString(e));
                    }
                }
            }
        });
    }

    private void delete_meeting(String name, final JSONObject obt) throws Exception {

        Log.e("delete", "" + obt);
        AlertDialog.Builder dl = new AlertDialog.Builder(MeetingsActivity.this);
        dl.setTitle("Запись " + name);

        String dateStr = obt.getString("meetupDate");
        SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date dateDate = parser.parse(dateStr);
        SimpleDateFormat formatter = new SimpleDateFormat("EEE, d MMM yyyy HH:mm");
        String formattedDate = formatter.format(dateDate);

        dl.setMessage("Уверены, что хотите удалить запись на " + formattedDate + "?");
        dl.setPositiveButton("Да", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
			    String urlbase = shar.getString("base", "http://192.168.1.2:8888");
                try {
                    NetworkHelper.getInstance(urlbase).getWebService().del_meeting(obt.getInt("id")).enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> arg0, Response<ResponseBody> arg1) {
                            reload();
                            try {
                                send_message(obt.getString("meetupDate"));
                            } catch (JSONException e) {
                                Log.e("enroll_me", "Exception: " + Log.getStackTraceString(e));
                            }
                        }


                        @Override
                        public void onFailure(Call<ResponseBody> arg0, Throwable arg1) {
                            
                            Toast.makeText(getApplicationContext(), "Не удалось удалить запись", Toast.LENGTH_LONG).show();
                        }
                    });
                } catch (JSONException e) {
                    
                    Log.e("enroll_me", "Exception: " + Log.getStackTraceString(e));
                }

            }

        });
        dl.setNegativeButton("Нет", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                

            }
        });
        dl.show();
    }

    private void reload() {
        
        String urlbase = shar.getString("base", "http://192.168.1.2:8888");
        NetworkHelper.getInstance(urlbase).getWebService().schedule_clients(shar.getString("UUID", "1")).enqueue(new Callback<ResponseBody>() {

            @Override
            public void onResponse(Call<ResponseBody> arg0, Response<ResponseBody> arg1) {
                
                try {
                    meetingsList = new JSONArray(arg1.body().string());
                    Log.d("schedule", "response " + meetingsList);
                    cleanUnconfirmedRecords();
                    new Handler(Looper.getMainLooper()).post(new Runnable() {

                        @Override
                        public void run() {
                            
                            load();
                        }
                    });
                } catch (Exception e) {
                    
                    Log.e("enroll_me", "Exception: " + Log.getStackTraceString(e));
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> arg0, Throwable arg1) {
                

            }
        });
    }

    void send_message(String time) {
        AlertDialog.Builder dl = new AlertDialog.Builder(this);
        dl.setTitle("С помощью чего отправить уведомление?");
        dl.setPositiveButton("CMC", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                
                send_sms(time);

            }
        });
        dl.setNeutralButton("Соц. сети", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                
                send_soc(time);

            }
        });
        dl.setNegativeButton("Mail", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                
                composeEmail(time);

            }
        });
        dl.show();
    }

    private void send_soc(String time) {
        Intent sendIntent = new Intent(Intent.ACTION_SEND);
        // intent.putExtra(Intent.EXTRA_TITLE,
        //		 shar.getString("name", "") +" " + shar.getString("last_nameedit", "")+
        //			" Приглашает Вас записаться на приём");
        sendIntent.putExtra(Intent.EXTRA_TEXT,
                shar.getString("name", "") + " " + shar.getString("last_nameedit", "") +
                        ", удалила Вашу запись на приём " + time
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
                shar.getString("name", "") + " " + shar.getString("last_nameedit", "") +
                        ", удалила Вашу запись на приём ");
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
                shar.getString("name", "") + " " + shar.getString("last_nameedit", "") +
                        ", удалила Вашу запись на приём ");
        intent.putExtra(Intent.EXTRA_TEXT, "Время записи " + time);
        startActivity(intent);
    }
}
