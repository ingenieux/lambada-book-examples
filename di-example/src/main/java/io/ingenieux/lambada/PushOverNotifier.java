/*
 * Copyright (c) 2016 ingenieux Labs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package io.ingenieux.lambada;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

@Singleton
public class PushOverNotifier {
    @Inject
    OkHttpClient okHttpClient;

    @Inject
    @Named("token")
    String token;

    @Inject
    @Named("user")
    String user;

    public void sendMessage(String message) throws Exception {
        RequestBody requestBody = new FormBody.Builder()
                .add("token", this.token)
                .add("user", this.user)
                .add("message", message)
                .build();

        Request request = new Request.Builder()
                .url("https://api.pushover.net/1/messages.json")
                .post(requestBody)
                .build();

        Response response = okHttpClient.newCall(request).execute();

        if (200 != response.code())
            throw new IllegalStateException("Error in request: " +
                    response.code() + " " +
                    response.body().string());
    }
}
