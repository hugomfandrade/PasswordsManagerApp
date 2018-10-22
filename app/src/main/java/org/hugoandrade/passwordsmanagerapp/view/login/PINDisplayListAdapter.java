package org.hugoandrade.passwordsmanagerapp.view.login;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import org.hugoandrade.passwordsmanagerapp.R;

public class PINDisplayListAdapter extends RecyclerView.Adapter<PINDisplayListAdapter.ViewHolder> {

    @SuppressWarnings("unused")
    private final static String TAG = PINDisplayListAdapter.class.getSimpleName();

    private final int NUMBER_OF_KEYS = 4;

    private String pin = "";

    public PINDisplayListAdapter() { }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater vi = LayoutInflater.from(parent.getContext());
        View v = vi.inflate(R.layout.list_item_pin, parent, false);

        v.getLayoutParams().width = parent.getMeasuredWidth() / NUMBER_OF_KEYS;

        return new PINDisplayListAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final PINDisplayListAdapter.ViewHolder holder, int position) {
        holder.ivPINKey.setImageResource(holder.getAdapterPosition() < pin.length()?
                R.drawable.ic_asterisk:
                R.drawable.ic_circle);
    }

    @Override
    public int getItemCount() {
        return NUMBER_OF_KEYS;
    }

    public boolean delete() {
        if (pin.length() == 0) {
            return false;
        }

        pin = pin.substring(0, pin.length() - 1);
        notifyItemChanged(pin.length());

        return true;
    }

    public boolean add(int keyboardKey) {
        if (pin.length() == NUMBER_OF_KEYS) {
            return false;
        }

        pin = pin.concat(Integer.toString(keyboardKey));
        notifyItemChanged(pin.length() - 1);

        return true;
    }

    public String getPIN() {
        return pin;
    }

    public void reset() {
        pin = "";
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivPINKey;

        ViewHolder(View view) {
            super(view);

            ivPINKey = (ImageView) view.findViewById(R.id.iv_pin);
        }
    }
}