package com.example.jyotirmayghosh.ngo.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.jyotirmayghosh.ngo.MainActivity;
import com.example.jyotirmayghosh.ngo.R;

import junit.framework.Test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 */
public class MessageFragment extends Fragment {

    private static final int PICK_IMAGE_REQUEST = 10;
    String name, phone, subject, message, image;
    private TextView nameView, phoneView;
    private EditText subjectText, messageText;
    private ImageView attachedImageView;
    private ConstraintLayout layout;
    String serverUrl = "https://accelerated-invento.000webhostapp.com/send_message.php";

    Bitmap FixBitmap ;
    ProgressDialog progressDialog;

    public MessageFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_message, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        nameView = getView().findViewById(R.id.tvName);
        phoneView = getView().findViewById(R.id.tvPhone);
        subjectText = getView().findViewById(R.id.etSubject);
        messageText = getView().findViewById(R.id.etMessage);
        layout=getView().findViewById(R.id.attachLayout);
        attachedImageView=getView().findViewById(R.id.imgAttached);

        SharedPreferences preferences = this.getActivity().getSharedPreferences("NOG_Pref", MODE_PRIVATE);
        String nameSaved = preferences.getString("name", "N/A");
        String phoneSaved = preferences.getString("phone", "N/A");

        nameView.setText(nameSaved);
        phoneView.setText(phoneSaved);

        name=nameSaved;
        phone=phoneSaved;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.action_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        subject = subjectText.getText().toString();
        message = messageText.getText().toString();
        if (item.getItemId() == R.id.action_send) {
            uploadImageSend();
        } /*else if (item.getItemId() == R.id.action_attach) {
            Intent intent = new Intent();
            intent.setType("image*//*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Image From Gallery"), PICK_IMAGE_REQUEST);

        }*/ else if (item.getItemId() == R.id.action_discard) {
            subjectText.setText("");
            messageText.setText("");
            Toast.makeText(getContext(), "Message Discarded.", Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri filePath = data.getData();
            layout.setVisibility(View.VISIBLE);
            try {
                FixBitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), filePath);
                attachedImageView.setImageBitmap(FixBitmap);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void uploadImageSend()
    {
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Send message, please wait...");
        progressDialog.show();

        //converting image to base64 string
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        if (FixBitmap==null)
        {
            image= "empty path";
        }
        else
        {
            FixBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] imageBytes = baos.toByteArray();
            image = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        }

        final RequestQueue queue = Volley.newRequestQueue(getContext());
        StringRequest stringRequest = new StringRequest(Request.Method.POST, serverUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.hide();
                        Toast.makeText(getContext(), response, Toast.LENGTH_SHORT).show();
                        subjectText.setText("");
                        messageText.setText("");
                        layout.setVisibility(View.INVISIBLE);
                        queue.stop();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.getMessage();
                error.printStackTrace();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("user_name", name);
                params.put("user_phone", phone);
                params.put("user_subject", subject);
                params.put("user_message", message);
                params.put("attached_image", image);
                return params;
            }
        };
        queue.add(stringRequest);
    }

}
