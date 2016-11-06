package com.nutrition.express.util;

import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.content.LocalBroadcastManager;

import com.facebook.common.references.CloseableReference;
import com.facebook.datasource.BaseDataSubscriber;
import com.facebook.datasource.DataSource;
import com.facebook.datasource.DataSubscriber;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.core.ImagePipeline;
import com.facebook.imagepipeline.memory.PooledByteBuffer;
import com.facebook.imagepipeline.request.ImageRequest;
import com.nutrition.express.application.ExpressApplication;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * Created by huang on 11/5/16.
 */

public class FrescoUtils {
    public static void save(final Uri uri, final String action) {
        ImageRequest request = ImageRequest.fromUri(uri);
        ImagePipeline pipeline = Fresco.getImagePipeline();
        DataSource<CloseableReference<PooledByteBuffer>> dataSource =
                pipeline.fetchEncodedImage(request, null);

        DataSubscriber<CloseableReference<PooledByteBuffer>> dataSubscriber =
                new BaseDataSubscriber<CloseableReference<PooledByteBuffer>>() {
                    @Override
                    protected void onNewResultImpl(DataSource<CloseableReference<PooledByteBuffer>> dataSource) {
                        if (!dataSource.isFinished()) {
                            return;
                        }
                        CloseableReference<PooledByteBuffer> ref = dataSource.getResult();
                        FileOutputStream outputStream = null;
                        if (ref != null) {
                            try {
                                File dir = new File(Environment.getExternalStorageDirectory(), "tumblr_image");
                                if (!dir.exists()) {
                                    dir.mkdirs();
                                }
                                File file = new File(dir, uri.getLastPathSegment());
                                PooledByteBuffer buffer = ref.get();
                                if (!file.exists() || file.length() != buffer.size()) {
                                    outputStream = new FileOutputStream(file);
                                    byte[] bytes = new byte[buffer.size()];
                                    buffer.read(0, bytes, 0, buffer.size());
                                    outputStream.write(bytes);
                                }
                                Intent intent = new Intent(action);
                                intent.putExtra("success", true);
                                LocalBroadcastManager
                                        .getInstance(ExpressApplication.getApplication())
                                        .sendBroadcast(intent);
                            } catch (IOException e) {
                                e.printStackTrace();
                                onFailureImpl(dataSource);
                            } finally {
                                CloseableReference.closeSafely(ref);
                                ref = null;
                                try {
                                    if (outputStream != null) {
                                        outputStream.close();
                                    }
                                } catch (IOException e) {
                                }
                            }
                        }
                    }

                    @Override
                    protected void onFailureImpl(DataSource<CloseableReference<PooledByteBuffer>> dataSource) {
                        Intent intent = new Intent(action);
                        intent.putExtra("success", false);
                        LocalBroadcastManager
                                .getInstance(ExpressApplication.getApplication())
                                .sendBroadcast(intent);
                    }
                };

        dataSource.subscribe(dataSubscriber,
                ExpressApplication.getApplication().getImagePipelineConfig()
                        .getExecutorSupplier().forLocalStorageWrite());
    }


    public static void saveAll(List<String> urls, final String action) {

        //// TODO: 11/6/16
    }
}
