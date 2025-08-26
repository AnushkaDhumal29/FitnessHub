package com.v2v.fitnesshub;

import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.InputStream;

public class MyPostActivity extends AppCompatActivity {

    LinearLayout postContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_post);

        postContainer = findViewById(R.id.postContainer);

        loadPosts();
    }

    private void loadPosts() {
        try {
            SharedPreferences prefs = getSharedPreferences("MyPosts", MODE_PRIVATE);
            String postsJson = prefs.getString("posts", "[]");

            JSONArray postArray = new JSONArray(postsJson);

            for (int i = 0; i < postArray.length(); i++) {
                JSONObject post = postArray.getJSONObject(i);

                String text = post.optString("text", "");
                String imageUri = post.optString("image", null);

                // Post View Layout
                LinearLayout postLayout = new LinearLayout(this);
                postLayout.setOrientation(LinearLayout.VERTICAL);
                postLayout.setPadding(16,16,16,16);

                if (imageUri != null) {
                    try {
                        Uri uri = Uri.parse(imageUri);
                        InputStream inputStream = getContentResolver().openInputStream(uri);
                        ImageView iv = new ImageView(this);
                        iv.setImageBitmap(BitmapFactory.decodeStream(inputStream));
                        iv.setAdjustViewBounds(true);
                        postLayout.addView(iv);
                    } catch (Exception e) { e.printStackTrace(); }
                }

                if (!text.isEmpty()) {
                    TextView tv = new TextView(this);
                    tv.setText(text);
                    tv.setTextSize(16f);
                    postLayout.addView(tv);
                }

                postContainer.addView(postLayout);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
