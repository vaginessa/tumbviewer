package com.nutrition.express.model.data.bean;

/**
 * Created by huang on 11/22/16.
 */

public class TumblrApp {
    private transient long dayLimit, dayRemaining, dayReset;
    private transient long hourLimit, hourRemaining, hourReset;
    private transient long timestamp; //in seconds
    private String apiKey;
    private String apiSecret;
    private boolean using;

    public TumblrApp(String apiKey, String apiSecret) {
        this.apiKey = apiKey;
        this.apiSecret = apiSecret;
    }

    public String getApiKey() {
        return apiKey;
    }

    public String getApiSecret() {
        return apiSecret;
    }

    public boolean isUsing() {
        return using;
    }

    public void setUsing(boolean using) {
        this.using = using;
    }

    public boolean isOutOfLimit() {
        if (hourRemaining > 0) {
            return false;
        } else {
            if (System.currentTimeMillis() / 1000 - timestamp > hourReset) {
                return false;
            }
        }
        return true;
    }

    public long getDayLimit() {
        return dayLimit;
    }

    public long getDayRemaining() {
        return dayRemaining;
    }

    public long getDayReset() {
        return dayReset;
    }

    public long getHourLimit() {
        return hourLimit;
    }

    public long getHourRemaining() {
        return hourRemaining;
    }

    public long getHourReset() {
        return hourReset;
    }

    public void setDayLimit(String dayLimit) {
        try {
            this.dayLimit = Long.valueOf(dayLimit);
        } catch (NumberFormatException e) {
            this.dayLimit = 1;
        }
    }

    public void setDayRemaining(String dayRemaining) {
        try {
            this.dayRemaining = Long.valueOf(dayRemaining);
        } catch (NumberFormatException e) {
            this.dayRemaining = 1;
        }
    }

    public void setDayReset(String dayReset) {
        try {
            this.dayReset = Long.valueOf(dayReset);
        } catch (NumberFormatException e) {
            this.dayReset = 1;
        }
    }

    public void setHourLimit(String hourLimit) {
        try {
            this.hourLimit = Long.valueOf(hourLimit);
        } catch (NumberFormatException e) {
            this.hourLimit = 1;
        }
    }

    public void setHourRemaining(String hourRemaining) {
        try {
            this.hourRemaining = Long.valueOf(hourRemaining);
        } catch (NumberFormatException e) {
            this.hourRemaining = 1;
        }
    }

    public void setHourReset(String hourReset) {
        try {
            this.hourReset = Long.valueOf(hourReset);
        } catch (NumberFormatException e) {
            this.hourReset = 1;
        }
        timestamp = System.currentTimeMillis() / 1000;
    }

}
