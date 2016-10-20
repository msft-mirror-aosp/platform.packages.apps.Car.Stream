/*
 * Copyright (c) 2016, The Android Open Source Project
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
package com.android.car.stream;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.annotation.CallSuper;
import android.util.Log;

/**
 * A base class that produces {@link StreamCard} for the StreamService
 */
public abstract class StreamProducer {
    private static final String TAG = "StreamProducer";

    private StreamService mStreamService;
    protected Context mContext;

    public StreamProducer(Context context) {
        mContext = context;
    }

    public final boolean postCard(StreamCard card) {
        if (mStreamService != null) {
            mStreamService.addStreamCard(card);
            return true;
        }
        if (Log.isLoggable(TAG, Log.DEBUG)) {
            Log.d(TAG, "StreamService not found, unable to post card");
        }
        return false;
    }

    public final boolean removeCard(StreamCard card) {
        if (mStreamService != null) {
            mStreamService.removeStreamCard(card);
            return true;
        }
        if (Log.isLoggable(TAG, Log.DEBUG)) {
            Log.d(TAG, "StreamService not found, unable to remove card");
        }
        return false;
    }

    public void onCardDismissed(StreamCard card) {
        // Handle when a StreamCard is dismissed.
        if (Log.isLoggable(TAG, Log.DEBUG)) {
            Log.d(TAG, "Stream Card dismissed: " + card);
        }
    }

    /**
     * Start the producer and connect to the {@link StreamService}
     */
    @CallSuper
    public void start() {
        Intent streamServiceIntent = new Intent(mContext, StreamService.class);
        streamServiceIntent.setAction(StreamConstants.STREAM_PRODUCER_BIND_ACTION);
        mContext.bindService(streamServiceIntent, mServiceConnection, 0 /* flags */);
    }

    /**
     * Stop the producer.
     */
    @CallSuper
    public void stop() {
        mContext.unbindService(mServiceConnection);
    }

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            StreamService.StreamProducerBinder binder
                    = (StreamService.StreamProducerBinder) service;
            mStreamService = binder.getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mStreamService = null;
        }
    };
}