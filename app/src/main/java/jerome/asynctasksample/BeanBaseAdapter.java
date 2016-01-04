package jerome.asynctasksample;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Jerome on 2016/1/3.
 */
public class BeanBaseAdapter extends BaseAdapter implements AbsListView.OnScrollListener
{
    private List<Bean> mList;
    private LayoutInflater mLayoutInflater;
    private ImageLoaderAsyncTask mImageLoaderAsyncTask;
    private int mScrollStart, mScrollEnd;
    public static String[] mURLS;
    private boolean mFirstIn;
    public BeanBaseAdapter(Context context, List<Bean> data, ListView listView)
    {
        mList = data;
        mLayoutInflater = LayoutInflater.from(context);
        mImageLoaderAsyncTask = new ImageLoaderAsyncTask(listView);
        mURLS = new String[mList.size()];
        for (int index = 0; index < mList.size() ;index++)
            mURLS[index] = mList.get(index).mIconURL;
        listView.setOnScrollListener(this);
        mFirstIn = true;
    }
    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null)
        {
            convertView = mLayoutInflater.inflate(R.layout.listivew_item, null);
            viewHolder = new ViewHolder();
            viewHolder.mImageViewIcon = (ImageView)convertView.findViewById(R.id.imageView_icon);
            viewHolder.mTextViewContent = (TextView)convertView.findViewById(R.id.tv_content);
            viewHolder.mTextViewTitle = (TextView)convertView.findViewById(R.id.tv_title);

            convertView.setTag(viewHolder);
        }
        else
            viewHolder = (ViewHolder)convertView.getTag();

        viewHolder.mImageViewIcon.setImageResource(R.mipmap.ic_launcher);
        viewHolder.mImageViewIcon.setTag(mList.get(position).mIconURL);
//        new ImageLoaderThread().showImageByThread(viewHolder.mImageViewIcon, mList.get(position).mIconURL);
        //new ImageLoaderAsyncTask().showImageByAsyncTask(viewHolder.mImageViewIcon, mList.get(position).mIconURL);
        mImageLoaderAsyncTask.showImageByAsyncTask(viewHolder.mImageViewIcon, mList.get(position).mIconURL);

        viewHolder.mTextViewTitle.setText(mList.get(position).mTitle);
        viewHolder.mTextViewContent.setText(mList.get(position).mContent);
        return convertView;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (scrollState == SCROLL_STATE_IDLE) {
            mImageLoaderAsyncTask.loadImages(mScrollStart,mScrollEnd);
        }
        else
        {
            mImageLoaderAsyncTask.cancelAllTasks();
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        mScrollStart = firstVisibleItem;
        mScrollEnd = firstVisibleItem + visibleItemCount;
        if (visibleItemCount > 0)
        {
            mImageLoaderAsyncTask.loadImages(mScrollStart, mScrollEnd);
//            mFirstIn = false;
        }
    }

    class ViewHolder
    {
        public TextView mTextViewTitle, mTextViewContent;
        public ImageView mImageViewIcon;
    }
}
