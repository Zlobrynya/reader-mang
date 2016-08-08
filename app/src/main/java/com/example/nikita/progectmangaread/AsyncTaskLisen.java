package com.example.nikita.progectmangaread;

import java.io.InputStream;

/**
 * Created by Nikita on 19.11.2015.
 */

//Интерфейс для потока
public interface AsyncTaskLisen {
   // void onBegin(); //Асинхронная операция началась
    void onEnd(); //Операция закончилась
    void onEnd(int number);
}
