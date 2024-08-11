package com.demo.bmr;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Button create_btn;
    private Button modify_btn;
    private ListView listView;
    private UserAdapter userAdapter;
    private List<User> userList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        create_btn=findViewById(R.id.create_btn);
        listView=findViewById(R.id.listview);
        userAdapter = new UserAdapter(this, userList);
        listView.setAdapter(userAdapter);

        // Fetch data from the server
        new FetchDataTask().execute("http://10.0.2.2/GetData.php"); // Use 10.0.2.2 for localhost in emulator

        create_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {           //change page
                Intent intent=new Intent(MainActivity.this,CreateRecordActivity.class);
                startActivity(intent);
            }
        });

    }
    private class FetchDataTask extends AsyncTask<String,Void,String>{
        @Override
        protected String doInBackground(String...urls){
            String result = "";
            HttpURLConnection urlConnection = null;
            try{
                URL url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = urlConnection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
                result = sb.toString();
            }catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }
            return result;
        }
        protected  void onPostExecute(String result){
            try{
                JSONArray jsonArray=new JSONArray(result);
                userList.clear();
                for(int i=0;i< jsonArray.length();i++){
                    JSONObject jsonObject=jsonArray.getJSONObject(i);
                    String name=jsonObject.getString("name");
                    String age=jsonObject.getString("age");
                    String height=jsonObject.getString("height");
                    String weight=jsonObject.getString("weight");
                    String BMR=jsonObject.getString("BMR");
                    String BMI=jsonObject.getString("BMI");
                    userList.add(new User(name,age,height,weight,BMR,BMI));
                }
                userAdapter.notifyDataSetChanged();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    public class User{
        private String name,age,height,weight,BMR,BMI;
        public User(String name,String age,String height,String weight,String BMR,String BMI){
            this.name=name;
            this.age=age;
            this.height=height;
            this.weight=weight;
            this.BMR=BMR;
            this.BMI=BMI;
        }
        public String getName(){
            return name;
        }
        public String getAge(){
            return age;
        }
        public String getHeight(){
            return height;
        }
        public String getWeight(){
            return weight;
        }
        public String getBMR(){
            return BMR;
        }
        public String getBMI(){
            return BMI;
        }
    }
    public class UserAdapter extends ArrayAdapter<User>{
        public UserAdapter(Context context, List<User> users){
            super(context,0,users);

        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent){
            if(convertView==null){
                convertView= LayoutInflater.from(getContext()).inflate(R.layout.list_item_user,parent,false);
            }
            // Get the data item for this position
            User user = getItem(position);
            // Lookup view for data population
            TextView nameTextView = convertView.findViewById(R.id.name);
            TextView ageTextView = convertView.findViewById(R.id.age);
            TextView weightTextView = convertView.findViewById(R.id.weight);
            TextView heightTextView = convertView.findViewById(R.id.height);
            TextView bmrTextView = convertView.findViewById(R.id.bmr);
            TextView bmiTextView = convertView.findViewById(R.id.bmi);
            Button delete_btn=convertView.findViewById(R.id.delete_btn);
            Button modify_btn=convertView.findViewById(R.id.modify_btn);

            // Populate the data into the template view using the data object
            nameTextView.setText(user.getName());
            ageTextView.setText("Age: "+user.getAge());
            heightTextView.setText("Height: "+user.getHeight());
            weightTextView.setText("Weight: "+user.getWeight());
            bmrTextView.setText("BMR: "+user.getBMR());
            bmiTextView.setText("BMI: "+user.getBMI());

            //delete record onclick event
            delete_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new DeleteUserTask().execute(user.getName());
                }
            });
            //modify record onclick event
            modify_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getContext(), ModifyActivity.class);
                    intent.putExtra("name", user.getName());
                    intent.putExtra("age", user.getAge());
                    intent.putExtra("height", user.getHeight());
                    intent.putExtra("weight", user.getWeight());
                    intent.putExtra("BMR", user.getBMR());
                    intent.putExtra("BMI", user.getBMI());
                    getContext().startActivity(intent);
                }
            });

            // Return the completed view to render on screen
            return convertView;
        }
    }
    //deal with delete details
    private class DeleteUserTask extends AsyncTask<String,Void,String>{
        @Override
        protected String doInBackground(String...names){
            String result = "";
            HttpURLConnection urlConnection = null;
            try{
                URL url = new URL("http://10.0.2.2/DeleteData.php?name=" + names[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                InputStream inputStream = urlConnection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
                result = sb.toString();
            }catch (Exception e){
                e.printStackTrace();
            }finally {
                if(urlConnection!=null){
                    urlConnection.disconnect();
                }
            }
            return result;
        }
        @Override
        protected void onPostExecute(String result){
            new FetchDataTask().execute("http://10.0.2.2/GetData.php");
        }
    }

    //deal with modify detils
    public class EditRecordActivity extends AppCompatActivity{
        private EditText nameEditText, ageEditText, heightEditText, weightEditText;
        private Button saveChangesBtn;
        private String recordId;
        private LinearLayout femaleModify;
        ImageView mimgModify,fimgModify;

        @Override
        protected void onCreate(Bundle savedInstanceState){
            super.onCreate(savedInstanceState);
            setContentView(R.layout.modify);

            nameEditText=findViewById(R.id.nameModify);
            ageEditText=findViewById(R.id.ageModify);
            heightEditText=findViewById(R.id.heightModify);
            weightEditText=findViewById(R.id.weightModify);
            femaleModify=findViewById(R.id.felayout_modify);
            fimgModify=findViewById(R.id.femaleModify);
            saveChangesBtn=findViewById(R.id.save_change_btn);

            //get data from intent
            Intent intent=getIntent();
            recordId=intent.getStringExtra("recordId");
            String name = intent.getStringExtra("name");
            String age = intent.getStringExtra("age");
            String height = intent.getStringExtra("height");
            String weight = intent.getStringExtra("weight");
            String BMR = intent.getStringExtra("BMR");
            String BMI = intent.getStringExtra("BMI");

            //set data to edit text
            nameEditText.setText(name);
            ageEditText.setText(age);
            heightEditText.setText(height);
            weightEditText.setText(weight);

            saveChangesBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String updatedName = nameEditText.getText().toString();
                    String updatedAge = ageEditText.getText().toString();
                    String updatedHeight = heightEditText.getText().toString();
                    String updatedWeight = weightEditText.getText().toString();
                    double updateAgeDouble=Double.parseDouble(updatedAge);
                    double updateHeightDouble=Double.parseDouble(updatedHeight);
                    double updateWeightDouble=Double.parseDouble(updatedWeight);
                    double updatedBmr = (10*(updateWeightDouble))+(6.25*updateHeightDouble)-(5*updateAgeDouble)+161;
                    double updatedBmi = updateWeightDouble/((updateHeightDouble/100)*(updateHeightDouble/100));

                    new UpdateDataTask(recordId, updatedName, updatedAge, updatedHeight, updatedWeight, updatedBmr, updatedBmi).execute();
                }
            });

        }
        private class UpdateDataTask extends AsyncTask<Void,Void,String>{
            private String id, name, age, height, weight;
            private double BMI, BMR;

            public UpdateDataTask(String id, String name, String age, String height, String weight, double BMI, double BMR) {
                this.id = id;
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
                    URL url = new URL("http://10.0.2.2/update_data.php");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setDoOutput(true);
                    String postData = "id=" + id + "&name=" + name + "&age=" + age + "&height=" + height + "&weight=" + weight + "&BMR=" + BMR + "&BMI=" + BMI;
                    OutputStream os = conn.getOutputStream();
                    os.write(postData.getBytes());
                    os.flush();
                    os.close();

                    int responseCode = conn.getResponseCode();
                    Log.d("HTTP", "Response Code: " + responseCode);
                    response = "Response Code: " + responseCode;

                }catch(Exception e){
                    e.printStackTrace();
                }
                return response;
            }
            @Override
            protected void onPostExecute(String result){
                Intent intent=new Intent(EditRecordActivity.this,ModifyActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        }
    }

}
