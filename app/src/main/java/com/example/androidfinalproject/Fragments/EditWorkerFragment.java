package com.example.androidfinalproject.Fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.widget.ImageViewCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.androidfinalproject.Activities.Login;
import com.example.androidfinalproject.CustomViews.GeneralUtils;
import com.example.androidfinalproject.R;
import com.example.androidfinalproject.Utilities.Database;
import com.example.androidfinalproject.Utilities.User;
import com.example.androidfinalproject.Utilities.UserAdapter;
import com.example.androidfinalproject.Utilities.UserSession;
import com.google.android.material.textfield.TextInputEditText;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EditWorkerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EditWorkerFragment extends Fragment {
    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
    private List<User> userList = new ArrayList<>();  // Initialize the userList
    private TextInputEditText enterEmail_TEXTINPUT;
    private AppCompatImageView app_IMG_addWorker;
    private AppCompatImageView app_IMG_removeWorker;
    private int index;
    public interface OnWorkerInteractionListener {
        void onWorkerSelected(User user);
    }

    private OnWorkerInteractionListener mListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // Ensure that the host activity implements the callback interface
        try {
            mListener = (OnWorkerInteractionListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnWorkerInteractionListener");
        }
    }
    public static EditWorkerFragment newInstance(boolean fromCalendar) {
        EditWorkerFragment fragment = new EditWorkerFragment();
        Bundle args = new Bundle();
        args.putBoolean("fromCalendar", fromCalendar);
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_worker, container, false);
        recyclerView = view.findViewById(R.id.recyclerView); // make sure you have a RecyclerView with an id in your layout
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        enterEmail_TEXTINPUT = view.findViewById(R.id.enterEmail_TEXTINPUT);
        app_IMG_addWorker = view.findViewById(R.id.app_IMG_addWorker);
        app_IMG_removeWorker = view.findViewById(R.id.app_IMG_removeWorker);

        // Initialize the adapter with the empty list
        setOnClicks();


        userAdapter = new UserAdapter(userList, user -> {
            Toast.makeText(getActivity(), "User clicked: " + user.getName(), Toast.LENGTH_SHORT).show();
            mListener.onWorkerSelected(user);  // Notify the activity that a worker has been selected
        });
        recyclerView.setAdapter(userAdapter);
        Bundle args = getArguments();

        if (args != null && args.getBoolean("fromCalendar", false)) {
            enterEmail_TEXTINPUT.setVisibility(View.GONE);
            app_IMG_addWorker.setVisibility(View.GONE);
            app_IMG_removeWorker.setVisibility(View.GONE);
        }

        loadUsers();  // Load users from database
        return view;
    }
    private void handleUserClick(User user) {
        // Example of handling a click; could be navigating to another fragment, etc.
        Toast.makeText(getActivity(), "Handling click for: " + user.getName(), Toast.LENGTH_SHORT).show();
        // Implement additional logic, such as updating UI components or performing transitions
    }

    private void setOnClicks(){
        app_IMG_addWorker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = enterEmail_TEXTINPUT.getText().toString();
                new Database().getUserByEmail(email, new Database.OnUserDataReceivedListener() {
                    @Override
                    public void onUserDataReceived(User user) {
                        if (user != null) {
                            if(!user.getStatus().equals("manager") && !user.getStatus().equals("worker")) {
                                attemptStatusChange(email, "worker");
                            }else
                                GeneralUtils.makeAToast(getActivity(), "User is already a worker.");
                        } else {
                            GeneralUtils.makeAToast(getActivity(), "Email not found.");
                        }
                    }
                    @Override
                    public void onError(Exception exception) {
                        // Handle the error
                    }
                });
            }
        });

        app_IMG_removeWorker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = enterEmail_TEXTINPUT.getText().toString();
                new Database().getUserByEmail(email, new Database.OnUserDataReceivedListener() {
                    @Override
                    public void onUserDataReceived(User user) {
                        if (user != null) {
                            attemptStatusChange(email, "user");
                            // HERE CHANGE USER STATUS
                        } else {
                            GeneralUtils.makeAToast(getActivity(), "Email not found.");
                        }
                    }
                    @Override
                    public void onError(Exception exception) {
                        // Handle the error
                    }
                });
            }
        });
    }

    // Inside your fragment when you want to change a user's status
    private void attemptStatusChange(String email, String newStatus) {
        Database database = new Database();
        database.changeStatus(email, newStatus, new Database.OnUserStatusChangedListener() {
            @Override
            public void onStatusChanged(User user) {
                // Determine the index of the user in the list
                index = -1;
                for (int i = 0; i < userList.size(); i++) {
                    if (userList.get(i).getEmail().equals(user.getEmail())) {
                        index = i;
                        break;
                    }
                }

                // If changing to "worker" and the user is not in the list
                if ("worker".equals(newStatus) && index == -1) {
                    userList.add(user);  // Add the user to the list
                    getActivity().runOnUiThread(() -> {
                        userAdapter.notifyItemInserted(userList.size() - 1);  // Notify the adapter of the new item
                        UserSession.getInstance().setWorkers(userList);
                        GeneralUtils.makeAToast(getActivity(), "Added " + user.getName() + " as worker.");
                    });
                }
                // If changing from "worker" to something else and the user is in the list
                else if (!"worker".equals(newStatus) && index != -1) {
                    userList.remove(index);  // Remove the user from the list
                    getActivity().runOnUiThread(() -> {
                        userAdapter.notifyItemRemoved(index);  // Notify the adapter of the removal
                        UserSession.getInstance().setWorkers(userList);
                        GeneralUtils.makeAToast(getActivity(), "Removed " + user.getName() + " from workers.");
                    });
                }
            }

            @Override
            public void onError(Exception exception) {
                // There was an error during the status change process. Update UI or notify the user.
                getActivity().runOnUiThread(() -> {
                    GeneralUtils.makeAToast(getActivity(), "Failed to update status: " + exception.getMessage());
                });
            }
        });
    }

    private void loadUsers() {
        Database database = new Database();
        database.fetchUsersByStatus(Arrays.asList("worker", "manager"), new Database.OnUsersReceivedListener() {
            @Override
            public void onUsersReceived(List<User> users) {
                Log.d("UserData", "Number of users fetched: " + users.size());
                userList.clear();  // Clear any existing data
                userList.addAll(users);  // Add all fetched users
                getActivity().runOnUiThread(() -> userAdapter.notifyDataSetChanged());  // Update on UI thread
            }

            @Override
            public void onError(Exception exception) {
                // Log or handle the error appropriately
            }
        });
    }
}