package com.kplritesh.treeleaf;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.kplritesh.treeleaf.adapters.UserProfileAdapter;
import com.kplritesh.treeleaf.databinding.ActivityMainBinding;
import com.kplritesh.treeleaf.interfaces.OnDataChanged;
import com.kplritesh.treeleaf.interfaces.UserProfileDao;
import com.kplritesh.treeleaf.room.NewUserProfileEntity;
import com.kplritesh.treeleaf.room.UserProfileDatabase;
import com.kplritesh.treeleaf.ui.DetailsActivity;
import com.kplritesh.treeleaf.utils.MSG;

import java.util.List;
import java.util.concurrent.ExecutorService;

public class MainActivity extends AppCompatActivity implements OnDataChanged<NewUserProfileEntity> {
    private ActivityMainBinding binding;
    private NewUserProfileEntity userProfiles;
    private UserProfileDao userProfileDao;
    private List<NewUserProfileEntity> userProfileList;
    private UserProfileDatabase userProfileDatabase;
    private ExecutorService executorService;
    UserProfileAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        App app = (App) getApplication();
        userProfileDatabase = app.getUserProfileDatabase();
        executorService = app.getDatabaseWriteExecutor();
        userProfileDao = userProfileDatabase.userProfileDao();
        userProfiles = new NewUserProfileEntity();
        executorService.execute(() -> {
            userProfileList = userProfileDao.getAllUserProfiles();
            if(userProfileList != null && userProfileList.size() > 0 ){
                runOnUiThread(this::loadProfileRV);
            }else {binding.noItems.setVisibility(View.VISIBLE);}
        });

        binding.fab.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
            intent.putExtra("Action", MSG.SAVE);
            startActivity(intent);
        });
    }

    private void loadProfileRV(){
        try {
            binding.noItems.setVisibility(View.GONE);
            adapter = new UserProfileAdapter();
            binding.recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
            binding.recyclerView.setClickable(true);
            binding.recyclerView.setFocusable(true);
            binding.recyclerView.setAdapter(adapter);
            adapter.setData(userProfileList, this);
            if (adapter.getItemCount() == 0) {binding.noItems.setVisibility(View.VISIBLE);}
            else {binding.noItems.setVisibility(View.GONE);}
        }catch (Exception ignored){}
    }

    @Override
    public void onDataChanged(NewUserProfileEntity data) {
        if (data != null){
            int id = data.getId();
            Intent intent = new Intent(this, DetailsActivity.class);
            intent.putExtra("ProfileId", id);
            intent.putExtra("Action", MSG.UPDATE);
            startActivity(intent);
        }
    }
}
