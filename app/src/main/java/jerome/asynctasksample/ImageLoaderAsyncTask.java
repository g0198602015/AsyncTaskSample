package jerome.asynctasksample;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.LruCache;
import android.widget.ImageView;
import android.widget.ListView;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Jerome on 2016/1/3.
 */
public class ImageLoaderAsyncTask
{
    private LruCache<String, Bitmap> mCaches;
    private ListView mListView;
    private Set<asyncTask> mTask;
    public ImageLoaderAsyncTask(ListView listview)
    {
        mListView = listview;
        mTask = new HashSet<>();
        int maxMemory = (int)Runtime.getRuntime().maxMemory();
        int cacheSize = maxMemory / 4;
        mCaches = new LruCache<String, Bitmap>(cacheSize)
        {
            @Override
            protected int sizeOf(String key, Bitmap value)
            {
                return value.getByteCount();
            }
        };
    }
    public void cancelAllTasks()
    {
        if (mTask != null)
        {
            for (asyncTask task : mTask)
                task.cancel(false);
        }
    }
    public void loadImages(int start, int end)
    {
        for (int index = start; index < end; index++)
        {
            String url = BeanBaseAdapter.mURLS[index];
            Bitmap bitmap = getBitmapFromCache(url);
            if (bitmap == null)
            {
                asyncTask task = new asyncTask(url);
                task.execute(url);
                mTask.add(task);
            }
            else
            {
                ImageView imageView = (ImageView)mListView.findViewWithTag(url);
                imageView.setImageBitmap(bitmap);
            }

        }
    }
    private void addBitmapToCache(String url, Bitmap bitmap)
    {
        if (getBitmapFromCache(url) == null)
            mCaches.put(url, bitmap);
    }
    private Bitmap getBitmapFromCache(String url)
    {
        return mCaches.get(url);
    }
    public void showImageByAsyncTask(ImageView imageView, final String urlString)
    {
        Bitmap bitmap = getBitmapFromCache(urlString);
        if (bitmap == null)
            imageView.setImageResource(R.mipmap.ic_launcher);
        else
            imageView.setImageBitmap(bitmap);
    }
    public Bitmap getBitmapFromURL(String urlString) {
        Bitmap bitmap = null;
        InputStream inputStream;
        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            inputStream = new BufferedInputStream(connection.getInputStream());
            bitmap = BitmapFactory.decodeStream(inputStream);
            connection.disconnect();
            inputStream.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }
    private class asyncTask extends AsyncTask<String, Void, Bitmap> {
        private String mURL;
        public asyncTask(String url)
        {
            mURL = url;
        }
        @Override
        protected Bitmap doInBackground(String... params) {
            Bitmap bitmap = getBitmapFromURL(params[0]);
            if (bitmap != null)
                addBitmapToCache(params[0], bitmap);
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap)
        {
            super.onPostExecute(bitmap);
            ImageView imageView = (ImageView)mListView.findViewWithTag(mURL);
            if (imageView != null && bitmap != null)
                imageView.setImageBitmap(bitmap);
        }
    }
}
