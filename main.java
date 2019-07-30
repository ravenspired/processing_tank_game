//Final Project
//Anton Voronov
//Global Variables

//high score = 177
SceneSwitcher scsw; //defining the entities in the game
Sprite player,
smbulletbase,
enemyBase,
medkit,
kaboomBase,
minigun,
coilgun;


boolean up, //defining controls
down,
left,
right;
boolean firing, isPlayerDead, usingCoil; //true or false game features

//Editable variables
float pAngle = 0; //default player's angle
int playerHP = 100; //player's starting health
int sTimer = 30; //default gun reload speed
int maxTimer = 0; //used for increasing enemy spawn rate
float spawnDelay = 100; //delay of enemy spawn
float timerIncrease = 0; //the timer stays constant
int firingSpeed = 60; //default gun shooting speed
float prevX = 0; //records players position in the previous frame
float prevY = 0; //records players position in the previous frame
int tickSpeed = 60; //determines how fast items drop
int spawnSpeed = 60; // how fast enemies start spawning
int kaboomSpeed = 60; //determines how long dead tanks are displayed on screen
int playerScore = 0; //player's score starts at zero

boolean pause = false;

Enemy eToBeRemoved; //sets up enemy

KTAudioController ktAudio; //audio setup
KTSound clickEffect;


String weapon = "gun";//default starting weapon

//pics
String tankURL = "https://raw.githubusercontent.com/AntonVoronov/tankgame/master/tank.png";
String tankbulletURL = "https://raw.githubusercontent.com/AntonVoronov/tankgame/master/smbullet.png";
String enemytankURL = "https://raw.githubusercontent.com/AntonVoronov/tankgame/master/enemytank1.png";
String healthURL = "https://raw.githubusercontent.com/AntonVoronov/tankgame/master/medkitplus.png";
String explosionURL = "https://raw.githubusercontent.com/AntonVoronov/tankgame/master/dedtank.png";
String minigunURL = "https://raw.githubusercontent.com/AntonVoronov/tankgame/master/minigun.png";
String coilgunURL = "https://raw.githubusercontent.com/AntonVoronov/tankgame/master/coilgun.png";

//sound files
String gunshotURL = "https://raw.githubusercontent.com/AntonVoronov/tankgame/master/gunshot.mp3";
String coilshotURL ="https://raw.githubusercontent.com/AntonVoronov/tankgame/master/coilgun.mp3";

//creating arrays for each entity
ArrayList < Sprite > smBullets = new ArrayList < Sprite > ();
ArrayList < Enemy > enemies = new ArrayList < Enemy > ();
ArrayList < Medkit > health = new ArrayList < Medkit > ();
ArrayList < Sprite > minigunDrop = new ArrayList < Sprite > ();
ArrayList < Sprite > coilgunDrop = new ArrayList < Sprite > ();
ArrayList < Explosion > explosion = new ArrayList < Explosion > ();
ArrayList < Sprite > enemyBullets = new ArrayList < >();

//Gameplay functions
void drawTitle() {
    background(255, 255, 255);
    fill(0);
    textSize(100);
    textAlign(CENTER);
    text("Welcome to Tankomino!!!", 1024, 100);
    textSize(50);
    text("Click anywhere on the window to start.", 1024, 200);
    text("Controls/How to play", 1024, 300);
    text("WASD to move, mouse pointer moves tank turret around", 1024, 400);
    text("Pick up new weapons and health on the ground by touching it", 1024, 500);
    isPlayerDead = false;
//hi
    if (mousePressed) {
        scsw.goToScene("Game");
    }

} //draws the title screen

void testDeath(){
    if(playerHP <= 0){
        isPlayerDead = true;
        scsw.goToScene("Death");
    }
} //tests if the player is dead yet.

void drawDeath() {
    background(255, 255, 255);
    fill(0);
    textSize(75);
    textAlign(CENTER);
    text("You Died!", 1024, 100);
    text("Kills= "+ playerScore, 1024,200);
    text("Restart Program to try again.", 1024, 300);

} //draws the death screen

