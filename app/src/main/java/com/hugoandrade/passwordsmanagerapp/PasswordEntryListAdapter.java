package com.hugoandrade.passwordsmanagerapp;

import android.support.v7.widget.RecyclerView;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class PasswordEntryListAdapter extends RecyclerView.Adapter<PasswordEntryListAdapter.ViewHolder> {

    @SuppressWarnings("unused")
    private final static String TAG = PasswordEntryListAdapter.class.getSimpleName();

    private final List<MPasswordEntry> passwordEntryList;

    public PasswordEntryListAdapter() {
        this(new ArrayList<PasswordEntry>());
    }

    public PasswordEntryListAdapter(List<PasswordEntry> passwordEntryList) {
        this.passwordEntryList = new ArrayList<>();
        for (PasswordEntry e : passwordEntryList)
            this.passwordEntryList.add(new MPasswordEntry(e, false));
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater vi = LayoutInflater.from(parent.getContext());
        return new ViewHolder(vi.inflate(R.layout.list_item_password_entry, parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final MPasswordEntry passwordEntry = passwordEntryList.get(position);
        holder.tvAccountName.setText(passwordEntry.accountName);
        holder.etPassword.setText(passwordEntry.password);
        holder.etPassword.setTransformationMethod(!passwordEntry.isVisible?
                PasswordTransformationMethod.getInstance() :
                HideReturnsTransformationMethod.getInstance());
        holder.ivPasswordVisibility.setImageResource(!passwordEntry.isVisible?
                R.drawable.ic_password_visibility :
                R.drawable.ic_password_visibility_off);
        holder.ivPasswordVisibility.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                passwordEntry.isVisible = !passwordEntry.isVisible;
                notifyItemChanged(holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return passwordEntryList.size();
    }

    public void setAll(List<PasswordEntry> passwordEntryList) {
        this.passwordEntryList.clear();
        for (PasswordEntry e : passwordEntryList)
            this.passwordEntryList.add(new MPasswordEntry(e, false));
        notifyDataSetChanged();
    }

    private class MPasswordEntry extends PasswordEntry {

        public boolean isVisible;
        public MPasswordEntry(PasswordEntry passwordEntry, boolean isVisible) {
            super(passwordEntry.id, passwordEntry.accountName, passwordEntry.password);
            this.isVisible = isVisible;
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvAccountName, etPassword;
        ImageView ivPasswordVisibility;

        public ViewHolder(View view) {
            super(view);
            this.tvAccountName = (TextView) view.findViewById(R.id.tv_account_name);
            this.etPassword = (TextView) view.findViewById(R.id.tv_password);
            this.ivPasswordVisibility = (ImageView) view.findViewById(R.id.iv_password_visibility);
        }
    }
}
