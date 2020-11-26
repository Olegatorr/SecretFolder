package ua.coursework.secretfolder.utils;

import android.view.View;
import android.widget.ProgressBar;

public class ProgressBarHelper {

    ProgressBar progressBar;
    SnackbarHelper snackBar;
    int count = 0;

    public ProgressBarHelper(ProgressBar progressBar){
        this.progressBar = progressBar;
        snackBar = new SnackbarHelper(progressBar);
    }

    public void add(){
        count ++;
        updateProgressBar();
    }

    public void sub(){
        count --;
        updateProgressBar();
    }

    public void set(int digit){
        count = digit;
        updateProgressBar();
    }

    public void setMax(int max){
        progressBar.setMax(max);
        updateProgressBar();
    }

    private void updateProgressBar(){
        if(count < 0 || count >= progressBar.getMax()){
            count = 0;
            progressBar.setVisibility(View.INVISIBLE);
            progressBar.setMax(0);
            progressBar.setProgress(0);
            snackBar.show("Done!");
        }else{
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setProgress(count);
        }
    }

    public void abort(){

        count = 0;
        progressBar.setVisibility(View.INVISIBLE);
        progressBar.setMax(0);
        progressBar.setProgress(0);

        snackBar.show("Something went wrong");
    }

}