void setup() {
    size(2048, 2048); //sets size of canvas
    
    //names entities and their position
    player = new Sprite(tankURL, 100, 400, 100, 100);
    smbulletbase = new Sprite(tankbulletURL, 100, 400, 100, 100);
    enemyBase = new Sprite(enemytankURL, 100, 400, 100, 100);
    medkit = new Sprite(healthURL, 100, 400, 100, 100);
    kaboomBase = new Sprite(explosionURL, 100, 400, 100, 100);
    minigun = new Sprite(minigunURL, 100, 400, 100, 100);
    coilgun = new Sprite(coilgunURL, 100, 400, 100, 100);
    
    
    //sets size of some pictures
    smbulletbase.setSize(50, 50);
    minigun.setSize(150,150);
    coilgun.setSize(150,150);
    
    //initialization of scene switcher library
    scsw = new SceneSwitcher();
    scsw.addScene("Title", new Scene(() -> drawTitle()));
    scsw.addScene("Game", new Scene(() -> drawGame()));
    scsw.addScene("Death", new Scene(() -> drawDeath()));
    
    
    //sets players front angle
    player.frontAngle(90);
    
    //sets up audio library
    ktAudio = new KTAudioController(this);
    clickEffect = new KTSound(this, gunshotURL);
    ktAudio.add(clickEffect);
    

} //sets up the game

void draw() {
        scsw.draw();
    
} //runs the game

void drawGame() {
    
    if (!pause) {
        background(10, 107, 5);
    
        smallgunHandler();
        testDeath();
        enemy1Handler();
        // increaseSpawnRate();
        collisionHandler();
        deathHandler(eToBeRemoved);
        dropHandler();
        garbageCollector();
        playerHandler();
        fill(0);
        textSize(80);
        textAlign(LEFT);
        text("Health= " + (int)playerHP, 100, 100);
        text("Kills= "+playerScore, 100, 200);
        text("Current Weapon= " + weapon, 100, 300);

        Explosion remove = null;

        for (Explosion exp: explosion) {
            if (exp.kaboomCounter <= 0) {
                remove = exp;
            }
        }

        explosion.remove(remove);
    }

} //draws the game onscreen

void playerHandler() {
    player.turnToPoint(mouseX, mouseY);
    if (firing && sTimer < 1) {

        Sprite smb = new Sprite(smbulletbase);
        smb.moveToSprite(player);
        if(usingCoil){
            clickEffect = new KTSound(this, coilshotURL);
            ktAudio.add(clickEffect);
            clickEffect.play();
            clickEffect = new KTSound(this, gunshotURL);
            ktAudio.add(clickEffect);
        }
        smBullets.add(smb);
        clickEffect.play();
        if(weapon == "minigun"){
            smb.turnToPoint(mouseX+(int)random(-90,90), mouseY+(int)random(-90,90));
        }
        else{
            smb.turnToPoint(mouseX, mouseY);
        }

        sTimer = firingSpeed;
    }
    sTimer -= 1;
    
    prevX = player.getX();
    prevY = player.getY();

    if (up) {
        player.moveY( - 5);
        // pAngle -= 1;
    }
    if (down) {
        player.moveY(5);
        // pAngle += 1;
    }
    if (left) {
        player.moveX( - 5);
        // pAngle -= 1;
    }
    if (right) {
        player.moveX(5);
    }
    if (player.getY() < player.getH() / 2) {
        player.setY(player.getH() / 2);
    }
    if (player.getY() > 2048 - player.getH() / 2) {
        player.setY(2048 - player.getH() / 2);
    }
    if (player.getX() < player.getH() / 2) {
        player.setX(player.getH() / 2);
    }
    if (player.getX() > 2048 - player.getH() / 2) {
        player.setX(2048 - player.getH() / 2);
    }
    
    

    // player.turnToDir(pAngle);
    player.display();
    // player.displayHitbox();
} //handles everything the player does, such as move and shoot

void deathHandler(Enemy e) {

    if (e != null) {
        Explosion rip = new Explosion();
        rip.kaboom.moveToPoint(e.eTank.getX(), e.eTank.getY());
        // println("made enemy");
        rip.kaboom.frontAngle(90);
        rip.kaboom.turn(90);
        explosion.add(rip);
    }
    for (Explosion exp: explosion) {
        if (exp.kaboomCounter > 1) {
            exp.kaboom.display();
        }
        // if (exp.kaboomCounter < 1) {
        //     explosion.remove(exp);
        // }
        // else {
        exp.kaboomCounter -= 3;
        // println(exp.kaboomCounter);
        // println(explosion.size());
        // }
    }

} //handles deaths of the enemy tanks

