/* Copyright (c) 2010 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.ytd;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import java.io.IOException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import com.scoutbuzz.R;


public class ClientLoginAuthorizer implements Authorizer {
    public static final String YOUTUBE_AUTH_TOKEN_TYPE = "youtube";
    private static final String AUTH_URL = "https://www.google.com/accounts/ClientLogin";
    private Context ctx;
    private static final String LOG_TAG = ClientLoginAuthorizer.class
            .getSimpleName();

    public ClientLoginAuthorizer(Context context) {
        this.ctx = context;

    }

    @Override
    public void fetchAccounts(AuthorizationListener<String[]> listener) {
        // not used

    }

    @Override
    public void addAccount(Activity activity,
            AuthorizationListener<String> listener) {
        // not used

    }

    @Override
    public void fetchAuthToken(String accountName, Activity activity,
            AuthorizationListener<String> listener) {
        Log.d(LOG_TAG, "Getting " + YOUTUBE_AUTH_TOKEN_TYPE + " authToken for "
                + accountName);
        try {
            String token = getCLAuthToken(accountName);
            listener.onSuccess(token);
        } catch (Exception e) {

            listener.onError(e);

        }
    }

    @Override
    public String getAuthToken(String accountName) {
        try {
            String token = getCLAuthToken(accountName);
            return token;
        } catch (IOException e) {

            e.printStackTrace();
            return null;

        }
    }

    public String getCLAuthToken(String accountName) throws IOException {
        HttpURLConnection urlConnection = getGDataUrlConnection(AUTH_URL);
        urlConnection.setRequestMethod("POST");
        urlConnection.setDoOutput(true);
        urlConnection.setRequestProperty("Content-Type",
                "application/x-www-form-urlencoded");
        String template = "Email=%s&Passwd=%s&service=%s&source=%s";
        String userName = "joshua.calloway"; // TODO
        String password = "call00way"; // TODO
        String service = YOUTUBE_AUTH_TOKEN_TYPE;
        String source = ctx.getString(R.string.client_id);
        String loginData = String.format(template, encode(userName),
                encode(password), service, source);
        OutputStreamWriter outStreamWriter = new OutputStreamWriter(
                urlConnection.getOutputStream());
        outStreamWriter.write(loginData);
        outStreamWriter.close();
        int responseCode = urlConnection.getResponseCode();
        if (responseCode != 200) {
            Log.d(LOG_TAG, "Got an error response : " + responseCode + " "
                    + urlConnection.getResponseMessage());
            throw new IOException(urlConnection.getResponseMessage());
        } else {

            InputStream is = urlConnection.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String line = null;
            while ((line = br.readLine()) != null) {
                if (line.startsWith("Auth=")) {
                    String split[] = line.split("=");
                    String token = split[1];
                    Log.d(LOG_TAG, "Auth Token : " + token);
                    return token;
                }
            }
        }

        throw new IOException("Could not read response");

    }

    private String encode(String string) throws UnsupportedEncodingException {
        return URLEncoder.encode(string, "UTF-8");

    }

    private HttpURLConnection getGDataUrlConnection(String urlString)
            throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        return connection;

    }

    @Override
    public String getFreshAuthToken(String accountName, String authToken) {
        return getAuthToken(accountName);

    }

    public static class ClientLoginAuthorizerFactory implements
            AuthorizerFactory {
        public Authorizer getAuthorizer(Context context, String authTokenType) {
            return new ClientLoginAuthorizer(context);

        }
    }
}