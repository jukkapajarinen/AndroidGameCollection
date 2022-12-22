# AndroidGameLoop
This project is an android-sdk game template made with 100% custom 2D game-loop by @jukkapajarinen.  
I decided to make it open source and to publish it with MIT license. Have fun with it! :grin:

## Installation
```bash
git clone https://jukkapajarinen@bitbucket.org/jukkapajarinen/androidgameloop.git
cp -R androidgameloop/* {{YOUR_PROJECT_FOLDER}}/app/src/main/java/
```
That's it, really. I wanted to keep it as simple as possible.

## Classes
* **Game**: GameObject. Extend this and write your game logic to it.
* **GameActivity**: Launcher for GameView. Extend this and call start() to launch the game.
* **GameHelpers**: Static convenience class for working with Android pixels and dimensions.
* **GameSprite**: SpriteObject. Extend this to create your sprites. It has x, y, speed, image, etc.
* **GameThread**: 60fps game-loop itself. Designed to call update() and draw() for GameObject.
* **GameView**: Extends SurfaceView. Initializes GameThread and starts it with your Game.

## Basic usage
```java
public class MainActivity extends GameActivity {
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    MyGame myGame = new MyGame(this);
    start(myGame);
  }
}
```
```java
public class MyGame extends Game {
  private final MySprite mySprite;

  public MyGame(Context context) {
    super(context);
    // Create your sprite here
    Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.sprite);
    mySprite = new MySprite(bitmap, 0xff00ff00, SCREEN_W / 2, SCREEN_H / 2, dp(21), dp(21), 1, 1, 5);
  }

  public boolean handleTouchEvent(MotionEvent event) {
    // Handle your input
    for(int i = 0 ; i < event.getPointerCount() ; i++) {
      if (event.getActionMasked() == MotionEvent.ACTION_MOVE) {
        mySprite.move(event.getX(i) - mySprite.getWidth() / 2, event.getY(i) - mySprite.getHeight() / 2);
      }
    }
    return true;
  }

  public void handleUpdate() {
    // Update your stuff here
    mySprite.update();
  }

  public void handleDraw(Canvas canvas, Paint paint) {
    // Draw your stuff here
    mySprite.draw(canvas, paint);
  }
}
```
```java
public class MySprite extends GameSprite {
  public MySprite(Bitmap image, int tintColor, float x, float y, int width, int height, float xDir, float yDir, float velocity) {
    super(image, tintColor, x, y, width, height, xDir, yDir, velocity);
  }

  @Override
  public void update() {
    // Update your sprite here
  }
}
```

## License

Copyright (c) 2017 Jukka Pajarinen

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.