void dropHandler() {
    if (tickSpeed < 0) {
        if((int)random(1,6) == 1){ //every 6 medkits the handler drops a minigun
            Sprite mg = new Sprite(minigun);
            mg.moveToPoint(random(0,2048), random(0, 2048)); //moves drop to random point
            minigunDrop.add(mg);
            tickSpeed = (int)random(100,500);
        }else if((int)random(1,4) == 1){ //every 4 medkits the handler drops a coilgun
            Sprite cg = new Sprite(coilgun);
            cg.moveToPoint(random(0,2048), random(0,2048)); //moves drop to random point
            coilgunDrop.add(cg);
            tickSpeed = (int)random(100,500); //changes 
        }
        else {
            
        
        Medkit hp = new Medkit();
        hp.med.moveToPoint(random(0, 2048), random(0, 2048));

        health.add(hp);

        tickSpeed = (int)random(100,500);
        }
    }
    tickSpeed--;
    for (Medkit hp: health) {
        hp.med.display();
        hp.timer--;

    }
    for (Sprite mg: minigunDrop){
        mg.display();
    }
    for (Sprite cg: coilgunDrop){
        cg.display();
    }
    //     e.forward(5);
} //handles item drops***

void enemy1Handler() {
    if (spawnSpeed < 0) {
        Enemy e = new Enemy();
        // e.eTank.moveToPoint(width + 100, random(e.eTank.getH() / 2, height - e.eTank.getH() / 2));
        float randomX;
        float randomY;
        do {
            randomX= random(0,2048);
            randomY= random(0,2048);
        } while (dist(player.getX(), player.getY(), randomX, randomY) < 200);
        
        e.eTank.moveToPoint(randomX, randomY);
        e.eTank.frontAngle(90);
        e.eTank.turn(90);
        enemies.add(e);
        spawnSpeed = (int)spawnDelay;
        spawnDelay = max(spawnDelay*0.99,1);
        println(spawnDelay);
    }
    spawnSpeed--;
    for (Enemy e1: enemies) {
        e1.eTank.display();
        // println("showed enemy");
        e1.prevX = e1.eTank.getX();
        e1.prevY = e1.eTank.getY();
        e1.eTank.forward(3);
        e1.eTank.turnToSprite(player);

        for (Sprite bullet: e1.bullets) {
            bullet.display();
            bullet.forward(25);
        }
    }

    enemy1Shoot();

} //handles enemy movements***

void enemy1Shoot() {

    for (Enemy e1: enemies) {
        if (e1.shootTimer < 1) {
            Sprite smb = new Sprite(smbulletbase);
            smb.moveToSprite(e1.eTank);
            e1.bullets.add(smb);
            clickEffect.play();
            smb.turnToPoint(player.getX(), player.getY());

            e1.shootTimer = 500;

        }
        e1.shootTimer--;

    }

} //handles enemy shooting

