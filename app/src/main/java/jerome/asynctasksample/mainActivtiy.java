package jerome.asynctasksample;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jerome on 2016/1/2.
 */
public class mainActivtiy extends Activity
{
    public ListView mListView;

    private static String mURL = "http://www.imooc.com/api/teacher?type=4&num=30";
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listview);
        mListView = (ListView)findViewById(R.id.lv_main);
        new listViewAsyncTask().execute(mURL);
    }


    private List<Bean> getJsonData(String url)
    {
        List<Bean> beanList = new ArrayList<>();
        try {
            String jsonString = readStream(new URL(url).openStream());
            JSONObject jsonObject;
            Bean bean;
            jsonObject = new JSONObject(jsonString);
            JSONArray jsonArray = jsonObject.getJSONArray("data");
            for (int index = 0 ; index < jsonArray.length() ; index++)
            {
                jsonObject = jsonArray.getJSONObject(index);
                bean = new Bean();
                bean.mIconURL = jsonObject.getString("picSmall");
                bean.mTitle = jsonObject.getString("name");
                bean.mContent = jsonObject.getString("description");
                beanList.add(bean);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return beanList;
    }
    private String readStream(InputStream inputStream)
    {
        InputStreamReader inputSteamReader;
        String result = "";
        try {
            String line = "";
            inputSteamReader = new InputStreamReader(inputStream, "utf-8");
            BufferedReader bufferedReader = new BufferedReader(inputSteamReader);
            while ( (line = bufferedReader.readLine()) != null)
            {
                result += line;
            }
        }
        catch(UnsupportedEncodingException ex)
        {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return result;

    }


    class listViewAsyncTask extends AsyncTask<String, Void, List<Bean>>
    {


        @Override
        protected List<Bean> doInBackground(String... params) {
            return getJsonData(params[0]);
        }
        @Override
        protected  void onPostExecute(List<Bean> beanList)
        {
            super.onPostExecute(beanList);
            BeanBaseAdapter adapter = new BeanBaseAdapter(mainActivtiy.this, beanList, mListView);
            mListView.setAdapter(adapter);
        }
    }
}
