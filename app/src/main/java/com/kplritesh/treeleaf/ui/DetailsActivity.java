package com.kplritesh.treeleaf.ui;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.kplritesh.treeleaf.App;
import com.kplritesh.treeleaf.MainActivity;
import com.kplritesh.treeleaf.R;
import com.kplritesh.treeleaf.databinding.ActivityDetailsBinding;
import com.kplritesh.treeleaf.interfaces.UserProfileDao;
import com.kplritesh.treeleaf.room.NewUserProfileEntity;
import com.kplritesh.treeleaf.room.UserProfileDatabase;
import com.kplritesh.treeleaf.utils.MSG;
import com.kplritesh.treeleaf.utils.Utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.ExecutorService;

public class DetailsActivity extends AppCompatActivity {
    private ActivityDetailsBinding binding;
    private NewUserProfileEntity userProfile, newUserProfile;
    private UserProfileDao userProfileDao;
    private static final int STORAGE_PERMISSION_CODE = 123;
    private static final int CAMERA_PERMISSION_CODE = 100;
    private UserProfileDatabase userProfileDatabase;
    private ExecutorService executorService;
    private Utils utils;
    private Bitmap bitmap;
    private final int CAPTURE_IMG_REQUEST = 0;
    private final int PICK_IMG_REQUEST = 1;
    private String pictureStr = "", btnAction = "", userName, email, address, countryCode, phone, description, gender, photoURI, action;
    private int id, newId;
    private ActivityResultLauncher<String> requestPermissionLauncher;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        utils = new Utils();
        requestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted ->{
            if (isGranted){
                showFileChooser();
            }else {
                Toast.makeText(this, "Please grant all the requested permission first.", Toast.LENGTH_LONG).show();
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
            }
        });
        grantCameraPermission();

        App app = (App) getApplication();
        userProfileDatabase = app.getUserProfileDatabase();
        executorService = app.getDatabaseWriteExecutor();
        userProfileDao = userProfileDatabase.userProfileDao();
        Intent intent = getIntent();
        if(intent != null){
            action = intent.getStringExtra("Action");
            newId = intent.getIntExtra("ProfileId", 0);
        }
        if (action.equals(MSG.SAVE)){
            setToolBarTitle(binding.navToolbar.navToolbar, "Add Data");
        } else if (action.equals(MSG.UPDATE)) {
            setToolBarTitle(binding.navToolbar.navToolbar, "Edit Data");
            binding.addBtn.setVisibility(View.GONE);
            binding.updateBtn.setVisibility(View.VISIBLE);
            binding.deleteBtn.setVisibility(View.VISIBLE);
        }
        binding.navToolbar.navToolbar.setNavigationOnClickListener(view -> onBackPressed());
        executorService.execute(() -> {
            if (newId > 0) {
                newUserProfile = userProfileDao.getSelUserProfiles(newId);
                if (newUserProfile != null) runOnUiThread(() -> setUserData(newUserProfile));
            }
        });

        userProfile = new NewUserProfileEntity();

        utils.removeErrorUIOnTextChange(binding.nameET, binding.nameTIL);
        utils.removeErrorUIOnTextChange(binding.emailET, binding.emailTIL);
        utils.removeErrorUIOnTextChange(binding.addressET, binding.addressTIL);
        utils.removeErrorUIOnTextChange(binding.phoneNumET, binding.phoneNumTIL);
        utils.removeErrorUIOnTextChange(binding.itemDesc, binding.descTil);
        binding.uploadFileImg.setOnClickListener(view12 -> showFileChooser());
        binding.replace.setOnClickListener(view16 -> showFileChooser());


        binding.addBtn.setOnClickListener(view -> {
            btnAction = MSG.SAVE;
            getUserData();
        });
        binding.updateBtn.setOnClickListener(view -> {
            btnAction = MSG.UPDATE;
            getUserData();
        });
        binding.deleteBtn.setOnClickListener(view -> {
            btnAction = MSG.DELETE;
            getUserData();
        });
    }
    private void setUserData(NewUserProfileEntity data){
        try {
            if (data != null) {
                id = data.getId();
                if (utils.isNotNOE(data.getName())) {binding.nameET.setText(data.getName());}
                binding.emailET.setText(data.getEmail());
                binding.addressET.setText(data.getAddress());
                binding.ccp.setCountryForPhoneCode(Integer.parseInt(data.getCountryCode()));
                binding.phoneNumET.setText(data.getPhone());
                if (data.getGender() == null || data.getGender().equals("")) {
                    binding.maleRB.setChecked(false);
                    binding.femaleRB.setChecked(false);
                    binding.othersRB.setChecked(false);
                } else if (data.getGender().contentEquals(binding.maleRB.getText())) {
                    binding.maleRB.setChecked(true);
                } else if (data.getGender().equals("Female")) {
                    binding.femaleRB.setChecked(true);
                } else if (data.getGender().equals("Others")) {
                    binding.othersRB.setChecked(true);
                }
                binding.itemDesc.setText(data.getDescription());
                if (data.getPhotoUri() != null) {
                    if (!data.getPhotoUri().isEmpty()) {
                        Glide.with(binding.getRoot().getContext())
                                .load(data.getPhotoUri())
                                .into(binding.uploadFileImg);
                    }
                } else {binding.uploadFileImg.setImageResource(R.drawable.ic_upload_document);}
            }
        }catch (Exception ignored){}
    }
    private void getUserData(){
        try {
            userProfile.setId(id);
            userName = Objects.requireNonNull(binding.nameET.getText()).toString().trim();
            if(utils.isNotNOE(userName)){userProfile.setName(userName);}
            else  {utils.errorOnTIL(binding.nameTIL, binding.nameET, MSG.REQ);return;}

            email = Objects.requireNonNull(binding.emailET.getText()).toString().trim();
            if(utils.isNotNOE(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches()){userProfile.setEmail(email);}
            else {utils.errorOnTIL(binding.emailTIL, binding.emailET, MSG.INVALID_EMAIL);return;}

            address = Objects.requireNonNull(binding.addressET.getText()).toString().trim();
            if(utils.isNotNOE(address)){userProfile.setAddress(address);}
            else  {utils.errorOnTIL(binding.addressTIL, binding.addressET, MSG.REQ);return;}

            countryCode = "+" + binding.ccp.getSelectedCountryCode() + "";
            if (utils.isNotNOE(countryCode)){userProfile.setCountryCode(countryCode);}
            else {Toast.makeText(this, "Select at-least Country Code", Toast.LENGTH_SHORT).show();return;}

            phone = Objects.requireNonNull(binding.phoneNumET.getText()).toString().trim();
            if(utils.isNotNOE(phone) && phone.length() == 10 && Patterns.PHONE.matcher(phone).matches()){userProfile.setPhone(phone);
            }else {utils.errorOnTIL(binding.phoneNumTIL, binding.phoneNumET, MSG.INVALID_PHONE);return;}

            if (binding.maleRB.isChecked()){gender = "Male";}
            else if (binding.femaleRB.isChecked()) {gender = "Female";}
            else if (binding.othersRB.isChecked()) {gender = "Others";}
            if(utils.isNotNOE(gender)) {userProfile.setGender(gender);}
            else {Toast.makeText(this, "Select at-least one gender", Toast.LENGTH_SHORT).show();return;}

            description = Objects.requireNonNull(binding.itemDesc.getText()).toString().trim();
            if(utils.isNotNOE(description)){userProfile.setDescription(description);}
            else  {utils.errorOnTIL(binding.descTil, binding.itemDesc, MSG.REQ);return;}

            if(utils.isNotNOE(pictureStr)) {
                photoURI = pictureStr;
                userProfile.setPhotoUri(photoURI);
            }

            switch (btnAction) {
                case MSG.SAVE:
                    saveData(userProfile);
                    break;
                case MSG.UPDATE:
                    updateData(userProfile);
                    break;
                case MSG.DELETE:
                    deleteData(userProfile);
                    break;
                default:
                    Toast.makeText(this, "Incorrect Action !!", Toast.LENGTH_SHORT).show();
                    break;
            }
        }catch (Exception ignored){}
    }

    private void saveData(NewUserProfileEntity userProfile){
        try {
            executorService.execute(() -> userProfileDao.insert(userProfile));
            Toast.makeText(this, "Data Saved Successfully !!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(DetailsActivity.this, MainActivity.class);
            startActivity(intent);
        }catch (Exception ignored){}
    }
    private void updateData(NewUserProfileEntity userProfile){
        try {
            executorService.execute(() -> userProfileDao.update(userProfile));
            Toast.makeText(this, "Data Updated Successfully !!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(DetailsActivity.this, MainActivity.class);
            startActivity(intent);
        }catch (Exception ignored){}
    }
    private void deleteData(NewUserProfileEntity userProfile){
        try {
            executorService.execute(() -> userProfileDao.delete(userProfile));
            Toast.makeText(this, "Data Deleted Successfully !!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(DetailsActivity.this, MainActivity.class);
            startActivity(intent);
        }catch (Exception ignored){}
    }
    private void showFileChooser(){
        final CharSequence[] options = {MSG.TAKE_PHOTO, MSG.SEL_FRM_GALLERY, MSG.CANCEL};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(MSG.SEL_OPT);
        builder.setItems(options, (dialog, item) ->{
            if(options[item].equals(MSG.TAKE_PHOTO)){
                dialog.dismiss();
                try {
                    PackageManager pm = this.getPackageManager();
                    int hasPerm = pm.checkPermission(Manifest.permission.CAMERA, this.getPackageName());
                    if (hasPerm == PackageManager.PERMISSION_GRANTED) {
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(intent, CAPTURE_IMG_REQUEST);
                    } else if (hasPerm == PackageManager.PERMISSION_DENIED) {
                        Toast.makeText(this, MSG.CAM_REQ_DEN, Toast.LENGTH_SHORT).show();
                        requestPermissions(new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
                    } else {
                        Toast.makeText(this, MSG.CAM_PER_REQ, Toast.LENGTH_SHORT).show();
                        requestPermissions(new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
                    }
                } catch (Exception e) {
                    Toast.makeText(this, MSG.SMTHG_WNT_WRNG, Toast.LENGTH_SHORT).show();
                }
            } else if (options[item].equals(MSG.SEL_FRM_GALLERY)) {
                dialog.dismiss();
                Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pickPhoto, PICK_IMG_REQUEST);
            } else if (options[item].equals(MSG.CANCEL)) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    public void grantCameraPermission(){
        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, MSG.CAM_PER_REQ, Toast.LENGTH_SHORT).show();
            requestPermissions(new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
        } else if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
            Toast.makeText(this, MSG.CAM_REQ_DEN, Toast.LENGTH_SHORT).show();
            requestPermissions(new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
        }
    }

    private String imageToString(Bitmap bitmap) {
        if (bitmap == null) { return ""; }
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
        byte[] imgBytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(imgBytes, Base64.DEFAULT);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAPTURE_IMG_REQUEST) {
            try {
                bitmap = (Bitmap) data.getExtras().get("data");
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, bytes);

                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
                File destination = new File(Environment.getExternalStorageDirectory() + "/" +
                        MSG.APP_NAME, "IMG_" + timeStamp + ".jpg");
                FileOutputStream fo;
                try {
                    destination.createNewFile();
                    fo = new FileOutputStream (destination);
                    fo.write(bytes.toByteArray());
                    fo.close();
                } catch (IOException ignored) {}

                showUploadedImages();

            } catch (Exception ignored) {}
        }else if (requestCode == PICK_IMG_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            try {
                Uri selectedImage = data.getData();
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                showUploadedImages();
            } catch (IOException ignored) {}
        }
    }
    private void showUploadedImages(){
        binding.uploadedImage.setImageBitmap(bitmap);
        pictureStr = "data:image/png;base64," + imageToString(bitmap);
        binding.uploadedImage.setVisibility(View.VISIBLE);
        binding.replace.setVisibility(View.VISIBLE);
        binding.uploadNewImageGroup.setVisibility(View.GONE);
    }
    public void setToolBarTitle(Toolbar toolbar, String title) {
        this.setSupportActionBar(toolbar);
        if (this.getSupportActionBar() != null) {
            this.getSupportActionBar().setTitle(title);
        }
    }
}