void collisionHandler() {

    Enemy tank = null;
    Sprite bToBeRemoved = null;
    Medkit hToBeRemoved = null;
    Sprite mgToBeRemoved = null;
    Sprite cgToBeRemoved = null;
    eToBeRemoved = null;
    for (Enemy e1: enemies) {
        for (Sprite eb: e1.bullets) {
            if (player.touchingSprite(eb)) {

                playerHP -= 50;
                bToBeRemoved = eb;
                tank = e1;

            }
        }
        
        if (player.touchingSprite(e1.eTank)) {
            player.setX(prevX);
            player.setY(prevY);
            
            e1.eTank.setX(e1.prevX);
            e1.eTank.setY(e1.prevY);
        }
        
        for (Enemy e2: enemies) {
            if (e1.eTank.touchingSprite(e2.eTank) && e1 != e2) {
                e1.eTank.setX(e1.prevX);
                e1.eTank.setY(e1.prevY);
                e2.eTank.setX(e2.prevX);
                e2.eTank.setY(e2.prevY);
    
            }
        }
        for(Explosion exp : explosion) {
            if (exp.kaboom.touchingSprite(e1.eTank)) {

                e1.eTank.setX(e1.prevX);
                e1.eTank.setY(e1.prevY);
    
            }
        }
    }
    
    for(Explosion exp : explosion) {
        if (exp.kaboom.touchingSprite(player)) {
            playerHP-=0.2;
            player.setX(prevX);
            player.setY(prevY);
    
        }
    }
    
    if (bToBeRemoved != null) {
        tank.bullets.remove(bToBeRemoved);
        bToBeRemoved = null;
        
    }
    for (Medkit hp: health) {

        if (hp.med.touchingSprite(player)) {
            hToBeRemoved = hp;
            playerHP += 200;
        }
    }
    for (Sprite mg: minigunDrop) {
        if(mg.touchingSprite(player)){
            weapon = "minigun";
            usingCoil = false;
            firingSpeed = 5;
            mgToBeRemoved = mg;
        }
    }
    for (Sprite cg: coilgunDrop) {
        if(cg.touchingSprite(player)){
            weapon = "coilgun";
            println("picked coil");
            firingSpeed = 30;
            cgToBeRemoved = cg;
            usingCoil = true;
        }
    }
    for (Enemy e1: enemies) {

        for (Sprite smb: smBullets) {
            if (e1.eTank.touchingSprite(smb)) {
                if(usingCoil){
                   smb.forward(120);
                  }else{
                      bToBeRemoved = smb;

                  }

                eToBeRemoved = e1;
                
                playerScore += 1;

            }

        }

        // if (smb.touchingSprite(player)) {
        //     playerHP -= 50;
        //     eToBeRemoved = e1;
        // }
    }
    if (eToBeRemoved != null) {
        enemies.remove(eToBeRemoved);

    }
    if (bToBeRemoved != null) {
        smBullets.remove(bToBeRemoved);

    }
    if (hToBeRemoved != null) {
        health.remove(hToBeRemoved);

    }
    if (mgToBeRemoved != null){
        minigunDrop.remove(mgToBeRemoved);
    }
    if (cgToBeRemoved != null){
        coilgunDrop.remove(cgToBeRemoved);
    }

    //kaboom.remove(rip);
} //handles collisions of entities

void smallgunHandler() {
    for (Sprite smb: smBullets) {
        
        smb.display();
        if(usingCoil){
            smb.forward(120);
        }else{
        smb.forward(40);
        }

    }
} //handles shooting of players gun

void keyPressed() {
    if (key == 'w') {
        up = true;
    }
    if (key == 's') {
        down = true;
    }
    if (key == 'a') {
        left = true;

    }
    if (key == 'd') {
        right = true;

    }
    
    if (key == 27) {
        pause = !pause;
    }

    // player.turnToDir(0);
} //tests when person is pressing keys

void garbageCollector() {
    Enemy ETR = null;
    Sprite BTR = null;
    Medkit MTR = null;
    for (Enemy e: enemies) {
        if (e.eTank.getX() < -100) {
            ETR = e;
        }
    }
    for (Sprite c: smBullets) {
        if (c.getX() > width + 100) {
            BTR = c;
        }
        if (c.getY() > height + 100) {
            BTR = c;
        }
        if (c.getX() < -100) {
            BTR = c;
        }
        if (c.getY() < -100) {
            BTR = c;
        }
    }
    
    for (Medkit med : health) {
        if (med.timer <= 0) {
            MTR = med;
        }
    }
    //gets rid of bullets that went off-screen
    if (BTR != null) {
        smBullets.remove(BTR);
        println("Deleted bullet");
    }
    if (ETR != null) {
        enemies.remove(ETR);
    }
    //Medkits to despawn
    if (MTR != null) {
        health.remove(MTR);
    }
} 

void keyReleased() {
    if (key == 'w') {
        up = false;
    }
    if (key == 's') {
        down = false;
    }
    if (key == 'a') {
        left = false;

    }
    if (key == 'd') {
        right = false;

    }
    // player.turnToDir(0);
} //tests if person wants to stop moving tank

void mousePressed() {
    firing = true;

} //tests if person wants to shoot

void mouseReleased() {
    firing = false;
} //tests if person wants to stop shooting

class Explosion {
    Sprite kaboom = new Sprite(kaboomBase);
    int kaboomCounter = 600;
    //   int bTimer = 30;
} //n-a

