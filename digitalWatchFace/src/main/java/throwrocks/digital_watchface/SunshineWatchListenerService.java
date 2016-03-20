package throwrocks.digital_watchface;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataItemBuffer;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;

/**
 * Created by josel on 3/6/2016.
 */
public class SunshineWatchListenerService extends WearableListenerService
        implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, DataApi.DataListener {

    public final String LOG_TAG = SunshineWatchListenerService.class.getSimpleName();
    private static final String WEARABLE_DATA_PATH = "/sunshine_data";


    GoogleApiClient mGoogleApiClient;

    @Override
    public void onConnected(Bundle bundle) {
        Log.d(LOG_TAG, "Google API Client was connected");
        Wearable.DataApi.addListener(mGoogleApiClient, this);
    }

    @Override
    public void onConnectionSuspended(int cause) {
        Log.d(LOG_TAG, "Connection to Google API client was suspended");
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.e(LOG_TAG, "Connection to Google API client has failed");
        Wearable.DataApi.removeListener(mGoogleApiClient, this);
        mGoogleApiClient.connect();
    }


    @Override
    public void onCreate() {
        super.onCreate();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .build();
        mGoogleApiClient.connect();
        Log.i(LOG_TAG, "onCreate");

    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        super.onMessageReceived(messageEvent);
        Log.i(LOG_TAG, "onMessageReceived");
    }

    /**
     Jose: Documentation on onDataChanged, data items, and sending Inents via localBroadcastManager
     http://android-wear-docs.readthedocs.org/en/latest/data.html
     http://developer.android.com/intl/ko/training/wearables/data-layer/data-items.html
     http://developer.android.com/intl/ko/reference/android/support/v4/content/LocalBroadcastManager.html#sendBroadcast(android.content.Intent)
     **/

    /**
     * onDataChanged
     * Receive the data when it changes on the mobile app and send it to the watch face
     * @param dataEvents the type of event, ex. changes, deleted, etc.
     */
    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        Log.i(LOG_TAG, "onDataChanged");

        DataMap dataMap;
        for (DataEvent event : dataEvents) {

            // Check the data type
            if (event.getType() == DataEvent.TYPE_CHANGED) {

                // Get the data map
                dataMap = DataMapItem.fromDataItem(event.getDataItem()).getDataMap();
                Log.i(LOG_TAG, "DataMap received on watch: " + dataMap);

                // Get the data map items
                String sunshine_temperature_high = dataMap.getString("sunshine_temperature_high");
                String sunshine_temperature_low = dataMap.getString("sunshine_temperature_low");
                String  sunshine_temperature_description = dataMap.get("sunshine_temperature_description");
                int sunshine_weather_id = dataMap.get("sunshine_weather_id");
                Long sunshine_time_millis = dataMap.getLong("sunshine_time_millis");

                Log.i(LOG_TAG, "Temperature: " + sunshine_temperature_high + ", " + sunshine_temperature_low);
                Log.i(LOG_TAG, "Weather id: " + sunshine_weather_id);

                // Create intent and broadcast it
                Intent send_weather = new Intent("ACTION_WEATHER_CHANGED");
                send_weather.putExtra("sunshine_temperature_high", sunshine_temperature_high);
                send_weather.putExtra("sunshine_temperature_low", sunshine_temperature_low);
                send_weather.putExtra("sunshine_temperature_description", sunshine_temperature_description);
                send_weather.putExtra("sunshine_weather_id", sunshine_weather_id);
                send_weather.putExtra("sunshine_time_millis", sunshine_time_millis);
                sendBroadcast(send_weather);


            }
            else if (event.getType() == DataEvent.TYPE_DELETED) {
                // DataItem deleted
            }

        }
    }




}
