package com.webdynamo.quickcart.Activity;

import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.webdynamo.quickcart.Adapter.FoodListAdapter;
import com.webdynamo.quickcart.Domain.Foods;
import com.webdynamo.quickcart.R;
import com.webdynamo.quickcart.databinding.ActivityListFoodBinding;
import com.webdynamo.quickcart.databinding.ActivityMainBinding;

import java.util.ArrayList;

public class ListFoodActivity extends BaseActivity {

    ActivityListFoodBinding binding;
    private RecyclerView.Adapter adapterListFood;
    private int categoryId;
    private String categoryName;
    private String searchText;
    private boolean isSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityListFoodBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getIntentExtra();
        initList();
    }

    private void initList() {
        DatabaseReference myRef = database.getReference("Foods");
        binding.progressBar.setVisibility(View.VISIBLE);
        ArrayList<Foods> list = new ArrayList<>();

        Query query;
        if (isSearch) {
            query = myRef.orderByChild("Title").startAt(searchText).endAt(searchText+'\uf8ff');

        } else {
            query = myRef.orderByChild("CategoryId").equalTo(categoryId);
        }
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot issue : snapshot.getChildren()) {
                        list.add(issue.getValue(Foods.class));
                    }
                    if (list.size() > 0) {
                        binding.foodListView.setLayoutManager(new GridLayoutManager(ListFoodActivity.this, 2));
                        adapterListFood = new FoodListAdapter(list);
                        binding.foodListView.setAdapter(adapterListFood);
                    }
                    binding.progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getIntentExtra() {
        categoryId = getIntent().getIntExtra("CategoryId", 0);
        categoryName = getIntent().getStringExtra("CategoryName");
        searchText = getIntent().getStringExtra("text");
        isSearch = getIntent().getBooleanExtra("isSearch", false);

        binding.titleTxt.setText(categoryName);
        binding.backBtn.setOnClickListener(v -> finish());
    }
}