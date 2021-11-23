package com.nextinger.binancer.utils

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.view.View
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.annotation.RawRes
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target




class AnimationUtils {
    companion object{

        fun fadeIn(view: View) {
            view.apply {
                alpha = 0f
                visibility = View.VISIBLE

                animate()
                    .alpha(1f)
                    .setDuration(Constants.FADE_ANIMATION_DURATION)
                    .setListener(null)
            }
        }

        fun fadeOut(view: View) {
            view.animate()
                .alpha(0f)
                .setDuration(Constants.FADE_ANIMATION_DURATION)
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        view.visibility = View.GONE
                    }
                })
        }

        fun startGifAnimationOnce(imageView: ImageView, @RawRes @DrawableRes gifResId: Int, roundEdges: Boolean = false) {

            var glideRequestManager = Glide.with(imageView.context).asGif()

            if (roundEdges) {
                glideRequestManager = glideRequestManager.apply(RequestOptions.bitmapTransform(RoundedCorners(16)))
            }

            glideRequestManager
                .load(gifResId)
                .listener(object: RequestListener<GifDrawable> {
                    override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<GifDrawable>?, isFirstResource: Boolean): Boolean {
                        return false
                    }

                    override fun onResourceReady(resource: GifDrawable?, model: Any?, target: Target<GifDrawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                        resource?.setLoopCount(1)
                        return false
                    }
                })
                .into(imageView)
        }

    }
}