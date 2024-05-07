# Welcome to Trident!

Trident is a framework game engine built with and using the Java programming language.
*Trident uses Java 8*

## Basic setup and controls tutorial:
> https://www.youtube.com/watch?v=0pgDlQ04KHU&t=31s



# Documentation

*Please note this documentation is for the default new project. All methods can be altered by you, and updates to the engine will not update old games' methods. Also, this will not contain any documentation for anything built into the Java language, even if it's frequently used throughout the engine.*

## custom/

Put the custom classes you write that aren't entities in here.

## ent/

Put your custom entity classes in here

### ExampleEntity.java
*Template for custom entities*

> `ExampleEntity(Position pos)` or `ExampleEntity(Position pos, Dimension coll)`

Builds the entity with one of two superclass constructors: `super(Position pos)` and `super(Position pos, Dimension collision)`.

`super(Position pos)` will place the entity at `pos` with no collision

`super(Position pos, Dimension collision)` will place the entity at `pos` with the collision defined by the `collision` `Dimension` object. You can use this to either make a set dimension or one that is set in the editor. For example,
```
public ExampleEntity(Position pos){
	super(pos, new Dimension(32, 16));
}
```
would create an entity with a dimension of `32x16`. And then,
```
public ExampleEntity(Position pos, Dimension coll){
	super(pos, coll);
}
```
could be used to make the editor pass through a Dimension with the `construct(Position pos, Dimension collision, int[] data)` method.

> `ExampleEntity()` or "registry constructor"

Register the entity in the entity registry.
Use `super(String n, boolean hasColl, int numData);`.
For example, if we wanted an entity with the name "`enemy`" that doesn't have collision that's set in the editor and has 1 piece of data sent from the editor, we would write `super("enemy", false, 1);`.
Important note: the `hasColl` variable means the editor can set the collision, it does not mean whether or not the entity has collision.

