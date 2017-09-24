package org.drunkcode.madbike.ui.search.interactor;

import android.content.Context;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.JsonObject;

import org.drunkcode.madbike.R;
import org.drunkcode.madbike.base.BaseInteractor;
import org.drunkcode.madbike.network.Apis;
import org.drunkcode.madbike.preferences.Preferences;
import org.drunkcode.madbike.ui.search.response.SearchResponse;
import org.drunkcode.madbike.utils.JSONUtils;
import org.drunkcode.madbike.utils.MD5;
import org.json.JSONException;

import de.greenrobot.event.EventBus;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class PostSearchInteractor implements BaseInteractor {

    private Context context;
    private LatLng latLng;

    public PostSearchInteractor(Context context) {
        this.context = context;
    }

    private JsonObject createJSONToPostRequest() throws JSONException {
        JsonObject requestItem = new JsonObject();
        Preferences preferences = Preferences.getInstance(context);
        requestItem.addProperty(JSONUtils.DNI, preferences.getUserDni());
        requestItem.addProperty(JSONUtils.ID_AUTH, preferences.getIdAuth());
        requestItem.addProperty(JSONUtils.LATITUDE, latLng.latitude);
        requestItem.addProperty(JSONUtils.LONGITUDE, latLng.longitude);
        requestItem.addProperty(JSONUtils.ID_SECURITY, MD5.buildIdSecurity(preferences.getUserDni(), preferences.getIdAuth()));
        return requestItem;
    }

    @Override
    public void execute() throws Throwable {

        RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint(context.getString(R.string.url_base)).build();
        Apis apis = restAdapter.create(Apis.class);
        apis.getNearestStations(createJSONToPostRequest(), new Callback<SearchResponse>() {
            @Override
            public void success(SearchResponse searchResponse, Response response) {
                EventBus.getDefault().post(searchResponse);
            }

            @Override
            public void failure(RetrofitError error) {
                SearchResponse searchResponse = new SearchResponse(0, context.getString(R.string.error_generic));
                EventBus.getDefault().post(searchResponse);
            }
        });

    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }
}
