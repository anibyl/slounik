package org.anibyl.slounik.network;

import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import org.anibyl.slounik.Notifier;
import org.anibyl.slounik.R;
import org.anibyl.slounik.Util;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Own server communication.
 *
 * Created by Usievaład Čorny on 05.04.2015 4:16.
 */
public class Server {
    private static Config config;

    public abstract static class Callback {
        public abstract void invoke();
    }

    public static abstract class BooleanCallback {
        public abstract void invoke(Boolean bool);
    }

    public static void loadConfig(final Context context) {
        loadConfig(context, null);
    }

    public static void loadConfig(final Context context, final Callback callback) {
        final String androidId = Util.getAndroidId(context);

        assert androidId != null;
        assert context != null;

        String requestStr = context.getString(R.string.server) + "config";
        RequestQueue queue = Volley.newRequestQueue(context);
        StringRequest request = new StringRequest(requestStr,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(final String response) {
                        new AsyncTask<String, Void, Config>() {
                            @Override
                            protected Config doInBackground(String... params) {
                                Config config = new Config();
                                try {
                                    JSONObject json = new JSONObject(response);

                                    String mainUrl = json.getString("mainUrl");
                                    config.setMainUrl(mainUrl);

                                    String skarnikUrl = json.getString("skarnikUrl");
                                    config.setSkarnikUrl(skarnikUrl);

                                    JSONArray array = json.getJSONArray("testDevices");
                                    JSONObject device;
                                    for (int i = 0; i < array.length(); ++i) {
                                        device = array.getJSONObject(i);
                                        if (androidId.equals(device.optString("androidId"))) {
                                            config.setTestDevice(true);
                                        }
                                    }
                                } catch (JSONException ignored) {
                                    Notifier.log("Config can not be read.");
                                }
                                return config;
                            }

                            @Override
                            protected void onPostExecute(Config config) {
                                Server.config = config;
                                if (callback != null) {
                                    callback.invoke();
                                }
                            }
                        }.execute();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (callback != null) {
                            callback.invoke();
                        }
                    }
                });

        queue.add(request);
    }

    @Nullable
    public static String getMainUrl() {
        if (config != null) {
            return config.getMainUrl();
        } else {
            return null;
        }
    }

    @Nullable
    public static String getSkarnikUrl() {
        if (config != null) {
            return config.getSkarnikUrl();
        } else {
            return null;
        }
    }

    public static boolean isTestDevice() {
        return config != null && config.isTestDevice();
    }

    private static class Config {
        private boolean testDevice;
        private String mainUrl;
        private String skarnikUrl;

        public Config() {
        }

        public boolean isTestDevice() {
            return testDevice;
        }

        public void setTestDevice(boolean testDevice) {
            this.testDevice = testDevice;
        }

        public String getMainUrl() {
            return mainUrl;
        }

        public void setMainUrl(String mainUrl) {
            this.mainUrl = mainUrl;
        }

        public String getSkarnikUrl() {
            return skarnikUrl;
        }

        public void setSkarnikUrl(String skarnikUrl) {
            this.skarnikUrl = skarnikUrl;
        }
    }
}