package com.example.androidfinalproject.Utilities;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Database {

    private DatabaseReference db;
    private boolean found = false;

    public Database() {
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://barberandroidfinal-default-rtdb.europe-west1.firebasedatabase.app");
        db = database.getReference();
     }

    // Updated method to add user details to Firebase Realtime Database
    public void addUserToRealtimeDatabase(String userId, String email, String status, String name) {
        Map<String, Object> userData = new HashMap<>();
        userData.put("name", name);  // Add name to the user data
        userData.put("email", email);
        userData.put("status", status);

        db.child("users").child(userId).setValue(userData)
                .addOnSuccessListener(aVoid -> {
                    System.out.println("User added to Realtime Database successfully");
                })
                .addOnFailureListener(e -> {
                    System.err.println("Error adding user to Realtime Database: " + e.getMessage());
                });
    }

    public void getUserByEmail(String email, final OnUserDataReceivedListener listener) {
        db.child("users").orderByChild("email").equalTo(email)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            // Assuming each email is unique, there will only be one child
                            for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                                // Now you have the user's data in userSnapshot
                                User user = userSnapshot.getValue(User.class);
                                listener.onUserDataReceived(user);
                            }
                        } else {
                            listener.onUserDataReceived(null);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        listener.onError(databaseError.toException());
                    }
                });
    }

    public void getAppointmentsByDate(String date, OnAppointmentsReceivedListener listener) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("appointments");
        Query query = ref.orderByChild("date").equalTo(date);  // Query for appointments on the specified date
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Appointment> appointments = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Appointment appointment = snapshot.getValue(Appointment.class);
                    if (appointment != null) {
                        appointments.add(appointment);  // Add to the list if the appointment exists
                    }
                }
                listener.onAppointmentsReceived(appointments);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                listener.onError(databaseError.toException());
            }
        });
    }
    public void fetchUsersByStatus(List<String> statuses, OnUsersReceivedListener listener) {
        Query query = db.child("users").orderByChild("status").startAt("manager").endAt("worker\uf8ff");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<User> users = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User user = snapshot.getValue(User.class);
                    if (user != null && (user.getStatus().equals("worker") || user.getStatus().equals("manager"))) {
                        users.add(user);
                    }
                }
                listener.onUsersReceived(users);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                listener.onError(databaseError.toException());
            }
        });
    }

    public interface OnUserDataReceivedListener {
        void onUserDataReceived(User user);
        void onError(Exception exception);
    }

    // Callback interface for delivering a single user
    public interface OnUsersReceivedListener {
        void onUsersReceived(List<User> users);
        void onError(Exception exception);
    }

    public void changeStatus(String email, String newStatus, final OnUserStatusChangedListener listener) {
        // First, we query the database to find the user by email
        db.child("users").orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Loop through the snapshot to find the user's key
                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                        // Get the user's key
                        String userKey = userSnapshot.getKey();
                        if (userKey != null) {
                            // Create a map for the update with the new status
                            Map<String, Object> statusUpdate = new HashMap<>();
                            statusUpdate.put("status", newStatus);
                            // Update the user's status using their key
                            db.child("users").child(userKey).updateChildren(statusUpdate).addOnSuccessListener(aVoid -> {
                                User updatedUser = userSnapshot.getValue(User.class);
                                if (updatedUser != null) {
                                    updatedUser.setStatus(newStatus); // Make sure to set the new status
                                    listener.onStatusChanged(updatedUser);
                                }
                            }).addOnFailureListener(e -> {
                                listener.onError(e);
                            });
                        }
                    }
                } else {
                    listener.onError(new Exception("No user found with the email: " + email));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                listener.onError(databaseError.toException());
            }
        });
    }

    // The listener interface
    public interface OnUserStatusChangedListener {
        void onStatusChanged(User user);
        void onError(Exception exception);
    }

    public void addAppointment(Appointment appointment, OnDataStatusListener listener) {
        // Create a unique key for the appointment
        String key = db.child("appointments").push().getKey();
        if (key == null) {
            listener.onDataFailure(new Exception("Failed to generate a unique key for the appointment."));
            return;
        }

        db.child("appointments").child(key).setValue(appointment)
                .addOnSuccessListener(aVoid -> listener.onDataSuccess())
                .addOnFailureListener(listener::onDataFailure);
    }

    public interface OnDataStatusListener {
        void onDataSuccess();
        void onDataFailure(Exception e);
    }

    public void getAppointmentsByDateAndWorker(String date, String workerName, OnAppointmentsReceivedListener listener) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("appointments");
        Query query = ref.orderByChild("date").equalTo(date);  // Query for the date first
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Appointment> appointments = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Appointment appointment = snapshot.getValue(Appointment.class);
                    if (appointment != null && workerName.equals(appointment.getWorkerName())) {
                        appointments.add(appointment);  // Add only if the worker matches
                    }
                }
                listener.onAppointmentsReceived(appointments);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                listener.onError(databaseError.toException());
            }
        });
    }

    public interface OnAppointmentsReceivedListener {
        void onAppointmentsReceived(List<Appointment> appointments);
        void onError(Exception exception);
    }

    public void deleteAppointment(String workerName, String date, String time, OnDataStatusListener listener) {
        DatabaseReference appointmentsRef = db.child("appointments");

        // Fetch all appointments
        appointmentsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                found = false;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Appointment appointment = snapshot.getValue(Appointment.class);
                    if (appointment != null && appointment.getWorkerName().equals(workerName) &&
                            appointment.getDate().equals(date) && appointment.getTime().equals(time)) {
                        // Delete the matched appointment
                        snapshot.getRef().removeValue()
                                .addOnSuccessListener(aVoid -> {
                                    listener.onDataSuccess();  // Notify success
                                    found = true;
                                })
                                .addOnFailureListener(e -> listener.onDataFailure(e));  // Notify failure
                        break;
                    }
                }
                if (!found) {
                    listener.onDataFailure(new Exception("No matching appointment found to delete."));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                listener.onDataFailure(databaseError.toException());
            }
        });

    }
    public interface OnAppointmentOperationListener {
        void onOperationSuccess();
        void onOperationFailure(Exception e);
    }
}
