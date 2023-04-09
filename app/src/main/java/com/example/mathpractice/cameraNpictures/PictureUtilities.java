package com.example.mathpractice.cameraNpictures;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import com.example.mathpractice.sqlDataBase.DataBaseHelper;
import com.example.mathpractice.sqlDataBase.UsersHelper;

/**
 * Static utility class for picture editing.
 * */
public class PictureUtilities {

	/**
	 * This method rotates the given bitmap by the given degrees.
	 * @param source the bitmap to rotate.
	 * @param angle the rotation angle.
	 * @return rotated bitmap.
	 * */
	public static Bitmap rotateBitmap(Bitmap source, float angle)
	{
		Matrix matrix = new Matrix();
		matrix.postRotate(angle);
		return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
	}

	/**
	 * This method affect an {@link ImageView} to black and white effect.
	 * @param picture the {@link ImageView} to affect.
	 * */
	public static void makeBlackNwhite(ImageView picture) {
		ColorMatrix matrix = new ColorMatrix();
		matrix.setSaturation(0);
		ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);
		picture.setColorFilter(filter);
	}

	/**
	 * This method put the correct hat on the user's face in the bitmap given.
	 * @param context the context which is now active.
	 * @param username the current logged-in user.
	 * @param baseUsersImage the base image to put hat on.
	 * @return a bitmap of the base image with the required hat.
	 * */
	public static Bitmap putHat(Context context, String username, Bitmap baseUsersImage){
		Rect hatRect = UsersHelper.getHatRect(new UsersHelper(context), username);
		if (hatRect == null) {
			System.out.println("No hat rect");
			return baseUsersImage;
		}
		int id = hatRect.bottom, hatSize = hatRect.right, hatX = hatRect.left, hatY = hatRect.top;
		Bitmap imageToDisplay = baseUsersImage;
		if (id != -1 && hatSize != 0) {
			System.out.println(id);
			@SuppressLint("UseCompatLoadingForDrawables")
			Drawable drawable = context.getResources().getDrawable(id);
			Bitmap hatBitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
			drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
			drawable.draw(new Canvas(hatBitmap));
			hatBitmap = Bitmap.createScaledBitmap(hatBitmap, hatSize, hatSize, false);
			Bitmap copy = baseUsersImage.copy(Bitmap.Config.ARGB_8888, true);
			Canvas canvas = new Canvas(copy);
			canvas.drawBitmap(hatBitmap, hatX, hatY, null);
			imageToDisplay = copy;
		}
		return imageToDisplay;
	}
}
