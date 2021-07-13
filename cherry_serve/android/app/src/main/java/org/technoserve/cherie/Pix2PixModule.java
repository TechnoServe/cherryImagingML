package org.technoserve.cherie;
import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.WritableNativeMap;
import java.util.Map;
import java.util.HashMap;

public class Pix2PixModule extends ReactContextBaseJavaModule {
    Pix2PixModule(ReactApplicationContext context) {
       super(context);
   }

   @ReactMethod
   public void execute(String name, String uri, Promise promise) {
        try {

            WritableNativeMap outputMap = new WritableNativeMap();
            
            outputMap.putString("uri", "IMG_URI");
            outputMap.putString("ripe", "90");
            outputMap.putString("overripe", "2");
            outputMap.putString("underripe", "8");

            promise.resolve(outputMap);
        } catch(Exception e) {
            promise.reject("Segmentation Error", e);
        }
   }

    @Override
    public String getName() {
        return "Pix2PixModule";
    }
}