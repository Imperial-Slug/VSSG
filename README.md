"VERY SIMPLE SHIP GAME"

A fun personal project, my first venture into 2D game programming using 
the Java libGDX framework.  The sprites are all homemade in GIMP on Gentoo Linux.  
The laser sounds are homemade using Audacity to alter recordings of my electric 
guitar hitting a note.

The project has Desktop, Android, HTML and iOS modules that are automatically managed while only the class files in the "core" directory are usually edited and added to.

It's currently setup with Gradle.  Download the latest version of Android Studio IDE and import the project if you want to work with the code.  


If you just want to run it on your desktop without opening a coding environment, you can simply run ./gradlew 


 HTML controls are currently broken.
 
To test the html version, you can spin up an http server.  Running this from your IDE terminal will do fine on a Linux system:

python -m http.server 8000
 
 Then go to  http://localhost:8000 and navigate to html/build/dist in that browser window.
 
 Helpful links:
 
 https://libgdx.com/wiki/deployment/deploying-your-application
