package org.anibyl.slounik.network;

import android.content.Context;
import android.os.AsyncTask;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import org.anibyl.slounik.R;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Own server communication.
 *
 * Created by Usievaład Čorny on 05.04.2015 4:16.
 */
public class Server {
    public abstract static class Callback {
        public abstract void invoke();
    }

    public static abstract class BooleanCallback {
        public abstract void invoke(Boolean bool);
    }

    public static void getTestDevice(final String androidId, final BooleanCallback callback, Context context) {
        assert androidId != null;
        assert callback != null;
        assert context != null;

        String requestStr = context.getString(R.string.server) + "testDevices.json";
        RequestQueue queue = Volley.newRequestQueue(context);
        StringRequest request = new StringRequest(requestStr,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(final String response) {
                        new AsyncTask<String, Boolean, Boolean>() {
                            @Override
                            protected Boolean doInBackground(String... params) {
                                try {
                                    JSONObject json = new JSONObject(response);

                                    JSONArray array = json.getJSONArray("testDevices");
                                    JSONObject device;
                                    for (int i = 0; i < array.length(); ++i) {
                                        device = array.getJSONObject(i);
                                        if (androidId.equals(device.optString("androidId"))) {
                                            return true;
                                        }
                                    }
                                } catch (JSONException e) {
                                    return false;
                                }
                                return false;
                            }

                            @Override
                            protected void onPostExecute(Boolean testDevice) {
                                callback.invoke(testDevice);
                            }
                        }.execute();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        callback.invoke(false);
                    }
                });

        queue.add(request);
    }
}
