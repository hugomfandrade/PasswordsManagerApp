package com.hugoandrade.passwordsmanagerapp;

import android.graphics.Color;
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
    private RecyclerView recyclerView;

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
        this.recyclerView = recyclerView;

        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(this);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(this.recyclerView);
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
        holder.etPassword.setText(!passwordEntry.isVisible?
                HIDDEN_DEFAULT_TEXT:
                passwordEntry.password);
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
                if (mOnItemClickListener != null)
                    mOnItemClickListener.onClick(passwordEntry);
            }
        });
        holder.llContainer.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (mItemTouchHelper != null && viewMode == MainActivity.MODE_DEFAULT)
                    mItemTouchHelper.startDrag(holder);
                if (mOnItemClickListener != null)
                    mOnItemClickListener.onLongClick(passwordEntry);
                return false;
            }
        });
        holder.vgPasswordEntry.clearAnimation();
        if (viewMode == MainActivity.MODE_DELETE_EDIT) {
            holder.vgCheckBox.setVisibility(View.VISIBLE);
            holder.vgPasswordEntry.setPadding(
                    (int) Options.fromDpToPixel(holder.vgPasswordEntry.getContext(), 60), 0, 0, 0);

        }
        else
        {
            holder.vgCheckBox.setVisibility(View.INVISIBLE);
            holder.vgPasswordEntry.setPadding(0, 0, 0, 0);
        }
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

    public void setItemChecked(PasswordEntry passwordEntry) {
        for (int i = 0 ; i < passwordEntryList.size() ; i++)
            if (passwordEntryList.get(i) == passwordEntry) {
                passwordEntryList.get(i).isSelected = !passwordEntryList.get(i).isSelected;
                ViewHolder holder = ((ViewHolder) recyclerView.findViewHolderForAdapterPosition(i));
                holder.checkBoxSelected.setChecked(passwordEntryList.get(i).isSelected);
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

    public void updateViewMode(int viewMode) {
        int prevViewMode = this.viewMode;
        this.viewMode = viewMode;

        switch (this.viewMode) {
            case MainActivity.MODE_DEFAULT:
                for (MPasswordEntry e : passwordEntryList)
                    e.isSelected = false;

                for (int i = 0; i < recyclerView.getChildCount(); i++) {
                    final ViewHolder holder = (ViewHolder) recyclerView.getChildViewHolder(recyclerView.getChildAt(i));
                    if (holder != null && holder.getAdapterPosition() != -1) {
                        MPasswordEntry e = passwordEntryList.get(holder.getAdapterPosition());
                        holder.vgCheckBox.setVisibility(View.INVISIBLE);
                        holder.checkBoxSelected.setChecked(e.isSelected);
                        if (prevViewMode == MainActivity.MODE_DELETE_EDIT)
                            holder.vgPasswordEntry.startAnimation(
                                    new MyAnimator(holder.checkBoxSelected, holder.vgPasswordEntry, 60, 0, 300L));
                        else
                            holder.vgPasswordEntry.setPadding(0, 0, 0, 0);
                    }
                }
                android.os.Handler h = new android.os.Handler();
                h.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        notifyDataSetChanged();
                    }
                }, 300L);

                break;
            case MainActivity.MODE_DELETE_EDIT:
                for (MPasswordEntry e : passwordEntryList)
                    e.isSelected = false;

                for (int i = 0; i < recyclerView.getChildCount(); i++) {
                    final ViewHolder holder = (ViewHolder) recyclerView.getChildViewHolder(recyclerView.getChildAt(i));
                    if (holder != null && holder.getAdapterPosition() != -1) {
                        MPasswordEntry e = passwordEntryList.get(holder.getAdapterPosition());
                        holder.vgCheckBox.setVisibility(View.VISIBLE);
                        holder.checkBoxSelected.setChecked(e.isSelected);
                        holder.vgPasswordEntry.startAnimation(
                                new MyAnimator(holder.checkBoxSelected, holder.vgPasswordEntry, 0, 60, 300L));
                    }
                }
                break;
            case MainActivity.MODE_REORDER:
                for (int i = 0; i < recyclerView.getChildCount(); i++) {
                    final ViewHolder holder = (ViewHolder) recyclerView.getChildViewHolder(recyclerView.getChildAt(i));
                    if (holder != null && holder.getAdapterPosition() != -1) {
                        holder.vgCheckBox.setVisibility(View.INVISIBLE);
                        holder.vgPasswordEntry.startAnimation(
                                new MyAnimator(holder.checkBoxSelected, holder.vgPasswordEntry, 60, 0, 300L));
                    }
                }
                break;
        }
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        //Collections.swap(passwordEntryList, fromPosition, toPosition);
        //notifyItemMoved(fromPosition, toPosition);
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
        return true;
    }

    @Override
    public void onItemDropped(int position) {
        notifyDataSetChanged();
        if (mOnItemMoveListener != null)
            mOnItemMoveListener.onItemDropped();
    }

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

        @Override
        public void onItemSelected() {
            llContainer.setBackgroundColor(Color.LTGRAY);
        }

        @Override
        public void onItemClear() {
            llContainer.setBackgroundColor(Color.TRANSPARENT);
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
    private class MPasswordEntry extends PasswordEntry {
        public boolean isVisible, isSelected;

        public MPasswordEntry(PasswordEntry passwordEntry, boolean isVisible, boolean isSelected) {
            super(passwordEntry.id, passwordEntry.accountName, passwordEntry.password, passwordEntry.order);
            this.isVisible = isVisible;
            this.isSelected = isSelected;
        }
    }
}
