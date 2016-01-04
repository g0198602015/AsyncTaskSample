package jerome.asynctasksample;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.util.LruCache;
import android.widget.ImageView;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Jerome on 2016/1/3.
 */
public class ImageLoaderThread
{
    private LruCache<String, Bitmap> mCaches;
    private String mUrl = "";
    private ImageView mImageView = null;
    private Handler mHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            super.handleMessage(msg);
            if (mImageView.getTag().equals(mUrl))
                mImageView.setImageBitmap((Bitmap) msg.obj);
        }
    };
    public ImageLoaderThread()
    {
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
    private void addBitmapToCache(String url, Bitmap bitmap)
    {
        if (getBitmapFromCache(url) == null)
            mCaches.put(url, bitmap);
    }
    private Bitmap getBitmapFromCache(String url)
    {
        return mCaches.get(url);
    }
    public void showImageByThread(ImageView imageView, final String urlString)
    {
        mImageView = imageView;
        mUrl = urlString;
        new Thread()
        {
            @Override
            public void run()
            {
                super.run();
                Bitmap bitmap = getBitmapFromURL(urlString);
                Message message = Message.obtain();
                message.obj = bitmap;
                mHandler.sendMessage(message);
            }
        }.start();
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
}
