package com.kplritesh.treeleaf.adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kplritesh.treeleaf.R;
import com.kplritesh.treeleaf.databinding.ItemUserProfileBinding;
import com.kplritesh.treeleaf.interfaces.OnDataChanged;
import com.kplritesh.treeleaf.room.NewUserProfileEntity;
import com.kplritesh.treeleaf.utils.Utils;

import java.util.List;

public class UserProfileAdapter extends RecyclerView.Adapter<UserProfileAdapter.UserProfileViewHolder> {
    private List<NewUserProfileEntity> userProfiles;
    private OnDataChanged<NewUserProfileEntity> listener;
    public void setData(List<NewUserProfileEntity> userProfiles, OnDataChanged<NewUserProfileEntity> listener) {
        this.userProfiles = userProfiles;
        this.listener = listener;
    }

    @NonNull
    @Override
    public UserProfileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemUserProfileBinding binding = ItemUserProfileBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new UserProfileViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull UserProfileViewHolder holder, int position) {
        NewUserProfileEntity userProfile = userProfiles.get(position);
        if(userProfile != null){
            holder.bind(userProfile);
        }

    }

    @Override
    public int getItemCount() {return userProfiles != null ? userProfiles.size() : 0;}

    public class UserProfileViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private final ItemUserProfileBinding binding;

        UserProfileViewHolder(ItemUserProfileBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            binding.getRoot().setOnClickListener(this);
        }
        @SuppressLint("SetTextI18n")
        void bind(NewUserProfileEntity userProfile) {
            binding.profileNameTV.setText(userProfile.getName());
            binding.profileAddressTV.setText(userProfile.getAddress());
            binding.profileEmailTV.setText(userProfile.getEmail());
            binding.profileGenderTV.setText(userProfile.getGender());
            binding.profilePhoneTV.setSelected(true);
            binding.profilePhoneTV.setText("" + userProfile.getCountryCode() + " " + userProfile.getPhone() + "");
            if(userProfile.getPhotoUri() != null) {
                if (!userProfile.getPhotoUri().isEmpty()) {
                    binding.profilePic.setImageBitmap(Utils.convertStringToBitmap(userProfile.getPhotoUri()));
                }
            }else {binding.profilePic.setImageResource(R.drawable.ic_guest);}
            binding.profileDescTV.setText(userProfile.getDescription());
        }

        @Override
        public void onClick(View view) {
            int position = getBindingAdapterPosition();
            if (position != RecyclerView.NO_POSITION && listener != null) {
                NewUserProfileEntity item = userProfiles.get(position);
                listener.onDataChanged(item);
            }
        }
    }
}
