package elfak.mosis.zeljko.citzens_app;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Point;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Display;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.ar.sceneform.Camera;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.Scene;
import com.google.ar.sceneform.collision.Ray;
import com.google.ar.sceneform.math.Quaternion;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.MaterialFactory;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.ShapeFactory;
import com.google.ar.sceneform.rendering.Texture;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Random;

public class AR_get_more_coins extends AppCompatActivity {

    private Scene scene;
    private Camera camera;
    private ModelRenderable bulletRenderable;
    private boolean timerStarted = false;
    private int balloonsLeft = 10;
    private Point point;
    private TextView balloonsLeftText;
    private SoundPool soundPool;
    private int sound;
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Display display = getWindowManager().getDefaultDisplay();
        point = new Point();
        display.getRealSize(point);

        setContentView(R.layout.activity_a_r_get_more_coins);
        CustomArFragment fragment = (CustomArFragment) getSupportFragmentManager().findFragmentById(R.id.fragment);
        scene = fragment.getArSceneView().getScene();
        camera = scene.getCamera();

        setBallonsToTheScene();
        builtBullet();

        balloonsLeftText = findViewById(R.id.balloonsCount);
        loadSoundPool();

        Button kill = findViewById(R.id.kill);
        kill.setOnClickListener(view -> {
            if (! timerStarted) {
                startTimer();
                timerStarted = true;
            }

            shoot();
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void loadSoundPool() {
        AudioAttributes attributes = new AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_GAME)
                .build();
        soundPool = new SoundPool.Builder().setMaxStreams(1).setAudioAttributes(attributes).build();
        sound = soundPool.load(this, R.raw.blop_sound, 1);
    }

    private void shoot() {
        Ray ray = camera.screenPointToRay(point.x / 2f, point.y / 2f);

        Node node = new Node();
        node.setRenderable(bulletRenderable);
        scene.addChild(node);

        new Thread(() -> {
            for (int i = 0; i < 100; i++){

                int finalI = i;
                runOnUiThread(() -> {
                    Vector3 v = ray.getPoint(finalI * 0.2f);
                    node.setWorldPosition(v);

                    Node nodeContact = scene.overlapTest(node);

                    if (nodeContact != null){
                        balloonsLeft--;

                        balloonsLeftText.setText("Left : " + balloonsLeft);
                        scene.removeChild(nodeContact);
                        scene.removeChild(node);

                        soundPool.play(sound, 1, 1, 1, 0, 1);
                    }


                });

                try {
                    Thread.sleep(20);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            runOnUiThread(() -> {
                scene.removeChild(node);
            });

        }).start();


        AddCoins();
    }

    @SuppressLint("SetTextI18n")
    private void startTimer() {
        TextView timer = findViewById(R.id.timer);

        new Thread(() -> {

            int seconds = 0;

            while (balloonsLeft > 0) {

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                seconds++;
                int mins = seconds / 60;
                int secs = seconds % 60;

                runOnUiThread(() -> {


                    timer.setText(mins + " : " + secs);
                });
            }

        }).start();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void builtBullet() {
        Texture.builder()
                .setSource(this, R.drawable.texture)
                .build()
                .thenAccept(texture -> {
                    MaterialFactory.makeOpaqueWithTexture(this, texture).thenAccept( material -> {
                        bulletRenderable = ShapeFactory.makeSphere( 0.02f, new Vector3(0f, 0f, 0f), material);
                    });
                });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void setBallonsToTheScene() {
        ModelRenderable.builder()
                .setSource(this, Uri.parse("model.sfb"))
                .build()
                .thenAccept(modelRenderable -> {

                    for (int i = 0; i < 10; i++){

                        Node node = new Node();
                        node.setRenderable(modelRenderable);

                        Random random = new Random();
                        float x = random.nextInt(8) - 4f;
                        float y = random.nextInt(2);
                        float z = random.nextInt(4);

                        Vector3 position = new Vector3( x, y, -z - 5f);
                        Vector3 worldPosition = scene.getCamera().getWorldPosition();

                        node.setWorldPosition(position);
                        node.setLocalRotation(Quaternion.axisAngle(new Vector3(0, 1f, 0), 230));
                        scene.addChild(node);
                    }
                });
    }



    private void AddCoins()
    {

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference uidRef = rootRef.child("Users").child(uid);
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(balloonsLeft == 1) {
                    int coins = dataSnapshot.child("coins").getValue(int.class);
                    coins += 10;
                    rootRef.child("Users").child(uid).child("coins").setValue(coins);
                    Toast.makeText(getApplicationContext(),"Congratulations! You scored 10 coins!", Toast.LENGTH_SHORT);

                    Intent i = new Intent(AR_get_more_coins.this, Coins.class);
                    startActivity(i);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        uidRef.addListenerForSingleValueEvent(valueEventListener);

    }
}

