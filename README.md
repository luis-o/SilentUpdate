[![License](https://img.shields.io/npm/l/mithril.svg)](https://www.npmjs.com/package/mithril)
[![](https://jitpack.io/v/caoyanglee/SilentUpdate.svg)](https://jitpack.io/#caoyanglee/SilentUpdate)
[![API](https://img.shields.io/badge/API-21%2B-brightgreen.svg?style=flat)](https://android-arsenal.com/api?level=21)

> kotlin <br>

A library silently & automatically download latest apk to update your App<br>

Demo
![](https://github.com/CaoyangLee/SilentUpdate/blob/master/gif/gif_demo.gif)

# Etapas de execução da estratégia dupla
1. Autoridade de julgamento real Realização do usuário】
2. Obtenha o link para download e julgue o número da versão [Implementação do usuário]
3. Antes de iniciar o download, determine se o arquivo de atualização existe, ** existência **: exibe o arquivo de instalação Diálogo e retorno de chamada (onFileIsExist)

一：Wifi<br>

4. When downloading, it is silent, there will be no notification bar showing progress
5. Download is complete, receive callback (onDownLoadSuccess), display Notification and Dialog
6. The user clicks DownloadSuccessDialog or Notification to jump to the installation interface

二：Two: traffic situation [users to operate by themselves] <br>

4. The UpdateDialog of the update app is displayed. After the user clicks update, the download operation begins
5. When downloading, the notification bar will show the download progress
5. After the download is complete, receive the callback (onDownLoadSuccess) and jump to the installation interface

## Ready to work 
1. Get dependencies

**project build.gradle**

```
allprojects {
    repositories {
        ......        
        maven { url 'https://jitpack.io' }
    }
}
```
**app build.gradle**
[![](https://jitpack.io/v/caoyanglee/SilentUpdate.svg)](https://jitpack.io/#caoyanglee/SilentUpdate)


> Note: Kotlin 1.3.31 version library is used by default
```gradle
implementation 'com.github.caoyanglee:SilentUpdate:{latestVersion}'
```

2.Increase permissions

```xml
<!-Link Network->
<uses-permission android:name="android.permission.INTERNET" />
<!-Check the network status->
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
<!-Storage permission->
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
<!-Notification permission->
<uses-permission android:name="android.permission.DOWNLOAD_WITHOUT_NOTIFICATION" />
<!-Compatible with 8.0->
<uses-permission android:name="android.permission.REQUEST_INSTALL_PACKAGES" />
```       
3. Add FileProvider [adaption 7.0]

>：Note```android:resource="@xml/filepaths"```Google or directly obtain [demo file] (https://github.com/CaoyangLee/SilentUpdate/blob/master/app/src/main/res/xml/filepaths.xml)

```xml
<provider
    android:name="android.support.v4.content.FileProvider"
    android:authorities="${applicationId}.fileprovider"
    android:exported="false"
    android:grantUriPermissions="true">
    <meta-data
        android:name="android.support.FILE_PROVIDER_PATHS"
        android:resource="@xml/filepaths" />
</provider>
```

> Note: The following is the operation of Kotlin, if you use Java, please click [here](https://github.com/CaoyangLee/SilentUpdateDemo/blob/master/README_JAVA.md)

4.Initialize in Application

```kotlin
SilentUpdate.init(this)
```

## Usage
> Note: <br>
apkUrl: apk download address provided by the server <br>
latestVersion: The server returns the latest version number of the client <br>
title: the title of the prompt <br>
msg: prompt content <br>
isForce: Is it mandatory <br>
extra: carry more data through extra <br>

```kotlin
SilentUpdate.update {
    this.apkUrl = it.apkUrl
    this.latestVersion = it.latestVersion
    this.title = "This is a custom title"
    this.msg = "This is a custom message"
    this.isForce = true
    this.extra = Bundle()
}
```

## Custom configuration
1. Set the time interval for prompting Dialog

```kotlin
SilentUpdate.intervalDay = 7 // If not set, the default is 7 days```

2. Replace the default popup <br>
> Note: Before executing the download task, it will be determined whether the update file exists, the flow mode is to call ** download ** Dialog, the flow mode, if the file already exists is to call ** install ** Dialog <br>


Many times the style of downloading Dialog and installing Dialog are the same, so you can share a DialogTipAction :)

```kotlin

// Download reminder-> flow mode
SilentUpdate.downLoadDialogShowAction = object : DialogShowAction {
    override fun show(context: ContextWrapper, updateContent: String, positiveClick: () -> Unit, negativeClick: () -> Unit) {
        AlertDialog.Builder(context)
                .setCancelable(false)
                .setTitle("prompt")
                .setMessage("Download prompt popup custom $ updateContent")
                .setPositiveButton("update immediately") { dialog, which -> positiveClick() }
                .setNegativeButton("Later") { dialog, which -> negativeClick() }
                .show()
    }

}
// Installation prompt-> wireless mode, the file already exists
SilentUpdate.installDialogShowAction = object : DialogShowAction {
    override fun show(context: ContextWrapper, updateContent: String, positiveClick: () -> Unit, negativeClick: () -> Unit) {
        AlertDialog.Builder(context)
                .setCancelable(false)
                .setTitle("prompt")
                .setMessage("Installation prompt popup customization $updateContent")
                .setPositiveButton("install now") { dialog, which -> positiveClick() }
                .setNegativeButton("Later") { dialog, which -> negativeClick() }
                .show()
    }
}
```

