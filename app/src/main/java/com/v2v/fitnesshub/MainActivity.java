package com.v2v.fitnesshub;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.widget.VideoView;
import android.widget.ViewFlipper;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;

public class MainActivity extends AppCompatActivity {

    EditText etLoginUser, etLoginPassword;
    Button btnLogin, btnRegister;
    RadioGroup rgTabs;
    RadioButton tabLogin, tabRegister;
    ViewFlipper authFlipper;
    VideoView videoView;
    SignInButton signInButtonLogin, signInButtonRegister;

    GoogleSignInClient googleSignInClient;

    // Activity Result Launcher for Google Sign-In
    ActivityResultLauncher<Intent> googleSignInLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getData() != null) {
                    try {
                        GoogleSignInAccount account = GoogleSignIn.getSignedInAccountFromIntent(result.getData())
                                .getResult(ApiException.class);
                        if (account != null) {
                            String name = account.getDisplayName();
                            String email = account.getEmail();
                            Toast.makeText(this, "Signed in as: " + name, Toast.LENGTH_SHORT).show();
                        }
                    } catch (ApiException e) {
                        Toast.makeText(this, "Google Sign-In failed", Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Video background setup
        videoView = findViewById(R.id.videoView);
        Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.background_video);
        videoView.setVideoURI(uri);
        videoView.setOnPreparedListener(mp -> {
            mp.setLooping(true);
            videoView.start();
        });

        // Login fields
        etLoginUser = findViewById(R.id.etLoginUser);
        etLoginPassword = findViewById(R.id.etLoginPassword);
        btnLogin = findViewById(R.id.btnLogin);

        // Register fields
        btnRegister = findViewById(R.id.btnRegister);

        // Google buttons
        signInButtonLogin = findViewById(R.id.signInButtonLogin);
        signInButtonRegister = findViewById(R.id.signInButtonRegister);

        // Tabs and ViewFlipper
        rgTabs = findViewById(R.id.rgTabs);
        tabLogin = findViewById(R.id.tabLogin);
        tabRegister = findViewById(R.id.tabRegister);
        authFlipper = findViewById(R.id.authFlipper);

        // Google Sign-In Options
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);

        // Switch forms when tabs change
        rgTabs.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.tabLogin) {
                authFlipper.setDisplayedChild(0);
            } else if (checkedId == R.id.tabRegister) {
                authFlipper.setDisplayedChild(1);
            }
        });

        // Login button click
        btnLogin.setOnClickListener(v -> {
            String username = etLoginUser.getText().toString().trim();
            String password = etLoginPassword.getText().toString().trim();

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(MainActivity.this, "Please enter all fields", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity.this, "Login successful!", Toast.LENGTH_SHORT).show();
            }
        });

        // Register button click
        btnLogin.setOnClickListener(v -> {
            String username = etLoginUser.getText().toString().trim();
            String password = etLoginPassword.getText().toString().trim();

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(MainActivity.this, "Please enter all fields", Toast.LENGTH_SHORT).show();
            } else {
                Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void signInWithGoogle() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        googleSignInLauncher.launch(signInIntent);
    }
}
