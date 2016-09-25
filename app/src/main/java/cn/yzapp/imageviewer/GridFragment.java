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
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import cn.yzapp.imageviewerlib.ImageViewer;
import cn.yzapp.imageviewerlib.Utils;
import me.relex.circleindicator.CircleIndicator;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link GridFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link GridFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GridFragment extends Fragment{

    private OnFragmentInteractionListener mListener;
    private ArrayList<Object> urlList;

    public GridFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment PageFragment.
     */
    public static GridFragment newInstance() {
        return new GridFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        initData();

        View view = inflater.inflate(R.layout.fragment_page, container, false);
        view.findViewById(R.id.layout_viewpage).setVisibility(View.GONE);

        initGridLayout(view);

        return view;
    }

    private void initData() {
        urlList = new ArrayList<>();
        urlList.add("http://news.mydrivers.com/img/topimg/20160711/081730219.jpg");
        urlList.add("http://news.mydrivers.com/img/topimg/20160711/191353087.jpg");
        urlList.add("http://news.mydrivers.com/img/topimg/20160711/081730219.jpg");
        urlList.add("http://news.mydrivers.com/img/topimg/20160711/191353087.jpg");
        urlList.add(R.mipmap.ic_launcher);
        urlList.add("http://news.mydrivers.com/img/topimg/20160711/191353087.jpg");
        urlList.add("http://news.mydrivers.com/img/topimg/20160711/081730219.jpg");
        urlList.add("http://news.mydrivers.com/img/topimg/20160711/191353087.jpg");
        urlList.add("http://news.mydrivers.com/img/topimg/20160711/081730219.jpg");
    }

    private void initGridLayout(View root) {

        final ImageViewer imageViewer = new ImageViewer();

        GridLayout layout = (GridLayout) root.findViewById(R.id.grid_layout);

        int width = Utils.INSTANCE.getScreenWidth(getContext()) / 3;
        final LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(width, width);

        final List<ImageView> imgs = new ArrayList<>();

        int i = 0;
        for (Object url : urlList) {

            final View view = View.inflate(getContext(), R.layout.view_img, null);
            final ImageView img = (ImageView) view.findViewById(R.id.img);
            if (url instanceof Integer) {
                Picasso.with(getContext()).load((int) url).into(img);
            } else {
                Picasso.with(getContext()).load((String) url).into(img);
            }
            img.setLayoutParams(lp);

            imgs.add(img);

            final int finalI = i;
            img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    imageViewer.open(getActivity(), imgs, urlList, finalI);
                }
            });

            if (layout != null) {
                layout.addView(view);
            }

            i++;
        }
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

}
