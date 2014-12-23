package cloud.columbia.edu.footprint;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;


public class UploadingPhoto extends Activity {


    private LocationManager locationManager;
    private TextView show;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uploading_photo);

        Intent intent = getIntent();
        userId = intent.getIntExtra("USER_ID", 0);

        show = (TextView) findViewById(R.id.gps_text);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        updateView(location);

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                2000, 8, new LocationListener() {

                    @Override
                    public void onLocationChanged(Location location) {
                        // 当GPS定位信息发生改变时，更新位置
                        updateView(location);
                    }

                    @Override
                    public void onProviderDisabled(String provider) {
                        updateView(null);
                    }

                    @Override
                    public void onProviderEnabled(String provider) {
                        // 当GPS LocationProvider可用时，更新位置
                        updateView(locationManager
                                .getLastKnownLocation(provider));

                    }

                    @Override
                    public void onStatusChanged(String provider, int status,
                                                Bundle extras) {
                    }
                });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_uploading_photo, menu);
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

    public void takePhoto(View view) {
        Intent intent = new Intent(this, PhotoTake.class);
        intent.putExtra("PIC_SOURCE", PhotoTake.SELECT_PIC_BY_TACK_PHOTO);
        startActivityForResult(intent, 0);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK)
            return;
        String path = data.getStringExtra(PhotoTake.KEY_PHOTO_PATH);
        if (path != null) {
            Intent intent = new Intent(this, UploadPhoto.class);
            intent.putExtra("PIC_PATH", path);
            intent.putExtra("LATITUDE", latitude);
            intent.putExtra("LONGITUDE", longitude);
            intent.putExtra("USER_ID", userId);
            startActivity(intent);
            //UploadFileTask uploadFileTask = new UploadFileTask();
            //uploadFileTask.execute(path);
        }

    }

    double latitude;
    double longitude;
    private void updateView(Location location) {
        if (location != null) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            StringBuffer sb = new StringBuffer();
            sb.append("Real time position：\nLongitude: ");
            sb.append(location.getLongitude());
            sb.append("\nLatitude: ");
            sb.append(location.getLatitude());
            sb.append("\nAltitude: ");
            sb.append(location.getAltitude());
            sb.append("\nSpeed: ");
            sb.append(location.getSpeed());
            sb.append("\nBearing: ");
            sb.append(location.getBearing());
            sb.append("\nAccuracy: ");
            sb.append(location.getAccuracy());
            show.setText(sb.toString());
        } else {
            show.setText("");
        }
    }




}
