package com.example.countryexplorerd.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.example.countryexplorerd.R;
import com.example.countryexplorerd.models.Sightseeing;

import java.util.List;

public class SightseeingAdapter extends RecyclerView.Adapter<SightseeingAdapter.ViewHolder> {

    private List<Sightseeing> items;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Sightseeing item);
    }

    public SightseeingAdapter(List<Sightseeing> items, OnItemClickListener listener) {
        this.items = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sightseeing, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Sightseeing item = items.get(position);

        holder.tvName.setText(item.getTitle());
        holder.tvCountry.setText(item.getCountry());

        // 1. Очищаем старое изображение перед новой загрузкой
        Glide.with(holder.itemView.getContext()).clear(holder.imgSight);

        // 2. Загружаем новое изображение
        Glide.with(holder.itemView.getContext())
                .load(item.getImageUrl())
                .centerCrop()
                // Убрали карту из placeholder, чтобы она не мелькала при свайпе
                .thumbnail(0.1f) // Сначала грузим очень маленькую копию для скорости
                .transition(DrawableTransitionOptions.withCrossFade()) // Плавное появление картинки
                .error(R.drawable.word) // Карту покажем только если ссылка реально битая
                .into(holder.imgSight);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgSight;
        TextView tvName, tvCountry;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgSight = itemView.findViewById(R.id.imgSight);
            tvName = itemView.findViewById(R.id.tvSightName);
            tvCountry = itemView.findViewById(R.id.tvSightFact);
        }
    }
}