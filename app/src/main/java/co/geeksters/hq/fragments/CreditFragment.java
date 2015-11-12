package co.geeksters.hq.fragments;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import co.geeksters.hq.R;
import co.geeksters.hq.activities.GlobalMenuActivity;
import co.geeksters.hq.global.GlobalVariables;

@EFragment(R.layout.credit_fragment)
public class CreditFragment extends Fragment {

    @ViewById(R.id.text1)
    TextView text1;
    @ViewById(R.id.text2)
    TextView text2;
    @ViewById(R.id.text3)
    TextView text3;
    @ViewById(R.id.text4)
    TextView text4;
    @ViewById(R.id.text5)
    TextView text5;
    @ViewById(R.id.text6)
    TextView text6;
    @ViewById(R.id.title1)
    TextView title1;
    @ViewById(R.id.title2)
    TextView title2;
    @ViewById(R.id.title3)
    TextView title3;
    @ViewById(R.id.title4)
    TextView title4;
    @ViewById(R.id.title5)
    TextView title5;
    @ViewById(R.id.text_thanks)
    TextView textThanks;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        GlobalVariables.needReturnButton = false;
        GlobalVariables.menuPart = 7;
        GlobalVariables.menuDeep = 0;
        GlobalVariables.inRadarFragement = false;
        GlobalVariables.inMyTodosFragment = false;
        GlobalVariables.inMarketPlaceFragment = false;
        GlobalVariables.inMyProfileFragment = false;
        ((GlobalMenuActivity) getActivity()).setActionBarTitle("CREDITS");

        return null;
    }

    @AfterViews
    public void setStyle(){

        Typeface typeFace=Typeface.createFromAsset(getActivity().getAssets(), "fonts/OpenSans-Regular.ttf");
         text1.setTypeface(typeFace);
        text2.setTypeface(typeFace);
        text3.setTypeface(typeFace);
        text4.setTypeface(typeFace);
        text5.setTypeface(typeFace);
        text6.setTypeface(typeFace);
         title1.setTypeface(typeFace);
         title2.setTypeface(typeFace);
        title3.setTypeface(typeFace);
        title4.setTypeface(typeFace);
        title5.setTypeface(typeFace);
        textThanks.setTypeface(null, typeFace.BOLD);

    }
    @Override
    public void onDestroy(){

        super.onDestroy();

        getActivity().onPrepareOptionsMenu(GlobalVariables.menu);
    }



    @Override
    public void onStart(){
        super.onStart();
     }
}
