package pmd.di.ubi.pt.testinglogin.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import pmd.di.ubi.pt.testinglogin.MainActivity;
import pmd.di.ubi.pt.testinglogin.R;
import pmd.di.ubi.pt.testinglogin.app.AppConfig;
import pmd.di.ubi.pt.testinglogin.app.AppController;
import pmd.di.ubi.pt.testinglogin.helper.SQLiteHandler;
import pmd.di.ubi.pt.testinglogin.helper.SessionManager;

public class TokenCreate extends Activity {
    private static final String TAG = TokenCreate.class.getSimpleName();

    private EditText tokenName;
    private ProgressDialog pDialog;
    private SessionManager session;
    private SQLiteHandler db;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_token_create);

        tokenName = (EditText) findViewById(R.id.name);

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        // Session manager
        session = new SessionManager(getApplicationContext());
/*
        // SQLite database handler
        db = new SQLiteHandler(getApplicationContext());

        // Check if user is already logged in or not
        if (session.isLoggedIn()) {
            // User is already logged in. Take him to main activity
            Intent intent = new Intent(TokenCreate.this,
                    MainActivity.class);
            startActivity(intent);
            finish();
        }

*/
    }

    /**
     * Function to store user in MySQL database will post params(tag, name,
     * email, password) to register url
     * */
    private void registerToken(final String name, final String ident, final String signature, final String uid) {
        // Tag used to cancel the request
        String tag_string_req = "req_registerToken";

        pDialog.setMessage("Registering ...");
        showDialog();
        Log.d("AQUI", "Register Response: ");

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_REGISTERTOKEN, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "tokenRegister Response: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {
                        // User successfully stored in MySQL
                        // Now store the user in sqlite
                       // String uid = jObj.getString("uid");

                        JSONObject token = jObj.getJSONObject("token");
                        String name = token.getString("name");
                        String ident = token.getString("ident");
                        String signature = token.getString("signature");

                        //String created_at = user
                         //       .getString("created_at");


                        // Inserting row in users table
                     //   db.addUser(name, ident, signature, created_at);

                        Toast.makeText(getApplicationContext(), "Token successfully inserted", Toast.LENGTH_LONG).show();

                        // Launch login activity
                        Intent intent = new Intent(
                                TokenCreate.this,
                                LoginActivity.class);
                        startActivity(intent);
                        finish();
                    } else {

                        // Error occurred in registration. Get the error
                        // message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Registration Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                params.put("name", name);
                params.put("ident", ident);
                params.put("signature", signature);
                params.put("uid",uid);

                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    public void genToken(View view) {

        String name = tokenName.getText().toString().trim();



        TokenOperation token = new TokenOperation(name,this);

        if (!name.isEmpty()) {
            //registerToken("osddeeeela","identtteeetttt","signaturee322eeee", "1");
            registerToken(token.getName(),token.getIdent(),token.getSign(), "1");

        } else {
            Toast.makeText(getApplicationContext(),
                    "Please enter your details!", Toast.LENGTH_LONG)
                    .show();
        }
    }
}


