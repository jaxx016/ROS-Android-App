package com.example.ismail.app3;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.SeekBar;
//socket.io on android is made by gottox
//source : https://github.com/Gottox/socket.io-java-client
import io.socket.IOAcknowledge;
import io.socket.IOCallback;
import io.socket.SocketIO;
import java.net.MalformedURLException;
import org.xwalk.core.XWalkView;

public class MainActivity extends AppCompatActivity {
    private static SeekBar seek,seekv;
    private XWalkView xWalkWebView;
   // private static String server_ad ;
    private boolean done=true ;
    protected static SocketIO socket ;

  /*  {
        try {
            socket = new SocketIO("http://192.168.0.201:8080"); // connect to the server
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }*/


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main );

        connection("http://197.168.1.3:8080");
        new Thread(new Runnable() {
            public void run(){
                seek_bar() ;
            }
        }).start();
// crosswalk is an enhanced webview (more fluid than the native one) ,
// so you can easily open webcontent inside a native android application
       String imageUrl ="http://192.168.1.3:8181/stream?topic=/camera/rgb/image_raw&quality=65";
      String stream="<img src="+imageUrl+" style=\"display: inline; height: 100%; width:100%\" />" ;
        xWalkWebView=(XWalkView)findViewById(R.id.xwalkWebView);
        xWalkWebView.load(null,stream); // streaming loaded
        xWalkWebView.setBackgroundColor(Color.TRANSPARENT);

        setTitle("");
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        showdia(getWindow().getDecorView().getRootView()) ;
    }



    public void seek_bar()
    {

        seekv=(SeekBar)findViewById(R.id.mySeekBar) ; //vertical seekv to control velocity
        seek=(SeekBar)findViewById(R.id.seekBar1) ; // seek to control direction
        seek.setMax(50);
        seek.setProgress(25) ;
        seekv.setProgress(50) ;
        socket.emit("move1",50,50); // at the beginnig the direction is forward (first arg) and the velocity is 0 (second arg)

        seek.setOnSeekBarChangeListener(

                new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        int x=2*progress; // the direction
                        int y=seekv.getProgress(); // the velocity
                        //this block is made to avoid sending the same command multiple times
                        if (x>45 && x<55 && fromUser )
                        {seek.setProgress(25);
                            if(!done)
                            {done=true ;
                                socket.emit("move1",x,y); // send the command
                            }
                        }
                        else if (fromUser){
                            socket.emit("move1", x, y); //send the command
                            done=false ;
                        }
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                        socket.emit("move1",2*seek.getProgress(),seekv.getProgress());
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        seek.setProgress(25) ;

                        socket.emit("move1",50,50);

                    }
                }
        ) ;

//velocity control
        seekv.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        if (seekv.getProgress()<60 && seekv.getProgress()>40)
                        {
                            seekv.setProgress(50);
                            socket.emit("move1",50,50);
                        }
                        //else {socket.emit("move1",seek.getProgress(),seekv.getProgress());}
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {

                    }
                }
        ) ;


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater() ;
        inflater.inflate(R.menu.main_menu,menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.settings:
                showdia(getWindow().getDecorView().getRootView()) ;
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
public void showdia(View view)
{
    settings obj= new settings() ;
    obj.show(getFragmentManager(),"My settings");
}

    public void connection (String server_ad )
    {
        socket = new SocketIO();
        if (socket.isConnected())
        {
            socket.disconnect();
        }
        try {
            socket.connect(server_ad, new Socket());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        /*String imageUrl ="http://192.168.0.201:8181/stream?topic=/camera/rgb/image_raw&quality=65";
        String stream="<img src="+imageUrl+" style=\"display: inline; height: 100%; width:100%\" />" ;
        xWalkWebView=(XWalkView)findViewById(R.id.xwalkWebView);
        xWalkWebView.load(null,stream);*/
    }


}





