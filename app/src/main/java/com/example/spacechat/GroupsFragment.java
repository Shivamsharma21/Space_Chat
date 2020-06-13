package com.example.spacechat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;


/**
 * A simple {@link Fragment} subclass.
 */
public class GroupsFragment extends Fragment {

    private View groupFregmentView;
    private DatabaseReference databaseReference;
    private ListView listView;

    private ArrayList<String> list_of_group = new ArrayList<>();
    private ArrayAdapter<String> arrayAdapter;

    public GroupsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        groupFregmentView = inflater.inflate(R.layout.fragment_groups, container, false);

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Groups");
        InitilizedField();
        RetriveGroupData();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                  String GroupName = parent.getItemAtPosition(position).toString();
                  Intent groupintent= new Intent(getContext(),Group_Chat_Activity.class);
                  groupintent.putExtra("group name",GroupName);
                  startActivity(groupintent);
            }
        });
        return groupFregmentView;
    }

    private void RetriveGroupData() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Set<String> values = new HashSet<>();
                Iterator iterator = (Iterator) dataSnapshot.getChildren().iterator();

                while (iterator.hasNext()) {
                    values.add(((DataSnapshot) iterator.next()).getKey());
                }
                list_of_group.clear();
                list_of_group.addAll(values);
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("Error:", "Not Getting the Data");
            }
        });
    }

    private void InitilizedField() {

        listView = (ListView) groupFregmentView.findViewById(R.id.list_view);
        arrayAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, list_of_group);
        listView.setAdapter(arrayAdapter);

    }

}