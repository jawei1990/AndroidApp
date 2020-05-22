# ArCore_SceneformDetect
 This application shows a gallery of objects that can be placed, or anchored in the augmented world. You can then take a photo of the AR scene and save it to Photos.
 
# Import ARCore

1. Add `classpath 'com.google.ar.sceneform:plugin:1.15.0'` in build.gradle(Project) dependencies

2. Add in build.gradle(App) dependencies
````
implementation 'com.google.ar:core:1.15.0'
implementation 'com.google.ar.sceneform.ux:sceneform-ux:1.15.0'
implementation 'com.google.ar.sceneform:core:1.15.0'
````

3. Add below in manifest.xml
````
 <uses-feature
        android:name="android.hardware.camera.ar"
        android:required="true" />
````
````
<application
....>
	<meta-data
            android:name="com.google.ar.core"
            android:value="required" />
</application>
````
 
# Problem what I'd met
1. [E/ACameraMetadata: getConstEntry: cannot find metadata tag](https://github.com/google-ar/arcore-android-sdk/issues/982)

	After ARCore had been detect the plan,and touch the screen in order to put 3D models on the plan,unfortunately it crash with this log.
	In tutorial it use  `com.google.ar.sceneform.ux:sceneform-ux:1.8.0` only,therefore change to below and it works good. 

	`implementation 'com.google.ar:core:1.15.0'`
    
	`implementation 'com.google.ar.sceneform.ux:sceneform-ux:1.15.0'`
    
	`implementation 'com.google.ar.sceneform:core:1.15.0'`

2. android.support.v4.content.FileProvider not found in AndroidManifest.xml
	Hence, I change android.support.v4.content.FileProvider to androidx.core.content.FileProvider.
````
	<provider 
	android:name="androidx.core.content.FileProvider"
	android:authorities="${applicationId}.fileprovider"
	android:exported="false"
	android:grantUriPermissions="true"> 
	</provider>
````
	
Also modify the build.gradle(app) appcompat to "v7:28.0.0"

# Reference
[Codelabs.developers](https://codelabs.developers.google.com/codelabs/sceneform-intro/index.html?index=..%2F..io2018#0)
