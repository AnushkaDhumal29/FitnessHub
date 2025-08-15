package com.v2v.fitnesshub;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.*;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.*;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.firebase.auth.*;

public class MainActivity extends AppCompatActivity {

    EditText etLoginEmail, etLoginPassword, etRegEmail, etRegPassword;
    Button btnLogin, btnRegister;
    RadioGroup rgTabs;
    ViewFlipper authFlipper;
    VideoView videoView;
    SignInButton btnGoogleLogin, btnGoogleRegister;

    FirebaseAuth mAuth;
    GoogleSignInClient googleSignInClient;

    ActivityResultLauncher<Intent> googleSignInLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getData() != null) {
                    try {
                        GoogleSignInAccount account = GoogleSignIn.getSignedInAccountFromIntent(result.getData())
                                .getResult(ApiException.class);
                        if (account != null) {
                            firebaseAuthWithGoogle(account.getIdToken());
                        }
                    } catch (ApiException e) {
                        Toast.makeText(this, "Google Sign-In failed", Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        // Video background
        videoView = findViewById(R.id.videoView);
        Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.background_video);
        videoView.setVideoURI(uri);
        videoView.setOnPreparedListener(mp -> { mp.setLooping(true); videoView.start(); });

        // Login
        etLoginEmail = findViewById(R.id.etLoginUser);
        etLoginPassword = findViewById(R.id.etLoginPassword);
        btnLogin = findViewById(R.id.btnLogin);

        // Register
        etRegEmail = findViewById(R.id.etRegEmail);
        etRegPassword = findViewById(R.id.etRegPassword);
        btnRegister = findViewById(R.id.btnRegister);

        // Google buttons
        btnGoogleLogin = findViewById(R.id.signInButtonLogin);
        btnGoogleRegister = findViewById(R.id.signInButtonRegister);

        // Tabs
        rgTabs = findViewById(R.id.rgTabs);
        authFlipper = findViewById(R.id.authFlipper);
        rgTabs.setOnCheckedChangeListener((g, id) -> authFlipper.setDisplayedChild(id == R.id.tabLogin ? 0 : 1));

        // Google Sign-In setup
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);

        // Email login
        btnLogin.setOnClickListener(v -> {
            String email = etLoginEmail.getText().toString().trim();
            String pass = etLoginPassword.getText().toString().trim();
            if (TextUtils.isEmpty(email) || TextUtils.isEmpty(pass)) {
                show("Enter email & password");
                return;
            }
            mAuth.signInWithEmailAndPassword(email, pass)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) goHome();
                        else show("Login failed: " + task.getException().getMessage());
                    });
        });

        // Email register
        btnRegister.setOnClickListener(v -> {
            String email = etRegEmail.getText().toString().trim();
            String pass = etRegPassword.getText().toString().trim();
            if (TextUtils.isEmpty(email) || TextUtils.isEmpty(pass)) {
                show("Enter email & password");
                return;
            }
            mAuth.createUserWithEmailAndPassword(email, pass)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) goHome();
                        else show("Registration failed: " + task.getException().getMessage());
                    });
        });

        // Google sign-in buttons
        btnGoogleLogin.setOnClickListener(v -> signInWithGoogle());
        btnGoogleRegister.setOnClickListener(v -> signInWithGoogle());
    }

    private void signInWithGoogle() {
        googleSignInLauncher.launch(googleSignInClient.getSignInIntent());
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential cred = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(cred)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) goHome();
                    else show("Google auth failed: " + task.getException().getMessage());
                });
    }

    private void goHome() {
        show("Welcome " + (mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getEmail() : ""));
        // startActivity(new Intent(this, HomeActivity.class));
        // finish();
    }

    private void show(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}
