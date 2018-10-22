package org.hugoandrade.passwordsmanagerapp.view.main;

import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.hugoandrade.passwordsmanagerapp.common.ItemTouchHelperAdapter;
import org.hugoandrade.passwordsmanagerapp.common.ItemTouchHelperViewHolder;
import org.hugoandrade.passwordsmanagerapp.objects.PasswordEntry;
import org.hugoandrade.passwordsmanagerapp.R;
import org.hugoandrade.passwordsmanagerapp.customview.RoundedCornerLayout;
import org.hugoandrade.passwordsmanagerapp.common.SimpleItemTouchHelperCallback;

@SuppressWarnings("WeakerAccess")
public class PasswordEntryListAdapter

        extends RecyclerView.Adapter<PasswordEntryListAdapter.ViewHolder>

        implements ItemTouchHelperAdapter {

    @SuppressWarnings("unused")
    private final static String TAG = PasswordEntryListAdapter.class.getSimpleName();

    private static final String HIDDEN_DEFAULT_TEXT = "password";

    private final List<MPasswordEntry> mPasswordEntries;

    private ViewMode mViewMode = ViewMode.NONE;

    private OnItemClickListener mOnItemClickListener;
    private OnItemMoveListener mOnItemMoveListener;
    private ItemTouchHelper mItemTouchHelper;

    public PasswordEntryListAdapter(ViewMode viewMode) {
        this(viewMode, new ArrayList<PasswordEntry>());
    }

    public PasswordEntryListAdapter(ViewMode viewMode, List<PasswordEntry> mPasswordEntries) {
        this.mViewMode = viewMode;
        this.mPasswordEntries = new ArrayList<>();
        for (PasswordEntry e : mPasswordEntries) {
            this.mPasswordEntries.add(new MPasswordEntry(e, false, false));
        }
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {

        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(this);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(recyclerView);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater vi = LayoutInflater.from(parent.getContext());
        return new ViewHolder(vi.inflate(R.layout.list_item_password_entry, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        MPasswordEntry e = mPasswordEntries.get(holder.getAdapterPosition());
        PasswordEntry passwordEntry = e.passwordEntry;
        boolean isVisible = e.isVisible;
        boolean isSelected = e.isSelected;

        holder.tvEntryName.setText(passwordEntry.entryName);
        holder.tvAccountName.setText(passwordEntry.accountName);
        holder.tvAccountName.setVisibility(
                passwordEntry.accountName == null? View.GONE : View.VISIBLE);
        holder.etPassword.setText(!isVisible? HIDDEN_DEFAULT_TEXT: passwordEntry.password);
        holder.etPassword.setTransformationMethod(!isVisible?
                PasswordTransformationMethod.getInstance() :
                HideReturnsTransformationMethod.getInstance());
        holder.ivPasswordVisibility.setImageResource(!isVisible?
                R.drawable.ic_password_visibility :
                R.drawable.ic_password_visibility_off);

        int color = ContextCompat.getColor(holder.rclContainer.getContext(),
                isSelected ?
                        R.color.colorPrimaryDark:
                        R.color.colorPrimary);

        holder.rclContainer.setBackgroundColor(color);
    }

    @Override
    public int getItemCount() {
        return mPasswordEntries.size();
    }

    public void setAll(List<PasswordEntry> passwordEntryList) {
        this.mPasswordEntries.clear();
        for (PasswordEntry e : passwordEntryList) {
            this.mPasswordEntries.add(new MPasswordEntry(e, false, false));
        }

        notifyDataSetChanged();
    }

    public void resetAll() {
        for (MPasswordEntry e : mPasswordEntries) {
            e.isSelected = false;
        }

        notifyDataSetChanged();
    }

    public void removeItem(PasswordEntry passwordEntry) {
        for (int i = 0; i < mPasswordEntries.size() ; i++)
            if (mPasswordEntries.get(i).passwordEntry.equals(passwordEntry)) {
                mPasswordEntries.remove(i);
                notifyItemRemoved(i);
                notifyItemRangeChanged(i, getItemCount());
                break;
            }
    }

    public void removeItems(List<PasswordEntry> passwordEntryList) {
        for (PasswordEntry e : passwordEntryList) {
            removeItem(e);
        }
    }

    public void addItem(PasswordEntry passwordEntry) {
        mPasswordEntries.add(new MPasswordEntry(passwordEntry, false, false));
        notifyItemInserted(mPasswordEntries.size() - 1);

    }

    public void changeItemSelectionStatus(PasswordEntry passwordEntry) {
        for (int i = 0; i < mPasswordEntries.size() ; i++)
            if (mPasswordEntries.get(i).passwordEntry.equals(passwordEntry)) {
                mPasswordEntries.get(i).isSelected = !mPasswordEntries.get(i).isSelected;

                if (mViewMode == ViewMode.DELETE_EDIT) {
                    notifyItemChanged(i);
                }

                break;
            }
    }

    public List<PasswordEntry> getSelectedItems() {
        List<PasswordEntry> passwordEntryCheckedList = new ArrayList<>();
        for (MPasswordEntry e : mPasswordEntries) {
            if (e.isSelected) {
                passwordEntryCheckedList.add(e.passwordEntry);
            }
        }
        return passwordEntryCheckedList;
    }

    public void updateViewMode(ViewMode viewMode) {
        this.mViewMode = viewMode;
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        for (int i = 0 ; i < Math.abs(fromPosition - toPosition) ; i++) {
            int multiplier = (fromPosition < toPosition) ? 1 : -1;
            Collections.swap(
                    mPasswordEntries,
                    fromPosition + i * multiplier,
                    fromPosition + i * multiplier + multiplier);
            notifyItemMoved(
                    fromPosition + i * multiplier,
                    fromPosition + i * multiplier + multiplier);

        }

        List<PasswordEntry> passwordEntryAffectedList = new ArrayList<>();
        int i = fromPosition + ((fromPosition > toPosition)? 1 : -1);
        do {
            if (fromPosition > toPosition) {
                i--;
            }
            else {
                i++;
            }
            mPasswordEntries.get(i).passwordEntry.order = i;
            passwordEntryAffectedList.add(mPasswordEntries.get(i).passwordEntry);
        }
        while (i != toPosition);
        if (mOnItemMoveListener != null) {
            mOnItemMoveListener.onItemMove(passwordEntryAffectedList);
        }
    }

    @Override
    public void onItemDropped(int position) {
        notifyDataSetChanged();
        if (mOnItemMoveListener != null) {
            mOnItemMoveListener.onItemDropped();
        }
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

    public class ViewHolder extends RecyclerView.ViewHolder implements ItemTouchHelperViewHolder, View.OnClickListener, View.OnLongClickListener {

        TextView tvEntryName;
        TextView tvAccountName;
        TextView etPassword;
        ImageView ivPasswordVisibility;
        RoundedCornerLayout rclContainer;
        View vContainer;

        ViewHolder(View view) {
            super(view);

            this.rclContainer = view.findViewById(R.id.rcl_container);
            this.vContainer = view.findViewById(R.id.ll_inner_container);

            this.tvEntryName = view.findViewById(R.id.tv_entry_name);
            this.tvAccountName = view.findViewById(R.id.tv_account_name);
            this.etPassword = view.findViewById(R.id.tv_password);
            this.ivPasswordVisibility = view.findViewById(R.id.iv_password_visibility);

            this.ivPasswordVisibility.setOnClickListener(this);
            this.vContainer.setOnClickListener(this);
            this.vContainer.setOnLongClickListener(this);
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

        @Override
        public void onClick(View v) {
            if (v == ivPasswordVisibility) {
                MPasswordEntry passwordEntry = mPasswordEntries.get(getAdapterPosition());
                passwordEntry.isVisible = !passwordEntry.isVisible;
                notifyItemChanged(getAdapterPosition());
            }
            else if (v == vContainer) {
                PasswordEntry passwordEntry = mPasswordEntries.get(getAdapterPosition()).passwordEntry;
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onClick(passwordEntry);
                }
            }
        }

        @Override
        public boolean onLongClick(View v) {
            PasswordEntry passwordEntry = mPasswordEntries.get(getAdapterPosition()).passwordEntry;
            if (mItemTouchHelper != null && mViewMode == ViewMode.DEFAULT) {
                mItemTouchHelper.startDrag(this);
            }
            if (mOnItemClickListener != null) {
                mOnItemClickListener.onLongClick(passwordEntry);
            }
            return false;
        }
    }

    private class MPasswordEntry {
        boolean isVisible;
        boolean isSelected;
        PasswordEntry passwordEntry;

        MPasswordEntry(PasswordEntry passwordEntry, boolean isVisible, boolean isSelected) {
            this.passwordEntry = passwordEntry;
            this.isVisible = isVisible;
            this.isSelected = isSelected;
        }
    }
}
