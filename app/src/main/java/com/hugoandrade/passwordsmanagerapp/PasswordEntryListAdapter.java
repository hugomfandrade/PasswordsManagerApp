package com.hugoandrade.passwordsmanagerapp;

import android.support.v7.widget.RecyclerView;
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
import java.util.List;

public class PasswordEntryListAdapter extends RecyclerView.Adapter<PasswordEntryListAdapter.ViewHolder> {

    @SuppressWarnings("unused")
    private final static String TAG = PasswordEntryListAdapter.class.getSimpleName();

    private final List<MPasswordEntry> passwordEntryList;
    private int viewMode;

    private OnLongClickListener mOnLongClickListener;
    private OnClickListener mOnClickListener;

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
        holder.checkBoxSelected.setChecked(passwordEntry.isSelected);
        holder.llContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnClickListener != null)
                    mOnClickListener.onClick(passwordEntry);
            }
        });
        holder.llContainer.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (mOnLongClickListener != null)
                    mOnLongClickListener.onLongClick(passwordEntry);
                return false;
            }
        });
        if (viewMode == MainActivity.MODE_DELETE_EDIT) {
            holder.vgCheckBox.setVisibility(View.VISIBLE);
            if (passwordEntry.viewMode != viewMode)
                holder.vgPasswordEntry.startAnimation(
                        new MyAnimator(holder.checkBoxSelected, holder.vgPasswordEntry, 0, 60, 300L));

            else
                holder.vgPasswordEntry.setPadding(
                        (int) Options.fromDpToPixel(holder.vgPasswordEntry.getContext(), 60), 0, 0, 0);

        }
        else if (viewMode == MainActivity.MODE_MAIN){
            holder.vgCheckBox.setVisibility(View.INVISIBLE);
            if (passwordEntry.viewMode != viewMode)
                holder.vgPasswordEntry.startAnimation(
                        new MyAnimator(holder.checkBoxSelected, holder.vgPasswordEntry, 60, 0, 300L));
            else
                holder.vgPasswordEntry.setPadding(0, 0, 0, 0);
        }
        passwordEntry.viewMode = viewMode;
    }

    public void setOnLongClickListener(OnLongClickListener listener) {
        this.mOnLongClickListener = listener;
    }

    public void setOnClickListener(OnClickListener listener) {
        this.mOnClickListener = listener;
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

    public void updateViewMode(int viewMode) {
        this.viewMode = viewMode;
        for (MPasswordEntry e : passwordEntryList)
            e.isSelected = false;

        notifyDataSetChanged();
    }

    public void setItemChecked(PasswordEntry passwordEntry) {
        for (int i = 0 ; i < passwordEntryList.size() ; i++)
            if (passwordEntryList.get(i) == passwordEntry) {
                passwordEntryList.get(i).isSelected = !passwordEntryList.get(i).isSelected;
                notifyItemChanged(i);
                break;
            }
    }

    public List<PasswordEntry> getItemsChecked() {
        List<PasswordEntry> passwordEntryCheckedList = new ArrayList<>();
        for (MPasswordEntry e : passwordEntryList)
            if (e.isSelected)
                passwordEntryCheckedList.add(e);
        return passwordEntryCheckedList;
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

    public interface OnLongClickListener {
        void onLongClick(PasswordEntry passwordEntry);
    }
    public interface OnClickListener {
        void onClick(PasswordEntry passwordEntry);
    }

    private class MPasswordEntry extends PasswordEntry {

        public int viewMode = -1;
        public boolean isVisible, isSelected;
        public MPasswordEntry(PasswordEntry passwordEntry, boolean isVisible, boolean isSelected) {
            super(passwordEntry.id, passwordEntry.accountName, passwordEntry.password);
            this.isVisible = isVisible;
            this.isSelected = isSelected;
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvAccountName, etPassword;
        ImageView ivPasswordVisibility;
        CheckBox checkBoxSelected;
        View llContainer;
        View vgCheckBox;
        View vgPasswordEntry;

        public ViewHolder(View view) {
            super(view);
            this.llContainer = view.findViewById(R.id.ll_container);
            this.vgCheckBox = view.findViewById(R.id.vg_checkBox);
            this.vgPasswordEntry = view.findViewById(R.id.vg_password_entry);

            this.tvAccountName = (TextView) view.findViewById(R.id.tv_account_name);
            this.etPassword = (TextView) view.findViewById(R.id.tv_password);
            this.ivPasswordVisibility = (ImageView) view.findViewById(R.id.iv_password_visibility);
            this.checkBoxSelected = (CheckBox) view.findViewById(R.id.checkbox_selected);
        }
    }

    private class MyAnimator extends Animation {

        // The views to be animated
        private View checkBoxView, viewGroupPasswordEntry;

        // The dimensions and animation values (this is stored so other changes to layout don't interfere)
        private final int fromDimension; // Dimension to animate from
        private final int toDimension; // Dimension to animate to

        // Constructor
        MyAnimator(View checkBoxView, View viewGroupPasswordEntry, int fromDimension, int toDimension, long duration) {
            // Setup references
            // the view to animate
            this.checkBoxView = checkBoxView;
            this.viewGroupPasswordEntry = viewGroupPasswordEntry;
            // Get the current starting point of the animation (the current width or height of the provided view)
            this.fromDimension = (int) Options.fromDpToPixel(checkBoxView.getContext(), fromDimension);
            this.toDimension = (int) Options.fromDpToPixel(checkBoxView.getContext(), toDimension);
            // See enum above, the type of animation
            // Set the duration of the animation
            this.setDuration(duration);
        }

        @Override
        public void applyTransformation(float interpolatedTime, Transformation t) {
            // Used to apply the animation to the view
            // Animate given the height or width
            checkBoxView.setTranslationX(
                    -(toDimension + (int) ((fromDimension - toDimension) * interpolatedTime)));

            viewGroupPasswordEntry.setPadding(
                    fromDimension + (int) ((toDimension - fromDimension) * interpolatedTime),
                    0, 0, 0);

            // Ensure the views are measured appropriately
            checkBoxView.requestLayout();
            viewGroupPasswordEntry.requestLayout();
        }
    }
}
