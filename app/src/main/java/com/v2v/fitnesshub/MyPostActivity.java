package com.v2v.fitnesshub;

import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
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

            LayoutInflater inflater = LayoutInflater.from(this);

            for (int i = 0; i < postArray.length(); i++) {
                JSONObject post = postArray.getJSONObject(i);

                String text = post.optString("text", "");
                String imageUri = post.optString("image", null);
                String time = post.optString("time", "0");

                // Inflate your card design (post_item.xml)
                LinearLayout postLayout = (LinearLayout) inflater.inflate(
                        R.layout.post_item, postContainer, false);

                ImageView postImage = postLayout.findViewById(R.id.postImage);
                TextView postText = postLayout.findViewById(R.id.postText);
                TextView postTime = postLayout.findViewById(R.id.postTime);

                // Set post text
                postText.setText(text);

                // Format timestamp
                try {
                    long timestamp = Long.parseLong(time);
                    long now = System.currentTimeMillis();

                    CharSequence relativeTime = DateUtils.getRelativeTimeSpanString(
                            timestamp,
                            now,
                            DateUtils.MINUTE_IN_MILLIS,
                            DateUtils.FORMAT_ABBREV_RELATIVE);

                    postTime.setText(relativeTime);
                } catch (Exception e) {
                    postTime.setText("Just now");
                }

                // Set image if available
                if (imageUri != null) {
                    try {
                        Uri uri = Uri.parse(imageUri);
                        InputStream inputStream = getContentResolver().openInputStream(uri);
                        postImage.setImageBitmap(BitmapFactory.decodeStream(inputStream));
                    } catch (Exception e) {
                        e.printStackTrace();
                        postImage.setVisibility(ImageView.GONE);
                    }
                } else {
                    postImage.setVisibility(ImageView.GONE);
                }

                // Add card to container
                postContainer.addView(postLayout);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
