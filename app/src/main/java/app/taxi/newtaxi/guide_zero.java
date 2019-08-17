package app.taxi.newtaxi;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

public class guide_zero extends Fragment {

    public guide_zero() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_guide_zero, container, false);

        ImageView profileGIF = view.findViewById(R.id.pointGIF);
        ImageView chargeGIF = view.findViewById(R.id.chargeGIF);

        Glide.with(getActivity()).load(R.drawable.profile).into(profileGIF);
        Glide.with(getActivity()).load(R.drawable.charge).into(chargeGIF);

        return view;
    }
}
