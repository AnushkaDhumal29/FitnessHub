package com.v2v.fitnesshub;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TutorialsFragment extends Fragment {

    private RecyclerView recyclerView;
    private ExerciseAdapter adapter;
    private final List<ExerciseItem> exercises = new ArrayList<>();
    private SearchView searchView;
    private androidx.appcompat.widget.AppCompatSpinner spinnerCategories;

    private final String[] categories = {
            "All",
            "Stretching tutorials",
            "Warm ups",
            "Post-workout recovery",
            "Push-ups, Squats, Lunges, Planks",
            "Perfect for home workouts",
            "Strength Training (with equipment/dumbbells)",
            "Cardio & HIIT",
            "Yoga (control)",
            "Yoga for pregnant ladies",
            "Yoga for ladies",
            "Muscles tutorials (chest, back, legs, arms, abs)",
            "Weight loss",
            "Muscle gains",
            "Common mistakes & how to avoid injuries",
            "Meditation",
            "Beginner friendly",
            "Periods & PMS stretch",
            "Blood Pressure Control",
            "Workout For Senior Citizens"
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_turorials, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewExercises);
        searchView = view.findViewById(R.id.searchViewExercises);
        spinnerCategories = view.findViewById(R.id.exerciseSpinner);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // 🔹 Load video data
        loadExercises();

        // 🔹 Adapter with click listener for opening video in YouTube
        adapter = new ExerciseAdapter(requireContext(), exercises, item -> {
            String videoUrl = "https://www.youtube.com/watch?v=" + item.getSingleVideoId();
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(videoUrl));
            try {
                intent.setPackage("com.google.android.youtube");
                startActivity(intent);
            } catch (Exception e) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(videoUrl)));
            }
        });

        recyclerView.setAdapter(adapter);

        // 🔹 Search bar filtering
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filter(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filter(newText);
                return true;
            }
        });

        // 🔹 Category spinner
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item, categories);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategories.setAdapter(spinnerAdapter);

        spinnerCategories.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selected = categories[position];
                if (selected.equals("All")) {
                    adapter.updateList(new ArrayList<>(exercises));
                } else {
                    filterByCategory(selected);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        return view;
    }

    // 🔹 Load tutorials list
    private void loadExercises() {
        exercises.clear();

        exercises.add(new ExerciseItem("Stretching tutorials", "itJE4neqDJw", null, getThumbnail("itJE4neqDJw")));
        exercises.add(new ExerciseItem("Strength Training (with equipment/dumbbells)", "RPi9aJGuRDM", null, getThumbnail("RPi9aJGuRDM")));
        exercises.add(new ExerciseItem("Cardio & HIIT", "M0uO8X3_tEA", null, getThumbnail("M0uO8X3_tEA")));
        exercises.add(new ExerciseItem("Yoga (control)", "dAqQqmaI9vY", null, getThumbnail("dAqQqmaI9vY")));
        exercises.add(new ExerciseItem("Yoga for pregnant ladies", "B87FpWtkIKA", null, getThumbnail("B87FpWtkIKA")));
        exercises.add(new ExerciseItem("Yoga for ladies", "5rfII8w7gwQ", null, getThumbnail("5rfII8w7gwQ")));
        exercises.add(new ExerciseItem("Weight loss", "ow3hpYJqYEI", null, getThumbnail("ow3hpYJqYEI")));
        exercises.add(new ExerciseItem("Muscle gains", "RuXmYDGcoa8", null, getThumbnail("RuXmYDGcoa8")));
        exercises.add(new ExerciseItem("Muscles tutorials (chest, back, legs, arms, abs)", "ESkI_WR1qqc", null, getThumbnail("ESkI_WR1qqc")));
        exercises.add(new ExerciseItem("Warm ups", "f3zOrYCwquE", null, getThumbnail("f3zOrYCwquE")));
        exercises.add(new ExerciseItem("Post-workout recovery", "AUsbthQ9W-I", null, getThumbnail("AUsbthQ9W-I")));
        exercises.add(new ExerciseItem("Push-ups, Squats, Lunges, Planks", "bI2qDaLrKCg", null, getThumbnail("bI2qDaLrKCg")));
        exercises.add(new ExerciseItem("Perfect for home workouts", "jKTxe236-4U", null, getThumbnail("jKTxe236-4U")));
        exercises.add(new ExerciseItem("Common mistakes & how to avoid injuries", "2AdC-o0fd2s", null, getThumbnail("2AdC-o0fd2s")));
        exercises.add(new ExerciseItem("Meditation", "inpok4MKVLM", null, getThumbnail("inpok4MKVLM")));
        exercises.add(new ExerciseItem("Beginner friendly", "W2Bm9dfQjbk", null, getThumbnail("W2Bm9dfQjbk")));
        exercises.add(new ExerciseItem("Periods & PMS stretch", "2X78NWuRfJU", null, getThumbnail("2X78NWuRfJU")));
        exercises.add(new ExerciseItem("Blood Pressure Control", "_KB18nR27F8", null, getThumbnail("_KB18nR27F8")));
        exercises.add(new ExerciseItem("Workout For Senior Citizens", "E2YqFYFLSbE", null, getThumbnail("E2YqFYFLSbE")));
    }

    // 🔹 Generate YouTube thumbnail URL from video ID
    private String getThumbnail(String videoId) {
        return "https://img.youtube.com/vi/" + videoId + "/hqdefault.jpg";
    }

    private void filter(String text) {
        List<ExerciseItem> filtered = new ArrayList<>();
        for (ExerciseItem item : exercises) {
            if (item.getName().toLowerCase().contains(text.toLowerCase())) {
                filtered.add(item);
            }
        }
        adapter.updateList(filtered);
    }

    private void filterByCategory(String category) {
        List<ExerciseItem> filtered = new ArrayList<>();
        for (ExerciseItem item : exercises) {
            if (item.getName().toLowerCase().contains(category.toLowerCase())) {
                filtered.add(item);
            }
        }
        adapter.updateList(filtered);
    }
}