package com.nutrition.express.downloadservice;


import java.util.List;

/**
 * Created by huang on 4/23/17.
 */

class TransferList {
    DownloadService.DownloadListener listener;
    List<TransferRequest> requestList;

    TransferList(DownloadService.DownloadListener listener, List<TransferRequest> requestList) {
        this.listener = listener;
        this.requestList = requestList;
    }
}
