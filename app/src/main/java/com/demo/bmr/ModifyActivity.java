package com.demo.bmr;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class ModifyActivity extends AppCompatActivity {

    EditText nameEditText,ageEditText,heightEditText,weightEditText;
    Button saveChangesBtn;
    private String recordId;
    ImageView fimg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.modify);

        nameEditText = findViewById(R.id.nameModify);
        ageEditText = findViewById(R.id.ageModify);
        heightEditText = findViewById(R.id.heightModify);
        weightEditText = findViewById(R.id.weightModify);
        saveChangesBtn = findViewById(R.id.save_change_btn);
        fimg=findViewById(R.id.femaleModify);

        // Get data from intent
        Intent intent = getIntent();
        recordId = intent.getStringExtra("recordId");
        String name = intent.getStringExtra("name");
        String age = intent.getStringExtra("age");
        String height = intent.getStringExtra("height");
        String weight = intent.getStringExtra("weight");

        //set data to edit texts
        nameEditText.setText(name);
        ageEditText.setText(age);
        heightEditText.setText(height);
        weightEditText.setText(weight);
        int basecolor2 = ContextCompat.getColor(ModifyActivity.this, R.color.purple_700);
        int color2= ColorUtils.setAlphaComponent(basecolor2,30);
        fimg.setColorFilter(color2);


        saveChangesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String updatedName = nameEditText.getText().toString();
                String updatedAge = ageEditText.getText().toString();
                String updatedHeight = heightEditText.getText().toString();
                String updatedWeight = weightEditText.getText().toString();

                try{
                    double updateAgeDouble = Double.parseDouble(updatedAge);
                    double updateHeightDouble = Double.parseDouble(updatedHeight);
                    double updateWeightDouble = Double.parseDouble(updatedWeight);
                    double updatedBmr = (10 * (updateWeightDouble)) + (6.25 * updateHeightDouble) - (5 * updateAgeDouble) + 161;
                    double updatedBmi = updateWeightDouble / ((updateHeightDouble / 100) * (updateHeightDouble / 100));

                    new UpdateDataTask(recordId, updatedName, updatedAge, updatedHeight, updatedWeight, updatedBmr, updatedBmi).execute();
                    Toast.makeText(ModifyActivity.this, "modify successful", Toast.LENGTH_LONG).show();
                }catch (NumberFormatException e) {
                    e.printStackTrace();
                    Toast.makeText(ModifyActivity.this, "Invalid input", Toast.LENGTH_SHORT).show();
                }

            }

        });

    }
    private class UpdateDataTask extends AsyncTask<Void, Void, String> {
        private String id, name, age, height, weight;
        private double BMR, BMI;

        public UpdateDataTask(String id, String name, String age, String height, String weight, double BMR, double BMI) {
            this.id = id;
            this.name = name;
            this.age = age;
            this.height = height;
            this.weight = weight;
            this.BMR = BMR;
            this.BMI = BMI;
        }

        @Override
        protected String doInBackground(Void... voids) {
            String response = "";
            try {
                URL url = new URL("http://10.0.2.2/update_data.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                String postData =
                        "&name=" + URLEncoder.encode(name, "UTF-8") +
                        "&age=" + URLEncoder.encode(age, "UTF-8") +
                        "&height=" + URLEncoder.encode(height, "UTF-8") +
                        "&weight=" + URLEncoder.encode(weight, "UTF-8") +
                        "&BMR=" + URLEncoder.encode(String.valueOf(BMR), "UTF-8") +
                        "&BMI=" + URLEncoder.encode(String.valueOf(BMI), "UTF-8");
                OutputStream os = conn.getOutputStream();
                os.write(postData.getBytes("UTF-8"));
                os.flush();
                os.close();

                int responseCode = conn.getResponseCode();
                Log.d("HTTP", "Response Code: " + responseCode);
                response = "Response Code: " + responseCode;

            } catch (Exception e) {
                e.printStackTrace();
            }
            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            // Refresh the MainActivity to show updated data
            Intent intent = new Intent(ModifyActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }
    }

}