Keep in mind that choices in code will not affect the editor, so you have to keep things consistent. For example, if you make an entity with the registry arguments `("gunpickup", false, 1)` , you will have to name a custom entity "gunpickup" (yes, it's case-sensitive), click `no` when prompted if it should have collision, and type '1' for the amount of data it has (as well as whatever you want on that 1 data in the next popup).

> `TridEntity construct(Position pos, Dimension collision, int[] data)`

Used to place the entity in the world from the editor.
Depending on the arguments passed through in the registry constructor, `collision` could be `null`, and `data` could be any length.

`collision` is null if `hasColl` was set to `false` in the registry constructor, but will have a value if set to `true`. 

> `void render(Graphics g, JPanel panel, int x, int y)`

Renders the entity.
g = Graphics object for rendering
panel = the panel
x, y = the screen location the entity should render at

> `void update(long elapsedTime)`

Runs every 'tick'. Use this for things like game logic.

> `void sceneStart(String scene)`

Runs when the scene is loaded.


## trident/

Main Trident classes. Don't edit these unless you know what you're doing.

### Main.java
The main class. Use this to run the game.

### MainPanel.java
The interface in the window. This updates the game and draws what is rendered.

### RenderingThread.java
Renders the game on a separate thread.

### Scene.java
Holds a scene.

> `String name`

Name of the scene. Used to load scenes, and can be used to check what scene you're currently in.

### Trident.java
Main interface with the engine.

 ***Debug Settings***
 
> `boolean drawPos = false;`

Toggles drawing the player's position in the top left

> `boolean drawCollision = false;`

Toggles rendering the collision

> `boolean noclip = false;`

Toggles noclip

> `boolean engineDraw = false;`

Toggles drawing entities as seen in the engine

> `Color debugColor = Color.red;`

Changes the font color for drawPos and drawFrames

> `boolean intro = true;`

Toggles the intro splash screen at startup

> `ImageIcon splash = null;`

Image drawn below the engine splash screen at startup

> `boolean drawFrames = false;`

Toggles drawing the framerate and tickrate.
Framerate and tickrate are expressed as such:
```
TPS: -- (--ms)
FPS: -- (--ms)
```

> `boolean consoleEnabled = true;`

Enables or disables the developer console accessible with the tilde key

> `boolean fullbright = false;`

Toggles fullbright


 ***Public Variables***
 
> `Point mousePos;`

Current mouse position

> `Point mouseDelta;`

How much the mouse moved

> `boolean drawPlayer = true;`

Toggles whether or not the player is rendered with the default rendering

> `Position mouseWorldPos = new Position();`

Mouse position translated into the world

> `boolean enableBloom = true, enableExposure = true;`

Toggles bloom and exposure (experimental, kills performance)


 ***Methods***

> `void setPlrSpeed(double speed)`

Sets the player speed

> `void setPlrPos(Position pos)`

Sets the player position

> `void setShortCollision(boolean b)`

Toggles whether or not the player has short collision
true = half of sprite height
false = exact sprite height

> `void setWindowTitle(String title)`

Sets the title of the window

> `void setupScenes()`

Reads and mounts the scenes from the files in data/scenes/

> `void loadScene(String name)`

Load a scene (must be mounted first with setupScenes())

> `void addCustomEntity(TridEntity e)`

Registers a custom entity.
Must use the registry constructor (e.g. `Trident.addCustomEntity(new ExampleEntity());`)

> `void spawnEntity(TridEntity e)`

Spawn an entity in the current scene.

> `void setDefaultScene(String s)`

Sets the default scene when starting the game. Keep in mind this doesn't do anything during runtime, and is only useful during setup

> `void destroy(TridEntity object)`

Removes an object from the current scene. Keep in mind that if the object is referenced elsewhere, it will technically still be alive, but won't render or update.

> `void shakeCam(double intensity)`

Add 'trauma' to the camera shake system. 0 is minimum, 1 is maximum.

> `void removeShake()`

Set the camera shake's 'trauma' to 0, stopping any current shaking.

> `void setShakeStrength(int str)`

Sets the strength of the camera shake system. `str` is in pixels, as in if the current 'trauma' is 1, or max, the camera can move `str` pixels away from it's actual position.

> `void setShakeLoss(double loss)`

Sets how fast the camera shake loses 'trauma' per millisecond. 

For example, if `loss` is 0.0003 (the default setting), and 16 milliseconds pass in a tick, the camera shake system's 'trauma' will lose 0.0048 'trauma' or 0.48% 'trauma' in one tick. This may sound like very little, but remember the game is often running around 60 ticks per second. In one second, the camera shake would lose 30% 'trauma', meaning it would take just over 3 seconds to go from 100% 'trauma' to 0%.

> `void setBloom(double amount)`

Sets the amount of bloom to use in post-processing. This feature is experimental and causes significant performance issues.

> `void setExposure(double exp)`

Sets the amount of exposure to use in post-processing. This feature is experimental and causes strange rendering.

> `void setLightBlur(int level)`

Sets the lighting system to blur the lighting `level` times. It's recommended to leave it at 1, the default, since it has a good balance between performance and quality.

> `void addLight(Light l)`

Adds a light to the light list. If you want an entity to spawn during runtime, and have a light with it, you have to use this method. Make sure you also remove the light before the entity is deleted, otherwise you could end up with a rogue light.

> `void resetKeys()`

You can run this method and it will reset all keys to be not pressed, as well as all mouse buttons to not be pressed.

> `void setPlrSprite(String path)`

Sets a new filepath to be where the player looks for its spritesheets.

> `void removeLight(Light l)`

Removes a light from the light list.

> `void setDefaultLight(int level)`

Sets the default light level for the scene. 0 is completely dark, 255 is completely bright. You can use this during runtime to make something like a day-night cycle, or dynamically change the lighting when entering a dark area.

> `double getPlrSpeed()`

Returns the player's current speed.

> `Position getPlrPos()`

Returns a copy of the player's position. Editing this `Position` object will not change the player's location.

> `Scene getCurrentScene()`

Returns the current scene as a `Scene` object. If you want to get the scene's name, you can use `Trident.getCurrentScene().name`.

> `boolean getFullscreen()`

Returns a `boolean` to say if the game is in fullscreen. `true` means it's in fullscreen, `false` means it's in windowed.

> `ArrayList<Entity> tridArrToEntArr(ArrayList<TridEntity> entities)`

Translates a `TridEntity` list into an `Entity` list. Since `TridEntity` is an engine-specific class, and `Entity` is a library-specific class, this could be used to make an engine's entity list compatible with some library methods that take `Entity` lists.

> `ArrayList<TridEntity> entArrToTridArr(ArrayList<Entity> entities)`

Translates an `Entity` list into a `TridEntity` list. Similarly to the previous method, it could be used to make library methods that return `Entity` lists compatible with the engine.

> `ArrayList<TridEntity> getEntities()`

Gets the `TridEntity` list of the current scene. This is useful when looking for other entities.

> `ArrayList<Rectangle> getCollision()`

Gets the collision from all entities in the scene, expressed as `Rectangle` objects

> `boolean getMouseDown(int mb)`

Returns if the mouse button `mb` is pressed, from 1 to 5.
```
1 = left click
2 = middle click
3 = right click
4, 5 = mouse button 4, 5 (side buttons on specific mice)
```

> `boolean getKeyDown(int key)`

Returns if the key `key` is pressed, using the `java.awt.event.KeyEvent` keycodes. For example, if you import `java.awt.event.*` or `java.awt.event.KeyEvent`, you can use `KeyEvent.VK_W` to represent the 'W' key. Like this: `Trident.getKeyDown(KeyEvent.VK_W);`

> `Player getPlr()`

Gets the `Player` object.

> `int getFrameWidth()` and `int getFrameHeight()`

Gets the set frame width and height, respectively.


### TridEntity.java
Meant to be a superclass for all custom entities

> `final boolean HASCOLLISION`

It's in the name. `true` if it has collision, `false` if it doesn't.

> `TridEntity(Position pos)`

Constructor that makes an entity with no collision.

> `TridEntity(Position pos, Dimension collision)`

Constructor that makes an entity with collision as defined in `collision`.

> `TridEntity construct(Position pos, Dimension collision, int[] data)`

Prints "Error: tried to create an empty entity" and returns `null`.

> `Rectangle getCollision()`

Gets the collision of the entity. Can be overridden, for example if you want a door that returns a `Rectangle(0, 0, 0, 0)` collision when open, and the default collision when closed.

> `void render(Graphics g, JPanel panel, int x, int y)`

Refer to ExampleEntity.java.

> `void engineRender(Graphics g, JPanel panel, int x, int y)`

Renders the entity as it would be in the engine. Can be overridden, for example if the game would be too difficult to navigate without it rendering properly.

> `void update(long elapsedTime)`

Refer to ExampleEntity.java.

> `void sceneStart(String scene)`

Refer to ExampleEntity.java.


### ent/
Contains the default engine entities. Not typically used during runtime.

#### BoxColl.java
Colored box that has collision.

> `BoxColl(Position pos, Dimension size, Color c)`

`pos` = position of the box.
`size` = size of the box.
`c` = color of the box.

#### BoxNoColl.java
Colored box with no collision.

> `BoxNoColl(Position pos, Color c, int w, int h)`

`pos` = position of the box.
`c` = color of the box.
`w`, `h` = width and height of the box.

#### InvisColl.java
Invisible collision.

> `InvisColl(Position pos, Dimension size)`

`pos` = position of the collision.
`size` = size of the collision.

#### PlrStart.java
Where the player starts in a scene. Note that the engine will only take the first one it finds, or the oldest.

> `PlrStart(Position pos)`

I mean do I really need to tell you what `pos` means at this point?

#### TridLight.java
Spawns a light, only really useful in engine.

> `TridLight(Position pos, int r)`

`pos` = you guessed it, position of the light.
`r` = radius of the light.

#### Trigger.java
Runs code in the `Update` class every tick that the player is inside the bounds.

> `Trigger(Position pos, Dimension size, int i)`

`pos` = position of the trigger.
`size` = size of the trigger.
`i` = ID of the trigger. This is sent through when running the `Update.trigger(int id)` method.

## update/

Contains the main ways you, as the developer, will interact with the game, other than entities.

### Inputs.java
Runs code whenever an input is detected.

> `void keyPressed(int key)`

Runs when a key is pressed. `key` uses the `java.awt.event.KeyEvent` keycodes. See trident/Trident.java, method `getKeyDown(int key)`.

> `void mousePressed(int mb, Point mousePos, Position worldPos)`

Runs when a mouse button is pressed.
`mb` = mouse button that was pressed (see trident/Trident.java, method `getMouseDown(int mb)`).
`mousePos` = mouse position on the screen.
`worldPos` = mouse position in the game world (useful for clicking on entities).

> `void onScroll(int scroll)`

Runs when the mouse wheel is scrolled. `scroll` is the scroll direction, where `-1` is up/forward and `1` is down/backward.

### Update.java
Contains things like setup, update, and trigger. Used to interface with the engine.

> `void setup()`

Runs before the game starts to set things up. Such as adding custom entities to the registry, setting the settings, and setting the splash screen image.

> `void sceneStart(String scene)`

Runs when a new scene is loaded. `scene` is the name of the scene.

> `void update(long elapsedTime)`

Runs once every 'tick'. `elapsedTime` is the milliseconds since the last tick.

> `void trigger(int id)`

Run when the player is in a `Trigger` object. `id` is the ID of the `Trigger` object.

> `void tridentEvent(int id)`

Run when Trident does various things. Current events:
```
*Trident.EVENT_SCREENSHOT*
	Runs when the player takes a screenshot
```

> `int command(ArrayList<String> cmdParts)`

Lets you add more commands to the developer console accessible through the tilde key. Return 0 if the command is recognized, 1 if it is not.