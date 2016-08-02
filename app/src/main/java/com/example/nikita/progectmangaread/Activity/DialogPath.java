package com.example.nikita.progectmangaread.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.text.format.Formatter;

import java.io.File;

/**
 * Created by Nikita on 31.07.2016.
 */
public class DialogPath extends DialogFragment {

    public interface NoticeDialogListener {
        public void onDialogClickPath(String path);
        public void onDialogChoiceOfDirectory(DialogFragment dialog);
    }

    // Use this instance of the interface to deliver action events
    NoticeDialogListener mListener;

    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (NoticeDialogListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnArticleSelectedListener");
        }
    }


    @Override
    public android.app.Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final String[] name = new String[2];
        name[1] = "";
      //  name[2] = "Выберите свою директорию.";

        //Расчитываем свободное место для внутреней памяти
        final File internalPath = Environment.getDataDirectory();
        StatFs stat = new StatFs(internalPath.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();

        //путь во внутреней памяти
        name[0] = getActivity().getFilesDir().getPath() + " Свободно: " + Formatter.formatFileSize(getActivity(), availableBlocks * blockSize);

        ///Расчитываем свободное место для внешней памяти
        File externalPath = Environment.getExternalStorageDirectory();
        stat = new StatFs(externalPath.getPath());
        blockSize = stat.getBlockSize();
        availableBlocks = stat.getAvailableBlocks();
        //путь во внешней памяти
        name[1] = getActivity().getExternalFilesDir(null).getPath() + " Свободно: " + Formatter.formatFileSize(getActivity(), availableBlocks * blockSize);

        builder.setItems(name, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // The 'which' argument contains the index position
                        // of the selected item
                      //  if (which != 2)
                        mListener.onDialogClickPath(name[which].split(" ")[0]);
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }
}