class Enemy {
    Sprite eTank = new Sprite(enemyBase);
    int shootTimer = 300;
    ArrayList < Sprite > bullets = new ArrayList < >();
    float prevX = 0;
    float prevY = 0;
} //n-a

class Medkit {
    Sprite med = new Sprite(medkit);
    
    int timer = 1000;
}







//thanks to jackson for making this library!
class SceneSwitcher {
    private ArrayList < Scene > scenes;
    private HashMap < String,
    Integer > nameToScene;
    private int activeScene = 0;

    SceneSwitcher() {
        scenes = new ArrayList < >();
        nameToScene = new HashMap < >();
    }

    void addScene(String name, Scene scene) {
        scenes.add(scene);
        nameToScene.put(name, scenes.size() - 1);
    }

    void draw() {
        Scene s = scenes.get(activeScene);
        if (s == null) {
            println("[SceneSwitcher] No Scene");
        } else {
            s.draw();
        }
    }

    void goToScene(int to) {
        activeScene = to;
    }

    void goToScene(String sceneName) {
        Integer sceneNumber = nameToScene.get(sceneName);
        if (sceneNumber == null) {
            println("[SceneSwitcher] No scene with name '" + sceneName + "', switching to first scene...");
            sceneNumber = 0;
        }
        goToScene(sceneNumber);
    }
}

class Scene {
    SceneDrawer drawer;

    Scene(SceneDrawer drawer) {
        this.drawer = drawer;
    }

    void draw() {
        drawer.draw();
    }
}

interface SceneDrawer {
    void draw();
}


//hi









//Sprite library
class Sprite {
  // do not modify these except through the provided methods
  PImage _img;
  float _w;
  float _h;
  float _x;
  float _y;
  PVector _rotVector; // for movement
  float _front = 0;   // angle of front relative to right of image
 
  PVector _hitboxCenter = new PVector();
  PVector[] _hitbox;
 
  boolean _flipped = false;
 
  // constructor to create a Sprite at (x, y) with size (w, h)
  // using the image provided by the url
  Sprite(String url, float x, float y, float w, float h) {
    _img = loadImage(url);
    _x = x;
    _y = y;
    _w = w;
    _h = h;
    _rotVector = new PVector(1, 0, 0);
    resetRectHitbox();
  }
 
  // constructor to create a Sprite at (x, y) with size (w, h)
  // with a solid black color. The color of this Sprite can
  // change using the setColor() function
  Sprite(float x, float y, float w, float h) {
    _img = createImage(1, 1, RGB);
    _x = x;
    _y = y;
    _w = w;
    _h = h;
    _rotVector = new PVector(1, 0, 0);
    resetRectHitbox();
  }
 
  // constructor to create a copy of Sprite s
  Sprite(Sprite s) {
    _img = s._img;
    _x = s._x;
    _y = s._y;
    _w = s._w;
    _h = s._h;
    _rotVector = new PVector(s._rotVector.x, s._rotVector.y, 0);
    _front = s._front;
    _hitboxCenter = new PVector(s._hitboxCenter.x, s._hitboxCenter.y);
    _hitbox = new PVector[s._hitbox.length];
    for (int i = 0; i < _hitbox.length; i++) {
        _hitbox[i] = new PVector(s._hitbox[i].x, s._hitbox[i].y);
    }
    _flipped = s._flipped;
  }
 
  // adjust the direction of the PImage of the Sprite
  // without changing the orientation of the Sprite
  void frontAngle(float degrees) {
    float newFront = radians(degrees);
 
    // movement done from this direction from now on
    _rotVector.rotate(newFront - _front);
 
    _front = newFront;
  }
 
  // set rectangular hitbox of given size
  // h is along the front-back axis
  // w is along the perpendicular axis
  void setRectHitbox(float w, float h) {
    _hitbox = new PVector[]{
      new PVector(-w/2, h/2),
      new PVector(-w/2, -h/2),
      new PVector(w/2, -h/2),
      new PVector(w/2, h/2)
    };
  }
 
  // set rectangular hitbox of size based on image
  void resetRectHitbox() {
    setRectHitbox(_w, _h);
  }
 
  // set circular hitbox of given size
  void setRoundHitbox(float r) {
    _hitbox = new PVector[]{new PVector(r, r*2)};
  }
 
