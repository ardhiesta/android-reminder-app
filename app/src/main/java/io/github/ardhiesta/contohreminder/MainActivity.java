package io.github.ardhiesta.contohreminder;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.support.v4.app.DialogFragment;
import android.support.v4.widget.TextViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

// class MainActivity untuk tampilan program membuat notifikasi terjadwal
public class MainActivity extends AppCompatActivity {
    // TextView untuk menampikan tanggal dan waktu yangdipilih oleh user
    static TextView tvDate, tvTime;

    // EditText untuk memasukkan pesan yang akan tampil di notifikasi
    EditText etPesan;

    // NotificationManager untuk membuat notifikasi
    NotificationManager mNotificationManager;

    // konstanta ACTION_NOTIFY untuk memanggil AlarmReceiver melalui manifest
    private static final String ACTION_NOTIFY =
            "io.github.ardhiesta.contohreminder.ACTION_NOTIFY";

    // AlarmManager untuk menjadwalkan notifikasi
    AlarmManager alarmManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // binding komponen UI ke code
        tvDate = (TextView) findViewById(R.id.tvDate);
        tvTime = (TextView) findViewById(R.id.tvTime);
        etPesan = (EditText) findViewById(R.id.etReminder);

        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
    }

    public void setReminder(View view) throws ParseException {
        // format peulisan tanggal dan waktu untuk mengambil tanggal dan waktu dari TextView
        // dan mengubahnya ke tipe data Calendar
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        Calendar cal = Calendar.getInstance();
        try {
            // mengambil tanggal dan waktu dari TextView
            // diubah ke Calendar
            cal.setTime(df.parse(tvDate.getText().toString()+" "+tvTime.getText().toString()));

            // membuat Intent, memanggil AlarmReceiver
            Intent notifyIntent = new Intent(ACTION_NOTIFY);

            // mengirim data ke Intent berupa teks yang diketik di EditText
            notifyIntent.putExtra("pesan", etPesan.getText().toString());

            // membuat PendingIntent dari Intent yang dideklarasikan sebelumnya
            PendingIntent notifyPendingIntent = PendingIntent.getBroadcast
                    (this, (int) System.currentTimeMillis(), notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            // menjadwalkan pemanggilan PendingIntent
            // hasilnya berupa notifikasi yang akan muncul pada waktu yang dipilih
            alarmManager.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), notifyPendingIntent);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    // setDate dipanggil di property onClick tombol untuk memilih tanggal
    // untuk menampilkan date picker dialog
    public void setDate(View view){
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    // setTime dipanggil di property onClick tombol untuk memilih waktu
    // untuk menampilkan time picker dialog
    public void setTime(View view){
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }

    // membuat time picker dialog
    public static class TimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(),
                    this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            // menampilkan waktu yang dipilih ke TextView tvTime

            // jika waktu < 10, tambahkan 0 di depan
            if (minute < 10){
                tvTime.setText(String.valueOf(hourOfDay)
                        +":0"+String.valueOf(minute));
            } else {
                tvTime.setText(String.valueOf(hourOfDay)
                        +":"+String.valueOf(minute));
            }

        }
    }

    // membuat date picker dialog
    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            // menampilkan tanggal yang dipilih ke TextView tvDate
            tvDate.setText(String.valueOf(day)+"-"
                    +String.valueOf(month+1)
                    +"-"+String.valueOf(year));
        }
    }
}
