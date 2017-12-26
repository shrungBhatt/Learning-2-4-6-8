package com.example.android.learning2_4_6_8;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class AddTaskActivity extends AppCompatActivity {

    private static final String TAG = "AddTaskActivity";
    private EditText mAddTaskHeaderEditText;
    private EditText mAddTaskContentEditText;
    private FloatingActionButton mAddTaskFabButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_tasks);


        mAddTaskHeaderEditText = findViewById(R.id.add_task_header_edit_text);

        mAddTaskContentEditText = findViewById(R.id.add_task_content_edit_text);

        mAddTaskFabButton = findViewById(R.id.add_task_fab_button);
        mAddTaskFabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //All the function when addTaskButton is pressed.
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
                String startDate = simpleDateFormat.format(new Date()).trim();

                Calendar calendar = Calendar.getInstance();
                try {
                    calendar.setTime(simpleDateFormat.parse(startDate));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                calendar.add(Calendar.DATE, 2);
                String endDate = simpleDateFormat.format(calendar.getTime()).trim();

                int daysCounter = 1;

                String taskHeader = mAddTaskHeaderEditText.getText().toString().trim();

                String taskContent = mAddTaskContentEditText.getText().toString().trim();

                addTask(startDate, endDate, taskHeader, taskContent, String.valueOf(daysCounter)
                        .trim());

            }
        });
    }


    void addTask(final String startDate, final String endDate, final String taskHeader,
                 final String taskContent, final String repCounter) {

        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                "http://ersnexus.esy.es/task_data.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response.equals("Insert SuccessFul")) {
                            Log.e(TAG, "Task data insertion successful");
                            Toast.makeText(getApplicationContext(), "Insertion Successful",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "Error in submitting the request: " + error.toString());
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("start_date", startDate);
                params.put("end_date", endDate);
                params.put("task_content", taskContent);
                params.put("task_header",taskHeader);
                params.put("rep_counter", repCounter);
                return params;
            }
        };


        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }

}
