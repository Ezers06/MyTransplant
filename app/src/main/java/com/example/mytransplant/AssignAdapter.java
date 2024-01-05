package com.example.mytransplant;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class AssignAdapter extends BaseAdapter {

    private Context context;
    private List<UserModel> userList;
    private FirebaseFirestore firestore;

    public AssignAdapter(Context context) {
        this.context = context;
        this.userList = new ArrayList<>();
        this.firestore = FirebaseFirestore.getInstance();
    }

    @Override
    public int getCount() {
        return userList.size();
    }

    @Override
    public Object getItem(int position) {
        return userList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_assign, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        // Get the current user
        UserModel user = userList.get(position);

        // Set the data to views with the desired format
        viewHolder.userName.setText("Name: " + user.getName());
        viewHolder.identityCard.setText("ID Card: " + user.getIdentityCard());
        viewHolder.userAddress.setText("Address: " + user.getAddress());
        viewHolder.phoneNumber.setText("Number: " + user.getPhoneNumber());
        viewHolder.bloodType.setText("Blood Type: " + user.getBloodType());
        viewHolder.organType.setText("Organ Type: " + user.getOrganType());

        return convertView;
    }

    // ViewHolder pattern to efficiently reference views
    private static class ViewHolder {
        TextView userName;
        TextView identityCard;
        TextView userAddress;
        TextView phoneNumber;
        TextView bloodType;
        TextView organType;

        ViewHolder(View view) {
            userName = view.findViewById(R.id.userName);
            identityCard = view.findViewById(R.id.identityCard);
            userAddress = view.findViewById(R.id.userAddress);
            phoneNumber = view.findViewById(R.id.phoneNumber);
            bloodType = view.findViewById(R.id.bloodType);
            organType = view.findViewById(R.id.organType);
        }
    }

    // Method to update the adapter with new data
    public void updateData(List<UserModel> newData) {
        userList.clear();
        userList.addAll(newData);
        notifyDataSetChanged();
    }
}
