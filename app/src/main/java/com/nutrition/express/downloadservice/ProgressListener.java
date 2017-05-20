package com.nutrition.express.downloadservice;

/**
 * Created by huang on 4/15/17.
 */

interface ProgressListener {
    void update(long bytesRead, long contentLength, boolean done);
}
