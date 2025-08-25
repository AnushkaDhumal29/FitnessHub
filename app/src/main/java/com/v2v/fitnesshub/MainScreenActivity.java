package com.v2v.fitnesshub;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.FitnessOptions;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class MainScreenActivity extends AppCompatActivity {

    private EditText editSleep, editWorkout;
    private TextView tvWater;
    private LinearLayout calendarRow;
    private HorizontalScrollView horizontalCalendarScroll;
    private BottomNavigationView bottomNav;
    private ImageView imageView;

    // Fitness TextViews
    private TextView tvHeartRate, tvDistance, tvCalories, tvSteps;

    private static final int GOOGLE_FIT_PERMISSIONS_REQUEST_CODE = 1;

    // Alarm manager vars
    private AlarmManager alarmManager;
    private PendingIntent pendingIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainscreen_activity); // ✅ your XML

        // Find views
        editSleep = findViewById(R.id.editSleep);
        editWorkout = findViewById(R.id.editWorkout);
        tvWater = findViewById(R.id.tvWater);
        calendarRow = findViewById(R.id.calendarRow);
        horizontalCalendarScroll = findViewById(R.id.horizontalCalendarScroll);
        bottomNav = findViewById(R.id.bottomNav);
        TextView tvMonth = findViewById(R.id.tvMonth);
        imageView = findViewById(R.id.ivMenu);

        tvDistance = findViewById(R.id.tvDistance);
        tvHeartRate = findViewById(R.id.tvHeartRate);
        tvCalories = findViewById(R.id.tvCalories);
        tvSteps = findViewById(R.id.tvSteps);

        // Default values
        tvDistance.setText("0 km");
        tvHeartRate.setText("0 bpm");
        tvCalories.setText("0 kcal");
        tvSteps.setText("0 Steps");

        // Alarm Manager init
        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        // Calendar
        generateHorizontalCalendar(calendarRow, tvMonth);

        // Sleep reminder
        editSleep.setOnClickListener(v -> openTimePicker("Sleep", 101));

        // Workout reminder
        editWorkout.setOnClickListener(v -> openTimePicker("Workout", 102));

        // Water reminder (repeating every 2 hrs)
        tvWater.setOnClickListener(v -> setRepeatingReminder("Drink Water 💧", 2));

        // BottomNavigation
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                Toast.makeText(this, "Home clicked", Toast.LENGTH_SHORT).show();
                return true;
            } else if (id == R.id.nav_progress) {
                startActivity(new Intent(this, ProgressActivity.class));
                return true;
            } else if (id == R.id.nav_plan) {
                startActivity(new Intent(this, DietPlanActivity.class));
                return true;
            } else if (id == R.id.nav_tutorials) {
                Toast.makeText(this, "Tutorials clicked", Toast.LENGTH_SHORT).show();
                return true;
            }
            return false;
        });

        // Popup Menu
        imageView.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(MainScreenActivity.this, imageView);
            popup.getMenuInflater().inflate(R.menu.popup_menu, popup.getMenu());
            popup.setOnMenuItemClickListener(item -> {
                int id = item.getItemId();
                if (id == R.id.menu_profile) {
                    startActivity(new Intent(MainScreenActivity.this, ProfileViewActivity.class));
                } else if (id == R.id.menu_info) {
                    startActivity(new Intent(MainScreenActivity.this, AboutActivity.class));
                } else if (id == R.id.menu_game) {
                    startActivity(new Intent(MainScreenActivity.this, GameActivity.class));
                } else if (id == R.id.menu_nearby) {
                    Toast.makeText(MainScreenActivity.this, "Nearby Search clicked", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(MainScreenActivity.this, MappActivity.class));
                } else if (id == R.id.menu_posts) {
                    startActivity(new Intent(MainScreenActivity.this, MyPostActivity.class));
                } else if (id == R.id.menu_BMI) {
                    startActivity(new Intent(MainScreenActivity.this, BMICalculatorActivity.class));
                    return true;
                } else {
                    return false;
                }
                return true;
            });
            popup.show();
        });

        // Month update on scroll
        horizontalCalendarScroll.getViewTreeObserver().addOnScrollChangedListener(() -> {
            int scrollX = horizontalCalendarScroll.getScrollX();
            for (int i = 0; i < calendarRow.getChildCount(); i++) {
                LinearLayout dayView = (LinearLayout) calendarRow.getChildAt(i);
                int left = dayView.getLeft();
                int right = dayView.getRight();
                int dayCenter = (left + right) / 2;
                int scrollWidth = scrollX + horizontalCalendarScroll.getWidth() / 2;
                if (dayCenter >= scrollX && dayCenter <= scrollWidth) {
                    String monthYear = (String) dayView.getTag();
                    tvMonth.setText(monthYear);
                    break;
                }
            }
        });

        // Google Fit
        initGoogleFit();
    }

    // ================= ALARM FUNCTIONS =================

    private void openTimePicker(String type, int reqCode) {
        Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(
                this,
                (view, hourOfDay, min) -> {
                    c.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    c.set(Calendar.MINUTE, min);
                    c.set(Calendar.SECOND, 0);

                    setAlarm(type + " Reminder ⏰", c, reqCode);

                    if (type.equals("Sleep")) {
                        editSleep.setText("💤 " + hourOfDay + ":" + min);
                    } else {
                        editWorkout.setText("🏋️ " + hourOfDay + ":" + min);
                    }
                },
                hour, minute, true
        );
        timePickerDialog.show();
    }

    @SuppressLint("ScheduleExactAlarm")
    private void setAlarm(String message, Calendar calendar, int reqCode) {
        Intent intent = new Intent(this, ReminderReciver.class);
        intent.putExtra("msg", message);

        pendingIntent = PendingIntent.getBroadcast(this, reqCode, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        Toast.makeText(this, message + " set", Toast.LENGTH_SHORT).show();
    }

    private void setRepeatingReminder(String message, int hours) {
        Intent intent = new Intent(this, ReminderReciver.class);
        intent.putExtra("msg", message);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this, 200, intent, PendingIntent.FLAG_IMMUTABLE);

        long interval = hours * 60 * 60 * 1000; // every 2 hours
        alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                System.currentTimeMillis() + interval,
                interval,
                pendingIntent);

        Toast.makeText(this, "💧 Water Reminder every " + hours + " hrs", Toast.LENGTH_SHORT).show();
    }

    // ================= GOOGLE FIT =================
    private void initGoogleFit() {
        FitnessOptions fitnessOptions = FitnessOptions.builder()
                .addDataType(DataType.TYPE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
                .addDataType(DataType.TYPE_DISTANCE_DELTA, FitnessOptions.ACCESS_READ)
                .addDataType(DataType.TYPE_CALORIES_EXPENDED, FitnessOptions.ACCESS_READ)
                .addDataType(DataType.TYPE_HEART_RATE_BPM, FitnessOptions.ACCESS_READ)
                .build();

        GoogleSignInAccount account = GoogleSignIn.getAccountForExtension(this, fitnessOptions);

        if (!GoogleSignIn.hasPermissions(account, fitnessOptions)) {
            GoogleSignIn.requestPermissions(
                    this,
                    GOOGLE_FIT_PERMISSIONS_REQUEST_CODE,
                    account,
                    fitnessOptions);
        } else {
            accessGoogleFitData(account);
        }
    }

    private void accessGoogleFitData(GoogleSignInAccount account) {
        // Distance
        Fitness.getHistoryClient(this, account)
                .readDailyTotal(DataType.TYPE_DISTANCE_DELTA)
                .addOnSuccessListener(dataSet -> {
                    float distance = dataSet.isEmpty() ? 0 :
                            dataSet.getDataPoints().get(0).getValue(
                                    DataType.TYPE_DISTANCE_DELTA.getFields().get(0)).asFloat();
                    tvDistance.setText(String.format(Locale.getDefault(), "%.2f km", distance / 1000));
                });

        // Calories
        Fitness.getHistoryClient(this, account)
                .readDailyTotal(DataType.TYPE_CALORIES_EXPENDED)
                .addOnSuccessListener(dataSet -> {
                    float calories = dataSet.isEmpty() ? 0 :
                            dataSet.getDataPoints().get(0).getValue(
                                    DataType.TYPE_CALORIES_EXPENDED.getFields().get(0)).asFloat();
                    tvCalories.setText(String.format(Locale.getDefault(), "%.0f kcal", calories));
                });

        // Heart Rate
        Fitness.getHistoryClient(this, account)
                .readDailyTotal(DataType.TYPE_HEART_RATE_BPM)
                .addOnSuccessListener(dataSet -> {
                    float heartRate = dataSet.isEmpty() ? 0 :
                            dataSet.getDataPoints().get(0).getValue(
                                    DataType.TYPE_HEART_RATE_BPM.getFields().get(0)).asFloat();
                    tvHeartRate.setText((int) heartRate + " bpm");
                });

        // Steps
        Fitness.getHistoryClient(this, account)
                .readDailyTotal(DataType.TYPE_STEP_COUNT_DELTA)
                .addOnSuccessListener(dataSet -> {
                    int steps = dataSet.isEmpty() ? 0 :
                            dataSet.getDataPoints().get(0).getValue(
                                    DataType.TYPE_STEP_COUNT_DELTA.getFields().get(0)).asInt();
                    tvSteps.setText(steps + " Steps");
                })
                .addOnFailureListener(e -> Log.e("FITNESS", "Steps read fail: " + e));
    }

    // ================= CALENDAR =================
    private void generateHorizontalCalendar(LinearLayout calendarRow, TextView tvMonth) {
        Calendar calendar = Calendar.getInstance();
        int today = calendar.get(Calendar.DAY_OF_MONTH);
        int currentMonth = calendar.get(Calendar.MONTH);
        int currentYear = calendar.get(Calendar.YEAR);

        calendar.set(Calendar.DAY_OF_MONTH, 1);
        int daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        for (int i = 1; i <= daysInMonth; i++) {
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            int month = calendar.get(Calendar.MONTH);
            int year = calendar.get(Calendar.YEAR);

            LinearLayout dayContainer = new LinearLayout(this);
            dayContainer.setOrientation(LinearLayout.VERTICAL);
            dayContainer.setPadding(24, 12, 24, 12);
            dayContainer.setGravity(Gravity.CENTER);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(16, 0, 16, 0);
            dayContainer.setLayoutParams(params);

            TextView dayName = new TextView(this);
            dayName.setText(new SimpleDateFormat("EEE", Locale.getDefault()).format(calendar.getTime()));
            dayName.setTextSize(12);
            dayName.setGravity(Gravity.CENTER);
            dayName.setTextColor(getResources().getColor(android.R.color.white));

            TextView dayNumber = new TextView(this);
            dayNumber.setText(String.valueOf(day));
            dayNumber.setTextSize(16);
            dayNumber.setGravity(Gravity.CENTER);

            if (day == today && month == currentMonth && year == currentYear) {
                dayContainer.setBackgroundResource(R.drawable.bg_calendar_today);
                dayNumber.setTextColor(getResources().getColor(android.R.color.white));
            } else {
                dayContainer.setBackgroundResource(R.drawable.bg_calendar_day);
                dayNumber.setTextColor(getResources().getColor(android.R.color.darker_gray));
            }

            int finalDay = day;
            int finalMonth = month;
            int finalYear = year;
            dayContainer.setOnClickListener(v ->
                    Toast.makeText(this,
                            "Selected: " + finalDay + "/" + (finalMonth + 1) + "/" + finalYear,
                            Toast.LENGTH_SHORT).show()
            );

            dayContainer.setTag(new SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(calendar.getTime()));
            dayContainer.addView(dayName);
            dayContainer.addView(dayNumber);
            calendarRow.addView(dayContainer);

            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        tvMonth.setText(new SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(Calendar.getInstance().getTime()));
    }
}
