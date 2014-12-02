package com.tylerlubeck.buildingmapper;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;
import org.apache.http.util.EntityUtils;
import android.util.Log;

import com.crashlytics.android.Crashlytics;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tyler on 10/29/2014.
 */
public class PostAccessPointsTask extends GenericPOSTTask {

    Context ctx;
    FloorMapImage Fimage;

    public PostAccessPointsTask(String _url, JSONObject _data, Context _ctx) {
        super(_url, _data);
        this.ctx = _ctx;
        Fimage = null;
    }

    public PostAccessPointsTask(String _url, JSONObject _data, Context _ctx, FloorMapImage _Fimage) {
        super(_url, _data);
        Fimage = _Fimage;
        this.ctx = _ctx;
    }


    void updatePoints(int floorNum){
        View rootView = ((Activity)this.ctx).getWindow().getDecorView().findViewById(android.R.id.content);
        //Spinner point_picker = (Spinner) rootView.findViewById(R.id.location_picker_spinner);

        ArrayAdapter<String> points_adapter = new ArrayAdapter<String>(this.ctx,
                android.R.layout.simple_list_item_1,
                android.R.id.text1, new ArrayList<String>());

        String url = this.ctx.getString(R.string.get_access_points_url) + "/" + Fimage.getBuilding() + "/" + String.valueOf(floorNum);
        //point_picker.setAdapter(points_adapter);
        GetPointsAsyncTask fill_points_drop_down = new GetPointsAsyncTask(url, points_adapter, null);
        fill_points_drop_down.execute();

    }

    @Override
    void processData(HttpResponse response) {
        Toast.makeText(this.ctx, response.getStatusLine().toString(), Toast.LENGTH_LONG).show();
        if(Fimage != null){
            try {
                String jsonString = EntityUtils.toString(response.getEntity());
                JSONObject responseJson = new JSONObject(jsonString);
                Log.d("JSON RESPONSE:", responseJson.toString());
                if(!responseJson.has("error")) {
                    JSONObject success = responseJson.getJSONObject("success");
                    int x = success.getInt("x");
                    int y = success.getInt("y");
                    int floor_id = success.getInt("floor_id");
                    //floor_id = floor_id == 1 ? 1 : 0; //TODO: Figure out matching between floor id and floor number
                    //floor_id--;
                    int picker_id = 0;
                    int image_id = 1;
                    //FIX: This fucking blows. Figure out proper mapping between IDs and floor numbers
                    if (floor_id == 2) {
                        picker_id = 1;
                        image_id = 1;
                    } else if (floor_id == 1) {
                        picker_id = 0;
                        image_id = 2;
                    }
                    View rootView = ((Activity)this.ctx).getWindow().getDecorView().findViewById(android.R.id.content);
                    ((TextView) rootView.findViewById(R.id.header)).setText("You are on floor " + String.valueOf(image_id));
                    //((Spinner) rootView.findViewById(R.id.room_picker_spinner)).setSelection(picker_id);

                    //updatePoints(floor_id);
                    Fimage.create_image(image_id);
                    Fimage.draw_point_noclear(x, y);
                    Log.d("BUILDINGMAPPER", "UPLOADED DATA SUCCESS");
                    makeNotification("Map Done", "Success");
                } else {
                    Log.d("BUILDINGMAPPER", "UPLOADED DATA FAIL");
                    makeNotification("Map Done, Failed", "Awwww, shit");
                }
            } catch (JSONException e) {
                Log.d("BUILDINGMAPPER", "UPLOADED DATA WORKED, BUT BAD JSON RECEIVED");
                makeNotification("Map Done", "Bad json, nbd");
            }catch (IOException e){
                Crashlytics.logException(e);
                Toast.makeText(this.ctx, "UPLOADED DATA FAIL", Toast.LENGTH_LONG).show();
                makeNotification("Map Done, Failed", "Awwww, shit");
            }
        }
        //Toast.makeText(this.ctx, "POSTED", Toast.LENGTH_LONG).show();
    }

    void makeNotification(String title, String text){
        NotificationManager notificationManager = (NotificationManager) this.ctx.getSystemService(Context.NOTIFICATION_SERVICE);

        long pattern[] = {500, 500, 500};
        Notification.Builder builder = new Notification.Builder(this.ctx)
                        .setContentTitle(title)
                        .setContentText(text)
                        .setVibrate(pattern)
                        .setSmallIcon(R.drawable.ic_launcher);

        Notification notification = builder.getNotification();
        notificationManager.notify(20, notification);
        Log.d("BUILDINGMAPPER", "Showed notification");

    }
}
