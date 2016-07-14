package cn.yzapp.imageviewerlib;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: nestor
 * email: nestor@yzapp.cn
 */
public class ShowImage implements Parcelable {
    private List<int[]> sizes;

    private int index;

    private List<String> imgUrl;

    public List<int[]> getSizes() {
        return sizes;
    }

    /**
     * int[]依次为图片的宽，高，x轴坐标，y轴坐标
     * 不传就不显示缩放效果
     */
    public void setSizes(List<int[]> sizes) {
        this.sizes = sizes;
    }

    public int getIndex() {
        return index;
    }

    /**
     * 当前点击的图片索引
     */
    public void setIndex(int index) {
        this.index = index;
    }

    public List<String> getImgUrl() {
        return imgUrl;
    }

    /**
     * 图片的url
     */
    public void setImgUrl(List<String> imgUrl) {
        this.imgUrl = imgUrl;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(this.sizes);
        dest.writeInt(this.index);
        dest.writeStringList(this.imgUrl);
    }

    public ShowImage() {
    }

    protected ShowImage(Parcel in) {
        this.sizes = new ArrayList<int[]>();
        in.readList(this.sizes, int[].class.getClassLoader());
        this.index = in.readInt();
        this.imgUrl = in.createStringArrayList();
    }

    public static final Creator<ShowImage> CREATOR = new Creator<ShowImage>() {
        @Override
        public ShowImage createFromParcel(Parcel source) {
            return new ShowImage(source);
        }

        @Override
        public ShowImage[] newArray(int size) {
            return new ShowImage[size];
        }
    };
}
