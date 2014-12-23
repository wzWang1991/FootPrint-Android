package cloud.columbia.edu.footprint;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.google.gson.Gson;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends FragmentActivity {

    private MainFragment mainFragment;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            // Add the fragment on initial activity setup
            mainFragment = new MainFragment();
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(android.R.id.content, mainFragment)
                    .commit();
        } else {
            // Or set the fragment from restored state info
            mainFragment = (MainFragment) getSupportFragmentManager()
                    .findFragmentById(android.R.id.content);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void login(View view) throws IOException {
        EditText emailEdit = (EditText)findViewById(R.id.emailEdit);
        EditText passwordEdit = (EditText)findViewById(R.id.passwordEdit);
        String email = emailEdit.getText().toString();
        String password = passwordEdit.getText().toString();
        if (email.length() == 0 || password.length() == 0) {
            // 1. Instantiate an AlertDialog.Builder with its constructor
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // User clicked OK button
                }
            });
            // 2. Chain together various setter methods to set the dialog characteristics
            builder.setTitle("Warning")
                    .setMessage("Please input your email and password.");

            // 3. Get the AlertDialog from create()
            AlertDialog dialog = builder.create();
            dialog.show();
            return;
        }

        LoginAsyncTask loginAsyncTask = new LoginAsyncTask();
        loginAsyncTask.execute(email, password);


    }

    private class LoginAsyncTask extends AsyncTask<String, Void, String>
    {

        ProgressDialog mProgressDialog;
        @Override
        protected void onPostExecute(String result) {
            Gson gson = new Gson();
            final LoginStatus loginStatus = gson.fromJson(result, LoginStatus.class);
            mProgressDialog.dismiss();

            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    if (loginStatus.token != null) {
                        Intent intent = new Intent(MainActivity.this, UploadingPhoto.class);
                        intent.putExtra("USER_ID", loginStatus.userId);
                        startActivity(intent);
                    }
                }
            });
            // 2. Chain together various setter methods to set the dialog characteristics
            if (loginStatus.token != null) {
                builder.setTitle("Success")
                        .setMessage("Login successfully.");
            } else {
                builder.setTitle("Error")
                        .setMessage("Your email and password do not match in our system.");
            }

            // 3. Get the AlertDialog from create()
            AlertDialog dialog = builder.create();
            dialog.show();
        }

        @Override
        protected void onPreExecute() {
            mProgressDialog = ProgressDialog.show(MainActivity.this, "Login...", "Connecting to server...");
        }

        @Override
        protected String doInBackground(String... loginInfos) {
            // your network operationt
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost("http://FootPrint-7ih8dm447v.elasticbeanstalk.com/getToken");

                // Request parameters and other properties.
                List<NameValuePair> params = new ArrayList<NameValuePair>(2);
                params.add(new BasicNameValuePair("email", loginInfos[0]));
                params.add(new BasicNameValuePair("password", loginInfos[1]));
                try {
                    httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    return null;
                }

                //Execute and get the response.
                HttpResponse response = null;
                try {
                    response = httpclient.execute(httppost);
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    String str = null;
                    try {
                        str = EntityUtils.toString(entity);
                    } catch (IOException e) {
                        e.printStackTrace();
                        return null;
                    }
                    return str;
                }

            return null;
        }
    }


}
