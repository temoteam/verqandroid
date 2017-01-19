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


/**
 * A simple {@link Fragment} subclass.
 */
public class RegisterFragment extends Fragment {

    EditText editRegisterName;
    EditText editRegisterChildren;
    EditText editRegisterMail;
    EditText editRegisterPass;
    EditText retypeRegisterPass;
    Button registerButton;

    public RegisterFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_register, container, false);
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        editRegisterName = (EditText) getView().findViewById(R.id.editRegisterName);
        editRegisterChildren = (EditText) getView().findViewById(R.id.editRegisterName);
        editRegisterMail = (EditText) getView().findViewById(R.id.editRegisterMail);
        editRegisterPass = (EditText) getView().findViewById(R.id.editRegisterPass);
        retypeRegisterPass = (EditText) getView().findViewById(R.id.retypeRegisterPass);
        registerButton = (Button) getView().findViewById(R.id.buttonRegister);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pass = editRegisterPass.getText().toString();
                if(pass.equals(retypeRegisterPass.getText().toString())) {
                    String name = editRegisterName.getText().toString();
                    String children = editRegisterChildren.getText().toString();
                    String mail = editRegisterMail.getText().toString();
                    register(name, children, mail, pass);
                } else {
                    Toast.makeText(getActivity().getApplication(), "Password and retype is not equals", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    private final OkHttpClient client = new OkHttpClient();

    public void register(String name, String children, String mail, String pass){
        RequestBody formBody = new FormBody.Builder()
                .add("mail", mail)
                .add("name", name)
                .add("children", children)
                .add("type", "lala")
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
                    Toast.makeText(getActivity().getApplication(), "Error", Toast.LENGTH_LONG).show();
                }

            }
        });
    }

}
