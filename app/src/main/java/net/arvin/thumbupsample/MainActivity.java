package net.arvin.thumbupsample;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import net.arvin.thumbupsample.changed.CountView;
import net.arvin.thumbupsample.changed.ThumbUpView;
import net.arvin.thumbupsample.changed.ThumbView;
import net.arvin.thumbupsample.imitate.ImitateThumbUpView;
import net.arvin.thumbupsample.mine.MineActivity;

public class MainActivity extends AppCompatActivity {
    EditText edNum;
    OldThumbUpView oldThumbUpView;
    ThumbUpView newThumbUpView;
    ImitateThumbUpView imitateThumbUpView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        edNum = findViewById(R.id.ed_num);
        oldThumbUpView = findViewById(R.id.oldThumbUpView);
        newThumbUpView = findViewById(R.id.newThumbUpView);
        imitateThumbUpView = findViewById(R.id.imitateThumbUpView);

        oldThumbUpView.setThumbUpClickListener(new OldThumbUpView.ThumbUpClickListener() {
            @Override
            public void thumbUpFinish() {
                Log.d("MainActivity","Old点赞成功");
            }

            @Override
            public void thumbDownFinish() {
                Log.d("MainActivity","Old取消点赞成功");
            }
        });

        newThumbUpView.setThumbUpClickListener(new ThumbView.ThumbUpClickListener() {
            @Override
            public void thumbUpFinish() {
                Log.d("MainActivity","New点赞成功");
            }

            @Override
            public void thumbDownFinish() {
                Log.d("MainActivity","New取消点赞成功");
            }
        });
        //根据回调Toast的显示可以看出，之前的版本虽然结果正确但是会对回调有可能重复调用多次。

        findViewById(R.id.btn_judge).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        findViewById(R.id.btn_manual).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imitateThumbUpView.setThumbUp(! imitateThumbUpView.isThumbUp);
            }
        });
    }

    public void setNum(View v) {
        try {
            int num = Integer.valueOf(edNum.getText().toString().trim());
//            oldThumbUpView.setCount(num);
//            newThumbUpView.setCount(num).setThumbUp(false);
            imitateThumbUpView.setCount(num);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "只能输入整数", Toast.LENGTH_LONG).show();
        }
    }

    public void skip(View v) {
        Log.d("MainActivity", v.getClass() + " 跳");
        Intent intent = new Intent(MainActivity.this, MineActivity.class);
        MainActivity.this.startActivity(intent);
    }

    public void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}
