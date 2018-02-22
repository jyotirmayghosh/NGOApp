package com.example.jyotirmayghosh.ngo;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.example.jyotirmayghosh.ngo.permissions.PermissionRequestCallback;
import com.example.jyotirmayghosh.ngo.permissions.PermissionUtils;

import java.util.ArrayList;

public class StartActivity extends AppCompatActivity implements
        ActivityCompat.OnRequestPermissionsResultCallback, PermissionRequestCallback {

    private EditText nameText, phoneText;
    private ImageButton submitButton;

    static String name, phone;
    public static final String NAME = "NAME";
    public static final String PHONE = "PHONE";

    ArrayList<String> permissions = new ArrayList<>();
    PermissionUtils permissionUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        nameText = findViewById(R.id.etName);
        phoneText = findViewById(R.id.etPhone);
        submitButton = findViewById(R.id.imgBtnSubmit);

        SharedPreferences preferences = getSharedPreferences("NOG_Pref", MODE_PRIVATE);
        String nameSaved = preferences.getString("name", "N/A");
        String phoneSaved = preferences.getString("phone", "N/A");
        if (nameSaved.equals("N/A") && phoneSaved.equals("N/A")) {

            nameText.setVisibility(View.VISIBLE);
            phoneText.setVisibility(View.VISIBLE);
            submitButton.setVisibility(View.VISIBLE);
        } else {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.putExtra(NAME, name);
            intent.putExtra(PHONE, phone);
            startActivity(intent);
            finish();
        }

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                name = nameText.getText().toString();
                phone = phoneText.getText().toString();
                char noStart = phone.charAt(0);
                String number = noStart+"";

                if (nameText.getText().toString().length() == 0) {
                    nameText.setError("Name cannot be Blank");
                    return;
                } else if (phoneText.getText().toString().length() != 10 ||
                        number.equals("0") || number.equals("1") ||
                        number.equals("2") || number.equals("3") ||
                        number.equals("4") || number.equals("5")) {
                    phoneText.setError("Invalid phone number"+number);
                    return;
                } else {
                    getSharedPreferences("NOG_Pref", MODE_PRIVATE).edit().putString("name", name)
                            .putString("phone", phone).commit();
                    requestedPermissions();
                }
            }
        });
    }

    private void requestedPermissions() {
        permissionUtils = new PermissionUtils(StartActivity.this);

        permissions.add(Manifest.permission.INTERNET);
        permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);

        permissionUtils.check_permission(permissions, "Requested Permissions", 1);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        // redirects to utils
        permissionUtils.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Intent intent = new Intent(StartActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    // Callback functions

    @Override
    public void PermissionGranted(int request_code) {
        Log.i("PERMISSION", "GRANTED");
    }

    @Override
    public void PartialPermissionGranted(int request_code, ArrayList<String> granted_permissions) {
        Log.i("PERMISSION PARTIALLY", "GRANTED");
    }

    @Override
    public void PermissionDenied(int request_code) {
        Log.i("PERMISSION", "DENIED");
    }

    @Override
    public void NeverAskAgain(int request_code) {
        Log.i("PERMISSION", "NEVER ASK AGAIN");
    }
}
