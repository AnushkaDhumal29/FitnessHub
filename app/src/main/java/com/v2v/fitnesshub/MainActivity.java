package com.v2v.fitnesshub;

import android.net.Uri;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.widget.ViewFlipper;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class MainActivity extends AppCompatActivity {

    private ViewFlipper authFlipper;
    private RadioGroup rgTabs;

    // Login
    private EditText etLoginUser, etLoginPassword;
    private Button btnLogin;
    private SignInButton signInButtonLogin;

    // Register
    private EditText etRegUser, etRegEmail, etRegPassword, etConfirmPassword;
    private Button btnRegister;
    private SignInButton signInButtonRegister;

    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 100;

    private VideoView videoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        initViews();
        setupBackgroundVideo();
        setupTabs();
        setupGoogleSignIn();
        setupActions();
    }

    private void initViews() {
        authFlipper = findViewById(R.id.authFlipper);
        rgTabs = findViewById(R.id.rgTabs);

        etLoginUser = findViewById(R.id.etLoginUser);
        etLoginPassword = findViewById(R.id.etLoginPassword);
        btnLogin = findViewById(R.id.btnLogin);
        signInButtonLogin = findViewById(R.id.signInButtonLogin);

        etRegUser = findViewById(R.id.etRegUser);
        etRegEmail = findViewById(R.id.etRegEmail);
        etRegPassword = findViewById(R.id.etRegPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnRegister = findViewById(R.id.btnRegister);
        signInButtonRegister = findViewById(R.id.signInButtonRegister);

        videoView = findViewById(R.id.videoView);
    }

    private void setupBackgroundVideo() {
        // Put your video in res/raw/bg_video.mp4
        Uri video = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.background_video);
        videoView.setVideoURI(video);
        videoView.setOnPreparedListener(mp -> {
            mp.setLooping(true);
            mp.setVolume(0f, 0f); // mute bg video
        });
        videoView.start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (videoView != null && !videoView.isPlaying()) {
            videoView.start();
        }
    }

    private void setupTabs() {
        rgTabs.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.tabLogin) {
                authFlipper.setDisplayedChild(0);
            } else if (checkedId == R.id.tabRegister) {
                authFlipper.setDisplayedChild(1);
            }
        });
    }

    private void setupGoogleSignIn() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    private void setupActions() {
        // Email Login
        btnLogin.setOnClickListener(v -> {
            String email = etLoginUser.getText().toString().trim();
            String pass = etLoginPassword.getText().toString().trim();

            if (email.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Enter Email & Password", Toast.LENGTH_SHORT).show();
            } else {
                mAuth.signInWithEmailAndPassword(email, pass)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                FirebaseUser user = mAuth.getCurrentUser();
                                Toast.makeText(this, "Welcome back " + user.getEmail(), Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(MainActivity.this, MainScreenActivity.class));
                                finish();
                            } else {
                                Toast.makeText(this, "Login failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        // Register new user
        btnRegister.setOnClickListener(v -> {
            String email = etRegEmail.getText().toString().trim();
            String pass = etRegPassword.getText().toString().trim();
            String confirm = etConfirmPassword.getText().toString().trim();

            if (email.isEmpty() || pass.isEmpty() || confirm.isEmpty()) {
                Toast.makeText(this, "All fields required", Toast.LENGTH_SHORT).show();
            } else if (!pass.equals(confirm)) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            } else {
                mAuth.createUserWithEmailAndPassword(email, pass)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                FirebaseUser user = mAuth.getCurrentUser();
                                Toast.makeText(this, "Registered: " + user.getEmail(), Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(MainActivity.this, MainScreenActivity.class));
                                finish();
                            } else {
                                Toast.makeText(this, "Register failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        // Google Sign-in
        signInButtonLogin.setOnClickListener(v -> signInGoogle());
        signInButtonRegister.setOnClickListener(v -> signInGoogle());
    }

    private void signInGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                Toast.makeText(this, "Google sign in failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        Toast.makeText(this, "Google Login success: " + user.getEmail(), Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(MainActivity.this, MainScreenActivity.class));
                        finish();
                    } else {
                        Toast.makeText(this, "Google Login failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
