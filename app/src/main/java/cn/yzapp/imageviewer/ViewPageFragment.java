package cn.yzapp.imageviewer;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import cn.yzapp.imageviewerlib.ImageViewer;
import me.relex.circleindicator.CircleIndicator;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ViewPageFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ViewPageFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ViewPageFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    private ViewPager mViewPager;
    private CircleIndicator mIndicator;
    private ArrayList<Object> urlList;
    private List<ImageView> mImageList = new ArrayList<>();

    public ViewPageFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment PageFragment.
     */
    public static ViewPageFragment newInstance() {
        return new ViewPageFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        initData();

        View view = inflater.inflate(R.layout.fragment_page, container, false);

        initViewPager(view);

        return view;
    }

    private void initData() {
        urlList = new ArrayList<>();
        urlList.add("http://news.mydrivers.com/img/topimg/20160711/081730219.jpg");
        urlList.add("http://news.mydrivers.com/img/topimg/20160711/191353087.jpg");
        urlList.add(R.mipmap.ic_launcher);
    }

    private void initViewPager(View root) {
        mViewPager = (ViewPager) root.findViewById(R.id.view_pager);
        mIndicator = (CircleIndicator) root.findViewById(R.id.indicator);
        initAdapter();

    }

    private void initAdapter() {
        final ImageViewer imageViewer = new ImageViewer();

        if (mImageList != null && mImageList.size() > 0) {
            mImageList.clear();
        }

        for (Object ignored : urlList) {
            ImageView img = new ImageView(getContext());
            img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    imageViewer.openWithChoose(getContext(), mImageList, urlList, mViewPager.getCurrentItem());
                }
            });
            mImageList.add(img);
        }

        MyPagerAdapter adapter = new MyPagerAdapter();
        mViewPager.setAdapter(adapter);
        mViewPager.setCurrentItem(0);

        imageViewer.setViewPager(mViewPager);

        mIndicator.setViewPager(mViewPager);
    }


    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    class MyPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return urlList.size();
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            if (urlList.get(position) instanceof Integer) {
                Picasso.with(getContext()).load((int) urlList.get(position)).into(mImageList.get(position));
            } else {
                Picasso.with(getContext()).load((String) urlList.get(position)).into(mImageList.get(position));
            }

            container.addView(mImageList.get(position));
            return mImageList.get(position);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }

}
