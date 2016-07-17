/*
* Copyright (C) 2015 Vincent Mi
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package com.thinksns.sociax.t4.android.img;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.ColorFilter;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

import com.thinksns.sociax.t4.android.image.SmartImage;
import com.thinksns.sociax.t4.android.image.SmartImageTask;
import com.thinksns.sociax.t4.android.image.WebImage;
import com.thinksns.sociax.android.R;

@SuppressWarnings("UnusedDeclaration")
public class RoundCornerImageView extends ImageView {

  // Constants for tile mode attributes
	
  private static final int TILE_MODE_UNDEFINED = -2;
  private static final int TILE_MODE_CLAMP = 0;
  private static final int TILE_MODE_REPEAT = 1;
  private static final int TILE_MODE_MIRROR = 2;

  public static final String TAG = "RoundedImageView";
  public static final float DEFAULT_RADIUS = 0f;
  public static final float DEFAULT_BORDER_WIDTH = 0f;
  public static final Shader.TileMode DEFAULT_TILE_MODE = Shader.TileMode.CLAMP;
  private static final ScaleType[] SCALE_TYPES = {
      ScaleType.MATRIX,
      ScaleType.FIT_XY,
      ScaleType.FIT_START,
      ScaleType.FIT_CENTER,
      ScaleType.FIT_END,
      ScaleType.CENTER,
      ScaleType.CENTER_CROP,
      ScaleType.CENTER_INSIDE
  };

  private float cornerRadius = DEFAULT_RADIUS;
  private float borderWidth = DEFAULT_BORDER_WIDTH;
  private ColorStateList borderColor =
      ColorStateList.valueOf(RoundedDrawable.DEFAULT_BORDER_COLOR);
  private boolean isOval = false;
  private boolean mutateBackground = false;
  private Shader.TileMode tileModeX = DEFAULT_TILE_MODE;
  private Shader.TileMode tileModeY = DEFAULT_TILE_MODE;

  private ColorFilter mColorFilter = null;
  private boolean mHasColorFilter = false;
  private boolean mColorMod = false;

  private int mResource;
  private Drawable mDrawable;
  private Drawable mBackgroundDrawable;

	private ScaleType mScaleType;

	private static final int LOADING_THREADS = 4;
	private String imgUrl;
	private static ExecutorService threadPool = Executors
			.newFixedThreadPool(LOADING_THREADS);

	private SmartImageTask currentTask;
	
  public RoundCornerImageView(Context context) {
    super(context);
  }

  public RoundCornerImageView(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public RoundCornerImageView(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);

//    setScaleType(ScaleType.CENTER_CROP);
    cornerRadius = context.getResources().getDimensionPixelSize(R.dimen.riv_corner_radius);
    borderWidth = context.getResources().getDimensionPixelSize(R.dimen.riv_border_width);
//    TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RoundCornerImageView, defStyle, 0);
//
//    int index = a.getInt(R.styleable.RoundCornerImageView_android_scaleType, -1);
//    if (index >= 0) {
//      setScaleType(SCALE_TYPES[index]);
//    } else {
//      // default scaletype to FIT_CENTER
//      setScaleType(ScaleType.FIT_CENTER);
//    }
//
//    cornerRadius = a.getDimensionPixelSize(R.styleable.RoundCornerImageView_riv_corner_radius, -1);
//    borderWidth = a.getDimensionPixelSize(R.styleable.RoundCornerImageView_riv_border_width, -1);

    // don't allow negative values for radius and border
    if (cornerRadius < 0) {
      cornerRadius = DEFAULT_RADIUS;
    }
    if (borderWidth < 0) {
      borderWidth = DEFAULT_BORDER_WIDTH;
    }

    borderColor = null;//a.getColorStateList(R.styleable.RoundCornerImageView_riv_border_color);
    if (borderColor == null) {
      borderColor = ColorStateList.valueOf(RoundedDrawable.DEFAULT_BORDER_COLOR);
    }

    mutateBackground = true;//a.getBoolean(R.styleable.RoundCornerImageView_riv_mutate_background, false);
    isOval = false;//a.getBoolean(R.styleable.RoundCornerImageView_riv_oval, false);

    final int tileMode = TILE_MODE_UNDEFINED;//a.getInt(R.styleable.RoundCornerImageView_riv_tile_mode, TILE_MODE_UNDEFINED);
    if (tileMode != TILE_MODE_UNDEFINED) {
      setTileModeX(parseTileMode(tileMode));
      setTileModeY(parseTileMode(tileMode));
    }

//    final int tileModeX = a.getInt(R.styleable.RoundCornerImageView_riv_tile_mode_x, TILE_MODE_UNDEFINED);
//    if (tileModeX != TILE_MODE_UNDEFINED) {
//      setTileModeX(parseTileMode(tileModeX));
//    }

//    final int tileModeY = a.getInt(R.styleable.RoundCornerImageView_riv_tile_mode_y, TILE_MODE_UNDEFINED);
//    if (tileModeY != TILE_MODE_UNDEFINED) {
//      setTileModeY(parseTileMode(tileModeY));
//    }

    updateDrawableAttrs();
    updateBackgroundDrawableAttrs(true);

//    a.recycle();
  }

  private static Shader.TileMode parseTileMode(int tileMode) {
    switch (tileMode) {
      case TILE_MODE_CLAMP:
        return Shader.TileMode.CLAMP;
      case TILE_MODE_REPEAT:
        return Shader.TileMode.REPEAT;
      case TILE_MODE_MIRROR:
        return Shader.TileMode.MIRROR;
      default:
        return null;
    }
  }

  @Override
  protected void drawableStateChanged() {
    super.drawableStateChanged();
    invalidate();
  }

  /**
   * Return the current scale type in use by this ImageView.
   *
   * @attr ref android.R.styleable#ImageView_scaleType
   * @see android.widget.ImageView.ScaleType
   */
  @Override
  public ScaleType getScaleType() {
    return mScaleType;
  }

  /**
   * Controls how the image should be resized or moved to match the size
   * of this ImageView.
   *
   * @param scaleType The desired scaling mode.
   * @attr ref android.R.styleable#ImageView_scaleType
   */
  @Override
  public void setScaleType(ScaleType scaleType) {
    assert scaleType != null;

    if (mScaleType != scaleType) {
      mScaleType = scaleType;

      switch (scaleType) {
        case CENTER:
        case CENTER_CROP:
        case CENTER_INSIDE:
        case FIT_CENTER:
        case FIT_START:
        case FIT_END:
        case FIT_XY:
          super.setScaleType(ScaleType.FIT_XY);
          break;
        default:
          super.setScaleType(scaleType);
          break;
      }

      updateDrawableAttrs();
      updateBackgroundDrawableAttrs(false);
      invalidate();
    }
  }

  @Override
  public void setImageDrawable(Drawable drawable) {
    mResource = 0;
    mDrawable = RoundedDrawable.fromDrawable(drawable);
    updateDrawableAttrs();
    super.setImageDrawable(mDrawable);
  }

  @Override
  public void setImageBitmap(Bitmap bm) {
    mResource = 0;
    mDrawable = RoundedDrawable.fromBitmap(bm);
    updateDrawableAttrs();
    super.setImageDrawable(mDrawable);
  }

  @Override
  public void setImageResource(int resId) {
    if (mResource != resId) {
      mResource = resId;
      mDrawable = resolveResource();
      updateDrawableAttrs();
      super.setImageDrawable(mDrawable);
    }
  }

  @Override public void setImageURI(Uri uri) {
    super.setImageURI(uri);
    setImageDrawable(getDrawable());
  }

  private Drawable resolveResource() {
    Resources rsrc = getResources();
    if (rsrc == null) { return null; }

    Drawable d = null;

    if (mResource != 0) {
      try {
        d = rsrc.getDrawable(mResource);
      } catch (Exception e) {
        Log.w(TAG, "Unable to find resource: " + mResource, e);
        // Don't try again.
        mResource = 0;
      }
    }
    return RoundedDrawable.fromDrawable(d);
  }

  @SuppressLint("Override") public void setBackground(Drawable background) {
    setBackgroundDrawable(background);
  }

  private void updateDrawableAttrs() {
    updateAttrs(mDrawable);
  }

  private void updateBackgroundDrawableAttrs(boolean convert) {
    if (mutateBackground) {
      if (convert) {
        mBackgroundDrawable = RoundedDrawable.fromDrawable(mBackgroundDrawable);
      }
      updateAttrs(mBackgroundDrawable);
    }
  }

  @Override public void setColorFilter(ColorFilter cf) {
    if (mColorFilter != cf) {
      mColorFilter = cf;
      mHasColorFilter = true;
      mColorMod = true;
      applyColorMod();
      invalidate();
    }
  }

  private void applyColorMod() {
    // Only mutate and apply when modifications have occurred. This should
    // not reset the mColorMod flag, since these filters need to be
    // re-applied if the Drawable is changed.
    if (mDrawable != null && mColorMod) {
      mDrawable = mDrawable.mutate();
      if (mHasColorFilter) {
        mDrawable.setColorFilter(mColorFilter);
      }
      // TODO: support, eventually...
      //mDrawable.setXfermode(mXfermode);
      //mDrawable.setAlpha(mAlpha * mViewAlphaScale >> 8);
    }
  }

  private void updateAttrs(Drawable drawable) {
    if (drawable == null) { return; }

    if (drawable instanceof RoundedDrawable) {
      ((RoundedDrawable) drawable)
          .setScaleType(mScaleType)
          .setCornerRadius(cornerRadius)
          .setBorderWidth(borderWidth)
          .setBorderColor(borderColor)
          .setOval(isOval)
          .setTileModeX(tileModeX)
          .setTileModeY(tileModeY);
      applyColorMod();
    } else if (drawable instanceof LayerDrawable) {
      // loop through layers to and set drawable attrs
      LayerDrawable ld = ((LayerDrawable) drawable);
      for (int i = 0, layers = ld.getNumberOfLayers(); i < layers; i++) {
        updateAttrs(ld.getDrawable(i));
      }
    }
  }

  @Override
  @Deprecated
  public void setBackgroundDrawable(Drawable background) {
    mBackgroundDrawable = background;
    updateBackgroundDrawableAttrs(true);
    super.setBackgroundDrawable(mBackgroundDrawable);
  }

  public float getCornerRadius() {
    return cornerRadius;
  }

  public void setCornerRadiusDimen(int resId) {
    setCornerRadius(getResources().getDimension(resId));
  }

  public void setCornerRadius(float radius) {
    if (cornerRadius == radius) { return; }

    cornerRadius = radius;
    updateDrawableAttrs();
    updateBackgroundDrawableAttrs(false);
    invalidate();
  }

  public float getBorderWidth() {
    return borderWidth;
  }

  public void setBorderWidth(int resId) {
    setBorderWidth(getResources().getDimension(resId));
  }

  public void setBorderWidth(float width) {
    if (borderWidth == width) { return; }

    borderWidth = width;
    updateDrawableAttrs();
    updateBackgroundDrawableAttrs(false);
    invalidate();
  }

  public int getBorderColor() {
    return borderColor.getDefaultColor();
  }

  public void setBorderColor(int color) {
    setBorderColor(ColorStateList.valueOf(color));
  }

  public ColorStateList getBorderColors() {
    return borderColor;
  }

  public void setBorderColor(ColorStateList colors) {
    if (borderColor.equals(colors)) { return; }

    borderColor =
        (colors != null) ? colors : ColorStateList.valueOf(RoundedDrawable.DEFAULT_BORDER_COLOR);
    updateDrawableAttrs();
    updateBackgroundDrawableAttrs(false);
    if (borderWidth > 0) {
      invalidate();
    }
  }

  public boolean isOval() {
    return isOval;
  }

  public void setOval(boolean oval) {
    isOval = oval;
    updateDrawableAttrs();
    updateBackgroundDrawableAttrs(false);
    invalidate();
  }

  public Shader.TileMode getTileModeX() {
    return tileModeX;
  }

  public void setTileModeX(Shader.TileMode tileModeX) {
    if (this.tileModeX == tileModeX) { return; }

    this.tileModeX = tileModeX;
    updateDrawableAttrs();
    updateBackgroundDrawableAttrs(false);
    invalidate();
  }

  public Shader.TileMode getTileModeY() {
    return tileModeY;
  }

  public void setTileModeY(Shader.TileMode tileModeY) {
    if (this.tileModeY == tileModeY) { return; }

    this.tileModeY = tileModeY;
    updateDrawableAttrs();
    updateBackgroundDrawableAttrs(false);
    invalidate();
  }

  public boolean mutatesBackground() {
    return mutateBackground;
  }

  public void mutateBackground(boolean mutate) {
    if (mutateBackground == mutate) { return; }

    mutateBackground = mutate;
    updateBackgroundDrawableAttrs(true);
    invalidate();
  }
  
  public void setImageUrl(String url) {
	  	setImage(new WebImage(url));
		this.imgUrl = url;
	}

	public void setImageUrl(String url,
			SmartImageTask.OnCompleteListener completeListener) {
		setImage(new WebImage(url), completeListener);
	}

	public void setImageUrl(String url, final Integer fallbackResource) {
		setImage(new WebImage(url), fallbackResource);
	}

	public void setImageUrl(String url, final Integer fallbackResource,
			SmartImageTask.OnCompleteListener completeListener) {
		setImage(new WebImage(url), fallbackResource, completeListener);
	}

	public void setImageUrl(String url, final Integer fallbackResource,
			final Integer loadingResource) {
		setImage(new WebImage(url), fallbackResource, loadingResource);
	}

	public void setImageUrl(String url, final Integer fallbackResource,
			final Integer loadingResource,
			SmartImageTask.OnCompleteListener completeListener) {
		setImage(new WebImage(url), fallbackResource, loadingResource,
				completeListener);
	}
	
	// Set image using SmartImage object
		public void setImage(final SmartImage image) {
			setImage(image, null, R.drawable.default_image_small, null);
		}

		public void setImage(final SmartImage image,
				final SmartImageTask.OnCompleteListener completeListener) {
			setImage(image, null, null, completeListener);
		}

		public void setImage(final SmartImage image, final Integer fallbackResource) {
			setImage(image, fallbackResource, fallbackResource, null);
		}

		public void setImage(final SmartImage image,
				final Integer fallbackResource,
				SmartImageTask.OnCompleteListener completeListener) {
			setImage(image, fallbackResource, fallbackResource, completeListener);
		}

		public void setImage(final SmartImage image,
				final Integer fallbackResource, final Integer loadingResource) {
			setImage(image, fallbackResource, loadingResource, null);
		}

		public void setImage(final SmartImage image,
				final Integer fallbackResource, final Integer loadingResource,
				final SmartImageTask.OnCompleteListener completeListener) {
			// Set a loading resource
			if (loadingResource != null) {
				setImageResource(loadingResource);
			}

			// Cancel any existing tasks for this image view
			if (currentTask != null) {
				currentTask.cancel();
				currentTask = null;
			}

			// Set up the new task
			currentTask = new SmartImageTask(getContext(), image);
			currentTask
					.setOnCompleteHandler(new SmartImageTask.OnCompleteHandler() {
						@Override
						public void onComplete(Bitmap bitmap) {
							if (bitmap != null) {
								setImageBitmap(bitmap);
							} else {
								// Set fallback resource
								if (fallbackResource != null) {
									setImageResource(fallbackResource);
								}
							}

							if (completeListener != null) {
								completeListener.onComplete(bitmap);
							}
						}
					});

			// Run the task in a threadpool
			threadPool.execute(currentTask);
		}

		public static void cancelAllTasks() {
			threadPool.shutdownNow();
			threadPool = Executors.newFixedThreadPool(LOADING_THREADS);
		}
		
}
