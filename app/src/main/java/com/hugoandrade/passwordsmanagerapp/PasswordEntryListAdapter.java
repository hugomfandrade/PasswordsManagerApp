package com.hugoandrade.passwordsmanagerapp;

import android.graphics.Color;
import android.os.Parcelable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import android.util.Log;

public class PasswordEntryListAdapter
        extends RecyclerView.Adapter<PasswordEntryListAdapter.ViewHolder>
        implements ItemTouchHelperAdapter {

    @SuppressWarnings("unused")
    private final static String TAG = PasswordEntryListAdapter.class.getSimpleName();

    private static final String HIDDEN_DEFAULT_TEXT = "Password";

    private final List<MPasswordEntry> passwordEntryList;
    private int viewMode = MainActivity.MODE_NONE;

    private OnItemClickListener mOnItemClickListener;
    private OnItemMoveListener mOnItemMoveListener;
    private ItemTouchHelper mItemTouchHelper;
    //private RecyclerView recyclerView;

    public PasswordEntryListAdapter(int viewMode) {
        this(viewMode, new ArrayList<PasswordEntry>());
    }

    public PasswordEntryListAdapter(int viewMode, List<PasswordEntry> passwordEntryList) {
        this.viewMode = viewMode;
        this.passwordEntryList = new ArrayList<>();
        for (PasswordEntry e : passwordEntryList)
            this.passwordEntryList.add(new MPasswordEntry(e, false, false));
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        //this.recyclerView = recyclerView;

        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(this);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(//this.
                recyclerView);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater vi = LayoutInflater.from(parent.getContext());
        return new ViewHolder(vi.inflate(R.layout.list_item_password_entry, parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final MPasswordEntry passwordEntry = passwordEntryList.get(position);

        holder.tvEntryName.setText(passwordEntry.entryName);
        holder.tvAccountName.setText(passwordEntry.accountName);
        holder.etPassword.setText(!passwordEntry.isVisible?
                HIDDEN_DEFAULT_TEXT:
                passwordEntry.password);
        holder.tvAccountName.setVisibility(
                passwordEntry.accountName == null? View.GONE : View.VISIBLE);
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
        holder.vContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemClickListener != null)
                    mOnItemClickListener.onClick(passwordEntry);
            }
        });
        holder.vContainer.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (mItemTouchHelper != null && viewMode == MainActivity.MODE_DEFAULT) {
                    mItemTouchHelper.startDrag(holder);
                }
                if (mOnItemClickListener != null)
                    mOnItemClickListener.onLongClick(passwordEntry);
                return false;
            }
        });

        int color = ContextCompat.getColor(holder.rclContainer.getContext(),
                passwordEntry.isSelected ?
                        R.color.colorPrimaryDark:
                        R.color.colorPrimary);

        holder.rclContainer.setBackgroundColor(color);
    }

    @Override
    public int getItemCount() {
        return passwordEntryList.size();
    }

    public void setAll(List<PasswordEntry> passwordEntryList) {
        this.passwordEntryList.clear();
        for (PasswordEntry e : passwordEntryList)
            this.passwordEntryList.add(new MPasswordEntry(e, false, false));

        notifyDataSetChanged();
    }

    public void resetAll() {
        for (MPasswordEntry e : passwordEntryList)
            e.isSelected = false;

        notifyDataSetChanged();
    }

    public void removeItem(PasswordEntry passwordEntry) {
        for (int i = 0 ; i < passwordEntryList.size() ; i++)
            if (passwordEntryList.get(i) == passwordEntry) {
                passwordEntryList.remove(i);
                notifyItemRemoved(i);
                notifyItemRangeChanged(i, getItemCount());
                break;
            }
    }

    public void removeItems(List<PasswordEntry> passwordEntryList) {
        for (PasswordEntry e : passwordEntryList)
            removeItem(e);
    }

    public void addItem(PasswordEntry passwordEntry) {
        passwordEntryList.add(new MPasswordEntry(passwordEntry, false, false));
        notifyItemInserted(passwordEntryList.size() - 1);

    }

    public void changeItemSelectionStatus(PasswordEntry passwordEntry) {
        for (int i = 0 ; i < passwordEntryList.size() ; i++)
            if (passwordEntryList.get(i) == passwordEntry) {
                passwordEntryList.get(i).isSelected = !passwordEntryList.get(i).isSelected;

                if (viewMode == MainActivity.MODE_DELETE_EDIT)
                    notifyItemChanged(i);

                break;
            }
    }

    public List<PasswordEntry> getSelectedItems() {
        List<PasswordEntry> passwordEntryCheckedList = new ArrayList<>();
        for (MPasswordEntry e : passwordEntryList)
            if (e.isSelected)
                passwordEntryCheckedList.add(e);
        return passwordEntryCheckedList;
    }

    public void updateViewMode(int viewMode) {
        this.viewMode = viewMode;
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        for (int i = 0 ; i < Math.abs(fromPosition - toPosition) ; i++) {
            int multiplier = (fromPosition < toPosition) ? 1 : -1;
            Collections.swap(
                    passwordEntryList,
                    fromPosition + i * multiplier,
                    fromPosition + i * multiplier + multiplier);
            notifyItemMoved(
                    fromPosition + i * multiplier,
                    fromPosition + i * multiplier + multiplier);

        }

        List<PasswordEntry> passwordEntryAffectedList = new ArrayList<>();
        int i = fromPosition + ((fromPosition > toPosition)? 1 : -1);
        do {
            if (fromPosition > toPosition)
                i--;
            else
                i++;
            passwordEntryList.get(i).order = i;
            passwordEntryAffectedList.add(passwordEntryList.get(i));
        }
        while (i != toPosition);
        if (mOnItemMoveListener != null)
            mOnItemMoveListener.onItemMove(passwordEntryAffectedList);
    }

    @Override
    public void onItemDropped(int position) {
        notifyDataSetChanged();
        if (mOnItemMoveListener != null)
            mOnItemMoveListener.onItemDropped();
    }

    @Override
    public void onItemSelected(int position) { }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    public void setOnItemMoveListener(OnItemMoveListener listener) {
        this.mOnItemMoveListener = listener;
    }

    public interface OnItemClickListener {
        void onLongClick(PasswordEntry passwordEntry);
        void onClick(PasswordEntry passwordEntry);
    }
    public interface OnItemMoveListener {
        void onItemMove(List<PasswordEntry> passwordEntryAffectedList);
        void onItemDropped();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements ItemTouchHelperViewHolder {
        TextView tvEntryName, tvAccountName, etPassword;
        ImageView ivPasswordVisibility;
        RoundedCornerLayout rclContainer;
        View vContainer;

        public ViewHolder(View view) {
            super(view);
            this.rclContainer = (RoundedCornerLayout) view.findViewById(R.id.rcl_container);
            this.vContainer = view.findViewById(R.id.ll_inner_container);

            this.tvEntryName = (TextView) view.findViewById(R.id.tv_entry_name);
            this.tvAccountName = (TextView) view.findViewById(R.id.tv_account_name);
            this.etPassword = (TextView) view.findViewById(R.id.tv_password);
            this.ivPasswordVisibility = (ImageView) view.findViewById(R.id.iv_password_visibility);
        }

        @Override
        public void onItemSelected() {
            rclContainer.setBackgroundColor(
                    ContextCompat.getColor(rclContainer.getContext(), R.color.colorPrimaryDark));
        }

        @Override
        public void onItemClear() {
            rclContainer.setBackgroundColor(
                    ContextCompat.getColor(rclContainer.getContext(), R.color.colorPrimary));
        }
    }

    private class MPasswordEntry extends PasswordEntry {
        boolean isVisible, isSelected;

        MPasswordEntry(PasswordEntry passwordEntry, boolean isVisible, boolean isSelected) {
            super(passwordEntry.id,
                    passwordEntry.entryName, passwordEntry.accountName,
                    passwordEntry.password, passwordEntry.order);
            this.isVisible = isVisible;
            this.isSelected = isSelected;
        }
    }
}
