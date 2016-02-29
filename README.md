# Places Autocomplete API to get GPS coordinates 

## Overview
The autocomplete service in the Google Places API for Android returns place predictions in response to user search queries. As the user types, the autocomplete service returns suggestions for places such as businesses, addresses and points of interest.

## Inspiration
Go to the [Places Autocomplete API to get GPS coordinates from address entered][4]

## Get Google API Key Server

1. Go to the [Google Console][3]
2. Create a new project / open project
3. Click on 'APIs & Auth' on the left
4. Find the 'Google Cloud Messaging for Android' option, and press off to turn it on
5. Go to the creditials tab on the left
6. Go the 'Public API access' section and click 'Create new key'
7. Choose 'Server key' and click 'Create'
8. The API key is now shown under the section 'Key for server applications'

Let's take care of the depency and library:

```
android {
  	useLibrary 'org.apache.http.legacy'
}

dependencies {
    compile 'com.android.support:appcompat-v7:23.1.0'
    compile 'com.google.android.gms:play-services:8.3.0'
	compile 'com.android.support:cardview-v7:23.1.+'
}
```

Don't forget Android App Permissions in AndroidManifest:

```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:name="com.google.android.providers.gsf.permission.READ_GSERVICES" />
<uses-permission android:name="{your_package}.permission.MAPS_RECEIVE" />
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
<!-- EXTERNAL_STORAGE permissions are optional for Android 6.0 onwards. -->
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
<permission android:name="{your_package}.permission.MAPS_RECEIVE" android:protectionLevel="signature" />
    
```

Within Application 
```xml
<!-- The API key for Google Maps-based APIs is defined as a string resource.
            (See the file "res/values/google_maps_api.xml").-->
<meta-data android:name="com.google.android.geo.API_KEY"
           android:value="@string/google_maps_key" />

<meta-data android:name="com.google.android.gms.version"
           android:value="@integer/google_play_services_version" />
   
```

Support
----
 - [Google Maps APOs for Android][1]
 - [Google Places API for Android][2]
 - [Google Places API Web Service][5]
 

Screenshots
----
![alt tag](https://github.com/cleidimarviana/places-autocomplete-and-gps-coordinates/blob/master/screenshots/screenshot.png "Map")

Feedback
----
Questions, comments, and feedback are welcome at cleidimarviana@gmail.com

License
----

The MIT License (MIT)

Copyright (c) 2015 Cleidimar Viana 

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.

**Free Software, Hell Yeah!**

  [1]: https://developers.google.com/maps/android/?hl=pt-br
  [2]: https://developers.google.com/places/android-api/?hl=pt-br
  [3]: https://console.developers.google.com
  [4]: http://stackoverflow.com/questions/14946169/places-autocomplete-api-to-get-gps-coordinates-from-address-entered
  [5]: https://developers.google.com/places/web-service/details
