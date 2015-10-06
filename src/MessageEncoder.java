import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by speter-toshiba on 10/6/15.
 */
public class MessageEncoder
{
    /**
     * Take a Map of message arguments and encode that into a String that can be passed between nodes.
     * @param messageMap Message contents
     * @return Message string
     */
    public static String encodeMessage(Map<String,String> messageMap)
    {
        JSONObject object = new JSONObject(messageMap);

        return object.toString();
    }

    /**
     * Take a message string from another node and decode it into its arguments for processing.
     * @param message Message string
     * @return Message contents
     */
    public static Map<String,String> decodeMessage(String message)
    {
        try
        {
            JSONObject object = new JSONObject(message);

            boolean storeValuesAsInts = false;

            return JSONParser.convertJSONObjectToMap(object, storeValuesAsInts);
        }
        catch(JSONException ex)
        {
            ex.printStackTrace();

            return null;
        }
    }
}
