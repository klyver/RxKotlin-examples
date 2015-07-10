package com.example.klyver.kotlinsample;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class UserListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    List<GithubUser> data = new ArrayList<>();
    Context context;

    public UserListAdapter(final Context context, List<GithubUser> data) {
        this.data = data;
        this.context = context;
    }

    public UserListAdapter(final Context context) {
        this.context = context;
    }

    public void setData(List<GithubUser> followers) {
        this.data = followers;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_row_user, parent, false);
        return new UserViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder h, int position) {
        UserViewHolder holder = (UserViewHolder) h;
        final GithubUser user = data.get(position);
        holder.textView.setText(user.getLogin());
        holder.locationTextView.setText(user.getLocation());
        holder.emailTextView.setText(user.getEmail());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), UserDetailActivity.class);
                intent.putExtra(UserDetailActivity.EXTRA_USER_LOGIN, user.getLogin());
                v.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        public TextView textView;
        public TextView locationTextView;
        public TextView emailTextView;
        public UserViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.login_text_view);
            locationTextView = (TextView) itemView.findViewById(R.id.location_text_view);
            emailTextView = (TextView) itemView.findViewById(R.id.email_text_view);
        }
    }




}
