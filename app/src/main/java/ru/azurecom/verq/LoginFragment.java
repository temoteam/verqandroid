package ru.azurecom.verq;


import android.app.FragmentTransaction;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.content.Context.MODE_PRIVATE;


public class LoginFragment extends Fragment {

    EditText editMail;
    EditText editPass;
    Button buttonLogin;
    Button buttonRegister;

    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false);
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        editMail = (EditText) getView().findViewById(R.id.editMail);
        editPass = (EditText) getView().findViewById(R.id.editPass);
        buttonLogin = (Button) getView().findViewById(R.id.buttonLogin);
        buttonRegister = (Button) getView().findViewById(R.id.buttonRegister);

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mail = editMail.getText().toString();
                String pass = editMail.getText().toString();
                try {
                    login(mail, pass);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction().replace(R.id.frgmntCont, new RegisterFragment()).addToBackStack(null);
                fragmentTransaction.commit();
            }
        });


    }


    private final OkHttpClient client = new OkHttpClient();

    public void login(String mail, String pass)  throws Exception {




        RequestBody formBody = new FormBody.Builder()
                .add("mail", mail)
                .add("pass", pass)
                .build();
        Request request = new Request.Builder()
                .url(getString(R.string.maindomain))
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .post(formBody)
                .build();



        client.newCall(request).enqueue(new Callback() {
            @Override public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

                Headers responseHeaders = response.headers();
                for (int i = 0, size = responseHeaders.size(); i < size; i++) {
                    System.out.println(responseHeaders.name(i) + ": " + responseHeaders.value(i));
                }

                System.out.println(response.body().string());

                try {
                    JSONObject result = new JSONObject(response.body().string());
                    String token = result.getString("token");
                    String id = result.getString("id");

                    SharedPreferences sharedPreferences = getActivity().getPreferences(MODE_PRIVATE);
                    SharedPreferences.Editor ed = sharedPreferences.edit();
                    ed.putString("token", token);
                    ed.putString("id", id);
                    ed.commit();

                    FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction().replace(R.id.frgmntCont, new QueueFragment()).addToBackStack(null);
                    fragmentTransaction.commit();
                } catch (JSONException e) {
                    Toast.makeText(getActivity().getApplication(), "Wrong login/pass", Toast.LENGTH_LONG).show();
                }
            }
        });


    }

}
