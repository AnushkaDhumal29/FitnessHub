package com.v2v.fitnesshub;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class MainScreenActivity extends AppCompatActivity {

    private EditText editSleep, editWorkout;
    private LinearLayout calendarRow;
    private HorizontalScrollView horizontalCalendarScroll;

    ImageView imageView;
    private BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainscreen_activity);


        // Find views
        editSleep = findViewById(R.id.editSleep);
        editWorkout = findViewById(R.id.editWorkout);
        calendarRow = findViewById(R.id.calendarRow);
        horizontalCalendarScroll = findViewById(R.id.horizontalCalendarScroll);
        bottomNav = findViewById(R.id.bottomNav); // BottomNavigationView
        TextView tvMonth = findViewById(R.id.tvMonth);
        imageView =findViewById(R.id.ivMenu);

        // Generate calendar
        generateHorizontalCalendar(calendarRow, tvMonth);

        // Sleep reminder -> TimePicker
        editSleep.setOnClickListener(v -> showTimePicker(editSleep, "sleep"));

        // Workout reminder -> TimePicker
        editWorkout.setOnClickListener(v -> showTimePicker(editWorkout, "workout"));

        // BottomNavigationView item selection
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_home) {
                Toast.makeText(this, "Home clicked", Toast.LENGTH_SHORT).show();
                return true;
            } else if (id == R.id.nav_progress) {
                Intent intent = new Intent(this,ProgressActivity.class);
                startActivity(intent);
                return true;
            } else if (id == R.id.nav_plan) {
                Toast.makeText(this, "Plan clicked", Toast.LENGTH_SHORT).show();
                return true;
            } else if (id == R.id.nav_tutorials) {
                Toast.makeText(this, "Tutorials clicked", Toast.LENGTH_SHORT).show();
                return true;
            }

            return false;
        });

        imageView.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(MainScreenActivity.this, imageView);
            popup.getMenuInflater().inflate(R.menu.popup_menu, popup.getMenu());

            popup.setOnMenuItemClickListener(item -> {
                int id = item.getItemId();

                if (id == R.id.menu_profile) {
                    Toast.makeText(MainScreenActivity.this, "Profile clicked", Toast.LENGTH_SHORT).show();
                    // open profile activity
                } else if (id == R.id.menu_info) {
                    Toast.makeText(MainScreenActivity.this, "Info clicked", Toast.LENGTH_SHORT).show();
                } else if (id == R.id.menu_game) {
                    Intent intent = new Intent(MainScreenActivity.this, GameActivity.class);
                    startActivity(intent);
                } else if (id == R.id.menu_nearby) {
                    Toast.makeText(MainScreenActivity.this, "Nearby Search clicked", Toast.LENGTH_SHORT).show();
                } else {
                    return false;
                }

                return true;
            });

            popup.show();
        });


        // Scroll listener for dynamic month update
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
    }

    private void showTimePicker(EditText targetEditText, String type) {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog dialog = new TimePickerDialog(
                this,
                (view, selectedHour, selectedMinute) -> {
                    String amPm = (selectedHour >= 12) ? "PM" : "AM";
                    int hour12 = selectedHour % 12;
                    if (hour12 == 0) hour12 = 12;

                    String time = String.format(Locale.getDefault(), "%02d:%02d %s", hour12, selectedMinute, amPm);

                    if ("sleep".equals(type)) {
                        targetEditText.setText("Your sleep time is " + time);
                    } else if ("workout".equals(type)) {
                        targetEditText.setText("Your workout time is " + time);
                    }
                },
                hour, minute, false
        );
        dialog.show();
    }


    private void generateHorizontalCalendar(LinearLayout calendarRow, TextView tvMonth) {
        Calendar calendar = Calendar.getInstance();
        int today = calendar.get(Calendar.DAY_OF_MONTH);
        int currentMonth = calendar.get(Calendar.MONTH);
        int currentYear = calendar.get(Calendar.YEAR);

        for (int m = 0; m < 12; m++) {
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

            calendar.set(Calendar.DAY_OF_MONTH, 1);
            calendar.add(Calendar.MONTH, 1);
        }

        tvMonth.setText(new SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(Calendar.getInstance().getTime()));
    }


}
