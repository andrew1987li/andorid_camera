package station.dev.com.photo_camera;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Develop on 5/14/2016.
 */
public class MUtils {
    public static String TAG = "Camera";

    public static Bitmap decodeSampledBitmapFromFile(String path, int reqWidth, int reqHeight)
    { // BEST QUALITY MATCH

        //First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        BitmapFactory.decodeFile(path, options);

        // Calculate inSampleSize, Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        int inSampleSize = 1;

        try {
            if (height > reqHeight)
            {
                inSampleSize = Math.round((float)height / (float)reqHeight);
            }
            int expectedWidth = width / inSampleSize;

            if (expectedWidth > reqWidth)
            {
                //if(Math.round((float)width / (float)reqWidth) > inSampleSize) // If bigger SampSize..
                inSampleSize = Math.round((float)width / (float)reqWidth);
            }

            options.inSampleSize = inSampleSize;

            // Decode bitmap with inSampleSize set
            options.inJustDecodeBounds = false;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        Bitmap bmp =  BitmapFactory.decodeFile(path, options);
/*		    Bitmap bmr = null;

			if (bmp !=null) {
			    try {
			    	// String[] orientationColumn = {MediaStore.Images.Media.ORIENTATION};
					ExifInterface exif = new ExifInterface(path);
					int exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
					if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90)
						bmr = rotateBitmap(bmp, ExifInterface.ORIENTATION_ROTATE_90);
					else
						bmr = bmp;
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

			}
*/
        return bmp;
    }


    public static String getNewImagePath(){
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
                .format(new Date());
        String path =  Environment.getExternalStorageDirectory().getAbsolutePath()+ File.separator+ "PocImage";
        File dir = new File(path);

        if(!dir.exists()){
            dir.mkdir();
        }

        String mediaFile =  path + File.separator+ "IMG_" + timeStamp + ".jpg";

        return mediaFile;

    }
        static public byte[] MakePacket(byte[]data,byte cmd_type, int len){

            int data_size = len + 5;
            byte[] sendData = new byte[data_size];

            byte[] sizearr = intToByteArray(data_size);

            System.arraycopy(sizearr, 0, sendData, 0, 4);


            sendData[4] = cmd_type;
            System.arraycopy(data,0, sendData, 5, len);

            return  sendData;
        }

        public static byte[] intToByteArray(int a)
        {
            byte[] ret = new byte[4];
            ret[3] = (byte) (a & 0xFF);
            ret[2] = (byte) ((a >> 8) & 0xFF);
            ret[1] = (byte) ((a >> 16) & 0xFF);
            ret[0] = (byte) ((a >> 24) & 0xFF);
            return ret;
        }

        public static int byteArrayToInt(byte[] b)
        {
            return   b[3] & 0xFF |
                    (b[2] & 0xFF) << 8 |
                    (b[1] & 0xFF) << 16 |
                    (b[0] & 0xFF) << 24;
        }



}
