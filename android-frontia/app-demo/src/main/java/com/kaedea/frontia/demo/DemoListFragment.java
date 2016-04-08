/*
 * Copyright (c) 2016 Kaede Akatsuki (kidhaibara@gmail.com)
 */

package com.kaedea.frontia.demo;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import edu.gemini.tinyplayer.R;

/**
 * Created by kaede on 2015/10/23.
 */
public class DemoListFragment extends Fragment {
    private static final String BUNDLE_INDEX = "BUNDLE_INDEX";

    private int index;
    private DemoProvider.ActivityHolder activityHolder;

    public static DemoListFragment newInstance(int index) {
        DemoListFragment fragment = new DemoListFragment();
        Bundle args = new Bundle();
        args.putInt(BUNDLE_INDEX, index);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            index = getArguments().getInt(BUNDLE_INDEX);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        RecyclerView recyclerView = new RecyclerView(getActivity());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        MyAdapter adapter = new MyAdapter();
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));
        activityHolder = DemoProvider.demos.valueAt(index);
        adapter.notifyDataSetChanged();
        return recyclerView;
    }

    class MyAdapter extends RecyclerView.Adapter<ViewHolder> {

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = getItemViewLayout(getActivity());
            return new ViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.bind(position);
        }

        @Override
        public int getItemCount() {
            return activityHolder == null ? 0 : activityHolder.getCount();
        }
    }

    @SuppressWarnings("ResourceType")
    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public int position;
        public TextView tvTitle;
        public TextView tvSubTitle;

        public ViewHolder(View itemView) {
            super(itemView);
            tvTitle = (TextView) itemView.findViewById(ID_TITLE);
            tvSubTitle = (TextView) itemView.findViewById(ID_SUBTITLE);
        }

        public void bind(int position) {
            this.position = position;
            tvTitle.setText(activityHolder.getActivityName(position) == null ? "" : activityHolder.getActivityName(position));
            tvSubTitle.setText(activityHolder.getActivityDesc(position) == null ? "" : activityHolder.getActivityDesc(position));
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            activityHolder.startActivity(getActivity(), position);
        }
    }

    public static final int ID_TITLE = 100;
    public static final int ID_SUBTITLE = 200;

    ////////////////
    //  Title     //
    //  Sub Title //
    ////////////////
    @SuppressWarnings("ResourceType")
    public static View getItemViewLayout(Context context) {
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setGravity(Gravity.CENTER_VERTICAL);
        int[] attrs = new int[]{R.attr.selectableItemBackground};
        TypedArray typedArray = context.obtainStyledAttributes(attrs);
        int backgroundResource = typedArray.getResourceId(0, 0);
        linearLayout.setBackgroundResource(backgroundResource);
        typedArray.recycle();
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        linearLayout.setLayoutParams(layoutParams);
        // Title
        TextView tvTitle = new TextView(context);
        tvTitle.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16f);
        tvTitle.setMaxLines(1);
        tvTitle.setTextColor(Color.parseColor("#212121"));
        tvTitle.setId(ID_TITLE);
        layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(Utils.dpToPx(context, 20f), Utils.dpToPx(context, 10f),
                Utils.dpToPx(context, 20f), 0);
        linearLayout.addView(tvTitle, layoutParams);
        // Sub Title
        TextView tvSubTitle = new TextView(context);
        tvSubTitle.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14f);
        tvSubTitle.setMaxLines(2);
        tvSubTitle.setTextColor(Color.parseColor("#757575"));
        tvSubTitle.setId(ID_SUBTITLE);
        layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(Utils.dpToPx(context, 20f), 0,
                Utils.dpToPx(context, 20f), Utils.dpToPx(context, 10f));
        linearLayout.addView(tvSubTitle, layoutParams);
        return linearLayout;
    }

    /**
     * This class is from the v7 samples of the Android SDK. It's not by me!
     * <p/>
     * See the license above for details.
     */
    public static class DividerItemDecoration extends RecyclerView.ItemDecoration {

        private static final int[] ATTRS = new int[]{
                android.R.attr.listDivider
        };

        public static final int HORIZONTAL_LIST = LinearLayoutManager.HORIZONTAL;

        public static final int VERTICAL_LIST = LinearLayoutManager.VERTICAL;

        private Drawable mDivider;

        private int mOrientation;

        public DividerItemDecoration(Context context, int orientation) {
            final TypedArray a = context.obtainStyledAttributes(ATTRS);
            mDivider = a.getDrawable(0);
            a.recycle();
            setOrientation(orientation);
        }

        public void setOrientation(int orientation) {
            if (orientation != HORIZONTAL_LIST && orientation != VERTICAL_LIST) {
                throw new IllegalArgumentException("invalid orientation");
            }
            mOrientation = orientation;
        }

        @Override
        public void onDraw(Canvas c, RecyclerView parent) {
            if (mOrientation == VERTICAL_LIST) {
                drawVertical(c, parent);
            } else {
                drawHorizontal(c, parent);
            }

        }


        public void drawVertical(Canvas c, RecyclerView parent) {
            final int left = parent.getPaddingLeft();
            final int right = parent.getWidth() - parent.getPaddingRight();

            final int childCount = parent.getChildCount();
            for (int i = 0; i < childCount; i++) {
                final View child = parent.getChildAt(i);
                RecyclerView v = new RecyclerView(parent.getContext());
                final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
                        .getLayoutParams();
                final int top = child.getBottom() + params.bottomMargin;
                final int bottom = top + mDivider.getIntrinsicHeight();
                mDivider.setBounds(left, top, right, bottom);
                mDivider.draw(c);
            }
        }

        public void drawHorizontal(Canvas c, RecyclerView parent) {
            final int top = parent.getPaddingTop();
            final int bottom = parent.getHeight() - parent.getPaddingBottom();

            final int childCount = parent.getChildCount();
            for (int i = 0; i < childCount; i++) {
                final View child = parent.getChildAt(i);
                final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
                        .getLayoutParams();
                final int left = child.getRight() + params.rightMargin;
                final int right = left + mDivider.getIntrinsicHeight();
                mDivider.setBounds(left, top, right, bottom);
                mDivider.draw(c);
            }
        }

        @Override
        public void getItemOffsets(Rect outRect, int itemPosition, RecyclerView parent) {
            if (mOrientation == VERTICAL_LIST) {
                outRect.set(0, 0, 0, mDivider.getIntrinsicHeight());
            } else {
                outRect.set(0, 0, mDivider.getIntrinsicWidth(), 0);
            }
        }
    }
}
