package pmd.di.ubi.pt.testinglogin;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import pmd.di.ubi.pt.testinglogin.activity.EditUserActivity;
import pmd.di.ubi.pt.testinglogin.activity.LoginActivity;
import pmd.di.ubi.pt.testinglogin.activity.TokenCreate;
import pmd.di.ubi.pt.testinglogin.helper.SQLiteHandler;
import pmd.di.ubi.pt.testinglogin.helper.SessionManager;

import java.util.HashMap;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends Activity {

    private TextView txtName;
    private TextView txtEmail;
    private TextView tokenName;
    private ImageView tokenAvatar;
    private Button btnLogout;
    private Button btnInserirToken;
    private Button btnEditUser;
    private Button btnEditToken;

    private SQLiteHandler db;
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /*  User    */
        txtName = (TextView) findViewById(R.id.name);
        txtEmail = (TextView) findViewById(R.id.email);
        btnEditUser = (Button) findViewById(R.id.btnEditUser);
        /*  Token    */
        tokenName = (TextView) findViewById(R.id.token_name);
        tokenAvatar = (ImageView) findViewById(R.id.token_image);
        btnEditToken = (Button) findViewById(R.id.btnEditToken);

        Boolean query = false; // just for testing, after we need to create a query for fetching token information
        if(!query) {
            findViewById(R.id.token_label_avatar).setVisibility(View.INVISIBLE);
            findViewById(R.id.token_label_name).setVisibility(View.INVISIBLE);

            tokenName.setVisibility(View.INVISIBLE); //not visible
            tokenAvatar.setVisibility(View.INVISIBLE); //not visible
            btnEditToken.setVisibility(View.INVISIBLE); //not visible

            /*
            *  create button programmatically to generate new token
            */


        }


        btnLogout = (Button) findViewById(R.id.btnLogout);
        //btnInserirToken = (Button) findViewById(R.id.btInsTok);
        // SqLite database handler
        db = new SQLiteHandler(getApplicationContext());

        // session manager
        session = new SessionManager(getApplicationContext());

        if (!session.isLoggedIn()) {
            logoutUser();
        }

        // Fetching user details from sqlite
        HashMap<String, String> user = db.getUserDetails();

        final String name = user.get("name");
        final String email = user.get("email");

        // Displaying the user details on the screen
        txtName.setText(name);
        txtEmail.setText(email);

        // Logout button click event
        btnLogout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                logoutUser();
            }
        });

        // Edit user click event
        btnEditUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editUser(name,email);
            }
        });
        /*btnInserirToken.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });*/
    }

    /**
     * Logging out the user. Will set isLoggedIn flag to false in shared
     * preferences Clears the user data from sqlite users table
     * */
    private void logoutUser() {
        session.setLogin(false);

        db.deleteUsers();

        // Launching the login activity
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
    /**
     * Logging out the user. Will set isLoggedIn flag to false in shared
     * preferences Clears the user data from sqlite users table
     * */
    private void editUser(String name, String email) {
        // Launching the edit user activity
        Intent intent = new Intent(MainActivity.this, TokenCreate.class);
       // intent.putExtra("username", name);
       // intent.putExtra("email", email);
        startActivity(intent);
        finish();
    }
}
