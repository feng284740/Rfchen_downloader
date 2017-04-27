package com.rfchen.downloader;

import android.content.Context;
import android.content.Intent;

import java.sql.Time;

/**
 * Created by feng on 17/3/31.
 */

public class DownloadManager {

    private static final long MIN_OPERATE_INTERVAL = 1 * 500;
    private volatile static DownloadManager INSTANCE;
    private final Context context;
    private final DataChanger dataChanger;
    private long lastStamp;

    private DownloadManager(Context context) {
        this.context = context;
        dataChanger = DataChanger.getInstance(context);
        context.startService(new Intent(context, DownloadSevice.class));
    }

    public static DownloadManager getInstance(Context context) {

        if (INSTANCE == null) {
            synchronized (DownloadManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new DownloadManager(context);
                }
            }
        }
        return INSTANCE;
    }


    public void add(DownloadEntry entry) {
        if (!checkIfOperate()) {
            return;
        }
        Intent intent = new Intent(context, DownloadSevice.class);
        intent.putExtra(Constants.KEY_DOWNLOAD_ENTRY, entry);
        intent.putExtra(Constants.KEY_DOWNLOAD_ACTION, Constants.KEY_DOWNLOAD_ACTION_ADD);
        context.startService(intent);
    }

    public void pause(DownloadEntry entry) {
        if (!checkIfOperate()) {
            return;
        }
        Intent intent = new Intent(context, DownloadSevice.class);
        intent.putExtra(Constants.KEY_DOWNLOAD_ENTRY, entry);
        intent.putExtra(Constants.KEY_DOWNLOAD_ACTION, Constants.KEY_DOWNLOAD_ACTION_PAUSE);
        context.startService(intent);
    }

    public void resume(DownloadEntry entry) {
        if (!checkIfOperate()) {
            return;
        }
        Intent intent = new Intent(context, DownloadSevice.class);
        intent.putExtra(Constants.KEY_DOWNLOAD_ENTRY, entry);
        intent.putExtra(Constants.KEY_DOWNLOAD_ACTION, Constants.KEY_DOWNLOAD_ACTION_RESUME);
        context.startService(intent);
    }

    public void cancel(DownloadEntry entry) {
        if (!checkIfOperate()) {
            return;
        }
        Intent intent = new Intent(context, DownloadSevice.class);
        intent.putExtra(Constants.KEY_DOWNLOAD_ENTRY, entry);
        intent.putExtra(Constants.KEY_DOWNLOAD_ACTION, Constants.KEY_DOWNLOAD_ACTION_CANCEL);
        context.startService(intent);
    }

    public void pauseAll() {
        if (!checkIfOperate()) {
            return;
        }
        Intent intent = new Intent(context, DownloadSevice.class);
        intent.putExtra(Constants.KEY_DOWNLOAD_ACTION, Constants.KEY_DOWNLOAD_ACTION_PAUSEALL);
        context.startService(intent);
    }

    public void recoverAll() {
        if (!checkIfOperate()) {
            return;
        }
        Intent intent = new Intent(context, DownloadSevice.class);
        intent.putExtra(Constants.KEY_DOWNLOAD_ACTION, Constants.KEY_DOWNLOAD_ACTION_RECOVERALL);
        context.startService(intent);
    }

    public void addDataWatcher(DataWatcher dataWatcher) {
        if (!checkIfOperate()) {
            return;
        }
        dataChanger.addObserver(dataWatcher);
    }


    public void deleteDataWatcher(DataWatcher dataWatcher) {
        if (!checkIfOperate()) {
            return;
        }
        dataChanger.deleteObserver(dataWatcher);
    }


    private boolean checkIfOperate() {
        long currentTimeStamp = System.currentTimeMillis();
        if (currentTimeStamp - lastStamp > MIN_OPERATE_INTERVAL) {
            lastStamp = currentTimeStamp;
            return true;
        }
        return false;
    }

    public DownloadEntry queryLatestDownloadEntry(String id) {
        return dataChanger.queryLatestDownloadEntry(id);
    }
}
