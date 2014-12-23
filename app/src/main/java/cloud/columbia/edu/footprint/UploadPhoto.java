package cloud.columbia.edu.footprint;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.IOException;


public class UploadPhoto extends Activity {

    private static final String URL_STR = "http://FootPrint-7ih8dm447v.elasticbeanstalk.com/upload";

    private String picPath;
    private double latitude;
    private double longitude;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_photo);
        Intent intent = getIntent();
        picPath = intent.getStringExtra("PIC_PATH");
        latitude = intent.getDoubleExtra("LATITUDE", 0.0);
        longitude = intent.getDoubleExtra("LONGITUDE", 0.0);
        userId = intent.getIntExtra("USER_ID", 0);

        File imgFile = new File(picPath);
        if(imgFile.exists()){
            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            ImageView imageView = (ImageView)findViewById(R.id.imageView2);
            imageView.setImageBitmap(myBitmap);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_upload_photo, menu);
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

    public void upload(View view) {
        EditText commentText = (EditText)findViewById(R.id.editText);
        UploadFileTask uploadFileTask = new UploadFileTask();
        uploadFileTask.execute(picPath, commentText.getText().toString());
    }

    private class UploadFileTask extends AsyncTask<String, Void, String>
    {

        ProgressDialog mProgressDialog;
        @Override
        protected void onPostExecute(String result) {
            mProgressDialog.dismiss();
            Log.i(this.getClass().getName(), result+" success");
            if (result.contains("success")) {
                Toast.makeText(getApplicationContext(), "Upload success!",
                        Toast.LENGTH_LONG).show();
                Log.i(this.getClass().getName(), "success");
                finish();
            }
            else
                Toast.makeText(getApplicationContext(), "Upload failed. Please upload again.",
                    Toast.LENGTH_LONG).show();
        }

        @Override
        protected void onPreExecute() {
            mProgressDialog = ProgressDialog.show(UploadPhoto.this, "Uploading...", "Uploading your photo...");
        }

        @Override
        protected String doInBackground(String... param) {
            // your network operationt
            File image = new File(param[0]);
            if (!image.exists()) {
                return null;
            }
            HttpPost httppost = new HttpPost(URL_STR);
            FileBody bin = new FileBody(image);

            HttpClient httpclient = new DefaultHttpClient();


            String[] pathSplit = param[0].split("\\.");
            HttpEntity reqEntity = MultipartEntityBuilder.create()
                    .addPart("image", bin)
                    .addTextBody("latitude", String.valueOf(latitude))
                    .addTextBody("longitude", String.valueOf(longitude))
                    .addTextBody("description", param[1])
                    .addTextBody("userId", String.valueOf(userId))
                    .addTextBody("format", pathSplit[pathSplit.length - 1])
                    .build();

            httppost.setEntity(reqEntity);
            HttpResponse response = null;
            try {
                response = httpclient.execute(httppost);
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
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
