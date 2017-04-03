libGDX Super Mario Bros.
==

A Super Mario Bros. level 1 clone for the desktop and Android game built in **JAVA** with _libGDX_ simply to demonstrate basics of _libGDX_. This is, by no means, a complete video game and it puts no effort into being one.

----------

#### Development Environment Setup Instructions
Setting Up Andriod Studio 
1. Edit Run/Debug Configurations
2. Add a application configuration
  A. Name: desktop
  B. Main Class: net.kaparis.game.supermariobros.desktop.DesktopLauncher
  C. Working Directory: ../supermariobros-master/android/assets 
3. Apply Settings. 
4. Use the "Desktop" configuration to run and debug the game. 

Note: Andriod configuration is there by default.

#### Building a Distribution of the game
Grab the source
```
git clone https://github.com/kaparis/superMarioBros.git
cd superMarioBros
gradlew desktop:dist  // Create a runnable JAR file located in the desktop/build/libs/
gradlew android:assembleRelease // This will create an unsigned APK file in the android/build/outputs/apk folder
```

#### Running the game
The following commands should be enough to run the game in any desktop platform with **JAVA** 1.8+ installed.

Grab the source
```
git clone https://github.com/kaparis/superMarioBros.git
cd superMarioBros
gradlew desktop:run // Runs on desktop
gradlew android:installDebug android:run // Runs on first connected emulator or device
```

Or go to the Github releases section to grab the desktop JAR and Andriod APK.

#### Controls
* Player moves: Pressing the `←`, `↑`, `↓ `, `→` keys.
* Jumping/Swimming: Pressing `↑` or virtual screen key `A` (No swimming level is implemented)
* Fire Ball/Run: by pressing `space` or virtual screen key `B`

* Collect coins for points
* If you fall down a pit, you'll lose a life.
* There is an arbitrary ticking clock. Provides a sense of imminent doom.

#### Technologies
This project was developed using the following frameworks and tools:

 - [libGDX](http://libgdx.badlogicgames.com/) - A great **JAVA** game development framework.
 - [Box2D](http://box2d.org/) - Physics engine. _libGDX_ has a built-in **JAVA** implementation of it.
 - [Tiled](http://www.mapeditor.org) - Free tile map editor. Used as a level editor, as well as laying out physics related objects.
 - [libgdx Texture Packer GUI](https://code.google.com/archive/p/libgdx-texturepacker-gui/downloads) - Creation of sprite sheets. Allows exporting to _libGDX_ atlas format.

#### Platforms Supported
_libGDX_ lets your write your game once in **JAVA**, and deploy everywhere. Using the _libGDX_, you can select the platforms you want to support on project creation. I selected all platforms. Here are some notes about each target platform.

- **Desktop** - Works great and is how I primarily tested the game.
- **Android** - I developed the game using Android Studio, and was easily able to deploy to Android.
- **HTML** - I tried to deploy to HTML as well. However, I learned the backend "GWT" doesn't support the use of reflection in your code.
- **IOS** - I don't have a MAC, so wasn't able to try this one out.
