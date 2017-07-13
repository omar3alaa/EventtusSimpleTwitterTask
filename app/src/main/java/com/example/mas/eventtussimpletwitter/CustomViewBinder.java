package com.example.mas.eventtussimpletwitter;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;


//Hi, definitely you came from Followers class.
    // Am I right?
class CustomViewBinder implements SimpleAdapter.ViewBinder {

    @Override
    public boolean setViewValue(View view, Object inputData, String textRepresentation) {
        int id = view.getId();
        String data = (String) inputData;
        switch (id) {
            case R.id.bio:
                populateBio(view,data);
                break;
            case R.id.profilePic:
                populateProfileImage(view,data);
                break;
            case R.id.fullName:
                populateName(view, data);
                break;
            case R.id.handle:
                populateHandle(view, data);
                break;
        }
        return true;

    }
    private void populateBio(final View view, final String data){
        TextView Bio = (TextView) view.findViewById(R.id.bio);
        if(data!=null){
        Bio.setText(data);}
        else{
            // if there is no bio then we will make its visibility gone to save some space for another Bio Heroes
            Bio.setVisibility(View.GONE);
            }
    }
    private void populateProfileImage(final View view, final String data){
        ImageView ProfilePic =(ImageView) view.findViewById(R.id.profilePic);
        View v = view.getRootView();
        //Here a progress bar to make it a user-frindly Interface till image is loaded
        final ProgressBar progressBar = (ProgressBar) v.findViewById(R.id.Progressbar);
        Picasso.with(view.getContext()).load(data).fit().into(ProfilePic, new Callback() {
            @Override
            public void onSuccess() {
                if(progressBar!=null){
                    // we will dismiss this progressbar after loading image successfully, We will miss you, ProgressBar <3
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onError() {

            }
        });
    }
    private void populateName(final View view, final String data){
        TextView FullName = (TextView) view.findViewById(R.id.fullName);
        FullName.setText(data);
    }
    private void populateHandle(final View view, final String data){
        TextView Handle = (TextView) view.findViewById(R.id.handle);
        Handle.setText(data);
    }
}
