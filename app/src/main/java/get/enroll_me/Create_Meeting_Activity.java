package get.enroll_me;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.json.JSONArray;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;

import get.enroll_me.model.Client;
import get.enroll_me.net.NetworkHelper;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Create_Meeting_Activity extends Base_Activity {
    private Client client;
    private int type;
    TextView meeting, t;
    DatePicker datePicker;

    Date date;
    String time;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.brand_background));
        client = (Client) getIntent().getSerializableExtra("client");
        setContentView(R.layout.meeting_create);
        meeting = findViewById(R.id.meeting);
        datePicker = findViewById(R.id.DatePicker);
        t = findViewById(R.id.t);
        if (android.os.Build.VERSION.SDK_INT > 22)
            try {

                datePicker.setOnDateChangedListener(new DatePicker.OnDateChangedListener() {

                    @Override
                    public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        // TODO Auto-generated method stub
                        //	date.setYear(year);
                        date.setMonth(datePicker.getMonth());
                        date.setDate(datePicker.getDayOfMonth());
                    }
                });
            } catch (Exception e) {
            }
        //*/
        date = new Date();

        type = getIntent().getIntExtra("type", 1);
        if (type == 1) {
            meeting.setText("Записать ");
            meeting.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    final String urlbase = shar.getString("base", "http://192.168.1.2:8888");

                    NetworkHelper.getInstance(urlbase).getWebService().schedule_day(shar.getString("UUID", "UUID"), date.getMonth(),
                            date.getDate()).enqueue(new Callback<ResponseBody>() {

                        @Override
                        public void onResponse(Call<ResponseBody> arg0, Response<ResponseBody> arg1) {
                            // TODO Auto-generated method stub
                            try {
                                date.setHours(0);
                                date.setMinutes(0);
                                date.setSeconds(0);
                                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                NetworkHelper.getInstance(urlbase).getWebService().invite_client(client.id, client.app_id, dateFormat.format(date)).enqueue(new Callback<ResponseBody>() {

                                    @Override
                                    public void onResponse(Call<ResponseBody> arg0, Response<ResponseBody> arg1) {
                                        // TODO Auto-generated method stub
                                        if (arg1.message().equalsIgnoreCase("OK")) {
                                            try {
                                                String s = arg1.body().string();
                                                s = s.replace("\"", "");
                                                int invite = Integer.parseInt(s);
                                                sendclient(invite);

                                            } catch (Exception e) {
                                                // TODO Auto-generated catch block
                                                e.printStackTrace();
                                            }
                                        }

                                    }


                                    @Override
                                    public void onFailure(Call<ResponseBody> arg0, Throwable arg1) {
                                        // TODO Auto-generated method stub
                                        Toast.makeText(getApplicationContext(), "Не удалось сформировать приглашение на запись", 0).show();
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
            });
        } else if (type == 2) {
            meeting.setText("Посмотреть свободное время");
            meeting.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    String urlbase = shar.getString("base", "http://192.168.1.2:8888");

                    NetworkHelper.getInstance(urlbase).getWebService().schedule_day(shar.getString("UUID", "UUID"), date.getMonth(),
                            date.getDate()).enqueue(new Callback<ResponseBody>() {

                        @Override
                        public void onResponse(Call<ResponseBody> arg0, Response<ResponseBody> arg1) {
                            // TODO Auto-generated method stub
                            try {
                                JSONArray arr = new JSONArray(arg1.body().string());

                                ArrayList<String> amm = new ArrayList<>();
                                amm.add("10:00:00");
                                amm.add("11:00:00");
                                amm.add("12:00:00");
                                amm.add("13:00:00");
                                amm.add("14:00:00");
                                amm.add("15:00:00");
                                amm.add("16:00:00");
                                amm.add("17:00:00");
                                amm.add("18:00:00");
                                amm.add("19:00:00");
                                for (int i = 0; i < arr.length(); i++) {
                                    JSONObject ob = arr.getJSONObject(i);
                                    String[] meetupDate = ob.getString("meetupDate").split(" ");
                                    for (int a = 0; a < amm.size(); a++)
                                        if (meetupDate[meetupDate.length - 1].equals(amm.get(a))) {
                                            amm.remove(a);
                                            break;
                                        }
                                }
                                if (amm.size() == 0)
                                    t.setText("       На данный день нет свободных часов для записи");
                                else {
                                    LinearLayout li = findViewById(R.id.h_list);
                                    for (String s : amm) {
                                        ContextThemeWrapper wr = new ContextThemeWrapper(getApplicationContext(), R.style.TextClick);
                                        TextView text = new TextView(wr);
                                        LinearLayout.LayoutParams par = new LinearLayout.LayoutParams(-2, -2);
                                        par.leftMargin = 20;
                                        text.setLayoutParams(par);
                                        text.setText(s);
                                        li.addView(text);
                                        final String str = s;
                                        text.setOnClickListener(new OnClickListener() {

                                            @Override
                                            public void onClick(View v) {
                                                // TODO Auto-generated method stub
                                                time = str;
                                                meeting.setText("Записать " + client.name + " на " + time);
                                                meeting.setOnClickListener(new OnClickListener() {

                                                    @Override
                                                    public void onClick(View v) {
                                                        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
                                                        try {
                                                            Date d = dateFormat.parse(time);
                                                            date.setHours(d.getHours());
                                                            date.setMinutes(d.getMinutes());
                                                            date.setSeconds(d.getSeconds());
                                                        } catch (ParseException e) {
                                                            // TODO Auto-generated catch block
                                                            e.printStackTrace();
                                                        }
                                                        send();

                                                    }


                                                });
                                            }
                                        });
                                    }
                                    //

                                }
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
            });
        }

    }


    private void sendclient(final int id) {

        AlertDialog.Builder dl = new AlertDialog.Builder(Create_Meeting_Activity.this);
        dl.setTitle("С помощью чего отправить уведомление?");
        dl.setPositiveButton("CMC", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                send_sms(id);
                finish();
            }
        });
        dl.setNeutralButton("Соц. сети", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                send_soc(id);
                finish();
            }
        });
        dl.setNegativeButton("Mail", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                composeEmail(id);
                finish();
            }
        });
        dl.show();

    }

    private void send() {
        // TODO Auto-generated method stub
        final String urlbase = shar.getString("base", "http://192.168.1.2:8888");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        NetworkHelper.getInstance(urlbase).getWebService().invite_client(client.id, client.app_id, dateFormat.format(date)).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> arg0, Response<ResponseBody> arg1) {

                try {
                    String s = arg1.body().string();
                    s = s.replace("\"", "");
                    int invite = Integer.parseInt(s);
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    final String dotime = dateFormat.format(date);

                    AlertDialog.Builder dl = new AlertDialog.Builder(Create_Meeting_Activity.this);
                    dl.setTitle("С помощью чего отправить уведомление?");
                    dl.setPositiveButton("CMC", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            send_sms(dotime);
                            finish();
                        }
                    });
                    dl.setNeutralButton("Соц. сети", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // TODO Auto-generated method stub
                            send_soc(dotime);
                            finish();
                        }
                    });
                    dl.setNegativeButton("Mail", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // TODO Auto-generated method stub
                            composeEmail(dotime);
                            finish();
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
                Toast.makeText(getApplicationContext(), "Не удалось сформировать приглашение на запись", 0).show();
            }
        });

    }

    private void send_sms(int id) {
        Uri uri = Uri.parse("smsto:" + client.phone);
        Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
        intent.putExtra(Intent.EXTRA_SUBJECT,
                shar.getString("name", "") + " " + shar.getString("last_nameedit", "") +
                        " Приглашает Вас записаться на приём");
        intent.putExtra(Intent.EXTRA_TEXT, shar.getString("base", "http://192.168.1.2:8888") + "/api/enroll/invite?invite=" + id);
        startActivity(intent);
    }

    void composeEmail(int id) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:")); // only email apps should handle this
        //  intent.putExtra(Intent.EXTRA_EMAIL, addresses);
        intent.putExtra(Intent.EXTRA_SUBJECT,
                shar.getString("name", "") + " " + shar.getString("last_nameedit", "") +
                        " Приглашает Вас записаться на приём");
        intent.putExtra(Intent.EXTRA_TEXT, shar.getString("base", "http://192.168.1.2:8888") + "/api/enroll/invite?invite=" + id);

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    private void send_soc(int id) {
        Intent sendIntent = new Intent(Intent.ACTION_SEND);
        // intent.putExtra(Intent.EXTRA_TITLE,
        //		 shar.getString("name", "") +" " + shar.getString("last_nameedit", "")+
        //			" Приглашает Вас записаться на приём");
        sendIntent.putExtra(Intent.EXTRA_TEXT, shar.getString("base", "http://192.168.1.2:8888") + "/api/enroll/invite?invite=" + id);

        sendIntent.setType("text/plain");
        Intent shareIntent = Intent.createChooser(sendIntent, null);
        startActivity(shareIntent);
    }

    private void send_soc(String time) {
        Intent sendIntent = new Intent(Intent.ACTION_SEND);
        // intent.putExtra(Intent.EXTRA_TITLE,
        //		 shar.getString("name", "") +" " + shar.getString("last_nameedit", "")+
        //			" Приглашает Вас записаться на приём");
        sendIntent.putExtra(Intent.EXTRA_TEXT,
                shar.getString("client.name", "") + " " + shar.getString("last_nameedit", "") +
                        client.name + ", рады будем видеть Вас на приёме: " + time
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
                        " Записатл Вас  на приём");
        intent.putExtra(Intent.EXTRA_TEXT, "Время записи " + time);

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    private void send_sms(String time) {
        Uri uri = Uri.parse("smsto:" + client.phone);
        Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
        intent.putExtra(Intent.EXTRA_SUBJECT,
                shar.getString("name", "") + " " + shar.getString("last_nameedit", "") +
                        " Записатл Вас  на приём");
        intent.putExtra(Intent.EXTRA_TEXT, "Время записи " + time);
        startActivity(intent);
    }
}
