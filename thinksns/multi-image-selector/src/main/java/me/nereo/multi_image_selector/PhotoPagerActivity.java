package me.nereo.multi_image_selector;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by donglua on 15/6/24.
 */
public class PhotoPagerActivity extends FragmentActivity {

  private ImagePagerFragment pagerFragment;
  private LinearLayout ll_original;
  private RadioButton mIsSelectBtn, mOriginalBtn;    //选择图标和原图图标
  private ImageView mBack;
  private Button okBtn;
  private Map<Integer, String> delPath = new HashMap<Integer, String>();
  private ArrayList<String> paths;

  public final static String EXTRA_CURRENT_ITEM = "current_item";
  public final static String EXTRA_PHOTOS = "photos";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    requestWindowFeature(Window.FEATURE_NO_TITLE);
    setContentView(R.layout.activity_photo_pager);

    mBack = (ImageView)findViewById(R.id.btn_back);
    mIsSelectBtn = (RadioButton)findViewById(R.id.select_btn);
    ll_original = (LinearLayout)findViewById(R.id.ll_original);
    mOriginalBtn = (RadioButton)findViewById(R.id.original_btn);
    okBtn = (Button)findViewById(R.id.ok_btn);

    int currentItem = getIntent().getIntExtra(EXTRA_CURRENT_ITEM, 0);
    paths = getIntent().getStringArrayListExtra(EXTRA_PHOTOS);

    pagerFragment = (ImagePagerFragment) getSupportFragmentManager().findFragmentById(R.id.photoPagerFragment);
    pagerFragment.setPhotos(new ArrayList<String>(paths), currentItem);

    pagerFragment.getViewPager().setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
      @Override
      public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//        updateActionBarTitle();
      }

      @Override
      public void onPageSelected(int i) {
        if(delPath.containsKey(i)) {
          mIsSelectBtn.setChecked(false);
        }else {
          mIsSelectBtn.setChecked(true);
        }
      }

      @Override
      public void onPageScrollStateChanged(int i) {

      }
    });

    mBack.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        onBackPressed();
      }
    });

    mIsSelectBtn.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        //更新选中列表
        int pos = pagerFragment.getViewPager().getCurrentItem();
        String path = pagerFragment.getPaths().get(pos);
        if(delPath.containsKey(pos)) {
          //已经被删除了，再次加入选中列表
          delPath.remove(pos);
          paths.add(pos, path);
          mIsSelectBtn.setChecked(true);
        }else {
          //加入删除列表
          mIsSelectBtn.setChecked(false);
          delPath.put(pos, path);
          if(paths.contains(path)) {
            paths.remove(pos);
          }
        }

        okBtn.setText("确定(" + paths.size() + ")");
      }
    });

    ll_original.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        mOriginalBtn.setChecked(!mOriginalBtn.isChecked());
      }
    });

    okBtn.setText("确定(" + paths.size() + ")");
    okBtn.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        finish();
      }
    });
  }


  @Override
  public void finish() {
    Intent intent = new Intent();
    intent.putExtra(MultiImageSelectorActivity.EXTRA_DEFAULT_SELECTED_LIST, paths);
    intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_ORIGIANL, mOriginalBtn.isChecked());
    setResult(RESULT_OK, intent);
    super.finish();
  }
}
