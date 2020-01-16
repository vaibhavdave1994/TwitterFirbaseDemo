package com.e.twitterfirbasedemo

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.widget.ImageView

internal class DownloadImageTask(bmImage: ImageView): AsyncTask<String, Void, Bitmap>() {
    internal var bmImage:ImageView
    init{
        this.bmImage = bmImage
    }
    protected override fun doInBackground(vararg urls:String):Bitmap {
        val urldisplay = urls[0]
        var bitmap:Bitmap? = null
        try
        {
            val `in` = java.net.URL(urldisplay).openStream()
            bitmap = BitmapFactory.decodeStream(`in`)
        }
        catch (e:Exception) {
            e.printStackTrace()
        }
        return bitmap!!
    }
    protected override fun onPostExecute(result:Bitmap) {
        bmImage.setImageBitmap(result)
    }
}