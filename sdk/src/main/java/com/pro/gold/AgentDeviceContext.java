package com.pro.gold;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;

import org.json.JSONObject;

public final class AgentDeviceContext {

    private final String packageName;
    private final String applicationLabel;
    private final String versionName;
    private final long versionCode;
    private final String androidVersion;
    private final int sdkInt;
    private final String manufacturer;
    private final String model;
    private final String device;
    private final String product;

    private AgentDeviceContext(
            String packageName,
            String applicationLabel,
            String versionName,
            long versionCode,
            String androidVersion,
            int sdkInt,
            String manufacturer,
            String model,
            String device,
            String product
    ) {
        this.packageName = packageName;
        this.applicationLabel = applicationLabel;
        this.versionName = versionName;
        this.versionCode = versionCode;
        this.androidVersion = androidVersion;
        this.sdkInt = sdkInt;
        this.manufacturer = manufacturer;
        this.model = model;
        this.device = device;
        this.product = product;
    }

    public static AgentDeviceContext from(Context context) {

        if (context == null) {
            throw new IllegalArgumentException(
                    "Context cannot be null"
            );
        }

        Context appContext =
                context.getApplicationContext();

        PackageManager pm =
                appContext.getPackageManager();

        String packageName =
                appContext.getPackageName();

        String label = packageName;
        String versionName = "unknown";
        long versionCode = 0L;

        try {

            ApplicationInfo info =
                    pm.getApplicationInfo(
                            packageName,
                            0
                    );

            CharSequence value =
                    pm.getApplicationLabel(info);

            if (value != null) {
                label = value.toString();
            }

        } catch (PackageManager.NameNotFoundException ignored) {
        }

        try {

            PackageInfo info =
                    pm.getPackageInfo(
                            packageName,
                            0
                    );

            versionName =
                    info.versionName == null
                            ? "unknown"
                            : info.versionName;

            if (Build.VERSION.SDK_INT >= 28) {

                versionCode =
                        info.getLongVersionCode();

            } else {

                versionCode =
                        info.versionCode;
            }

        } catch (PackageManager.NameNotFoundException ignored) {
        }

        return new AgentDeviceContext(
                packageName,
                label,
                versionName,
                versionCode,
                Build.VERSION.RELEASE,
                Build.VERSION.SDK_INT,
                Build.MANUFACTURER,
                Build.MODEL,
                Build.DEVICE,
                Build.PRODUCT
        );
    }

    public String getPackageName() {
        return packageName;
    }

    public String getApplicationLabel() {
        return applicationLabel;
    }

    public String getVersionName() {
        return versionName;
    }

    public long getVersionCode() {
        return versionCode;
    }

    public String getAndroidVersion() {
        return androidVersion;
    }

    public int getSdkInt() {
        return sdkInt;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public String getModel() {
        return model;
    }

    public String getDevice() {
        return device;
    }

    public String getProduct() {
        return product;
    }

    public JSONObject toJson() {

        JSONObject json =
                new JSONObject();

        try {

            json.put(
                    "packageName",
                    packageName
            );

            json.put(
                    "applicationLabel",
                    applicationLabel
            );

            json.put(
                    "versionName",
                    versionName
            );

            json.put(
                    "versionCode",
                    versionCode
            );

            json.put(
                    "androidVersion",
                    androidVersion
            );

            json.put(
                    "sdkInt",
                    sdkInt
            );

            json.put(
                    "manufacturer",
                    manufacturer
            );

            json.put(
                    "model",
                    model
            );

            json.put(
                    "device",
                    device
            );

            json.put(
                    "product",
                    product
            );

        } catch (Exception exception) {

            throw new IllegalStateException(
                    "Unable to serialize device context",
                    exception
            );
        }

        return json;
    }
}