  // set circular hitbox based on image size
  void resetRoundHitbox() {
    setRoundHitbox((_w+_h)/4);
  }
 
  // recenter hitbox relative to center of image
  void setHitboxCenter(float x, float y) {
    _hitboxCenter = new PVector(x, y);
  }
 
  // recenter hitbox to exactly center of image
  void resetHitboxCenter() {
    _hitboxCenter = new PVector(0, 0);
  }
 
  void setHitboxPoints(PVector[] array) {
    if (array.length > 0) {
      boolean valid = true;
      for (PVector pv : array) if (pv == null) valid = false;
 
      if (valid) _hitbox = array;
      else println("invalid hitbox: " + java.util.Arrays.toString(array));
    }
    else {
      println("hitbox must have 3+ points: " + java.util.Arrays.toString(array));
    }
  }
 
  // change the color of a Sprite created without an image
  void setColor(float r, float g, float b) {
    color c = color(r, g, b);
    for(int x = 0; x < _img.width; x++) {
      for(int y = 0; y < _img.height; y++) {
        _img.set(x, y, c);
      }
    }
  }
 
  // flips Sprite image across its X axis
  void flip() {
      _flipped = !_flipped;
  }
 
  // turn the specified number of degrees
  void turn(float degrees) {
    _rotVector.rotate(radians(degrees));
  }
 
  // turn to the specified (x, y) location
  void turnToPoint(float x, float y) {
    _rotVector.set(x - _x, y - _y, 0);
    _rotVector.setMag(1);
  }
 
  // turn to the specified angle
  void turnToDir(float angle) {  
    float radian = radians(angle);
    _rotVector.set(cos(radian), sin(radian));
    _rotVector.setMag(1);
  }
 
  // turn to the specified Sprite s
  void turnToSprite(Sprite s) {
    turnToPoint(s._x, s._y);
  }
 
  // move sprite to location (x, y)
  void moveToPoint(float x, float y) {
    _x = x;
    _y = y;
  }
 
  // move sprite to location of Sprite s
  void moveToSprite(Sprite s) {
    _x = s._x;
    _y = s._y;
  }
 
  // move in the X direction by the specified amount 
  void moveX(float x) {
    _x += x;
  }
 
  // move in the Y direction by the specified amount 
  void moveY(float y) {
    _y += y;
  }
 
  void moveXY(float dx, float dy) {
      _x += dx;
      _y += dy;
  }
 
  // move forward in the direction the sprite is facing
  // by the specified number of steps (pixels)
  void forward(float steps) {
    _x += _rotVector.x * steps;
    _y += _rotVector.y * steps;
  }
 
  // move 90 degree clockwise from the direction
  // the sprite is facing by the specified number of steps (pixels)
  void sideStep(float steps) {
    _rotVector.rotate(PI / 2);
    _x += _rotVector.x * steps;
    _y += _rotVector.y * steps;
    _rotVector.rotate(-PI / 2);
  }
 
  // draw the Sprite. This function
  // should be called in the void draw() function
  void display() {
    pushMatrix();
    pushStyle();
 
    translate(_x, _y);
    rotate(_rotVector.heading() - _front);
    if (_flipped) scale(-1, 1);
    imageMode(CENTER);
    image(_img, 0, 0, _w, _h);
 
    popStyle();
    popMatrix();
  }
 
  void displayHitbox() {
    PVector cen = _getCenter();
 
    pushStyle();
    stroke(255, 0, 0);
    strokeWeight(5);
    noFill();
 
    if (_hitbox.length == 1) {
      ellipseMode(CENTER);
      ellipse(cen.x, cen.y, _hitbox[0].y, _hitbox[0].y);
    }
    else {
      PVector[] corners = _getPoints();
      for (int i = 0; i < corners.length; i++) {
        PVector a = corners[i];
        PVector b = corners[(i+1)%corners.length];
        line(a.x, a.y, b.x, b.y);
      }
    }
 
    line(cen.x, cen.y, cen.x + _rotVector.x * 20, cen.y + _rotVector.y * 20);
 
    fill(255,0,0);
    noStroke();
    ellipse(cen.x, cen.y, 15, 15);
 
    popStyle();
  }
 
