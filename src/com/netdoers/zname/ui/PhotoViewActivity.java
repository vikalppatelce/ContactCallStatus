package com.netdoers.zname.ui;

import android.net.Uri;
import android.os.Bundle;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.netdoers.zname.R;
import com.netdoers.zname.Zname;
import com.netdoers.zname.utils.SquareImageView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class PhotoViewActivity extends SherlockFragmentActivity{

	//UI COMPONENTS
	private SquareImageView mPhotoView;
	String intentPhoto;
	
	//CONSTANTS
	public static final String mIntentPhoto = "intent_photo";
	
	//IMAGELOADER
	ImageLoader imageLoader;
	DisplayImageOptions options;
	
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		requestWindowFeature(com.actionbarsherlock.view.Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_photoview);
		
		initUi();
		getIntentData();
		
		setUniversalImageLoader();
		
		setImageOnPhotoView();
			
	}
	
	private void initUi(){
		mPhotoView = (SquareImageView)findViewById(R.id.activity_photoview_image);
	}
	
	private void getIntentData(){
		intentPhoto = getIntent().getStringExtra(mIntentPhoto);
	}
	
	private void setUniversalImageLoader(){
		imageLoader = ImageLoader.getInstance();
        // Initialize ImageLoader with configuration. Do it once.
        imageLoader.init(Zname.getImageLoaderConfiguration());
        
        options = new DisplayImageOptions.Builder()
        .showImageOnLoading(R.drawable.def_contact) // resource or drawable
        .showImageForEmptyUri(R.drawable.def_contact) // resource or drawable
        .showImageOnFail(R.drawable.def_contact) //this is the image that will be displayed if download fails
        .cacheInMemory()
        .cacheOnDisc()
        .build();
	}
	
	private void setImageOnPhotoView(){
		if(intentPhoto.contains("http")){
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					imageLoader.displayImage(Zname.getPreferences().getProfilePicPath(), mPhotoView, options);
				}
			});
		}else{
			mPhotoView.setImageURI(Uri.parse(intentPhoto));
		}
		
		if(mPhotoView.getDrawable()==null)
			mPhotoView.setImageResource(R.drawable.def_contact);
	}
}

