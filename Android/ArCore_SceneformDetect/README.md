# ArCore_SceneformDetect
 This application shows a gallery of objects that can be placed, or anchored in the augmented world. You can then take a photo of the AR scene and save it to Photos.
 
# Problem what I'd met
1. [E/ACameraMetadata: getConstEntry: cannot find metadata tag](https://github.com/google-ar/arcore-android-sdk/issues/982)

	After ARCore had been detect the plan,and touch the screen in order to put 3D models on the plan,unfortunately it crash with this log.
	In tutorial it use  `com.google.ar.sceneform.ux:sceneform-ux:1.8.0` only,therefore change to below and it works good. 

	`implementation 'com.google.ar:core:1.15.0'`
    
	`implementation 'com.google.ar.sceneform.ux:sceneform-ux:1.15.0'`
    
	`implementation 'com.google.ar.sceneform:core:1.15.0'`

2. android.support.v4.content.FileProvider not found in AndroidManifest.xml
	Hence, I change android.support.v4.content.FileProvider to androidx.core.content.FileProvider.
```
	<provider 
	android:name="androidx.core.content.FileProvider"
	android:authorities="${applicationId}.fileprovider"
	android:exported="false"
	android:grantUriPermissions="true"> 
	</provider>
```

	Also modify the build.gradle(app) appcompat to "v7:28.0.0"

# Reference
[Codelabs.developers](https://codelabs.developers.google.com/codelabs/sceneform-intro/index.html?index=..%2F..io2018#0)
