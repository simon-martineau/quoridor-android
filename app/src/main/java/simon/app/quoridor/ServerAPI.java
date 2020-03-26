package simon.app.quoridor;

import android.app.Activity;
import android.util.Log;

import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ServerAPI {
	public static final String apiBaseUrl = "https://python.gel.ulaval.ca/quoridor/api/";
	private final OkHttpClient httpClient = new OkHttpClient();
	private static final String TAG = "ServerAPI";



	public JSONObject makeMove(String targetURL, String gameID, String moveType, String position) {

		RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
				.addFormDataPart("id", gameID)
				.addFormDataPart("type", moveType)
				.addFormDataPart("pos", position)
				.build();
		Request request = new Request.Builder()
				.url(targetURL)
				.method("POST", body)
				.build();

		try {
			Response response = httpClient.newCall(request).execute();
			return new JSONObject(response.body().string());
		} catch (IOException | JSONException e) {
			e.printStackTrace();
			return null;
		}
	}
}
