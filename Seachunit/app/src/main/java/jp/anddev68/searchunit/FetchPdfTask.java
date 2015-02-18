package jp.anddev68.searchunit;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by hideki on 2014/12/18.
 */
public class FetchPdfTask extends AsyncTask<String,Integer,String> {

    @Override
    protected String doInBackground(String... params) {
        String in = params[0];
        String out = params[1];
        InputStream is = null;
        FileOutputStream fos = null;
        URL url;

        try{
            url = new URL(in);
            is = url.openStream();
            fos = new FileOutputStream(new File(out));
            copy(is,fos);
            fos.close();
            is.close();

        }catch(Exception e){
            e.printStackTrace();
        }



        return null;
    }

    public interface TaskEndListener{
        public void onEndTask();
    }

    ProgressDialog _dialog;
    Context _context;




    private static int copy(InputStream input, OutputStream output) throws IOException {
        byte[] buffer = new byte[1024 * 4];
        int count = 0;
        int n = 0;
        while (-1 != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
            count += n;
        }
        return count;
    }

}
