package elfak.mosis.zeljko.citzens_app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class AddFriend extends AppCompatActivity {

    Button btnAllUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);

        btnAllUsers = findViewById(R.id.btn_allUsers);
        btnAllUsers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddFriend.this, AllUsers.class);
                startActivity(intent);
            }
        });

    }
}