package com.radugunn.app.adapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;

import androidx.recyclerview.widget.RecyclerView;

import android.text.Html;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.radugunn.app.R;
import com.radugunn.app.activity.ProductDetailActivity;
import com.radugunn.app.customview.MaterialRatingBar;
import com.radugunn.app.customview.roundedimageview.RoundedTransformationBuilder;
import com.radugunn.app.customview.textview.TextViewBold;
import com.radugunn.app.customview.textview.TextViewRegular;
import com.radugunn.app.interfaces.OnItemClickListner;
import com.radugunn.app.model.CategoryList;
import com.radugunn.app.utils.BaseActivity;
import com.radugunn.app.utils.Constant;
import com.radugunn.app.utils.RequestParamUtils;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Bhumi Shah on 11/7/2017.
 */

public class RecentViewAdapter extends RecyclerView.Adapter<RecentViewAdapter.RecentViewHolde> {

    private List<CategoryList> list = new ArrayList<>();
    private final Transformation mTransformation;

    private Activity activity;
    private OnItemClickListner onItemClickListner;
    private int width = 0, height = 0;

    public RecentViewAdapter(Activity activity, OnItemClickListner onItemClickListner) {
        this.activity = activity;
        this.onItemClickListner = onItemClickListner;
        mTransformation = new RoundedTransformationBuilder()
                .cornerRadiusDp(5)
                .oval(false)
                .build();
    }

    public void addAll(List<CategoryList> list) {
        this.list = list;
        if (this.list == null) {
            this.list = new ArrayList<>();
        }

        getWidthAndHeight();
        notifyDataSetChanged();
    }

    @Override
    public RecentViewHolde onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_special_offer, parent, false);
        return new RecentViewHolde(itemView);
    }

    @Override
    public void onBindViewHolder(RecentViewHolde holder, final int position) {
        holder.llMain.getLayoutParams().width = width - (width / 3);
        holder.ivAddToCart.setVisibility(View.GONE);

        Log.e("Harsh", "Harsh: CategoryListResponse "+ new Gson().toJson(list.get(position).stockQuantity) );
        if (list.get(position).inStock) {
            Log.e("Harsh", "Harsh: Quantity: Set Data: "+list.get(position).stockQuantity );
            holder.tvStock.setText(String.valueOf(list.get(position).stockQuantity));
            holder.tvAvailibility.setText(R.string.in_stock);
            holder.tvStock.setTextColor(activity.getResources().getColor(R.color.green));
            holder.tvAvailibility.setTextColor(activity.getResources().getColor(R.color.green));
        } else {
            holder.tvStock.setVisibility(View.GONE);
            holder.tvAvailibility.setText(R.string.out_of_stock);
            holder.tvAvailibility.setTextColor(Color.RED);
        }

        ((BaseActivity) activity).showDiscount(holder.tvOff, list.get(position).salePrice, list.get(position).regularPrice);

        holder.llMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (list.get(position).type.equals(RequestParamUtils.external)) {

                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(list.get(position).externalUrl));
                    activity.startActivity(browserIntent);
                } else {
                    Constant.CATEGORYDETAIL = list.get(position);
                    Intent intent = new Intent(activity, ProductDetailActivity.class);
                    intent.putExtra(RequestParamUtils.ID, list.get(position).id);
                    activity.startActivity(intent);
                }
            }
        });

        if (list.get(position).appthumbnail != null) {
            holder.ivImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
            Picasso.get().load(list.get(position).appthumbnail)
                    .error(R.drawable.no_image_available)
                    .fit()
                    .transform(mTransformation)
                    .into(holder.ivImage);
        } else {
            holder.ivImage.setImageResource(R.drawable.no_image_available);
        }
        if (android.os.Build.VERSION.SDK_INT >= android.
                os.Build.VERSION_CODES.N) {
            holder.tvName.setText(Html.fromHtml(list.get(position).name + "", Html.FROM_HTML_MODE_LEGACY));
        } else {
            holder.tvName.setText(Html.fromHtml(list.get(position).name + ""));
        }


        holder.tvPrice.setTextSize(15);
        if (list.get(position).priceHtml != null)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                holder.tvPrice.setText(Html.fromHtml(list.get(position).priceHtml + "", Html.FROM_HTML_MODE_COMPACT));
            } else {
                holder.tvPrice.setText(Html.fromHtml(list.get(position).priceHtml) + "");
            }

        holder.tvPrice.setTextSize(15);

        ((BaseActivity) activity).setPrice(holder.tvPrice, holder.tvPrice1, list.get(position).priceHtml);

        if (!list.get(position).averageRating.equals("") && list.get(position).averageRating != null) {
            holder.ratingBar.setRating(Float.parseFloat(list.get(position).averageRating));
        } else {
            holder.ratingBar.setRating(0);
        }
    }

    @Override
    public int getItemCount() {
        if (list.size() > 5) {
            return 5;
        }
        return list.size();
    }

    public void getWidthAndHeight() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        width = displayMetrics.widthPixels;
    }

    public class RecentViewHolde extends RecyclerView.ViewHolder {

        @BindView(R.id.llMain)
        LinearLayout llMain;

        @BindView(R.id.ivImage)
        ImageView ivImage;

        @BindView(R.id.tvName)
        TextViewBold tvName;

        @BindView(R.id.tvOff)
        TextViewRegular tvOff;

        @BindView(R.id.tvPrice)
        TextViewRegular tvPrice;

        @BindView(R.id.tvPrice1)
        TextViewRegular tvPrice1;

        @BindView(R.id.ivAddToCart)
        ImageView ivAddToCart;

        @BindView(R.id.ratingBar)
        MaterialRatingBar ratingBar;

        @BindView(R.id.tvAvailibility)
        TextView tvAvailibility;

        @BindView(R.id.tvStock)
        TextView tvStock;

        @BindView(R.id.tvAvailibilitytext)
        TextView tvAvailibilitytext;

        public RecentViewHolde(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }
}