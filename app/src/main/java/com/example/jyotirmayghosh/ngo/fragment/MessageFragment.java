package com.example.jyotirmayghosh.ngo.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.util.Base64;
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
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.jyotirmayghosh.ngo.R;

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
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 */
public class MessageFragment extends Fragment {

    private static final int PICK_IMAGE_REQUEST = 10;
    String name, phone, subject = null, message = null;
    private TextView nameView, phoneView;
    private EditText subjectText, messageText;
    private ImageView attachedImageView;
    private ConstraintLayout layout;
    String serverUrl = "https://accelerated-invento.000webhostapp.com/send_message.php";

    Bitmap FixBitmap = null ;
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
            if (subject.equals("") || message.equals("")) {
                Toast.makeText(getContext(), "No Subject or Message.", Toast.LENGTH_SHORT).show();
            }
            else {
                uploadImageSend();
            }
        } else if (item.getItemId() == R.id.action_attach) {
            startActivityForResult(new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI), PICK_IMAGE_REQUEST);

        } else if (item.getItemId() == R.id.action_discard) {
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

    public void uploadImageSend() {

        String image = "empty path";
        if (FixBitmap!=null) {
            ByteArrayOutputStream byteArrayOutputStreamObject;
            byteArrayOutputStreamObject = new ByteArrayOutputStream();
            FixBitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStreamObject);
            byte[] byteArrayVar = byteArrayOutputStreamObject.toByteArray();
            image = Base64.encodeToString(byteArrayVar, Base64.DEFAULT);
        }

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Send message, please wait...");
        progressDialog.show();

        final RequestQueue queue = Volley.newRequestQueue(getContext());
        final String finalImage = image;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, serverUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.hide();
                        Toast.makeText(getContext(), response, Toast.LENGTH_SHORT).show();
                        subjectText.setText("");
                        messageText.setText(response);
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
                HashMap<String, String> params = new HashMap<String, String>();
                params.put("user_name", name);
                params.put("user_phone", phone);
                params.put("user_subject", subject);
                params.put("user_message", message);

                ImageProcessClass imageProcessClass = new ImageProcessClass();
                params.put("attached_image", finalImage);
                imageProcessClass.ImageHttpRequest(serverUrl, params);

                return params;
            }
        };
        queue.add(stringRequest);
    }
    
    public class ImageProcessClass {

        boolean check=true;
        public String ImageHttpRequest(String requestURL, HashMap<String, String> PData) {
            StringBuilder stringBuilder = new StringBuilder();
            try {
                URL url;
                HttpURLConnection httpURLConnectionObject;
                OutputStream OutPutStream;
                BufferedWriter bufferedWriterObject;
                BufferedReader bufferedReaderObject;
                int RC;
                url = new URL(requestURL);
                httpURLConnectionObject = (HttpURLConnection) url.openConnection();
                httpURLConnectionObject.setReadTimeout(19000);
                httpURLConnectionObject.setConnectTimeout(19000);
                httpURLConnectionObject.setRequestMethod("POST");
                httpURLConnectionObject.setDoInput(true);
                httpURLConnectionObject.setDoOutput(true);
                OutPutStream = httpURLConnectionObject.getOutputStream();
                bufferedWriterObject = new BufferedWriter(new OutputStreamWriter(OutPutStream, "UTF-8"));
                bufferedWriterObject.write(bufferedWriterDataFN(PData));
                bufferedWriterObject.flush();
                bufferedWriterObject.close();
                OutPutStream.close();
                RC = httpURLConnectionObject.getResponseCode();
                if (RC == HttpsURLConnection.HTTP_OK) {
                    bufferedReaderObject = new BufferedReader(new InputStreamReader(httpURLConnectionObject.getInputStream()));
                    stringBuilder = new StringBuilder();
                    String RC2;
                    while ((RC2 = bufferedReaderObject.readLine()) != null) {
                        stringBuilder.append(RC2);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return stringBuilder.toString();
        }

        private String bufferedWriterDataFN(HashMap<String, String> HashMapParams) throws UnsupportedEncodingException {
            StringBuilder stringBuilderObject;
            stringBuilderObject = new StringBuilder();
            for (Map.Entry<String, String> KEY : HashMapParams.entrySet()) {
                if (check)
                    check = false;
                else
                    stringBuilderObject.append("&");
                stringBuilderObject.append(URLEncoder.encode(KEY.getKey(), "UTF-8"));
                stringBuilderObject.append("=");
                stringBuilderObject.append(URLEncoder.encode(KEY.getValue(), "UTF-8"));
            }
            return stringBuilderObject.toString();
        }
    }
}