  // set the size of the Sprite
  void setSize(float w, float h) {
    _w = w;
    _h = h;
  }
 
  void setCoor(float x, float y) {
    _x = x;
    _y = y;
  }
 
  // set the x coordinate
  void setX(float x) {
    _x = x;
  }
 
  // set the y coordinate
  void setY(float y) {
    _y = y;
  }
 
  // change the image of the Sprite
  void setImage(PImage img) {
    _img = img;
  }
 
  // get the x coordinate of the sprite 
  float getX() {
    return _x;
  }
 
  // get the y coordinate of the sprite
  float getY() {
    return _y;
  }
 
  // get the width of the sprite
  float getW() {
    return _w;
  }
 
  // get the height of the sprite
  float getH() {
    return _h;
  }
 
  // get the image of the sprite
  PImage getImage() {
    return _img;
  }
 
  // get the direction (in degrees) the Sprite is facing
  float getDir() {
    return degrees(_rotVector.heading());
  }
 
  // calculate the distance from this Sprite to Sprite s
  float distTo(Sprite s) {
    return dist(_x, _y, s._x, s._y);
  }
 
  float distToPoint(float x, float y) {
    return dist(_x, _y, x, y);
  }
 
  // checks whether this Sprite is touching Sprite s
  boolean touchingSprite(Sprite s) {
    if (s._hitbox.length == 1) {
      if (_hitbox.length == 1) {
        return PVector.dist(this._getCenter(), s._getCenter()) <= 
               this._hitbox[0].x + s._hitbox[0].x;
      }
      return _circPoly(s._getCenter(), s._hitbox[0].x, this._getPoints());
    }
    if (_hitbox.length == 1) {
      return _circPoly(this._getCenter(), this._hitbox[0].x, s._getPoints());
    }
 
    PVector[] s1Points = s._getPoints();
    PVector[] s2Points = this._getPoints();
 
    for(int i = 0; i < s1Points.length; i++) {
      PVector a = s1Points[i], b = s1Points[(i+1)%s1Points.length];
      for(int j = 0; j < s2Points.length; j++) {
        PVector c = s2Points[j], d = s2Points[(j+1)%s2Points.length];
 
        // sprites touch if ab crosses cd
        if(_clockwise(a, c, d) != _clockwise(b, c, d) &&  // a & b on different sides of cd, and
           _clockwise(a, b, c) != _clockwise(a, b, d)) {  // c & d on different sides of ab
          return true;
        }
      }
    }
 
    return _insidePts(s1Points,s2Points) || _insidePts(s2Points,s1Points);
  }
 
  //checks to see if this Sprite is fully inside another sprite
  boolean insideSprite(Sprite s){
    if (s._hitbox.length == 1) {
      if (_hitbox.length == 1) {
        return PVector.dist(s._getCenter(),this._getCenter()) <
               s._hitbox[0].x - this._hitbox[0].x;
      }
      return _insideCirc(_getPoints(), s._getCenter(), s._hitbox[0].x);
    }
    if (s._hitbox.length == 1) {
      // TODO: check if center is in middle but NOT touching any side
      //   (will want to adapt existing _circPoly to separate side-touching
      //    code into individual method)
      return false;
    }
    return _insidePts(this._getPoints(), s._getPoints());
  }
 
  // checks whether this Sprite is touching the specified point
  boolean touchingPoint(float x, float y) {
    if (_hitbox.length == 1) return dist(x,y,_hitboxCenter.x, _hitboxCenter.y) < _hitbox[0].x;
    return _ptPoly(new PVector(x,y), _getPoints());
  }
 
  // checks whether this Sprite's hitbox is at least partially inside the canvas
  // TODO: technically this returns true even if circular Sprite is just outside
  //   at the corners, and false if a tilted rectangular Sprite's edge crosses
  //   a corner with endpoints outside
  boolean isInsideScreen() {
    if (_hitbox.length == 1) {
      float r = _hitbox[0].x;
      PVector c = _getCenter();
      return 0 <= c.x + r && c.x - r < width && 0 <= c.y + r && c.y - r < height;
    }
 
    PVector[] points = this._getPoints();
    for(PVector p : points) {
      if(0 <= p.x && p.x < width && 0 <= p.y && p.y < height) {
        return true;
      }
    }
    return false;
  }
 
