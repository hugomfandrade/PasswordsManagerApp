package com.hugoandrade.passwordsmanagerapp;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

public class KeyboardView extends LinearLayout {

    @SuppressWarnings("unused")
    private static final String TAG = KeyboardView.class.getSimpleName();

    private RecyclerView rvKeyboard;
    private int lastMeasuredHeight = -3;
    private int lastMeasuredWidth = -3;

    public static int KEY_ZERO     = 0;
    public static int KEY_ONE      = 1;
    public static int KEY_TWO      = 2;
    public static int KEY_THREE    = 3;
    public static int KEY_FOUR     = 4;
    public static int KEY_FIVE     = 5;
    public static int KEY_SIX      = 6;
    public static int KEY_SEVEN    = 7;
    public static int KEY_EIGHT    = 8;
    public static int KEY_NINE     = 9;
    public static int KEY_DELETE   = 11;
    public static int KEY_BOTTOM_LEFT = 12;

    private String bottomStartButtonString;

    private OnKeyboardKeyListener mOnKeyboardKeyListener;

    public KeyboardView(Context context) {
        super(context);
        init(context, null);
    }

    public KeyboardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public KeyboardView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    public KeyboardView(Context context, AttributeSet attrs, int defStyle, int defStyleRes) {
        super(context, attrs, defStyle, defStyleRes);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {

        setGravity(Gravity.CENTER);

        readAttributes(context, attrs);

        initChildViews(context);
    }

    private void readAttributes(Context context, AttributeSet attrs) {

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.KeyboardView, 0, 0);

        bottomStartButtonString =
                a.getString(R.styleable.KeyboardView_bottom_start_button);
        a.recycle();
    }

    private void initChildViews(Context context) {
        int height = getMeasuredHeight() != 0 ? getMeasuredHeight() :
                (getLayoutParams() != null? getLayoutParams().height : 0 );
        int width = getMeasuredWidth() != 0 ? getMeasuredWidth() :
                (getLayoutParams() != null? getLayoutParams().width : 0 );
        if (lastMeasuredHeight == height && lastMeasuredWidth == width)
            return;

        boolean fitWidth = width == LayoutParams.WRAP_CONTENT;
        boolean fitHeight = height == LayoutParams.WRAP_CONTENT;

        int finalHeight = 0;
        int finalWidth = 0;
        if (!fitHeight && !fitWidth) {
            finalHeight = height / 4;
            finalWidth = width / 3;
        } else {
            if (fitHeight) {
                finalHeight = width / 3;
                finalWidth = width / 3;
            }
            if (fitWidth) {
                finalHeight = height / 4;
                finalWidth = height / 4;
            }
        }
        setMinimumHeight(finalHeight);
        setMinimumWidth(finalWidth);

        if (rvKeyboard != null)
            removeView(rvKeyboard);

        rvKeyboard = new RecyclerView(context);
        addView(rvKeyboard, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));


        rvKeyboard.setLayoutManager(new GridLayoutManager(context, 3));
        rvKeyboard.setAdapter(new KeyboardListAdapter(height, width));

