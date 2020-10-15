# iPin7 App
-------
2020/10/15 
第二次再寫的 Output file, 還是花了不少時間再研究如何 Output log file.
雖然開啟權限以外, 還遇到其他問題
    ```
     <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>
     <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"/>
    ```
故來記錄一下

1. 在比較高版本的android 系統上無法繼續使用: `android.support.v4.content.FileProvider` 
    需要在AndroidManifest.xml 修改為:   
    ```
    <provider
            android:name="androidx.core.content.FileProvider"
            android:authorities="${applicationId}"
            android:exported="false"
            android:grantUriPermissions="true">
            <meta-data
                android:name="android.support.FILE_PROVIDER_PATHS"
                android:resource="@xml/images" />
        </provider>
    ```
2. 也要在AndroidManifest.xml Application 內加上`android:requestLegacyExternalStorage="true"`
    一直忘記要加這行, 不然會導致系統出現 `open failed: EACCES (Permission denied)` 的問題
    以下為官方的解釋:
    > **Scoped storage enforcement:**
    Apps that run on Android 11 but `target Android 10 (API level 29) can still request the requestLegacyExternalStorage attribute`. This flag allows apps to temporarily opt out of the changes associated with scoped storage, such as granting access to different directories and different types of media files. After you update your app to target Android 11, the system ignores the requestLegacyExternalStorage flag.