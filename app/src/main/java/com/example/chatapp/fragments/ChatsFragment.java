package com.example.chatapp.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.chatapp.R;
import com.example.chatapp.adapter.UserAdapter;
import com.example.chatapp.model.ChatList;
import com.example.chatapp.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * It displays the recent user chats
 */
public class ChatsFragment extends Fragment {
    private final String DATABASE_URL = "https://chatapp-20368-default-rtdb.europe-west1.firebasedatabase.app";
    private UserAdapter userAdapter;
    private List<User> userList;

    FirebaseUser firebaseUser;
    DatabaseReference reference;

    private List<ChatList> chats;
    RecyclerView recyclerView;

    public ChatsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chats, container, false);
        recyclerView = view.findViewById(R.id.recyclerView_2);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        chats = new ArrayList<>();
        reference = FirebaseDatabase.getInstance(DATABASE_URL).getReference("ChatList").child(firebaseUser.getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chats.clear();
                // Loop for all users
                for (DataSnapshot snap : snapshot.getChildren()){
                    ChatList chatList = snap.getValue(ChatList.class);
                    chats.add(chatList);
                }
                listChat(); 
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return view;
    }

    private void listChat() {
        //Getting all recent chats;
        userList = new ArrayList<>();
        reference = FirebaseDatabase.getInstance(DATABASE_URL).getReference("User");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear();
                for (DataSnapshot snap : snapshot.getChildren()){
                    User user = snap.getValue(User.class);
                    for (ChatList list : chats){
                        if (user.getId().equals(list.getId())){
                            userList.add(user);
                        }
                    }
                }
                userAdapter = new UserAdapter(getContext(), userList, true);
                recyclerView.setAdapter(userAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
}