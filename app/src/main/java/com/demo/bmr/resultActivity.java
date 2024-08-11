package com.demo.bmr;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class resultActivity extends AppCompatActivity {

    private Button cancel_btn2,save_btn2;
    TextView name_output,bmr_output,bmi_output;
    double height_count=0,weight_count=0,age_count=0,bmr=0,bmi=0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.result);

        cancel_btn2=findViewById(R.id.cancel_btn2);
        save_btn2=findViewById(R.id.save_btn2);
        name_output=findViewById(R.id.name_output);
        bmr_output=findViewById(R.id.bmr_output);
        bmi_output=findViewById(R.id.bmi_output);


        //take data from previous page
        Intent intent=getIntent();
        String name_count=intent.getStringExtra("name");
        String age_count=intent.getStringExtra("age");
        String height_count=intent.getStringExtra("height");
        String weight_count=intent.getStringExtra("weight");
        String gender_count=intent.getStringExtra("user");

        //convert string to int/double
        int ageInt=Integer.parseInt(age_count);
        double heightDouble=Double.parseDouble(height_count);
        int weightInt=Integer.parseInt(weight_count);
        //count bmr
        name_output.setText(name_count);
        if(gender_count.equals("Male")){
            bmr=((10*(weightInt))+(6.25*heightDouble)-(5*ageInt)+5);
            bmr_output.setText(Double.toString(bmr));
            bmi=weightInt/((heightDouble/100)*(heightDouble/100));
            bmi_output.setText(Double.toString(bmi));
        }
        else if(gender_count.equals("Female")){
            bmr=((10*(weightInt))+(6.25*heightDouble)-(5*ageInt)+161);
            bmr_output.setText(Double.toString(bmr));
            bmi=weightInt/((heightDouble/100)*(heightDouble/100));
            bmi_output.setText(Double.toString(bmi));
        }

        cancel_btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {           //change page
                Intent intent=new Intent(resultActivity.this,CreateRecordActivity.class);
                startActivity(intent);
            }
        });
        save_btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {           //change page
                new SendDataTask(name_count, age_count, height_count, weight_count, bmr, bmi).execute();
                Intent intent2=new Intent(resultActivity.this,MainActivity.class);
                startActivity(intent2);

            }
        });

    }
    public class SendDataTask extends AsyncTask<Void,Void,String>{
        private String name,age,height,weight;
        private double BMI,BMR;
        public SendDataTask(String name,String age, String height, String weight, double BMR, double BMI){
            this.name = name;
            this.age = age;
            this.height = height;
            this.weight = weight;
            this.BMR = BMR;
            this.BMI = BMI;
        }
        @Override
        protected String doInBackground(Void...voids){
            String response="";
            try{
                URL url=new URL("http://10.0.2.2/insert_data.php");
                HttpURLConnection conn=(HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                String postData="name=" + name + "&age=" + age + "&height=" + height + "&weight=" + weight +  "&BMR=" + BMR + "&BMI=" + BMI;
                OutputStream os=conn.getOutputStream();
                os.write(postData.getBytes());
                os.flush();
                os.close();

                int responseCode=conn.getResponseCode();
                Log.d("HTTP","Response Code"+responseCode);
                response="Response Code: "+responseCode;
            }catch (Exception e){
                e.printStackTrace();
            }
            return response;
        }
    }


}
