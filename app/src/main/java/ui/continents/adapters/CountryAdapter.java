package ui.continents.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.countryexplorerd.FavoriteCountry;
import com.example.countryexplorerd.MainActivity;
import com.example.countryexplorerd.R;
import com.example.countryexplorerd.models.Country;
import java.util.List;

public class CountryAdapter extends RecyclerView.Adapter<CountryAdapter.ViewHolder> {
    private List<Country> countries;

    public CountryAdapter(List<Country> countries) {
        this.countries = countries;
    }

    // МЕТОД ДЛЯ ОБНОВЛЕНИЯ ДАННЫХ (Обязательно добавь его!)
    public void updateData(List<Country> newCountries) {
        this.countries = newCountries;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_country, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Country country = countries.get(position);
        holder.tvName.setText(country.getName());
        holder.tvFlag.setText(country.getFlag());
        holder.tvCapital.setText(country.getCapital());

        // Логика избранного
        boolean isFav = MainActivity.db.favoriteDao().isFavorite(country.getName());
        holder.btnFavorite.setImageResource(isFav ? android.R.drawable.btn_star_big_on : android.R.drawable.btn_star_big_off);

        holder.btnFavorite.setOnClickListener(v -> {
            FavoriteCountry fav = new FavoriteCountry(country.getName());
            if (MainActivity.db.favoriteDao().isFavorite(country.getName())) {
                MainActivity.db.favoriteDao().delete(fav);
                holder.btnFavorite.setImageResource(android.R.drawable.btn_star_big_off);
            } else {
                MainActivity.db.favoriteDao().insert(fav);
                holder.btnFavorite.setImageResource(android.R.drawable.btn_star_big_on);
            }
        });

        holder.itemView.setOnClickListener(v -> {
            if (v.getContext() instanceof MainActivity) {
                ((MainActivity) v.getContext()).openDetails(country);
            }
        });
    }

    @Override
    public int getItemCount() { return countries != null ? countries.size() : 0; }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvFlag, tvName, tvCapital;
        ImageButton btnFavorite;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFlag = itemView.findViewById(R.id.tvFlag);
            tvName = itemView.findViewById(R.id.tvCountryName);
            tvCapital = itemView.findViewById(R.id.tvCapital);
            btnFavorite = itemView.findViewById(R.id.btnFavorite);
        }
    }
}