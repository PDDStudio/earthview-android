# EarthView Android Library
A simple and easy to use API to integrate [EarthView with Google](http://earthview.withgoogle.com) into your android application.

[![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-earthview--android-green.svg?style=true)](https://android-arsenal.com/details/1/2902)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.pddstudio/earthview-android/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.pddstudio/earthview-android)

## Showcase
### Advanced Demo Application (Beta)
To showcase the different possibilities of this library I've created a demo 'Wallpaper Application'.
You can find the source for this application in the `ps-app/` folder.
In case you want to install the Application, feel free to do so! **It's on Google Play™**

[![](https://github.com/PDDStudio/earthview-android/blob/master/preview/en-play-badge.png)](https://play.google.com/store/apps/details?id=com.pddstudio.earthviewer)

### !! Translators needed !!
You can help translating EarthViewer into your native language via OneSkyApp: [Contribute to EarthViewer](http://goo.gl/z4218r)

### Advanced Demo Screenshot
![ ](https://github.com/PDDStudio/earthview-android/blob/master/preview/screenshot.png)
### Simple Demo Application
You can find a simple demo application in the `app/` folder which you can also download [here](https://github.com/PDDStudio/earthview-android/raw/master/app-debug.apk).

## Getting Started
Include the library into your project by adding the following line to your ```build.gradle```:
```compile 'com.pddstudio:earthview-android:[VERSION]'```
**Note:** *To make sure you're using the latest version, take a look at the maven badge above*

## Usage
 - Implement either `SingleEarthViewCallback` (for a single EarthView) or `EarthViewCallback` (for multiple EarthViews) into your Activity, Adapter or any other class where you want to load the EarthView's.
- Get an instance of the EarthView by calling:
```java
EarthView.withGoogle()
```
- Fire the event which fit's your needs, you'll get the results through the callback interface you provide in your project.

### Fetching a single EarthView

A quick sample to fetch a single EarthView

```java
public class MainActivity extends AppCompatActivity implements SingleEarthViewCallback {

    Button loadBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        loadBtn = (Button) findViewById(R.id.load_wall_btn);
        loadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
	        //in case you want to get a random wallpaper
            EarthView.withGoogle().getRandomEarthWallpaper(MainActivity.this);
	        //in case you want to get a specific EarthWallpaper
		    EarthView.withGoogle().getEarthWallpaper(earthWallpaperIdentifier, this);
            }
        });
    }
    @Override
    public void onStartedLoading() {
        //in case you want to show a loading dialog or anything else
    }

    @Override
    public void onFinishedLoading(EarthWallpaper earthWallpaper) {
        //check whether the result is null or not
        if(earthWallpaper != null) {
            //do whatever you want to do with the EarthWallpaper object
        }
    }


}
```

### Fetching multiple EarthViews

A quick sample to load multiple EarthViews:

```java
public class MainActivity extends AppCompatActivity implements EarthViewCallback {

    Button loadBtn;
   
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        loadBtn = (Button) findViewById(R.id.load_wall_btn);
        loadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EarthView.withGoogle().getAllEarthWallpapers(MainActivity.this);
            }
        });
    }

    @Override
    public void onStartedLoading() {
		//in case you want to show a loading dialog or something else you can do this here
    }

    @Override
    public void onItemLoaded(EarthWallpaper earthWallpaper) {
        //just to be sure the result isn't null
        if(earthWallpaper != null) {
		//here you can handle what you want to do with the new item - this event get's fired every time the library has loaded a new EarthView object
        }
    }

    @Override
    public void onFinishedLoading(Collection<EarthWallpaper> earthWallpapers) {
		//here you can stop showing the loading dialog
		//in case you don't want to handle each item seperately the complete Collection of EarthWallpapers is provided here, too 
    }
}
```

## The EarthWallpaper reference sheet
Once you received an EarthWallpaper object through the callback you can get the following information out of it:
- The EarthView's ID (identifier - similar to the original one)
- The EarthView's Slug
- The EarthView's Url (Link to the official Site for this EarthView)
- The EarthView's Title (Official Title for this EarthView)
- The EarthView's Latitude
- The EarthView's Longitude
- The EarthView's Photo Url (In case you want to load it into an ImageView or save it to your storage)
- The EarthView's Thumbnail Url (Recommended when fetching a lot of EarthViews - to reduce bandwith)
- The EarthView's Download Url (The official Url to download this EarthView)
- The EarthView's Region (Region where this EarthView was taken)
- The EarthView's Country (The Country this EarthView was taken)
- The EarthView's Attribution (Copyright information)
- The EarthView's GoogleMaps Url (To show the direct position of this EarthView)
- The EarthView's GoogleMaps Title (Can be used for wrapping around the GoogleMaps Url e.g)

All information can be fetched via their get-Methods.
You can find more information in the EarthView JavaDoc.

## Dependencies (Library)
- [Gson](https://github.com/google/gson)
- [Apache Commons IO](https://commons.apache.org/proper/commons-io/)
- [OkHttp](http://square.github.io/okhttp/)

## Dependencies (Simple Demo Application)
- [Picasso](http://square.github.io/picasso/)

**Note:** *Dependencies for the Advanced Demo can be found inside the application*

## About & Contact
- In case you've a question feel free to hit me up via E-Mail (patrick.pddstudio@googlemail.com) 
- or [Google+](http://plus.google.com/+PatrickJung42)

## Contributors
*A special thanks goes to everyone who contributed to this project!*
- [Alex Lionne](https://github.com/AlexLionne)

## License
    Copyright 2015 Patrick J

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.