        lastMeasuredHeight = height;
        lastMeasuredWidth = width;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);

        initChildViews(getContext());
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        /*if (lastMeasuredHeight == -3 && lastMeasuredWidth == -3)
            return;

        boolean fitHeight = lastMeasuredHeight == LayoutParams.WRAP_CONTENT;
        boolean fitWidth = lastMeasuredWidth == LayoutParams.WRAP_CONTENT;

        int finalHeight = 0;
        int finalWidth = 0;
        if (!fitHeight && !fitWidth) {
            finalHeight = lastMeasuredHeight / 4;
            finalWidth = lastMeasuredWidth / 3;
        } else {
            if (fitHeight) {
                finalHeight = lastMeasuredWidth / 3;
                finalWidth = lastMeasuredWidth / 3;
            }
            if (fitWidth) {
                finalHeight = lastMeasuredHeight / 4;
                finalWidth = lastMeasuredHeight / 4;
            }
        }

        setMeasuredDimension(finalWidth, finalHeight);/**/
    }

    public void setOnKeyboardKeyListener(OnKeyboardKeyListener onKeyboardKeyListener) {
        mOnKeyboardKeyListener = onKeyboardKeyListener;
        rvKeyboard.getAdapter().notifyDataSetChanged();
    }

    public interface OnKeyboardKeyListener {
        void onKeyboardKey(int keyboardKey);
    }

    class KeyboardListAdapter extends RecyclerView.Adapter<KeyboardListAdapter.ViewHolder> {

        @SuppressWarnings("unused")
        private final String TAG = KeyboardListAdapter.class.getSimpleName();

        private final int NUMBER_OF_KEYS = 12;

        private int childHeight;
        private int childWidth;

        KeyboardListAdapter(int height, int width) {

            boolean fitWidth = width == LayoutParams.WRAP_CONTENT;
            boolean fitHeight = height == LayoutParams.WRAP_CONTENT;

            childHeight = 0;
            childWidth = 0;
            if (!fitHeight && !fitWidth) {
                childHeight = height / 4;
                childWidth = width / 3;
            } else {
                if (fitHeight) {
                    childHeight = width / 3;
                    childWidth = width / 3;
                }
                if (fitWidth) {
                    childHeight = height / 4;
                    childWidth = height / 4;
                }
            }
            /*Log.e(TAG, ""
                    + String.valueOf(childHeight) + " "
                    + String.valueOf(childWidth) + " "
            );/**/
        }

        @Override
        public KeyboardListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater vi = LayoutInflater.from(parent.getContext());

            View v;
            if (viewType == 11)
                v = vi.inflate(R.layout.keyboard_key_backspace, parent, false);
            else if (viewType == 9)
                v = vi.inflate(R.layout.keyboard_key_bottom_left, parent, false);
            else
                v = vi.inflate(R.layout.keyboard_key, parent, false);

            v.getLayoutParams().height = childHeight;
            v.getLayoutParams().width = childWidth;

            return new KeyboardListAdapter.ViewHolder(v, viewType, childHeight, childWidth);
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }

        @Override
        public void onBindViewHolder(final KeyboardListAdapter.ViewHolder holder, int position) {
            holder.vKey.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnKeyboardKeyListener == null)
                        return;

                    if (holder.getAdapterPosition() == 9)
                        mOnKeyboardKeyListener.onKeyboardKey(KEY_BOTTOM_LEFT);
                    else if (holder.getAdapterPosition() == 10)
                        mOnKeyboardKeyListener.onKeyboardKey(KEY_ZERO);
                    else if (holder.getAdapterPosition() == 11)
                        mOnKeyboardKeyListener.onKeyboardKey(KEY_DELETE);
                    else
                        mOnKeyboardKeyListener.onKeyboardKey(holder.getAdapterPosition() + 1);
                }
            });
            holder.vKey.setClickable(mOnKeyboardKeyListener != null);
        }

        @Override
        public int getItemCount() {
            return NUMBER_OF_KEYS;
        }


        class ViewHolder extends RecyclerView.ViewHolder {
            View vKey;

            ViewHolder(View view, int position, int height, int width) {
                super(view);

                if (position == 11) {
                    vKey = view.findViewById(R.id.iv_keyboard_backspace);
                    RoundedCornerLayout rcl = (RoundedCornerLayout) vKey.getParent();
                    rcl.getLayoutParams().height = height;
                    rcl.getLayoutParams().width = width;
                }
                else if (position == 9) {
                    vKey = view.findViewById(R.id.tv_keyboard_bottom_left);
                    if (bottomStartButtonString == null)
                        ((View) vKey.getParent()).setVisibility(GONE);
                    else {
                        ((TextView) vKey).setText(bottomStartButtonString);
                    }
                }
                else {
                    vKey = view.findViewById(R.id.tv_keyboard);
                    if (position == 10)
                        ((TextView) vKey).setText("0");
                    else
                        ((TextView) vKey).setText(String.valueOf(Integer.toString(position)));

                    int min = height;
                    if (width < min)
                        min = width;

                    RoundedCornerLayout rcl = (RoundedCornerLayout) vKey.getParent();
                    rcl.getLayoutParams().height = min;
                    rcl.getLayoutParams().width = min;
                }

            }
        }
    }
}
