package ua.coursework.secretfolder.utils;

import android.content.Context;
import android.view.View;
import android.widget.ProgressBar;

public class ProgressBarHelper {

    ProgressBar progressBar;
    UIHelper snackBar;
    Context context;

    int count = 0;

    public ProgressBarHelper(ProgressBar progressBar, Context context){
        this.progressBar = progressBar;
        this.context = context;
        snackBar = new UIHelper(progressBar, context);
    }

    public void add(){
        count ++;
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
            snackBar.showSnackbar("Done!");
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

        snackBar.showSnackbar("Something went wrong");
    }

}