  PVector out = new PVector(-10000, -10000); // any outside point
 
  // (pseudo-static) checks whether pt touches polygon
  boolean _ptPoly(PVector pt, PVector[] poly) {  
    // count edges crossed by the line connecting the target point to "the outside"
    int count = 0;
 
    for(int i = 0; i < poly.length; i++) {
      PVector a = poly[i], b = poly[(i+1)%poly.length];  // edge points
      if(_clockwise(a, pt, out) != _clockwise(b, pt, out) &&  // a & b on different sides of line
         _clockwise(a, b, pt)   != _clockwise(a, b, out)) {   // tgt & out on diff sides of edge
        count++;
      }
    }
 
    return count % 2 == 1;
    // a convex poly would be crossed on one edge;
    //   concave could be crossed on any odd # of edges
  }
 
  // (pseudo-static) checks whether circle is touching polygon
  //   (including one inside the other)
  boolean _circPoly(PVector center, float r, PVector[] poly) {
    // center is in polygon
    if (_ptPoly(center, poly)) return true;
    if (_insideCirc(poly, center, r)) return true;
 
    // circle encloses any corner
    for (PVector corner : poly) {
      if (dist(center.x, center.y, corner.x, corner.y) < r) return true;
    }
 
    // circle is adjacent and close enough to any side
    for (int i = 0; i < poly.length; i++) {
      if (_circSeg(center, r, poly[i], poly[(i+1)%poly.length])) return true;
    }
 
    return false;
  }
 
  // (pseudo-static) 
  // checks if circle touches segment AB from a perpendicular direction,
  //   but NOT from "beyond the ends"
  //   (this should be checked separately if desired)
  // aka, checks if center forms a perpendicular to any point on segment
  //   with length <= r 
  boolean _circSeg(PVector center, float r, PVector a, PVector b) {
    PVector ab = PVector.sub(b,a);
    PVector abPerp = (new PVector(-ab.y, ab.x)).normalize().mult(r);
 
    PVector[] limits = new PVector[]{
      PVector.add(a,abPerp), // move perpendicular to the segment by
      PVector.sub(a,abPerp), // distance r from each of the endpoints,
      PVector.sub(b,abPerp), // forming a bounding rectangle
      PVector.add(b,abPerp)
    };
 
    return _ptPoly(center, limits);
  }
 
  // (pseudo-static) checks whether all inPts are completely within the outPts
  //   TODO: does not check whether edges between inPts are within outPts!
  boolean _insidePts(PVector[] inPts, PVector[] outPts) {
 
    for(int i = 0; i < inPts.length; i++){
      // direction of angular relationship to any side must match
      //   direction of relationship to opposite side
      if(!_ptPoly(inPts[i], outPts)) return false;
    }
    return true;
  }
 
  // (pseudo-static) checks whether all inPts are completely within circle
  boolean _insideCirc(PVector[] inPts, PVector center, float r) {
 
    for(int i = 0; i < inPts.length; i++){
      // direction of angular relationship to any side must match
      //   direction of relationship to opposite side
      if(PVector.dist(inPts[i],center) > r) return false;
    }
    return true;
  }
 
  // get hitbox absolute center based on image center, relative offset, rotation, and front
  PVector _getCenter() {
    PVector cen = new PVector(_hitboxCenter.x, _hitboxCenter.y);
    cen.rotate(_rotVector.heading() - _front);
    cen.x += _x;
    cen.y += _y;
    return cen;
  }
 
  // get points representing rectangular hitbox
  PVector[] _getPoints() {
    PVector cen = _getCenter();
 
    PVector[] points = new PVector[_hitbox.length];
    float angle = _rotVector.heading();
    for(int i = 0; i < _hitbox.length; i++) {
      points[i] = new PVector(_hitbox[i].x, _hitbox[i].y);
      points[i].rotate(angle);
      points[i].x += cen.x;
      points[i].y += cen.y;
    }
    return points;
  }
 
  // checks whether motion in AB turns clockwise to follow BC
  // i.e. which way is angle ABC concave?
  private boolean _clockwise(PVector A, PVector B, PVector C) {
    return (C.y-A.y) * (B.x-A.x) > (B.y-A.y) * (C.x-A.x);
  }
}


