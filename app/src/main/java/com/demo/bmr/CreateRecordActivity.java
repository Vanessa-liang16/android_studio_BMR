package com.demo.bmr;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;

public class CreateRecordActivity extends AppCompatActivity {

    private Button cancel_btn,count_btn;
    EditText name,age,height,weight;
    LinearLayout malelayout,femalelayout;
    ImageView mimg,fimg;


    double h=0,w=0,a=0,bmr=0;
    String user="0";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_record);

        cancel_btn=findViewById(R.id.cancel_btn);
        count_btn=findViewById(R.id.count_btn);
        name=findViewById(R.id.nameInput);
        age=findViewById(R.id.ageInput);
        height=findViewById(R.id.heightInput);
        weight=findViewById(R.id.weightInput);
        malelayout=findViewById(R.id.mlayout);
        femalelayout=findViewById(R.id.felayout);
        mimg=findViewById(R.id.male);
        fimg=findViewById(R.id.female);

        //click image and change color
        malelayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int basecolor2 = ContextCompat.getColor(CreateRecordActivity.this, R.color.purple_700);
                int color2= ColorUtils.setAlphaComponent(basecolor2,30);
                mimg.setColorFilter(color2);
                user="Male";
            }
        });
        femalelayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int basecolor2 = ContextCompat.getColor(CreateRecordActivity.this, R.color.purple_700);
                int color2= ColorUtils.setAlphaComponent(basecolor2,30);
                fimg.setColorFilter(color2);
                user="Female";
            }
        });

        cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {           //change page
                Intent intent=new Intent(CreateRecordActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });
        count_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //input need to be transformed to next page to do caculation
                String nameValue=name.getText().toString();
                String ageValue=age.getText().toString();
                String heightValue=height.getText().toString();
                String weightValue=weight.getText().toString();
                String genderValue=user;
                /*if(user.equals("0")){
                    Toast.makeText(CreateRecordActivity.this, "Select your gender", Toast.LENGTH_SHORT).show();
                }
                else if(TextUtils.isEmpty(nameValue)){
                    height.setError("height is not been record");
                    height.requestFocus();
                    return;
                }*/

                Intent intent=new Intent(CreateRecordActivity.this,resultActivity.class);
                //transfrom data
                intent.putExtra("name",nameValue);
                intent.putExtra("age",ageValue);
                intent.putExtra("height",heightValue);
                intent.putExtra("weight",weightValue);
                intent.putExtra("user",genderValue);

                startActivity(intent);

            }
        });
    }



}