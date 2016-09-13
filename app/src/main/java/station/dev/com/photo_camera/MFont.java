package station.dev.com.photo_camera;

import android.content.Context;
import android.graphics.Typeface;

/**
 * Created by Develop on 5/18/2016.
 */
public class MFont {
    public static Context context;
    public static Typeface getBoldfont(){

        Typeface face= Typeface.createFromAsset(context.getAssets(), "fonts/Raleway-Bold.ttf");
        return face;

    }
    public static Typeface getExtraBoldfont(){

        Typeface face= Typeface.createFromAsset(context.getAssets(), "fonts/Raleway-ExtraBold.ttf");

        return face;
    }
    public static Typeface getMediumfont(){

        Typeface face= Typeface.createFromAsset(context.getAssets(), "fonts/Raleway-Medium.ttf");

        return face;
    }

    public static Typeface getRegularfont(){

        Typeface face= Typeface.createFromAsset(context.getAssets(), "fonts/Raleway-Regular.ttf");

        return face;

    }

}
