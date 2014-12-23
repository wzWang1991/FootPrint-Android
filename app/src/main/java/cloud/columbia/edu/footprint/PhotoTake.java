package cloud.columbia.edu.footprint;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;


public class PhotoTake extends Activity {

    public static final int SELECT_PIC_BY_TACK_PHOTO = 1;
    public static final int SELECT_PIC_BY_PICK_PHOTO = 2;

    public static final String KEY_PHOTO_PATH = "photo_path";

    private Intent lastIntent;

    private Uri photoUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_take);
        lastIntent = getIntent();
        int picSource = lastIntent.getIntExtra("PIC_SOURCE", SELECT_PIC_BY_TACK_PHOTO);
        Log.i(this.getLocalClassName(), String.valueOf(picSource));
        if (picSource == SELECT_PIC_BY_TACK_PHOTO) {
            takePhoto();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_photo_take, menu);
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

    public void takePhoto() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        ContentValues values = new ContentValues();
        photoUri = this.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, photoUri);
        startActivityForResult(intent, SELECT_PIC_BY_TACK_PHOTO);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK)
            return;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(photoUri, proj, null, null, null);
        String picPath = "";
        if(cursor != null )
        {
            int columnIndex = cursor.getColumnIndexOrThrow(proj[0]);
            cursor.moveToFirst();
            picPath = cursor.getString(columnIndex);
            cursor.close();
        }
        if(picPath != null && ( picPath.endsWith(".png") || picPath.endsWith(".PNG") ||picPath.endsWith(".jpg") ||picPath.endsWith(".JPG")  ))
        {
            lastIntent.putExtra(KEY_PHOTO_PATH, picPath);
            setResult(Activity.RESULT_OK, lastIntent);
            finish();
        }else{
            Toast.makeText(this, "You choose a wrong file.", Toast.LENGTH_LONG).show();
        }
    }
}
