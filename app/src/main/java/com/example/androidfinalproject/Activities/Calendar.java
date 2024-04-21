package com.example.androidfinalproject.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.androidfinalproject.CustomViews.GeneralUtils;
import com.example.androidfinalproject.Fragments.EditWorkerFragment;
import com.example.androidfinalproject.R;
import com.example.androidfinalproject.Utilities.Appointment;
import com.example.androidfinalproject.Utilities.AppointmentAdapter;
import com.example.androidfinalproject.Utilities.Database;
import com.example.androidfinalproject.Utilities.ImageUtils;
import com.example.androidfinalproject.Utilities.User;
import com.example.androidfinalproject.Utilities.UserSession;
import com.google.android.material.textfield.TextInputEditText;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Calendar extends AppCompatActivity implements EditWorkerFragment.OnWorkerInteractionListener {

    private TextView selected_worker_TXTVIW;
    private CalendarView calendar_CLNDR;
    private Button addApp_BTN;
    private LinearLayout addApp_LAYOUT;
    private TextInputEditText name_TEXTINPUT;
    private TextInputEditText time_TEXTINPUT;
    private Button done_BTN;
    private Button delete_BTN;
    private Boolean addAppVisibility = false;
    private RecyclerView appointmentsRecyclerView;
    private AppointmentAdapter appointmentAdapter;
    private AppCompatImageView app_IMG_back;
    private String selectedDate = "";
    boolean userPermission = UserSession.getInstance().getUserPermission();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        ImageUtils.setStatusBarColor(this, R.color.statusBarColor);
        findViews();

        EditWorkerFragment fragment = EditWorkerFragment.newInstance(true);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.editWorker_frag_container, fragment)
                .commit();
    }

    private void findViews(){
        selected_worker_TXTVIW = findViewById(R.id.selected_worker_TXTVIW);
        calendar_CLNDR = findViewById(R.id.calendar_CLNDR);
        addApp_BTN = findViewById(R.id.addApp_BTN);
        if(!userPermission)
            addApp_BTN.setVisibility(View.INVISIBLE);
        else
            addApp_BTN.setVisibility(View.VISIBLE);
        addApp_LAYOUT = findViewById(R.id.addApp_LAYOUT);
        name_TEXTINPUT = findViewById(R.id.name_TEXTINPUT);
        time_TEXTINPUT = findViewById(R.id.time_TEXTINPUT);
        done_BTN = findViewById(R.id.done_BTN);
        delete_BTN = findViewById(R.id.delete_BTN);
        app_IMG_back = findViewById(R.id.app_IMG_back);

        appointmentsRecyclerView = findViewById(R.id.appointmentsRecyclerView);

        appointmentsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        appointmentAdapter = new AppointmentAdapter(new ArrayList<>());
        appointmentsRecyclerView.setAdapter(appointmentAdapter);
        initButtons();
        initButtons();
    }
    void getUserPermission(){
        String email = UserSession.getInstance().getEmail();
        new Database().getUserByEmail(email, new Database.OnUserDataReceivedListener() {
            @Override
            public void onUserDataReceived(User user) {
                if (user != null) {
                    if(user.getStatus().equals("manager") || user.getStatus().equals("worker"))
                        userPermission = true;
                } else {
                    userPermission = false;
                }
            }
            @Override
            public void onError(Exception exception) {
                // Handle the error
            }
        });
    }
    private void initButtons(){
        app_IMG_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Calendar.this, Home.class);
                startActivity(intent);
                finish();
            }
        });
        addApp_BTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(addAppVisibility){
                    addApp_LAYOUT.setVisibility(View.GONE);
                    addAppVisibility = false;
                }
                else{
                    addApp_LAYOUT.setVisibility(View.VISIBLE);
                    addAppVisibility = true;
                }
            }
        });

        done_BTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(addAppVisibility){
                    addApp_LAYOUT.setVisibility(View.GONE);
                    addAppVisibility = false;
                }
                else{
                    addApp_LAYOUT.setVisibility(View.VISIBLE);
                    addAppVisibility = true;
                }
                addAppointmentToDatabase();
                fetchAppointments();
            }
        });

        delete_BTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteAppointmentFromDatabase();
            }
        });

        calendar_CLNDR.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                java.util.Calendar calendar = java.util.Calendar.getInstance();
                calendar.set(year, month, dayOfMonth);
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                selectedDate = dateFormat.format(calendar.getTime());
                fetchAppointments();
            }
        });
    }

    @Override
    public void onWorkerSelected(User user) {
        if (user != null) {
            selected_worker_TXTVIW.setText("Selected Worker: " + user.getName());
            fetchAppointments();
        }
    }

    private void deleteAppointmentFromDatabase() {
        String workerName = selected_worker_TXTVIW.getText().toString().replace("Selected Worker: ", "");
        String clientName = name_TEXTINPUT.getText().toString();
        String time = time_TEXTINPUT.getText().toString();
        String date = getSelectedDateFromCalendar();

        if (workerName.isEmpty() || clientName.isEmpty() || time.isEmpty()) {
            Toast.makeText(this, "All fields must be filled to delete an appointment", Toast.LENGTH_SHORT).show();
            return;
        }
        System.out.println(workerName+" "+clientName+" "+time);
        new Database().deleteAppointment(workerName, date, time, new Database.OnDataStatusListener() {
            @Override
            public void onDataSuccess() {
                Toast.makeText(Calendar.this, "Appointment deleted successfully!", Toast.LENGTH_SHORT).show();
                fetchAppointments();  // Refresh the list after deletion
            }

            @Override
            public void onDataFailure(Exception e) {
                Toast.makeText(Calendar.this, "Failed to delete appointment: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Methods like fetchAppointments and others remain unchanged



    private void fetchAppointments() {
        System.out.println(getSelectedDateFromCalendar());
        String selectedDate = getSelectedDateFromCalendar();
        String workerName = selected_worker_TXTVIW.getText().toString().replace("Selected Worker: ", "");
        if (!workerName.isEmpty()) {
            new Database().getAppointmentsByDateAndWorker(selectedDate, workerName, new Database.OnAppointmentsReceivedListener() {
                @Override
                public void onAppointmentsReceived(List<Appointment> appointments) {
                    appointmentAdapter.setAppointments(appointments);
                    appointmentAdapter.notifyDataSetChanged();
                }

                @Override
                public void onError(Exception e) {
                    Toast.makeText(Calendar.this, "Failed to fetch appointments: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void addAppointmentToDatabase() {
        String workerName = selected_worker_TXTVIW.getText().toString().replace("Selected Worker: ", "");
        String clientName = name_TEXTINPUT.getText().toString();
        String hour = time_TEXTINPUT.getText().toString();
        String selectedDate = getSelectedDateFromCalendar();
        if(workerName.length() == 0){
            GeneralUtils.makeAToast(Calendar.this, "Select a worker");
            addApp_LAYOUT.setVisibility(View.VISIBLE);
            return;
        }
        if(clientName.length() == 0){
            GeneralUtils.makeAToast(Calendar.this, "Enter client's name");
            addApp_LAYOUT.setVisibility(View.VISIBLE);
            return;
        }
        if(!GeneralUtils.isValidTimeFormat(hour)){
            GeneralUtils.makeAToast(Calendar.this, "Enter correct time, format: HH:hh");
            addApp_LAYOUT.setVisibility(View.VISIBLE);
            return;
        }

        // Create an appointment object
        Appointment newAppointment = new Appointment(workerName, clientName, hour, selectedDate);

        // Assuming you have a method in your Database class to add an appointment
        new Database().addAppointment(newAppointment, new Database.OnDataStatusListener() {
            @Override
            public void onDataSuccess() {
                Toast.makeText(Calendar.this, "Appointment added successfully!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDataFailure(Exception e) {
                Toast.makeText(Calendar.this, "Failed to add appointment: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private String getSelectedDateFromCalendar() {
        if (selectedDate.equals("")) {
            java.util.Calendar calendar = java.util.Calendar.getInstance();
            calendar.setTimeInMillis(calendar_CLNDR.getDate());
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            return dateFormat.format(calendar.getTime());
        }else
            return selectedDate;
    }


}