package app.taxi.newtaxi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

public class Guide extends PagerAdapter {
    Context mContext;
    int count = 0;

    public Guide(){}
    public Guide(Context context) {
        mContext = context;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        ((ViewPager) container).removeView((View) object);
    }

    @Override
    public int getCount() {
        return 9;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        switch (position) {
            case 0:
                count = R.layout.fragment_guide_first;  //이용가이드 맨 첫 화면
                break;
            case 1:
                count = R.layout.fragment_guide_zero;   //결제방식(포인트)
                break;
            case 2:
                count = R.layout.fragment_guide_two;    //노선 검색
                break;
            case 3:
                count = R.layout.fragment_guide_three;  //노선 참가/생성
                break;
            case 4:
                count = R.layout.fragment_guide_four;    //노선 생성
                break;
            case 5:
                count = R.layout.fragment_guide_five;    //노선 대기
                break;
            case 6:
                count = R.layout.fragment_guide_six;    //채팅/택시호출
                break;
            case 7:
                count = R.layout.fragment_guide_seven;   //택시 정보 확
                break;
            case 8:
                count = R.layout.fragment_guide_eight;    //택시비 결제
                break;

        }
        ViewGroup layout = (ViewGroup) inflater.inflate(count, container, false);
        container.addView(layout);
        return layout;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }
}
