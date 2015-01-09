package co.geeksters.hq.global.helpers;

import android.content.Context;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.widget.FrameLayout;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;

import co.geeksters.hq.fragments.PeopleFinderRadarFragment_;

/**
 * Created by soukaina on 09/01/15.
 */
public  class TouchableWrapper extends FrameLayout {

    Boolean mMapIsTouched;

    public TouchableWrapper(Context context) {
        super(context);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mMapIsTouched = true;
                break;

            case MotionEvent.ACTION_UP:
                mMapIsTouched = false;
                break;
        }

        return super.dispatchTouchEvent(ev);
    }

    private final GoogleMap.OnCameraChangeListener mOnCameraChangeListener = new GoogleMap.OnCameraChangeListener() {
        @Override
        public void onCameraChange(CameraPosition cameraPosition) {
            if (!mMapIsTouched) {
                //refreshClustering(false);
            }
        }
    };
}