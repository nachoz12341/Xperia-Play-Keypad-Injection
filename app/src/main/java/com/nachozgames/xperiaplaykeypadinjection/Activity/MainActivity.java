package com.nachozgames.xperiaplaykeypadinjection.Activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.nachozgames.xperiaplaykeypadinjection.Injection.KeypadZeusWriter;
import com.nachozgames.xperiaplaykeypadinjection.R;
import com.nachozgames.xperiaplaykeypadinjection.Util.Toasts;

import java.io.IOException;


public class MainActivity extends Activity
{
    private KeypadZeusWriter keypadZeusWriter;

    @Override protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toasts.init(this);
        keypadZeusWriter = new KeypadZeusWriter();

        try
        {
            keypadZeusWriter.start("/dev/input/event5");
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Log.e("XperiaPlay", "Failed to start writer: " + e.getMessage());
        }
    }

    @Override protected void onStart()
    {
        super.onStart();
    }

    @Override protected void onStop()
    {
        super.onStop();

        try
        {
            keypadZeusWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onButtonOpen(View v)
    {
        try{
            keypadZeusWriter.switchEvent(0, 0);
            keypadZeusWriter.mscEvent(0x3, 0);
            keypadZeusWriter.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void onButtonClose(View v)
    {
        try{
            keypadZeusWriter.switchEvent(0, 1);
            keypadZeusWriter.mscEvent(0x3, 1);
            keypadZeusWriter.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}


