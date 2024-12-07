package com.example.nasa;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    String api = "ncmg2YDyPL4XUfR0kPEhYqgb2H3sjl1EjCvdRKUT";
    NasaApiService nasaApiService;
    Button downloadBtn, datePickerBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.nasa.gov/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        nasaApiService = retrofit.create(NasaApiService.class);

        downloadBtn = findViewById(R.id.downloadBtn);
        downloadBtn.setOnClickListener(v -> {
            String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
            getApod(today);
            TextView daysFromView = findViewById(R.id.daysFromView);
            daysFromView.setText("Picture of the Day posted: "+daysFrom(today)+" days ago.");
        });

        datePickerBtn = findViewById(R.id.datePickerBtn);
        datePickerBtn.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(MainActivity.this,
                    (view, year, month, dayOfMonth) -> {
                        String selectedDate = String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month + 1, dayOfMonth);
                        getApod(selectedDate);
                        TextView daysFromView = findViewById(R.id.daysFromView);
                        daysFromView.setText("Picture of the Day posted: "+daysFrom(selectedDate)+" days ago.");
                    },
                    Calendar.getInstance().get(Calendar.YEAR),
                    Calendar.getInstance().get(Calendar.MONTH),
                    Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
            datePickerDialog.show();
        });
    }

    public void getApod(String date) {
        nasaApiService.getPictureOfTheDay(api, date).enqueue(new Callback<NasaPhoto>() {
            @Override
            public void onResponse(Call<NasaPhoto> call, Response<NasaPhoto> response) {
                if (response.isSuccessful() && response.body() != null) {
                    NasaPhoto photo = response.body();

                    ImageView imageView = findViewById(R.id.imageView);
                    Glide.with(MainActivity.this)
                            .load(photo.getUrl())
                            .into(imageView);

                    TextView titleView = findViewById(R.id.titleView);
                    titleView.setText(photo.getTitle());

                    TextView explanationView = findViewById(R.id.explanationView);
                    explanationView.setText(photo.getExplanation());
                }
            }

            @Override
            public void onFailure(Call<NasaPhoto> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Fetching data failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private int daysFrom(String date) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date publishedDate = sdf.parse(date);
            long diff = new Date().getTime() - publishedDate.getTime();
            return (int) (diff / (1000 * 60 * 60 * 24));
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

}